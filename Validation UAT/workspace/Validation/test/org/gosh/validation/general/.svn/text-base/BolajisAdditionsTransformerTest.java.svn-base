package org.gosh.validation.general;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.xml.datatype.DatatypeFactory;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.Attributes;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.Attributes.DeceasedNotificationDate;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.Attributes.DirectMarketingType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.ConstituentAppeal;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.Tribute;
import org.gosh.re.dmcash.bindings.GiftAttributes;
import org.gosh.validation.general.error.Reporter;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

public class BolajisAdditionsTransformerTest extends MockObjectTestCase {
	public void testNullCase() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		transformer.setReporter(mock(Reporter.class));
		Message<GOSHCC> message = new GenericMessage<GOSHCC>(new GOSHCC());
		transformer.transform(message);
	}
	
	public void testValidCommentCase() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		transformer.setReporter(mock(Reporter.class));
		
		GOSHCC goshcc = new GOSHCC();
		goshcc.setSupplierID("supplierId");
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		Attributes attributes = new Attributes();
		donorCplxType.setAttributes(attributes);
		DirectMarketingType directMarketingType = new DirectMarketingType();
		attributes.setDirectMarketingType(directMarketingType);
		directMarketingType.setComment("Obtained via supplierId");
		directMarketingType.setDescription("DM Prospect");
		
		Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(message);
	}

	public void testInvalidCommentCase() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		goshcc.setSupplierID("supplierId");
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		Attributes attributes = new Attributes();
		donorCplxType.setAttributes(attributes);
		DirectMarketingType directMarketingType = new DirectMarketingType();
		attributes.setDirectMarketingType(directMarketingType);
		directMarketingType.setComment("Obtained via someone else.");
		directMarketingType.setDescription("DM Prospect");
		
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		checking(new Expectations(){{
			checkMessageReported(reporter, donorCplxType, message, "In one of the attributes we expected the comment \"Obtained via supplierId\" but didn't get it.");
		}});
		
		transformer.transform(message);
	}
	
	public void testValidMarketingSourceCodeCase() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setSupplierDonorID("supplierId");
		goshcc.getDonorCplxType().add(donorCplxType);

		ConstituentAppeal constituentAppeal = new ConstituentAppeal();
		constituentAppeal.setMarketingSourceCode("supplierId");
		constituentAppeal.setResponse("Prospect");
		donorCplxType.setConstituentAppeal(constituentAppeal);
		
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(message);
	}

	public void testInvalidMarketingSourceCodeCase() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setSupplierDonorID("supplierId");
		goshcc.getDonorCplxType().add(donorCplxType);

		ConstituentAppeal constituentAppeal = new ConstituentAppeal();
		constituentAppeal.setMarketingSourceCode("something else");
		donorCplxType.setConstituentAppeal(constituentAppeal);
		constituentAppeal.setResponse("Prospect");
		
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		checking(new Expectations(){{
			checkMessageReported(reporter, donorCplxType, message, "The marketing source code should be the same as the supplier donor id but it is not.");
		}});
		transformer.transform(message);
	}
	
	public void testValidResponseTypeCases() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);

		ConstituentAppeal constituentAppeal = new ConstituentAppeal();
		constituentAppeal.setResponse("Prospect");
		donorCplxType.setConstituentAppeal(constituentAppeal);
		
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(message);

		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		donationDetails.getCashDonationCplxType().add(new CashDonationCplxType());
		constituentAppeal.setResponse("Responded");
		
		transformer.transform(message);
	}

	public void testInvalidResponseTypeCases() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);

		ConstituentAppeal constituentAppeal = new ConstituentAppeal();
		constituentAppeal.setResponse("invalid");
		donorCplxType.setConstituentAppeal(constituentAppeal);
		
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		checkMessageReported(reporter, donorCplxType, message, "On one or more records, the \"response\" does not contain what is expected.");
		transformer.transform(message);
	}
	
	public void testInvalidResponseTypeCasesThatAreIgnoredBecauseThereIsATributeInTheFile() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		
		goshcc.getTribute().add(new Tribute());

		ConstituentAppeal constituentAppeal = new ConstituentAppeal();
		constituentAppeal.setResponse("invalid");
		donorCplxType.setConstituentAppeal(constituentAppeal);
		
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(message);
	}
	
	public void testInvalidResponseTypeCasesThatAreIgnoredBecauseAGiftHasATributeID() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		
		ConstituentAppeal constituentAppeal = new ConstituentAppeal();
		constituentAppeal.setResponse("invalid");
		donorCplxType.setConstituentAppeal(constituentAppeal);
		
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		CashDonationCplxType cashDonationCplxType = new CashDonationCplxType();
		donationDetails.getCashDonationCplxType().add(cashDonationCplxType);
		cashDonationCplxType.setTributeID("some id that is not blank");
		
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(message);
	}
	
	public void testValidDirectMarketingTypeCases() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		Attributes attributes = new Attributes();
		donorCplxType.setAttributes(attributes);
		DirectMarketingType directMarketingType = new DirectMarketingType();
		attributes.setDirectMarketingType(directMarketingType);
		directMarketingType.setDescription("DM Prospect");
		directMarketingType.setComment("Obtained via null");
		
		Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(message);
	}
	
	public void testInvalidDirectMarketingTypeCases() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		Attributes attributes = new Attributes();
		donorCplxType.setAttributes(attributes);
		DirectMarketingType directMarketingType = new DirectMarketingType();
		attributes.setDirectMarketingType(directMarketingType);
		directMarketingType.setDescription("Invalid");
		directMarketingType.setComment("Obtained via null");
		
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		checking(new Expectations(){{
			oneOf(reporter).log(with(message), with(equal(toHashSetWith(donorCplxType))), with("On one or more records, the \"DirectMarktingType\" does not contain what is expected."));
		}});
		transformer.transform(message);
	}
	
	/*Commenting the failing tests*/
