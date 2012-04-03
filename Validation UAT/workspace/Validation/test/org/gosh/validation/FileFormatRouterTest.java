package org.gosh.validation;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.gosh.validation.general.FileFormatRouter;
import org.springframework.core.io.DefaultResourceLoader;

import junit.framework.TestCase;

public class FileFormatRouterTest extends TestCase {
	public void testRouteREImportFile() throws Exception {
		FileFormatRouter router = new FileFormatRouter();

		File testDataFile = new DefaultResourceLoader().getResource("classpath:testdata.xml").getFile();
		assertEquals("messageIn", router.route(FileUtils.readFileToString(testDataFile)));
	}
	
	public void testWebDonationsDonorFile() throws Exception {
		FileFormatRouter router = new FileFormatRouter();
		
    	File testDataFile = new DefaultResourceLoader().getResource("classpath:firstFewLinesOfdonors.txt").getFile();
		assertEquals("webDonations", router.route(FileUtils.readFileToString(testDataFile)));
	}

	public void testWebDonationsDonoationsFile() throws Exception {
		FileFormatRouter router = new FileFormatRouter();

		File testDataFile = new DefaultResourceLoader().getResource("classpath:firstFewLinesOfdonations.txt").getFile();
		assertEquals("webDonations", router.route(FileUtils.readFileToString(testDataFile)));
	}

	public void testWebDDsFile() throws Exception {
		FileFormatRouter router = new FileFormatRouter();

		File testDataFile = new DefaultResourceLoader().getResource("classpath:firstFewLinesOfWebDDs_MariaTest.txt").getFile();
		assertEquals("webGeneratedDDs", router.route(FileUtils.readFileToString(testDataFile)));
	}
}
