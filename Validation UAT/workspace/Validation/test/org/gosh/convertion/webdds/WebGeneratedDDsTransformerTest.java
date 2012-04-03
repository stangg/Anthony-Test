package org.gosh.convertion.webdds;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.gosh.validation.convertion.BatchNumberGenerator;
import org.gosh.validation.webdds.WebGeneratedDDsTransformer;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.springframework.core.io.DefaultResourceLoader;

public class WebGeneratedDDsTransformerTest extends MockObjectTestCase {
	
	// this test is based on an existing set of input and output files. 
	// there was also some input in the actual code from a document from bolaji.
	public void testBuildsGoshccStructure() throws Exception {
		File donorFile = new DefaultResourceLoader().getResource("classpath:firstFewLinesOfWebDDs_MariaTest.txt").getFile();
		WebGeneratedDDsTransformer webGeneratedDDsApplication = new WebGeneratedDDsTransformer();
		final BatchNumberGenerator batchNumberGenerator = mock(BatchNumberGenerator.class);
		webGeneratedDDsApplication.setBatchNumberGenerator(batchNumberGenerator);
		webGeneratedDDsApplication.setSupplierID("RSM");
		
		checking(new Expectations(){{
			oneOf(batchNumberGenerator).getBatchNumberFromReAndLog();
			will(returnValue(5));
		}});
		
		String goshcc = webGeneratedDDsApplication.transform(FileUtils.readFileToString(donorFile));
		assertTrue(goshcc != null);
		
		File expectedFile = new DefaultResourceLoader().getResource("classpath:firstFewRecordsOfConvertedWebDDFile_MariaTest.xml").getFile();
		assertEquals(
				StringUtils.substringAfter(StringUtils.deleteWhitespace(FileUtils.readFileToString(expectedFile)), "</BatchNo>"), 
				StringUtils.substringAfter(StringUtils.deleteWhitespace(goshcc), "</BatchNo>")
			);
	}
	
	public void testWhenUnexpectedNumberOfColumns() throws Exception {
		File donorFile = new DefaultResourceLoader().getResource("classpath:firstFewLinesOfWebDDs_errorCase.txt").getFile();
		WebGeneratedDDsTransformer webGeneratedDDsApplication = new WebGeneratedDDsTransformer();
		final BatchNumberGenerator batchNumberGenerator = mock(BatchNumberGenerator.class);
		webGeneratedDDsApplication.setBatchNumberGenerator(batchNumberGenerator);
		
		checking(new Expectations(){{
			oneOf(batchNumberGenerator).getBatchNumberFromReAndLog();
			will(returnValue(5));
		}});
		
		String uOpException = null;
		try{
			webGeneratedDDsApplication.transform(FileUtils.readFileToString(donorFile));
		} catch (Exception e) {
			uOpException = e.getMessage();
		}
		
		assertEquals("One of the records had an unexpected number of columns 68. It was the following line: 2009-02-25,08:25:57,these,shouldn't,be,here,14038,PDD013041,63.29,Direct Marketing Online Direct Debit,,,Mr,Samuel,,Davey,35 ennersdale road,,,london,,se13 6je,GBR,Mr S Davey,600511,16564200,5.00,,0.00,0.00,5.00,0.00,Y,2009-04-01,Monthly,\"NAT WEST BANK PLC, ST PAUL'S\",Chatham Customer Service Centre,Waterside Court,Chatham Maritime,Chatham,Kent,ME4 4RT,Home,02082974350,Mobile,07595388713,samdavey@gmail.com,N,Y,N,Y,Y,N,,,1975-03-26,,,Other,,I wanted to support a children's charity,,,,,,,", uOpException);
	}
	
	/**
	 * This was something we've seen in real data.
	 */
	public void testFileWithBlankFirstLine() throws Exception {
		File donorFile = new DefaultResourceLoader().getResource("classpath:firstFewLinesOfWebDDs_BlankFirstLine.txt").getFile();
		WebGeneratedDDsTransformer webGeneratedDDsApplication = new WebGeneratedDDsTransformer();
		final BatchNumberGenerator batchNumberGenerator = mock(BatchNumberGenerator.class);
		webGeneratedDDsApplication.setBatchNumberGenerator(batchNumberGenerator);
		checking(new Expectations(){{
			oneOf(batchNumberGenerator).getBatchNumberFromReAndLog();
			will(returnValue(5));
		}});
		
		// do the transform
		String goshcc = webGeneratedDDsApplication.transform(FileUtils.readFileToString(donorFile));
		assertTrue(goshcc != null);
	}
	
