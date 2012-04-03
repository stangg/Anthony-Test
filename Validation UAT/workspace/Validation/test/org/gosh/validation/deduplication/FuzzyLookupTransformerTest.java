package org.gosh.validation.deduplication;

import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.validation.TestDataSourceFactory;
import org.gosh.validation.general.deduplication.FuzzyLookupTransformer;
import org.gosh.validation.general.deduplication.PossibleMatchModel;
import org.gosh.validation.general.error.ErrorReporter;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

public class FuzzyLookupTransformerTest extends TestCase {
	public void testAllCasesTogetherSoThatWeOnlyBuildTheIndexOnce() {
		//Matches "Murphy" checking with an actual fuzzy match.
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType matchingDonor = getDonor("Murfhy", "BT5 6BA", "Female");
		goshcc.getDonorCplxType().add(matchingDonor);
		DonorCplxType nonMatchingDonor = getDonor("SOMETHING THAT CAN REALLY REALLY NOT HAPPEN IN THE REAL WORLD", "ZZZZHHHHSOMETHINGBLAH", "Male");
		goshcc.getDonorCplxType().add(nonMatchingDonor);
		DonorCplxType matchingDonorWithProblemCharacterInName = getDonor("Denham-Davis", "SW 9TT", "Unknown");
		goshcc.getDonorCplxType().add(matchingDonorWithProblemCharacterInName);
		
		// These are the cases that dez was specifically looking at.
		// They give quite a good indication of what is required and should all resolve to constid 09889429.
		DonorCplxType case1 = getDonor("Mill", "LU7 8AB", "Female");
		goshcc.getDonorCplxType().add(case1);		
		DonorCplxType case2 = getDonor("Mill", "LU7 0HR", "Female");
		goshcc.getDonorCplxType().add(case2);		
		DonorCplxType case3 = getDonor("Mills", "LU7 8AB", "Male");
		goshcc.getDonorCplxType().add(case3);		
		DonorCplxType case4 = getDonor("Mills", "LU7 8AB", "Female");
		goshcc.getDonorCplxType().add(case4);		
		DonorCplxType case5 = getDonor("Mills", "LU7 8AB", "Male");
		goshcc.getDonorCplxType().add(case5);		
		DonorCplxType case6 = getDonor("Mills", "LU7 8AB", "Unknown");
		goshcc.getDonorCplxType().add(case6);
		DonorCplxType case7 = getDonor("MILLS", "LU7 8AB", "Female");
		goshcc.getDonorCplxType().add(case7);
		
		HashMap<DonorCplxType, List<PossibleMatchModel>> fuzzyMatches = getFuzzyMapFromMessage(goshcc);
//		assertFalse(fuzzyMatches.get(matchingDonor).isEmpty());
//		assertEquals("40445162", fuzzyMatches.get(matchingDonor).get(0).getConstituentId());
//		assertNotNull(fuzzyMatches.get(matchingDonor).get(0).getInternalId());
//		
//		assertTrue("Expected no results but got the following: " + fuzzyMatches.get(nonMatchingDonor), fuzzyMatches.get(nonMatchingDonor).isEmpty());
//
//		assertFalse(fuzzyMatches.get(matchingDonorWithProblemCharacterInName).isEmpty());
//		assertEquals("40533643", fuzzyMatches.get(matchingDonorWithProblemCharacterInName).get(0).getConstituentId());
//		
//		assertTrue("Expected to get 09889429 but got the following: " + fuzzyMatches.get(case1), contains("09889429", fuzzyMatches.get(case1)));
//		assertTrue("Expected to get 09889429 but got the following: " + fuzzyMatches.get(case2), contains("09889429", fuzzyMatches.get(case2)));
//		assertTrue("Expected to get 09889429 but got the following: " + fuzzyMatches.get(case3), contains("09889429", fuzzyMatches.get(case3)));
//		assertTrue("Expected to get 09889429 but got the following: " + fuzzyMatches.get(case4), contains("09889429", fuzzyMatches.get(case4)));
//		assertTrue("Expected to get 09889429 but got the following: " + fuzzyMatches.get(case5), contains("09889429", fuzzyMatches.get(case5)));
//		assertTrue("Expected to get 09889429 but got the following: " + fuzzyMatches.get(case6), contains("09889429", fuzzyMatches.get(case6)));
//		assertTrue("Expected to get 09889429 but got the following: " + fuzzyMatches.get(case7), contains("09889429", fuzzyMatches.get(case7)));
	}
	
	private boolean contains(String id, List<PossibleMatchModel> possibleMatchModels) {
		for (PossibleMatchModel possibleMatchModel : possibleMatchModels) {
			if (possibleMatchModel.getConstituentId().equals(id)){
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private HashMap<DonorCplxType, List<PossibleMatchModel>> getFuzzyMapFromMessage(GOSHCC goshcc) {
		FuzzyLookupTransformer fuzzyLookupTransformer = new FuzzyLookupTransformer();
		fuzzyLookupTransformer.setDataSource(TestDataSourceFactory.getDataSource());
		fuzzyLookupTransformer.setReporter(new ErrorReporter());
		fuzzyLookupTransformer.setFuzzymatchiness("0.636");
		
		GenericMessage<GOSHCC> originalMessage = new GenericMessage<GOSHCC>(goshcc);
		Message<GOSHCC> transformedMessage = fuzzyLookupTransformer.transform(originalMessage);
		
		return (HashMap<DonorCplxType, List<PossibleMatchModel>>) 
			transformedMessage.getHeaders().get("possibleMatches");
	}
	
	private DonorCplxType getDonor(String lastname, String postcode, String gender) {
		DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setLastName(lastname);
		donorCplxType.setPostCode(postcode);
		donorCplxType.setGender(gender);
		return donorCplxType;
	}
}
