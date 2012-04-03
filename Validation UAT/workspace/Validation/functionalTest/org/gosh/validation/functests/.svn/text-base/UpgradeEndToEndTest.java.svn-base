package org.gosh.validation.functests;

import java.io.File;

import org.springframework.core.io.DefaultResourceLoader;

public class UpgradeEndToEndTest extends EndToEndTests{
    public void testValidUpgradeCase() throws Exception {
    	File testDataFile = new DefaultResourceLoader().getResource("classpath:testdata-upgrade.xml").getFile();
    	testEntireProcess(testDataFile);
	}
}