	public void testDoesntChangeAppealFromCR104() throws Exception {
		WebGeneratedDDsTransformer transformer = new WebGeneratedDDsTransformer();
		String[] fields = transformer.getFields("10/06/2009,10:20:00,16975,PDD014157,63.29,Direct Marketing Online Direct Debit,DM09,DMC0910A,Mrs,Sarah,,Narahara,Flat 1,16 St. Stephens Gardens,,LONDON,,W2 5QX,GBR,Sarah E Narahara,400530,42091208,5,,0,0,5,0,N,01/07/2009,Monthly,\"HSBC BANK PLC, Queen Victoria St EC4N\",60 Queen Victoria Street,,,London,,EC4N 4TR,,,,,sarahnarahara@yahoo.com,N,N,N,N,Y,N,,,02/12/1973,,,Other,,I'm a parent of a child who was treated at GOSH,,,,,,,", ",");
		assertEquals("DMC0910A", transformer.createAppealFromCR104(fields[7], fields[54]));
	}

	public void testDefaultsAppealFromCR104() throws Exception {
		WebGeneratedDDsTransformer transformer = new WebGeneratedDDsTransformer();
		String[] fields = transformer.getFields("10/06/2009,10:20:00,16975,PDD014157,63.29,Direct Marketing Online Direct Debit,DM09,,Mrs,Sarah,,Narahara,Flat 1,16 St. Stephens Gardens,,LONDON,,W2 5QX,GBR,Sarah E Narahara,400530,42091208,5,,0,0,5,0,N,01/07/2009,Monthly,\"HSBC BANK PLC, Queen Victoria St EC4N\",60 Queen Victoria Street,,,London,,EC4N 4TR,,,,,sarahnarahara@yahoo.com,N,N,N,N,Y,N,,,02/12/1973,,,,,I'm a parent of a child who was treated at GOSH,,,,,,,", ",");
		assertEquals("DMC011009", transformer.createAppealFromCR104(fields[7], fields[54]));
	}

	public void testCustom1ColumnCorrectlyChangesAppealFromCR104() throws Exception {
		WebGeneratedDDsTransformer transformer = new WebGeneratedDDsTransformer();
		String[] fields = transformer.getFields("10/06/2009,10:20:00,16975,PDD014157,63.29,Direct Marketing Online Direct Debit,DM09,,Mrs,Sarah,,Narahara,Flat 1,16 St. Stephens Gardens,,LONDON,,W2 5QX,GBR,Sarah E Narahara,400530,42091208,5,,0,0,5,0,N,01/07/2009,Monthly,\"HSBC BANK PLC, Queen Victoria St EC4N\",60 Queen Victoria Street,,,London,,EC4N 4TR,,,,,sarahnarahara@yahoo.com,N,N,N,N,Y,N,,,02/12/1973,,,TV ad,,I'm a parent of a child who was treated at GOSH,,,,,,,", ",");
		assertEquals("DMC0110N", transformer.createAppealFromCR104(fields[7], fields[54]));
		fields = transformer.getFields("10/06/2009,10:20:00,16975,PDD014157,63.29,Direct Marketing Online Direct Debit,DM09,,Mrs,Sarah,,Narahara,Flat 1,16 St. Stephens Gardens,,LONDON,,W2 5QX,GBR,Sarah E Narahara,400530,42091208,5,,0,0,5,0,N,01/07/2009,Monthly,\"HSBC BANK PLC, Queen Victoria St EC4N\",60 Queen Victoria Street,,,London,,EC4N 4TR,,,,,sarahnarahara@yahoo.com,N,N,N,N,Y,N,,,02/12/1973,,,Mailing,,I'm a parent of a child who was treated at GOSH,,,,,,,", ",");
		assertEquals("DMC01109", transformer.createAppealFromCR104(fields[7], fields[54]));
		fields = transformer.getFields("10/06/2009,10:20:00,16975,PDD014157,63.29,Direct Marketing Online Direct Debit,DM09,,Mrs,Sarah,,Narahara,Flat 1,16 St. Stephens Gardens,,LONDON,,W2 5QX,GBR,Sarah E Narahara,400530,42091208,5,,0,0,5,0,N,01/07/2009,Monthly,\"HSBC BANK PLC, Queen Victoria St EC4N\",60 Queen Victoria Street,,,London,,EC4N 4TR,,,,,sarahnarahara@yahoo.com,N,N,N,N,Y,N,,,02/12/1973,,,Other,,I'm a parent of a child who was treated at GOSH,,,,,,,", ",");
		assertEquals("DMC0110P", transformer.createAppealFromCR104(fields[7], fields[54]));
	}
}
