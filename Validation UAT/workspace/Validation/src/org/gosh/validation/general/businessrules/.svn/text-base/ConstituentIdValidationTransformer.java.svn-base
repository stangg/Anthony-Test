package org.gosh.validation.general.businessrules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.validation.general.LoggingTransformer;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * Validates that any Constituent IDs that are already in the 
 * file actually exist in RE. 
 * 
 * @author Kevin.Savage
 */
public class ConstituentIdValidationTransformer {
	private Reporter reporter;
	private SimpleJdbcTemplate jdbcTemplate;
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message){
		log.info("Start Transformation");
		GOSHCC goshcc = message.getPayload();
		
		Set<String> idsFromFile = new HashSet<String>();
		
		List<DonorCplxType> donors = goshcc.getDonorCplxType();
		for (DonorCplxType donor : donors) {
			// throw out if there is an internal ID but no external one. 
			if (StringUtils.isNotBlank(donor.getInternalConstitID()) && StringUtils.isBlank(donor.getConstituentID())){
				return reporter.log(message, donor, "We had an internalId without a contituentID, this is probably wrong");
			} else if (StringUtils.isNotBlank(donor.getConstituentID())){
				idsFromFile.add(donor.getConstituentID());
			}
		}
		
		if (idsFromFile.isEmpty()){
			return message;
		}

		Set<Pair> pairsFromDB = lookupPairsFromDatabase(idsFromFile, message);
		Set<String> idsFromDB = new HashSet<String>();
		Map<String, String> pairMapping = new HashMap<String, String>();
		
		for (Pair pair : pairsFromDB) {
			idsFromDB.add(pair.getConstituentId());
			pairMapping.put(pair.getConstituentId(), pair.getId());
		}
		
		for (DonorCplxType donor : donors) {
			String value = pairMapping.get(donor.getConstituentID());
			if (StringUtils.isNotBlank(value)){
				donor.setInternalConstitID(value);
			}
		}

		// find the ones that are not in the database
		idsFromFile.removeAll(idsFromDB);
		if (!idsFromFile.isEmpty()){
			return reporter.log(message, "The ids in the file are not all in RE, we couldn't find: " + idsFromFile);
		}
		log.info("End of Transformation");
		return message;
	}
	
	private Set<Pair> lookupPairsFromDatabase(Set<String> idsFromFile, Message<GOSHCC> message) {
		String sql = null;
		Set<Pair> pairsFromDB = new HashSet<Pair>();
		try {
		
			sql = "select CONSTITUENT_ID, ID from records where ";
			
			int count = 0;
			for (String constituentId : idsFromFile) {
				if (constituentId != null){
					sql += (count++==0?"":"or") + " (CONSTITUENT_ID = '" + constituentId + "' )";
				}
			}
			
			pairsFromDB = new HashSet<Pair>(jdbcTemplate.query(sql, ParameterizedBeanPropertyRowMapper.newInstance(Pair.class)));
		} catch (Exception e) {
			System.out.println(e);
			reporter.log(message, "We tried to execute the sql " + sql + "but there was a problem relating to the database: " + e.getMessage());
		}
		return pairsFromDB;
	}

	public static final class Pair{
		String id;
		String constituentId;
		
		public Pair() {
		}
		
		public Pair(String constituentID, String internalConstitID) {
			constituentId = constituentID;
			id = internalConstitID;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public void setConstituentId(String constituentId) {
			this.constituentId = constituentId;
		}
		
		public String getConstituentId() {
			return constituentId;
		}
		
		public String getId() {
			return id;
		}

		@Override
		public boolean equals(Object arg0) {
			return EqualsBuilder.reflectionEquals(this, arg0);
		}
	}
	
	@Autowired
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
	
	@Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }
}
