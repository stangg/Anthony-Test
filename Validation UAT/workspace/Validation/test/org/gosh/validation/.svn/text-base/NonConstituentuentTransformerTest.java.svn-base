package org.gosh.validation;

import java.util.List;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.NonConstituentOrganisationRelationship;
import org.gosh.validation.general.error.Reporter;
import org.gosh.validation.xmas.NonConstituentuentTransformer;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.springframework.integration.message.GenericMessage;

public class NonConstituentuentTransformerTest extends MockObjectTestCase {
	public void testRemovesWhenExist() throws Exception {
		NonConstituentuentTransformer transformer = new NonConstituentuentTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		final Reporter reporter = mock(Reporter.class);
		transformer.setReporter(reporter);

		GOSHCC payload = new GOSHCC();
		DonorCplxType donor = addDonorTo(payload);
		addRelationTo(donor, "Allied Domecq PLC");
		final GenericMessage<GOSHCC> message = new GenericMessage<GOSHCC>(payload);

		checking(new Expectations(){{
			one(reporter).log(message, "We just removed non constit relations because they already exist. We removed relations with the following names: Allied Domecq PLC,");
		}});

		transformer.transform(message);
		assertTrue(relationsFor(payload).isEmpty());
	}

	public void testLeavesWhenDoesntExist() throws Exception {
		NonConstituentuentTransformer transformer = new NonConstituentuentTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());

		GOSHCC payload = new GOSHCC();
		DonorCplxType donor = addDonorTo(payload);
		NonConstituentOrganisationRelationship relationship 
			= addRelationTo(donor, "Unreal PLC");

		transformer.transform(new GenericMessage<GOSHCC>(payload));
		assertTrue(relationsFor(payload).contains(relationship));
	}

	private NonConstituentOrganisationRelationship addRelationTo(DonorCplxType donor, String name) {
		NonConstituentOrganisationRelationship nonConstituentOrganisationRelationship = new NonConstituentOrganisationRelationship();
		donor.getNonConstituentOrganisationRelationship().add(nonConstituentOrganisationRelationship);
		nonConstituentOrganisationRelationship.setName(name);
		return nonConstituentOrganisationRelationship;
	}

	private DonorCplxType addDonorTo(GOSHCC payload) {
		DonorCplxType donor = new DonorCplxType();
		payload.getDonorCplxType().add(donor);
		donor.setInternalConstitID("17");
		return donor;
	}

	private List<NonConstituentOrganisationRelationship> relationsFor(GOSHCC payload) {
		return payload.getDonorCplxType().get(0).getNonConstituentOrganisationRelationship();
	}
}
