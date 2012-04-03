/**
 * 
 */
package org.gosh.validation.functests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.validation.general.DataCleanUpTransformer;
import org.gosh.validation.general.HeaderAdditionTransformer;
import org.gosh.validation.general.error.ErrorReporter;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

/**
 * @author gayathri.polavaram
 *
 */
public class CleanUpTransformer_CR151Test extends TestCase {
	
	/** Schema location*/
	private static final String SCHEMA_BINDING_LOCATION = "org.gosh.re.dmcash.bindings";

	private DataCleanUpTransformer classToTest = null;
	private final String TEST_DATAFILE_PATH = "test/data/";
	private HeaderAdditionTransformer header = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		classToTest = new DataCleanUpTransformer();
		header = new HeaderAdditionTransformer();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/***
	 * Test case to check if all occurrences of ',' and '\n' are removed from the BankAccName element
	 * The dataCleanUp_CR151.xml contains data to test all possible test cases.
	 * 1. Only a ','
	 * 2. Only a '\n'
	 * 3. Both ',' and '\n'
	 */
	public void testTransform() {
		
		Message<GOSHCC> message;
		try {
			message = xml2Message(TEST_DATAFILE_PATH + "dataCleanUp_CR151.xml");
			ErrorReporter reporter = new ErrorReporter();
			classToTest.setReporter(reporter);
			header.setReporter(reporter);
			
			message = header.transform(message);
			Message<GOSHCC> transformedMsg = classToTest.transform(message);
			
			GOSHCC result = transformedMsg.getPayload();
			
			// Iterate through each DonorCplxType in the payload
			for (DonorCplxType donor : result.getDonorCplxType()) {
				if (donor.getDonationDetails() != null && donor.getDonationDetails().getDirectDebitDonationCplxType() != null 
						&& donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType()!= null
						&& donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType().getBankAccName() != null) {
					DonorBankCplxType donorBankType = donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType();
					if (!StringUtils.isBlank(donorBankType.getBankAccName())) {
						if (StringUtils.contains(donorBankType.getBankAccName(), ',')|| StringUtils.contains(donorBankType.getBankAccName(), '\n')) {
							fail("The BankAccName still contains the comma and hard return characters");
						}
					}
				}
			}
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
	
	/** Helper method to convert an xml file to GOSHCC message.*/
	public Message<GOSHCC> xml2Message(final String relativeFileName) throws JAXBException, FileNotFoundException {
		JAXBContext context = JAXBContext.newInstance(SCHEMA_BINDING_LOCATION);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		GOSHCC goshcc = (GOSHCC)unmarshaller.unmarshal(new FileInputStream( relativeFileName));
		Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		return message;
	}

}
