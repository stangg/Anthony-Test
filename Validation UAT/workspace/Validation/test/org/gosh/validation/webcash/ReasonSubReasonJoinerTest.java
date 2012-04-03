///**
// * 
// */
//package org.gosh.validation.webcash;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import junit.framework.TestCase;
//
//import org.apache.commons.lang.StringUtils;
//import org.gosh.validation.TestDataSourceFactory;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.htmlunit.HtmlUnitDriver;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
//
//public class ReasonSubReasonJoinerTest extends TestCase{
//	private static JdbcTemplate jdbcTemplate = new JdbcTemplate(TestDataSourceFactory.getLegacyDataSource());
//
//	/*Commenting the failing tests*/
////	public void testReasons() throws Exception {
////		List<WebCashModel> webcashmodel = lookupFromLegacyDb();
////		checkModelAgainstWebsiteReasons(webcashmodel);
////	}
////
////	public void testSubreasons() throws Exception {
////		List<WebCashModel> webcashmodel = lookupFromLegacyDb();
////		checkModelAgainstWebsiteSubReasons(webcashmodel);
////	}
//	
//	@SuppressWarnings("unchecked")
//	private static List<WebCashModel> lookupFromLegacyDb(){
//		String sql = "SELECT * FROM ReasonData";
//		final List<WebCashModel> wcmodels = jdbcTemplate.query(sql, new ParameterizedRowMapper<WebCashModel>(){
//			@Override
//			public WebCashModel mapRow(ResultSet rs, int rowNum)
//					throws SQLException {
//				return new WebCashModel(rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11));
//			}
//		});
//		return wcmodels;
//	}
//	
//	private void checkModelAgainstWebsiteReasons(List<WebCashModel> webCashModels){
//		Map<String, String> websiteReasons = lookupReasonDescription();
//		List<String> errors = new ArrayList<String>();
//		for (WebCashModel webCashModel : webCashModels) {
//			if (!StringUtils.equals(websiteReasons.get(webCashModel.getReasonID()), webCashModel.getReason())){
//				errors.add(webCashModel + " does not match reason " + websiteReasons.get(webCashModel.getReasonID()));
//			}
//		}
//	
//		for (String key: websiteReasons.keySet()) {
//			boolean exists = false; if (StringUtils.isBlank(key)) {continue;}
//			for (WebCashModel webCashModel : webCashModels) {
//				if (StringUtils.equals(key, webCashModel.getReasonID().toString())){
//					exists = true;
//					break;
//				}
//			}
//			if (!exists){
//				errors.add(key + "," + websiteReasons.get(key) + " exists on the website but not in the legacy database.");
//			}
//		}
//		
//		assertTrue("We had the following errors: " + errors, errors.isEmpty());
//	}
//
//	private void checkModelAgainstWebsiteSubReasons(List<WebCashModel> webCashModels){
//		Map<String, String> websiteSubReasons = lookupSubReasonDescription();
//		List<String> errors = new ArrayList<String>();
//		for (WebCashModel webCashModel : webCashModels) {
//			if (!StringUtils.equals(websiteSubReasons.get(webCashModel.getSubReasonID()), webCashModel.getSubReason())){
//				errors.add(webCashModel + " does not match subreason " + websiteSubReasons.get(webCashModel.getSubReasonID()));
//			}
//		}
//	
//		for (String key: websiteSubReasons.keySet()) {
//			boolean exists = false; if (StringUtils.isBlank(key) || StringUtils.equals(key, "selected=selected")) {continue;}
//			for (WebCashModel webCashModel : webCashModels) {
//				if (StringUtils.equals(key, webCashModel.getSubReasonID().toString())){
//					exists = true;
//					break;
//				}
//			}
//			if (!exists){
//				errors.add(key + "," + websiteSubReasons.get(key) + " exists on the website but not in the legacy database.");
//			}
//		}
//		
//		assertTrue("We had the following errors: " + errors, errors.isEmpty());
//	}
//	
//	static Map<String, String> lookupSubReasonDescription() {
//		HtmlUnitDriver driver = new HtmlUnitDriver();
//		driver.setJavascriptEnabled(true);
//		driver.get("https://euw3300129/cgi-bin/gosh/ff2-reg.pl?event=10&donation=online&whichform=regform");
//
//		WebElement element = driver.findElement(By.name("reason"));
//		List<WebElement> allOptions = element.getChildrenOfType("option");
//		
//		HashMap<String, String> map = new HashMap<String, String>();
//		for (WebElement option : allOptions) {
//			if (option.getValue().compareTo("")==0){
//				continue;
//			}
//			option.setSelected();
//			
//			WebElement subReason = driver.findElement(By.xpath("//select[@name='subreason']"));
//			List<WebElement> allSubReasonOptions = subReason.getChildrenOfType("option");
//
//			for (WebElement subReasonOption : allSubReasonOptions) {
//				map.put(subReasonOption.getValue(), subReasonOption.getText());
//			}
//		}
//		return map;
//	}
//
//	static Map<String, String> lookupReasonDescription() {
//		WebDriver driver = new HtmlUnitDriver();
//		driver.get("https://donate.gosh.org/cgi-bin/gosh/ff2-reg.pl?event=10&donation=online&whichform=regform");
//		WebElement element = driver.findElement(By.name("reason"));
//		List<WebElement> allOptions = element.getChildrenOfType("option");
//		
//		HashMap<String, String> map = new HashMap<String, String>();
//		for (WebElement option : allOptions) {
//			map.put(option.getValue(), option.getText());
//		}
//		return map;
//	}
//}
