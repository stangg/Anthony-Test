package org.gosh.schemagenerator;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;


/**
 * <p>TributeCR: Changed the SchemaGeneratorTool to adapt to the generic schema change that made ConstituentAppeal/Response optional. 
 * <p>A routine was added to make the Response type mandatory for all the generated schemas but the Tributes one.
 * @author Maria.Urso
 *
 */
public class SchemaGeneratorTool {
	private static final String ELEMENT = "<xs:element name=\"";
	private static final String ELEMENTENUM = "<xs:enumeration value=\"";

	public static void main(String[] args) throws IOException {
		String mainSchemaContent = FileUtils.readFileToString(new File("RaisersEdgeImportSchema_v1.xsd"));
		
		FileUtils.writeStringToFile(new File("Direct Debit Import Schema.xsd"), buildDDSchema(mainSchemaContent));
		FileUtils.writeStringToFile(new File("Cash Import Schema.xsd"), buildCashSchema(mainSchemaContent));
		FileUtils.writeStringToFile(new File("Challenge Cash Import Schema.xsd"), buildChallengeCashSchema(mainSchemaContent));
		FileUtils.writeStringToFile(new File("Prospects Debit Import Schema.xsd"), buildProspectsSchema(mainSchemaContent));
		FileUtils.writeStringToFile(new File("Tribute Import Schema.xsd"), buildTributeSchema(mainSchemaContent));
		FileUtils.writeStringToFile(new File("Direct Debit Upgrade Schema.xsd"), buildUpgradeDDSchema(mainSchemaContent));
		FileUtils.writeStringToFile(new File("Xmas Customer Schema.xsd"), buildXmasCustomerSchema(mainSchemaContent));
		FileUtils.writeStringToFile(new File("Xmas Supression Schema.xsd"), buildXmasSupressionSchema(mainSchemaContent));
	}
	
	// Schema Builder methods
	private static String buildChallengeCashSchema(String mainSchemaContent) {
		String result = mainSchemaContent;
		result = removeTag(result, "Relationship");
		result = makeTagMandatory(result, "Response");
		result = removeTag(result, "TributeID");
		result = removeTag(result, "Tribute");
		result = removeDeceasedTags(result);
		result = removeTag(result, "AddressType");
		result = removeTag(result, "Action");
		result = removeTag(result, "NonConstituentOrganisationRelationship");
		result = removeTag(result, "DirectDebitDonationCplxType");
		result = removeTag(result, "Reference");
		result = makeTagMandatory(result, "DirectMarketingType");
		result = makeGroupTagMandatory(result, "Address");
		return result;
	}

	private static String buildDDSchema(String mainSchemaContent) {
		String result = mainSchemaContent;
		result = removeTag(result, "Relationship");
		result = makeTagMandatory(result, "Response");
		result = removeTag(result, "TributeID");
		result = removeTag(result, "Tribute");
		result = removeDeceasedTags(result);
		result = removeTag(result, "AddressType");
		result = removeTag(result, "Action");
		result = removeTag(result, "NonConstituentOrganisationRelationship");
		result = removeTag(result, "CashDonationCplxType");
		result = removeTag(result, "ConstituentBankID");
		result = removeTag(result, "BankID");
		result = removeTag(result, "SchoolOrYouthType");
		result = makeTagMandatory(result, "DonationDetails");
		result = makeTagMandatory(result, "DirectMarketingType");
		result = makeTagMandatory(result, "DonorBankCplxType");
		result = makeGroupTagMandatory(result, "Address");
		result = removeEnum(result, "Registered");
		return result;
	}

	private static String buildUpgradeDDSchema(String mainSchemaContent) {
		String result = mainSchemaContent;
		result = removeTag(result, "Relationship");
		result = makeTagMandatory(result, "Response");
		result = removeTag(result, "TributeID");
		result = removeTag(result, "Tribute");
		result = removeDeceasedTags(result);
		result = removeTag(result, "AddressType");
		result = removeTag(result, "Action");
		result = removeTag(result, "NonConstituentOrganisationRelationship");
		result = removeTag(result, "CashDonationCplxType");
		result = removeTag(result, "ConstituentBankID");
		result = removeTag(result, "BankID");
		result = removeTag(result, "SchoolOrYouthType");
		result = makeTagMandatory(result, "DonationDetails");
		result = makeTagMandatory(result, "DirectMarketingType");
		result = makeGroupTagMandatory(result, "Address");
		result = removeEnum(result, "Registered");
		return result;
	}
	
