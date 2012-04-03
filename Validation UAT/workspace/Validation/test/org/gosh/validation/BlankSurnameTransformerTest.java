package org.gosh.validation;

import static org.hamcrest.Matchers.is;

import java.util.List;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.validation.general.businessrules.BlankSurnameTransformer;
import org.gosh.validation.general.error.Reporter;
import org.hamcrest.collection.IsCollectionContaining;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

public class BlankSurnameTransformerTest extends MockObjectTestCase {
	public void testValidCase() throws Exception {
		BlankSurnameTransformer transformer = new BlankSurnameTransformer();
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = new DonorCplxType();
		goshcc.getDonorCplxType().add(donorCplxType);
		donorCplxType.setLastName("name");
		transformer.transform(new GenericMessage<GOSHCC>(goshcc));
	}
	
	public void testWithBlankSurname() throws Exception {
		BlankSurnameTransformer transformer = new BlankSurnameTransformer();
		
		GOSHCC goshcc = new GOSHCC();
		List<DonorCplxType> donors = goshcc.getDonorCplxType();
		final DonorCplxType donor = new DonorCplxType();
		donors.add(donor);
		
		donor.setLastName(" ");
		
		final Reporter mock = mock(Reporter.class);
		final Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		checking(new Expectations(){{
			one(mock).log(with(message), with(new IsCollectionContaining<DonorCplxType>(is(donor))), with("The surname is blank."));
		}});
		transformer.setReporter(mock);
		
		transformer.transform(message);
	}
}
