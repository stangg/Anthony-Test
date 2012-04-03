package org.gosh.validation.general.bank;

import java.util.List;
import java.util.Map;

import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.xmldb.api.base.XMLDBException;

public interface BankDAO {

	public abstract Map<DonorBankCplxType, Boolean> lookupBankFromList(List<DonorBankCplxType> banks) throws XMLDBException, ClassNotFoundException, InstantiationException, IllegalAccessException;

}