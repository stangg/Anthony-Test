/**
 * 
 */
package org.gosh.validation.general.bank;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import junit.framework.TestCase;

/**
 * @author gayathri.polavaram
 *
 */
public class BankDataAccessTest extends TestCase {
	
	private BankDataAccess classToTest;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		classToTest = new BankDataAccess(new SQLServerDataSource());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.gosh.validation.general.bank.BankDataAccess#escapeApostrophe(java.lang.String)}.
	 */
	public void testEscapeApostrophe() {
		String testString = "Barclay's";
		String expected = "Barclay''s";
		assertEquals("Apostrophe not escaped", expected, classToTest.escapeApostrophe(testString));
		
		testString = "'In the beginning";
		expected = "''In the beginning";
		assertEquals("Apostrophe not escaped", expected, classToTest.escapeApostrophe(testString));

		testString = "'Two occurance's";
		expected = "''Two occurance''s";
		assertEquals("Apostrophe not escaped", expected, classToTest.escapeApostrophe(testString));

		testString = "Consequtive occurances''";
		expected = "Consequtive occurances''''";
		assertEquals("Apostrophe not escaped", expected, classToTest.escapeApostrophe(testString));
		
		testString = "''";
		expected = "''''";
		assertEquals("Apostrophe not escaped", expected, classToTest.escapeApostrophe(testString));
		
		testString = "";
		expected = "";
		assertEquals("Apostrophe not escaped", expected, classToTest.escapeApostrophe(testString));
		
		
		testString = null;
		expected = null;
		assertEquals("Apostrophe not escaped", expected, classToTest.escapeApostrophe(testString));
		
	}
}
