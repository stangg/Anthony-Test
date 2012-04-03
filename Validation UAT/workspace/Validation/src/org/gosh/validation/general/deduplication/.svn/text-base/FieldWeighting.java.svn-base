package org.gosh.validation.general.deduplication;

/**
 * This is a weighting model class that is used to describe 
 * the weighting each field is given.
 * 
 * @author Kevin.Savage 
 */
public class FieldWeighting {
	private String schemaLocation;
	private String fieldToCompareTo;
	private int weight;
	private int shortMatchWeight;
	
	public FieldWeighting() {
	}

	public FieldWeighting(String schemaLocation, String fieldToCompareTo, int weight){
		this.schemaLocation = schemaLocation;
		this.fieldToCompareTo = fieldToCompareTo;
		this.weight = weight;
	}

	/**
	 * @param schemaLocation is an xpath into the schema
	 * @param fieldToCompareTo is the name in {@link ExtendedDataSetModel}
	 * @param weight is an int that gets added to the score if there is a match. 
	 * @param shortMatchWeight is a smaller number that gets added if there is a "short" match.
	 */
	public FieldWeighting(String schemaLocation, String fieldToCompareTo, int weight, int shortMatchWeight){
		this.schemaLocation = schemaLocation;
		this.fieldToCompareTo = fieldToCompareTo;
		this.weight = weight;
		this.shortMatchWeight = shortMatchWeight;
	}
	
	public String getSchemaLocation() {
		return schemaLocation;
	}
	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}
	public String getFieldToCompareTo() {
		return fieldToCompareTo;
	}
	public void setFieldToCompareTo(String fieldToCompareTo) {
		this.fieldToCompareTo = fieldToCompareTo;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	/**
	 * This is used for the case where there is a possibility that there is a "short" match that 
	 * should count for less (or more). An example would be where there is an initial that matches. 
	 * Maybe you would like J matching J to count for less than James matching James. 
	 * @param shortMatchWeight
	 */
	public void setShortMatchWeight(int shortMatchWeight) {
		this.shortMatchWeight = shortMatchWeight;
	}
	public int getShortMatchWeight() {
		return shortMatchWeight;
	}
}
