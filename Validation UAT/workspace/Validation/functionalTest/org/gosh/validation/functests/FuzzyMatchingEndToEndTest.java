package org.gosh.validation.functests;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.validation.general.deduplication.FuzzyLookupTransformer;
import org.gosh.validation.general.deduplication.WeightedMatcherTransformer;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class FuzzyMatchingEndToEndTest extends AbstractDependencyInjectionSpringContextTests {
	private static final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><GOSHCC><BatchNo>000010</BatchNo><SupplierID>EIBS</SupplierID><DonorCplxType><Title1>Miss</Title1><FirstName>J</FirstName><LastName>Jefferies</LastName><ChangeOfName>false</ChangeOfName><PrimaryAddress>false</PrimaryAddress><Address><AddressLine>71 Dragon Road</AddressLine><AddressLine>Winterbourne</AddressLine></Address><City>BRISTOL</City><County>Avon</County><PostCode>BS36 1BH</PostCode><Country/><Gender>Female</Gender><ConsCodes><Code>DMGen</Code><DateFrom>2009-07-28</DateFrom></ConsCodes><Attributes><Source><Category>Source</Category><Description>Direct Mail</Description><Date>2009-07-28</Date><Comment>Obtained via VALLDATA</Comment></Source><DirectMarketingType><Category>Direct Marketing Type</Category><Description>Individual Donor</Description><Date>2009-07-28</Date><Comment>Obtained Via VALLDATA</Comment></DirectMarketingType></Attributes><ConstituentAppeal><AppealID>DMW6110</AppealID><PackageID>ZZ904</PackageID><Date>2009-07-28</Date><Response>Responded</Response><MarketingSourceCode>2778747</MarketingSourceCode></ConstituentAppeal><DonationDetails><CashDonationCplxType><Type>Cash</Type><SubType>Donations - 1010</SubType><Date>2009-07-28</Date><Amount>10.00</Amount><Fund>UNGEN</Fund><Campaign>DM61</Campaign><Appeal>DMW6110</Appeal><Package>ZZ904</Package><RefNo/><PostStatus>Not Posted</PostStatus><LetterCode/><PaymentType>Personal Cheque</PaymentType></CashDonationCplxType></DonationDetails></DonorCplxType></GOSHCC>";
	
	public void test() throws Exception {
		// this guy caused some concern
		GOSHCC goshcc = getModel();
		
		Message<GOSHCC> transform = fuzzyLookupTransformer.transform(new GenericMessage<GOSHCC>(goshcc));
		weightedMatcherTransformer.transform(transform);
		
		assertEquals("10126680", goshcc.getDonorCplxType().get(0).getConstituentID());
	}
	
	private GOSHCC getModel() throws DatatypeConfigurationException, JAXBException {
		JAXBContext context = JAXBContext.newInstance("org.gosh.re.dmcash.bindings");
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (GOSHCC)unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));
	}
	
	private WeightedMatcherTransformer weightedMatcherTransformer;
	private FuzzyLookupTransformer fuzzyLookupTransformer;
	
	public void setFuzzyLookupTransformer(FuzzyLookupTransformer fuzzyLookupTransformer) {
		this.fuzzyLookupTransformer = fuzzyLookupTransformer;
	}
	public void setWeightedMatcherTransformer(WeightedMatcherTransformer weightedMatcherTransformer) {
		this.weightedMatcherTransformer = weightedMatcherTransformer;
	}
    protected String[] getConfigLocations() {
        return new String[] { "classpath:config.xml", "classpath:beans.xml" };
    }
}
