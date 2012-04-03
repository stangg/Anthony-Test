package org.gosh.validation.functests;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class InvalidEndToEndTest extends EndToEndTests{
	
	public static final String VALIDATION_HOME="D:\\Validation";
	public void testInvalidCase() throws Exception {
    	clean();
    	
    	Date now = new Date();
		String testData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><GOSHCC xsi:noNamespaceSchemaLocation=\"DM%20Import%20System%20v13.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"></GOSHCC>";
		File originalFile = new File(VALIDATION_HOME + "\\Inbound\\" + now.getTime() + ".xml");
		FileUtils.writeStringToFile(originalFile, testData);
    	
    	// wait for it to come out. 
    	File newFile = new File(VALIDATION_HOME + "\\Errors\\" + now.getTime() + ".xml");
    	while((new Date().getTime() - now.getTime() < 100000)&&!newFile.exists()){
	 	   	Thread.sleep(500);
    	}
    	
    	assertTrue("File wasn't written", newFile.exists());
    	
    	//check a random error
    	assertTrue(StringUtils.containsIgnoreCase(FileUtils.readFileToString(newFile), "The file was not valid against the schema."));
    	
		clean();
	}
}
