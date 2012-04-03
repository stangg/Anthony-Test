package org.gosh.validation.general.bank

import junit.framework.TestCase
import groovy.sql.Sql
import java.sql.DriverManagerimport java.sql.Driver
import org.apache.commons.lang.StringUtils
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;
/**
 * Intended to generate things like this from the xindice db and RE:
 * 
 * 00001-103-0000007|30-91-35|LLOYDS TSB BANK PLC|9 MKT SQ BROMLEY (309135)|1 Legg Street|Chelmsford|Essex|CM1 1JS
 */
public class BankListGenerator extends TestCase{
	public void testMain() throws ClassNotFoundException, InstantiationException, IllegalAccessException, XMLDBException{
		Collection col = null;
		try {
			col = getConnection();
			XPathQueryService service = (XPathQueryService) col.getService("XPathQueryService", "1.0");

			File output = new File("c:\\latestBankList.txt");
			File errors = new File("c:\\errors.txt");
			
			def sql = Sql.newInstance("jdbc:jtds:sqlserver://goshcc-re:1433;databaseName=RE7;currentSchema=dbo;", "reopen7", "iomega", "net.sourceforge.jtds.jdbc.Driver")
			sql.eachRow("select * from bank", 
				{ it ->
					if (
						!output.readLines().any{line -> line.contains(it.import_id)}&&
						!errors.readLines().any{line -> line.contains(it.import_id)}
						){
						def bankNameXML = getContentForXPath(service, "/Bank[BankOffices[@SortCode=\""+StringUtils.remove(xmlEscape(it.sort_code),"-")+"\"]]/BankName");
						if (bankNameXML){
							def bankOfficesXML = getContentForXPath(service, "/Bank/BankOffices[@SortCode=\""+StringUtils.remove(xmlEscape(it.sort_code),"-")+"\"][1]");
							def bankOffices = new XmlParser().parseText(bankOfficesXML)
							String ouputLine = (it.import_id + "|" + it.sort_code + "|" + new XmlParser().parseText(bankNameXML).text() + "|"
								+ bankOffices.BankOfficeTitle.text() + "|"
								+ bankOffices.Address.PostalName.text() + "|" 
								+ bankOffices.Address.CityOrTown.text() + "|" 
								+ bankOffices.Address.PostCodeOutcode.text() + " " 
								+ bankOffices.Address.PostCodeIncode.text() + "\n")
							output.append(ouputLine)
							print ouputLine
						} else {
							errors.append("the following is not in the list," + it.import_id + "," + it.sort_code + "\n")
							println ("the following is not in the list," + it.import_id + "," + it.sort_code)
						}
					}
				} 
			);
		} catch (XMLDBException e) {
			println "XML:DB Exception occured " + e.errorCode;
			return null;
		} finally {
			if (col != null) {
				col.close();
			}
		}
	}

	private String getContentForXPath(XPathQueryService service, String xpath) throws XMLDBException {
		ResourceSet resultSet = service.query(xpath);
		ResourceIterator iterator = resultSet.getIterator();
		
		String result = "";
		while(iterator.hasMoreResources()){
			Resource bankName = iterator.nextResource();
			result += bankName.getContent().toString() + "\n";
		}
		return result;
	}
	
	private Collection getConnection() throws ClassNotFoundException, InstantiationException, IllegalAccessException, XMLDBException {
		Class<Database> c = Class.forName("org.apache.xindice.client.xmldb.DatabaseImpl");
		DatabaseManager.registerDatabase(c.newInstance());
		return DatabaseManager.getCollection("xmldb:xindice://goshcc-tomcat1:8080/db/banks");
	}

	private String xmlEscape(String string) {
		 string = StringUtils.replace(string,"&","&amp;");
		 string = StringUtils.replace(string,"<","&lt;");
		 string = StringUtils.replace(string,">","&gt;");
		 string = StringUtils.replace(string,"\"","");
		 return string;
	}
}
