package org.gosh.validation.convertion;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * Looks up in RE and the BatchLog database table to make sure that 
 * generated batch numbers are unique.
 */
public class DatabaseBatchNumberGenerator implements BatchNumberGenerator {
	private SimpleJdbcTemplate logJdbcTemplate;
	private SimpleJdbcTemplate reJdbcTemplate;
	private String supplierBatchNumberLog;
	private String startOfRange;
	private String endOfRange;

	@Override
	public int getBatchNumberFromReAndLog(){
		return Math.max(Integer.valueOf(startOfRange), 
			Math.max(
				logJdbcTemplate.queryForInt("select max(batchno) from " + supplierBatchNumberLog + " where batchno like '" + calculatePattern() +"' and batchno between '"+ startOfRange +"' and '"+ endOfRange +"'") + 1,
				reJdbcTemplate.queryForInt("select max(batch_number) from gift where batch_number like '" + calculatePattern() + "' and batch_number between '"+ startOfRange +"' and '"+ endOfRange +"'") + 1
			)
		);
	}
	
	private String calculatePattern() {
		String pattern = startOfRange.substring(0,2);
		for (int i = 0; i<startOfRange.length()-2; i++){
			pattern += "_";
		}
		return pattern;
	}

	@Required @Autowired
	public void setDataSource(DataSource dataSource) {
		this.reJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	
	@Required 
	public void setLogDataSource(DataSource dataSource) {
		this.logJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	
    @Required
    public void setSupplierBatchNumberLog(String supplierBatchNumberLog) {
		this.supplierBatchNumberLog = supplierBatchNumberLog;
	}
    
    @Required
	public void setStartOfRange(String startOfRange) {
		this.startOfRange = startOfRange;
	}
	
    @Required
	public void setEndOfRange(String endOfRange) {
		this.endOfRange = endOfRange;
	}
}
