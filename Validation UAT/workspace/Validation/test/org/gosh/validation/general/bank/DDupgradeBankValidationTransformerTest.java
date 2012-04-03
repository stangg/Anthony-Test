/**
 * 
 */
package org.gosh.validation.general.bank;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.PDDUpgradeFlag;
import org.gosh.validation.ValidationHelper;
import org.gosh.validation.common.FileType;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.accountnumber.ws.Report;
import org.gosh.validation.general.accountnumber.ws.SvcGetAccountNumber;
import org.gosh.validation.general.accountnumber.ws.SvcGetAccountNumberSoap;
import org.gosh.validation.general.error.ErrorReporter;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

/**
 * @author gayathri.polavaram
 *
 */
public class DDupgradeBankValidationTransformerTest extends MockObjectTestCase {
	
	private static final String TEST_DATAFILE_PATH = "test/data/";
	
	private ValidationHelper helper = null;
	
	Map<String, Object> header; 
	
	private DDUpgradeBankValidationTransformer classToTest = null;
	
	private BankDataAccessMock dao;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		dao = new BankDataAccessMock(new SQLServerDataSource());
			
		classToTest = new DDUpgradeBankValidationTransformer();
		classToTest.setReporter(new ErrorReporter());
		classToTest.setDao(dao);
		//classToTest.setDataSource(TestDataSourceFactory.getDataSource());
		
