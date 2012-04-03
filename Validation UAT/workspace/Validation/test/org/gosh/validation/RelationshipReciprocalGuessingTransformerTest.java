package org.gosh.validation;

import java.util.HashMap;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.Relationship;
import org.gosh.validation.tributes.RelationshipReciprocalGuessingTransformer;
import org.springframework.integration.message.GenericMessage;

import junit.framework.TestCase;

public class RelationshipReciprocalGuessingTransformerTest extends TestCase {
	public void testReciprocalIsNotReplacedIfPopulated() throws Exception {
		RelationshipReciprocalGuessingTransformer transformer = new RelationshipReciprocalGuessingTransformer();
		GOSHCC goshcc = new GOSHCC();
		Relationship relationship = new Relationship();
		goshcc.getRelationship().add(relationship);
		relationship.setRelationshipDescriptor("relationship");
		relationship.setReciprocalDescriptor("reciprocal");
		transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertEquals("reciprocal", relationship.getReciprocalDescriptor());
	}

	public void testReciprocalPopulatedFromMapIfNotThere() throws Exception {
		RelationshipReciprocalGuessingTransformer transformer = new RelationshipReciprocalGuessingTransformer();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("relationship","reciprocal");
		transformer.setRelationshipToReciprocalMap(map);
		
		GOSHCC goshcc = new GOSHCC();
		Relationship relationship = new Relationship();
		goshcc.getRelationship().add(relationship);
		relationship.setRelationshipDescriptor("relationship");
		transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertEquals("reciprocal", relationship.getReciprocalDescriptor());
	}
	
	public void testReciprocalNotPopulatedIfNotInMap() throws Exception {
		RelationshipReciprocalGuessingTransformer transformer = new RelationshipReciprocalGuessingTransformer();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("something else","reciprocal");
		transformer.setRelationshipToReciprocalMap(map);
		
		GOSHCC goshcc = new GOSHCC();
		Relationship relationship = new Relationship();
		goshcc.getRelationship().add(relationship);
		relationship.setRelationshipDescriptor("relationship");
		transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		assertNull(relationship.getReciprocalDescriptor());
	}
}
