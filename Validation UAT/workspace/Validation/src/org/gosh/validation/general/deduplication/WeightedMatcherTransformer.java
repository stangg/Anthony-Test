package org.gosh.validation.general.deduplication;

import static org.gosh.validation.general.deduplication.FuzzyLookupTransformer.POSSIBLE_MATCHES;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;

/**
 * Takes the previously generated list of possibles, looks up 
 * additional data and then uses this to score them. If there is a 
 * unique high score (above a threshold), the file may have an id 
 * added. If there is a non-unique high score this is probably because there are duplicate
 * records in RE, so we report this in the file.
 * 
 * We also log how may records were considered and what the id's 
 * were if there were multiple above the threshold. We don't log it 
 * in all cases for reasons of performance. It turns out that 
 * collecting this information for all records is computationally 
 * expensive. 
 * 
 * @author Kevin.Savage
 */
public class WeightedMatcherTransformer {
	private Matcher matcher;
	private Reporter reporter;
	private ExtendedDatasetLookup dao; 
	
	@SuppressWarnings("unchecked")
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) {
		Map<DonorCplxType, List<PossibleMatchModel>> possibles = (Map<DonorCplxType, List<PossibleMatchModel>>) message.getHeaders().get(POSSIBLE_MATCHES);
		if (possibles==null||possibles.isEmpty()){
			return message;
		}
		HashMap<DonorCplxType, List<ExtendedDataSetModel>> extended = dao.lookup(possibles);
		
		List<String> errorMessages = new ArrayList<String>();
		List<String> infoMessages = new ArrayList<String>();
		for (DonorCplxType donor : extended.keySet()) {
			List<ExtendedDataSetModel> list = extended.get(donor);
			Set<ExtendedDataSetModel> bestMatches = matcher.bestMatches(donor, list, infoMessages);
			Set<String> reducedIds = reduce(bestMatches);
			if (reducedIds.size()>1){
				String duplicateIds = "";
				for (String id : reducedIds) {
					duplicateIds += (id + ",");
				}
				errorMessages.add("There appear to be duplicate records in RE. These have ID's: " + duplicateIds);
			} else if (reducedIds.size() == 1){
				ExtendedDataSetModel extendedDataSetModel = bestMatches.iterator().next();
				if (extendedDataSetModel.getDeceased() == -1){
					addResultDoesntLookQuiteRightMessage("deceased", errorMessages, donor, extendedDataSetModel);
				} else if (extendedDataSetModel.getInactive() == -1){
					addResultDoesntLookQuiteRightMessage("inactive", errorMessages, donor, extendedDataSetModel);
				}
				
				donor.setConstituentID(extendedDataSetModel.getConstituentId());
				donor.setInternalConstitID(String.valueOf(extendedDataSetModel.getId()));
			}
		}
		
		return reporter.log(message, infoMessages, errorMessages);
	}

	private Set<String> reduce(Set<ExtendedDataSetModel> bestMatches) {
		HashSet<String> ids = new HashSet<String>();
		for (ExtendedDataSetModel extendedDataSetModel : bestMatches) {
			ids.add(extendedDataSetModel.getConstituentId());
		}
		return ids;
	}

	private void addResultDoesntLookQuiteRightMessage(String thingThatIsWrong, List<String> errorMessages, DonorCplxType donor, ExtendedDataSetModel extendedDataSetModel) {
		errorMessages.add("Our best match appears to be " + thingThatIsWrong + " for " + donor.getLastName() + " " + donor.getPostCode() + ". The match was " + extendedDataSetModel.getConstituentId() + ".");
	}

	@Required
	public void setMatcher(Matcher matcher) {
		this.matcher = matcher;
	}
	
	@Required
	@Autowired
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
	
	public void setDao(ExtendedDatasetLookup dao) {
		this.dao = dao;
	}
}
