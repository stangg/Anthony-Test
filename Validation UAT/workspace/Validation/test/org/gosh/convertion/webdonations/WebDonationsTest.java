package org.gosh.convertion.webdonations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.CashDonationCplxType;
import org.gosh.validation.convertion.BatchNumberGenerator;
import org.gosh.validation.general.error.ErrorReporter;
import org.gosh.validation.webcash.LegacyDatabaseLookup;
import org.gosh.validation.webcash.WebDonationsAggregator;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.springframework.core.io.DefaultResourceLoader;

public class WebDonationsTest extends MockObjectTestCase{
	@SuppressWarnings({ "serial"})
	public void testWithSomeFilesWeJustDownloaded() throws Exception{
		final LegacyDatabaseLookup legacyDatabaseLookup = mock(LegacyDatabaseLookup.class);
		final BatchNumberGenerator batchNumberGenerator = mock(BatchNumberGenerator.class);
		
		WebDonationsAggregator webDonationsApplication = new WebDonationsAggregator();
		webDonationsApplication.setLegacyDao(legacyDatabaseLookup);
		webDonationsApplication.setBatchNumberGenerator(batchNumberGenerator);

		webDonationsApplication.setReporter(new ErrorReporter());
		
		checking(new Expectations(){{
			atLeast(1).of(legacyDatabaseLookup).lookupCampaignAppealFundConstituentCodesAndContact(with(any(String.class)), with(any(String.class)), with(any(CashDonationCplxType.class)), with(any(DonorCplxType.class)));
			will(returnValue("contact"));
			oneOf(batchNumberGenerator).getBatchNumberFromReAndLog();
			will(returnValue(5));
		}});
		
		final File donorFile = new DefaultResourceLoader().getResource("classpath:firstFewLinesOfdonors.txt").getFile();
		final File donationsFile = new DefaultResourceLoader().getResource("classpath:firstFewLinesOfdonations.txt").getFile();
		// you can uncomment these to run a fuller test
		// final File donorFile = new File("donors.txt");
		// final File donationsFile = new File("donations.txt");
		webDonationsApplication.aggregate(new ArrayList<String>(){{add(FileUtils.readFileToString(donorFile)); add(FileUtils.readFileToString(donationsFile));}});
	}
	
	public void testCompletionStrategy() throws Exception {
		String donorFile = FileUtils.readFileToString(new DefaultResourceLoader().getResource("classpath:firstFewLinesOfdonors.txt").getFile());
		String donationsFile = FileUtils.readFileToString(new DefaultResourceLoader().getResource("classpath:firstFewLinesOfdonations.txt").getFile());

		List<String> fileList = new ArrayList<String>();
		WebDonationsAggregator webDonationsApplication = new WebDonationsAggregator();
		assertFalse(webDonationsApplication.checkCompleteness(fileList));
		
		fileList.add(donationsFile);
		assertFalse(webDonationsApplication.checkCompleteness(fileList));

		fileList.add(donorFile);
		assertTrue("we expected the file list to be complete but was: " + fileList, webDonationsApplication.checkCompleteness(fileList));
	}
}