	private static String buildCashSchema(String mainSchemaContent) {
		String result = mainSchemaContent;
		result = removeTag(result, "Relationship");
		result = makeTagMandatory(result, "Response");
		result = removeTag(result, "TributeID");
		result = removeTag(result, "Tribute");
		result = removeDeceasedTags(result);
		result = removeTag(result, "AddressType");
		result = removeTag(result, "Action");
		result = removeTag(result, "NonConstituentOrganisationRelationship");
		result = removeTag(result, "DirectDebitDonationCplxType");
		result = makeTagMandatory(result, "DirectMarketingType");
		result = makeTagMandatory(result, "DonationDetails");
		result = makeGroupTagMandatory(result, "Address");
		result = removeEnum(result, "Registered");
		return result;
	}

	private static String buildProspectsSchema(String mainSchemaContent) {
		String result = mainSchemaContent;
		result = removeTag(result, "Relationship");
		result = makeTagMandatory(result, "Response");
		result = removeTag(result, "TributeID");
		result = removeTag(result, "Tribute");
		result = removeDeceasedTags(result);
		result = removeTag(result, "AddressType");
		result = removeTag(result, "Action");
		result = removeTag(result, "NonConstituentOrganisationRelationship");
		result = removeTag(result, "DonationDetails");
		result = removeTag(result, "SchoolOrYouthType");
		result = makeTagMandatory(result, "DirectMarketingType");
		result = makeGroupTagMandatory(result, "Address");
		result = removeEnum(result, "Registered");
		return result;
	}
	
	private static String buildTributeSchema(String mainSchemaContent) {
		String result = mainSchemaContent;
		result = removeTag(result, "DirectDebitDonationCplxType");
		result = removeTag(result, "DirectMarketingType");
		result = removeTag(result, "Reference");
		result = removeTag(result, "Deceased");
		result = removeTag(result, "DeceasedNotificationDate");
		result = removeTag(result, "AddressType");
		result = removeTag(result, "Action");
		result = removeTag(result, "SchoolOrYouthType");
		result = removeTag(result, "NonConstituentOrganisationRelationship");
		result = makeTagMandatory(result, "Tribute");
		result = removeEnum(result, "Registered");
		return result;
	}
	
	private static String buildXmasCustomerSchema(String mainSchemaContent) {
		String result = mainSchemaContent;
		result = removeTag(result, "Relationship");
		result = makeTagMandatory(result, "Response");
		result = removeTag(result, "TributeID");
		result = removeTag(result, "Tribute");
		result = removeDeceasedTags(result);
		result = removeTag(result, "DirectDebitDonationCplxType");
		result = removeTag(result, "Reference");
		result = makeTagMandatory(result, "DonationDetails");
		result = removeTag(result, "DirectMarketingType");
		result = removeTag(result, "Action");
		result = removeTag(result, "SchoolOrYouthType");
		result = makeGroupTagMandatory(result, "Address");
		result = removeEnum(result, "Registered");
		return result;
	}

