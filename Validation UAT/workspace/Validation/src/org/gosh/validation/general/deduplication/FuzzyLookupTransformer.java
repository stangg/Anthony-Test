package org.gosh.validation.general.deduplication;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

/**
 * Looks up using an Lucene (see http://lucene.apache.org/java/docs/#Apache%20Lucene)
 * for an overview of this) index to generate an initial large 
 * pool of possible people. This is used to lookup people who 
 * are "close" matches on Surname, Gender and Postcode.
 * 
 * @author Kevin.Savage
 */
public class FuzzyLookupTransformer {
	public static final String POSSIBLE_MATCHES = "possibleMatches";
	private static final long ten_mins = 10*60*1000;
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	private Reporter reporter;
	private JdbcTemplate jdbcTemplate;
	private String fuzzymatchiness;
	protected int indexRowLimit = Integer.MAX_VALUE;
	
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) {
		File luceneIndex = null;
		try{
			luceneIndex = generateLuceneIndex(message);
		} catch (Exception e) {
			e.printStackTrace();
			return reporter.log(message, "failed to create lucene index because: " + e);
		}
		
		HashMap<DonorCplxType, List<PossibleMatchModel>> possibles;
		try {
			possibles = doFuzzyLookups(message, luceneIndex);
		} catch (Exception e) {
			e.printStackTrace();
			return reporter.log(message, "failed to do fuzzy matching lookup because: " + e);
		}

		return MessageBuilder
			.fromMessage(message)
			.setHeader(POSSIBLE_MATCHES, possibles)
			.build();
	}

	private File generateLuceneIndex(Message<GOSHCC> message) throws CorruptIndexException, LockObtainFailedException, IOException{
		Date start = new Date();
		
		String url = getLuceneDirectory();
		File[] previousIndexes = new File(url).listFiles();
		for (File previousIndex : previousIndexes) {
			String name = previousIndex.getName();
			if (StringUtils.isNumeric(name)){
				long timestamp = Long.valueOf(name);
				if (new Date().getTime() - timestamp > ten_mins){
					FileUtils.deleteQuietly(previousIndex);
				} else {
					log.info("Not rebuilding lucene because this has happened within the last " + ten_mins + "ms");
					return previousIndex;
				}
			}
		}

		String sql = "SELECT RECORDS.ID, RECORDS.CONSTITUENT_ID,RECORDS.LAST_NAME, RECORDS.SEX, address.POST_CODE "+ 
	    	"FROM RECORDS, Constit_Address, address "+
	    	"where CONSTIT_ADDRESS.CONSTIT_ID = RECORDS.id and "+
	    	"address.ID = CONSTIT_ADDRESS.ADDRESS_ID and" +
	    	"(RECORDS.LAST_NAME is not null and RECORDS.FIRST_NAME is not null and RECORDS.CONSTITUENT_ID is not null AND address.POST_CODE is not null and RECORDS.IS_CONSTITUENT  =  -1)";
		
		File indexDir = new File(url + new Date().getTime());
		final IndexWriter writer = new IndexWriter(indexDir, new StandardAnalyzer(), true);
				
		jdbcTemplate.query(sql, new RowCallbackHandler(){
			private int count = 0;
			@Override
			public void processRow(ResultSet resultSet) throws SQLException {
				if (count<indexRowLimit){
					Document doc = new Document();
					doc.add(new Field("internalid", String.valueOf(resultSet.getInt(1)), Field.Store.YES, Field.Index.UN_TOKENIZED));
					String constituentId = resultSet.getString(2);
					doc.add(new Field("constid", constituentId, Field.Store.YES, Field.Index.UN_TOKENIZED));
					String postCode = resultSet.getString(5).toLowerCase();
					String contents = StringUtils.lowerCase(resultSet.getString(3)) + String.valueOf(resultSet.getInt(4)) + StringUtils.remove(postCode, ' ');
					contents = StringUtils.deleteWhitespace(contents);
					doc.add(new Field("contents", contents, Field.Store.YES, Field.Index.UN_TOKENIZED));
					try {
						writer.addDocument(doc);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				count++;
			}
		}); 
		
		writer.close();
		
		long milliSeconds = (new Date().getTime() - start.getTime());
		log.info("Rebuilding lucene index took " + milliSeconds + "ms");
		
		return indexDir;
	}
	
	protected String getLuceneDirectory() {
		if (new File("I:\\").exists()){
			return "I:\\lucene\\";
		} else if (new File("C:\\").exists()){
			return "C:\\lucene\\";
		}
		throw new UnsupportedOperationException("Should have found a lucene directory by now.");
	}

	private HashMap<DonorCplxType, List<PossibleMatchModel>> doFuzzyLookups(Message<GOSHCC> message, File luceneIndex) throws IOException, ParseException{
		Directory fsDir = FSDirectory.getDirectory(luceneIndex);
		IndexSearcher is = new IndexSearcher(fsDir);
		QueryParser queryParser = new QueryParser("contents", new StandardAnalyzer());

		HashMap<DonorCplxType, List<PossibleMatchModel>> results = new HashMap<DonorCplxType, List<PossibleMatchModel>>();		
	
		for (DonorCplxType donor : message.getPayload().getDonorCplxType()) {
			if (StringUtils.isEmpty(donor.getConstituentID())) {
				String queryString = 
					specialCharEscape(donor.getLastName()) + 
					convert(donor.getGender()) + 
					StringUtils.remove(donor.getPostCode(), ' ') + "~" + fuzzymatchiness; 
				// you can tweak the 0.636 appropriately. This was on the basis of some modelling from examples. 
				queryString = StringUtils.lowerCase(queryString);
				queryString = StringUtils.deleteWhitespace(queryString);
				
				Query query = queryParser.parse(queryString);
				Hits hits = is.search(query);
				ArrayList<PossibleMatchModel> possibleMatches = new ArrayList<PossibleMatchModel>();
				for (int i = 0; i < hits.length(); i++) {
					Document doc = hits.doc(i);
					PossibleMatchModel possibleMatchModel = new PossibleMatchModel();
					possibleMatchModel.setConstituentId(doc.get("constid"));
					possibleMatchModel.setInternalId(String.valueOf(doc.get("internalid")));
					possibleMatches.add(possibleMatchModel);
				}
				results.put(donor, possibleMatches);
			}
		}
		
		is.close();
		fsDir.close();
	    
		return results;
	}

	private String convert(String gender) {
		if (StringUtils.equalsIgnoreCase("male", gender)){
			return "1";
		} else if (StringUtils.equalsIgnoreCase("female", gender)){
			return "2";
		} else {
			return "3";
		}
	}
	
	private String specialCharEscape(String name) {
		String[] escapeChars = new String[]{"+" ,"-", "&&", "||", "!", "(", ")", "{", "}", "[", "]", "^", "\\", "\"", "~", "*", "?", ":"};
		String[] escapedChars = new String[escapeChars.length];
		for (int i = 0; i < escapeChars.length; i++) {
			escapedChars[i] = "\\" + escapeChars[i];
		}
		return StringUtils.replaceEach(name, escapeChars, escapedChars);
	}

	@Autowired @Required
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
	
	public Reporter getReporter() {
		return reporter;
	}
	
	@Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
	public void setFuzzymatchiness(String fuzzymatchiness) {
		this.fuzzymatchiness = fuzzymatchiness;
	}
}
