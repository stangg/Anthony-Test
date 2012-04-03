/**
 * 
 */
package org.gosh.validation.general.bank;

import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;

/**
 * @author gayathri.polavaram
 * Utility class that provides number of common functionality around bank details.
 *  
 */
public class BankUtilities {
	
	/**
	 * Checks if some AccNo and SortCode are present.
	 * @param donor
	 * @return true if AccountNumber and SortCode are present, False even one of them is null/empty/only spaces.
	 */
	public boolean isAccOrSortBlank(DonorCplxType donor) {
		
		// If bank not null
		if (!isBankNull(donor)) {
			DonorBankCplxType bankCplx = donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType();
			if(StringUtils.isBlank(bankCplx.getBankAccNo()) || StringUtils.isBlank(bankCplx.getBankSort())){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks is bank details are present in the XML. In other words checks if the bank is null
	 * @param donor
	 * @return true if bank is null
	 */
	public boolean isBankNull(DonorCplxType donor) {
		if (donor.getDonationDetails()!=null && donor.getDonationDetails().getDirectDebitDonationCplxType()!= null &&
				donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType()!= null) {
				return false;
		}
		return true;
	}
	
	
	/**
	 * Checks if AccNo, SortCode and AccName are present
	 * @param donor
	 * @return true if all the above are present. False even one of them is null/empty/only spaces.
	 */
	public boolean isBankDetailsComplete(DonorCplxType donor) {
		
		// If bank not null
		if (!isBankNull(donor)) {
			DonorBankCplxType bankCplx = donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType();
			if(!StringUtils.isBlank(bankCplx.getBankAccNo()) && !StringUtils.isBlank(bankCplx.getBankSort())&& !StringUtils.isBlank(bankCplx.getBankAccName())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the number of digits in the account number.
	 * @return -1 if acc is not present or blank. Number of digits in account number field. counselling 
	 */
	public int getAccountNoLength(DonorCplxType donor) {
		int length = -1; 
		if (!isBankNull(donor)) {
			DonorBankCplxType bankCplx = donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType();
			if (bankCplx != null) {
				if(!StringUtils.isBlank(bankCplx.getBankAccNo())) {
					length = StringUtils.strip(bankCplx.getBankAccNo()).length();
				}
			}
		}
		return length;
	}

	/**
	 * Checks is the bank account name is null or blank
	 * @param donor
	 * @return true if account name is null or blank
	 */
	public boolean isAccNameBlank (DonorCplxType donor) {
		if (!isBankNull(donor)) {
			DonorBankCplxType bankCplx = donor.getDonationDetails().getDirectDebitDonationCplxType().getDonorBankCplxType();
			if (StringUtils.isBlank(bankCplx.getBankAccName())){
				return true;
			}
		}
		return false;
	}
}
