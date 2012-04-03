package org.gosh.validation.webcash;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageHeaders;
import org.springframework.mail.MailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;

/**
 * Splits a message into several fundraiser email messages. 
 */
public class FundraiserEmailSplitter {
	public static final String CONTACT_TO_MAIL_LINE = "contact2mailLine";
	
	private JavaMailSender mailSender;
	
	private Properties properties;
	
	@SuppressWarnings("unchecked")
	public List<MailMessage> split(Message<GOSHCC> message) throws MessagingException {
		List<MailMessage> emails = new ArrayList<MailMessage>();

		MessageHeaders headers = message.getHeaders();
		// Reprocessing routine
		MultiValueMap contactToMailLine = (MultiValueMap) headers.get(CONTACT_TO_MAIL_LINE);
		if (contactToMailLine != null && contactToMailLine.size()>0){
			Set<List<String>> keySet = contactToMailLine.keySet();
			for (List<String> contactEmail : keySet) {
				createMimeMessageAndAddToMailMessageList(getBodyFromMapping(contactEmail, contactToMailLine), contactEmail, emails);
			}
			return emails;
		} else {
			return new ArrayList<MailMessage>();
		}
	}

	/**
	 * @param emailBody
	 */
	private void createMimeMessageAndAddToMailMessageList(String emailBody, List<String> contactEmail, List<MailMessage> emails) {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			mimeMessage.setContent(emailBody, "text/html");
			MimeMailMessage mimeMailMessage = new MimeMailMessage(mimeMessage);
			
			String testEmailDestination = properties.getProperty("webcash.contact.email", null);
			if (StringUtils.isBlank(testEmailDestination)){
				mimeMailMessage.setTo((String[])contactEmail.toArray(new String[contactEmail.size()]));
			} else {
				mimeMailMessage.setTo(testEmailDestination);
			}

			mimeMailMessage.setSubject("Web Cash Donations Report");
			mimeMailMessage.setFrom("donotreply@gosh.org");
			
			emails.add(mimeMailMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private String getBodyFromMapping(List<String> contactEmail, MultiValueMap contactToMailLine) {
		String header = getHeader();
		String tableLines = "";
		Collection<String> mailLines = contactToMailLine.getCollection(contactEmail);
		if (mailLines != null){
			for (String mailLine : mailLines) {
				if (mailLine != null){
					tableLines += mailLine;
				}
			}
		}
		tableLines += "Recipients are: " + contactEmail + "\n";
		return header + tableLines;
	}

	private String getHeader() {
		String header = "<TABLE ><TBODY><TR><TD></TD></TR><TR><TD><U><B>Web Cash Donations Report Generated: " + new Date() + "</U></B></TD></TR></TBODY></TABLE>";
		
		header += "<TABLE><TBODY><TR><TD></TD></TR>" + 
			"<TR><TD></TD></TR><TR><TD></TD></TR>" + 
			"<TR><TD><B><U>TITLE</U></B></TD><TD><B><U>FIRST NAME</U></B></TD>" + 
			"<TD><B><U>LAST NAME</U></B></TD><TD><B><U>RE ID</U></B></TD>" + 
			"<TD><B><U>DATE</U></B></TD><TD><B><U>AMOUNT</U></B></TD>" + 
			"<TD><B><U>EMAIL</U></B></TD><TD><B><U>DATA FLAG</U></B></TD>" + 
			"<TD><B><U>GIFT AID</U></B></TD><TD><B><U>REASON</U></B></TD>" + 
			"<TD><B><U>APPEAL</U></B></TD><TD><B><U>CAMPAIGN</U></B></TD>" + 
			"<TD><B><U>DONATE DESCRIPTION</U></B></TD>";
		return header;
	}
	
	@Required
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	@Required
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
}
