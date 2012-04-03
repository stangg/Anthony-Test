package org.gosh.validation.general;

import java.math.BigDecimal;
import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GiftAttributes;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.Attributes;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.Attributes.SolicitCodes;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;


/** 
 * This was originally a response to Bolajis testing where he 
 * came up with 4 extra requirements. It has been expanded over
 * time as he has asked for more things. It has been useful for 
 * attributing things to him. 
 * 
 * It can log several things and the method names should describe what 
 * each of these are.
 * 
 * @author Kevin.Savage
 */
public class BolajisAdditionsTransformer {
	private static final String UPGRADED_DIRECT_DEBIT = "upgraded direct debit";
	private static final String NEW_DIRECT_DEBIT = "directDebit";
	private static final String CASH = "cash";
	private static final String NON_DONOR = "prospect";
	private Reporter reporter;
	
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) throws DatatypeConfigurationException{
		GOSHCC payload = message.getPayload();
		
		Message<GOSHCC> resultMessage = MessageBuilder.fromMessage(message).build();
		resultMessage = reportOn(firstDonorThatDoesntContainCorrectComments(payload), "In one of the attributes we expected the comment \"Obtained via " + payload.getSupplierID() + "\" but didn't get it.", resultMessage);
		resultMessage = reportOn(firstDonorWhereMarketingSourceIsNotSameAsSupplierDonorId(payload), "The marketing source code should be the same as the supplier donor id but it is not.", resultMessage);
		resultMessage = reportOn(donorsWhereResponseIsNotWhatWeWouldExpectForDonorTypeExceptWhereTheFileIsATributeFile(payload), "On one or more records, the \"response\" does not contain what is expected.", resultMessage);
		resultMessage = reportOn(donorsWhereDirectMarketingTypeIsNotWhatWeWouldExpectForDonorType(payload), "On one or more records, the \"DirectMarktingType\" does not contain what is expected.", resultMessage);
		resultMessage = reportOn(donorsWhereGiftStatusIsNotActive(payload), "On one or more records, the \"Gift Status\" is not Active.", resultMessage);
		resultMessage = reportOn(donorsWhereDonationValueIsLessThanOrEqualToZero(payload), "One or more records has a zero amount.", resultMessage);
		resultMessage = reportOn(donorsWhereThereIsANonConstituentOrgRelationButTheAddressTypeIsNotBusiness(payload), "One or more records has a non constituent organisation but does not have the address type of business.", resultMessage);
		resultMessage = reportOn(donorsWherePaymentIsVoucherButTheGiftTypeIsNotPledge(payload), "One or more records has a payment method of voucher but has the gift type of pledge.", resultMessage);
		resultMessage = reportOn(donorsWhereXmasFileContainsGiftAttributesDate(payload), "Could not change Gift Attributes Date to a valid format.", resultMessage);
		resultMessage = reportOn(donorsWhereSupplierIsEmeryButGiftHasNoAttribute(payload),"The file has the supplierId of Emery but gifts with no gift attributes.", resultMessage);
		resultMessage = reportOn(donorsWhereXmasFileDoesntContainOrderNumberGiftAttributes(payload),"The file has the supplierId of Emery but has no Order Number gift attributes.", resultMessage);
		//resultMessage = reportOn(donorsWhereAccountNameIsEmpty(payload), "On one or more records, the \"Account Name\" is empty and could cause an issue on BACS.", resultMessage);
		//resultMessage = reportOn(donorsWhereFileIsDDUpgradeAndBankDetailsExistButIncomplete(payload), "The file is for DDUpgrades but on one or more records, the \"Bank Details\" are incomplete and could cause an issue on BACS.", resultMessage);
		return resultMessage;
	}

	/**
	 * RE-IT-141 CR / Call Id: 1545 / CR ID:75 
	 * @param payload
	 * @return
	 */
	private Set<DonorCplxType> donorsWhereFileIsDDUpgradeAndBankDetailsExistButIncomplete(
			GOSHCC payload) {
		Set<DonorCplxType> results = new HashSet<DonorCplxType>();		
		List<DonorCplxType> donors = payload.getDonorCplxType();
		for (DonorCplxType donor : donors) {
			String donorType = getDonorType(donor);
			if (UPGRADED_DIRECT_DEBIT.equals(donorType)){
				if (donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType() != null){
					DonorBankCplxType bankCplx = donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType();
					if (StringUtils.isBlank(bankCplx.getBankAccName()) || StringUtils.isBlank(bankCplx.getBankAccNo()) || StringUtils.isBlank(bankCplx.getBankSort())){
						results.add(donor);
					}
				}
			}
		}
		return results;
	}

	/**This bit had some issues with DDUpgrade Files.
	 * RE-IT-126
	 * @param payload
	 * @return donors
	 */
	private Set<DonorCplxType> donorsWhereAccountNameIsEmpty(GOSHCC payload) {
		Set<DonorCplxType> donors = new HashSet<DonorCplxType>();
		for (DonorCplxType donor : payload.getDonorCplxType()) {
			String donorType = getDonorType(donor);
			DonationDetails donationDetails = donor.getDonationDetails();
			if (donationDetails!= null){
				if (donationDetails.getDirectDebitDonationCplxType() != null){
					DonorBankCplxType ddDonationBank = donationDetails.getDirectDebitDonationCplxType().getDonorBankCplxType();
					if (ddDonationBank == null){
						if(NEW_DIRECT_DEBIT.equals(donorType)){
							donors.add(donor);
							continue;
						}
					} else {
						if (StringUtils.isBlank(ddDonationBank.getBankAccName())){
							donors.add(donor);
						}
					}
				}
			}
		}
		return donors;
	}

	private Message<GOSHCC> reportOn(Set<DonorCplxType> set,	String string, Message<GOSHCC> message) {
		if (set == null || set.isEmpty()){
			return message;
		}
		return reporter.log(message, set, string);
	}

	private Set<DonorCplxType> donorsWhereSupplierIsEmeryButGiftHasNoAttribute(GOSHCC payload) {
		Set<DonorCplxType> donors = new HashSet<DonorCplxType>();
		if ("Emery".equals(payload.getSupplierID())){
			for (DonorCplxType donor : payload.getDonorCplxType()) {
				DonationDetails donationDetails = donor.getDonationDetails();
				if (donationDetails != null){
					for (CashDonationCplxType donation : donationDetails.getCashDonationCplxType()) {
						if (donation != null && donation.getType() != null && donation.getGiftAttributes().isEmpty()){
							donors.add(donor);
						}
					}
				}
			}
		}
		return donors;
	}

	/** 
	 * received via email on the 15/07/09. Will hopefully go in a doc we have.
	 */
	private Set<DonorCplxType> donorsWherePaymentIsVoucherButTheGiftTypeIsNotPledge(GOSHCC payload) {
		Set<DonorCplxType> donors = new HashSet<DonorCplxType>();
		for (DonorCplxType donor : payload.getDonorCplxType()) {
			DonationDetails donationDetails = donor.getDonationDetails();
			if (donationDetails!= null){
				List<CashDonationCplxType> cashDonationCplxType = donationDetails.getCashDonationCplxType();
				for (CashDonationCplxType donation : cashDonationCplxType) {
					if ("Voucher".equals(donation.getPaymentType()) && !"Pledge".equals(donation.getType())){
						donors.add(donor);
					}
				}
			}
		}
		return donors;
	}

	/** 
	 * received via email on the 15/07/09. Will hopefully go in a doc we have.
	 */
	private Set<DonorCplxType> donorsWhereThereIsANonConstituentOrgRelationButTheAddressTypeIsNotBusiness(GOSHCC payload) {
		Set<DonorCplxType> donors = new HashSet<DonorCplxType>();
		for (DonorCplxType donor : payload.getDonorCplxType()) {
			if (!donor.getNonConstituentOrganisationRelationship().isEmpty() && !"Business".equals(donor.getAddressType())){
				donors.add(donor);
			}
		}
		return donors;
	}

	/** this one was actually from Dez, not Bolaji*/
	private Set<DonorCplxType> donorsWhereDonationValueIsLessThanOrEqualToZero(GOSHCC payload) {
		Set<DonorCplxType> donors = new HashSet<DonorCplxType>();
		for (DonorCplxType donor : payload.getDonorCplxType()) {
			DonationDetails donationDetails = donor.getDonationDetails();
			if (donationDetails != null){
				List<CashDonationCplxType> cashDonations= donationDetails.getCashDonationCplxType();
				for (CashDonationCplxType cashDonation: cashDonations) {
					BigDecimal amount = cashDonation.getAmount();
					if (amount!= null && BigDecimal.ZERO.compareTo(amount)>=0){
						donors.add(donor);
					}
				}
				DirectDebitDonationCplxType directDebitDonationCplxType = donationDetails.getDirectDebitDonationCplxType();
				if (directDebitDonationCplxType != null && directDebitDonationCplxType.getAmount()!= null && BigDecimal.ZERO.compareTo(directDebitDonationCplxType.getAmount())>=0){
					donors.add(donor);
				}
			}
		}
		return donors;
	}

	private Set<DonorCplxType> donorsWhereDirectMarketingTypeIsNotWhatWeWouldExpectForDonorType(GOSHCC payload) {
		Set<DonorCplxType> donors = new HashSet<DonorCplxType>();
		for (DonorCplxType donor : payload.getDonorCplxType()) {
			String donorType = getDonorType(donor);
			Attributes attributes = donor.getAttributes();
			if (attributes != null && attributes.getDirectMarketingType() != null){
				String description = attributes.getDirectMarketingType().getDescription();
				if (NON_DONOR.equals(donorType) && !"DM Prospect".equals(description)){
					donors.add(donor);
				} else if (CASH.equals(donorType) && !"Individual Donor".equals(description)){
					donors.add(donor);
				} else if (NEW_DIRECT_DEBIT.equals(donorType) && !"Bankers Order".equals(description)){
					donors.add(donor);
				} else if (UPGRADED_DIRECT_DEBIT.equals(donorType) && !"Bankers Order".equals(description)){
					donors.add(donor);
				}
			}
		}
		return donors;
	}

	private Set<DonorCplxType> donorsWhereResponseIsNotWhatWeWouldExpectForDonorTypeExceptWhereTheFileIsATributeFile(GOSHCC payload) {
		Set<DonorCplxType> results = new HashSet<DonorCplxType>();		
		if (isTributeFile(payload)){
			return null; // we don't use response in this case. 
		}
		
		List<DonorCplxType> donors = payload.getDonorCplxType();
		for (DonorCplxType donor : donors) {
			String donorType = getDonorType(donor);
			if (donor.getConstituentAppeal() != null){
				String response = donor.getConstituentAppeal().getResponse();
				if (NON_DONOR.equals(donorType) && !asList("Prospect","No Follow Up","Responded").contains(response)){
					results.add(donor);
				} else if (CASH.equals(donorType) && !"Responded".equals(response)){
					results.add(donor);
				} else if (NEW_DIRECT_DEBIT.equals(donorType) && !"Responded".equals(response)){
					results.add(donor);
				} else if (UPGRADED_DIRECT_DEBIT.equals(donorType) && !"Upgraded".equals(response)){
					results.add(donor);
				}
			}
		}
		return results;
	}
	
	private boolean isTributeFile(GOSHCC payload) {
		boolean thereAreTributeIdsInGifts = false;
		for (DonorCplxType donor : payload.getDonorCplxType()) {
			if (donor.getDonationDetails()!=null){
				for (CashDonationCplxType cashDonation : donor.getDonationDetails().getCashDonationCplxType()) {
					if (StringUtils.isNotBlank(cashDonation.getTributeID())){
						thereAreTributeIdsInGifts = true;
					}
				}
			}
		}

		return !payload.getTribute().isEmpty() ||	 thereAreTributeIdsInGifts;
	}

	private String getDonorType(DonorCplxType donor) {
		if (donor.getDonationDetails() == null){
			return NON_DONOR;
		} else if (!donor.getDonationDetails().getCashDonationCplxType().isEmpty()){
			return CASH;
		} else if (donor.getDonationDetails().getDirectDebitDonationCplxType() != null){
			if (donor.getDonationDetails().getDirectDebitDonationCplxType().getPDDUpgradeFlag() == null ||
					!donor.getDonationDetails().getDirectDebitDonationCplxType().getPDDUpgradeFlag().isPDDUpgradeFlag()){
				return NEW_DIRECT_DEBIT;
			} else {
				return UPGRADED_DIRECT_DEBIT;
			}
		}
		
		throw new UnsupportedOperationException("Got an unexpected donor type");
	}
	
	private Set<DonorCplxType> firstDonorWhereMarketingSourceIsNotSameAsSupplierDonorId(GOSHCC payload) {
		Set<DonorCplxType> result = new HashSet<DonorCplxType>();
		List<DonorCplxType> donors = payload.getDonorCplxType();
		for (DonorCplxType donor : donors) {
			if (donor.getConstituentAppeal() != null && !StringUtils.equals(donor.getConstituentAppeal().getMarketingSourceCode(),donor.getSupplierDonorID())){
				result.add(donor);
			}
		}
		return result;
	}

	private Set<DonorCplxType> firstDonorThatDoesntContainCorrectComments(GOSHCC payload) {
		Set<DonorCplxType> result = new HashSet<DonorCplxType>();
		
		String commentString = "Obtained via " + payload.getSupplierID();
		List<DonorCplxType> donors = payload.getDonorCplxType();
		for (DonorCplxType donor: donors) {
			Attributes attributes = donor.getAttributes();
			if (attributes != null){
				// Rule for Attributes DirectMarketingType
				if (attributes.getDirectMarketingType() != null && 
						!commentString.equalsIgnoreCase(attributes.getDirectMarketingType().getComment())){
					result.add(donor);	
				}
				// Rule for Attributes Source
				if (attributes.getSource() != null && 
						!commentString.equalsIgnoreCase(attributes.getSource().getComment())){
					result.add(donor);	
				}
				// Rule for Attributes Solicit Codes
				List<SolicitCodes> solicitCodes = attributes.getSolicitCodes();
				for (SolicitCodes solicitCode : solicitCodes) {
					if (!commentString.equalsIgnoreCase(solicitCode.getComment())){
						result.add(donor);	
					}
				}
				// Rule for Attributes Deceased Notification Date
				if (attributes.getDeceasedNotificationDate() != null && 
						!commentString.equalsIgnoreCase(attributes.getDeceasedNotificationDate().getComment())){
					result.add(donor);	
				}
			}
			
			if (payload.getSupplierID()!= null && !payload.getSupplierID().equals("Netbanx")){
				// Do not validate comment on Web Cash files
				if (donor.getDonationDetails() != null){
					for (CashDonationCplxType cashDonation : donor.getDonationDetails().getCashDonationCplxType()) {
						for (GiftAttributes giftAttribute : cashDonation.getGiftAttributes()) {
							if (giftAttribute != null && 
									!commentString.equalsIgnoreCase(giftAttribute.getComment())){
								// Rule for Gift Attributes 
								result.add(donor);	
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	private Set<DonorCplxType> donorsWhereXmasFileDoesntContainOrderNumberGiftAttributes(GOSHCC payload) {
		Set<DonorCplxType> result = new HashSet<DonorCplxType>();
		// If it is a Xmas 2009 file then also validate the Gift Attributes
		if ("Emery".equals(payload.getSupplierID())){
			List<DonorCplxType> donors = payload.getDonorCplxType();
			for (DonorCplxType donor: donors) {
				if (donor.getDonationDetails() != null){
					for (CashDonationCplxType cashDonation : donor.getDonationDetails().getCashDonationCplxType()) {
						if (cashDonation != null && cashDonation.getType() != null){
							boolean hadOrderNumber = false;
							for (GiftAttributes giftAttribute : cashDonation.getGiftAttributes()) {
								if (giftAttribute != null && "Order Number".equals(giftAttribute.getCategory())){
									hadOrderNumber = true;
								}
							}
							if (!hadOrderNumber){
								result.add(donor);
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	private Set<DonorCplxType> donorsWhereXmasFileContainsGiftAttributesDate(GOSHCC payload) throws DatatypeConfigurationException {
		Set<DonorCplxType> donors = new HashSet<DonorCplxType>();
		for (DonorCplxType donor: payload.getDonorCplxType()) {
			// If it is a Xmas 2009 file then also validate the Gift Attributes
			if ("Emery".equals(payload.getSupplierID()) && donor.getDonationDetails() != null){
				for (CashDonationCplxType cashDonation : donor.getDonationDetails().getCashDonationCplxType()) {
					for (GiftAttributes giftAttribute : cashDonation.getGiftAttributes()) {
						if (giftAttribute != null ){
							if (giftAttribute.getDate() != null){
								// Rule for Gift Attributes 
								String removeSpacesFromDate = StringUtils.strip(giftAttribute.getDate().toString());
								giftAttribute.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(removeSpacesFromDate));
							} 
						} else {
							donors.add(donor);
						}
					}
				}
			}
		}
		return donors;
	}
		
	private Set<DonorCplxType> donorsWhereGiftStatusIsNotActive(GOSHCC payload) {
		Set<DonorCplxType> donors = new HashSet<DonorCplxType>();
		for (DonorCplxType donor: payload.getDonorCplxType()) {
			if (donor.getDonationDetails()!= null){
				if (donor.getDonationDetails().getDirectDebitDonationCplxType()!= null){
					if (!"Active".equals(donor.getDonationDetails().getDirectDebitDonationCplxType().getGiftStatus())){
						donors.add(donor);
					}
				}
			}
		}
		return donors;
	}
	
	
	
	@Autowired @Required 
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
}
