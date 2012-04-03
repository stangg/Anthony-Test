/**
 * 
 */
package org.gosh.validation.general;

import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.validation.common.FileType;
import org.gosh.validation.common.MessageHeaderName;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

/**
 * @author gayathri.polavaram
 *
 */
public class DataCleanUpTransformerTest extends TestCase {

	private DataCleanUpTransformer classToTest = null;

	protected void setUp() throws Exception {
		super.setUp();
		classToTest = new DataCleanUpTransformer();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	/**
	 * Test method for {@link org.gosh.validation.general.DataCleanUpTransformer#transform(org.springframework.integration.core.Message)}.
	 * Case 1: When the file contains ',' to be cleaned up, but it does not contain a header.
	 * The file must not be cleaned up
	 */
	public void testTransform1() {
		try {
			GOSHCC goshcc = new GOSHCC();
			
			DonorCplxType donorCplxType = new DonorCplxType();
			DonationDetails donationDetails = new DonationDetails();
			DirectDebitDonationCplxType ddDonation = new DirectDebitDonationCplxType();
			DonorBankCplxType donorBankType = new DonorBankCplxType();
			
			donorBankType.setBankAccName("The name contains (,) to be removed");
			ddDonation.setDonorBankCplxType(donorBankType);
			donationDetails.setDirectDebitDonationCplxType(ddDonation);
			donorCplxType.setDonationDetails(donationDetails);
			goshcc.getDonorCplxType().add(donorCplxType);
	
			final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			String bankAccName = transformedMsg.getPayload().getDonorCplxType().get(0).
										getDonationDetails().getDirectDebitDonationCplxType().
										getDonorBankCplxType().getBankAccName();
			
			assertEquals("The message is cleaned up, header logic not working", "The name contains (,) to be removed", bankAccName);
			
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		}
		
	}
	
	/**
	 * Test method for {@link org.gosh.validation.general.DataCleanUpTransformer#transform(org.springframework.integration.core.Message)}.
	 * Case 1: When the file contains ',' to be cleaned up also contains file type header as Cash
	 * The file must not be cleaned up
	 */
	public void testTransform2() {
		try {
			
			// Construct Payload
			GOSHCC goshcc = new GOSHCC();
			DonorCplxType donorCplxType = new DonorCplxType();
			DonationDetails donationDetails = new DonationDetails();
			DirectDebitDonationCplxType ddDonation = new DirectDebitDonationCplxType();
			DonorBankCplxType donorBankType = new DonorBankCplxType();
			
			donorBankType.setBankAccName("The name contains (,) to be removed");
			ddDonation.setDonorBankCplxType(donorBankType);
			donationDetails.setDirectDebitDonationCplxType(ddDonation);
			donorCplxType.setDonationDetails(donationDetails);
			goshcc.getDonorCplxType().add(donorCplxType);
	
			// Construct header
			Map<String,Object> header = new HashMap<String,Object>();
			header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.MIXED_TYPE);
			
			// Construct the message
			final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			String bankAccName = transformedMsg.getPayload().getDonorCplxType().get(0).
										getDonationDetails().getDirectDebitDonationCplxType().
										getDonorBankCplxType().getBankAccName();
			
			assertEquals("The message is cleaned up, header logic not working", "The name contains (,) to be removed", bankAccName);
			
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		}
		
	}
	
