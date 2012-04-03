                                                                                                                                        package org.gosh.validation.general;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.Relationship;
import org.gosh.re.dmcash.bindings.GOSHCC.Tribute;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.ConsCodes;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;

/**
 * This does the following, based on original email sent by Dez to team:
 * <ul>
 * <li>Check that constituent code is consistent. 
 * <li>Direct debit details, check ids, post status, payment type. 
 * <li>Installment freq vs no. of installments.
 * <li>Schedules, const bank id, and gift status. 
 * <li>Check countries/counties are in the list in RE. 
 * </ul>
 * 
 * This also removes "test" records. These are identified as records
 * that have a String property that contains the word "test". 
 * 
 * This also, also (late addition as a result of extra schema requirement) 
 * checks the bank details are not blank if the dd is not an upgrade.
 */
public class MiscValidationTransformer {
	private Reporter reporter;
	
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		GOSHCC payload = message.getPayload();
		Map<DonorCplxType, String> errors = new HashMap<DonorCplxType, String>();

		Map<DonorCplxType, DirectDebitDonationCplxType> dds = new HashMap<DonorCplxType, DirectDebitDonationCplxType>();
		
		List<DonorCplxType> donorCplxTypes = payload.getDonorCplxType();
		List<DonorCplxType> toRemove = new ArrayList<DonorCplxType>();
		
		String previousConsCode = null;
		
		for (DonorCplxType donorCplxType : donorCplxTypes) {
			// Netbanx means web cash donations 
			if (!"Netbanx".equals(payload.getSupplierID())){ 
				for (ConsCodes consCodes : donorCplxType.getConsCodes()){
					if (previousConsCode == null){
						previousConsCode = consCodes.getCode();
					} else if (!consCodes.getCode().equals(previousConsCode)){
						addTo(errors, donorCplxType, "Not all cons codes are the same.");
					}
				}
			}
			if (donorCplxType.getDonationDetails() == null && isNotExclusivelyAnHonor(donorCplxType, payload) 
					&& isNotAWebCashInMemoryRecord(donorCplxType, payload)){
				// it is a prospect, so this was requested:
				if (addressIsBlank(donorCplxType)){
					addTo(errors, donorCplxType, "Some part of the address is blank and shouldn't be");
				}
			} else if (Boolean.TRUE.equals(donorCplxType.isPrimaryAddress())){
				if (addressIsBlank(donorCplxType)){
					addTo(errors, donorCplxType, "Some part of the address is blank and shouldn't be");
				}
			}
			
			if (donorCplxType.getDonationDetails() != null && donorCplxType.getDonationDetails().getDirectDebitDonationCplxType()!=null){
				dds.put(donorCplxType, donorCplxType.getDonationDetails().getDirectDebitDonationCplxType());
			}
			
			if (StringUtils.containsIgnoreCase(donorCplxType.getFirstName()+donorCplxType.getMiddleName()+donorCplxType.getLastName(),"test")){
				toRemove.add(donorCplxType);
			}
		}

		donorCplxTypes.removeAll(toRemove);