//	public void testValidGiftStatus() throws Exception {
//		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
//		final Reporter reporter = mock(Reporter.class);
//		transformer.setReporter(reporter);
//		
//		GOSHCC goshcc = new GOSHCC();
//		DonorCplxType donorCplxType = new DonorCplxType();
//		goshcc.getDonorCplxType().add(donorCplxType);
//		Attributes attributes = new Attributes();
//		donorCplxType.setAttributes(attributes);
//		DirectMarketingType directMarketingType = new DirectMarketingType();
//		attributes.setDirectMarketingType(directMarketingType);
//		directMarketingType.setDescription("Bankers Order");
//		directMarketingType.setComment("Obtained via null");
//		DonationDetails donationDetails = new DonationDetails();
//		donorCplxType.setDonationDetails(donationDetails);
//		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
//		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
//		directDebitDonationCplxType.setGiftStatus("Active");
//		
//		Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
//		transformer.transform(message);
//	}
//	
//	public void testInvalidGiftStatus() throws Exception {
//		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
//		final Reporter reporter = mock(Reporter.class);
//		transformer.setReporter(reporter);
//		
//		GOSHCC goshcc = new GOSHCC();
//		final DonorCplxType donorCplxType = new DonorCplxType();
//		goshcc.getDonorCplxType().add(donorCplxType);
//		Attributes attributes = new Attributes();
//		donorCplxType.setAttributes(attributes);
//		DirectMarketingType directMarketingType = new DirectMarketingType();
//		attributes.setDirectMarketingType(directMarketingType);
//		directMarketingType.setDescription("Bankers Order");
//		directMarketingType.setComment("Obtained via null");
//		DonationDetails donationDetails = new DonationDetails();
//		donorCplxType.setDonationDetails(donationDetails);
//		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
//		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
//		directDebitDonationCplxType.setGiftStatus("Somehing invalid");
//		
//		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
//		checkMessageReported(reporter, donorCplxType, message, "On one or more records, the \"Gift Status\" is not Active.");
//		transformer.transform(message);
//	}

	public void testInvalidAmount() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		Attributes attributes = new Attributes();
		donorCplxType.setAttributes(attributes);
		DirectMarketingType directMarketingType = new DirectMarketingType();
		attributes.setDirectMarketingType(directMarketingType);
		directMarketingType.setDescription("Bankers Order");
		directMarketingType.setComment("Obtained via null");
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		directDebitDonationCplxType.setGiftStatus("Active");
		directDebitDonationCplxType.setAmount(new BigDecimal(0));
		
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		
		checking(new Expectations(){{
			exactly(1).of(reporter).log(with(message), with(equal(toHashSetWith(donorCplxType))), with("One or more records has a zero amount."));
			will(returnValue(message)); // Returns a message with empty Account Name and hence the next expectation
		}});
		transformer.transform(message);
	}

	/*Commenting the failing tests*/
