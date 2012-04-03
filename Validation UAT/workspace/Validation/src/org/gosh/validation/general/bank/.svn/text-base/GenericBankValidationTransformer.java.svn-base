/**
 * 
 */
package org.gosh.validation.general.bank;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.validation.common.FileType;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;

/**
 * @author gayathri.polavaram
 *
 */
public class GenericBankValidationTransformer {

	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	private Reporter reporter;
	private BankUtilities utils = new BankUtilities();
	private final String accNumOrSortEmptyMsg = "Either Account Number OR Sort Code is empty and could cause and issue in BACS";
	private final String accNameEmptyMsg = "The Account Name is empty and could cause an issue in BACS";
	private final String shortAccNoMsg = "The number of digits in the Bank Account Number are fewer than expected";
	private final String missingDigitAccNoMsg = "The Bank Account Number is short by at least one digit";
	private static int MIN_ACC_NUMBER = 8;
	private BankDataAccess dao;
	private final String errorNoUniquBank = "Multiple banks found for the following bank details: ";
	private final String multiBanksForSort = "Multiple banks found for the same sort code";
	private final String noBankID = "Bank not found for the record, it cannot be imported, kindly contact database support team to create a bank before trying importing the record again.";
	private String onlySortCode = "The banks with the following sort codes were found based only on sortcodes, since there was no exact match with Bank Names and Branch Names: ";
	

	/**
	 * This transformer deals with generic transformation for bank validations that is common to both
	 * DD and DD upgrades. 
	 * <br> Marks if the account number is less than 8 digits. If it has 7 digits logs that the account number is missing a digit. 
	 * If it has less than 7, it logs there are lesser digits than expected.
	 * <br> Logs if the Bank Account Name is missing/blank
	 * <br> Logs if either Account Number OR Sort Code is missing/blank. 
	 * @param message
	 * @return
	 * @throws DatatypeConfigurationException
	 */
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) throws DatatypeConfigurationException {
		log.info("Message Transformation Started");
		
		// Check if its a DD or DDUpgrade file
		FileType type = (FileType)message.getHeaders().get(MessageHeaderName.FILE_TYPE.getName());
		if (type == null || !(type == FileType.DD_UPGRADE_TYPE || type == FileType.DD_TYPE)) {
			// Not an DD or DD upgrade file, return without processing
			return message;
		}
		
		// get the payload
		GOSHCC payload = message.getPayload();
		
		// get all the donorCplxType elements
		List<DonorCplxType> donorList = payload.getDonorCplxType();
		
		// Lists to store all the donors with errors
		List<DonorCplxType> accNumOrSortEmptyList = new ArrayList<DonorCplxType>();
		List<DonorCplxType> accNameEmptyList = new ArrayList<DonorCplxType>();
		List<DonorCplxType> shortAccNoList = new ArrayList<DonorCplxType>();
		List<DonorCplxType> missingDigitAccNoList = new ArrayList<DonorCplxType>();
		List<DonorCplxType> multipleBanksList = new ArrayList<DonorCplxType>();
		List<DonorCplxType> noBankList = new ArrayList<DonorCplxType>();
		String errorNoUniquBankMsg = null;
		List<String> onlySortCodeList = new ArrayList<String>();
		
		// iterate through each donor 
		for (DonorCplxType donor : donorList) {
			if (utils.isAccNameBlank(donor)) {
				accNameEmptyList.add(donor);
			}
			if (utils.isAccOrSortBlank(donor)) {
				accNumOrSortEmptyList.add(donor);
			}
			int accountNumberLength = utils.getAccountNoLength(donor);
			if (accountNumberLength == -1) {
				// Do nothing at the moment, the above if condition logs the message that anyway. 
				// If ever specific logging for AccNum is required, we can fill in the code here. 
			} else if (accountNumberLength <= 6 ) {				// BankAccount number is definitely missing digits
				shortAccNoList.add(donor);
			} else if (accountNumberLength < MIN_ACC_NUMBER) { // Could be missing a leading zero that's missing, but sure is an error
				missingDigitAccNoList.add(donor);
			}
			
			if (!utils.isBankNull(donor) && !utils.isAccOrSortBlank(donor)) {
				// Check if the bank details exit on raisers edge.
				DonorBankCplxType bankCplx = donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType();
				List<String> bankIds = dao.getBankID(bankCplx.getBankSort(), bankCplx.getBankName(), bankCplx.getBranchName());
				
				if (bankIds != null && bankIds.size() > 0) { // means the bank exists on RE
					if (bankIds.size() > 1) {
						// This is an impossible condition if bank webservice is used
						// Log message
						if (errorNoUniquBankMsg == null)
							errorNoUniquBankMsg = errorNoUniquBank;
						// If there are more than one banks returned by the query log and continue
						errorNoUniquBankMsg += bankCplx.getBankSort() + ", " + bankCplx.getBankName() +" and " + bankCplx.getBranchName() + ";";
					} else {
						// Set the bank ID
						bankCplx.setBankID(new BigInteger(bankIds.get(0)));
					}
				}
				
				// If bankID is not set by now, try to fetch it using the sort code alone.
				if (bankCplx.getBankID() == null) {
					bankIds = dao.getBankID(bankCplx.getBankSort());
					if (bankIds != null && bankIds.size() > 0) {
						if (bankIds.size() > 1) {
							// Means that the data on Raiser's edge must be cleaned up
							multipleBanksList.add(donor);
						} else {
							// Set the bank ID
							bankCplx.setBankID(new BigInteger(bankIds.get(0)));
							onlySortCodeList.add(bankCplx.getBankSort());
						}
					} else {
						// Bank ID is not found so report an error.
						noBankList.add(donor);
					}
				}
			}
		}
		
		
		if (errorNoUniquBankMsg != null) {
			List<String> infoList = new ArrayList<String>();
			infoList.add(errorNoUniquBankMsg);
			message = reporter.logInfo(message, infoList);
		}
		
		if (!onlySortCodeList.isEmpty()) {
			String listOfSortCodes = "";
			for(String sortcode : onlySortCodeList) {
				listOfSortCodes = sortcode + ";";
			}
			List<String> infoList = new ArrayList<String>();
			infoList.add(onlySortCode + listOfSortCodes);
			message = reporter.logInfo(message, infoList);
		}
		
		// Report all errors
		message = reporter.log(message, accNameEmptyList, accNameEmptyMsg);
		message = reporter.log(message, accNumOrSortEmptyList, accNumOrSortEmptyMsg);
		message = reporter.log(message, shortAccNoList, shortAccNoMsg);
		message = reporter.log(message, missingDigitAccNoList, missingDigitAccNoMsg);
		message = reporter.log(message, multipleBanksList, multiBanksForSort);
		message = reporter.log(message, noBankList, noBankID);
			
		log.info("Message Transformation Complete");
		return message;
	}
	
	@Required @Autowired
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
	// 
	@Required @Autowired
    public void setDataSource(DataSource dataSource) {
		dao = new BankDataAccess(dataSource);
    }
	// 
	/**
	 * Utility method to set the DAO
	 * @param dao
	 */
	public void setDao(BankDataAccess dao) {
		this.dao = dao;
	}
}
