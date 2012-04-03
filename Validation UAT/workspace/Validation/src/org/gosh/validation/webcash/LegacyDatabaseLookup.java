package org.gosh.validation.webcash;

import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;

public interface LegacyDatabaseLookup {
	public abstract String lookupCampaignAppealFundConstituentCodesAndContact(String reason, String subreason, CashDonationCplxType donation, DonorCplxType donor);
}