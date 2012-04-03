package org.gosh.validation;


import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.ConsCodes;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.Tribute;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.MiscValidationTransformer;
import org.gosh.validation.general.error.ErrorReporter;
import org.gosh.validation.general.error.Reporter;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

public class MiscValidationTransformerTest extends MockObjectTestCase {
	
	public void testNullCase() throws Exception {
		MiscValidationTransformer transformer = new MiscValidationTransformer();
		transformer.transform(new GenericMessage<GOSHCC>(new GOSHCC()));
	}

	@SuppressWarnings({ "serial" })
	public void testConstCodeAlwaysTheSame() throws Exception {
		MiscValidationTransformer transformer = new MiscValidationTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
			
		GOSHCC goshcc = new GOSHCC();
		addDonorWithCode(goshcc, "same");
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);

		// with one...
		transformer.transform(message);
		
		// .. then with two the same ...
		addDonorWithCode(goshcc, "same");
		transformer.transform(message);
		
		// .. then with one different.
		final DonorCplxType lastDonor = addDonorWithCode(goshcc, "different");
		checking(new Expectations(){{
			oneOf(reporter).log(message, new HashMap<DonorCplxType, String>(){{put(lastDonor, "Not all cons codes are the same.");}});
		}});
		transformer.transform(message);
	}
	
	public void testConstCodeCanDifferIfItsANatbanxFile() throws Exception {
		MiscValidationTransformer transformer = new MiscValidationTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
			
		GOSHCC goshcc = new GOSHCC();
		goshcc.setSupplierID("Netbanx");
		addDonorWithCode(goshcc, "same");
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);

		// with one...
		transformer.transform(message);
		
		// ... then with two that are different...
		addDonorWithCode(goshcc, "different");
		transformer.transform(message);
		
		// .. then with two different.
		addDonorWithCode(goshcc, "differentAgain");

		transformer.transform(message);
	}
	
	public DonorCplxType addDonorWithCode(GOSHCC goshcc, String code){
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		ConsCodes consCodes = new ConsCodes();
		donorCplxType.getConsCodes().add(consCodes);
		consCodes.setCode(code);
		// this is to make this not a prospect so we don't get that error.
		donorCplxType.setDonationDetails(new DonationDetails());
		return donorCplxType;
	}
	
	@SuppressWarnings("unchecked")
	public void testFixedValueDDChecks() throws Exception {
		MiscValidationTransformer transformer = new MiscValidationTransformer();
		transformer.setReporter(new ErrorReporter());
			
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertEquals(transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()).toString(), 3, ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0).split(";").length);
	}

	@SuppressWarnings("unchecked")
	public void testInstallmentDDChecks() throws Exception {
		MiscValidationTransformer transformer = new MiscValidationTransformer();
		transformer.setReporter(new ErrorReporter());
			
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		directDebitDonationCplxType.setInstallmentFreq("Monthly");
		directDebitDonationCplxType.setNoOfInstalments(BigInteger.valueOf(10));

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertTrue(((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0).contains("Number of installments is not always set up properly against InstallmentFreq."));
	}
	
	@SuppressWarnings("unchecked")
	public void testInstallmentDDChecksFor3And10YearPledges() throws Exception {
		MiscValidationTransformer transformer = new MiscValidationTransformer();
		transformer.setReporter(new ErrorReporter());
			
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		directDebitDonationCplxType.setInstallmentFreq("Annually");
		directDebitDonationCplxType.setNoOfInstalments(BigInteger.valueOf(3));

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertFalse(((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0).contains("Number of installments is not always set up properly against InstallmentFreq."));

		directDebitDonationCplxType.setNoOfInstalments(BigInteger.valueOf(10));
		transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertFalse(((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0).contains("Number of installments is not always set up properly against InstallmentFreq."));
	}
	
	
	@SuppressWarnings("unchecked")
	public void testInstallmentDDChecksFor3And10YearBiMonthly() throws Exception {
		MiscValidationTransformer transformer = new MiscValidationTransformer();
		transformer.setReporter(new ErrorReporter());
			
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		directDebitDonationCplxType.setInstallmentFreq("Bimonthly");
		directDebitDonationCplxType.setNoOfInstalments(BigInteger.valueOf(18));

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertFalse(((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0).contains("Number of installments is not always set up properly against InstallmentFreq."));

		directDebitDonationCplxType.setNoOfInstalments(BigInteger.valueOf(60));
		transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertFalse(((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0).contains("Number of installments is not always set up properly against InstallmentFreq."));
		
		directDebitDonationCplxType.setInstallmentFreq("Fortnightly");
		directDebitDonationCplxType.setNoOfInstalments(BigInteger.valueOf(60));
		transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertTrue(((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0).contains("Number of installments is not always set up properly against InstallmentFreq."));
		
	}
	
	@SuppressWarnings("unchecked")
	public void testInstallmentDDChecksBSTestCase() throws Exception {
		MiscValidationTransformer transformer = new MiscValidationTransformer();
		transformer.setReporter(new ErrorReporter());
			
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		directDebitDonationCplxType.setInstallmentFreq("Monthly");
		directDebitDonationCplxType.setNoOfInstalments(BigInteger.valueOf(120));

		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		
		Message<GOSHCC> transform = transformer.transform(message);
		assertFalse(((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0).contains("Number of installments is not always set up properly."));
	}
	
	public void testRemovesTestRecords() throws Exception {
		MiscValidationTransformer transformer = new MiscValidationTransformer();
		transformer.setReporter(new ErrorReporter());
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setLastName("test");
		goshcc.getDonorCplxType().add(donorCplxType);
		
		transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertTrue(goshcc.getDonorCplxType().isEmpty());
	}
	
	@SuppressWarnings("serial")
	public void testAddressIsCheckedForProspects() throws Exception {
		MiscValidationTransformer transformer = new MiscValidationTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donor = new DonorCplxType();
		goshcc.getDonorCplxType().add(donor);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
	
		checking(new Expectations(){{
			oneOf(reporter).log(message, new HashMap<DonorCplxType, String>(){{put(donor, "Some part of the address is blank and shouldn't be");}});
		}});
		transformer.transform(message);
	}
	
	@SuppressWarnings("serial")
	public void testAddressIsCheckedPrimaryAddresses() throws Exception {
		MiscValidationTransformer transformer = new MiscValidationTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donor = new DonorCplxType();
		goshcc.getDonorCplxType().add(donor);
		donor.setDonationDetails(new DonationDetails());
		donor.setPrimaryAddress(true);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
	
		checking(new Expectations(){{
			oneOf(reporter).log(message, new HashMap<DonorCplxType, String>(){{put(donor, "Some part of the address is blank and shouldn't be");}});
		}});
		transformer.transform(message);
	}
	
	public void testAddressIsNotCheckedForNonPrimaryAddresses() throws Exception {
		MiscValidationTransformer transformer = new MiscValidationTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donor = new DonorCplxType();
		goshcc.getDonorCplxType().add(donor);
		donor.setDonationDetails(new DonationDetails());
		donor.setPrimaryAddress(false);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
	
		transformer.transform(message);
	}

	public void testAddressIsNotCheckedForHonors() throws Exception {
		MiscValidationTransformer transformer = new MiscValidationTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donor = new DonorCplxType();
		donor.setSupplierDonorID("ID");
		goshcc.getDonorCplxType().add(donor);
		Tribute tribute = new Tribute();
		tribute.setHonerSupplierDonorID("ID");
		goshcc.getTribute().add(tribute);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
	
		transformer.transform(message);
	}
	
	@SuppressWarnings("serial")
	public void testAddressIsCheckedForHonorsThatAreAlsoAcknowledgees() throws Exception {
		MiscValidationTransformer transformer = new MiscValidationTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donor = new DonorCplxType();
		donor.setSupplierDonorID("ID");
		goshcc.getDonorCplxType().add(donor);
		Tribute tribute = new Tribute();
		tribute.setHonerSupplierDonorID("ID");
		tribute.getAcknowledgeeSupplierDonorID().add("ID");
		goshcc.getTribute().add(tribute);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
	
		checking(new Expectations(){{
			oneOf(reporter).log(message, new HashMap<DonorCplxType, String>(){{put(donor, "Some part of the address is blank and shouldn't be");}});
		}});

		transformer.transform(message);
	}
}