	/**
	 * Test method for {@link org.gosh.validation.general.DataCleanUpTransformer#transform(org.springframework.integration.core.Message)}.
	 * Case 1: When the file contains ',' to be cleaned up also contains file type header as DD
	 * The file must be cleaned up
	 */
	public void testTransform3() {
		try {
			
			// Construct Payload
			GOSHCC goshcc = new GOSHCC();
			DonorCplxType donorCplxType = new DonorCplxType();
			DonationDetails donationDetails = new DonationDetails();
			DirectDebitDonationCplxType ddDonation = new DirectDebitDonationCplxType();
			DonorBankCplxType donorBankType = new DonorBankCplxType();
			
			donorBankType.setBankAccName("The name contains (,) to be removed");
			ddDonation.setDonorBankCplxType(donorBankType);
			donationDetails.setDirectDebitDonationCplxType(ddDonation);
			donorCplxType.setDonationDetails(donationDetails);
			goshcc.getDonorCplxType().add(donorCplxType);
	
			// Construct header
			Map<String,Object> header = new HashMap<String,Object>();
			header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_TYPE);
			
			// Construct the message
			final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			String bankAccName = transformedMsg.getPayload().getDonorCplxType().get(0).
										getDonationDetails().getDirectDebitDonationCplxType().
										getDonorBankCplxType().getBankAccName();
			
			assertEquals("The message is not cleaned up", "The name contains ( ) to be removed", bankAccName);
			
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		}
		
	}
	

	/**
	 * Test method for {@link org.gosh.validation.general.DataCleanUpTransformer#cleanCommaAndHardReturns(org.gosh.re.dmcash.bindings.GOSHCC)}.
	 * Test if the clean up is successful
	 */
	public void testCleanCommaAndHardReturns() {
		
		// Construct Payload
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		DonationDetails donationDetails = new DonationDetails();
		DirectDebitDonationCplxType ddDonation = new DirectDebitDonationCplxType();
		DonorBankCplxType donorBankType = new DonorBankCplxType();
		
		donorBankType.setBankAccName("The name contains (,) to be removed");
		ddDonation.setDonorBankCplxType(donorBankType);
		donationDetails.setDirectDebitDonationCplxType(ddDonation);
		donorCplxType.setDonationDetails(donationDetails);
		goshcc.getDonorCplxType().add(donorCplxType);
		
		goshcc = classToTest.cleanBankDetails(goshcc);
		
		String bankAccName = goshcc.getDonorCplxType().get(0).
		getDonationDetails().getDirectDebitDonationCplxType().
		getDonorBankCplxType().getBankAccName();

		assertEquals("The message is cleaned up", "The name contains ( ) to be removed", bankAccName);
	}
	
	/**
	 * Test method for {@link org.gosh.validation.general.DataCleanUpTransformer#cleanCommaAndHardReturns(org.gosh.re.dmcash.bindings.GOSHCC)}.
	 * Test behavior if BankAccName is null
	 */
	public void testCleanCommaAndHardReturns1() {
		
		// Construct Payload
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		DonationDetails donationDetails = new DonationDetails();
		DirectDebitDonationCplxType ddDonation = new DirectDebitDonationCplxType();
		DonorBankCplxType donorBankType = new DonorBankCplxType();
		
		ddDonation.setDonorBankCplxType(donorBankType);
		donationDetails.setDirectDebitDonationCplxType(ddDonation);
		donorCplxType.setDonationDetails(donationDetails);
		goshcc.getDonorCplxType().add(donorCplxType);

		goshcc = classToTest.cleanBankDetails(goshcc);

	}
	

	/**
	 * Test method for {@link org.gosh.validation.general.DataCleanUpTransformer#cleanUp(java.lang.String)}.
	 * Test if the clean up for (,) is successful
	 */
	public void testCleanUp() {
		String result = classToTest.cleanUpPatterns("Clean (,)");
		assertEquals("Comma not cleaned", "Clean ( )", result);
	}
	
	/**
	 * Test method for {@link org.gosh.validation.general.DataCleanUpTransformer#cleanUp(java.lang.String)}.
	 * Test if the clean up for (\n) is successful
	 */
	public void testCleanUp2() {
		String result = classToTest.cleanUpPatterns("Clean (" +'\n'+ ")");
		assertEquals("Comma not cleaned", "Clean ( )", result);
	}
	
	/**
	 * Test method for {@link org.gosh.validation.general.DataCleanUpTransformer#cleanUp(java.lang.String)}.
	 * Test if data is null
	 */
	public void testCleanUp3() {
		String result = classToTest.cleanUpPatterns(null);
		assertNull(result);
	}
	
	/**
	 * Test method for {@link org.gosh.validation.general.DataCleanUpTransformer#cleanUp(java.lang.String)}.
	 * Test is tabs, spaces and \n
	 */
	public void testCleanUp4() {
		String result = classToTest.cleanUpPatterns("						" + '\n' + "	   ");
		assertEquals(" " ,result);
	}
	
	/**
	 * Test method for {@link org.gosh.validation.general.DataCleanUpTransformer#cleanUp(java.lang.String)}.
	 * Test is tabs, commas, spaces and \n
	 */
	public void testCleanUp5() {
		String result = classToTest.cleanUpPatterns('\n' +"						" + '\n' + "	,   " + '\n');
		assertEquals(" " ,result);
	}
	
	// different patterns
	public void testCleanUp6() {
		String result = classToTest.cleanUpPatterns(
				"Spaces       "
				+ "\t"
				+ "Actual String"
				+ "     "
				+ '\n'
				+ "  "
				+ "\t "
				+ "Some text"
				);
		assertEquals("Spaces Actual String Some text" ,result);
	}

	
	public void testCleanUp7() {
		String result = classToTest.cleanUpPatterns("[, ,,  , , ]");
		assertEquals("[  ]", result);
	}
	
	public void testCleanData() {
		String  result = classToTest.cleanData("Hawes &amp; curtis");
		assertEquals("Hawes & curtis", result);
	}
	
	public void testCleanData1 () {
		String bankName = "ADAM &amp; COMPANY PLC";
		
		assertEquals("Cleanup not working", "ADAM & COMPANY PLC", classToTest.cleanData(bankName));
		
		bankName = "&amp;ADAM &amp; COMPANY PLC&amp;";
		assertEquals("Cleanup not working", "&ADAM & COMPANY PLC&", classToTest.cleanData(bankName));
		
		bankName = "ADAM &amp;&amp COMPANY PLC";
		assertEquals("Cleanup not working", "ADAM &&amp COMPANY PLC", classToTest.cleanData(bankName));
		
		bankName = "ADAamp;M COMPANY&amp; PLC";
		assertEquals("Cleanup not working", "ADAamp;M COMPANY& PLC", classToTest.cleanData(bankName));
		
		bankName = "&&";
		assertEquals("Cleanup not working", "&&", classToTest.cleanData(bankName));
		
		bankName = "&&&&&amp;";
		assertEquals("Cleanup not working", "&&&&&", classToTest.cleanData(bankName));
		
		bankName = "&amp;";
		assertEquals("Cleanup not working", "&", classToTest.cleanData(bankName));
	}
}
