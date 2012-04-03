package org.gosh.validation.webcash;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Looks up from a local mapping of "reason and sub-reason codes" to 
 * RE codes and fundraiser email addresses. The reason and sub-reason
 * are things that the donor specify on the website. 
 */
public class LegacyDatabaseDAO implements LegacyDatabaseLookup{
	private JdbcTemplate jdbcTemplate;
	
	@SuppressWarnings("unchecked")
	@Override
	public String lookupCampaignAppealFundConstituentCodesAndContact(String reason, String subreason, final CashDonationCplxType donation, final DonorCplxType donor){
		String sql = "select CampaignCode, AppealCode, FundCode, PackageCode, FundraisingContact, ConsCode from ReasonData where " +
				"reasonid = " + reason;
		if (StringUtils.isNotEmpty(subreason)){
			sql += " and SubReasonID = " + subreason;
		}
				
		List<String> results = jdbcTemplate.query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException {
				donation.setCampaign(resultSet.getString(1));
				donation.setAppeal(resultSet.getString(2));
				donation.setFund(resultSet.getString(3));
				donation.setPackage(resultSet.getString(4) != null ? resultSet.getString(4) : "");
				// To enable reprocessing
				donation.getFundraisingContact().add(resultSet.getString(5));
				
				donor.getConsCodes().get(0).setCode(resultSet.getString(6));
				return resultSet.getString(5);
			}
		});
		return results.isEmpty()? null: results.get(0);
	}

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
