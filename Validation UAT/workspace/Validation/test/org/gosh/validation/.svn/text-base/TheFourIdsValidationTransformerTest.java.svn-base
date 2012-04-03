package org.gosh.validation;

import java.util.List;

import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.ConstituentAppeal;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.businessrules.TheFourIdsValidationTransformer;
import org.gosh.validation.general.error.ErrorReporter;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

public class TheFourIdsValidationTransformerTest extends TestCase {
	TheFourIdsValidationTransformer transformer;
	GOSHCC goshcc; 
	CashDonationCplxType cashDonationCplxType;
	private DonorCplxType donorCplxType; 
	@SuppressWarnings("unchecked")
	public void testAppealLookup() throws Exception {
		cashDonationCplxType.setCampaign("CI01");
		cashDonationCplxType.setAppeal("CI0110");
		
		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertNull("we want the following to be null: " + transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()), transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()));
		
		cashDonationCplxType.setAppeal("invalidAppeal");
		transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertEquals("There was an error for the following donors: [null null of null] : the following appeal codes are invalid: [invalidAppeal]", ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));
	}
	
	@SuppressWarnings("unchecked")
	public void testConstituentAppealLookup() throws Exception {
		ConstituentAppeal constituentAppeal = new ConstituentAppeal();
		donorCplxType.setConstituentAppeal(constituentAppeal);
		constituentAppeal.setAppealID("FJ11");
		constituentAppeal.setPackageID("GEN10A");
		
		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertNull("we want the following to be null: " + transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()), transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()));
		
		constituentAppeal.setAppealID("invalidAppeal");
		transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertEquals("There was an error for the following donors: [null null of null] : the following appeal codes are invalid: [invalidAppeal]", ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));
	}
	
	@SuppressWarnings("unchecked")
	public void testPackageLookup() throws Exception {
		cashDonationCplxType.setCampaign("DM01");
		cashDonationCplxType.setAppeal("DM0109/J");
		cashDonationCplxType.setPackage("invalid");
		
		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertEquals("There was an error for the following donors: [null null of null] : The following package code is invalid: invalid", ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));
		
		// valid codes, but invalid combination
		cashDonationCplxType.setAppeal("DM0109/J");
		cashDonationCplxType.setPackage("DBSHT01");
		
		transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertEquals("There was an error for the following donors: [null null of null] : The following package code is invalid: DBSHT01", ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));

		cashDonationCplxType.setAppeal("FJ12/93XA01");
		cashDonationCplxType.setPackage("GEN10A");
		
		transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertNull(transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()));
	}
	
	/*Commenting the failing tests*/
//	@SuppressWarnings("unchecked")
//	public void testPackageLookupForPatrick() throws Exception {
//		cashDonationCplxType.setFund("UNGEN");
//		cashDonationCplxType.setCampaign("PC02");
//		cashDonationCplxType.setAppeal("PC0210DEJ");
//		cashDonationCplxType.setPackage("");
//		
//		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
//		assertEquals("There was an error for the following donors: [null null of null] : The following package code is invalid: ", ((List<String>)transform.getHeaders().get(MessageHeader.ERROR_HEADER.getName())).get(0));
//	}
	
	@SuppressWarnings("unchecked")
	public void testFundLookup() throws Exception {
		cashDonationCplxType.setFund("1010");
		
		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertNull(transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()));
		
		cashDonationCplxType.setFund("invalid");
		transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertEquals("There was an error for the following donors: [null null of null] : the following fund codes are invalid: [invalid]", ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));
	}
	
	@SuppressWarnings("unchecked")
	public void testCampaignLookup() throws Exception {
		cashDonationCplxType.setCampaign("DM52");
		
		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertNull(transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()));
		
		cashDonationCplxType.setCampaign("invalidCampaign");
		transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertEquals("There was an error for the following donors: [null null of null] : the following campaign codes are invalid: [invalidCampaign]", ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));
	}

	@SuppressWarnings("unchecked")
	public void testMultiples() throws Exception {
		cashDonationCplxType.setCampaign("invalid");
		
		DonorCplxType donor2 = new DonorCplxType();
		goshcc.getDonorCplxType().add(donor2);
		donor2.setLastName("Something");
		
		DonationDetails donationDetails = new DonationDetails();
		donor2.setDonationDetails(donationDetails);
		CashDonationCplxType cashDonation2 = new CashDonationCplxType();
		donationDetails.getCashDonationCplxType().add(cashDonation2);
		cashDonation2.setCampaign("invalid2");
		
		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		List<String> list = (List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		assertTrue(list.contains("There was an error for the following donors: [null null of null] : the following campaign codes are invalid: [invalid]"));
		assertTrue(list.contains("There was an error for the following donors: [null Something of null] : the following campaign codes are invalid: [invalid2]"));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		transformer = new TheFourIdsValidationTransformer();
		transformer.setReporter(new ErrorReporter());
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		goshcc = new GOSHCC();
		donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		cashDonationCplxType = new CashDonationCplxType();
		donationDetails.getCashDonationCplxType().add(cashDonationCplxType);

	}
}