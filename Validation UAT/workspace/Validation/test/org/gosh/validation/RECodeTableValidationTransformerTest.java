package org.gosh.validation;

import static ch.lambdaj.Lambda.convert;
import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.ConsCodes;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.re.dmcash.bindings.GiftAttributes;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.businessrules.RECodeTableValidationTransformer;
import org.gosh.validation.general.error.ErrorReporter;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

import ch.lambdaj.function.convert.Converter;

public class RECodeTableValidationTransformerTest extends MockObjectTestCase {
	@SuppressWarnings("unchecked")
	public void testMatchWithTitles() throws Exception {
		RECodeTableValidationTransformer transformer = new RECodeTableValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		transformer.setReporter(new ErrorReporter()); 
		
		HashMap<String, String> map = new HashMap<String, String>();
		transformer.setFieldNameToCodeMapping(map);
		
		map.put("title1", "5013");
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		donorCplxType.setTitle1("Mr");

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertTrue("Expected empty results, got " + ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())), ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	public void testMatchWithMultipleTitles() throws Exception {
		RECodeTableValidationTransformer transformer = new RECodeTableValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		transformer.setReporter(new ErrorReporter()); 
		
		HashMap<String, String> map = new HashMap<String, String>();
		transformer.setFieldNameToCodeMapping(map);
		
		map.put("title1", "5013");
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donor1 = new DonorCplxType();
		goshcc.getDonorCplxType().add(donor1);
		DonorCplxType donor2 = new DonorCplxType();
		goshcc.getDonorCplxType().add(donor2);
		donor1.setTitle1("Mr");
		donor2.setTitle1("Ms");

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertTrue("Expected empty results, got " + ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())), ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	public void testWithDeepInModelItem() throws Exception {
		RECodeTableValidationTransformer transformer = new RECodeTableValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		transformer.setReporter(new ErrorReporter()); 
		
		HashMap<String, String> map = new HashMap<String, String>();
		transformer.setFieldNameToCodeMapping(map);
		
		map.put("attributes/directMarketingType/description", "1129");
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		donorCplxType.setTitle1("Bankers Order");

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertTrue("Expected empty results, got " + ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())), ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).isEmpty());
	}

	@SuppressWarnings("unchecked")
	public void testNonMatchWithTitles() throws Exception {
		RECodeTableValidationTransformer transformer = new RECodeTableValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		transformer.setReporter(new ErrorReporter()); 

		HashMap<String, String> map = new HashMap<String, String>();
		transformer.setFieldNameToCodeMapping(map);
		transformer.setReporter(new ErrorReporter());
		
		map.put("title1", "5013");
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		donorCplxType.setTitle1("Not a title");

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertEquals("There are elements in title1 that are not in the tableentries table. The offending values were: [Not a title]",
			((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));
	}
	
	@SuppressWarnings("unchecked")
	public void testWithEmptyTitles() throws Exception {
		RECodeTableValidationTransformer transformer = new RECodeTableValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		transformer.setReporter(new ErrorReporter()); 

		HashMap<String, String> map = new HashMap<String, String>();
		transformer.setFieldNameToCodeMapping(map);
		transformer.setReporter(new ErrorReporter());
		
		map.put("title1", "5013");
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertTrue(((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	public void testInactiveCase() throws Exception {
		RECodeTableValidationTransformer transformer = new RECodeTableValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		transformer.setReporter(new ErrorReporter()); 

		HashMap<String, String> map = new HashMap<String, String>();
		transformer.setFieldNameToCodeMapping(map);
		transformer.setReporter(new ErrorReporter());
		
		map.put("title1", "5013");
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setTitle1("Mr & Mrs");
		goshcc.getDonorCplxType().add(donorCplxType);

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		//assertEquals("There are elements in title1 that are not in the tableentries table. The offending values were: [Mr & Mrs]",
		assertEquals("Number of errors do not match", 1, ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).size());
		String errorString = "There are elements in title1 that are not in the tableentries table. The offending values were: [Mr & Mrs]";
		assertEquals("Error messages do not match", errorString, ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));
		
	}	
	
	public void testCaseInsensitiveCase() throws Exception {
		RECodeTableValidationTransformer transformer = new RECodeTableValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());

		HashMap<String, String> map = new HashMap<String, String>();
		transformer.setFieldNameToCodeMapping(map);
		ErrorReporter reporter = new ErrorReporter();
		transformer.setReporter(reporter);
		
		map.put("title1", "5013");
		map.put("county", "5001");
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setTitle1("mr");
		donorCplxType.setCounty("DORSET");
		goshcc.getDonorCplxType().add(donorCplxType);

		transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertEquals("Mr", donorCplxType.getTitle1());
		assertEquals("Dorset", donorCplxType.getCounty());
	}	
	
	public void testShortDescriptionCase() throws Exception {
		RECodeTableValidationTransformer transformer = new RECodeTableValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		ErrorReporter reporter = new ErrorReporter();
		transformer.setReporter(reporter);

		HashMap<String, String> map = new HashMap<String, String>();
		transformer.setFieldNameToCodeMapping(map);
		
		map.put("consCodes/code.short", "43");
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		ConsCodes consCodes = new ConsCodes();
		consCodes.setCode("DMGen");
		donorCplxType.getConsCodes().add(consCodes);
		goshcc.getDonorCplxType().add(donorCplxType);

		transformer.transform(new GenericMessage<GOSHCC>(goshcc));
	}	
	
	@SuppressWarnings("unchecked")
	public void testGiftAttributeDescriptionCaseThatFails() throws Exception {
		RECodeTableValidationTransformer transformer = new RECodeTableValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		ErrorReporter reporter = new ErrorReporter();
		transformer.setReporter(reporter);

		HashMap<String, String> map = new HashMap<String, String>();
		transformer.setFieldNameToCodeMapping(map);
		
		map.put("donationDetails/cashDonationCplxType/giftAttributes/description", "1163");
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		GiftAttributes giftatt = new GiftAttributes();
		giftatt.setDescription("CL9224 - Monet appointment calendar");
		CashDonationCplxType cashdonation = new CashDonationCplxType();
		cashdonation.getGiftAttributes().add(giftatt);
		DonationDetails donation = new DonationDetails();
		donation.getCashDonationCplxType().add(cashdonation);
		donorCplxType.setDonationDetails(donation);
		donorCplxType.getDonationDetails();
		goshcc.getDonorCplxType().add(donorCplxType);

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertTrue(((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	public void testGiftAttributeDescriptionCaseSuccesful() throws Exception {
		RECodeTableValidationTransformer transformer = new RECodeTableValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		ErrorReporter reporter = new ErrorReporter();
		transformer.setReporter(reporter);

		HashMap<String, String> map = new HashMap<String, String>();
		transformer.setFieldNameToCodeMapping(map);
		
		map.put("donationDetails/cashDonationCplxType/giftAttributes/description", "1163");
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		GiftAttributes giftatt = new GiftAttributes();
		giftatt.setDescription("CC9112 - Holly Christmas cards");
		CashDonationCplxType cashdonation = new CashDonationCplxType();
		cashdonation.getGiftAttributes().add(giftatt);
		DonationDetails donation = new DonationDetails();
		donation.getCashDonationCplxType().add(cashdonation);
		donorCplxType.setDonationDetails(donation);
		donorCplxType.getDonationDetails();
		goshcc.getDonorCplxType().add(donorCplxType);

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertTrue(((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	public void testGiftAttributeDescriptionWhenCategoryIsOrderNumberCase() throws Exception {
		RECodeTableValidationTransformer transformer = new RECodeTableValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		ErrorReporter reporter = new ErrorReporter();
		transformer.setReporter(reporter);

		HashMap<String, String> map = new HashMap<String, String>();
		transformer.setFieldNameToCodeMapping(map);
		
		map.put("donationDetails/cashDonationCplxType/giftAttributes/description", "1163");
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		GiftAttributes giftatt = new GiftAttributes();
		giftatt.setCategory("Order Number");
		giftatt.setDescription("TT7292778");
		CashDonationCplxType cashdonation = new CashDonationCplxType();
		cashdonation.getGiftAttributes().add(giftatt);
		DonationDetails donation = new DonationDetails();
		donation.getCashDonationCplxType().add(cashdonation);
		donorCplxType.setDonationDetails(donation);
		donorCplxType.getDonationDetails();
		goshcc.getDonorCplxType().add(donorCplxType);

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertTrue(((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	public void testAttributeTypeCase() throws Exception {
		RECodeTableValidationTransformer transformer = new RECodeTableValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		ErrorReporter reporter = new ErrorReporter();
		transformer.setReporter(reporter);

		HashMap<String, String> map = new HashMap<String, String>();
		transformer.setFieldNameToCodeMapping(map);
		
		map.put("donationDetails/cashDonationCplxType/giftAttributes/category.attributeType", "1,6");
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donation = new DonationDetails();
		donorCplxType.setDonationDetails(donation);
		final CashDonationCplxType cashdonation = new CashDonationCplxType();
		donation.getCashDonationCplxType().add(cashdonation);
		GiftAttributes giftatt1 = new GiftAttributes();
		giftatt1.setCategory("Christmas Gift 2009");
		giftatt1.setDescription("CC8101 - 18 card multipack");
		cashdonation.getGiftAttributes().add(giftatt1);
		GiftAttributes giftatt2 = new GiftAttributes();
		giftatt2.setCategory("Order Number");
		giftatt2.setDescription("TT7292778");
		cashdonation.getGiftAttributes().add(giftatt2);

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertTrue(((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	public void testDeceasedAttributeTypeCase() throws Exception {
		RECodeTableValidationTransformer transformer = new RECodeTableValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		ErrorReporter reporter = new ErrorReporter();
		transformer.setReporter(reporter);

		HashMap<String, String> map = new HashMap<String, String>();
		transformer.setFieldNameToCodeMapping(map);
		
		map.put("attributes/deceasedNotificationDate/category.attributeType", "3");
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		GiftAttributes giftatt = new GiftAttributes();
		giftatt.setCategory("Christmas Gift 2009");
		CashDonationCplxType cashdonation = new CashDonationCplxType();
		cashdonation.getGiftAttributes().add(giftatt);
		DonationDetails donation = new DonationDetails();
		donation.getCashDonationCplxType().add(cashdonation);
		donorCplxType.setDonationDetails(donation);
		goshcc.getDonorCplxType().add(donorCplxType);

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertTrue(((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	public void testMultipleForSameDonor() throws Exception {
		List<String> descriptions = asList("CC8101 - 18 card multipack","invalid");
		
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		DonationDetails donation = new DonationDetails();
		donorCplxType.setDonationDetails(donation);
		final CashDonationCplxType cashdonation = new CashDonationCplxType();
		donation.getCashDonationCplxType().add(cashdonation);

		cashdonation.getGiftAttributes().addAll(
			convert(descriptions, new GiftAttributeConverter())
		);

		RECodeTableValidationTransformer transformer = new RECodeTableValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		ErrorReporter reporter = new ErrorReporter();
		transformer.setReporter(reporter);

		HashMap<String, String> map = new HashMap<String, String>();
		transformer.setFieldNameToCodeMapping(map);
		map.put("donationDetails/cashDonationCplxType/giftAttributes/description", "1163");

		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertEquals("There are elements in donationDetails/cashDonationCplxType/giftAttributes/description that are not in the tableentries table. The offending values were: [invalid]", ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));
	}
	
	class GiftAttributeConverter implements Converter<String, GiftAttributes>{
		@Override
		public GiftAttributes convert(String description) {
			GiftAttributes giftatt = new GiftAttributes();
			giftatt.setDescription(description);
			giftatt.setCategory("Christmas Gift 2009");
			return giftatt;
		}
	}
}