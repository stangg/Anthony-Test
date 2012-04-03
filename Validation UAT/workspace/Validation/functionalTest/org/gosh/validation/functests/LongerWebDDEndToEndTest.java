package org.gosh.validation.functests;

import java.io.File;

import org.springframework.core.io.DefaultResourceLoader;

public class LongerWebDDEndToEndTest extends EndToEndTests{
    public void testWebDDCaseWithFullFile() throws Exception {
    	File testDataFile = new DefaultResourceLoader().getResource("classpath:paperlessdd_goshcc_all_20090401-20090429_allbasic.csv").getFile();
    	testEntireProcess(testDataFile, 420000);
	}
}
