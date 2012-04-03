package org.gosh.validation;

import groovy.lang.GroovyClassLoader;
import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.validation.general.LoggingTransformer;
import org.springframework.integration.message.GenericMessage;

public class AdjustCafFileBatchNumberTest extends TestCase {
	LoggingTransformer transformer;
	public AdjustCafFileBatchNumberTest(){
		super();
		try {
			transformer = (LoggingTransformer) new GroovyClassLoader(this.getClass().getClassLoader()).loadClass("org.gosh.validation.general.businessrules.AdjustCafFileBatchNumbersTransformer").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testNullCase() throws Exception {
		GenericMessage<GOSHCC> genericMessage = new GenericMessage<GOSHCC>(new GOSHCC());
		transformer.transform(genericMessage);
	}
	
	public void testChangeCase() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		addDonorWithCafGift(goshcc);
		
		GenericMessage<GOSHCC> genericMessage = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(genericMessage);
		
		assertEquals("2288c", goshcc.getBatchNo());
	}
	
	public void testChangeNonCafCase() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		CashDonationCplxType nafGift = addDonorWithCafGift(goshcc);
		nafGift.setAgency("caf -> naf");
		
		GenericMessage<GOSHCC> genericMessage = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(genericMessage);
		
		assertEquals("2288n", goshcc.getBatchNo());
	}
	
	public void testChangeMultipleCase() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		addDonorWithCafGift(goshcc);
		addDonorWithCafGift(goshcc);
		
		GenericMessage<GOSHCC> genericMessage = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(genericMessage);
		
		assertEquals("2288c", goshcc.getBatchNo());
	}

	public void testNoChangeCase() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		goshcc.setBatchNo("batchNo");
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);

		GenericMessage<GOSHCC> genericMessage = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(genericMessage);
		
		assertEquals("batchNo", goshcc.getBatchNo());
	}
	
	public void testNoChangeForSupplierOtherThanValldata() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		addDonorWithCafGift(goshcc);
		goshcc.setSupplierID("something else");

		GenericMessage<GOSHCC> genericMessage = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(genericMessage);
		
		assertEquals("2288", goshcc.getBatchNo());
	}
	
	public void testUnderLoad() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		for (int i=0; i<100; i++) {
			addDonorWithCafGift(goshcc);
		}

		GenericMessage<GOSHCC> genericMessage = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(genericMessage);
		
		assertEquals("2288c", goshcc.getBatchNo());
	}
	
	private CashDonationCplxType addDonorWithCafGift(GOSHCC goshcc) {
		goshcc.setBatchNo("2288"); // this was a real case from the db
		goshcc.setSupplierID("VALLDATA");
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		CashDonationCplxType cashDonationCplxType = new CashDonationCplxType();
		donationDetails.getCashDonationCplxType().add(cashDonationCplxType);
		cashDonationCplxType.setAgency("CAF");
		return cashDonationCplxType;
	}
}
