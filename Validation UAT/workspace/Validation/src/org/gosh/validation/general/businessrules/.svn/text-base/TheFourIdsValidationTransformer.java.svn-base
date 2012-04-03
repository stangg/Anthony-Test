package org.gosh.validation.general.businessrules;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.ConstituentAppeal;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * This validates the fund, campaign, appeal and package ids. There is 
 * complication because RE treating some packages as only valid for 
 * some appeals, etc. These are also notably not table entries. 
 * 
 * This was updated at some stage to give feedback that involves donor 
 * information so they can tell where it is.
 * 
 * @author Kevin.Savage
 */
public class TheFourIdsValidationTransformer {
	private SimpleJdbcTemplate jdbcTemplate;
	private Reporter reporter;
	
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) {
		Map<DonorCplxType, Set<String>> funds = new HashMap<DonorCplxType, Set<String>>();
		Map<DonorCplxType, Set<String>> campaigns = new HashMap<DonorCplxType, Set<String>>();
		Map<DonorCplxType, Set<String>> appeals = new HashMap<DonorCplxType, Set<String>>();
		Map<DonorCplxType,Map<String,String>> packages = new HashMap<DonorCplxType,Map<String,String>>();
		
		List<DonorCplxType> donors = message.getPayload().getDonorCplxType();
		for (DonorCplxType donor : donors) {
			if (donor.getDonationDetails()!=null){
				List<CashDonationCplxType> cashDonations = donor.getDonationDetails().getCashDonationCplxType();
				for (CashDonationCplxType cashDonation : cashDonations) {
					if (StringUtils.isNotBlank(cashDonation.getFund())){addTo(funds, donor, cashDonation.getFund());}
					if (StringUtils.isNotBlank(cashDonation.getCampaign())){addTo(campaigns, donor, cashDonation.getCampaign());};
					if (StringUtils.isNotBlank(cashDonation.getAppeal())){
						addTo(appeals, donor, cashDonation.getAppeal());
						if (StringUtils.isNotBlank(cashDonation.getPackage())){addTo(packages, donor, cashDonation.getPackage(),cashDonation.getAppeal());}
					}
				}
				
				DirectDebitDonationCplxType directDebitDonation = donor.getDonationDetails().getDirectDebitDonationCplxType();
				if (directDebitDonation != null){
					if (StringUtils.isNotBlank(directDebitDonation.getFund())){addTo(funds, donor,directDebitDonation.getFund());}
					if (StringUtils.isNotBlank(directDebitDonation.getCampaign())){addTo(campaigns, donor, directDebitDonation.getCampaign());};
					if (StringUtils.isNotBlank(directDebitDonation.getAppeal())){
						addTo(appeals, donor, directDebitDonation.getAppeal());
						if (StringUtils.isNotBlank(directDebitDonation.getPackage())){addTo(packages, donor, directDebitDonation.getPackage(),directDebitDonation.getAppeal());}
					}
				}
			}
			
			ConstituentAppeal constituentAppeal = donor.getConstituentAppeal();
			if (constituentAppeal!= null){
				addTo(appeals, donor, constituentAppeal.getAppealID());
				addTo(packages, donor, constituentAppeal.getPackageID(), constituentAppeal.getAppealID());
			}
		}
		
		Message<GOSHCC> resultMessage = check("appeal", appeals, message);
		resultMessage = check("campaign", campaigns, resultMessage);
		resultMessage = check("fund", funds, resultMessage);
		resultMessage = check("package", "appeal", packages, resultMessage);
		
		return resultMessage;
	}
	
	private void addTo(Map<DonorCplxType, Map<String, String>> map, DonorCplxType donor, String packageCode /*package is a reserved word*/, String appeal) {
		Map<String, String> mapping = map.get(donor);
		if (mapping == null){
			mapping = new HashMap<String, String>();
			map.put(donor, mapping);
		}
		mapping.put(packageCode, appeal);
	}

	private void addTo(Map<DonorCplxType, Set<String>> map, DonorCplxType donor, String code) {
		Set<String> set = map.get(donor);
		if (set == null){
			set = new HashSet<String>();
			map.put(donor, set);
		}
		set.add(code);
	}

	private Message<GOSHCC> check(String type, String against, Map<DonorCplxType, Map<String, String>> mapping, Message<GOSHCC> resultMessage) {
    	Map<DonorCplxType, String> results = new HashMap<DonorCplxType, String>();
		for (Entry<DonorCplxType, Map<String, String>> mappingEntry : mapping.entrySet()) {
			Map<String, String> packages = mappingEntry.getValue();
			for (Entry<String, String> entry : packages.entrySet()) {
				String sql = "select count(1) from "+ type + "," + against + " " +
	   				"where " + type+"."+type + "_ID ='" + entry.getKey() + "' " +
	   				"and "+ against +"."+ against +"_id='" + entry.getValue() + "' " + 
	   				"and " + against + ".id = " + type + "." + against + "_id";
	
		    	int count = jdbcTemplate.queryForInt(sql);
				if (count!=1){
					results.put(mappingEntry.getKey(), "The following " + type + " code is invalid: " + entry.getKey());
				}
			}
		}

    	if (results.isEmpty()){
    		return resultMessage;
    	}
		return reporter.log(resultMessage, results);
	}

	@SuppressWarnings("unchecked")
	private Message<GOSHCC> check(String type, Map<DonorCplxType, Set<String>> appeals, Message<GOSHCC> message) {
    	Map<DonorCplxType, String> results = new HashMap<DonorCplxType, String>();
    	for (Entry<DonorCplxType, Set<String>> entry : appeals.entrySet()) {
	    	String sql = "select distinct(" + type + "_ID) from " + type + " where " + type + "_ID in (";
	    	for (String item : entry.getValue()) {
	    		sql += "'" + item + "',";
			}
	    	sql = StringUtils.removeEnd(sql, ",")+ ")";
	    	
	    	ParameterizedRowMapper<String> mapper = new ParameterizedRowMapper<String>(){
				@Override
				public String mapRow(ResultSet rs, int rowNum)throws SQLException {
					return rs.getString(1);
				}
	    	};
			List<String> query = jdbcTemplate.query(sql, mapper);
			Collection<String> disjunction = CollectionUtils.disjunction(entry.getValue(), query);
			if (!disjunction.isEmpty()){
				results.put(entry.getKey(), "the following " + type + " codes are invalid: " + disjunction);
			}
    	}
		
    	if (results.isEmpty()){
    		return message;
    	}
		return reporter.log(message, results);
	}

	
	public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}

	@Autowired @Required
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
}
