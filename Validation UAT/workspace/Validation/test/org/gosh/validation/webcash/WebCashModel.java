/**
 * 
 */
package org.gosh.validation.webcash;

/**
 * @author Maria.Urso
 *
 */
public class WebCashModel {
	private Integer reasonID;
	private Integer subReasonID;
	private String campaignCode;
	private String appealCode;
	private String fundCode;
	private String packageCode;
	private String fundraisingContact;
	private String consCode;
	private String reason;
	private String subReason;
	private String databaseReason;
	private String databaseSubReason;
	
	public WebCashModel(Integer reasonID, Integer subReasonID,	String campaignCode, String appealCode,	String fundCode, String fundraisingContact,String packageCode,String consCode,String databaseReason,String databaseSubReason){
		this.reasonID = reasonID;
		this.subReasonID = subReasonID;
		this.campaignCode = campaignCode;
		this.appealCode = appealCode;
		this.fundCode = fundCode;
		this.packageCode = packageCode;
		this.fundraisingContact = fundraisingContact;
		this.consCode = consCode;
		this.databaseReason = databaseReason;
		this.databaseSubReason = databaseSubReason;
	}
	
	public WebCashModel(String reason, String subReason){
		this.reason = reason;
		this.subReason = subReason;
	}
	
	public Integer getReasonID() {
		return reasonID;
	}
	public void setReasonID(Integer reasonID) {
		this.reasonID = reasonID;
	}
	public Integer getSubReasonID() {
		return subReasonID;
	}
	public void setSubReasonID(Integer subReasonID) {
		this.subReasonID = subReasonID;
	}
	public String getCampaignCode() {
		return campaignCode;
	}
	public void setCampaignCode(String campaignCode) {
		this.campaignCode = campaignCode;
	}
	public String getAppealCode() {
		return appealCode;
	}
	public void setAppealCode(String appealCode) {
		this.appealCode = appealCode;
	}
	public String getFundCode() {
		return fundCode;
	}
	public void setFundCode(String fundCode) {
		this.fundCode = fundCode;
	}
	public String getPackageCode() {
		return packageCode;
	}
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}
	public String getFundraisingContact() {
		return fundraisingContact;
	}
	public void setFundraisingContact(String fundraisingContact) {
		this.fundraisingContact = fundraisingContact;
	}
	public String getConsCode() {
		return consCode;
	}
	public void setConsCode(String consCode) {
		this.consCode = consCode;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public void setSubReason(String subReason) {
		this.subReason = subReason;
	}

	public String getSubReason() {
		return subReason;
	}

	public String getDatabaseReason() {
		return databaseReason;
	}

	public void setDatabaseReason(String databaseReason) {
		this.databaseReason = databaseReason;
	}

	public String getDatabaseSubReason() {
		return databaseSubReason;
	}

	public void setDatabaseSubReason(String databaseSubReason) {
		this.databaseSubReason = databaseSubReason;
	}
	
	
}
