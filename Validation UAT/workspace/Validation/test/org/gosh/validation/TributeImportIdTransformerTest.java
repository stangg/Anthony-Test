/**
 * 
 */
package org.gosh.validation;

import java.util.Collections;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.Tribute;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.validation.general.error.Reporter;
import org.gosh.validation.tributes.TributeImportIdTransformer;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

/**
 * @author Kevin.Savage
 *
 */
public class TributeImportIdTransformerTest extends MockObjectTestCase {
	public void testNullCase() throws Exception {
		TributeImportIdTransformer transformer = new TributeImportIdTransformer();
		transformer.transform(new GenericMessage<GOSHCC>(new GOSHCC()));
	}
	
	public void testTruncation() throws Exception {
		TributeImportIdTransformer transformer = new TributeImportIdTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		GOSHCC goshcc = new GOSHCC();
		Tribute tribute = new Tribute();
		goshcc.getTribute().add(tribute);
		tribute.setImportID("Oh my, what a very, very long import Id");
		
		transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertEquals("Oh my, what a very, ", tribute.getImportID());
	}
	
	public void testNoTruncation() throws Exception {
		TributeImportIdTransformer transformer = new TributeImportIdTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		GOSHCC goshcc = new GOSHCC();
		Tribute tribute = new Tribute();
		goshcc.getTribute().add(tribute);
		tribute.setImportID("Short id");
		
		transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertEquals("Short id", tribute.getImportID());
		
	}
	
	public void testTruncationResultingInNonUniqueId() throws Exception {
		TributeImportIdTransformer transformer = new TributeImportIdTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		GOSHCC goshcc = new GOSHCC();
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		Tribute tribute = new Tribute();
		goshcc.getTribute().add(tribute);
		tribute.setImportID("00170-602-0000000604-plus some more that will be truncated");

		checking(new Expectations(){{
			oneOf(reporter).log(message, Collections.singletonList("There was an import id on a tribute that in its truncated form was not unique. This id was: 00170-602-0000000604 (truncated)"));
		}});
		
		transformer.transform(message);
		
		assertEquals("00170-602-0000000604", tribute.getImportID());
	}
	
	public void testTruncationOnGift() throws Exception {
		TributeImportIdTransformer transformer = new TributeImportIdTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		CashDonationCplxType donation = new CashDonationCplxType();
		donationDetails.getCashDonationCplxType().add(donation);
		// truncates to a real value in the database
		donation.setTributeID("00170-602-0000000604 oh my, what a very, very long import Id");
		
		transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertEquals("00170-602-0000000604", donation.getTributeID());
	}

	public void testTruncationOfRefNoOnGift() throws Exception {
		TributeImportIdTransformer transformer = new TributeImportIdTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		CashDonationCplxType donation = new CashDonationCplxType();
		donationDetails.getCashDonationCplxType().add(donation);
		donation.setRefNo("Oh my, what a very, very long ref no");
		
		transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertEquals("Oh my, what a very, ", donation.getRefNo());
	}

	
	public void testNoTruncationOnGift() throws Exception {
		TributeImportIdTransformer transformer = new TributeImportIdTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		CashDonationCplxType donation = new CashDonationCplxType();
		donationDetails.getCashDonationCplxType().add(donation);
		donation.setTributeID("00170-602-0000000604");
		
		transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertEquals("00170-602-0000000604", donation.getTributeID());
	}
	
	public void testGiftWithATributeIDThatDoesntExist() throws Exception {
		TributeImportIdTransformer transformer = new TributeImportIdTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		CashDonationCplxType donation = new CashDonationCplxType();
		donationDetails.getCashDonationCplxType().add(donation);
		donation.setTributeID("doesn''t exist");
		
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		checking(new Expectations(){{
			oneOf(reporter).log(message, Collections.singletonList("There was a donation with a tribute ID of doesn''t exist but this is not a valid tribute id in either of the RE database or this file"));
		}});
		
		transformer.transform(message);
	}

	public void testGiftWithATributeIDThatExistsInTheFileButNotInTheDB() throws Exception {
		TributeImportIdTransformer transformer = new TributeImportIdTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		CashDonationCplxType donation = new CashDonationCplxType();
		donationDetails.getCashDonationCplxType().add(donation);
		donation.setTributeID("exists in file");
		Tribute tribute = new Tribute();
		goshcc.getTribute().add(tribute);
		tribute.setImportID("exists in file");
		
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(message);
	}
	
	public void testSuspectForInefficentCode() throws Exception {
		TributeImportIdTransformer transformer = new TributeImportIdTransformer();
		
		GOSHCC goshcc = new GOSHCC();
		for (int i = 0; i < 10000; i++) {
			goshcc.getDonorCplxType().add(new DonorCplxType());
		}
		
		transformer.transform(new GenericMessage<GOSHCC>(goshcc));
	}
}
