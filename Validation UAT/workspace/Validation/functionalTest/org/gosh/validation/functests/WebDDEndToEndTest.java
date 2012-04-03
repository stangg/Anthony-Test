package org.gosh.validation.functests;

import java.io.File;

import org.springframework.core.io.DefaultResourceLoader;

public class WebDDEndToEndTest extends EndToEndTests{
    public void testWebDDCase() throws Exception {
    	File testDataFile = new DefaultResourceLoader().getResource("classpath:firstFewLinesOfWebDDs_MariaTest.txt").getFile();
    	testEntireProcess(testDataFile, 630000);
	}
}
