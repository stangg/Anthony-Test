/**
 * 
 */
package org.gosh.validation.general.bank;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.validation.common.FileType;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.error.ErrorReporter;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

/**
 * @author gayathri.polavaram
 *
 */
public class GenericBankValidationTransformerTest extends TestCase {
	
	private GenericBankValidationTransformer classToTest = null;
	private Map<String, Object> header = new HashMap<String, Object>();
	private BankDataAccessMock mockdao;
	
	protected void setUp() throws Exception {
		super.setUp();
		classToTest = new GenericBankValidationTransformer();
		classToTest.setReporter(new ErrorReporter());
		
		mockdao = new BankDataAccessMock(new SQLServerDataSource());
		classToTest.setDao(mockdao);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	/**
	 * Test method for {@link org.gosh.validation.general.bank.GenericBankValidationTransformer#transform(org.springframework.integration.core.Message)}.
	 */
	// Test case to check if the account number has enough digits with file type set as DD
	public void testTransformer4AccountNumber() throws Exception {
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankAccNo("1234567");
		bank.setBankAccName("Test Name");
		bank.setBankSort("45-12-34");
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setFirstName("TesterFirst");
		donorCplxType.setLastName("TesterLast");
		donorCplxType.setPostCode("CR0 6RT");
		
		DonationDetails donationDetails = new DonationDetails();
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		directDebitDonationCplxType.setDonorBankCplxType(bank);
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		donorCplxType.setDonationDetails(donationDetails);
		goshcc.getDonorCplxType().add(donorCplxType);

		header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_TYPE);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
		Message<GOSHCC> transformedMessage = classToTest.transform(message);
		
		ArrayList<String> errorArray = (ArrayList<String>)transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		
		assertNotNull("No errors logged, 1 error expected", errorArray);
		
		assertEquals("The number of expected errors dont match", 2,  errorArray.size());
		
		String expected = "There was an error for the following donors: [TesterFirst TesterLast of CR0 6RT] : The Bank Account Number is short by at least one digit";
		assertEquals("Error messages dont match", expected, errorArray.get(0));
	}
	
	// Test case to check if the account number has enough digits with file type set as DDU
	public void testTransformer4AccountNumber1() throws Exception {
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankAccNo("1");
		bank.setBankAccName("Test Name");
		bank.setBankSort("45-12-34");
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setFirstName("TesterFirst");
		donorCplxType.setLastName("TesterLast");
		donorCplxType.setPostCode("CR0 6RT");
		
		DonationDetails donationDetails = new DonationDetails();
		
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		directDebitDonationCplxType.setDonorBankCplxType(bank);
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		donorCplxType.setDonationDetails(donationDetails);
		goshcc.getDonorCplxType().add(donorCplxType);

		header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_UPGRADE_TYPE);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
		Message<GOSHCC> transformedMessage = classToTest.transform(message);
		
		ArrayList<String> errorArray = (ArrayList<String>)transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		
		assertNotNull("No errors logged, 1 error expected", errorArray);
		
		assertEquals("The number of expected errors dont match", 2,  errorArray.size());
		
