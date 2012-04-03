package org.gosh.validation.functests;

import java.io.File;

import org.springframework.core.io.DefaultResourceLoader;

public class CAFEndToEndTest extends EndToEndTests{
    public void testValidCAFCase() throws Exception {
    	File testDataFile = new DefaultResourceLoader().getResource("classpath:testdata-caf.xml").getFile();
    	testEntireProcess(testDataFile, 200000);
	}
}
