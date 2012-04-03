package org.gosh.validation.functests;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

public abstract class EndToEndTests extends AbstractDependencyInjectionSpringContextTests {
	
	private static String VALIDATION_HOME = "D:\\Validation\\";
	
    protected String[] getConfigLocations() {
        return new String[] { "classpath:validation.xml"};
    }
    
	protected void copyFileToInfolder(Date now, String filename) throws IOException {
		File donations = new DefaultResourceLoader().getResource("classpath:" + filename).getFile();
    	String testData = FileUtils.readFileToString(donations);
		File originalFile = new File("Inbound\\" + filename);
		FileUtils.writeStringToFile(originalFile, testData);
	}

    protected void clean() throws IOException {
		new File(VALIDATION_HOME + "Inbound\\").mkdirs();
		new File(VALIDATION_HOME + "Valid\\").mkdirs();
		new File(VALIDATION_HOME + "Errors\\").mkdirs();

		FileUtils.cleanDirectory(new File(VALIDATION_HOME + "Inbound\\"));
		FileUtils.cleanDirectory(new File(VALIDATION_HOME + "Valid\\"));
		FileUtils.cleanDirectory(new File(VALIDATION_HOME + "Errors\\"));

		if (new File(VALIDATION_HOME + "Inbound\\").listFiles().length>0){
			System.out.println("We tried to clear out files, but the inbound directory still contains" + new File(VALIDATION_HOME + "Inbound\\").listFiles());			
		}
		new JdbcTemplate(TestDataSourceFactory.getCIIADataSource()).execute("delete from SupplierBatchNumberLogTest where SupplierName = 'testID'");
	}
    
	protected void testEntireProcess(File testDataFile) throws IOException, InterruptedException {
		testEntireProcess(testDataFile, 360000);
	}
	
	protected void testEntireProcess(File testDataFile, int timeLimit) throws IOException, InterruptedException {
		clean();
    	
    	Date now = new Date();
    	
    	String testData = FileUtils.readFileToString(testDataFile);
		File originalFile = new File(VALIDATION_HOME + "Inbound\\" + now.getTime() + ".xml");
		FileUtils.writeStringToFile(originalFile, testData);
    	
    	check(testData, timeLimit);
    	
    	//clean();
	}

	protected void check(String testData) throws InterruptedException, IOException {
		check(testData, 1000000);
	}
	
	@SuppressWarnings("unchecked")
	private void check(String testData, int timeLimit) throws InterruptedException, IOException {
		// wait for it to come out. 
		Date now = new Date();
		
    	File valid = new File(VALIDATION_HOME + "Valid\\");
    	File failed = new File(VALIDATION_HOME + "Errors\\");
    	while((new Date().getTime() - now.getTime() < timeLimit)&&FileUtils.listFiles(valid, new String[]{"xml","csv","txt"}, true).isEmpty()&&FileUtils.listFiles(failed, new String[]{"xml","csv","txt","msg"}, true).isEmpty()){
    		Thread.sleep(500);
    	}
    	
    	Collection<File> listFiles = FileUtils.listFiles(failed, new String[]{"xml","csv","txt","msg"}, true);
    	if (!listFiles.isEmpty()){
    		String message = "An invalid file was unexpectedly written after " + (new Date().getTime() - now.getTime()) + "ms. It contained the following: ";
    		for (File file : listFiles) {
    			message += FileUtils.readFileToString(file) + "; ";
			}
        	fail(message);
    	}
    	
    	listFiles = FileUtils.listFiles(valid, new String[]{"xml","csv","txt"}, true);
    	assertFalse("File wasn't written", listFiles.isEmpty());
    	assertEquals("Got the wrong number of files", 1, listFiles.size());
    	
    	// There are some schema changes, etc that happen before the 
    	// <BatchNo> tag. Everything else should be the same.
    	String readFileToString = FileUtils.readFileToString(listFiles.iterator().next());
		assertTrue("expected: " + readFileToString + " but got : " + testData,
       		StringUtils.substringAfter("<\\BatchNo>", StringUtils.deleteWhitespace(readFileToString)).contains(
    		StringUtils.substringAfter("<\\BatchNo>", StringUtils.deleteWhitespace(testData))) 
   		);
    	
    	assertTrue(readFileToString.contains("This file has been successfully verified"));
	}
        
    protected SQLServerDataSource dataSource;

    public void setReDataSource(SQLServerDataSource dataSource) {
		this.dataSource = dataSource;
	}
}
