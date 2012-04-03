package org.gosh.validation.functests;

import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.validation.general.bank.BankDAO;
import org.gosh.validation.general.bank.BankXindiceDAO;

public class BankXindiceDAOTest extends TestCase {
	public void testLookup() throws Exception {
		BankDAO bankXindiceDAO = new BankXindiceDAO();
		
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankName("ABN AMRO BANK NV");
		bank.setBankSort("406104");
		
		Map<DonorBankCplxType, Boolean> lookupMap = bankXindiceDAO.lookupBankFromList(Collections.singletonList(bank));
		assertTrue(lookupMap.get(bank));
	}

	public void testLookupWithADifferentKnownBank() throws Exception {
		BankDAO bankXindiceDAO = new BankXindiceDAO();
		
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankName("BANK OF ENGLAND");
		bank.setBankSort("102253");
		
		Map<DonorBankCplxType, Boolean> lookupMap = bankXindiceDAO.lookupBankFromList(Collections.singletonList(bank));
		assertTrue(lookupMap.get(bank));
	}
	
	public void testLookupWhereDoesntExist() throws Exception {
		BankDAO bankXindiceDAO = new BankXindiceDAO();
		
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankName("ABN AMRO BANK NV");
		bank.setBankSort("406105");

		Map<DonorBankCplxType, Boolean> lookupMap = bankXindiceDAO.lookupBankFromList(Collections.singletonList(bank));
		assertFalse(lookupMap.get(bank));
	}
	
	public void testLookupWithDashes() throws Exception {
		BankDAO bankXindiceDAO = new BankXindiceDAO();
		
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankName("ABN AMRO BANK NV");
		bank.setBankSort("40-61-04");
		
		Map<DonorBankCplxType, Boolean> lookupMap = bankXindiceDAO.lookupBankFromList(Collections.singletonList(bank));
		assertTrue(lookupMap.get(bank));
	}
	
	public void testLookupBlank() throws Exception {
		BankDAO bankXindiceDAO = new BankXindiceDAO();
		
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankName(" ");

		Map<DonorBankCplxType, Boolean> lookupMap = bankXindiceDAO.lookupBankFromList(Collections.singletonList(bank));
		assertFalse(lookupMap.get(bank));
	}
	
	public void testLookupWithAmpersand() throws Exception {
		BankDAO bankXindiceDAO = new BankXindiceDAO();
		
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankName("COUTTS & CO");
		bank.setBankSort("18-00-02");

		Map<DonorBankCplxType, Boolean> lookupMap = bankXindiceDAO.lookupBankFromList(Collections.singletonList(bank));
		assertTrue(lookupMap.get(bank));
	}
	
	public void testCaseInsensitive() throws Exception {
		BankDAO bankXindiceDAO = new BankXindiceDAO();
		
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankName("ABN AMRO Bank NV");
		bank.setBankSort("406104");
		
		Map<DonorBankCplxType, Boolean> lookupMap = bankXindiceDAO.lookupBankFromList(Collections.singletonList(bank));
		assertTrue(lookupMap.get(bank));
	}
	
	public void testMentalRealWorldCase() throws Exception {
		BankDAO bankXindiceDAO = new BankXindiceDAO();
		
		DonorBankCplxType bank = new DonorBankCplxType();
		bank.setBankName("\"NAT WEST BANK PLC");
		bank.setBankSort("600511");
		
		Map<DonorBankCplxType, Boolean> lookupMap = bankXindiceDAO.lookupBankFromList(Collections.singletonList(bank));
		assertFalse(lookupMap.get(bank));
	}
}
