package org.gosh.validation.general.deduplication;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.springframework.beans.factory.annotation.Required;

/**
 * This scores data from the file against data in the database. 
 * 
 * If there is a best match it returns this. 
 * If there are more than one best matches, it returns these. 
 * If there are no matches above a threshold, it returns nothing. 
 * 
 * Any preformance gains here welcome!
 *  
 * @author Kevin.Savage
 */
public class WeightedScoringMatcher implements Matcher{
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	private List<FieldWeighting> fieldWeightings;
	private int threshold;
	
	public Set<ExtendedDataSetModel> bestMatches(DonorCplxType donor, List<ExtendedDataSetModel> possibleMatches, List<String> infoMessages){
		int maxScore = Integer.MIN_VALUE;
		Set<ExtendedDataSetModel> maximumScorers = new HashSet<ExtendedDataSetModel>();
		for (ExtendedDataSetModel extendedDataSetModel : possibleMatches) {
			int score = score(donor, extendedDataSetModel);
			if (score > threshold && score >= maxScore){
				if (score > maxScore){
					maxScore = score;
					maximumScorers.clear();
				} 
				maximumScorers.add(extendedDataSetModel);
			}
		}
		
		infoMessages.add(getInfoMessage(donor, maximumScorers, possibleMatches));
		log.info("the max score above threshold was: " + maxScore);
		
		return maximumScorers;
	}

	private String getInfoMessage(DonorCplxType donor, Set<ExtendedDataSetModel> maximumScorers, List<ExtendedDataSetModel> possibleMatches) {
		String message = "For donor " + donor.getLastName() + " " + donor.getGender() + " " + donor.getPostCode() + " we considered " + possibleMatches.size() + " options and the following were equal first above the threshold (of " + threshold + "): ";
		for (ExtendedDataSetModel entry : maximumScorers) {
			message += entry.getConstituentId() + ";";
		}
		return message;
	}
	
	public int score(DonorCplxType donor, ExtendedDataSetModel possibleMatch){
		try {
			JXPathContext context = JXPathContext.newContext(donor);
			context.setLenient(true);
			
			int score = 0;
			for (FieldWeighting fieldWeighting : fieldWeightings) {
				Object schemaValue = context.getValue(fieldWeighting.getSchemaLocation());
				Object databaseValue = PropertyUtils.getProperty(possibleMatch, fieldWeighting.getFieldToCompareTo());
				if (schemaValue!= null && // If schemaValue not null continue with other checks
					(ObjectUtils.equals(schemaValue, databaseValue) // If objects are equal jump inside the if block
							|| (databaseValue instanceof Integer && ObjectUtils.equals(schemaValue, String.valueOf(databaseValue))) // If its an integer, convert to string and check if its equal to value on file
							|| (databaseValue instanceof String && databaseValue.toString().equalsIgnoreCase(schemaValue.toString())))){ // If its string, ignore case and check if its the same
					
					if (schemaValue instanceof String && ((String)schemaValue).length() <=1 && fieldWeighting.getShortMatchWeight() != 0){
						score += fieldWeighting.getShortMatchWeight();
					} else {
						score += fieldWeighting.getWeight();
					}
				}
			}
			return score;
		} catch (Exception e) {
			throw new RuntimeException("There is a misconfiguation of weightings.", e);
		}
	}

	public List<FieldWeighting> getFieldWeightings() {
		return fieldWeightings;
	}

	@Required
	public void setFieldWeightings(List<FieldWeighting> fieldWeightings) {
		this.fieldWeightings = fieldWeightings;
	}

	public int getThreshold() {
		return threshold;
	}

	@Required
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
}
