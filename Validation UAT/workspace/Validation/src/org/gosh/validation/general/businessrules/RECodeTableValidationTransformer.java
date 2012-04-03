package org.gosh.validation.general.businessrules;

import static ch.lambdaj.Lambda.aggregate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GiftAttributes;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.jdbc.core.JdbcTemplate;

import ch.lambdaj.function.aggregate.Aggregator;

/**
 * This generically checks code table entries based on configuration. 
 * {@link #fieldNameToCodeMapping} contains a {@link http://commons.apache.org/jxpath/ jxPath}
 * expression to denote the field in the file and a "code tables id"
 * which is used by the RE table "TABLEENTRIES". 
 * 
 * The configuration is currently in config.xml
 * 
 * @author Kevin.Savage
 */
public class RECodeTableValidationTransformer {
	private HashMap<String, String> fieldNameToCodeMapping;
	private JdbcTemplate jdbcTemplate;
	private Reporter reporter;

	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) {
		ArrayList<String> errors = new ArrayList<String>();
		
		for (Entry<String, String> entry : fieldNameToCodeMapping.entrySet()) {
			Map<Pointer, Set<String>> values = getMapOfDonorsToFieldValueFor(message, entry);
			if (values.isEmpty()){
				continue;
			}
			GOSHCC payload = message.getPayload();
			List<DonorCplxType> donorCplxTypes = payload.getDonorCplxType();
			List<String> databaseValues = getCorrespondingDatabaseValues(entry, values, donorCplxTypes);
			Set<String> erroringValues = replaceValuesWithDatabaseValuesAndReturnErrors(entry, values, databaseValues, donorCplxTypes);
			if (!erroringValues.isEmpty()){
				errors.add("There are elements in " + entry.getKey() + " that are not in the tableentries table. The offending values were: " + erroringValues);
			}
		}
		
		return reporter.log(message, errors);
	}

	@SuppressWarnings("unchecked")
	private Set<String> replaceValuesWithDatabaseValuesAndReturnErrors(Entry<String, String> entry, Map<Pointer, Set<String>> fileValues, List<String> databaseValues, List<DonorCplxType> donorCplxTypes) {
		for (String databaseValue : databaseValues) {
			for (Entry<Pointer, Set<String>> fileValue: fileValues.entrySet()) {
				Set<String> values = fileValue.getValue();
				List<String> toRemove = new ArrayList<String>();
				for (String value : values) {
					if (StringUtils.equalsIgnoreCase(value, databaseValue)){
						fileValue.getKey().setValue(databaseValue);
						toRemove.add(value);
					} else if (allowedGiftAttribute(donorCplxTypes, value)){
						toRemove.add(value);
					}
				}
				values.removeAll(toRemove);				
			}
		}
		// Xmas 2009: This is to avoid logging false errors when gift attribute description is validated and category is "Order Number"
		if (databaseValues.isEmpty() || databaseValues == null){
			for (Entry<Pointer, Set<String>> fileValue: fileValues.entrySet()) {
				Set<String> values = fileValue.getValue();
				List<String> toRemove = new ArrayList<String>();
				for (String value : values) {
					if (allowedGiftAttribute(donorCplxTypes, value)){
						toRemove.add(value);
					}
				}
				values.removeAll(toRemove);
			}
		} 
		return aggregate(
				fileValues.values(),
				new Aggregator<Set<String>>(){
					@Override
					public Set<String> aggregate(Set<String> arg0, Set<String> arg1) {
						return new HashSet<String>(CollectionUtils.union(arg0, arg1));
					}
					@Override
					public Set<String> emptyItem() {
						return new HashSet<String>();
					}
				}
		);

	}

	private boolean allowedGiftAttribute(List<DonorCplxType> donorCplxTypes, String value) {
		boolean isAllowedGiftAtt = false;
		for (DonorCplxType donor : donorCplxTypes) {
			if (donor.getDonationDetails() != null && !donor.getDonationDetails().getCashDonationCplxType().isEmpty()){
				for (CashDonationCplxType cash : donor.getDonationDetails().getCashDonationCplxType()) {
					if (cash.getGiftAttributes() != null){
						for (GiftAttributes gift : cash.getGiftAttributes()) {
							// Xmas 2009
							if (gift.getDescription().equals(value) && gift.getCategory().equals("Order Number")){
								isAllowedGiftAtt = true;
								break;
							}
							// Web Cash Donations
							if (gift.getDescription().equals(value) && gift.getCategory().equals("Internet Donation Notes")){
								isAllowedGiftAtt = true;
								break;
							}
						}
					}
				}
			}
		}
		return isAllowedGiftAtt;
	}

	@SuppressWarnings("unchecked")
	private List<String> getCorrespondingDatabaseValues(Entry<String, String> fieldToCodeTable, Map<Pointer, Set<String>> values, List<DonorCplxType> donorCplxTypes) {
		if (fieldToCodeTable.getKey().endsWith(".attributeType")){
			String sql = "select DESCRIPTION from AttributeTypes WHERE TYPEOFDATA in (" + fieldToCodeTable.getValue() + ") and RECORDTYPE IN (1,2) and lower(DESCRIPTION) in (" + lookupValues(values) + ")";
			return jdbcTemplate.queryForList(sql, String.class);
		} else {
			String description = fieldToCodeTable.getKey().endsWith(".short") ? "SHORTDESCRIPTION" : "longdescription";
			String sql = "select "+description+" from TABLEENTRIES where active = -1 and CODETABLESID in (" + fieldToCodeTable.getValue() + ") and lower("+description+") in (" + lookupValues(values) + ")";
			return jdbcTemplate.queryForList(sql, String.class);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<Pointer, Set<String>> getMapOfDonorsToFieldValueFor(Message<GOSHCC> message, Entry<String, String> entry) {
		Map<Pointer, Set<String>> values = new HashMap<Pointer, Set<String>>();
		for (DonorCplxType donorCplxType: message.getPayload().getDonorCplxType()) {
			JXPathContext context = JXPathContext.newContext(donorCplxType);
			context.setLenient(true);
			
			Iterator<Pointer> pointers = context.iteratePointers(extractXpath(entry));
			while (pointers.hasNext()) {
				Pointer pointer = pointers.next();
				String value = (String) pointer.getValue();
				if (StringUtils.isNotBlank(value)){
					add(values, pointer, value);
				}
			}
		}
		return values;
	}
	
	private String lookupValues(Map<Pointer, Set<String>> values) {
		String sql = "";
		for (Set<String> strings : values.values()) {
			for (String string : strings) {
				sql += "'" + StringUtils.lowerCase(string) + "',";
			}
		}
		return StringUtils.removeEnd(sql, ",");
	}

	private <T,U> void add(Map<T, Set<U>> map, T key, U value) {
		Set<U> set = map.get(key);
		if (set == null){
			set = new HashSet<U>();
			map.put(key, set);
		}
		set.add(value);
	}

	private String extractXpath(Entry<String, String> entry) {
		return StringUtils.substringBefore(entry.getKey(),".");
	}

	@Required
	public void setFieldNameToCodeMapping(HashMap<String, String> fieldNameToCodeMapping) {
		this.fieldNameToCodeMapping = fieldNameToCodeMapping;
	}
	
	@Autowired @Required
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
	
	public Reporter getReporter() {
		return reporter;
	}
	
	@Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
}
