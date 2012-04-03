package org.gosh.validation.general.bank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

/**
 * This connects to the {@link http://goshcc-tomcat1:8080/xindice/?/db/banks xindice}
 * xml database on tomcat1 which contains an up to date bank list. 
 *  
 * @author Kevin.Savage
 */
public class BankXindiceDAO implements BankDAO {
	public Map<DonorBankCplxType, Boolean> lookupBankFromList(List<DonorBankCplxType> banks) throws XMLDBException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Collection col = null;
		try {
			col = getConnection();

			HashMap<DonorBankCplxType, Boolean> results = new HashMap<DonorBankCplxType, Boolean>();
			for (DonorBankCplxType donorBankCplxType : banks) {
				results.put(donorBankCplxType, lookupBank(donorBankCplxType, col));
			}
			return results;
		} catch (XMLDBException e) {
			System.err.println("XML:DB Exception occured " + e.errorCode);
			return null;
		} finally {
			if (col != null) {
				col.close();
			}
		}
	}


	private Boolean lookupBank(DonorBankCplxType donorBankCplxType, Collection col) throws XMLDBException {
		String xpath = "/Bank[BankOffices[@SortCode=\""+StringUtils.remove(xmlEscape(donorBankCplxType.getBankSort()),"-")+"\"]]/BankName";
		XPathQueryService service = (XPathQueryService) col.getService("XPathQueryService", "1.0");
		ResourceSet resultSet = service.query(xpath);
		ResourceIterator iterator = resultSet.getIterator();
		while(iterator.hasMoreResources()){
			Resource bankName = iterator.nextResource();
			return StringUtils.containsIgnoreCase(bankName.getContent().toString(), xmlEscape(donorBankCplxType.getBankName()));
		}
		return resultSet.getSize()>0;
	}
	
	@SuppressWarnings("unchecked")
	private Collection getConnection() throws ClassNotFoundException, InstantiationException, IllegalAccessException, XMLDBException {
		Collection col;
		String driver = "org.apache.xindice.client.xmldb.DatabaseImpl";
		Class<Database> c = (Class<Database>) Class.forName(driver);
		
		Database database = (Database) c.newInstance();
		DatabaseManager.registerDatabase(database);
		
		String uri = "xmldb:xindice://goshcc-tomcat1:8080/db/banks";
		col = DatabaseManager.getCollection(uri);
		return col;
	}

	private String xmlEscape(String string) {
		 string = StringUtils.replace(string,"&","&amp;");
		 string = StringUtils.replace(string,"<","&lt;");
		 string = StringUtils.replace(string,">","&gt;");
		 string = StringUtils.replace(string,"\"","");
		 return string;
	}
}