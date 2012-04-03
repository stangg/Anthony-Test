package org.gosh.validation.functests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.HeaderAdditionTransformer;
import org.gosh.validation.general.accountnumber.ws.SvcGetAccountNumber;
import org.gosh.validation.general.bank.DDUpgradeBankValidationTransformer;
import org.gosh.validation.general.bank.GenericBankValidationTransformer;
import org.gosh.validation.general.businessrules.ConstituentIdValidationTransformer;
import org.gosh.validation.general.error.ErrorReporter;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

/**
 * @author gayathri.polavaram
 * Class to test the cases in the test plan for CR 144
 */
public class DDUpgradeTest extends TestCase{

	private DDUpgradeBankValidationTransformer classToTest;
	private GenericBankValidationTransformer classToTest2;
	private HeaderAdditionTransformer prerequisite1;
	private ConstituentIdValidationTransformer prerequisite2;
	
	private final String TEST_DATAFILE_PATH = "test/data/";
	private static final String SCHEMA_BINDING_LOCATION = "org.gosh.re.dmcash.bindings";
	
	protected void setUp() throws Exception {
		super.setUp();
		
		// create instances of transformers
		classToTest = new DDUpgradeBankValidationTransformer();
		prerequisite1 = new HeaderAdditionTransformer();
		prerequisite2 = new ConstituentIdValidationTransformer();
		classToTest2 = new GenericBankValidationTransformer();
		
		// add reporters
		ErrorReporter reporter = new ErrorReporter();
		classToTest.setReporter(reporter);
		classToTest2.setReporter(reporter);
		prerequisite1.setReporter(reporter);
		prerequisite2.setReporter(reporter);
		
		// Set data sources
		SQLServerDataSource ds = TestDataSourceFactory.getDataSource(); 
		classToTest.setDataSource(ds);
		classToTest2.setDataSource(ds);
		prerequisite2.setDataSource(ds);
		
		// Set web services
		classToTest.setAccNumberWS(new SvcGetAccountNumber());
		
		
		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/** Helper method to convert an xml file to GOSHCC message.*/
	public Message<GOSHCC> xml2Message(final String relativeFileName) throws JAXBException, FileNotFoundException {
		JAXBContext context = JAXBContext.newInstance(SCHEMA_BINDING_LOCATION);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		GOSHCC goshcc = (GOSHCC)unmarshaller.unmarshal(new FileInputStream( relativeFileName));
		Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		return message;
	}
	
	/**
	 * To test Item 3 in test plan 
	 */
	public void testCase3() {
		Message<GOSHCC> message;
		try {
			message = xml2Message(TEST_DATAFILE_PATH + "cr144_case3.xml");
			
			message = prerequisite1.transform(message);
			message = prerequisite2.transform(message);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			GOSHCC result = transformedMsg.getPayload();
			
			ArrayList<String> errorArray = (ArrayList<String>)transformedMsg.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			
			assertNull("No errors expected", errorArray);
			
			ArrayList<String> infoArray = (ArrayList<String>)transformedMsg.getHeaders().get(MessageHeaderName.INFO_HEADER.getName());
			assertNotNull("2 info messages expected", infoArray);
			assertEquals("number of expected info don't match", 1, infoArray.size());
			
			String expected = "Bank details or constituentBankID removed for the donors with following ConstituentID: 40792179;";
			assertEquals("Info messages don't match", expected, infoArray.get(0));
			
			DonorBankCplxType bankdetails = ((GOSHCC)transformedMsg.getPayload()).getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType();
			assertNull("bank details not removed", bankdetails);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		}
	}
	
	/**
	 * To test Item 5 in test plan 
	 */
	public void testCase5() {
		Message<GOSHCC> message;
		try {
			message = xml2Message(TEST_DATAFILE_PATH + "cr144_case5.xml");
			
			message = prerequisite1.transform(message);
			message = prerequisite2.transform(message);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			GOSHCC result = transformedMsg.getPayload();

			ArrayList<String> errorArray = (ArrayList<String>)transformedMsg.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			
			assertNull("No errors expected", errorArray);
			
			ArrayList<String> infoArray = (ArrayList<String>)transformedMsg.getHeaders().get(MessageHeaderName.INFO_HEADER.getName());
			assertNotNull("1 info messages expected", infoArray);
			assertEquals("number of expected info don't match", 1, infoArray.size());
			
			String expected = "Bank details or constituentBankID removed for the donors with following ConstituentID: 40792181;";
			assertEquals("Info messages don't match", expected, infoArray.get(0));
			
			DonorBankCplxType bankdetails = ((GOSHCC)transformedMsg.getPayload()).getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType();
			assertNull("bank details not removed", bankdetails);
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		}
	}
	
	/**
	 * To test Item 3 in test plan 
	 */
	public void testCase40() {
		Message<GOSHCC> message;
		try {
			message = xml2Message(TEST_DATAFILE_PATH + "cr144_case40.xml");
			
			message = prerequisite1.transform(message);
			message = prerequisite2.transform(message);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			GOSHCC result = transformedMsg.getPayload();
			
			ArrayList<String> errorArray = (ArrayList<String>)transformedMsg.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			
			assertNull("No errors expected", errorArray);

			ArrayList<String> infoArray = (ArrayList<String>)transformedMsg.getHeaders().get(MessageHeaderName.INFO_HEADER.getName());
			assertNotNull("1 info messages expected", infoArray);
			assertEquals("number of expected info don't match", 1, infoArray.size());
			
			String expected = "Bank details or constituentBankID removed for the donors with following ConstituentID: 40792216;";
			assertEquals("Info messages don't match", expected, infoArray.get(0));
			
			DonorBankCplxType bankdetails = ((GOSHCC)transformedMsg.getPayload()).getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType();
			assertNull("bank details not removed", bankdetails);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		}
	}
	
	/**
	 * To test item 2 is bulk test
	 */
	public void testBulkCase2() {
		Message<GOSHCC> message;
		try {
			message = xml2Message(TEST_DATAFILE_PATH + "bulkTest_cr144_case2.xml");
			
			message = prerequisite1.transform(message);
			message = prerequisite2.transform(message);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			
			GOSHCC result = transformedMsg.getPayload();
			
			// check if the internal const id is added
			assertNotNull(result.getDonorCplxType().get(0).getInternalConstitID());
			assertEquals("InternalConstIDs dont match", "1253421", result.getDonorCplxType().get(0).getInternalConstitID());
			
			// check if the bank const id is added
			assertNotNull(result.getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getConstituentBankID());
			assertEquals("BankConstIDs dont match", BigInteger.valueOf(225691), result.getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getConstituentBankID());

			ArrayList<String> errorArray = (ArrayList<String>)transformedMsg.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			
			assertNull("No errors expected", errorArray);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		}
	}
	
	/**
	 * To test item 3 is bulk test of test plan for CR 144
	 * assuming that there is no relation yet b/w the bank and the donor in RE
	 * It must set the bank ID, we wont run it through DDUpgradeBankValidationTransformer
	 */
	public void testBulkCase3() {
		Message<GOSHCC> message;
		try {
			message = xml2Message(TEST_DATAFILE_PATH + "bulkTest_cr144_case3.xml");
			
			message = prerequisite1.transform(message);
			Message<GOSHCC> transformedMsg = classToTest2.transform(message);
			
			
			GOSHCC result = transformedMsg.getPayload();

			ArrayList<String> errorArray = (ArrayList<String>)transformedMsg.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			
			assertNull("No errors expected", errorArray);
			
			BigInteger bankId = result.getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType().getBankID();
			
			assertEquals("Bank IDs dont match", BigInteger.valueOf(12509), bankId);
			
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception occured: " + e.getMessage());
		} 
	}
	
	
	
}
