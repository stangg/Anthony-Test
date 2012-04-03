package org.gosh.validation.general.bank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.xmldb.api.base.XMLDBException;

/**
 * This transformer validates against the official BACS list. 
 * It is currently commented out in validation.xml but you can
 * re-enable it easily by changing this file.
 * 
 * It looks up the actual data using {@link BankXindiceDAO}.
 * 
 * @author Kevin.Savage
 */
public class BankValidationTransformer {
	private BankDAO dao;
	private Reporter reporter;
	
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) throws XMLDBException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		GOSHCC payload = message.getPayload();
		List<DonorCplxType> donorCplxTypes = payload.getDonorCplxType();
		ArrayList<DonorBankCplxType> listOfBanksFromFile = getListOfBanks(donorCplxTypes);
		
		Map<DonorBankCplxType, Boolean> lookup = dao.lookupBankFromList(listOfBanksFromFile);
		
		String errorMessage = "The following Banks were not valid: ";
		boolean thereWereErrors = false;
		
		for (Entry<DonorBankCplxType, Boolean> entry : lookup.entrySet()) {
			if (entry.getValue()!=null && !entry.getValue()){
				errorMessage += entry.getKey().getBankName() + " : ";
				errorMessage += entry.getKey().getBankSort() + " ; ";
				thereWereErrors = true;
			}
		}
		
		if (!thereWereErrors){
			return message;
		}
		
		return reporter.log(message, errorMessage);
	}

	private ArrayList<DonorBankCplxType> getListOfBanks(
			List<DonorCplxType> donorCplxTypes) {
		ArrayList<DonorBankCplxType> listOfBanksFromFile = new ArrayList<DonorBankCplxType>();
		for (DonorCplxType donor : donorCplxTypes) {
			if (donor.getDonationDetails()!=null && donor.getDonationDetails().getDirectDebitDonationCplxType()!= null && donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType()!=null){
				listOfBanksFromFile.add(donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType());
			}
		}
		return listOfBanksFromFile;
	}
	
	@Required
	public void setDao(BankDAO dao) {
		this.dao = dao;
	}
	
	@Autowired @Required
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
}
