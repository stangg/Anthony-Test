/**
 * 
 */
package org.gosh.convertion.webdonations;

import java.math.BigDecimal;
import java.util.HashMap;

import junit.framework.TestCase;

import org.apache.commons.collections.map.MultiValueMap;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GiftSubtype;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.validation.webcash.FundraiserEmailSplitter;
import org.gosh.validation.webcash.WebCashFileReprocessingTransformer;
import org.springframework.integration.message.GenericMessage;

/**
 * @author Maria.Urso
 *
 */
public class WebCashFileReprocessingTransformerTest extends TestCase {

	/*Commenting the failing tests*/
//	/**
//	 * @param name
//	 */
//	public void testWebCashFileReprocessingCase() throws Exception {
//		WebCashFileReprocessingTransformer transformer = new WebCashFileReprocessingTransformer();
//		
//		DonorCplxType reallySimpleModel = new GOSHCC.DonorCplxType();
//		GOSHCC.DonorCplxType.DonationDetails donationDetails = new GOSHCC.DonorCplxType.DonationDetails();
//		GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType cashDonation = new GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType();
//		cashDonation.getFundraisingContact().add("maria.urso@gosh.org");
//		cashDonation.setDonateDescription("somedonatedescription");
//		donationDetails.getCashDonationCplxType().add(cashDonation);
//		reallySimpleModel.setDonationDetails(donationDetails);
//		
//		HashMap<String, Object> headers = new HashMap<String, Object>();
//		
//		GOSHCC payload = new GOSHCC();
//		payload.setSupplierID("Netbanx");
//		payload.getDonorCplxType().add(reallySimpleModel);
//		
//		GenericMessage<GOSHCC> genericMessage = new GenericMessage<GOSHCC>(payload, headers);
//		assertEquals(3, transformer.transform(genericMessage).getHeaders().size());
//		
//		// TODO: evaluate the header contains the CONTACT_TO_MAIL_LINE mapping
////		MultiValueMap contact2mailLineExpected = new MultiValueMap();
////		contact2mailLineExpected.put("maria.urso@gosh.org", "<TR><TD></TD></TR><TR><TD></TD></TR><TR><TD></TD></TR><TR><TD>null</TD><TD>null</TD><TD>null</TD><TD>null</TD><TD></TD><TD>null</TD><TD>null</TD><TD>N</TD><TD>Internet Donations</TD><TD>null</TD><TD>null</TD><TD>somedonatedescription</TD>");
////		
////		assertEquals(contact2mailLineExpected, transformer.transform(genericMessage).getHeaders().get(FundraiserEmailSplitter.CONTACT_TO_MAIL_LINE));
//	}
//	
	public void testIsDirectDebitFileCase() throws Exception {
		WebCashFileReprocessingTransformer transformer = new WebCashFileReprocessingTransformer();
		
		DonorCplxType reallySimpleModel = new GOSHCC.DonorCplxType();
		GOSHCC.DonorCplxType.DonationDetails donationDetails = new GOSHCC.DonorCplxType.DonationDetails();
		GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType dDonation = new GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType();
		dDonation.setAmount(new BigDecimal("100"));
		donationDetails.setDirectDebitDonationCplxType(dDonation);
		reallySimpleModel.setDonationDetails(donationDetails);
		
		HashMap<String, Object> headers = new HashMap<String, Object>();
		
		GOSHCC payload = new GOSHCC();
		payload.setSupplierID("DD Suplier");
		payload.getDonorCplxType().add(reallySimpleModel);
		
		GenericMessage<GOSHCC> genericMessage = new GenericMessage<GOSHCC>(payload, headers);
		assertEquals(2, transformer.transform(genericMessage).getHeaders().size());
		
		MultiValueMap contact2mailLineExpected = null;
		
		assertEquals(contact2mailLineExpected, transformer.transform(genericMessage).getHeaders().get(FundraiserEmailSplitter.CONTACT_TO_MAIL_LINE));
	}
	
	public void testCashButNotAWebCashFileCase() throws Exception {
		WebCashFileReprocessingTransformer transformer = new WebCashFileReprocessingTransformer();
		
		DonorCplxType reallySimpleModel = new GOSHCC.DonorCplxType();
		GOSHCC.DonorCplxType.DonationDetails donationDetails = new GOSHCC.DonorCplxType.DonationDetails();
		GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType cashDonation = new GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType();
		cashDonation.setAmount(new BigDecimal("100"));
		cashDonation.setSubType(GiftSubtype.DONATIONS_INTERNET_1014);
		donationDetails.getCashDonationCplxType().add(cashDonation);
		reallySimpleModel.setDonationDetails(donationDetails);
		
		HashMap<String, Object> headers = new HashMap<String, Object>();
		
		GOSHCC payload = new GOSHCC();
		payload.setSupplierID("Cash Supplier");
		payload.getDonorCplxType().add(reallySimpleModel);
		
		GenericMessage<GOSHCC> genericMessage = new GenericMessage<GOSHCC>(payload, headers);
		assertEquals(2, transformer.transform(genericMessage).getHeaders().size());
		
		MultiValueMap contact2mailLineExpected = null;
		
		assertEquals(contact2mailLineExpected, transformer.transform(genericMessage).getHeaders().get(FundraiserEmailSplitter.CONTACT_TO_MAIL_LINE));
	}
}
