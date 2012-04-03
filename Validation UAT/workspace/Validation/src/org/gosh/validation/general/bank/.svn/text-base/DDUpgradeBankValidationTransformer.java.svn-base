package org.gosh.validation.general.bank;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.validation.common.FileType;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.accountnumber.ws.Report;
import org.gosh.validation.general.accountnumber.ws.SvcGetAccountNumber;
import org.gosh.validation.general.accountnumber.ws.SvcGetAccountNumberSoap;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;

/**
   This class has been refactored based on requirements for CR144.
   This class must contain transformation and logic for DD Upgrades only.
 */
public class DDUpgradeBankValidationTransformer {
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	private Reporter reporter;
	private BankDataAccess dao;
	private SvcGetAccountNumber accNumberWS;
	private BankUtilities utilities = new BankUtilities();
	private String errorNoBankId = "No banks found with the supplied sort code";
	private String errorNoBankConstID = "Either the donation that these replace does not have bank details associated with them or they do not exist.";
	private String errorNoBankConstIDMatch = "The bank details in the supplied in xml file do not match with any banks on RE associated with.";
	private String info = "Bank details or constituentBankID removed for the donors with following ConstituentID: ";
	/**
	 * This transformer performs the following:
	 * <li> Checks if the file is of type DD upgrade, if not returns.
	 * <li> Checks is the bank details are present. If not present, IllegalAccessException
	 * it queries RE for a ConstituentBankID based on InternalConstituentID and GiftImportID. 
	 * The GiftImportID is provided in the XML, where as the InternalConstituentID is added 
	 * into the payload in ConstituentIdValidationTransformer. Logs an error in case the ConstituentBankID
	 * is not found.
	 * <li> If the bank details are not present, it takes the following course of actions:
	 * <br>1. Queries the BankID based on SortCode, logs an error if multiple IDs are received (multiple 
	 * ids mean that the database has dirty data, in practical sense it means that 2 or more banks have
	 * the same sort code, for eg. citibank and HSBC having the same sort code)
	 * <br>2. Queries the ConstituentBankID based on ConstituentID and SortCode provided in the xml.
	 * <br>3. If no ConstituentBankID are found, it keeps the bank details. 
	 * <br>4. If exactly one ConstituentBankID is found, it removes the bank details and adds the ConstituentBankID. 
	 * <br>5. If more than one ConstituentBankID are found, it uses a web service to fetch the account numbers that each ConstituentBankID
	 * is associated with and tries to match it with the account number provided in the XML.
	 * <br>6. If there is a match with the account number in the XML remove the bank details and add the ConstituentBankID.
	 * <br>7. If there is no match, log an error.
	 * 
	 * @param localMessage - message containing the XML data as payload.
	 * @return modified message after performing the transformation as described.
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> localMessage) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		// Check if it is an upgrade file
		FileType type = (FileType)localMessage.getHeaders().get(MessageHeaderName.FILE_TYPE.getName());
		if (type == null || type != FileType.DD_UPGRADE_TYPE) {
			// Not an DD upgrade file, return without processing
			return localMessage;
		}

		// get the payload
		GOSHCC payload = localMessage.getPayload();
		
		// get all the donorCplxType elements
		List<DonorCplxType> donorCplxTypes = payload.getDonorCplxType();

		String infoMessage = null;
		List<DonorCplxType> errorNoBankList = new ArrayList<DonorCplxType>(); 
		List<DonorCplxType> errorNoBankConstIDList = new ArrayList<DonorCplxType>();
		List<DonorCplxType> errorNoBankConstIDMatchList = new ArrayList<DonorCplxType>();
		
		// iterate through each 
		for (DonorCplxType donor : donorCplxTypes) {
			if (!utilities.isBankNull(donor)) { // enter only if bank is not null
				// Check if there is enough bank details
				if (!utilities.isAccOrSortBlank(donor)) {
					// Check if the donor is already associated with the given bank details
					DonorBankCplxType bankCplx = donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType();
					List<BigInteger> bankConstIds = dao.getBankConstitIdBySortCode(bankCplx.getBankSort(), donor.getConstituentID());
					if (bankConstIds != null && bankConstIds.size() > 0) { // User is associated with the bank
						if (bankConstIds.size() == 1) {
							// One bankConstId found, add the constituent ID and remove the bank details
							donor.getDonationDetails().getDirectDebitDonationCplxType().setConstituentBankID(bankConstIds.get(0));
							donor.getDonationDetails().getDirectDebitDonationCplxType().setDonorBankCplxType(null);
							if (infoMessage == null)
								infoMessage = info;
							infoMessage += donor.getConstituentID() + ";";
						} else {
							boolean constBankIdMatched = false;
							// Multiple bankConstIds found. Fetch A/C for each of the bankConstID and match with that in xml
							for (BigInteger bankConstId : bankConstIds) {
								// Call the WS and match the bank details
								SvcGetAccountNumberSoap soap = accNumberWS.getSvcGetAccountNumberSoap();
								Report wsReport = soap.getAccountNumber(bankConstId.intValue());
								if (wsReport.getAccountNo()!=null && wsReport.isSuccess() && wsReport.getResponse() == null) {  // response==null success
									if (wsReport.getAccountNo().equals(bankCplx.getBankAccNo())) { // If match found
										// Remove bank details add constiuentBankID and break the loop
										donor.getDonationDetails().getDirectDebitDonationCplxType().setConstituentBankID(bankConstId);
										donor.getDonationDetails().getDirectDebitDonationCplxType().setDonorBankCplxType(null);
										constBankIdMatched = true;
										if (infoMessage == null)
											infoMessage = info;
										infoMessage += donor.getConstituentID() + ";";
										break;
									}
								}
							}
							if (!constBankIdMatched) { 
								errorNoBankConstIDMatchList.add(donor);
							}
						}
					} else { // User is not associated with this bank. 
						// Delete if a constBankID is present
						if (donor.getDonationDetails().getDirectDebitDonationCplxType().getConstituentBankID() != null){ 
						
							donor.getDonationDetails().getDirectDebitDonationCplxType().setConstituentBankID(null);
							if (infoMessage == null)
								infoMessage = info;
							infoMessage += donor.getConstituentID() + ";";
						}
					}	
				}
					
			} else {
				// Get the BankConstituentId by using the internalConstId along with the giftID
				if(donor != null && donor.getDonationDetails() != null && donor.getDonationDetails().getDirectDebitDonationCplxType() != null) {
					DirectDebitDonationCplxType ddDonationType = donor.getDonationDetails().getDirectDebitDonationCplxType();
					
					// proceed only if required data exists
					if (!StringUtils.isBlank(donor.getInternalConstitID()) && ddDonationType.getPDDUpgradeFlag()!= null 
							&& !StringUtils.isBlank(ddDonationType.getPDDUpgradeFlag().getGiftimportID())) {
					
						BigInteger bankConstId = dao.getBankConstitIdByGift(donor.getInternalConstitID(), ddDonationType.getPDDUpgradeFlag().getGiftimportID());
						if (bankConstId != null && bankConstId != BigInteger.ZERO ) {
							// bankConstId found, set it in the payload
							ddDonationType.setConstituentBankID(bankConstId);
						} else {
							// bankConstId not found, add to error map
							errorNoBankConstIDList.add(donor);
						}
					}
				}
			}
		}
		
		
		if (infoMessage != null) {
			List<String> infoList = new ArrayList<String>();
			infoList.add(infoMessage);
			localMessage = reporter.logInfo(localMessage, infoList);
		}
		

		localMessage = reporter.log(localMessage, errorNoBankList, errorNoBankId);
		localMessage = reporter.log(localMessage, errorNoBankConstIDList, errorNoBankConstID);
		localMessage = reporter.log(localMessage, errorNoBankConstIDMatchList, errorNoBankConstIDMatch);
		
		return localMessage;
	}
	
	@Required @Autowired
	public void setAccNumberWS(SvcGetAccountNumber accNumberWS) {
		this.accNumberWS = accNumberWS;
	}
	
	@Required @Autowired
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
	
	@Required @Autowired
    public void setDataSource(DataSource dataSource) {
		dao = new BankDataAccess(dataSource);
    }
	
	/**
	 * Utility method to set the DAO
	 * @param dao
	 */
	public void setDao(BankDataAccess dao) {
		this.dao = dao;
	}
}
