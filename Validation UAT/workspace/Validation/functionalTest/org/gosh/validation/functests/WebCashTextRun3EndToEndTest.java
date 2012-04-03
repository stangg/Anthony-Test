package org.gosh.validation.functests;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.DefaultResourceLoader;

public class WebCashTextRun3EndToEndTest extends EndToEndTests{
    public void testWebCashCase() throws Exception {
    	Date now = new Date();
		clean();

		copyFileToInfolder(now, "WebCashTestRun3.txt");
		copyFileToInfolder(now, "WebCashTestRun3Donations.txt");

		check(contentOf("expectedWebCashOut.xml"));
		
		clean();
	}

    private String contentOf(String fileName) throws IOException{
    	return FileUtils.readFileToString(
    		new DefaultResourceLoader().getResource("classpath:"  + fileName).getFile()
    	);
    }
}
