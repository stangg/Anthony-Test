package org.gosh.validation.general.bank;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.validation.common.FileType;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageHeaders;

/**
 * This class is specific for fresh DDs. 
 *
 */
public class BankUpgradeTransformer {

	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	private Reporter reporter;
	private String errorMsg = "There are no bank details though it is a fresh DD and not an Upgrade. Also means that the BankAccountName is not present and could cause an issue with BACS";
	private BankUtilities utils = new BankUtilities();
	
	
	/**
	 * Checks if the file is of DD, returns if not. Checks is bank is not null, if so logs a message
	 */
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		log.info("Start Transformation");
		// Check if the header for the type of the file.
		MessageHeaders header = message.getHeaders();
		FileType fileType = (FileType) header.get(MessageHeaderName.FILE_TYPE.getName());
		if ((fileType != FileType.DD_TYPE))  { // Check if bank details are all present
			return message;
		}
		
		List<DonorCplxType> donorsList = message.getPayload().getDonorCplxType();
		List<DonorCplxType> errorList = new ArrayList<DonorCplxType>();
		for (DonorCplxType donor : donorsList) {
			if (utils.isBankNull(donor)){
				errorList.add(donor);
			} 
		}
		log.info("End of Transformation");
		return message = reporter.log(message, errorList, errorMsg);
	}
		
	@Required @Autowired
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
}