		for (Entry<DonorCplxType, DirectDebitDonationCplxType> dd : dds.entrySet()) {
			if (!"Direct Debit".equals(dd.getValue().getPaymentType())){
				addTo(errors, dd.getKey(), "Not all DD's have Direct Debit as payment type.");
			}
			if (!"Not Posted".equals(dd.getValue().getPostStatus())){
				addTo(errors, dd.getKey(), "Not every post status is Not Posted.");
			}
			if (!"Specific Day".equals(dd.getValue().getScheduleMonthlyType())){
				addTo(errors, dd.getKey(), "ScheduleMonthlyType is not Specific Day");				
			}
			
			if (dd.getValue().getNoOfInstalments()!= null){
				switch (dd.getValue().getNoOfInstalments().intValue()) {
					case 10:
					case 3:
						if (!"Annually".equals(dd.getValue().getInstallmentFreq())){addTo(errors, dd.getKey(), "Number of installments is not always set up properly against InstallmentFreq.");}
						break;
					case 20:
					case 6:
						if (!"Semi-Annually".equals(dd.getValue().getInstallmentFreq())){addTo(errors, dd.getKey(), "Number of installments is not always set up properly against InstallmentFreq.");}
						break;
					case 40:
					case 12:
						if (!"Quarterly".equals(dd.getValue().getInstallmentFreq())){addTo(errors, dd.getKey(), "Number of installments is not always set up properly against InstallmentFreq.");}
						break;
					case 120:
					case 36:
						if (!"Monthly".equals(dd.getValue().getInstallmentFreq())){addTo(errors, dd.getKey(), "Number of installments is not always set up properly against InstallmentFreq.");}
						break;
					case 60:
					case 18:
						if (!"Bimonthly".equals(dd.getValue().getInstallmentFreq())){addTo(errors, dd.getKey(), "Number of installments is not always set up properly against InstallmentFreq.");}
						break;
					default:
						addTo(errors, dd.getKey(), "Number of installments not set up properly.");
						break;
				}
			}
		}
		
		if (!errors.isEmpty()){
			return reporter.log(message, errors);
		}
		return message;
	}

	private boolean isNotAWebCashInMemoryRecord(DonorCplxType donor, GOSHCC payload) {
		for (Relationship relationship : payload.getRelationship()) {
			if (StringUtils.equals(relationship.getRelatedSupplierDonorID(), donor.getSupplierDonorID()) &&
				isDeceased(donor) && hasConsCodeOfMemo(donor)){
				return false;
			}
		}
		return true;
	}

	private boolean isDeceased(DonorCplxType donor) {
		return donor.getDeceasedDate() != null;
	}

	private boolean hasConsCodeOfMemo(DonorCplxType donor) {
		List<ConsCodes> consCodes = donor.getConsCodes();
		for (ConsCodes consCode : consCodes) {
			if (StringUtils.equals(consCode.getCode(),"MEMO")){
				return true;
			}
		}
		return false;
	}

	private void addTo(Map<DonorCplxType, String> errors, DonorCplxType donorCplxType, String string) {
		String prev = errors.get(donorCplxType);
		if (StringUtils.isBlank(prev)){
			errors.put(donorCplxType, string);
		} else {
			prev += ("; " + string);
			errors.put(donorCplxType, prev);
		}
	}

	private boolean isNotExclusivelyAnHonor(DonorCplxType donor, GOSHCC payload) {
		boolean donorIsAnHonor = false;
		boolean donorIsAnAcknoledgee = false;
		for (Tribute tribute : payload.getTribute()) {
			if (StringUtils.equals(donor.getSupplierDonorID(),tribute.getHonerSupplierDonorID())){
				donorIsAnHonor = true;
			}
			if (tribute.getAcknowledgeeSupplierDonorID().contains(donor.getSupplierDonorID())){
				donorIsAnAcknoledgee = true;
			}
		}
		return donorIsAnAcknoledgee || !donorIsAnHonor;
	}

	private boolean addressIsBlank(DonorCplxType donorCplxType) {
		boolean addressIsBlank = false;
		if (donorCplxType.getAddress() == null || donorCplxType.getAddress().getAddressLine().isEmpty()){
			addressIsBlank = true;
		} else {
			int count = 0;
			for (String line : donorCplxType.getAddress().getAddressLine()) {
				if (StringUtils.isBlank(line)){
					count++;
				}
			}
			if (count == donorCplxType.getAddress().getAddressLine().size()){
				addressIsBlank = true;
			}
		} 
		if (StringUtils.isBlank(donorCplxType.getCity())||StringUtils.isBlank(donorCplxType.getPostCode())){
			addressIsBlank = true;
		}
		return addressIsBlank;
	}
	
	@Autowired
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
}
