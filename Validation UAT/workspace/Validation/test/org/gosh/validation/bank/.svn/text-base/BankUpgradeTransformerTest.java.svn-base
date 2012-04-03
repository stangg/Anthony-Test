/**
 * 
 */
package org.gosh.validation.bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.validation.common.FileType;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.bank.BankUpgradeTransformer;
import org.gosh.validation.general.error.ErrorReporter;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

/**
 * @author Kevin.Savage
 *
 */
public class BankUpgradeTransformerTest extends TestCase {
	
	private BankUpgradeTransformer classToTest = null;
	private Map<String, Object> header = new HashMap<String, Object>();
	
	protected void setUp() throws Exception {
		super.setUp();
		classToTest = new BankUpgradeTransformer();
		classToTest.setReporter(new ErrorReporter());
		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	// file type not DD, expect no error even if bank is blank
	public void testBankValdiation() throws Exception {
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);

		header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_UPGRADE_TYPE);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
		
		Message<GOSHCC> transformed = classToTest.transform(message);
		
		ArrayList<String> errorArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		
		assertNull("No errors expected", errorArray);
		
	}
	
	// file type  DD, expect error bank is blank
	public void testBankValdiation2() throws Exception {
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setFirstName("TesterFirst");
		donorCplxType.setLastName("TesterLast");
		donorCplxType.setPostCode("postcode :D");
		
		
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);

		header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_TYPE);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
		
		Message<GOSHCC> transformed = classToTest.transform(message);
		
		ArrayList<String> errorArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		

		for (String error: errorArray) {
			System.out.println(error);
		}
		assertNotNull("No errors logged, 1 error expected", errorArray);
		
		assertEquals("The number of expected errors dont match", 1,  errorArray.size());
		
		String expected = "There was an error for the following donors: [TesterFirst TesterLast of postcode :D] : There are no bank details though it is a fresh DD and not an Upgrade. Also means that the BankAccountName is not present and could cause an issue with BACS";
		assertEquals("Error messages dont match", expected, errorArray.get(0));
	}
	
	// file type DD, expect no error bank not null
	public void testBankValdiation3() throws Exception {
			
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
	
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		
		directDebitDonationCplxType.setDonorBankCplxType(new DonorBankCplxType());

		header.put(MessageHeaderName.FILE_TYPE.getName(), FileType.DD_TYPE);
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc, header);
		
		Message<GOSHCC> transformed = classToTest.transform(message);
		
		ArrayList<String> errorArray = (ArrayList<String>)transformed.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		assertNull("No errors expected", errorArray);
	}
	
