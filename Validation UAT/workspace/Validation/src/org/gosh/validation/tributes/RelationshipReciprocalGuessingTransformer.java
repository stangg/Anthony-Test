package org.gosh.validation.tributes;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.Relationship;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;

/**
 * As result of the change request from Jag of BS0001. The below
 * is taken from this:
 * 
 * We have agreed with the Tribute project team and Pip Jones 
 * that it would be acceptable to ask users for just their 
 * 'Relationship' to the Tributee. Then we would make an assumption 
 * for the Reciprocal field based on what had been populated in the 
 * Relationship field.  The assumptions would be fixed as in the table 
 * below:
 * 
 * Relationship (completed by user)	Reciprocal 
 * Mother	Child
 * Father	Child
 * Son	Parent
 * Daughter	Parent
 * Brother	Sibling
 * Sister	Sibling
 * Cousin	Cousin
 * Aunt	Relation
 * Uncle	Relation
 * Nephew	Relation
 * Niece	Relation
 * Grandmother	Grandchild
 * Grandfather	Grandchild
 * Great Grandmother	Great Grandchild
 * Great Grandfather	Great Grandchild
 * Godparent	Godchild
 * Friend	Friend
 * Partner	Partner
 * Husband	Wife
 * Wife	Husband
 * 
 *  Formbuilder on Easysite is not able to populate the reciprocal field 
 *  based on logic. Therefore the change would be as follows:
 *  
 *  Stop collecting reciprocal field on Easysite formbuilder. Kevin's team 
 *  to create validation rules so that when data validated from Easysite 
 *  before being imported into RE – reciprocal field would be filled based 
 *  on what was passed from relationship field. 
 *  
 *  Then Kevin's team would need to update XSLT to recognize change. 
 *  (i.e. no longer collecting reciprocal field on Easysite).
 * 
 * @author Kevin.Savage
 */
public class RelationshipReciprocalGuessingTransformer {
	private Map<String, String> relationshipToReciprocalMap;
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) {
		log.info("Start Transformation");
		List<Relationship> relationships = message.getPayload().getRelationship();
		for (Relationship relationship : relationships) {
			if (StringUtils.isBlank(relationship.getReciprocalDescriptor())){
				String reciprocal = relationshipToReciprocalMap.get(relationship.getRelationshipDescriptor());
				if (reciprocal != null){
					relationship.setReciprocalDescriptor(reciprocal);
					relationship.setReciprocate(true);
				}
			}
		}
		log.info("End of Transformation");
		return message;
	}

	public void setRelationshipToReciprocalMap(Map<String, String> hashMap) {
		this.relationshipToReciprocalMap = hashMap;
	}
}
