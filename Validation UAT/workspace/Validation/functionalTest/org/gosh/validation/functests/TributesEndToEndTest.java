package org.gosh.validation.functests;

import java.io.File;

import org.springframework.core.io.DefaultResourceLoader;

public class TributesEndToEndTest extends EndToEndTests{
    public void testValidCase() throws Exception {
    	File testDataFile = new DefaultResourceLoader().getResource("classpath:Tribute_combined_testing_07052009_A.xml").getFile();
    	testEntireProcess(testDataFile);
	}
}
