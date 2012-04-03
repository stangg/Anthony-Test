package org.gosh.validation.webcash;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.PhoneEmail;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.message.MessageBuilder;

/**
 * Validates that the file is a web cash reprocessed file. 
 * 
 * If it is it creates a mapping between fundraising contacts and email lines (contact2mailLine)
 * The mapping gets transferred to the FundraiserEmailSplitter.
 * 
 * We also check that if the file is web cash and no mail lines were generated that it logs a message. 
 * Finally we check that if the file is not web cash it returns the message to the flow.
 * 
 * 10/11/2009: This class now generates the gift notes report only after the xml file has been validated. 
 * 
 * This transformer goes as the last step in the validation chain.
 * @author Maria.Urso
 */
public class WebCashFileReprocessingTransformer {
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	private Reporter reporter;
	private GiftNotesReportGenerator giftNotesReportGen	= new GiftNotesReportGenerator();

	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) {
		MultiValueMap contact2mailLine = new MultiValueMap();
		Boolean webCashBoolean = false;
		Boolean thereAreCashDs = false;
		
		GOSHCC payload = message.getPayload();
		
		// Build gift notes report before anything else
		if ((payload.getSupplierID()!= null) && (payload.getSupplierID().equals("Netbanx"))){
			List<String> notesReport = giftNotesReportGen.generateReport(payload);
			giftNotesReportGen.printFile(notesReport, "D:\\Validation\\WebCashReport\\giftNotes" + new Date().getTime() + ".csv");
		
			List<DonorCplxType> donorCplxTypes = payload.getDonorCplxType();
			int halfway = donorCplxTypes.size()/2;
			int i = halfway + 1;
			
				if (donorCplxTypes.get(i).getDonationDetails() != null && 
						donorCplxTypes.get(i).getDonationDetails().getCashDonationCplxType() != null ){
					thereAreCashDs = true;
					for (CashDonationCplxType cashDonation : donorCplxTypes.get(i).getDonationDetails().getCashDonationCplxType()) {
						if (cashDonation.getFundraisingContact().size()>0){
							webCashBoolean = true;
						}
					}
				}
			
			if (thereAreCashDs && webCashBoolean){
				for (DonorCplxType donor : donorCplxTypes) {
					constructWebCashEmailLine(donor, contact2mailLine);	
				}
			} else {
				return message;
			}
			
			if (webCashBoolean && contact2mailLine.size()==0){
					return reporter.log(message, "We expected to create email lines for this web cash reprocessed file but something went wrong.");
			}
				
			return MessageBuilder
			.fromMessage(message)
			.setHeader(FundraiserEmailSplitter.CONTACT_TO_MAIL_LINE, contact2mailLine)
			.build();
			
		} else {
			return message;
		}
	}

@SuppressWarnings("unused")
private boolean isFileNameForWebCash(Message<GOSHCC> message) {
	//Map<String, Object> headers = new HashMap<String, Object>();
	String filename = (String)message.getHeaders().get(FileHeaders.FILENAME);
	log.info("WEB CASH --- header is : " + filename);
	if (StringUtils.startsWithIgnoreCase(filename, "webdonations-")){
		return true;
	}
	return false;
}

	private void constructWebCashEmailLine(DonorCplxType donor,
			MultiValueMap contact2mailLine) {
		String tableLines = "";
		if (donor.getDonationDetails() != null && !donor.getDonationDetails().getCashDonationCplxType().isEmpty()){
			for (CashDonationCplxType cashDonation : donor.getDonationDetails().getCashDonationCplxType()) {
				String date = cashDonation.getDate()!=null?cashDonation.getDate().toString():"";
				
				tableLines += "<TR><TD></TD></TR>" + 
				"<TR><TD></TD></TR><TR><TD></TD></TR>" + 
				"<TR><TD>" + donor.getTitle1() + "</TD>" +
				"<TD>" + donor.getFirstName() + "</TD>" + 
				"<TD>" + donor.getLastName() + "</TD>" + 
				"<TD>" + donor.getConstituentID() + "</TD>" + 
				"<TD>" + StringUtils.substring(date, 0, 10) + "</TD>" + 
				"<TD>" + cashDonation.getAmount() + "</TD>" + 
				"<TD>" + getEmail(donor) + "</TD>" +
				"<TD>" + (donor.getTaxDeclaration() != null?"Y":"N") + "</TD>" +
				"<TD>" + "Internet Donations" + "</TD>" + 
				"<TD>" + cashDonation.getAppeal()+ "</TD>" +
				"<TD>" + cashDonation.getCampaign()+"</TD>" + 
				"<TD>" + cashDonation.getDonateDescription() + "</TD>"; 
				
				contact2mailLine.put(cashDonation.getFundraisingContact(), tableLines);
			}
		}
	}
	
	private String getEmail(DonorCplxType donor) {
		List<PhoneEmail> phoneEmails = donor.getPhoneEmail();
		for (PhoneEmail phoneEmail : phoneEmails) {
			if("email".equals(phoneEmail.getType())){
				return phoneEmail.getValue();
			}
		}
		return null;
	}
	
	@Autowired
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}	
}
