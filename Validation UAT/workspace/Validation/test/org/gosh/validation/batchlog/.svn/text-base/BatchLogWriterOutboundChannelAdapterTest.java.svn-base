package org.gosh.validation.batchlog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.validation.TestDataSourceFactory;
import org.gosh.validation.general.batchlog.BatchLogWriterOutboundChannelAdapter;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.message.GenericMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowCountCallbackHandler;

public class BatchLogWriterOutboundChannelAdapterTest extends TestCase {
	private JdbcTemplate jdbcTemplate;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.jdbcTemplate = new JdbcTemplate(TestDataSourceFactory.getCIIADataSource());

		jdbcTemplate.execute("delete from supplierBatchNumberLogTest where SupplierName = 'testID'");
	}
	
	public void testNonUpdateOfEmptyModel() throws Exception {
		BatchLogWriterOutboundChannelAdapter adapter = new BatchLogWriterOutboundChannelAdapter();
		adapter.setDataSource(TestDataSourceFactory.getCIIADataSource());

		RowCountCallbackHandler originalRowCountCallbackHandler = new RowCountCallbackHandler();
		jdbcTemplate.query("select * from supplierBatchNumberLogTest", originalRowCountCallbackHandler);

		adapter.writeToDatabase(new GenericMessage<GOSHCC>(new GOSHCC()));
		
		RowCountCallbackHandler finalRowCountCallbackHandler = new RowCountCallbackHandler();
		jdbcTemplate.query("select * from supplierBatchNumberLogTest", finalRowCountCallbackHandler);
		
		assertEquals(originalRowCountCallbackHandler.getRowCount(), finalRowCountCallbackHandler.getRowCount());
	}

	public void testUpdate() throws Exception {
		BatchLogWriterOutboundChannelAdapter adapter = new BatchLogWriterOutboundChannelAdapter();
		adapter.setDataSource(TestDataSourceFactory.getCIIADataSource());
		adapter.setSupplierBatchNumberLog("supplierBatchNumberLogTest");
		
		GOSHCC goshcc = new GOSHCC();
		final String time = String.valueOf(new Date().getTime());
		goshcc.setBatchNo(String.valueOf(time));
		goshcc.setSupplierID("testID");
		goshcc.getDonorCplxType().add(new DonorCplxType());
		
		HashMap<String, Object> headers = new HashMap<String, Object>();
		headers.put(FileHeaders.FILENAME, "fileName");
		
		adapter.writeToDatabase(new GenericMessage<GOSHCC>(goshcc, headers));
		
		jdbcTemplate.query("select * from supplierBatchNumberLogTest where SupplierName = 'testID'", new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				assertEquals("fileName", rs.getString(1));
				assertEquals(time, rs.getString(2));
				assertEquals(1, rs.getInt(4));
				assertEquals("testID", rs.getString(5));
			}
		});
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		jdbcTemplate.execute("delete from SupplierBatchNumberLogTest where SupplierName = 'testID'");
	}
}