		String expected = "There was an error for the following donors: [TesterFirst TesterLast of CR0 6RT] : The number of digits in the Bank Account Number are fewer than expected";
		assertEquals("Error messages dont match", expected, errorArray.get(0));

	}
	
	// Test case to check if the account number has enough digits even if we pu spaces to make up valid digits, with file type set as DDU
	public void testTransformer4AccountNumber2() throws Exception {
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankAccNo("              1               ");
		bank.setBankAccName("Test Name");
		bank.setBankSort("45-12-34");
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setFirstName("TesterFirst");
		donorCplxType.setLastName("TesterLast");
		donorCplxType.setPostCode("CR0 6RT");
		
		DonationDetails donationDetails = new DonationDetails();
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		directDebitDonationCplxType.setDonorBankCplxType(bank);
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		donorCplxType.setDonationDetails(donationDetails);
		goshcc.getDonorCplxType().add(donorCplxType);

		header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_UPGRADE_TYPE);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
		Message<GOSHCC> transformedMessage = classToTest.transform(message);
		
		ArrayList<String> errorArray = (ArrayList<String>)transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		
		assertNotNull("No errors logged, 1 error expected", errorArray);
		
		assertEquals("The number of expected errors dont match", 2,  errorArray.size());
		
		String expected = "There was an error for the following donors: [TesterFirst TesterLast of CR0 6RT] : The number of digits in the Bank Account Number are fewer than expected";
		assertEquals("Error messages dont match", expected, errorArray.get(0));

	}
	
	// Test case to check if the account number has enough digits with filetype not set
	public void testTransformer4InvalidFileType() throws Exception {
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankAccNo("1");
		bank.setBankAccName("Test Name");
		bank.setBankSort("45-12-34");
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setFirstName("TesterFirst");
		donorCplxType.setLastName("TesterLast");
		donorCplxType.setPostCode("CR0 6RT");
		
		DonationDetails donationDetails = new DonationDetails();
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		directDebitDonationCplxType.setDonorBankCplxType(bank);
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		donorCplxType.setDonationDetails(donationDetails);
		goshcc.getDonorCplxType().add(donorCplxType);

		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
		Message<GOSHCC> transformedMessage = classToTest.transform(message);
		
		ArrayList<String> errorArray = (ArrayList<String>)transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		assertNull("No errors expected", errorArray);
		
	}
	
	// Test case to check if the account number has enough digits with filetype set to cash
	public void testTransformer4InvalidFileType2() throws Exception {
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankAccNo("1");
		bank.setBankAccName("Test Name");
		bank.setBankSort("45-12-34");
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setFirstName("TesterFirst");
		donorCplxType.setLastName("TesterLast");
		donorCplxType.setPostCode("CR0 6RT");
		
		DonationDetails donationDetails = new DonationDetails();
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		directDebitDonationCplxType.setDonorBankCplxType(bank);
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		donorCplxType.setDonationDetails(donationDetails);
		goshcc.getDonorCplxType().add(donorCplxType);

		header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.CASH_TYPE);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
		Message<GOSHCC> transformedMessage = classToTest.transform(message);
		
		ArrayList<String> errorArray = (ArrayList<String>)transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		assertNull("No errors expected", errorArray);
		
	}
	
	// Test case to check if the account number has enough digits with filetype set to cash
	public void testTransformerBlankAccNo() throws Exception {
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankAccNo("");
		bank.setBankAccName("Test Name");
		bank.setBankSort("45-12-34");
		
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setFirstName("TesterFirst");
		donorCplxType.setLastName("TesterLast");
		donorCplxType.setPostCode("CR0 6RT");
		
		DonationDetails donationDetails = new DonationDetails();
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		directDebitDonationCplxType.setDonorBankCplxType(bank);
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		donorCplxType.setDonationDetails(donationDetails);
		goshcc.getDonorCplxType().add(donorCplxType);

		header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_TYPE);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
		Message<GOSHCC> transformedMessage = classToTest.transform(message);
		
		ArrayList<String> errorArray = (ArrayList<String>)transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		
		assertNotNull("No errors logged, 1 error expected", errorArray);
		
		assertEquals("The number of expected errors dont match", 1,  errorArray.size());
		
		String expected = "There was an error for the following donors: [TesterFirst TesterLast of CR0 6RT] : Either Account Number OR Sort Code is empty and could cause and issue in BACS";
		assertEquals("Error messages dont match", expected, errorArray.get(0));
		
	}
	
	// Test case to check if the account number has enough digits and blank sortcode with filetype set to cash
	public void testTransformerBlankSort() throws Exception {
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankAccNo("78908");
		bank.setBankAccName("Test Name");
		bank.setBankSort("  ");
		
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setFirstName("TesterFirst");
		donorCplxType.setLastName("TesterLast");
		donorCplxType.setPostCode("CR0 6RT");
		
		DonationDetails donationDetails = new DonationDetails();
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		directDebitDonationCplxType.setDonorBankCplxType(bank);
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		donorCplxType.setDonationDetails(donationDetails);
		goshcc.getDonorCplxType().add(donorCplxType);

		header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_TYPE);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
		Message<GOSHCC> transformedMessage = classToTest.transform(message);
		
		ArrayList<String> errorArray = (ArrayList<String>)transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		
		assertNotNull("No errors logged, 2 errors expected", errorArray);
		
		assertEquals("The number of expected errors dont match", 2,  errorArray.size());
		
		String expected = "There was an error for the following donors: [TesterFirst TesterLast of CR0 6RT] : Either Account Number OR Sort Code is empty and could cause and issue in BACS";
		assertEquals("Error messages dont match", expected, errorArray.get(0));
		
		expected = "There was an error for the following donors: [TesterFirst TesterLast of CR0 6RT] : The number of digits in the Bank Account Number are fewer than expected";
		assertEquals("Error messages dont match", expected, errorArray.get(1));
		
	}
	
	// Test case to check if the account number has enough digits and blank sortcode with filetype set to cash
	public void testTransformerBlankAccName() throws Exception {
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankAccNo("78908766");
		bank.setBankAccName("    ");
		bank.setBankSort("7691382193");
		
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setFirstName("TesterFirst");
		donorCplxType.setLastName("TesterLast");
		donorCplxType.setPostCode("CR0 6RT");
		
		DonationDetails donationDetails = new DonationDetails();
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		directDebitDonationCplxType.setDonorBankCplxType(bank);
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		donorCplxType.setDonationDetails(donationDetails);
		goshcc.getDonorCplxType().add(donorCplxType);

		header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_TYPE);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
		Message<GOSHCC> transformedMessage = classToTest.transform(message);
		
		ArrayList<String> errorArray = (ArrayList<String>)transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		
		assertNotNull("No errors logged, 1 errors expected", errorArray);
		
		assertEquals("The number of expected errors dont match", 2,  errorArray.size());
		
		String expected = "There was an error for the following donors: [TesterFirst TesterLast of CR0 6RT] : The Account Name is empty and could cause an issue in BACS";
		assertEquals("Error messages dont match", expected, errorArray.get(0));
		
	}

	// Test case to check if the account number has enough digits and blank sortcode with filetype set to cash
	public void testTransformerNullAccName() throws Exception {
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankAccNo("78908766");
		bank.setBankAccName("    ");
		bank.setBankSort("7691382193");
		
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setFirstName("TesterFirst");
		donorCplxType.setLastName("TesterLast");
		donorCplxType.setPostCode("CR0 6RT");
		
		DonationDetails donationDetails = new DonationDetails();
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		directDebitDonationCplxType.setDonorBankCplxType(bank);
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		donorCplxType.setDonationDetails(donationDetails);
		goshcc.getDonorCplxType().add(donorCplxType);

		header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_TYPE);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
		Message<GOSHCC> transformedMessage = classToTest.transform(message);
		
		ArrayList<String> errorArray = (ArrayList<String>)transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		
		assertNotNull("No errors logged, 1 errors expected", errorArray);
		
		assertEquals("The number of expected errors dont match", 2,  errorArray.size());
		
		String expected = "There was an error for the following donors: [TesterFirst TesterLast of CR0 6RT] : The Account Name is empty and could cause an issue in BACS";
		assertEquals("Error messages dont match", expected, errorArray.get(0));
		
	}
	
	// Case 1 when bankID is returned by the dao
	public void testSetBankID() throws Exception {
		
		List<String> bankIds = new ArrayList<String>();
		bankIds.add("7865564");
		mockdao.bankIDs = bankIds;
		
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankAccNo("78908766");
		bank.setBankAccName("TesterFirst");
		bank.setBankSort("7691382193");
		bank.setBankName("Any Bank");
		bank.setBankSort("an-ys-or");
		bank.setBranchName("Buckingham palace");
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setFirstName("TesterFirst");
		donorCplxType.setLastName("TesterLast");
		donorCplxType.setPostCode("CR0 6RT");
		
		DonationDetails donationDetails = new DonationDetails();
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		directDebitDonationCplxType.setDonorBankCplxType(bank);
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		donorCplxType.setDonationDetails(donationDetails);
		goshcc.getDonorCplxType().add(donorCplxType);

		header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_TYPE);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
		Message<GOSHCC> transformedMessage = classToTest.transform(message);
		
		ArrayList<String> errorArray = (ArrayList<String>)transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		
		assertNull("No errors expected", errorArray);
		
		BigInteger bankID = transformedMessage.getPayload().getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType().getBankID();
		
		assertEquals("bankIDs dont match", BigInteger.valueOf(7865564), bankID);
		
	}
	
	
	// Case 2 when multiple bankIDs are returned by the dao
	public void testSetBankID2() throws Exception {
		
		
		List<String> bankIds = new ArrayList<String>();
		bankIds.add("7865564");
		bankIds.add("7865564");
		mockdao.bankIDs = bankIds;
		
		
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankAccNo("78908766");
		bank.setBankAccName("TesterFirst");
		bank.setBankSort("7691382193");
		bank.setBankName("Any Bank");
		bank.setBankSort("an-ys-or");
		bank.setBranchName("Buckingham palace");
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setFirstName("TesterFirst");
		donorCplxType.setLastName("TesterLast");
		donorCplxType.setPostCode("CR0 6RT");
		
		DonationDetails donationDetails = new DonationDetails();
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		directDebitDonationCplxType.setDonorBankCplxType(bank);
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		donorCplxType.setDonationDetails(donationDetails);
		goshcc.getDonorCplxType().add(donorCplxType);

		header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_TYPE);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
		Message<GOSHCC> transformedMessage = classToTest.transform(message);
		
		ArrayList<String> errorArray = (ArrayList<String>)transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		
		assertNotNull("1 error expected", errorArray);
		assertEquals("No. of errors don't match the expected", 1, errorArray.size());

		ArrayList<String> infoArray = (ArrayList<String>)transformedMessage.getHeaders().get(MessageHeaderName.INFO_HEADER.getName());
		
		assertNotNull("1 info expected, none found", infoArray);
		
		String infoMsg = "Multiple banks found for the following bank details: an-ys-or, Any Bank and Buckingham palace;";
		assertEquals("Info messages dont match", infoMsg, infoArray.get(0));
	}

	// Mock Up BankDataAccess
	public class BankDataAccessMock extends BankDataAccess {

		public List<String> bankIDs;
		
		/**
		 * @param ds
		 */
		public BankDataAccessMock(DataSource ds) {
			super(ds);
		}
		
		@Override
		public List<String> getBankID(String sortCode, String bankName, String branchName) {
			return bankIDs;
		}
		
		@Override
		public List<String> getBankID(String sortCode) {
			return bankIDs;
		}
		
	}
}
