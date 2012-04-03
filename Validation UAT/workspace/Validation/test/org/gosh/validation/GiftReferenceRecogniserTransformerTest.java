package org.gosh.validation;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.validation.general.error.Reporter;
import org.gosh.validation.tributes.GiftReferenceNoRecogniserTransformer;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

public class GiftReferenceRecogniserTransformerTest extends MockObjectTestCase {
	public void testRecognisedCase(){
		GiftReferenceNoRecogniserTransformer transformer = new GiftReferenceNoRecogniserTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC payload = new GOSHCC();
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(payload);
		addDonorWith(payload, "00000003/1");

		checking(new Expectations(){{
			oneOf(reporter).log(with(message), with("The following gifts have ref nos that are already in the database: [00000003/1]"));
			will(returnValue(message));
		}});
		transformer.transform(message);
	}
	
	public void testEmptyCase(){
		GiftReferenceNoRecogniserTransformer transformer = new GiftReferenceNoRecogniserTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC payload = new GOSHCC();
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(payload);

		transformer.transform(message);
	}

	public void testUnrecognisedCase(){
		GiftReferenceNoRecogniserTransformer transformer = new GiftReferenceNoRecogniserTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC payload = new GOSHCC();
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(payload);
		addDonorWith(payload, "Unrecognisable");

		Message<GOSHCC> transformed = transformer.transform(message);
		assertFalse(transformed.getPayload().getDonorCplxType().get(0).getDonationDetails().getCashDonationCplxType().isEmpty());
	}
	
	/** has been noticed as slow, trying to improve */	
	public void testSpeed(){
		GiftReferenceNoRecogniserTransformer transformer = new GiftReferenceNoRecogniserTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC payload = new GOSHCC();
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(payload);
		for(int i = 0; i<1000; i++){
			addDonorWith(payload, "Unrecognisable");
		}

		Message<GOSHCC> transformed = transformer.transform(message);
		assertFalse(transformed.getPayload().getDonorCplxType().get(0).getDonationDetails().getCashDonationCplxType().isEmpty());
	}
	
	private void addDonorWith(GOSHCC payload, String refNo) {
		DonorCplxType donorCplxType = new DonorCplxType();
		payload.getDonorCplxType().add(donorCplxType);
		DonationDetails value = new DonationDetails();
		donorCplxType.setDonationDetails(value);
		CashDonationCplxType cashDonationCplxType = new CashDonationCplxType();
		value.getCashDonationCplxType().add(cashDonationCplxType);
		cashDonationCplxType.setRefNo(refNo);
	}
}