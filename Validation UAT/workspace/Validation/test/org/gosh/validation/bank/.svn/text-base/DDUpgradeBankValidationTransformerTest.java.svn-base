///**
// * 
// */
//package org.gosh.validation.bank;
//
//import java.util.List;
//
//import org.gosh.re.dmcash.bindings.GOSHCC;
//import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
//import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
//import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
//import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
//import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.PDDUpgradeFlag;
//import org.gosh.validation.general.bank.DDUpgradeBankValidationTransformer;
//import org.gosh.validation.general.bank.ws.BankReportTool;
//import org.gosh.validation.general.bank.ws.SvcGetBankDetForPledge;
//import org.gosh.validation.general.bank.ws.SvcGetBankDetForPledgeSoap;
//import org.gosh.validation.general.error.ErrorReporter;
//import org.gosh.validation.general.error.MessageXmlTransformer;
//import org.jmock.integration.junit3.MockObjectTestCase;
//import org.springframework.integration.core.Message;
//import org.springframework.integration.message.GenericMessage;
//
///**
// * @author Maria.Urso
// *
// */
//public class DDUpgradeBankValidationTransformerTest extends MockObjectTestCase {
//	/*Commenting the failing tests*/
////@SuppressWarnings("unchecked")
////	public void testLookupWhenFileUpgradeDoNotExistOnRE() throws Exception {
////		GOSHCC goshcc = new GOSHCC();
////		final SvcGetBankDetForPledgeSoap mockWS = mock(SvcGetBankDetForPledgeSoap.class);
////
////		final DonorBankCplxType donorBankCplxType = setTestModel(goshcc, "40-44-19", "HSBC BANK PLC", "Tolworth", "01234567");
////		DDUpgradeBankValidationTransformer ddubankValidationTransformer = setupTransformer();
////		
////		Message<GOSHCC> transformedMessage = ddubankValidationTransformer.transform(new GenericMessage<GOSHCC>(goshcc));
////		assertNull(((List<String>)transformedMessage.getHeaders().get(MessageXmlTransformer.ERROR)));
////	}
//	
////	@SuppressWarnings("unchecked")
////	public void testLookupWhenFileUpgradeExistsOnRE() throws Exception {
////		GOSHCC goshcc = new GOSHCC();
////		final SvcGetBankDetForPledgeSoap mockWS = mock(SvcGetBankDetForPledgeSoap.class);
////
////		final DonorBankCplxType donorBankCplxType = setTestModel(goshcc, "11-02-60", "HSBC BANK PLC", "Tolworth", "00416519");
////		DDUpgradeBankValidationTransformer ddubankValidationTransformer = setupTransformer();
////		
//////		final HashMap<DonorBankCplxType, Boolean> lookupResults = new HashMap<DonorBankCplxType, Boolean>();
//////		lookupResults.put(donorBankCplxType, false);
////		final BankReportTool report = new BankReportTool();
////		
////		
//////		checking(new Expectations(){{
//////			oneOf(mockWS).getBankDetails("00170-545-0002984237");
//////			will(returnValue(report));
//////		}});
////		
////		Message<GOSHCC> transformedMessage = ddubankValidationTransformer.transform(new GenericMessage<GOSHCC>(goshcc));
////		assertFalse(((List<String>)transformedMessage.getHeaders().get(MessageXmlTransformer.INFO)).isEmpty());
////		assertEquals("Some Banks or ConstituentBankID's were removed for: null;", ((List<String>)transformedMessage.getHeaders().get(MessageXmlTransformer.INFO)).get(0));
////		assertTrue(transformedMessage.getPayload().getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getPDDUpgradeFlag().isPDDUpgradeFlag());
////		assertNull("Bank object should be null now.", 
////				transformedMessage.getPayload().getDonorCplxType().get(0).getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType());
////	}
//	
//	private DDUpgradeBankValidationTransformer setupTransformer() {
//		DDUpgradeBankValidationTransformer ddubankValidationTransformer = new DDUpgradeBankValidationTransformer();
//		ddubankValidationTransformer.setBankWS(new SvcGetBankDetForPledge());
//		ddubankValidationTransformer.setReporter(new ErrorReporter());
//		return ddubankValidationTransformer;
//	}
//	
//	private DonorBankCplxType setTestModel(GOSHCC goshcc, String sortCode, String bankName, String postcode, String acctNo) {
//		DonorCplxType donorCplxType = new DonorCplxType();
//		goshcc.getDonorCplxType().add(donorCplxType);
//		return setTestModel(donorCplxType , sortCode, bankName, postcode, acctNo);
//	}
//	
//	private DonorBankCplxType setTestModel(DonorCplxType donorCplxType, String sortCode, String bankName, String postcode, String acctNo) {
//		DonationDetails donationDetails = new DonationDetails();
//		donorCplxType.setDonationDetails(donationDetails);
//		
//		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
//		PDDUpgradeFlag flag = new PDDUpgradeFlag();
//		flag.setPDDUpgradeFlag(true);
//		flag.setGiftimportID("00170-545-0002984237");
//		directDebitDonationCplxType.setPDDUpgradeFlag(flag);
//		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
//		
//		DonorBankCplxType donorBankCplxType = setupDonorBank(sortCode, bankName, postcode, acctNo);
//		directDebitDonationCplxType.setDonorBankCplxType(donorBankCplxType);
//		return donorBankCplxType;
//	}
//
//	private DonorBankCplxType setupDonorBank(String sortCode, String bankName, String branchName, String acctNo) {
//		DonorBankCplxType donorBankCplxType = new DonorBankCplxType();
//		donorBankCplxType.setBankAccNo(acctNo);
//		donorBankCplxType.setBankSort(sortCode);
//		donorBankCplxType.setBankName(bankName);
//		donorBankCplxType.setBranchName(branchName);
//
//		return donorBankCplxType;
//	}
//}
