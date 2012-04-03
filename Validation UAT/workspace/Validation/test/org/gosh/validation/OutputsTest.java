package org.gosh.validation;
import junit.framework.TestCase;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class OutputsTest extends TestCase {
    protected String[] getConfigLocations() {
        return new String[] { "classpath:outputs.xml" };
    }
    
    public void test() throws Exception {
		// just check that the config is valid (currently may increase in the future).
	}
}
