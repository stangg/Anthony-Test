package org.gosh.validation.directdebits;

import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.springframework.integration.annotation.Transformer;

/**
 * This changes the date of the first payment of a DD if this payment 
 * is less that a week away. It is delayed by one month.
 * 
 * Hopefully in the future we wont need this because we will have cleverer
 * scheduling with BACS.
 * 
 * @author Kevin.Savage
 */
public class FirstPaymentDateTransformer {

	@Transformer
	public GOSHCC transform(GOSHCC goshcc) throws DatatypeConfigurationException {
		List<DonorCplxType> donorCplxTypes = goshcc.getDonorCplxType();
		for (DonorCplxType donorCplxType : donorCplxTypes) {
			if (donorCplxType.getDonationDetails()!=null&&
					donorCplxType.getDonationDetails().getDirectDebitDonationCplxType()!=null&&
					donorCplxType.getDonationDetails().getDirectDebitDonationCplxType().getDate1StPayment()!=null){
				XMLGregorianCalendar date1StPayment = donorCplxType.getDonationDetails().getDirectDebitDonationCplxType().getDate1StPayment();
				
				XMLGregorianCalendar nowPlusOneWeek = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
				nowPlusOneWeek.add(DatatypeFactory.newInstance().newDuration(true, 0, 0, 7, 0, 0, 0));
				if (date1StPayment.compare(nowPlusOneWeek)<=0){
					date1StPayment.add(DatatypeFactory.newInstance().newDuration(true, 0, 1, 0, 0, 0, 0));
				}
			}
		}
		
		return goshcc;
	}
}
