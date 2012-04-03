package org.gosh.validation.general.batchlog;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCountCallbackHandler;

/**
 * This validates the batch number in the file against the 
 * batch numbers in RE across all previously existing files. 
 * 
 * @author Kevin.Savage
 */
public class REBatchNumberValidationTransformer {
	private JdbcTemplate jdbcTemplate;
	private Reporter reporter;
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) {
		log.info("Start Transformation");
		GOSHCC payload = message.getPayload();
		String batchNo = payload.getBatchNo();
		
		if (batchNo == null){
			return reporter.log(message, "There is a null batch number in this file.");
		}
		
		String sql = "select * from GIFT where gift.BATCH_NUMBER = '"+batchNo+"'";

		RowCountCallbackHandler countCallback = new RowCountCallbackHandler();
		jdbcTemplate.query(sql, countCallback);
		if (countCallback.getRowCount()>0){
			return reporter.log(message, "In RE there was already a batch number of " + batchNo);
		}
		log.info("End of Transformation");
		return message;
	}
	
	@Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Autowired @Required
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
}