//	public void testLooksUpUpgradeBankId() throws Exception {
//		BankUpgradeTransformer transformer = new BankUpgradeTransformer();
//		final Reporter reporter = mock(Reporter.class);
//		transformer.setReporter(reporter);
//			
//		GOSHCC goshcc = new GOSHCC();
//		addValidDonor(goshcc);
//
//		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
//		transformer.transform(message);
//		
//		assertEquals(7619, goshcc.getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getConstituentBankID().intValue());
//	}	
//
//	public void testLooksUpUpgradeBankIdForTheFullCaseFromEmma() throws Exception {
//		BankUpgradeTransformer transformer = new BankUpgradeTransformer();
//		final Reporter reporter = mock(Reporter.class);
//		transformer.setReporter(reporter);
//		
//		JAXBContext context = JAXBContext.newInstance("org.gosh.re.dmcash.bindings");
//		Unmarshaller unmarshaller = context.createUnmarshaller();
//		GOSHCC goshcc = (GOSHCC)unmarshaller.unmarshal(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><GOSHCC><BatchNo>7840034</BatchNo> <SupplierID>PTF</SupplierID> <DonorCplxType><Constituent_ID>40393024</Constituent_ID> <InternalConstit_ID>770812</InternalConstit_ID> <SupplierDonorID>24351673</SupplierDonorID> <Title1>Mr</Title1> <FirstName>James</FirstName> <LastName>Reed</LastName> <ChangeOfName>false</ChangeOfName> <PrimaryAddress>false</PrimaryAddress> <Address><AddressLine>66 The Drive</AddressLine> </Address><City>HORLEY</City> <County>Surrey</County> <PostCode>RH6 7NH</PostCode> <Country>United Kingdom</Country> <DateOfBirth>1967-01-01</DateOfBirth> <Gender>Unknown</Gender> <PhoneEmail><Type>Home</Type> <Value>01293441798</Value> </PhoneEmail><ConsCodes><Code>DMGen</Code> <DateFrom>2009-06-16</DateFrom> </ConsCodes><Attributes><Source><Category>Source</Category> <Description>Telemarketing</Description> <Date>2009-06-16</Date> <Comment>Obtained via PTF</Comment> </Source><DirectMarketingType><Category>Direct Marketing Type</Category> <Description>Bankers Order</Description> <Date>2009-06-16</Date> <Comment>Obtained via PTF</Comment> </DirectMarketingType></Attributes><ConstituentAppeal><AppealID>DMW3210O</AppealID> <PackageID>GEN10A</PackageID> <Date>2009-06-16</Date> <Response>Upgraded</Response> <MarketingSourceCode>24351673</MarketingSourceCode> </ConstituentAppeal><TaxDeclaration><StartDate>2001-04-06</StartDate> <TaxPayer>true</TaxPayer> <DeclarationDate>2009-06-16</DeclarationDate> <DeclarationIndicator>Oral</DeclarationIndicator> <ConfirmationDate>2009-06-16</ConfirmationDate> </TaxDeclaration><DonationDetails><DirectDebitDonationCplxType><Type>Pledge</Type> <SubType>Donations - 1010</SubType> <Date>2009-06-16</Date> <Amount>5.00</Amount> <Fund>UNGEN</Fund> <Campaign>DM32</Campaign> <Appeal>DMW3210O</Appeal> <Package>GEN10A</Package> <PostStatus>Not Posted</PostStatus> <PaymentType>Direct Debit</PaymentType> <LetterCode /> <PDDUpgradeFlag><PDDUpgradeFlag>true</PDDUpgradeFlag> <PreviousPaymentMethod>Direct Debit</PreviousPaymentMethod> <GiftimportID>00170-545-0003232754</GiftimportID> </PDDUpgradeFlag><InstallmentFreq>Monthly</InstallmentFreq> <NoOfInstalments>120</NoOfInstalments> <ScheduleMonthlyType>Specific Day</ScheduleMonthlyType> <ScheduleDayOfMonth>15</ScheduleDayOfMonth> <ScheduleSpacing>1</ScheduleSpacing> <ScheduleWeeklyDayOfWeek>0</ScheduleWeeklyDayOfWeek> <Date1stPayment>2009-08-15</Date1stPayment> <RefNo>1</RefNo> <GiftStatus>Active</GiftStatus> <DonorBankCplxType><BankName /> <BranchName /> <BankSort>- -</BankSort> <BankAccName /> <BankAccNo /> <BankAddress><AddressLine /> </BankAddress><BankCity /> <BankCounty /> <BankPost>AAAA AAA</BankPost> </DonorBankCplxType></DirectDebitDonationCplxType></DonationDetails></DonorCplxType></GOSHCC>".getBytes()));
//
//		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
//		transformer.transform(message);
//		
//		assertEquals(66974, goshcc.getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getConstituentBankID().intValue());
//	}	
//	
//	public void testFailsIfBankIdNotInDB() throws Exception {
//		BankUpgradeTransformer transformer = new BankUpgradeTransformer();
//		final Reporter reporter = mock(Reporter.class);
//		transformer.setReporter(reporter);
//			
//		GOSHCC goshcc = new GOSHCC();
//		final DonorCplxType donorCplxType = new DonorCplxType();
//		donorCplxType.setInternalConstitID("1");
//		
//		goshcc.getDonorCplxType().add(donorCplxType);
//		DonationDetails donationDetails = new DonationDetails();
//		donorCplxType.setDonationDetails(donationDetails);
//	
//		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
//		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
//		PDDUpgradeFlag upgradeFlag = new PDDUpgradeFlag();
//		directDebitDonationCplxType.setPDDUpgradeFlag(upgradeFlag);
//		upgradeFlag.setPDDUpgradeFlag(true);
//		upgradeFlag.setGiftimportID("00001-057-0000001");
//
//		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
//		checking(new Expectations(){{
//			oneOf(reporter).log(with(message), with(hasEntry(donorCplxType, "Either the donation that this donation replaces does not have bank details associated with it or it does not exist.")));
//		}});
//		
//		transformer.transform(message);
//	}	
//	
//	public void testFailsIfGiftImportIdNotInDB() throws Exception {
//		BankUpgradeTransformer transformer = new BankUpgradeTransformer();
//		final Reporter reporter = mock(Reporter.class);
//		transformer.setReporter(reporter);
//			
//		GOSHCC goshcc = new GOSHCC();
//		final DonorCplxType donorCplxType = new DonorCplxType();
//		donorCplxType.setInternalConstitID("49753");
//		
//		goshcc.getDonorCplxType().add(donorCplxType);
//		DonationDetails donationDetails = new DonationDetails();
//		donorCplxType.setDonationDetails(donationDetails);
//	
//		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
//		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
//		PDDUpgradeFlag upgradeFlag = new PDDUpgradeFlag();
//		directDebitDonationCplxType.setPDDUpgradeFlag(upgradeFlag);
//		upgradeFlag.setPDDUpgradeFlag(true);
//		upgradeFlag.setGiftimportID("not-in-database");
//
//		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
//		
//		checking(new Expectations(){{
//			oneOf(reporter).log(with(message), with(hasEntry(donorCplxType, "Either the donation that this donation replaces does not have bank details associated with it or it does not exist.")));
//		}});
//		
//		transformer.transform(message);
//	}	
//	
//	public void testPerformanceIsOk() throws Exception {
//		BankUpgradeTransformer transformer = new BankUpgradeTransformer();
//		final Reporter reporter = mock(Reporter.class);
//		transformer.setReporter(reporter);
//			
//		GOSHCC goshcc = new GOSHCC();
//		for(int i = 0; i < 150; i++){
//			addValidDonor(goshcc);
//		}
//
//		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
//		transformer.transform(message);
//		
//		for (DonorCplxType donor : goshcc.getDonorCplxType()) {
//			assertEquals(7619, donor.getDonationDetails().getDirectDebitDonationCplxType().getConstituentBankID().intValue());
//		}
//	}
//
//	private void addValidDonor(GOSHCC goshcc) {
//		DonorCplxType donorCplxType = new DonorCplxType();
//		donorCplxType.setInternalConstitID("49753");
//		
//		goshcc.getDonorCplxType().add(donorCplxType);
//		DonationDetails donationDetails = new DonationDetails();
//		donorCplxType.setDonationDetails(donationDetails);
//	
//		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
//		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
//		PDDUpgradeFlag upgradeFlag = new PDDUpgradeFlag();
//		directDebitDonationCplxType.setPDDUpgradeFlag(upgradeFlag);
//		upgradeFlag.setPDDUpgradeFlag(true);
//		upgradeFlag.setGiftimportID("00170-057-0046943");
//	}	
}
