package org.gosh.validation.general.deduplication;

import org.apache.commons.lang.StringUtils;

/**
 * This is a model used for weighting purposes. 
 * @author Kevin.Savage
 *
 */
public class ExtendedDataSetModel {
	private int id;
	private int inactive;
	private int deceased;
	private String constituentId;
	private String title;
	private String firstName;
	private String middleName;
	private String lastName;
	private int sex;
	private String addressBlock;
	private String postCode;
	private int addressId;
	private String accountName;
	private String branchName;
	private String sortCode;
	private String phoneNumber;
	private String email;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getConstituentId() {
		return constituentId;
	}
	public void setConstituentId(String constituentId) {
		this.constituentId = constituentId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getAddressBlock() {
		return addressBlock;
	}
	public void setAddressBlock(String addressBlock) {
		this.addressBlock = addressBlock;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public int getAddressId() {
		return addressId;
	}
	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getSortCode() {
		return sortCode;
	}
	public void setSortCode(String sortCode) {
		this.sortCode = sortCode;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstLineOfAddress() {
		return 
			StringUtils.substringBefore(		
					StringUtils.substringBefore(getAddressBlock(), "\n"),
				"\r");
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getSex() {
		return sex;
	}
	public String getGender() {
		switch (sex) {
			case 1: return "Male";
			case 2: return "Female";
			default: return "Unknown";
		}
	}
	public void setDeceased(int deceased) {
		this.deceased = deceased;
	}
	public int getDeceased() {
		return deceased;
	}
	public void setInactive(int inactive) {
		this.inactive = inactive;
	}
	public int getInactive() {
		return inactive;
	}
}
