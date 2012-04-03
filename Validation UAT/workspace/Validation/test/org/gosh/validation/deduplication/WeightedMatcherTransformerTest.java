package org.gosh.validation.deduplication;

import static org.gosh.validation.general.deduplication.FuzzyLookupTransformer.POSSIBLE_MATCHES;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.deduplication.ExtendedDataSetModel;
import org.gosh.validation.general.deduplication.ExtendedDatasetLookup;
import org.gosh.validation.general.deduplication.Matcher;
import org.gosh.validation.general.deduplication.PossibleMatchModel;
import org.gosh.validation.general.deduplication.WeightedMatcherTransformer;
import org.gosh.validation.general.error.ErrorReporter;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

public class WeightedMatcherTransformerTest extends MockObjectTestCase{
	private ExtendedDatasetLookup lookup;
	private Matcher matcher;

	public void testWeightedUniqueMatch() throws Exception {
		WeightedMatcherTransformer transformer = setupTransformer();
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donor = new DonorCplxType();
		goshcc.getDonorCplxType().add(donor);
		
		final HashMap<DonorCplxType, List<PossibleMatchModel>> possibles = new HashMap<DonorCplxType, List<PossibleMatchModel>>();
		possibles.put(null, null);
		
		ExtendedDataSetModel extendedPossibleMatchModel = new ExtendedDataSetModel();
		extendedPossibleMatchModel.setConstituentId("constituentId");
		extendedPossibleMatchModel.setId(12345);
		final List<ExtendedDataSetModel> extendedPossibleMatches = Collections.singletonList(extendedPossibleMatchModel);
		final Set<ExtendedDataSetModel> extendedPossibleMatchesSet = Collections.singleton(extendedPossibleMatchModel);
		final HashMap<DonorCplxType, List<ExtendedDataSetModel>> extendedPossibles = new HashMap<DonorCplxType, List<ExtendedDataSetModel>>();
		extendedPossibles.put(donor, extendedPossibleMatches);
		
		Message<GOSHCC> message = MessageBuilder
			.withPayload(goshcc)
			.setHeader(POSSIBLE_MATCHES, possibles)
			.build();
		
		checking(new Expectations(){{
			oneOf(lookup).lookup(possibles);
			will(returnValue(extendedPossibles));
			oneOf(matcher).bestMatches(donor, extendedPossibleMatches, new ArrayList<String>());
			will(returnValue(extendedPossibleMatchesSet));
			
		}});

		Message<GOSHCC> transformedMessage = transformer.transform(message);
		
		checkDonorWasPopulatedWithIds(donor);
		checkNoErrorMessages(transformedMessage);
	}


	
	public void testDeceasedIsBestMatch() throws Exception {
		WeightedMatcherTransformer transformer = setupTransformer();
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donor = new DonorCplxType();
		donor.setLastName("LastName");
		donor.setPostCode("PostCode");
		goshcc.getDonorCplxType().add(donor);
		
		final HashMap<DonorCplxType, List<PossibleMatchModel>> possibles = new HashMap<DonorCplxType, List<PossibleMatchModel>>();
		possibles.put(null, null);
		
		ExtendedDataSetModel extendedPossibleMatchModel = new ExtendedDataSetModel();
		extendedPossibleMatchModel.setConstituentId("constituentId");
		extendedPossibleMatchModel.setId(12345);
		extendedPossibleMatchModel.setDeceased(-1);
		final List<ExtendedDataSetModel> extendedPossibleMatches = Collections.singletonList(extendedPossibleMatchModel);
		final Set<ExtendedDataSetModel> extendedPossibleMatchesSet = Collections.singleton(extendedPossibleMatchModel);
		final HashMap<DonorCplxType, List<ExtendedDataSetModel>> extendedPossibles = new HashMap<DonorCplxType, List<ExtendedDataSetModel>>();
		extendedPossibles.put(donor, extendedPossibleMatches);
		
		Message<GOSHCC> message = MessageBuilder
			.withPayload(goshcc)
			.setHeader(POSSIBLE_MATCHES, possibles)
			.build();
		
		checking(new Expectations(){{
			oneOf(lookup).lookup(possibles);
			will(returnValue(extendedPossibles));
			oneOf(matcher).bestMatches(donor, extendedPossibleMatches, new ArrayList<String>());
			will(returnValue(extendedPossibleMatchesSet));
			
		}});

		Message<GOSHCC> transformedMessage = transformer.transform(message);
		
		checkDonorWasPopulatedWithIds(donor);
		checkFirstErrorMessage(transformedMessage, "Our best match appears to be deceased for LastName PostCode. The match was constituentId." );
	}
	
