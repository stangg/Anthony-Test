package org.gosh.validation.functests;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public class InvalidDataSourceEndToEndTest extends EndToEndTests{
    public void testWithWrongDatasourceToCheckForErrors() throws Exception {
    	clean();
    	
    	Date now = new Date();
		String testData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><GOSHCC xsi:noNamespaceSchemaLocation=\"DM%20Import%20System%20v13.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"></GOSHCC>";
		
    	String originalURL = dataSource.getURL();
    	dataSource.setURL("something is wrong with the url!!!");
    	
    	File originalFile = new File("C:\\Validation\\Inbound\\" + now.getTime() + ".xml");
		FileUtils.writeStringToFile(originalFile, testData);
    	
    	// wait for it to come out. 
    	File newFile = new File("C:\\Validation\\Errors\\" + now.getTime() + ".xml");
    	File failedFile = new File("C:\\Validation\\Valid\\" + now.getTime() + ".xml");
    	while((new Date().getTime() - now.getTime() < 60000)&&!newFile.exists()&&!failedFile.exists()){
	 	   	Thread.sleep(500);
    	}

    	if (failedFile.exists()){
    		fail("A valid file was unexpectedly written. It contained the following: " + FileUtils.readFileToString(failedFile));
    	}
    	assertTrue("File wasn't written", newFile.exists());
    	
    	// cleanup
    	dataSource.setURL(originalURL);	
    	
		clean();
    }
}
