package org.gosh.validation.bank;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.bank.BankDAO;
import org.gosh.validation.general.bank.BankValidationTransformer;
import org.gosh.validation.general.error.ErrorReporter;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

public class BankValidationTransformerTest extends MockObjectTestCase {
	@SuppressWarnings("unchecked")
	public void testLookupWhenExists() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		final BankDAO mockDAO = mock(BankDAO.class);

		final DonorBankCplxType donorBankCplxType = setTestModel(goshcc, "40-44-19", "HSBC BANK PLC", "Tolworth");
		BankValidationTransformer bankValidationTransformer = setupTransformer(mockDAO);
		
		final HashMap<DonorBankCplxType, Boolean> lookupResults = new HashMap<DonorBankCplxType, Boolean>();
		lookupResults.put(donorBankCplxType, true);
		
		checking(new Expectations(){{
			oneOf(mockDAO).lookupBankFromList(Collections.singletonList(donorBankCplxType));
			will(returnValue(lookupResults));
		}});
		
		Message<GOSHCC> transformedMessage = bankValidationTransformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertNull(((List<String>)transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())));
	}
	
	@SuppressWarnings("unchecked")
	public void testLookupWhenDoesntExist() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		final BankDAO mockDAO = mock(BankDAO.class);

		final DonorBankCplxType donorBankCplxType = setTestModel(goshcc, "40-44-19", "HSBC BANK PLC", "Tolworth");
		BankValidationTransformer bankValidationTransformer = setupTransformer(mockDAO);
		
		final HashMap<DonorBankCplxType, Boolean> lookupResults = new HashMap<DonorBankCplxType, Boolean>();
		lookupResults.put(donorBankCplxType, false);
		
		checking(new Expectations(){{
			oneOf(mockDAO).lookupBankFromList(Collections.singletonList(donorBankCplxType));
			will(returnValue(lookupResults));
		}});
		
		Message<GOSHCC> transformedMessage = bankValidationTransformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertFalse(((List<String>)transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).isEmpty());
		assertEquals("The following Banks were not valid: HSBC BANK PLC : 40-44-19 ; ", ((List<String>)transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));
	}
	
	private BankValidationTransformer setupTransformer(final BankDAO mockDAO) {
		BankValidationTransformer bankValidationTransformer = new BankValidationTransformer();
		bankValidationTransformer.setDao(mockDAO);
		bankValidationTransformer.setReporter(new ErrorReporter());
		return bankValidationTransformer;
	}
	
	private DonorBankCplxType setTestModel(GOSHCC goshcc, String sortCode, String bankName, String postcode) {
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		return setTestModel(donorCplxType , sortCode, bankName, postcode);
	}
	
	private DonorBankCplxType setTestModel(DonorCplxType donorCplxType, String sortCode, String bankName, String postcode) {
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		
		DonorBankCplxType donorBankCplxType = setupDonorBank(sortCode, bankName, postcode);
		directDebitDonationCplxType.setDonorBankCplxType(donorBankCplxType);
		return donorBankCplxType;
	}

	private DonorBankCplxType setupDonorBank(String sortCode, String bankName, String branchName) {
		DonorBankCplxType donorBankCplxType = new DonorBankCplxType();
		
		donorBankCplxType.setBankSort(sortCode);
		donorBankCplxType.setBankName(bankName);
		donorBankCplxType.setBranchName(branchName);

		return donorBankCplxType;
	}
}