	public void testInactiveIsBestMatch() throws Exception {
		WeightedMatcherTransformer transformer = setupTransformer();
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donor = new DonorCplxType();
		donor.setLastName("LastName");
		donor.setPostCode("PostCode");
		goshcc.getDonorCplxType().add(donor);
		
		final HashMap<DonorCplxType, List<PossibleMatchModel>> possibles = new HashMap<DonorCplxType, List<PossibleMatchModel>>();
		possibles.put(null, null);
		
		ExtendedDataSetModel extendedPossibleMatchModel = new ExtendedDataSetModel();
		extendedPossibleMatchModel.setConstituentId("constituentId");
		extendedPossibleMatchModel.setId(12345);
		extendedPossibleMatchModel.setInactive(-1);
		final List<ExtendedDataSetModel> extendedPossibleMatches = Collections.singletonList(extendedPossibleMatchModel);
		final Set<ExtendedDataSetModel> extendedPossibleMatchesSet = Collections.singleton(extendedPossibleMatchModel);
		final HashMap<DonorCplxType, List<ExtendedDataSetModel>> extendedPossibles = new HashMap<DonorCplxType, List<ExtendedDataSetModel>>();
		extendedPossibles.put(donor, extendedPossibleMatches);
		
		Message<GOSHCC> message = MessageBuilder
			.withPayload(goshcc)
			.setHeader(POSSIBLE_MATCHES, possibles)
			.build();
		
		checking(new Expectations(){{
			oneOf(lookup).lookup(possibles);
			will(returnValue(extendedPossibles));
			oneOf(matcher).bestMatches(donor, extendedPossibleMatches, new ArrayList<String>());
			will(returnValue(extendedPossibleMatchesSet));
			
		}});

		Message<GOSHCC> transformedMessage = transformer.transform(message);
		
		checkDonorWasPopulatedWithIds(donor);
		checkFirstErrorMessage(transformedMessage, "Our best match appears to be inactive for LastName PostCode. The match was constituentId.");
	}
	
	public void testWeightedMultipleMatch() throws Exception{
		WeightedMatcherTransformer transformer = setupTransformer();
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donor = new DonorCplxType();
		goshcc.getDonorCplxType().add(donor);
		
		final HashMap<DonorCplxType, List<PossibleMatchModel>> possibles = new HashMap<DonorCplxType, List<PossibleMatchModel>>();
		possibles.put(null, null);
		final HashMap<DonorCplxType, List<ExtendedDataSetModel>> extendedPossibles = new HashMap<DonorCplxType, List<ExtendedDataSetModel>>();
		ExtendedDataSetModel possibleMatchModel = createModelFor("constituentId1", 12345);
		ExtendedDataSetModel secondPossibleMatchModel = createModelFor("constituentId2", 12345);
		
		final List<ExtendedDataSetModel> possibleMatches = new ArrayList<ExtendedDataSetModel>();
		possibleMatches.add(possibleMatchModel);
		possibleMatches.add(secondPossibleMatchModel);

		final Set<ExtendedDataSetModel> possibleMatchesSet = new HashSet<ExtendedDataSetModel>();
		possibleMatchesSet.addAll(possibleMatches);
		
		extendedPossibles.put(donor, possibleMatches);
		
		Message<GOSHCC> message = MessageBuilder
			.withPayload(goshcc)
			.setHeader(POSSIBLE_MATCHES, possibles)
			.build();
		
		checking(new Expectations(){{
			oneOf(lookup).lookup(possibles);
			will(returnValue(extendedPossibles));
			oneOf(matcher).bestMatches(donor, possibleMatches, new ArrayList<String>());
			will(returnValue(possibleMatchesSet));
		}});

		Message<GOSHCC> transformedMessage = transformer.transform(message);
		
		checkDidntGetAMatch(donor);
		checkFirstErrorMessage(transformedMessage, "There appear to be duplicate records in RE. These have ID's: constituentId1,constituentId2,");
	}

