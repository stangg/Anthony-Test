package org.gosh.validation.tributes;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;
import static org.apache.commons.lang.StringUtils.removeEnd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * In Ginelles test plan was written the following:
 * 
 * An assigned unique ID or approval code should be on each gift 
 * at webpage. When file is received by utility - utility should 
 * identify duplicate ID or approval code to avoid duplicate 
 * entries being imported into database.
 * 
 * @author Kevin.Savage
 */
public class GiftReferenceNoRecogniserTransformer {
	private SimpleJdbcTemplate jdbcTemplate;
	private Reporter reporter;
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) {
		log.info("Start Transformation");
		GOSHCC payload = message.getPayload();
		List<String> refNos = new ArrayList<String>();
		for (DonorCplxType donor : payload.getDonorCplxType()) {
			DonationDetails donationDetails = donor.getDonationDetails();
			if (donationDetails != null){
				refNos.addAll(collect(donationDetails.getCashDonationCplxType(), on(CashDonationCplxType.class).getRefNo()));
			}
		}
		if (refNos.isEmpty()){
			return message;
		}
		
		String sql = "select reference_number from gift where reference_number in (";
		for (String refNo : refNos) {
			sql += "'" + refNo + "',";
		}
		sql = removeEnd(sql, ",");
		sql += ")";
		
		List<String> list = jdbcTemplate.query(sql, new ParameterizedRowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});
		
		
		if (!list.isEmpty()){
			return reporter.log(message, "The following gifts have ref nos that are already in the database: " + new HashSet<String>(list));
		}
		log.info("End of Transformation");
		return message;
	}
	

	@Autowired @Required
	public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}

	@Autowired @Required
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
}
