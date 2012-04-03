package org.gosh.validation.tributes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.Tribute;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * This makes adjustments to the importID so that it is short
 * enough to import. This is done here due to an RE limitation and the 
 * fact that EIBS use SQL Server GUIDs (amazing that re cannot accept
 * these as it runs on SQL Server). There is an additional check that
 * the id is still unique.
 * 
 * This also truncates tribute ids on gifts so that they match again.
 * 
 * @author Kevin.Savage
 */
public class TributeImportIdTransformer {
	private Reporter reporter;
	private SimpleJdbcTemplate jdbcTemplate;
	protected Log log = LogFactory.getFactory().getInstance(this.getClass());
	
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) {
		log.info("Start Transformation");
		List<Tribute> tributes = message.getPayload().getTribute();
		for (Tribute tribute : tributes) {
			tribute.setImportID(StringUtils.substring(tribute.getImportID(),0,20));
		}
		
		List<String> messages = new ArrayList<String>();
		for (Tribute tribute : tributes) {
			int queryForInt = jdbcTemplate.queryForInt("select count(1) from TRIBUTE where IMPORT_ID = '" + tribute.getImportID() + "'");
			if (queryForInt>0){
				messages.add("There was an import id on a tribute that in its truncated form was not unique. This id was: " + tribute.getImportID() + " (truncated)");
			}
		}
		
		truncateAndValidateDonationTributeIds(message, messages);
		
		if (messages.isEmpty()){
			return message;
		}
		log.info("End of Transformation");
		return reporter.log(message, messages);
	}

	@SuppressWarnings("unchecked")
	private void truncateAndValidateDonationTributeIds(Message<GOSHCC> message, List<String> messages) {
		JXPathContext context = JXPathContext.newContext(message.getPayload());
		
		Iterator<CashDonationCplxType> donations = context.iterate("/donorCplxType/donationDetails/cashDonationCplxType");
		while (donations.hasNext()){
			CashDonationCplxType next = donations.next();
			next.setRefNo(StringUtils.substring(next.getRefNo(),0,20));
			if (StringUtils.isNotBlank(next.getTributeID())){
				next.setTributeID(StringUtils.substring(next.getTributeID(),0,20));
				if (thereNoTributeInTheFileWithID(message, next.getTributeID()) &&
					jdbcTemplate.queryForInt("select count(1) from TRIBUTE where IMPORT_ID = '" + next.getTributeID() + "'")==0)
				{
					messages.add("There was a donation with a tribute ID of " + next.getTributeID() + " but this is not a valid tribute id in either of the RE database or this file");
				}
			}
		}
	}
	
	private boolean thereNoTributeInTheFileWithID(Message<GOSHCC> message, String tributeID) {
		List<Tribute> tributes = message.getPayload().getTribute();
		for (Tribute tribute : tributes) {
			if (StringUtils.equals(tributeID, tribute.getImportID())){
				return false;
			}
		}
		return true;
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
