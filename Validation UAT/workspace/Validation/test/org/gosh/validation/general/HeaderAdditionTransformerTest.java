/**
 * 
 */
package org.gosh.validation.general;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.validation.ValidationHelper;
import org.gosh.validation.common.FileType;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.error.ErrorReporter;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageHeaders;

/**
 * @author gayathri.polavaram
 *
 */
public class HeaderAdditionTransformerTest extends TestCase {
	
	private HeaderAdditionTransformer classToTest = null;
	private ValidationHelper helper = null;
	
	private final String TEST_DATAFILE_PATH = "test/data/";

	protected void setUp() throws Exception {
		super.setUp();
		classToTest = new HeaderAdditionTransformer();
		helper = new ValidationHelper();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.gosh.validation.general.HeaderAdditionTransformer#transform(org.springframework.integration.core.Message)}.
	 */
	
	/**
	 * Negative test case
	 * Case 1: When the the file does not contain any donation details
	 * The fileType header must not be added. Error must be logged that a files must be of one of the types
	 * This case is not possible if the file is validated against the schema
	 */
	public void testTransform() {
		// Read the data from xml
		try {
			final Message<GOSHCC> message = helper.xml2Message(TEST_DATAFILE_PATH + "no_header_data.xml");
			ErrorReporter reporter = new ErrorReporter();
			classToTest.setReporter(reporter);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			MessageHeaders headers = transformedMsg.getHeaders();
			
			// get the file type header and see check if it is null
			assertNull("FileType header is not null", headers.get(MessageHeaderName.FILE_TYPE.getName()));
			
			// get the file info header and see check if it is null
			assertNull("INFO header is not null", headers.get(MessageHeaderName.INFO_HEADER.getName()));
			
			// get the file error header and check if it is not null
			ArrayList<String> errorList = (ArrayList<String>)headers.get(MessageHeaderName.ERROR_HEADER.getName());
			assertNotNull("Error list is null, expected 1 error", errorList);
			
			assertEquals("Expected number of errors do not match the actual", 1, errorList.size());
			
			assertEquals("Error messages do not match", "Unable to determine the file type", errorList.get(0));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * Case 2: When the file contains tribute element.
	 * FileType header must contain Tribute type
	 */
	public void testTransform2() {
		// Read the data from xml
		try {
			final Message<GOSHCC> message = helper.xml2Message(TEST_DATAFILE_PATH + "header_file2.xml");
			ErrorReporter reporter = new ErrorReporter();
			classToTest.setReporter(reporter);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			MessageHeaders headers = transformedMsg.getHeaders();
			
			// get the error header and see check if it is null
			assertNull("Error List header is not null", headers.get(MessageHeaderName.ERROR_HEADER.getName()));
			
			// get the file info header and see check if it is null
			assertNull("INFO header is not null", headers.get(MessageHeaderName.INFO_HEADER.getName()));
			
			// get the file type header and check if it is not null
			FileType fileType = (FileType)headers.get(MessageHeaderName.FILE_TYPE.getName());
			assertNotNull("FileType is null", fileType);
			assertEquals("Expected FileType does not match the actual", FileType.TRIBUTE_TYPE, fileType);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}

	}
	
	/**
	 * Case 3: When the file contains a tribute ID in the donorCplxType 
	 * FileType header must contain tribute in gift header.
	 */
	public void testTransform3() {
		// Read the data from xml
		try {
			final Message<GOSHCC> message = helper.xml2Message(TEST_DATAFILE_PATH + "header_file3.xml");
			ErrorReporter reporter = new ErrorReporter();
			classToTest.setReporter(reporter);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			MessageHeaders headers = transformedMsg.getHeaders();

			// get the file info header and see check if it is null
			assertNull("INFO header is not null, no info expected", headers.get(MessageHeaderName.INFO_HEADER.getName()));
			
			// get the file error header and check if it is null
			assertNull("Error list is not null, no errors expected", headers.get(MessageHeaderName.ERROR_HEADER.getName()));

			// get the file type header and see check if it is not null
			FileType fileType = (FileType) headers.get(MessageHeaderName.FILE_TYPE.getName());
			assertNotNull("FileType header is null", fileType);
			assertEquals("FileTypes do not match", FileType.TRIBUTE_IN_GIFT_TYPE, fileType);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * Case 4: When the file contains gift attributes in the cash donation
	 * The file type header must be CASH_WITH_GIFT_TYPE  
	 */
	public void testTransform4() {
		// Read the data from xml
		try {
			final Message<GOSHCC> message = helper.xml2Message(TEST_DATAFILE_PATH + "header_file4.xml");
			ErrorReporter reporter = new ErrorReporter();
			classToTest.setReporter(reporter);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			MessageHeaders headers = transformedMsg.getHeaders();
			
			// get the file info header and see check if it is null
			assertNull("INFO header is not null, no info expected", headers.get(MessageHeaderName.INFO_HEADER.getName()));
			
			// get the file error header and check if it is null
			assertNull("Error list is not null, no errors expected", headers.get(MessageHeaderName.ERROR_HEADER.getName()));

			// get the file type header and see check if it is not null
			FileType fileType = (FileType) headers.get(MessageHeaderName.FILE_TYPE.getName());
			assertNotNull("FileType header is null", fileType);
			assertEquals("FileTypes do not match", FileType.CASH_WITH_GIFT_TYPE, fileType);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * Case 5: When there is no donation details
	 * FileType header must contain prospect type
	 */
	public void testTransform5() {
		// Read the data from xml
		try {
			final Message<GOSHCC> message = helper.xml2Message(TEST_DATAFILE_PATH + "header_file5.xml");
			ErrorReporter reporter = new ErrorReporter();
			classToTest.setReporter(reporter);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			MessageHeaders headers = transformedMsg.getHeaders();

			// get the file info header and see check if it is null
			assertNull("INFO header is not null, no info extpected", headers.get(MessageHeaderName.INFO_HEADER.getName()));
			
			// get the file error header and check if it is null
			assertNull("Error list is not null, no errors expected", headers.get(MessageHeaderName.ERROR_HEADER.getName()));

			// get the file type header and see check if it is not null
			FileType fileType = (FileType) headers.get(MessageHeaderName.FILE_TYPE.getName());
			assertNotNull("FileType header is null", fileType);
			assertEquals("FileTypes do not match", FileType.PROSPECT_TYPE, fileType);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * Case 6: When the DonorType is Cash
	 * FileType header must contain cash type
	 */
	public void testTransform6() {
		// Read the data from xml
		try {
			final Message<GOSHCC> message = helper.xml2Message(TEST_DATAFILE_PATH + "header_file6.xml");
			ErrorReporter reporter = new ErrorReporter();
			classToTest.setReporter(reporter);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			MessageHeaders headers = transformedMsg.getHeaders();

			// get the file info header and see check if it is null
			assertNull("INFO header is not null, no info extpected", headers.get(MessageHeaderName.INFO_HEADER.getName()));
			
			// get the file error header and check if it is null
			assertNull("Error list is not null, no errors expected", headers.get(MessageHeaderName.ERROR_HEADER.getName()));

			// get the file type header and see check if it is not null
			FileType fileType = (FileType) headers.get(MessageHeaderName.FILE_TYPE.getName());
			assertNotNull("FileType header is null", fileType);
			assertEquals("FileTypes do not match", FileType.CASH_TYPE, fileType);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * Case 7: When the DonorType is DD
	 * FileType header must contain DD type
	 */
	public void testTransform7() {
		// Read the data from xml
		try {
			final Message<GOSHCC> message = helper.xml2Message(TEST_DATAFILE_PATH + "header_file7.xml");
			ErrorReporter reporter = new ErrorReporter();
			classToTest.setReporter(reporter);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			MessageHeaders headers = transformedMsg.getHeaders();

			// get the file info header and see check if it is null
			assertNull("INFO header is not null, no info extpected", headers.get(MessageHeaderName.INFO_HEADER.getName()));
			
			// get the file error header and check if it is null
			assertNull("Error list is not null, no errors expected", headers.get(MessageHeaderName.ERROR_HEADER.getName()));

			// get the file type header and see check if it is not null
			FileType fileType = (FileType) headers.get(MessageHeaderName.FILE_TYPE.getName());
			assertNotNull("FileType header is null", fileType);
			assertEquals("FileTypes do not match", FileType.DD_TYPE, fileType);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * Case 8: When the DonorType is DD Upgrade
	 * Header must contain DD Upgrade type
	 */
	public void testTransform8() {
		// Read the data from xml
		try {
			final Message<GOSHCC> message = helper.xml2Message(TEST_DATAFILE_PATH + "header_file8.xml");
			ErrorReporter reporter = new ErrorReporter();
			classToTest.setReporter(reporter);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			MessageHeaders headers = transformedMsg.getHeaders();

			// get the file info header and see check if it is null
			assertNull("INFO header is not null, no info extpected", headers.get(MessageHeaderName.INFO_HEADER.getName()));
			
			// get the file error header and check if it is null
			assertNull("Error list is not null, no errors expected", headers.get(MessageHeaderName.ERROR_HEADER.getName()));

			// get the file type header and see check if it is not null
			FileType fileType = (FileType) headers.get(MessageHeaderName.FILE_TYPE.getName());
			assertNotNull("FileType header is null", fileType);
			assertEquals("FileTypes do not match", FileType.DD_UPGRADE_TYPE, fileType);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}

}
