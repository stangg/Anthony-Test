/**
 * 
 */
package org.gosh.validation.functests;

import java.io.File;

import org.springframework.core.io.DefaultResourceLoader;

/**
 * @author gayathri.polavaram
 *
 */
public class BimonthlyPledgesEndtoEndTest extends EndToEndTests{

	public void testCase34() throws Exception {
    	File testDataFile = new DefaultResourceLoader().getResource("classpath:DDUpgrade_ValImp_2011060836_40_01.xml").getFile();
    	testEntireProcess(testDataFile);
    	
    	testDataFile = new DefaultResourceLoader().getResource("classpath:DDUpgrade_ValImp_2011060837_40_01.xml").getFile();
    	testEntireProcess(testDataFile);
	}
}
