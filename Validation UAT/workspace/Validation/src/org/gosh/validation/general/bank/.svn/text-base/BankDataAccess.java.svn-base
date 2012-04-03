/**
 * 
 */
package org.gosh.validation.general.bank;

import java.math.BigInteger;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;

/**
 * @author gayathri.polavaram
 *
 */
public class BankDataAccess {
	
	private JdbcTemplate jdbcTemplate;
	
	/** The value for bank name in CODETABLESID column of tableentries table*/
	private static final int BANK_NAME_CODE=5087;
	
	public BankDataAccess(DataSource ds) {
		this.jdbcTemplate = new JdbcTemplate(ds);
	}

	/**
	 * Fetches all IDs from the table Bank where sort code matches the given sort Code. Ideally sort code must be unique to a Bank.
	 * In case we get multiple records, then data clean up must be performed.
	 * @param sortCode 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getBankID(String sortCode) {
		List<String> bankIds = null;
		// Build the query
		String sqlQuery = "SELECT bank.ID FROM bank WHERE bank.SORT_CODE='" + sortCode + "'"; 
		try {
			bankIds = (List<String>)jdbcTemplate.query(sqlQuery, new SingleColumnRowMapper(String.class));
		} catch (EmptyResultDataAccessException e) {
			// ignore it
		}
		return bankIds;
		
	}
	
	/**
	 * Fetches the bank constuient ID of an existing record based on the sortCode and person constituentID
	 * @param sortCode
	 * @param constituentID
	 * @return
	 */
	public List<BigInteger> getBankConstitIdBySortCode(String sortCode, String constituentID) {
	
		List<BigInteger> constitBankIDs = null;
	
		String sqlQuery = "SELECT constituent_bank.ID FROM records, bank, constituent_bank WHERE records.CONSTITUENT_ID = '" 
			+ constituentID + "' AND bank.ID=constituent_bank.BRANCH_ID " +
					"AND constituent_bank.CONSTIT_ID=records.ID AND bank.SORT_CODE='"
			+ sortCode +"'";
		
		try {
			constitBankIDs = (List<BigInteger>) jdbcTemplate.query(sqlQuery, new SingleColumnRowMapper(BigInteger.class));
		} catch (EmptyResultDataAccessException e) {
			// ignore it
		}
		return constitBankIDs;
	}
	
		
	/**
	 * Fetches the bank constuient ID of an existing record based on the giftImportId
	 * @param giftImportID
	 * @return BankConstId of given giftImportID or null if it does not exist
	 */
	public BigInteger getBankConstitIdByGift(String internalConstId, String giftImportID) {

		
		BigInteger constitBankID = null;
		
		String sqlQuery = "SELECT gift.CONSTITUENTBANKID FROM gift WHERE gift.CONSTIT_ID = " 
			+ internalConstId +	" AND  gift.IMPORT_ID = '" + giftImportID +"'";

		
		try {	
			constitBankID = (BigInteger) jdbcTemplate.queryForObject(sqlQuery, BigInteger.class);
		} catch (EmptyResultDataAccessException e) {
			// ignore it
		}
		
		return constitBankID;
	}

	/**
	 * Fetches bank ID from the bank table that matches the given sortcode, branch name and bank name. 
	 * Bank Name will be queried from Tableentries table.
	 * We choose these fields since the bank webservice considers the combination of these three
	 * fields when checking if the bank already exists. 
	 * Escape ' with another ' for bank name and branch name.
	 * @param sortCode
	 * @param bankName
	 * @param branchName
	 * @return bank ID from bank table if the bank exists, null if not
	 */
	public List<String> getBankID(String sortCode, String bankName, String branchName) {
		List<String> bankIds = null;
		String subSql;
		bankName = escapeApostrophe(bankName);
		
		// Determine if branchname is null/blank
		if (branchName == null || StringUtils.isBlank(branchName)) {
			subSql = "(bank.BRANCH_NAME='' or bank.BRANCH_NAME is null)";
		} else {
			subSql = "bank.BRANCH_NAME='" + escapeApostrophe(branchName) + "'";
		}
		
		/*
		 * Build the query:
		 * Example:
		 * select bank.id from tableentries, bank 
		 * where tableentries.TABLEENTRIESID=bank.bank 
		 * and bank.sort_code='09-01-26' 
		 * and (bank.branch_name='' or bank.branch_name=null) 
		 * and tableentries.longdescription='SANTANDER UK PLC' or  tableentries.SHORTDESCRIPTION='SANTANDER UK PLC'
		 * and tableentries.active = -1 and tableentries.CODETABLESID=5087
		 */
		String sqlQuery = "SELECT bank.ID FROM bank, tableentries" 
						+ " WHERE bank.SORT_CODE='" + sortCode + "'"
						+ " and " + subSql 
						+ " and (tableentries.LONGDESCRIPTION ='" + bankName + "' OR tableentries.SHORTDESCRIPTION = '" + bankName + "')"
						+ " and tableentries.ACTIVE = -1 and tableentries.CODETABLESID=" + BANK_NAME_CODE 
						+ " and tableentries.TABLEENTRIESID=bank.bank"; 
		try {
			bankIds = (List<String>)jdbcTemplate.query(sqlQuery, new SingleColumnRowMapper(String.class));
		} catch (EmptyResultDataAccessException e) {
			// ignore it
		}
		return bankIds;
		
	}
	
	/**
	 * @param input string to be escaped if it contains Apostrophe
	 * @return input string with additional Apostrophe to escape the real one
	 */
	public String escapeApostrophe(String input) {
		return StringUtils.replace(input, "'", "''");
	}
	
}
