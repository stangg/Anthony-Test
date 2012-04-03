package org.gosh.validation.directdebits;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.PDDUpgradeFlag;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowCountCallbackHandler;

/**
 * Validates that the file is just an upgrade file, just a new 
 * direct debit file, etc. because we ask suppliers to give these 
 * to us separately. 
 * 
 * For upgrades, we ask suppliers to send the details for the 
 * DD that is being replaced. Here we check those ids are in RE.
 * 
 * Finally we check that if the record is not an upgrade that there
 * are no previous active DD's against that constituent. Supporter 
 * services then look at these and in some cases contact the donors
 * to see if this is what they intended to do. I'm not sure why but 
 * this was very important at the time. 
 * 
 * @author Kevin.Savage
 */
public class ExistingDDsValidationTransformer {
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	private JdbcTemplate jdbcTemplate;
	private Reporter reporter;
	
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) {
		log.info("Start Transformation");
		List<String> previousImportIds = new ArrayList<String>();
		Boolean upgradeFlagBoolean = null;
		boolean thereAreDDs = false;
		
		GOSHCC payload = message.getPayload();
		List<DonorCplxType> donorCplxTypes = payload.getDonorCplxType();
		log.info("ExistingDDsValidationTransformer --- " + donorCplxTypes.size() + " donors ");
		for (DonorCplxType donorCplxType : donorCplxTypes) {
			if (donorCplxType.getDonationDetails() != null && 
				donorCplxType.getDonationDetails().getDirectDebitDonationCplxType() != null ){
				// defines there are DD's in file
				thereAreDDs = true;
				if (donorCplxType.getDonationDetails().getDirectDebitDonationCplxType().getPDDUpgradeFlag() != null){
					// checks there are upgrades
					PDDUpgradeFlag upgradeFlag = donorCplxType.getDonationDetails().getDirectDebitDonationCplxType().getPDDUpgradeFlag();
					previousImportIds.add(upgradeFlag.getGiftimportID());
					
					if (upgradeFlagBoolean == null){
						upgradeFlagBoolean = upgradeFlag.isPDDUpgradeFlag();
					} else {
						if (upgradeFlagBoolean.booleanValue() != upgradeFlag.isPDDUpgradeFlag()){
							return reporter.log(message, "The upgrade flag is not consistent in the file.");
						}
					}
				}
			}
		}
		// TODO: Debug this. This look like a black hole.
		if (upgradeFlagBoolean != null && upgradeFlagBoolean && !allImportIdsExistInRE(previousImportIds)){
			return reporter.log(message, "There are previous import IDs that don't exist in RE.");
		}
		
		if (thereAreDDs && (upgradeFlagBoolean == null || !upgradeFlagBoolean)){
			List<String> unexpectedDDs = thereAreUnexpectedDDs(payload);
			if (unexpectedDDs!= null && !unexpectedDDs.isEmpty()){
				return reporter.log(message, "For records that are not upgrades there are previously existing DD's in RE. The import Id's for these were: " + unexpectedDDs);
			}
			// CR RE-IT-126
//			List<String> ddsWoAccName = thereAreDDsWoAccountName(payload);
//			if (ddsWoAccName!= null && !ddsWoAccName.isEmpty()){
//				return reporter.log(message, "For records that are not upgrades there are previously exsting DD's in RE that don't have an Account Name. The import Id's for these were: " + unexpectedDDs);
//			}
		}
		log.info("End of Transformation");
		return message;
	}
	
	private List<String> thereAreUnexpectedDDs(GOSHCC payload) {
		List<DonorCplxType> donorCplxTypes = payload.getDonorCplxType();
		if (donorCplxTypes.isEmpty()){
			return null;
		}
		
		String sql = "select gift.CONSTIT_ID from records, gift where gift.CONSTIT_ID = records.ID and gift.TYPE = 8 and gift.gift_status = 1 and CONSTITUENT_ID in (";
		boolean thereWasAtLeastOne = false;
		for (DonorCplxType donorCplxType : donorCplxTypes) {
			if (!StringUtils.isBlank(donorCplxType.getConstituentID())){
				thereWasAtLeastOne = true;
				sql+="'"+donorCplxType.getConstituentID()+"',";
			}
		}
		if (!thereWasAtLeastOne){
			return null;
		}
		
		sql = StringUtils.removeEnd(sql, ",");
		sql += ")";
		
		final Set<String> importIdsFromFile = new HashSet<String>();
		for (DonorCplxType donor : donorCplxTypes) {
			if (donor.getDonationDetails()!= null && donor.getDonationDetails().getDirectDebitDonationCplxType() != null && donor.getDonationDetails().getDirectDebitDonationCplxType().getPDDUpgradeFlag() != null){
				importIdsFromFile.add(donor.getDonationDetails().getDirectDebitDonationCplxType().getPDDUpgradeFlag().getGiftimportID());
			}
		}
		
		ValidationRowCallbackHandler rowCallbackHandler = new ValidationRowCallbackHandler(importIdsFromFile);
		jdbcTemplate.query(sql, rowCallbackHandler);
		return rowCallbackHandler.getInvalidImportIds();
	}
	
	// TODO: implement a sql here to look for upgrades without an account Name on RE.
	@SuppressWarnings("unused")
	private List<String> thereAreDDsWoAccountName(GOSHCC payload) {
		List<DonorCplxType> donorCplxTypes = payload.getDonorCplxType();
		if (donorCplxTypes.isEmpty()){
			return null;
		}
		
		String sql = "select gift.CONSTIT_ID from records, gift where gift.CONSTIT_ID = records.ID and gift.TYPE = 8 and gift.gift_status = 1 and CONSTITUENT_ID in (";
		boolean thereWasAtLeastOne = false;
		for (DonorCplxType donorCplxType : donorCplxTypes) {
			if (!StringUtils.isBlank(donorCplxType.getConstituentID())){
				thereWasAtLeastOne = true;
				sql+="'"+donorCplxType.getConstituentID()+"',";
			}
		}
		if (!thereWasAtLeastOne){
			return null;
		}
		
		sql = StringUtils.removeEnd(sql, ",");
		sql += ")";
		
		final Set<String> importIdsFromFile = new HashSet<String>();
		for (DonorCplxType donor : donorCplxTypes) {
			if (donor.getDonationDetails()!= null && donor.getDonationDetails().getDirectDebitDonationCplxType() != null && donor.getDonationDetails().getDirectDebitDonationCplxType().getPDDUpgradeFlag() != null){
				importIdsFromFile.add(donor.getDonationDetails().getDirectDebitDonationCplxType().getPDDUpgradeFlag().getGiftimportID());
			}
		}
		
		ValidationRowCallbackHandler rowCallbackHandler = new ValidationRowCallbackHandler(importIdsFromFile);
		jdbcTemplate.query(sql, rowCallbackHandler);
		return rowCallbackHandler.getInvalidImportIds();
	}

	private boolean allImportIdsExistInRE(List<String> previousImportIds) {
		if (previousImportIds.isEmpty()){
			return true;
		}
		
		String sql = "select * from GIFT where gift.IMPORT_ID in (";
		
		for (String string : previousImportIds) {
			sql += "'"+string+"',";
		}
		sql = StringUtils.removeEnd(sql, ",");
		sql += ")";
		
		RowCountCallbackHandler countCallback = new RowCountCallbackHandler();
		jdbcTemplate.query(sql, countCallback);
		return countCallback.getRowCount() == previousImportIds.size();
	}

	
	
	@Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
	
	private class ValidationRowCallbackHandler implements RowCallbackHandler{
		private List<String> invalidImportIds = new ArrayList<String>();
		private final Set<String> importIdsFromFile;

		public ValidationRowCallbackHandler(Set<String> importIdsFromFile) {
			this.importIdsFromFile = importIdsFromFile;
		}
		
		@Override
		public void processRow(ResultSet rs) throws SQLException {
			String idFromDB = rs.getString(1);
			if (!importIdsFromFile.contains(idFromDB)){
				invalidImportIds.add(idFromDB);
			}
		}
		
		public List<String> getInvalidImportIds() {
			return invalidImportIds;
		}
	}
}
