package org.gosh.validation.functests;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.DefaultResourceLoader;

public class WebCashTextRun4EndToEndTest extends EndToEndTests{
    public void testWebCashCase() throws Exception {
    	Date now = new Date();
		clean();

		copyFileToInfolder(now, "WebCashTestRun4.txt");
		copyFileToInfolder(now, "WebCashTestRun4Donations.txt");

		check(contentOf("expectedWebCashOut.xml"));
		
		clean();
	}

    private String contentOf(String fileName) throws IOException{
    	return FileUtils.readFileToString(
    		new DefaultResourceLoader().getResource("classpath:"  + fileName).getFile()
    	);
    }
}
