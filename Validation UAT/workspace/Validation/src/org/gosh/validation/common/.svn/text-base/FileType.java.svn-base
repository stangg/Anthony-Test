/**
 * 
 */
package org.gosh.validation.common;

/**
 * @author gayathri.polavaram
 * This enum is used to determine if the file contains donations or a tribute.
 */
public enum FileType {
	
	/** The file contains only prospective donors. */
	PROSPECT_TYPE(0, "prospect"),
	
	/** The file contains only cash donation details.*/
	CASH_TYPE(1, "cash"),
	
	/** The file contains only Direct Debit details. */
	DD_TYPE(2, "directDebits"),
	
	/** The file contains only Direct Debit Upgrade details. */
	DD_UPGRADE_TYPE(3, "ddUpgrade"),
	
	/** The file contains only Direct Debit Reactivation details. Currently not supported.*/
	DD_REACT_TYPE(4, "ddReact"),
	
	/** The file contains only tribute IDs */
	TRIBUTE_IN_GIFT_TYPE(5,  "tributeID"),
	
	/**The file contains the actual tributes*/
	TRIBUTE_TYPE(6, "tributeInMem"),
	
	/** The file contains gift details*/
	CASH_WITH_GIFT_TYPE(7, "xmas"),

	/** The file may contain donations along with tributes*/
	MIXED_TYPE(8, "mixed");
	
	/** Holds the enum value*/
	private int fileType;
	
	/** Holds the string representation*/
	private String fileTypeName;
	
	private FileType(int type, String name) {
		fileType = type;
		fileTypeName = name;
	}
	
	public int getFileType() {
		return fileType;
	}

	public String getFileTypeName() {
		return fileTypeName;
	}

}
