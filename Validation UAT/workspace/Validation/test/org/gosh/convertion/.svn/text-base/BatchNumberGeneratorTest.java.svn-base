package org.gosh.convertion;

import org.gosh.validation.TestDataSourceFactory;
import org.gosh.validation.convertion.DatabaseBatchNumberGenerator;

import junit.framework.TestCase;

public class BatchNumberGeneratorTest extends TestCase {
	public void test() throws Exception {
		DatabaseBatchNumberGenerator generator = setUpGenerator("7760000", "7780000");
		generator.setEndOfRange("7780000");
		
		int batchNumber = generator.getBatchNumberFromReAndLog();
		assertTrue("batch no should have been less than 7780000", batchNumber<7780000);
		assertTrue("batch no should have been more than 7760000", batchNumber>7760000);
	}
	
	public void testMinimumNot1() throws Exception {
		DatabaseBatchNumberGenerator generator = setUpGenerator("6400000", "6490000");
		
		int batchNumber = generator.getBatchNumberFromReAndLog();
		assertEquals(6400000, batchNumber);
	}
	
	public void testWithLongStartAndEnd() throws Exception {
		DatabaseBatchNumberGenerator generator = setUpGenerator("629000000", "630000000");
		
		int batchNumber = generator.getBatchNumberFromReAndLog();
		assertTrue("was below min expected", 629000000<batchNumber);
		assertTrue("was above max expected", 630000000>batchNumber);
	}

	private DatabaseBatchNumberGenerator setUpGenerator(String startOfRange, String endOfRange) {
		DatabaseBatchNumberGenerator generator = new DatabaseBatchNumberGenerator();
		generator.setDataSource(TestDataSourceFactory.getDataSource());
		generator.setLogDataSource(TestDataSourceFactory.getCIIADataSource());
		generator.setSupplierBatchNumberLog("supplierBatchNumberLogTest");
		generator.setStartOfRange(startOfRange);
		generator.setEndOfRange(endOfRange);
		return generator;
	}
}
