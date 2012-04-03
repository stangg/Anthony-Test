package org.gosh.validation.xmas;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.NonConstituentOrganisationRelationship;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * This removes nc relations where they already exist. 
 * 
 * In the future it may create constituent relations where
 * we are able to find a match.
 * 
 * @author Kevin.Savage
 */
public class NonConstituentuentTransformer {
	private Reporter reporter;
	private SimpleJdbcTemplate jdbcTemplate;
	
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message){
		List<NonConstituentOrganisationRelationship> toRemove = new ArrayList<NonConstituentOrganisationRelationship>();
		
		for (DonorCplxType donor : message.getPayload().getDonorCplxType()) {
			List<NonConstituentOrganisationRelationship> relationships = donor.getNonConstituentOrganisationRelationship();
			if (!relationships.isEmpty()){
				String sql = "select records.ORG_NAME from records, CONSTIT_RELATIONSHIPS where records.KEY_INDICATOR = 'O' " + 
					"and records.ID = CONSTIT_RELATIONSHIPS.RELATION_ID " + 
					"and CONSTIT_RELATIONSHIPS.CONSTIT_ID = " + donor.getInternalConstitID();
	
				List<String> names = jdbcTemplate.query(sql, 
						new ParameterizedRowMapper<String>() {
							@Override
							public String mapRow(ResultSet rs, int rowNum) throws SQLException {
								return rs.getString(1);
							}
						}
					);
				
				for (NonConstituentOrganisationRelationship relationship : relationships) {
					if (names.contains(relationship.getName())){
						toRemove.add(relationship);
					}
				}
				relationships.removeAll(toRemove);
			}
		}
		
		if (toRemove.isEmpty()){
			return message;
		}
		
		String errorMessage = "We just removed non constit relations because they already exist. We removed relations with the following names: ";
		for (NonConstituentOrganisationRelationship relationship : toRemove) {
			errorMessage += (relationship.getName() + ",");
		}
		return reporter.log(message, errorMessage);
	}
	
	@Autowired @Required 
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
	@Autowired @Required
	public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
}