		helper = new ValidationHelper();
		header = new HashMap<String, Object>();
		header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_UPGRADE_TYPE);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test for header other than DDU. No errors.. must return without performing any checks/transformation
	 */
	public void testTransformHeader() {
		header = new HashMap<String, Object>();
		header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_TYPE);
		GenericMessage<GOSHCC> genMsg = new GenericMessage(new GOSHCC(), header);
		try {
			Message transformed = classToTest.transform(genMsg);
			
			ArrayList<String> errorArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			
			assertNull("No errors expected", errorArray);
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	
	/**
	 * Test for header null. No errors.. must return without performing any checks/transformation
	 */
	public void testTransformHeader2() {
		GenericMessage<GOSHCC> genMsg = new GenericMessage(new GOSHCC());
		try {
			Message transformed = classToTest.transform(genMsg);
			
			ArrayList<String> errorArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			
			assertNull("No errors expected", errorArray);
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * When donorCplxType is null
	 */
	public void testTransform1() {
		GenericMessage<GOSHCC> genMsg = new GenericMessage(new GOSHCC(), header);
		try {
			Message transformed = classToTest.transform(genMsg);
			
			ArrayList<String> errorArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			
			assertNull("No errors expected", errorArray);
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * When donationDetails is null
	 */
	public void testTransform2() {
		
		GOSHCC payload = new GOSHCC();
		
		payload.getDonorCplxType().add(new DonorCplxType());
		
		GenericMessage<GOSHCC> genMsg = new GenericMessage(payload, header);
		try {
			Message transformed = classToTest.transform(genMsg);
			
			ArrayList<String> errorArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			
			assertNull("No errors expected", errorArray);
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
		
		
	}
	
	/**
	 * When DirectDebitDonationCplxType is null
	 */
	public void testTransform3() {
		
		GOSHCC payload = new GOSHCC();
		
		DonorCplxType donor = new DonorCplxType();

		donor.setDonationDetails(new DonationDetails());
		payload.getDonorCplxType().add(donor);
		
		GenericMessage<GOSHCC> genMsg = new GenericMessage(payload, header);
		try {
			Message transformed = classToTest.transform(genMsg);
			
			ArrayList<String> errorArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			
			assertNull("No errors expected", errorArray);
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
		
		
	}
	
	
	/**
	 * When DonorBankCplxType is null
	 */
	public void testTransform4() {
		
		GOSHCC payload = new GOSHCC();
		
		DonorCplxType donor = new DonorCplxType();
		DonationDetails donation = new DonationDetails();
		
		donation.setDirectDebitDonationCplxType(new DirectDebitDonationCplxType());
		donor.setDonationDetails(donation);
		payload.getDonorCplxType().add(donor);
		
		GenericMessage<GOSHCC> genMsg = new GenericMessage(payload, header);
		try {
			Message transformed = classToTest.transform(genMsg);
			
			ArrayList<String> errorArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			
			assertNull("No errors expected", errorArray);
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * When DonorBankCplxType(bank details) is null, gift importID is present. Bank const id must be added
	 */
	public void testTransform12() {
		
		GOSHCC payload = new GOSHCC();
		
		DonorCplxType donor = new DonorCplxType();
		donor.setFirstName("TesterFirst");
		donor.setLastName("TesterLast");
		donor.setPostCode("postcode :D");
		donor.setInternalConstitID("internalID");
		
		DonationDetails donation = new DonationDetails();
		DirectDebitDonationCplxType dDDetails = new DirectDebitDonationCplxType();
		final PDDUpgradeFlag ddUFlag = new PDDUpgradeFlag();
		ddUFlag.setGiftimportID("some gift import id");
		
		dDDetails.setPDDUpgradeFlag(ddUFlag);
		donation.setDirectDebitDonationCplxType(dDDetails);
		donor.setDonationDetails(donation);
		payload.getDonorCplxType().add(donor);
		
		GenericMessage<GOSHCC> genMsg = new GenericMessage(payload, header);
		try {
			
			dao.bankConstID = BigInteger.valueOf(10101);
			
			Message transformed = classToTest.transform(genMsg);
			ArrayList<String> errorArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			assertNull("No errors expected", errorArray);
			
			BigInteger constBankID = ((GOSHCC)transformed.getPayload()).getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getConstituentBankID();
			assertEquals("ConstBankID not matching", BigInteger.valueOf(10101), constBankID);
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * When AccountNo, SortCode and AccName is null and also the giftID and internalConstituent ID
	 */
	public void testTransform5() {
		
		GOSHCC payload = new GOSHCC();
		
		DonorCplxType donor = new DonorCplxType();
		donor.setFirstName("TesterFirst");
		donor.setLastName("TesterLast");
		donor.setPostCode("postcode :D");
		
		DonationDetails donation = new DonationDetails();
		DirectDebitDonationCplxType dDDetails = new DirectDebitDonationCplxType();

		dDDetails.setDonorBankCplxType(new DonorBankCplxType());
		donation.setDirectDebitDonationCplxType(dDDetails);
		donor.setDonationDetails(donation);
		payload.getDonorCplxType().add(donor);
		
		GenericMessage<GOSHCC> genMsg = new GenericMessage(payload, header);
		try {
			Message transformed = classToTest.transform(genMsg);
			
			ArrayList<String> errorArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			
			assertNull("No errors expected", errorArray);
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * When AccountNo, SortCode and AccName is null but bankdetails are not null. giftImportID and valid internalConstituent ID are present.
	 * No action to be taken
	 */
	public void testTransform6() {
		
		GOSHCC payload = new GOSHCC();
		
		final DonorCplxType donor = new DonorCplxType();
		donor.setFirstName("TesterFirst");
		donor.setLastName("TesterLast");
		donor.setPostCode("postcode :D");
		donor.setInternalConstitID("internalID");
		
		DonationDetails donation = new DonationDetails();
		DirectDebitDonationCplxType dDDetails = new DirectDebitDonationCplxType();
		final PDDUpgradeFlag ddUFlag = new PDDUpgradeFlag();
		ddUFlag.setGiftimportID("some gift import id");
		
		dDDetails.setPDDUpgradeFlag(ddUFlag);
		dDDetails.setDonorBankCplxType(new DonorBankCplxType());
		donation.setDirectDebitDonationCplxType(dDDetails);
		donor.setDonationDetails(donation);
		payload.getDonorCplxType().add(donor);
		
		GenericMessage<GOSHCC> genMsg = new GenericMessage(payload, header);
		try {
			
			dao.bankConstID = BigInteger.valueOf(10101);
			
			Message transformed = classToTest.transform(genMsg);
			ArrayList<String> errorArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			assertNull("No errors expected", errorArray);
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * bank details are null and giftImportID and invalid internalConstituent ID are present
	 * must add BankConstId and info
	 */
	public void testTransform7() {
		
		GOSHCC payload = new GOSHCC();
		
		final DonorCplxType donor = new DonorCplxType();
		donor.setFirstName("TesterFirst");
		donor.setLastName("TesterLast");
		donor.setPostCode("postcode :D");
		donor.setInternalConstitID("internalID");
		
		DonationDetails donation = new DonationDetails();
		DirectDebitDonationCplxType dDDetails = new DirectDebitDonationCplxType();
		final PDDUpgradeFlag ddUFlag = new PDDUpgradeFlag();
		ddUFlag.setGiftimportID("some gift import id");
		
		dDDetails.setPDDUpgradeFlag(ddUFlag);
		//dDDetails.setDonorBankCplxType(new DonorBankCplxType());
		donation.setDirectDebitDonationCplxType(dDDetails);
		donor.setDonationDetails(donation);
		payload.getDonorCplxType().add(donor);
		
		GenericMessage<GOSHCC> genMsg = new GenericMessage(payload, header);
		try {
			
			Message transformed = classToTest.transform(genMsg);
			ArrayList<String> errorArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			assertNotNull("1 error expected", errorArray);
			assertEquals("The number of expected errors dont match", 1, errorArray.size());
			
			String expected = "There was an error for the following donors: [TesterFirst TesterLast of postcode :D] : Either the donation that these replace does not have bank details associated with them or they do not exist.";
			
			assertEquals("error messages do not match", expected, errorArray.get(0));
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * When Acc Number and Sort Code are present, multiple banks match with the sortcode 
	 * and one constituentBankID is returned. No errors, the constBankID must be added and bank details removed.
	 * One info message expected
	 */
	public void testTransform10() {
		
		GOSHCC payload = new GOSHCC();
		
		final DonorCplxType donor = new DonorCplxType();
		donor.setFirstName("TesterFirst");
		donor.setLastName("TesterLast");
		donor.setPostCode("postcode :D");
		donor.setInternalConstitID("internalID");
		donor.setConstituentID("215371");
		
		DonationDetails donation = new DonationDetails();
		DirectDebitDonationCplxType dDDetails = new DirectDebitDonationCplxType();
		final PDDUpgradeFlag ddUFlag = new PDDUpgradeFlag();
		ddUFlag.setGiftimportID("some gift import id");
		DonorBankCplxType bankDetails = new DonorBankCplxType();
		bankDetails.setBankAccNo("Some Number");
		bankDetails.setBankSort("Some Bank Sort Code");
		
		
		dDDetails.setPDDUpgradeFlag(ddUFlag);
		dDDetails.setDonorBankCplxType(bankDetails);
		donation.setDirectDebitDonationCplxType(dDDetails);
		donor.setDonationDetails(donation);
		payload.getDonorCplxType().add(donor);
		
		GenericMessage<GOSHCC> genMsg = new GenericMessage(payload, header);
		try {
			
			dao.bankIDs = new ArrayList<String>();
			dao.bankIDs.add("one");
			
			dao.bankConstIDs = new ArrayList<BigInteger>();
			dao.bankConstIDs.add(BigInteger.valueOf(1234455667));
			
			Message transformed = classToTest.transform(genMsg);
			ArrayList<String> errorArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			assertNull("No errors expected", errorArray);
			
			BigInteger constBankID = ((GOSHCC)transformed.getPayload()).getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getConstituentBankID();
			assertEquals("ConstBankID not matching", BigInteger.valueOf(1234455667), constBankID);
			
			DonorBankCplxType bankdetails = ((GOSHCC)transformed.getPayload()).getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType();
			assertNull("bank details not removed", bankdetails);
			
			ArrayList<String> infoArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.INFO_HEADER.getName());
			assertNotNull("1 info expected", infoArray);
			assertEquals("number of expected info don't match", 1, infoArray.size());
			
			String expected = "Bank details or constituentBankID removed for the donors with following ConstituentID: 215371;";
			assertEquals("Info messages don't match", expected, infoArray.get(0));
			
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	/**
	 * When Acc Number and Sort Code are present, multiple banks match with the sortcode 
	 * and multiple constituentBankID is returned. ws must be called. WS returns a a/c no same as the
	 * one provided in xml. The constituentID that fetches the matching a/c number must be written into
	 * the file and bank details must be removed.
	 * 
	 */
	public void testTransform11() {
		
		GOSHCC payload = new GOSHCC();
		
		final DonorCplxType donor = new DonorCplxType();
		donor.setFirstName("TesterFirst");
		donor.setLastName("TesterLast");
		donor.setPostCode("postcode :D");
		donor.setInternalConstitID("internalID");
		donor.setConstituentID("215371");
		
		DonationDetails donation = new DonationDetails();
		DirectDebitDonationCplxType dDDetails = new DirectDebitDonationCplxType();
		final PDDUpgradeFlag ddUFlag = new PDDUpgradeFlag();
		ddUFlag.setGiftimportID("some gift import id");
		DonorBankCplxType bankDetails = new DonorBankCplxType();
		bankDetails.setBankAccNo("Some Number");
		bankDetails.setBankSort("Some Bank Sort Code");
		
		
		dDDetails.setPDDUpgradeFlag(ddUFlag);
		dDDetails.setDonorBankCplxType(bankDetails);
		donation.setDirectDebitDonationCplxType(dDDetails);
		donor.setDonationDetails(donation);
		payload.getDonorCplxType().add(donor);
		
		GenericMessage<GOSHCC> genMsg = new GenericMessage(payload, header);
		try {
			
			dao.bankIDs = new ArrayList<String>();
			dao.bankIDs.add("one");
			
			dao.bankConstIDs = new ArrayList<BigInteger>();
			dao.bankConstIDs.add(BigInteger.valueOf(1234455667));
			dao.bankConstIDs.add(BigInteger.valueOf(223455667));
			dao.bankConstIDs.add(BigInteger.valueOf(323455667));
			
			final SvcGetAccountNumberMock mockSvc = new SvcGetAccountNumberMock();
			final SvcGetAccountNumberSoap mockSoap = mock(SvcGetAccountNumberSoap.class);
			
			mockSvc.setWebsvr(mockSoap);
			
			classToTest.setAccNumberWS(mockSvc);
			
			final int bankID = 0;
			
			final Report wsreport0 = new Report();
			wsreport0.setAccountNo("Not the match");
			wsreport0.setSuccess(true);
			
			final Report wsreport1 = new Report();
			wsreport1.setAccountNo("Some Number");
			wsreport1.setSuccess(true);
			
			checking(new Expectations(){{
				oneOf(mockSoap).getAccountNumber(dao.bankConstIDs.get(0).intValue()); will (returnValue(wsreport0));
				oneOf(mockSoap).getAccountNumber(dao.bankConstIDs.get(1).intValue()); will (returnValue(wsreport1));
			}});
			
			
			Message transformed = classToTest.transform(genMsg);
			ArrayList<String> errorArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
			
			assertNull("No errors expected", errorArray);
			
			BigInteger constBankID = ((GOSHCC)transformed.getPayload()).getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getConstituentBankID();
			assertEquals("ConstBankID not matching", dao.bankConstIDs.get(1), constBankID);
			
			DonorBankCplxType bankdetails = ((GOSHCC)transformed.getPayload()).getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType();
			assertNull("bank details not removed", bankdetails);
			
			ArrayList<String> infoArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.INFO_HEADER.getName());
			assertNotNull("1 info expected", infoArray);
			assertEquals("number of expected info don't match", 1, infoArray.size());
			
			String expected = "Bank details or constituentBankID removed for the donors with following ConstituentID: 215371;";
			assertEquals("Info messages don't match", expected, infoArray.get(0));
			
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
	
	public void testTransformer12 () {
		String bankName = "Rangoli";
		BigInteger bankID = BigInteger.valueOf(123L);
		Integer intValue = Integer.valueOf(0);
		
		if (intValue instanceof Integer) {
			System.out.println("It's an integer value");
		}
		assertEquals("Same value", intValue, Integer.valueOf(0));
		
	}
	
	// Mock up the webservice
	public class SvcGetAccountNumberMock extends SvcGetAccountNumber {
		
		private SvcGetAccountNumberSoap websvr = null;
		
		public void setWebsvr(SvcGetAccountNumberSoap websvr) {
			this.websvr = websvr;
		}

		@Override
		public SvcGetAccountNumberSoap getSvcGetAccountNumberSoap() {
	        return websvr;
	    }
	}
	
	// Mock Up BankDataAccess
	public class BankDataAccessMock extends BankDataAccess {

		public List<String> bankIDs;
		public List<BigInteger> bankConstIDs;
		public BigInteger bankConstID;
		
		/**
		 * @param ds
		 */
		public BankDataAccessMock(DataSource ds) {
			super(ds);
		}
		
		@Override
		public List<String> getBankID(String sortCode) {
			return bankIDs;
		}
	
		@Override
		public List<BigInteger> getBankConstitIdBySortCode(String sortCode, String constituentID) {
			return bankConstIDs;
		}
		
		@Override
		public BigInteger getBankConstitIdByGift(String internalConstId, String giftImportID) {
			return bankConstID;
		}
		
	}
	
	

}
