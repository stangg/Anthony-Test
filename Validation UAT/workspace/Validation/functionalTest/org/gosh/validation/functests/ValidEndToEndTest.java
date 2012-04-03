package org.gosh.validation.functests;

import java.io.File;

import org.springframework.core.io.DefaultResourceLoader;

public class ValidEndToEndTest extends EndToEndTests{
    public void testValidCase() throws Exception {
    	File testDataFile = new DefaultResourceLoader().getResource("classpath:testdata.xml").getFile();
    	testEntireProcess(testDataFile);
	}
}
