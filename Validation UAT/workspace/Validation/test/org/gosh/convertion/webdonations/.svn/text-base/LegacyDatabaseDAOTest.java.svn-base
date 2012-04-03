package org.gosh.convertion.webdonations;

import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.ConsCodes;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.validation.TestDataSourceFactory;
import org.gosh.validation.webcash.LegacyDatabaseDAO;

public class LegacyDatabaseDAOTest extends TestCase {
	public void testALookupICheckedAgainstTheDB() throws Exception{
		LegacyDatabaseDAO dao = new LegacyDatabaseDAO();
		dao.setDataSource(TestDataSourceFactory.getLegacyDataSource());
		CashDonationCplxType cashDonation = new CashDonationCplxType();
		DonorCplxType donor = new DonorCplxType();
		donor.getConsCodes().add(new ConsCodes());
		String contact = dao.lookupCampaignAppealFundConstituentCodesAndContact("12", "115", cashDonation, donor);
		
		assertEquals("DM01", cashDonation.getCampaign());
		assertEquals("DMC0110B", cashDonation.getAppeal());
		assertEquals("UNGEN", cashDonation.getFund());
		assertEquals("GEN10A", cashDonation.getPackage());
		assertEquals("patrick.tang@gosh.org", cashDonation.getFundraisingContact().get(0));
		assertEquals("patrick.tang@gosh.org", contact);
		assertEquals("DMGen", donor.getConsCodes().get(0).getCode());
	}
}