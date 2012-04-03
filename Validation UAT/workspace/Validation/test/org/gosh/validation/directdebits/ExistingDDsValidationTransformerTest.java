package org.gosh.validation.directdebits;

import java.util.List;

import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.PDDUpgradeFlag;
import org.gosh.validation.TestDataSourceFactory;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.error.ErrorReporter;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

public class ExistingDDsValidationTransformerTest extends TestCase {
	ExistingDDsValidationTransformer transformer;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		transformer = new ExistingDDsValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		transformer.setReporter(new ErrorReporter());
	}
	
	public void testWithOneExisting() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		setupGoshccWithUpgradeFlag(goshcc , "00001-057-0000001", true);

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertNull(transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()));
	}
	
	public void testWithUpgradeFlagFalse() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		setupGoshccWithUpgradeFlag(goshcc , "not valid", false);

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertNull("expected null but got" + transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()), transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()));
	}

	public void testNullCase() throws Exception {
		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(new GOSHCC()));
		assertNull(transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()));
	}

	@SuppressWarnings("unchecked")
	public void testWithOneNonExistingId() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		setupGoshccWithUpgradeFlag(goshcc, "not an Import Id", true);
		
		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertEquals("There are previous import IDs that don't exist in RE.", ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));
	}

	@SuppressWarnings("unchecked")
	public void testWithInconsitentFlags() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		setupGoshccWithUpgradeFlag(goshcc, "not an Import Id", true);
		setupGoshccWithUpgradeFlag(goshcc, "not an Import Id", false);
		
		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertEquals("The upgrade flag is not consistent in the file.", ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));
	}

	@SuppressWarnings("unchecked")
	public void testWithMultipleIds() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		setupGoshccWithUpgradeFlag(goshcc, "not an Import Id", true);
		setupGoshccWithUpgradeFlag(goshcc, "00001-057-0000001", true);

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertEquals("There are previous import IDs that don't exist in RE.", ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));
	}
	
	@SuppressWarnings("unchecked")
	public void testPreviouslyExistingDDs() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		setupGoshccWithUpgradeFlag(goshcc, null, false);
		goshcc.getDonorCplxType().get(0).setConstituentID("40529828");
		
		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertEquals("For records that are not upgrades there are previously existing DD's in RE. The import Id's for these were: [933694]", ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));
	}
	
	// Commenting the below test case as the data may not be existing
//	@SuppressWarnings("unchecked")
//	public void testPreviouslyExistingDDsWithNicolesCase() throws Exception {
//		GOSHCC goshcc = new GOSHCC();
//		setupGoshccWithUpgradeFlag(goshcc, null, false);
//		goshcc.getDonorCplxType().get(0).setConstituentID("40533643");
//		
//		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
//		assertEquals("For records that are not upgrades there are previously exsting DD's in RE. The import Id's for these were: [937944]", ((List<String>)transform.getHeaders().get(MessageHeader.ERROR_HEADER.getName())).get(0));
//	}
	
	public void testNoPreviouslyExistingDDsMessageIfCashDonation() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		donationDetails.getCashDonationCplxType().add(new CashDonationCplxType());
		goshcc.getDonorCplxType().add(donorCplxType);
		
		goshcc.getDonorCplxType().get(0).setConstituentID("40533643");
		
		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertNull(transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()));
	}
	
	public void testNoPreviouslyExistingDDsCase() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		donationDetails.getCashDonationCplxType().add(new CashDonationCplxType());
		goshcc.getDonorCplxType().add(donorCplxType);
		
		goshcc.getDonorCplxType().get(0).setConstituentID("40342106");
		
		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertNull(transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()));
	}
	
	public void testLoadCaseWithMany() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		for(int i = 0; i<150; i++){
			setupGoshccWithUpgradeFlag(goshcc , "00001-057-0000001", true);
		}

		transformer.transform(new GenericMessage<GOSHCC>(goshcc));
	}
	
	private GOSHCC setupGoshccWithUpgradeFlag(GOSHCC goshcc, String importId, boolean pddUpgradeFlag) {
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		PDDUpgradeFlag upgradeFlag = new PDDUpgradeFlag();
		directDebitDonationCplxType.setPDDUpgradeFlag(upgradeFlag);
		
		upgradeFlag.setGiftimportID(importId);
		upgradeFlag.setPDDUpgradeFlag(pddUpgradeFlag);
		
		return goshcc;
	}
}
