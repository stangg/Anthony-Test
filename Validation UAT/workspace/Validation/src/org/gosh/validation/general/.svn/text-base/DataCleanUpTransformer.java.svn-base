package org.gosh.validation.general;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.validation.common.FileType;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageHeaders;

/**
 * @author gayathri.polavaram
 * This transformer cleans up any characters or data that may cause issues with BACS or any other processes.
 * Currently this transformer removes:
 * 1. Comma(,) and Hard Returns(\n) and tab characters (\t) from BankAccName element.
 * 2. Replaces &amp; with & from the BankName element.
 * 
 * All data cleaning activities must be housed in this transformer.
 */
public class DataCleanUpTransformer {

	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	private Reporter reporter;
	private Map<String, String> cleanUpCharacterMap = null;
	
	/** A map containing the regular expression pattern and character the pattern must replace*/
	private Map<Pattern, Character> patternMap;

	/**
	 * Constructors the patterns(regular expressions) to be replaced while cleaning up when the transformer instantiates
	 */
	public DataCleanUpTransformer() {
		buildPatterns();
		
		/* Instantiate the map and fill it up with default characters to clean and also their replacement*/
		cleanUpCharacterMap = new HashMap<String, String>(); 
		cleanUpCharacterMap.put("&amp;", "&");
		
	}
	
	private void buildPatterns() {
		patternMap = new HashMap<Pattern, Character>();
		
		Pattern tempPattern;

		// This pattern matches a single or more commas next to each other. Any spaces between the commas or on either sides will be ignored
		tempPattern = Pattern.compile("\\s*,+\\s*,*\\s*");
		patternMap.put(tempPattern, Character.valueOf(' ')); // Replace the above pattern by space
	 
		// This pattern matches a single or more tabs. any spaces or newlines on either sides are ignored
		tempPattern = Pattern.compile("\\s*\n*\\s*\t+\\s*\n*\\s*");
		patternMap.put(tempPattern, Character.valueOf(' ')); // Replace the above pattern by space
		
		//This pattern matches a single or more newlines. any spaces or tabs on either sides are ignored
		tempPattern = Pattern.compile("\\s*\t*\\s*\n+\\s*\t*\\s*");
		patternMap.put(tempPattern, Character.valueOf(' ')); // Replace the above pattern by space
		
	}
	
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) throws DatatypeConfigurationException{
		log.info("Message Transformation Started");
		
		GOSHCC payload = message.getPayload();
		
		// Clean all Comma and Hard Returns from BankAccName.
		// The BankAccName is available only in DD or DDUpgrade files. Call clean method only if the file type is one of the two. (DD Reactivate is not supported currently)
		MessageHeaders header = message.getHeaders();
		FileType fileType = (FileType) header.get(MessageHeaderName.FILE_TYPE.getName());
		if ((fileType == FileType.DD_TYPE) || (fileType == FileType.DD_UPGRADE_TYPE))  { // Add DD_REACT to the condition in future if/when we support it
			cleanBankDetails(payload);
		}
		
		
		log.info("Message Transformation Complete");
		return message;
	}
	
	/**
	 * Cleans the data of all the key entries in the cleanUpCharacterMap and replaces them with their corresponding values
	 * @param data
	 * @return
	 */
	public String cleanData(String data) {
		
		if (data != null ) {
			//clean the data up from each entry in patternMap
			Set<Entry<String, String>> entrySet = cleanUpCharacterMap.entrySet();
			
			for (Entry<String, String> entry : entrySet) {
				data = StringUtils.replace(data, entry.getKey(), entry.getValue());
			}
		}
		return data;
	}
	
	/**
	 * This method cleans up any Comma(,) and Hard Returns(\n) and tabs (\t) from BankAccName element from all DonorCplxType elements and replaces it with a single space
	 * @param payload the actual payload containing all DonorCplxType elements
	 * @return payload free of any Comma(,) and Hard Returns(\n)
	 */
	public GOSHCC cleanBankDetails(GOSHCC payload) {
		
		// Iterate through each DonorCplxType in the payload
		for (DonorCplxType donor : payload.getDonorCplxType()) {
			if (donor.getDonationDetails() != null && donor.getDonationDetails().getDirectDebitDonationCplxType() != null 
					&& donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType()!= null
					&& donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType().getBankAccName() != null) {
				DonorBankCplxType donorBankType = donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType();
				if (!StringUtils.isBlank(donorBankType.getBankAccName())) {
					// Set the new bank account name
					donorBankType.setBankAccName(cleanUpPatterns(donorBankType.getBankAccName()));
				}
				if (!StringUtils.isBlank(donorBankType.getBankName())) {
					// Set the new bank name
					donorBankType.setBankName(cleanData(donorBankType.getBankName()));
				}
			}
		}
		
		return payload;
	}
	
	public String cleanUpPatterns(String data) {
		if (data != null ) {
			//clean the data up from each entry in patternMap
			Set<Entry<Pattern, Character>> entrySet = patternMap.entrySet();
			
			for (Entry<Pattern, Character> entry : entrySet) {
				Matcher matcher = entry.getKey().matcher(data);
				// Replace the matching patterns with the replace-character
				data = matcher.replaceAll(""+entry.getValue());
			}
		}
		return data;
	}
	
	@Autowired @Required 
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
	
	public void setCleanUpMap(Map<String, String> map) {
		cleanUpCharacterMap = map;
	}

}
