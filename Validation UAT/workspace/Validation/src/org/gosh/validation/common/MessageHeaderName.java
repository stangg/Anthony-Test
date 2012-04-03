package org.gosh.validation.common;

/**
 * @author gayathri.polavaram
 * This is an enum to hold the names of the header to be added in the
 * messages we build. 
 */
public enum MessageHeaderName {

	ERROR_HEADER(1, "error"),
	INFO_HEADER(2, "info"),
	BANK_HEADER(4, "isOldBank"),
	FILE_TYPE(5, "fileType");
	
	private final int header;
	private final String name;
	
	private MessageHeaderName(int header, String name) {
		this.header = header;
		this.name = name;
	}
	
	public int getHeader() {
		return header;
	}
	
	public String getName() {
		return name;
	}
}
