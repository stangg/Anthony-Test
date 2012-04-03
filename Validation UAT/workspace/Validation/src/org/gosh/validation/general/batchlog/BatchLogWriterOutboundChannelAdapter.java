package org.gosh.validation.general.batchlog;

import java.sql.Date;

import javax.sql.DataSource;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.springframework.integration.core.Message;
import org.springframework.integration.file.FileHeaders;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * This writes to the CIIA database on goshcc-legacy.
 * It replicates behaviour that was previously manual.
 * 
 * @author Kevin.Savage
 *
 */
public class BatchLogWriterOutboundChannelAdapter {
	private JdbcTemplate jdbcTemplate;
	private String supplierBatchNumberLog;

	public void writeToDatabase(Message<GOSHCC> message) {
		String filename = (String) message.getHeaders().get(FileHeaders.FILENAME);
		GOSHCC payload = message.getPayload();

		String batchNo = payload.getBatchNo();
		int noOfRecords = payload.getDonorCplxType().size();
		String supplierID = payload.getSupplierID();
		
		if (batchNo != null && filename != null && supplierID != null){
			String sql = "insert into " + supplierBatchNumberLog + " values " +
				"('" + filename + "', '" + batchNo + "', " +
				"'" + new Date(new java.util.Date().getTime()).toString() + "', " +
				noOfRecords + ", '" + supplierID + "')";
				
			jdbcTemplate.execute(sql);
		}
	}
	
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
    
    public void setSupplierBatchNumberLog(String supplierBatchNumberLog) {
		this.supplierBatchNumberLog = supplierBatchNumberLog;
	}
}