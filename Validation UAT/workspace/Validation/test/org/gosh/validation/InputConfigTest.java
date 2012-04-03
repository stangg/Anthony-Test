package org.gosh.validation;
import junit.framework.TestCase;

public class InputConfigTest extends TestCase{
    protected String[] getConfigLocations() {
        return new String[] { "classpath:config.xml", "classpath:input.xml" };
    }
    
    public void test() throws Exception {
		// just check that the config is valid (currently may increase in the future).
	}
}