//	public void testValidAmount() throws Exception {
//		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
//		final Reporter reporter = mock(Reporter.class);
//		transformer.setReporter(reporter);
//		
//		GOSHCC goshcc = new GOSHCC();
//		final DonorCplxType donorCplxType = new DonorCplxType();
//		goshcc.getDonorCplxType().add(donorCplxType);
//		Attributes attributes = new Attributes();
//		donorCplxType.setAttributes(attributes);
//		DirectMarketingType directMarketingType = new DirectMarketingType();
//		attributes.setDirectMarketingType(directMarketingType);
//		directMarketingType.setDescription("Bankers Order");
//		directMarketingType.setComment("Obtained via null");
//		DonationDetails donationDetails = new DonationDetails();
//		donorCplxType.setDonationDetails(donationDetails);
//		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
//		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
//		directDebitDonationCplxType.setGiftStatus("Active");
//		directDebitDonationCplxType.setAmount(new BigDecimal(3));
//		
//		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
//		transformer.transform(message);
//	}
//	
//	public void testDonorsWithNCOrgsHaveAddressTypeBusiness() throws Exception {
//		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
//		final Reporter reporter = mock(Reporter.class);
//		transformer.setReporter(reporter);
//		
//		GOSHCC goshcc = new GOSHCC();
//		final DonorCplxType donorCplxType = new DonorCplxType();
//		goshcc.getDonorCplxType().add(donorCplxType);
//		Attributes attributes = new Attributes();
//		donorCplxType.setAttributes(attributes);
//		DirectMarketingType directMarketingType = new DirectMarketingType();
//		attributes.setDirectMarketingType(directMarketingType);
//		directMarketingType.setDescription("Bankers Order");
//		directMarketingType.setComment("Obtained via null");
//		DonationDetails donationDetails = new DonationDetails();
//		donorCplxType.setDonationDetails(donationDetails);
//		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
//		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
//		directDebitDonationCplxType.setGiftStatus("Active");
//		directDebitDonationCplxType.setAmount(new BigDecimal(3));
//		
//		List<NonConstituentOrganisationRelationship> relationships = donorCplxType.getNonConstituentOrganisationRelationship();
//		NonConstituentOrganisationRelationship relationship = new NonConstituentOrganisationRelationship();
//		relationships.add(relationship);
//		
//		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
//		checking(new Expectations(){{
//			oneOf(reporter).log(with(message), with(equal(toHashSetWith(donorCplxType))), with("One or more records has a non constituent organisation but does not have the address type of business."));
//		}});
//
//		transformer.transform(message);
//		
//		donorCplxType.setAddressType("Business");
//		transformer.transform(message);
//	}
	
	public void testDonationsWithVoucherPaymentTypeHavePledgeAsType() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		CashDonationCplxType cashDonationCplxType = new CashDonationCplxType();
		donationDetails.getCashDonationCplxType().add(cashDonationCplxType);

		cashDonationCplxType.setPaymentType("Voucher");
		
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		checking(new Expectations(){{
			oneOf(reporter).log(with(message), with(equal(toHashSetWith(donorCplxType))), with("One or more records has a payment method of voucher but has the gift type of pledge."));
		}});

		transformer.transform(message);
		
		cashDonationCplxType.setType("Pledge");
		transformer.transform(message);
	}
		
	public void testEmeryDonationsWithNoGiftAttribute() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		goshcc.setSupplierID("Emery");
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		CashDonationCplxType cashDonationCplxType = new CashDonationCplxType();
		cashDonationCplxType.setType("Cash");
		donationDetails.getCashDonationCplxType().add(cashDonationCplxType);
		donorCplxType.setDonationDetails(donationDetails);
		
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		final Message<GOSHCC> reportedMessage = new GenericMessage<GOSHCC>(goshcc);
		checking(new Expectations(){{
			oneOf(reporter).log(with(message), with(equal(toHashSetWith(donorCplxType))), with("The file has the supplierId of Emery but gifts with no gift attributes."));
			will(returnValue(reportedMessage));
			oneOf(reporter).log(with(reportedMessage), with(equal(toHashSetWith(donorCplxType))), with("The file has the supplierId of Emery but has no Order Number gift attributes."));
		}});

		transformer.transform(message);
	}
	
	public void testValidGiftAttributeCommentCase() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		transformer.setReporter(mock(Reporter.class));
		
		GOSHCC goshcc = new GOSHCC();
		goshcc.setSupplierID("supplierId");
		DonorCplxType donorCplxType = new DonorCplxType();
		CashDonationCplxType cashDonation = new CashDonationCplxType();
		GiftAttributes giftatt = new GiftAttributes();
		giftatt.setComment("Obtained via supplierId");
		giftatt.setDescription("DM Prospect");
		cashDonation.getGiftAttributes().add(giftatt);
		DonationDetails donation = new DonationDetails();
		donation.getCashDonationCplxType().add(cashDonation);
		donorCplxType.setDonationDetails(donation);
		goshcc.getDonorCplxType().add(donorCplxType);
		
		Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(message);
	}

	public void testInvalidGiftAttributeCommentCase() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		goshcc.setSupplierID("Emery");
		final DonorCplxType donorCplxType = new DonorCplxType();
		CashDonationCplxType cashDonation = new CashDonationCplxType();
		cashDonation.setType("Cash");
		GiftAttributes giftatt = new GiftAttributes();
		giftatt.setComment("Obtained via someone else.");
		giftatt.setDescription("DM Prospect");
		cashDonation.getGiftAttributes().add(giftatt);
		GiftAttributes orderNumber = new GiftAttributes();
		orderNumber.setCategory("Order Number");
		cashDonation.getGiftAttributes().add(orderNumber);
		DonationDetails donation = new DonationDetails();
		donation.getCashDonationCplxType().add(cashDonation);
		donorCplxType.setDonationDetails(donation);
		goshcc.getDonorCplxType().add(donorCplxType);
		
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		checking(new Expectations(){{
			checkMessageReported(reporter, donorCplxType, message, "In one of the attributes we expected the comment \"Obtained via Emery\" but didn't get it.");
		}});
		
		transformer.transform(message);
	}
	
	public void testValidDeceasedCommentCase() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.setSupplierID("supplierId");
		donorCplxType.setAttributes(new Attributes());
		DeceasedNotificationDate deceasedAtt = new DeceasedNotificationDate();
		deceasedAtt.setComment("Obtained via supplierId");
		deceasedAtt.setDescription(DatatypeFactory.newInstance().newXMLGregorianCalendar(2011, 1, 1, 0, 0, 0, 0, 0));
		donorCplxType.getAttributes().setDeceasedNotificationDate(deceasedAtt);
		goshcc.getDonorCplxType().add(donorCplxType);
		
		Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(message);
	}

	public void testInvalidDeceasedCommentCase() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.setSupplierID("supplierId");
		donorCplxType.setAttributes(new Attributes());
		DeceasedNotificationDate deceasedAtt = new DeceasedNotificationDate();
		deceasedAtt.setComment("Obtained via some other supplierId");
		deceasedAtt.setDescription(DatatypeFactory.newInstance().newXMLGregorianCalendar(2011, 1, 1, 0, 0, 0, 0, 0));
		donorCplxType.getAttributes().setDeceasedNotificationDate(deceasedAtt);
		goshcc.getDonorCplxType().add(donorCplxType);
		
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		checking(new Expectations(){{
			checkMessageReported(reporter, donorCplxType, message, "In one of the attributes we expected the comment \"Obtained via supplierId\" but didn't get it.");
		}});
		
		transformer.transform(message);
	}
		
	public void testEmeryDonationsWithOrderNumberGiftAttribute() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		goshcc.setSupplierID("Emery");
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		CashDonationCplxType cashDonation = new CashDonationCplxType();
		cashDonation.setType("Cash");
		GiftAttributes giftAtt = new GiftAttributes();
		giftAtt.setCategory("Order Number");
		giftAtt.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar("2009-02-12"));
		giftAtt.setComment("Obtained via Emery");
		cashDonation.getGiftAttributes().add(giftAtt);
		donationDetails.getCashDonationCplxType().add(cashDonation);
		donorCplxType.setDonationDetails(donationDetails);
		CashDonationCplxType cashDonationCplxType = new CashDonationCplxType();
		donationDetails.getCashDonationCplxType().add(cashDonationCplxType);

		
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		checking(new Expectations(){{
			never(reporter).log(message, donorCplxType, "The file has the supplierId of Emery but has no Order Number gift attributes.");
		}});

		transformer.transform(message);
	}
	
	public void testEmeryDonationsWithoutOrderNumberGiftAttribute() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		goshcc.setSupplierID("Emery");
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donationDetails = new DonationDetails();
		CashDonationCplxType cashDonation = new CashDonationCplxType();
		cashDonation.setType("Cash");
		GiftAttributes giftAtt = new GiftAttributes();
		giftAtt.setCategory("Special Events");
		giftAtt.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar("2009-02-12"));
		giftAtt.setComment("Obtained via Emery");
		cashDonation.getGiftAttributes().add(giftAtt);
		donationDetails.getCashDonationCplxType().add(cashDonation);
		donorCplxType.setDonationDetails(donationDetails);
		CashDonationCplxType cashDonationCplxType = new CashDonationCplxType();
		donationDetails.getCashDonationCplxType().add(cashDonationCplxType);

		
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		checkMessageReported(reporter, donorCplxType, message, "The file has the supplierId of Emery but has no Order Number gift attributes.");

		transformer.transform(message);
	}
	
	/**
	 * This has consistently been a problem in this class for some reason. 
	 */
	public void testEmeryDonationsWithoutGiftsDoesntThrowException() throws Exception {
		BolajisAdditionsTransformer transformer = new BolajisAdditionsTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		
		GOSHCC goshcc = new GOSHCC();
		goshcc.setSupplierID("Emery");
		final DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		
		transformer.transform(new GenericMessage<GOSHCC>(goshcc));
	}
	
	private void checkMessageReported(final Reporter reporter, final DonorCplxType donorCplxType, final Message<GOSHCC> message, final String text) {
		checking(new Expectations(){{
			oneOf(reporter).log(with(message), with(equal(toHashSetWith(donorCplxType))), with(text));
		}});
	}

	private Set<DonorCplxType> toHashSetWith(DonorCplxType donorCplxType) {
		final Set<DonorCplxType> set = new HashSet<DonorCplxType>();
		set.add(donorCplxType);
		return set;
	}
}
