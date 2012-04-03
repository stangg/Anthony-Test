/**
 * 
 */
package org.gosh.convertion;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.ArrayUtils;
import org.gosh.validation.convertion.AbstractCsvConverter;
import org.gosh.validation.webcash.WebDonationsAggregator;
import org.gosh.validation.webdds.WebGeneratedDDsTransformer;

import junit.framework.TestCase;

public class AbstractCsvConverterTest extends TestCase {
	public void testGetFields() throws Exception {
		AbstractCsvConverter webGeneratedDDsApplication = new WebGeneratedDDsTransformer();
		String testDonorLine = "field0,\"field1 pt 1, field1 pt 2\",field2";
		String[] fields = webGeneratedDDsApplication.getFields(testDonorLine, ",");
		
		assertEquals("field0", fields[0]);
		assertEquals("field1 pt 1, field1 pt 2", fields[1]);
		assertEquals("field2", fields[2]);
	}
	
	public void testGetFieldsWithLongSeperator() throws Exception {
		AbstractCsvConverter webGeneratedDDsApplication = new WebGeneratedDDsTransformer();
		String testDonorLine = "\"field0\"	\"field1\"	\"field2\"";
		String[] fields = webGeneratedDDsApplication.getFields(testDonorLine, "\"\t\"");
		
		assertEquals("field0", fields[0]);
		assertEquals("field1", fields[1]);
		assertEquals("field2", fields[2]);
	}
	
	public void testGetFieldsWithTabSeperatorOnly() throws Exception {
		AbstractCsvConverter webGeneratedDDsApplication = new WebGeneratedDDsTransformer();
		String testDonorLine = "field0	field1	field2";
		String[] fields = webGeneratedDDsApplication.getFields(testDonorLine, "\t");
		
		assertEquals("field0", fields[0]);
		assertEquals("field1", fields[1]);
		assertEquals("field2", fields[2]);
	}
	
	public void testGetFieldsWithARealCase() throws Exception {
		String test = "2008-01-04,15:38:29,6654,PDD004260,63.29,Direct Marketing Online Direct Debit,FQ10,FQ1008,Miss,Ann,,Harvey,157 Grove Road,,,HARPENDEN,Hertfordshire,AL5 1SY,GBR,Miss A M Harvey,404773,22894173,5.00,,0.00,0.00,5.00,0.00,Y,2008-02-05,Monthly,\"HSBC BANK PLC, FIRST DIRECT, LEEDS\",40 Wakefield Road,,,Leeds,,LS98 1FD,,,,,annm.harvey@googlemail.com,N,N,N,N,Y,N,,,1971-05-02,,,Special TV Ad,UK Style,Other,Female,,,,,,";
		AbstractCsvConverter webGeneratedDDsApplication = new WebGeneratedDDsTransformer();
		String[] fields = webGeneratedDDsApplication.getFields(test, ",");
		
		assertEquals(64, fields.length);
		assertTrue(ArrayUtils.contains(fields, "HSBC BANK PLC, FIRST DIRECT, LEEDS"));
	}
	
	public void testGetFieldsWithARealCaseWithDoubleProblem() throws Exception {
		String test = "2008-01-03,13:48:24,6639,PDD004220,63.29,Direct Marketing Online Direct Debit,FQ10,FQ1008,Miss,Anita,,Williams,10 Kings Mead,South Nutfield,,REDHILL,,RH1 5NN,GBR,Miss A Williams,606002,69675457,3.00,,0.00,0.00,3.00,0.00,Y,2008-02-05,Monthly,\"NAT WEST BANK PLC, KINGSTON MARKET PLACE\",Chatham Customer Service Centre,Western Avenue,\"Waterside, Chatham Maritime\",Chatham,Kent,ME4 4RT,,,,,anita.williams@dhl.com,N,N,N,N,Y,N,,,1975-05-11,,,Special TV Ad,ITV1 West,No connection to GOSH but support it's great work,Female,,,,,,";
		AbstractCsvConverter webGeneratedDDsApplication = new WebGeneratedDDsTransformer();
		String[] fields = webGeneratedDDsApplication.getFields(test, ",");
		
		assertEquals(64, fields.length);
		assertTrue(ArrayUtils.contains(fields, "NAT WEST BANK PLC, KINGSTON MARKET PLACE"));
		assertTrue(ArrayUtils.contains(fields, "Waterside, Chatham Maritime"));
	}
	
	public void testMinusSixFiscalYears() throws Exception {
		AbstractCsvConverter webCashApplication = new WebDonationsAggregator();
		XMLGregorianCalendar dateMinusSixFiscalYears = webCashApplication.minusSixFiscalYears(DatatypeFactory.newInstance().newXMLGregorianCalendar(2009, 8, 12, 1, 0, 0, 0, 0));
		XMLGregorianCalendar expectedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(2003, 4, 6, 1, 0, 0, 0, 0);
	
		assertEquals(expectedDate, dateMinusSixFiscalYears);
	}
}
