package org.gosh.convertion.webdonations;

import static java.util.Collections.*;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import junit.framework.TestCase;
import org.apache.commons.collections.map.MultiValueMap;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.validation.webcash.FundraiserEmailSplitter;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;
import org.springframework.mail.MailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import org.springframework.integration.message.MessageBuilder;

public class FundraiserEmailerTest extends TestCase {
	public void testNullCase() throws Exception {
		FundraiserEmailSplitter splitter = new FundraiserEmailSplitter();
		MultiValueMap contact2mailLine = new MultiValueMap();
		
		DonorCplxType reallySimpleModel = new GOSHCC.DonorCplxType();
		GOSHCC.DonorCplxType.DonationDetails donationDetails = new GOSHCC.DonorCplxType.DonationDetails();
		reallySimpleModel.setDonationDetails(donationDetails);
		donationDetails.getCashDonationCplxType().add(new GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType());

		contact2mailLine.put("maria.urso@gosh.org", "<TR><TD>SomeMailLine</TD></TR>");
		
		HashMap<String, Object> headers = new HashMap<String, Object>();
		GenericMessage<GOSHCC> genericMessage = new GenericMessage<GOSHCC>(new GOSHCC(), headers);
		assertTrue(splitter.split(genericMessage).isEmpty());
	}
	
	public void testEmailsCase() throws Exception {
		FundraiserEmailSplitter splitter = new FundraiserEmailSplitter();
		splitter.setProperties(new Properties());
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		splitter.setMailSender(mailSender);
		
		MultiValueMap contact2mailLine = new MultiValueMap();
		
		DonorCplxType reallySimpleModel = new GOSHCC.DonorCplxType();
		GOSHCC.DonorCplxType.DonationDetails donationDetails = new GOSHCC.DonorCplxType.DonationDetails();
		reallySimpleModel.setDonationDetails(donationDetails);
		donationDetails.getCashDonationCplxType().add(new GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType());
		
		contact2mailLine.put(singletonList("maria.urso@gosh.org"), "<TR><TD>SomeMailLine</TD></TR>");
		
		HashMap<String, Object> headers = new HashMap<String, Object>();
		headers.put(FundraiserEmailSplitter.CONTACT_TO_MAIL_LINE, contact2mailLine);
		
		GenericMessage<GOSHCC> genericMessage = new GenericMessage<GOSHCC>(new GOSHCC(), headers);
		assertEquals(1, splitter.split(genericMessage).size());
	}
	
	public void testMultipleEmailsCase() throws Exception {
		FundraiserEmailSplitter splitter = new FundraiserEmailSplitter();
		splitter.setProperties(new Properties());
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		splitter.setMailSender(mailSender);
		
		MultiValueMap contact2mailLine = new MultiValueMap();
		
		DonorCplxType reallySimpleModel = new GOSHCC.DonorCplxType();
		GOSHCC.DonorCplxType.DonationDetails donationDetails = new GOSHCC.DonorCplxType.DonationDetails();
		reallySimpleModel.setDonationDetails(donationDetails);
		donationDetails.getCashDonationCplxType().add(new GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType());
		
		contact2mailLine.put(singletonList("maria.urso@gosh.org"), "<TR><TD>SomeMailLine</TD></TR>");
		contact2mailLine.put(singletonList("maria.urso@gosh.org"), "<TR><TD>SomeOtherMailLine</TD></TR>");
		contact2mailLine.put(singletonList("maria.urso@gosh.org"), "<TR><TD>differentMailLine</TD></TR>");
		contact2mailLine.put(singletonList("kevin.savage@gosh.org"), "<TR><TD>SomeMailLine</TD></TR>");
		
		HashMap<String, Object> headers = new HashMap<String, Object>();
		headers.put(FundraiserEmailSplitter.CONTACT_TO_MAIL_LINE, contact2mailLine);
		
		GenericMessage<GOSHCC> genericMessage = new GenericMessage<GOSHCC>(new GOSHCC(), headers);
		assertEquals(2, splitter.split(genericMessage).size());
	}
	
	public void testBrokenCase() throws Exception {
		MultiValueMap contact2mailLine = new MultiValueMap();
		// this email address is ok, because these emails don't get sent.
		contact2mailLine.put(singletonList("supporter.care@gosh.org"), "<TR><TD></TD></TR><TR><TD></TD></TR><TR><TD></TD></TR><TR><TD>Mr</TD><TD>James</TD><TD>Davies</TD><TD>null</TD><TD>2008-07-04</TD><TD>50.00</TD><TD>null</TD><TD>Y</TD><TD>Internet Donations</TD><TD>DMC0910A</TD><TD>DM09</TD><TD></TD>");
		contact2mailLine.put(singletonList("supporter.care@gosh.org"), "<TR><TD></TD></TR><TR><TD></TD></TR><TR><TD></TD></TR><TR><TD>Mr</TD><TD>ricky</TD><TD>wells</TD><TD>40491991</TD><TD>2008-07-11</TD><TD>30.00</TD><TD>null</TD><TD>Y</TD><TD>Internet Donations</TD><TD>DMC0110E</TD><TD>DM01</TD><TD></TD>");
		
		FundraiserEmailSplitter splitter = new FundraiserEmailSplitter();
		splitter.setProperties(new Properties());
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		splitter.setMailSender(mailSender);
		Message<GOSHCC> message = new GenericMessage<GOSHCC>(new GOSHCC());
		message = MessageBuilder.fromMessage(message).setHeader("contact2mailLine", contact2mailLine).build();
		List<MailMessage> split = splitter.split(message);
		assertEquals(1, split.size());
	}
}
