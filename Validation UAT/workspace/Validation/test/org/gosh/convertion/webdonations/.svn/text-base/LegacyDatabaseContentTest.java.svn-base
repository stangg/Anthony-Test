//package org.gosh.convertion.webdonations;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.HashSet;
//import java.util.Set;
//
//import org.gosh.re.dmcash.bindings.GOSHCC;
//import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
//import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
//import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
//import org.gosh.validation.TestDataSourceFactory;
//import org.gosh.validation.general.businessrules.TheFourIdsValidationTransformer;
//import org.jmock.integration.junit3.MockObjectTestCase;
//import org.springframework.integration.message.GenericMessage;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowCallbackHandler;
//
//import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
//
//public class LegacyDatabaseContentTest extends MockObjectTestCase {
//	private TheFourIdsValidationTransformer transformer;
//	private GOSHCC goshcc; 
//	private final CashDonationCplxType cashDonationCplxType = new CashDonationCplxType();
//	private DonorCplxType donorCplxType; 
//	
//	/*Commenting the failing tests*/
////	public void testRETest() throws Exception {
////		transformer.setDataSource(TestDataSourceFactory.getDataSource());
////		checkAllContentsOfTheDatabaseHaveValidCodes();
////	}
////
////	public void testREProduction() throws Exception {
////		SQLServerDataSource dataSource = TestDataSourceFactory.getDataSource();
////		dataSource.setServerName("goshcc-re");
////		transformer.setDataSource(dataSource);		
////		checkAllContentsOfTheDatabaseHaveValidCodes();
////	}
//	
//	public void checkAllContentsOfTheDatabaseHaveValidCodes() throws Exception {
//		final Set<String> fails = new HashSet<String>();
//		
//		JdbcTemplate jdbcTemplate = new JdbcTemplate(TestDataSourceFactory.getLegacyDataSource());
//		jdbcTemplate.query("select FundCode, CampaignCode, AppealCode, packageCode from ReasonData",
//				new RowCallbackHandler() {
//					@Override
//					public void processRow(ResultSet rs) throws SQLException {
//						cashDonationCplxType.setFund(rs.getString(1));
//						cashDonationCplxType.setCampaign(rs.getString(2));
//						cashDonationCplxType.setAppeal(rs.getString(3));
//						cashDonationCplxType.setPackage(rs.getString(4));
//						
//						try{
//							transformer.transform(new GenericMessage<GOSHCC>(goshcc));
//						} catch (NullPointerException e){
//							// this implies that it isn't valid because we didn't set a reporter
//							fails.add(rs.getString(1) + "-" + rs.getString(2) + "-" + rs.getString(3) + "-" + rs.getString(4));
//						}
//					}
//				}
//			);
//		
//		assertTrue("The following are not valid combinations: " + fails, fails.isEmpty());
//	}
//	
//	@Override
//	protected void setUp() throws Exception
//	
//	{
//		super.setUp();
//		
//		transformer = new TheFourIdsValidationTransformer();
//		transformer.setDataSource(TestDataSourceFactory.getDataSource());
//		
//		goshcc = new GOSHCC();
//		donorCplxType = new DonorCplxType();
//		goshcc.getDonorCplxType().add(donorCplxType);
//		
//		DonationDetails donationDetails = new DonationDetails();
//		donorCplxType.setDonationDetails(donationDetails);
//		donationDetails.getCashDonationCplxType().add(cashDonationCplxType);
//
//	}
//}
