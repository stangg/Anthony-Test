/**
 * 
 */
package org.gosh.validation.webcash;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;

/**
 * @author Maria.Urso
 *
 */
public class GiftNotesReportGenerator {
	//public static final String GIFT_NOTES = "giftNotesReport";
	
	public List<String> generateReport(GOSHCC goshcc) {
		ArrayList<String> reportLines = new ArrayList<String>();
		reportLines.add("ConstituentId,Title,FirstName,LastName,Date,Amount,Appeal,Campaign,Fund,GiftNotes");
		// Requested 
		for (DonorCplxType donor : goshcc.getDonorCplxType()) {
			if (donor.getDonationDetails() == null){
				continue;
			}
			for (CashDonationCplxType cashDonation : donor.getDonationDetails().getCashDonationCplxType()) {
				// If there is no donate Description do not add to report
				if (StringUtils.isBlank(cashDonation.getDonateDescription())){
					continue;
				}
				
				String date = cashDonation.getDate()!=null?cashDonation.getDate().toString():"";
				
				String commonPart = 
					donor.getConstituentID() + ","
					+ donor.getTitle1() + ","
					+ donor.getFirstName() + ","
					+ donor.getLastName() + ","
					+ StringUtils.substring(date, 0, 10) + ","
					+ cashDonation.getAmount() + ","
					+ cashDonation.getAppeal() + ","
					+ cashDonation.getCampaign() + ","
					+ cashDonation.getFund() + ","
					+ cashDonation.getDonateDescription() ;
				
					reportLines.add(commonPart);
			}
		}
		return reportLines;
	}
	
	public void printFile(List<String> report, String outputFolder) {
		try {
			File file = new File(outputFolder);
			file.getParentFile().mkdirs();
			FileUtils.writeLines(file, report);
		} catch (IOException e) {
			System.out.println("----- Error -- Could not write Report File. " + e.getMessage());
			e.printStackTrace();
		}
	}
}