	public void testWeightedMultipleMatchSameId() throws Exception{
		WeightedMatcherTransformer transformer = setupTransformer();
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donor = new DonorCplxType();
		goshcc.getDonorCplxType().add(donor);
		
		final HashMap<DonorCplxType, List<PossibleMatchModel>> possibles = new HashMap<DonorCplxType, List<PossibleMatchModel>>();
		possibles.put(null, null);
		final HashMap<DonorCplxType, List<ExtendedDataSetModel>> extendedPossibles = new HashMap<DonorCplxType, List<ExtendedDataSetModel>>();
		ExtendedDataSetModel possibleMatchModel = createModelFor("constituentId", 12345);
		ExtendedDataSetModel secondPossibleMatchModel = createModelFor("constituentId", 12345);
		
		final List<ExtendedDataSetModel> possibleMatches = new ArrayList<ExtendedDataSetModel>();
		possibleMatches.add(possibleMatchModel);
		possibleMatches.add(secondPossibleMatchModel);
		
		final Set<ExtendedDataSetModel> possibleMatchesSet = new HashSet<ExtendedDataSetModel>();
		possibleMatchesSet.addAll(possibleMatches);
		
		extendedPossibles.put(donor, possibleMatches);
		
		Message<GOSHCC> message = MessageBuilder
			.withPayload(goshcc)
			.setHeader(POSSIBLE_MATCHES, possibles)
			.build();
		
		checking(new Expectations(){{
			oneOf(lookup).lookup(possibles);
			will(returnValue(extendedPossibles));
			oneOf(matcher).bestMatches(donor, possibleMatches, new ArrayList<String>());
			will(returnValue(possibleMatchesSet));
		}});

		Message<GOSHCC> transformedMessage = transformer.transform(message);
		
		checkDonorWasPopulatedWithIds(donor);
		checkNoErrorMessages(transformedMessage);
	}

	public void testNoMatch() throws Exception {
		WeightedMatcherTransformer transformer = setupTransformer();
		
		GOSHCC goshcc = new GOSHCC();
		final DonorCplxType donor = new DonorCplxType();
		goshcc.getDonorCplxType().add(donor);
		

		final HashMap<DonorCplxType, List<PossibleMatchModel>> possibles = new HashMap<DonorCplxType, List<PossibleMatchModel>>();
		possibles.put(null, null);
		final HashMap<DonorCplxType, List<ExtendedDataSetModel>> extendedPossibles = new HashMap<DonorCplxType, List<ExtendedDataSetModel>>();
		final List<ExtendedDataSetModel> possibleMatches = Collections.emptyList();
		extendedPossibles.put(donor, possibleMatches);
		
		Message<GOSHCC> message = MessageBuilder
			.withPayload(goshcc)
			.setHeader(POSSIBLE_MATCHES, possibles)
			.build();
		
		checking(new Expectations(){{
			oneOf(lookup).lookup(possibles);
			will(returnValue(extendedPossibles));
			oneOf(matcher).bestMatches(donor, possibleMatches, new ArrayList<String>());
			will(returnValue(Collections.emptySet()));
		}});

		Message<GOSHCC> transformedMessage = transformer.transform(message);
		
		checkDidntGetAMatch(donor);
		checkNoErrorMessages(transformedMessage);
	}
	
	@SuppressWarnings("unchecked")
	private void checkFirstErrorMessage(Message<GOSHCC> transformedMessage, String errorMessage) {
		List<String> errorMessages = (List<String>) transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		assertEquals(errorMessage , errorMessages.get(0));
	}

	private ExtendedDataSetModel createModelFor(String constId, int intId) {
		ExtendedDataSetModel secondPossibleMatchModel = new ExtendedDataSetModel();
		secondPossibleMatchModel.setConstituentId(constId);
		secondPossibleMatchModel.setId(intId);
		return secondPossibleMatchModel;
	}
	
	@SuppressWarnings("unchecked")
	private void checkNoErrorMessages(Message<GOSHCC> transformedMessage) {
		List<String> errorMessages = (List<String>) transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		assertTrue(errorMessages.isEmpty());
	}
	
	private WeightedMatcherTransformer setupTransformer() {
		WeightedMatcherTransformer transformer = new WeightedMatcherTransformer();
		matcher = mock(Matcher.class);
		transformer.setMatcher(matcher);
		lookup = mock(ExtendedDatasetLookup.class);
		transformer.setDao(lookup);
		transformer.setReporter(new ErrorReporter());
		return transformer;
	}
	
	private void checkDidntGetAMatch(final DonorCplxType donor) {
		assertNull(donor.getConstituentID());
		assertNull(donor.getInternalConstitID());
	}
	
	private void checkDonorWasPopulatedWithIds(final DonorCplxType donor) {
		assertEquals("constituentId", donor.getConstituentID());
		assertEquals("12345", donor.getInternalConstitID());
	}
}
