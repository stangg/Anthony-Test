package org.gosh.validation;

import java.util.List;

import org.gosh.re.dmcash.bindings.GOSHCC.Relationship;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.Tribute;
import org.gosh.validation.general.error.Reporter;
import org.gosh.validation.tributes.TributeValidationTransformer;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.springframework.integration.message.GenericMessage;

public class TributeValidationTransformerTest extends MockObjectTestCase {
	private static final String ACK_ID = "ack";
	private static final String HONOR_ID = "honor";
	private TributeValidationTransformer transformer;
	private Reporter reporter;
	private GenericMessage<GOSHCC> message;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		transformer = new TributeValidationTransformer();
		reporter = mock(Reporter.class);
		transformer.setReporter(reporter);
		GOSHCC goshcc = new GOSHCC();
		generateValidModel(goshcc);
		message = new GenericMessage<GOSHCC>(goshcc);
	}

	public void testFullValidCase() throws Exception {
		transformer.transform(message);
	}

	public void testInvalidHonorID() throws Exception {
		message.getPayload().getTribute().get(0).setHonerSupplierDonorID("something else");
		setToExpectTheMessage("Some of the IDs linking tributes to donors were not valid. There is a tribute that contains [something else, ack] when the ids for donors were [honor, ack]");
		transformer.transform(message);
	}

	public void testInvalidAckId() throws Exception {
		message.getPayload().getTribute().get(0).getAcknowledgeeSupplierDonorID().set(0, "something else");
		setToExpectTheMessage("Some of the IDs linking tributes to donors were not valid. There is a tribute that contains [honor, something else] when the ids for donors were [honor, ack]");
		transformer.transform(message);
	}
	
	public void testInvalidRelationships() throws Exception {
		message.getPayload().getRelationship().clear();
		setToExpectTheMessage("Some of the relationships linking honors and acknowledgees did not exist. For example the following were not all mapped [honor->[ack]]");
		transformer.transform(message);
	}
	
	public void testWithValidSelfAcknowledge() throws Exception {
		GOSHCC goshcc = new GOSHCC();
		
		Tribute tribute = new Tribute();
		tribute.setHonerSupplierDonorID(HONOR_ID);
		tribute.getAcknowledgeeSupplierDonorID().add(HONOR_ID);
		goshcc.getTribute().add(tribute);

		DonorCplxType honor = new DonorCplxType();
		honor.setSupplierDonorID(HONOR_ID);
		goshcc.getDonorCplxType().add(honor);
		
		message = new GenericMessage<GOSHCC>(goshcc);
		transformer.transform(message);
	}
	
	public void testLoadOnFullValidCase() throws Exception {
		for(int i = 0; i < 2000; i++){
			generateValidModel(message.getPayload());
		}
		
		transformer.transform(message);
	}
	
	private void setToExpectTheMessage(final String text) {
		checking(new Expectations(){{
			oneOf(reporter).log(message, text);
		}});
	}

	private void generateValidModel(GOSHCC goshcc) {
		List<Tribute> tributes = goshcc.getTribute();
		Tribute tribute = new Tribute();
		tribute.setHonerSupplierDonorID(HONOR_ID);
		tribute.getAcknowledgeeSupplierDonorID().add(ACK_ID);
		tributes.add(tribute);

		DonorCplxType honor = new DonorCplxType();
		honor.setSupplierDonorID(HONOR_ID);
		goshcc.getDonorCplxType().add(honor);
		
		DonorCplxType ack = new DonorCplxType();
		ack.setSupplierDonorID(ACK_ID);
		goshcc.getDonorCplxType().add(ack);
		
		Relationship relationship = new Relationship();
		relationship.setSupplierDonorID(HONOR_ID);
		relationship.setRelatedSupplierDonorID(ACK_ID);
		goshcc.getRelationship().add(relationship);
	}
}