	private static String buildXmasSupressionSchema(String mainSchemaContent) {
		String result = mainSchemaContent;
		result = removeTag(result, "Relationship");
		result = makeTagMandatory(result, "Response");
		result = removeTag(result, "TributeID");
		result = removeTag(result, "Tribute");
		result = removeTag(result, "DonationDetails");
		result = removeTag(result, "DeceasedDate");
		result = removeTag(result, "DirectMarketingType");
		result = removeTag(result, "Action");
		result = removeTag(result, "SchoolOrYouthType");
		result = makeGroupTagMandatory(result, "Address");
		result = removeEnum(result, "Registered");
		return result;
	}
	
	
	/** This method makes mandatory a specific tag.
	 * @param result - Expects the main generic schema content as a String.
	 * @param tagName - The name of the Type to convert as mandatory
	 * @return
	 */
	private static String makeGroupTagMandatory(String result, String tagName) {
		String contentOfTag = getContentOfTag("<xs:group ref=\"", result, tagName);
		int indexOfMinOccurs = StringUtils.indexOf(contentOfTag, "minOccurs=\"0\"");
		
		if (indexOfMinOccurs >0){
			int startOfStartTag = findStartOfStartTag("<xs:group ref=\"", result, tagName);
			result = StringUtils.substring(result, 0, startOfStartTag + indexOfMinOccurs)
			 + StringUtils.substring(result, startOfStartTag + indexOfMinOccurs + "minOccurs=\"0\"".length(), result.length());
		}
		
		return result;
	}

	
	/** This method makes mandatory a specific tag.
	 * @param result - Expects the main generic schema content as a String.
	 * @param tagName - The name of the Type to convert as mandatory
	 * @return
	 */
	private static String makeTagMandatory(String result, String tagName) {
		String contentOfTag = getContentOfTag(result, tagName);
		int indexOfMinOccurs = StringUtils.indexOf(contentOfTag, "minOccurs=\"0\"");
		
		if (indexOfMinOccurs >0){
			int startOfStartTag = findStartOfStartTag(result, tagName);
			result = StringUtils.substring(result, 0, startOfStartTag + indexOfMinOccurs)
			 + StringUtils.substring(result, startOfStartTag + indexOfMinOccurs + "minOccurs=\"0\"".length(), result.length());
		}
		
		return result;
	}

	private static String removeTag(String result, String tagName) {
		if (!StringUtils.contains(result, tagName)){
			System.out.println("There was not tag called " + tagName);
			return result;
		}
		
		String contentOfTag = getContentOfTag(result, tagName);
		result = StringUtils.remove(result, contentOfTag);
		return result;
	}

	private static String getContentOfTag(String result, String tagName) {
		return getContentOfTag(ELEMENT, result, tagName);
	}
	
	private static String removeEnum(String result, String tagName) {
		if (!StringUtils.contains(result, tagName)){
			System.out.println("There was not an Enum called " + tagName);
			return result;
		}
		
		String contentOfTag = getContentOfEnum(result, tagName);
		result = StringUtils.remove(result, contentOfTag);
		return result;
	}
	
	private static String getContentOfEnum(String result, String tagName) {
		return getContentOfTag(ELEMENTENUM, result, tagName);
	}
	
	private static String getContentOfTag(String startingWith, String result, String tagName) {
		int startOfStartTag = findStartOfStartTag(startingWith, result, tagName);
		int endOfEndTag = findEndOfEndTag(result, startOfStartTag);
		String contentOfTag = result.substring(startOfStartTag, endOfEndTag);
		return contentOfTag;
	}

	private static int findStartOfStartTag(String body, String tagName) {
		return findStartOfStartTag(ELEMENT, body, tagName);
	}
	private static int findStartOfStartTag(String startingWith, String body, String tagName) {
		return StringUtils.indexOf(body, startingWith + tagName);
	}

	private static int findEndOfEndTag(String body, int startAt) {
		int numberOpened = 0;
		int maxValueInTree = 0;
		int startPos = startAt;

		boolean isFirstIteration = true;
		while (numberOpened != 0 || isFirstIteration ){
			int nextOpenTag = StringUtils.indexOf(body, "<xs:", startPos );
			boolean isSelfEndingTag = StringUtils.indexOf(body, "/>", startPos ) < StringUtils.indexOf(body, "<", startPos );
			
			int nextCloseTag = isSelfEndingTag? 
					StringUtils.indexOf(body, "/>", startPos ):
					StringUtils.indexOf(body, "</xs:", startPos );
			
			if (nextOpenTag < nextCloseTag){
				numberOpened++;
				maxValueInTree++;
				startPos = nextOpenTag + 1;
			} else{
				numberOpened--;
				startPos = nextCloseTag + 1;			
			}
			isFirstIteration = false;
		}
		
		return startPos + ((maxValueInTree>1)?"/xs:element>".length():"/>".length());
	}
	
	private static String removeDeceasedTags(String result) {
		result = removeTag(result, "DeceasedDate");
		result = removeTag(result, "Deceased");
		result = removeTag(result, "DeceasedNotificationDate");
		return result;
	}
}
