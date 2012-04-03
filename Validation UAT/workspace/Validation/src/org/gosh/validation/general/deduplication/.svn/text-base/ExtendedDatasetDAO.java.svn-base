package org.gosh.validation.general.deduplication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

/**
 * This looks up data from RE to use for weighting purposes. 
 * 
 * @author Kevin.Savage
 *
 */
public class ExtendedDatasetDAO implements ExtendedDatasetLookup{
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	
	private List<String> phoneNumberTypes;
	private List<String> emailTypes;
	
	private JdbcTemplate jdbcTemplate;

	public HashMap<DonorCplxType, List<ExtendedDataSetModel>> lookup(Map<DonorCplxType, List<PossibleMatchModel>> fuzzyMatches) {
		List<ExtendedDataSetModel> mainDetails = lookupMainDetails(fuzzyMatches);
		log.info("looked up " + mainDetails.size() + " main details for weighting");
		populateWithOtherDetails(mainDetails);
		log.info("populated the above with phone numbers, etc.");
		
		HashMap<DonorCplxType, List<ExtendedDataSetModel>> results = new HashMap<DonorCplxType, List<ExtendedDataSetModel>>();
		for (ExtendedDataSetModel extendedDataSetModel : mainDetails) {
			Set<Entry<DonorCplxType, List<PossibleMatchModel>>> originalEntries = fuzzyMatches.entrySet();
			for (Entry<DonorCplxType, List<PossibleMatchModel>> entry : originalEntries) {
				List<PossibleMatchModel> possibles = entry.getValue();
				for (PossibleMatchModel possibleMatchModel : possibles) {
					if (StringUtils.equals(
							extendedDataSetModel.getConstituentId(),
							possibleMatchModel.getConstituentId()
						)){
						
						List<ExtendedDataSetModel> list = results.get(entry.getKey());
						if (list == null){
							list = new ArrayList<ExtendedDataSetModel>();
							results.put(entry.getKey(), list);
						}
						list.add(extendedDataSetModel);
					}
				}
			}
		}
		
		return results;
	}
	
	public void populateWithOtherDetails(final List<ExtendedDataSetModel> mainDetails) {
		if (mainDetails.isEmpty()){
			return;
		}
		
		final String baseSql = "select CONSTIT_ADDRESS.id, PHONES.num, TABLEENTRIES.LONGDESCRIPTION from phones, CONSTIT_ADDRESS_PHONES, CONSTIT_ADDRESS, TABLEENTRIES "
		    + "where CONSTIT_ADDRESS_PHONES.CONSTITADDRESSID = CONSTIT_ADDRESS.ID " 
		    + "and Phones.PHONESID = CONSTIT_ADDRESS_PHONES.PHONESID "
		    + "and TABLEENTRIES.TABLEENTRIESID = PHONES.PHONETYPEID and "
		    + "CONSTIT_ADDRESS.id in (";
		
		
		List<List<ExtendedDataSetModel>> split = split(mainDetails, 100);
		for (List<ExtendedDataSetModel> batch : split) {
			String sql = baseSql;

			for (ExtendedDataSetModel extendedDataSetModel : batch) {
				sql += extendedDataSetModel.getAddressId() + ",";
			}
			sql = StringUtils.removeEnd(sql, ",");
			sql +=")";

			jdbcTemplate.query(sql, new RowCallbackHandler(){
				@Override
				public void processRow(ResultSet rs) throws SQLException {
					int id = rs.getInt(1);
					for (ExtendedDataSetModel details : mainDetails) {
						if (details.getAddressId() == id){
							if (phoneNumberTypes.contains(rs.getString(3))){
								details.setPhoneNumber(rs.getString(2));
							}
							if (emailTypes.contains(rs.getString(3))){
								details.setEmail(rs.getString(2));
							}
						}
					}
				}}
			);
		}
	}

	@SuppressWarnings("unchecked")
	private List<ExtendedDataSetModel> lookupMainDetails(Map<DonorCplxType, List<PossibleMatchModel>> fuzzyMatches) {
		String sql = "SELECT RECORDS.ID, RECORDS.DECEASED, RECORDS.INACTIVE, RECORDS.CONSTITUENT_ID as constituentId, TABLEENTRIES.LONGDESCRIPTION as title, RECORDS.FIRST_NAME as firstName, records.MIDDLE_NAME as middleName, RECORDS.LAST_NAME as lastName, records.SEX,"+
			"CONSTIT_ADDRESS.ID as addressId,"+
		    "address.ADDRESS_BLOCK as addressBlock, address.POST_CODE as postCode, "+
		    "CONSTITUENT_bank.ACCOUNT_NAME as accountName,BANK.BRANCH_NAME as branchName, BANK.SORT_CODE as sortCode "+
		    "FROM tableentries, RECORDS "+
		    "Left JOIN Constit_Address ON RECORDS.ID = CONSTIT_ADDRESS.CONSTIT_ID "+
		    "Left JOIN address ON address.ID = CONSTIT_ADDRESS.ADDRESS_ID "+
		    "Left JOIN CONSTITUENT_BANK ON records.ID = CONSTITUENT_BANK.CONSTIT_ID "+
		    "Left JOIN bank ON constituent_bank.BRANCH_ID = BANK.id " + 
		    "where records.TITLE_1 = TABLEENTRIES.TABLEENTRIESID " + 
		    "and CONSTITUENT_ID in (";
		
		int count = 0;
		for (List<PossibleMatchModel> possibles : fuzzyMatches.values()) {
			for (PossibleMatchModel possible : possibles) {
				count++;
				sql += "'" + (possible.getConstituentId() + "',");
			}
		}
		
		if (count == 0){
			return new ArrayList<ExtendedDataSetModel>();
		}
		
		sql = StringUtils.removeEnd(sql, ",");
		sql +=")";
		
		return jdbcTemplate.query(sql,new BeanPropertyRowMapper(ExtendedDataSetModel.class));
	}

	private <T> List<List<T>> split(List<T> list, int size) {
		if (list.size() < size) {
		    return Collections.singletonList(list);
		}		
		
	    int middle = list.size() / 2;
		List<List<T>> splitList = new ArrayList<List<T>>();
	    splitList.add(list.subList(0, middle));
	    splitList.add(list.subList(middle, list.size()));
	    List<List<T>> subSplitList = new ArrayList<List<T>>();
	    for (List<T> split : splitList) {
	    	List<List<T>> subSplit = split(split, size);
	    	subSplitList.addAll(subSplit);
	    }
	    return subSplitList;
	}
	
	@Autowired @Required
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	public List<String> getPhoneNumberTypes() {
		return phoneNumberTypes;
	}

	@Required
	public void setPhoneNumberTypes(List<String> phoneNumberTypes) {
		this.phoneNumberTypes = phoneNumberTypes;
	}

	public List<String> getEmailTypes() {
		return emailTypes;
	}

	@Required
	public void setEmailTypes(List<String> emailTypes) {
		this.emailTypes = emailTypes;
	}
}
