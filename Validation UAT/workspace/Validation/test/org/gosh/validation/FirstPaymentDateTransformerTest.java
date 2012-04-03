package org.gosh.validation;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.validation.directdebits.FirstPaymentDateTransformer;

public class FirstPaymentDateTransformerTest extends TestCase {
	public void testDoesntMoveDate() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		
		XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(2011, 1, 1, 0, 0, 0, 0, 0);
		directDebitDonationCplxType.setDate1StPayment(date);
		
		new FirstPaymentDateTransformer().transform(goshcc);
		assertEquals(DatatypeFactory.newInstance().newXMLGregorianCalendar(2011, 2, 1, 0, 0, 0, 0, 0), directDebitDonationCplxType.getDate1StPayment());
	}
	
	public void testMovesDate() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		
		XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
		directDebitDonationCplxType.setDate1StPayment(date);
		
		new FirstPaymentDateTransformer().transform(goshcc);
		//incomplete
		
		GregorianCalendar today = new GregorianCalendar();
		
		if (DatatypeFactory.newInstance().newXMLGregorianCalendar(today).getMonth() == 12) {
			assertEquals("Adjustment is not correct", 1, directDebitDonationCplxType.getDate1StPayment().getMonth());
		} else {
			assertEquals(1, directDebitDonationCplxType.getDate1StPayment().getMonth()-DatatypeFactory.newInstance().newXMLGregorianCalendar(today).getMonth());
		}
	}
	
	public void testNullDate() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		new FirstPaymentDateTransformer().transform(goshcc);
	}
}
