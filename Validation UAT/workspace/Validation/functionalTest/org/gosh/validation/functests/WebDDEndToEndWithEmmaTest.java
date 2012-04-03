package org.gosh.validation.functests;

import java.io.File;

import org.springframework.core.io.DefaultResourceLoader;

public class WebDDEndToEndWithEmmaTest extends EndToEndTests{
    public void testWebDDCase() throws Exception {
    	File testDataFile = new DefaultResourceLoader().getResource("classpath:paperlessdd_goshcc_all_20090401-20090510_allbasic.csv").getFile();
    	testEntireProcess(testDataFile, 420000);
	}
}
