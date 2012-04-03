package org.gosh.validation.error;

import java.util.ArrayList;
import java.util.Collections;

import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.error.ErrorReporter;
import org.gosh.validation.general.error.Reporter;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

public class ErrorReporterTest extends TestCase {
	@SuppressWarnings({ "serial", "unchecked" })
	public void testLogRetainsWithDuplicateKeys() {
		Reporter errorReporter = new ErrorReporter();
		Message<String> message = new GenericMessage<String>("payload");
		message = errorReporter.log(message, "testMessage");
		message = errorReporter.log(message, "testMessage2");
		message = errorReporter.log(message, "testMessage3");
		
		ArrayList<String> listOfErrorMessages = (ArrayList<String>) message.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		
		assertEquals(
				new ArrayList<String>(){{add("testMessage");add("testMessage2");add("testMessage3");}}, 
				listOfErrorMessages
			);
	}
	
	
	@SuppressWarnings({ "serial", "unchecked" })
	public void testLogRetainsWithDuplicateKeysAndMultipleErrors() {
		Reporter errorReporter = new ErrorReporter();
		Message<String> message = new GenericMessage<String>("payload");
		message = errorReporter.log(message, "testMessage");
		
		ArrayList<String> errorList = new ArrayList<String>();
		errorList.add("testMessage2");
		errorList.add("testMessage3");
		errorReporter.log(message, errorList);
		
		ArrayList<String> listOfErrorMessages = (ArrayList<String>) message.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		
		assertEquals(
				new ArrayList<String>(){{add("testMessage");add("testMessage2");add("testMessage3");}}, 
				listOfErrorMessages
			);
	}
	
	public void testLogInfo() {
		Reporter errorReporter = new ErrorReporter();
		Message<String> message = new GenericMessage<String>("payload");
		message = errorReporter.logInfo(message, Collections.singletonList("testMessage"));

		assertEquals(
			Collections.singletonList("testMessage"), 
			message.getHeaders().get(MessageHeaderName.INFO_HEADER.getName())
		);
	}
	
	public void testWithDonor() {
		Reporter errorReporter = new ErrorReporter();
		Message<String> message = new GenericMessage<String>("payload");
		
		DonorCplxType donor = getDonor();
		
		message = errorReporter.log(message, donor, "testMessage");

		assertEquals(
			Collections.singletonList("There was an error for the following donors: [FirstName LastName of PostCode] : testMessage"), 
			message.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())
		);
	}
	
	
	public void testWithManyDonors() {
		Reporter errorReporter = new ErrorReporter();
		Message<String> message = new GenericMessage<String>("payload");
		
		ArrayList<DonorCplxType> arrayList = new ArrayList<DonorCplxType>();
		arrayList.add(getDonor());
		arrayList.add(getDonor());
		
		message = errorReporter.log(message, arrayList, "testMessage");

		assertEquals(
			Collections.singletonList("There was an error for the following donors: [FirstName LastName of PostCode] [FirstName LastName of PostCode] : testMessage"), 
			message.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())
		);
	}


	private DonorCplxType getDonor() {
		DonorCplxType donor = new DonorCplxType();
		donor.setFirstName("FirstName");
		donor.setLastName("LastName");
		donor.setPostCode("PostCode");
		return donor;
	}
}
