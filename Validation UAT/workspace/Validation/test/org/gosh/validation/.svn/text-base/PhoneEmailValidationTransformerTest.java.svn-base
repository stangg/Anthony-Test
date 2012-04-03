package org.gosh.validation;

import static org.hamcrest.collection.IsCollectionContaining.hasItem;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.PhoneEmail;
import org.gosh.validation.general.businessrules.PhoneEmailValidationTransformer;
import org.gosh.validation.general.error.Reporter;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.springframework.integration.message.GenericMessage;

public class PhoneEmailValidationTransformerTest extends MockObjectTestCase {
	public void testValidPhoneNumber() throws Exception {
		PhoneEmailValidationTransformer transformer = new PhoneEmailValidationTransformer();
		transformer.setErrorReporter(mock(Reporter.class));
		GenericMessage<GOSHCC> message = getModelWithPhone("Home", "079007900790");
		transformer.transform(message);
	}
	
	public void testValidPhoneNumberFromRealWorld() throws Exception {
		PhoneEmailValidationTransformer transformer = new PhoneEmailValidationTransformer();
		transformer.setErrorReporter(mock(Reporter.class));
		GenericMessage<GOSHCC> message = getModelWithPhone("Mobile", "07747 861717");
		transformer.transform(message);
	}

	public void testStripsWhitespaceFromPhoneNumbers() throws Exception {
		PhoneEmailValidationTransformer transformer = new PhoneEmailValidationTransformer();
		transformer.setErrorReporter(mock(Reporter.class));
		GenericMessage<GOSHCC> message = getModelWithPhone("Mobile", "07747 861717");
		transformer.transform(message);
		
		assertEquals("07747861717", getFirstPhoneNumber(message));
	}

	private String getFirstPhoneNumber(GenericMessage<GOSHCC> message) {
		return getFirstDonor(message).getPhoneEmail().get(0).getValue();
	}

	private DonorCplxType getFirstDonor(GenericMessage<GOSHCC> message) {
		return message.getPayload().getDonorCplxType().get(0);
	}

	public void testInvalidPhoneNumber() throws Exception {
		PhoneEmailValidationTransformer transformer = new PhoneEmailValidationTransformer();
		Reporter reporter = mock(Reporter.class);
		transformer.setErrorReporter(reporter);
		GenericMessage<GOSHCC> message = getModelWithPhone("Mobile", "kevin@savage.org");
		setupInvalidCheck(message, reporter, "One of the phones or emails are blank.");
		transformer.transform(message);
	}

	public void testBlankPhoneNumber() throws Exception {
		PhoneEmailValidationTransformer transformer = new PhoneEmailValidationTransformer();
		Reporter reporter = mock(Reporter.class);
		transformer.setErrorReporter(reporter);
		GenericMessage<GOSHCC> message = getModelWithPhone("Business", "");
		setupInvalidCheck(message, reporter, "One of the phones or emails are blank.");
		transformer.transform(message);
	}
	
	public void testValidEmail() throws Exception {
		PhoneEmailValidationTransformer transformer = new PhoneEmailValidationTransformer();
		transformer.setErrorReporter(mock(Reporter.class));
		GenericMessage<GOSHCC> message = getModelWithPhone("Email", "kevin@savage.org");
		transformer.transform(message);
	}
	
	public void testBlankEmail() throws Exception {
		PhoneEmailValidationTransformer transformer = new PhoneEmailValidationTransformer();
		Reporter reporter = mock(Reporter.class);
		transformer.setErrorReporter(reporter);
		GenericMessage<GOSHCC> message = getModelWithPhone("Email", "");
		setupInvalidCheck(message, reporter, "One of the phones or emails are blank.");
		transformer.transform(message);
	}
	
	public void testMultipleWithSameType() throws Exception {
		PhoneEmailValidationTransformer transformer = new PhoneEmailValidationTransformer();
		Reporter reporter = mock(Reporter.class);
		transformer.setErrorReporter(reporter);

		GenericMessage<GOSHCC> message = getModelWithPhone("Mobile", "07747 861717");
		addPhoneToDonor("Mobile", "07747 861718", getFirstDonor(message));
		
		setupInvalidCheck(message, reporter, "There are duplicate phone types.");
		
		transformer.transform(message);
	}

	public void testMultipleWithDifferentType() throws Exception {
		GenericMessage<GOSHCC> message = getModelWithPhone("Mobile", "07747 861717");
		addPhoneToDonor("Home", "07747 861718", getFirstDonor(message));
		
		PhoneEmailValidationTransformer transformer = new PhoneEmailValidationTransformer();
		transformer.transform(message);
	}
	
	public void testCanReturnMultipleMessages() throws Exception {
		PhoneEmailValidationTransformer transformer = new PhoneEmailValidationTransformer();
		final Reporter reporter = mock(Reporter.class);
		transformer.setErrorReporter(reporter);

		final GenericMessage<GOSHCC> message = getModelWithPhone("Mobile", "07747 861717");
		addPhoneToDonor("Mobile", "07747 861718", getFirstDonor(message));
		addPhoneToDonor("Mobile", "", getFirstDonor(message));
		
		setupInvalidCheck(message, reporter, "There are duplicate phone types.");
		setupInvalidCheck(message, reporter, "One of the phones or emails are blank.");
		
		transformer.transform(message);		
	}

	private void setupInvalidCheck(final GenericMessage<GOSHCC> message, final Reporter reporter, final String errorMessage) {
		checking(new Expectations(){{
			oneOf(reporter).log(with(message), with(hasItem(getFirstDonor(message))), with(errorMessage));
			will(returnValue(message)); // this is just so we can know what the message created by the mock is. 
		}});
	}
	
	private GenericMessage<GOSHCC> getModelWithPhone(String type, String number) {
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		addPhoneToDonor(type, number, donorCplxType);
		GenericMessage<GOSHCC> genericMessage = new GenericMessage<GOSHCC>(goshcc);
		return genericMessage;
	}

	private void addPhoneToDonor(String type, String number, DonorCplxType donorCplxType) {
		PhoneEmail phoneEmail = new PhoneEmail();
		donorCplxType.getPhoneEmail().add(phoneEmail);
		phoneEmail.setType(type);
		phoneEmail.setValue(number);
	}
}
