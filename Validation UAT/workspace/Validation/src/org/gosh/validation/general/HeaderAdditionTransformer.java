/**
 * 
 */
package org.gosh.validation.general;

import java.util.ArrayList;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.validation.common.FileType;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

/**
 * This class is used to add information to the header of the message. The following types of information is to be added in the headers:
 * -> The information that is required to fork the message/process flow in other word, 
 * the decision making information must be added inside the header. For example, if the xml file is for DD Upgrade.
 * -> The information in the XML that is accessed frequently is also added in the header.
 * -> The information discovered in the chain of transformers that are required for further processing but not required in the XML file. For example BankID
 * Note that all the above types information may not be added in the same transformer, but may be added along the chain of transformations.
 * 
 * Assumptions:
 * 1. A file will contain the same type of data, either tributes or dd upgrades or dd or cash and so on.
 * 
 * 
 * @author gayathri.polavaram  
 */
public class HeaderAdditionTransformer {

	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	private Reporter reporter;
	private final String noFileTypeMsg = "Unable to determine the file type";

	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) throws DatatypeConfigurationException {
		log.info("Message Transformation Started");
		
		message = addFileTypeHeader(message);
		
		if (message.getHeaders().get(MessageHeaderName.FILE_TYPE.getName()) == null) {
			ArrayList<String> errorList = new ArrayList<String>();
			errorList.add(noFileTypeMsg);
			message = reporter.log(message, errorList); 
		}
		
		log.info("Message Transformation Complete");
		return message;
		
	}
	
	/**
	 * This is a corroborate method to add a FileType header to the given message.
	 * Different types of files currently supported and discovery criteria for each type are described below:
	 * 1. TRIBUTE_TYPE: If the file contains a tribute element.
	 * 2. TRIBUTE_IN_GIFT_TYPE: If the CashDonationCplxType contains tributeID element.
	 * 3. CASH_WITH_GIFT_TYPE: If the CashDonationCplxType contains giftAttributes elements (for example: XMAS campaign)
	 * 4. CASH_TYPE: Non null CashDonationCplxType without tributeID or giftAttributes.
	 * 5. DD_TYPE: With no PDDUpgradeFlag or PDDUpgradeFlag set to false.
	 * 6. DD_UPGRADE_TYPE: PDDUpgradeFlag set to true.
	 * 7. PROSPECT_TYPE: DonationDetails missing.
	 * If none of the above is met, then an error is logged stating the inability to determine the FileType.
	 * @param message
	 * @return
	 */
	public Message<GOSHCC> addFileTypeHeader(Message<GOSHCC> message) {
		
		GOSHCC payload = message.getPayload();
		// Check if it is a tribute, first check for tribute element
		if(!payload.getTribute().isEmpty()) {
			// It is a tribute file add header and return
			message = MessageBuilder
			.fromMessage(message)
			.setHeader(MessageHeaderName.FILE_TYPE.getName(), FileType.TRIBUTE_TYPE)
			.build();
			return message;
		}
		
		// Tribute element is not found, browse through donor complex type
		for (DonorCplxType donor : payload.getDonorCplxType()) {
			if (donor.getDonationDetails()!= null) {
				if (!donor.getDonationDetails().getCashDonationCplxType().isEmpty()) {
					// Check if it's a tribute or cash
					for (CashDonationCplxType cashDonation : donor.getDonationDetails().getCashDonationCplxType()) {
						if (StringUtils.isNotBlank(cashDonation.getTributeID())){
							return MessageBuilder
							.fromMessage(message)
							.setHeader(MessageHeaderName.FILE_TYPE.getName(), FileType.TRIBUTE_IN_GIFT_TYPE)
							.build();
						} else if(!cashDonation.getGiftAttributes().isEmpty()) {
							return MessageBuilder
							.fromMessage(message)
							.setHeader(MessageHeaderName.FILE_TYPE.getName(), FileType.CASH_WITH_GIFT_TYPE)
							.build();
						}
						return MessageBuilder
						.fromMessage(message)
						.setHeader(MessageHeaderName.FILE_TYPE.getName(), FileType.CASH_TYPE)
						.build();
					}
				} else if (donor.getDonationDetails().getDirectDebitDonationCplxType() != null) {
					if (donor.getDonationDetails().getDirectDebitDonationCplxType().getPDDUpgradeFlag() == null ||
							!donor.getDonationDetails().getDirectDebitDonationCplxType().getPDDUpgradeFlag().isPDDUpgradeFlag()){
						return MessageBuilder
						.fromMessage(message)
						.setHeader(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_TYPE)
						.build();
					} else {
						return MessageBuilder
						.fromMessage(message)
						.setHeader(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_UPGRADE_TYPE)
						.build();
					}
				}
				
			} else {
				// Means donation details are null, so it is a prospective donor
				return MessageBuilder
				.fromMessage(message)
				.setHeader(MessageHeaderName.FILE_TYPE.getName(), FileType.PROSPECT_TYPE)
				.build();
			}
		}
		return message;
	}
	
	@Autowired @Required 
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
}
