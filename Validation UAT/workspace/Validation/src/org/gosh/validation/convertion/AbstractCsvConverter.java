package org.gosh.validation.convertion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.PhoneEmail;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.Attributes.DirectMarketingType;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

/**
 * Contains helper methods for converting csv files to our standard
 * xml format.
 */
public abstract class AbstractCsvConverter {
	protected BatchNumberGenerator batchNumberGenerator;
	protected String supplierID;
	
	protected String marshall(GOSHCC goshcc) throws JAXBException, PropertyException {
		JAXBContext context = JAXBContext.newInstance("org.gosh.re.dmcash.bindings");
		Marshaller marshaller = context.createMarshaller();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		marshaller.setProperty("jaxb.formatted.output", true);
		marshaller.marshal(goshcc, byteArrayOutputStream);
		return byteArrayOutputStream.toString();
	}
	
	protected Message<String> marshall(GOSHCC goshcc, Map<String, Object> headers) throws PropertyException, JAXBException {
		return new GenericMessage<String>(marshall(goshcc), headers);
	}
	
	@Required
	public void setBatchNumberGenerator(BatchNumberGenerator batchNumberGenerator) {
		this.batchNumberGenerator = batchNumberGenerator;
	}
	
	@Required
	public void setSupplierID(String supplierID) {
		this.supplierID = supplierID;
	}

	/**
	 * This is just public for testing reasons. Is there a better way? 
	 */
	public String[] getFields(String line, String seperator) {
		if (seperator.length()>1){
			line = StringUtils.replace(line, seperator, "!");
			seperator = "!";
		}
		line = StringUtils.removeStart(line, "\"");
		line = StringUtils.removeEnd(line, "\"");
		String[] fields = StringUtils.splitPreserveAllTokens(line, seperator);
		for (int i = 0; i<fields.length; i++) {
			if (fields[i].startsWith("\"")){
				for (int j = 1; j+i<fields.length; j++) {
					if (fields[j+i].endsWith("\"")){
						String newFieldContent = "";
						for (int k = 0; k<=j; k++) {
							newFieldContent += "," + fields[i];
							fields = (String[]) ArrayUtils.remove(fields, i);
						}
						newFieldContent = newFieldContent.replaceFirst(",", "");
						newFieldContent = newFieldContent.replace("\"", "");
						fields = (String[]) ArrayUtils.add(fields, i, newFieldContent);
						break;
					}
				}
			}
		}
		return fields;
	}

	protected XMLGregorianCalendar toDate(String date, String format, XMLGregorianCalendar defaultdate) {
		if (StringUtils.isNotEmpty(date) && !StringUtils.contains(date, "1900-01-01")){
			DateFormat dateFormat = new SimpleDateFormat(format);
			try {
				Date parse = dateFormat.parse(date);
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime(parse);
				return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return defaultdate;
	}

	protected XMLGregorianCalendar toDate(String date, String format) {
		if (StringUtils.isNotEmpty(date) && !StringUtils.contains(date, "1900-01-01")){
			DateFormat dateFormat = new SimpleDateFormat(format);
			try {
				Date parse = dateFormat.parse(date);
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime(parse);
				return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	protected XMLGregorianCalendar toDateWithSlash(String date, XMLGregorianCalendar defaultdate) {
		if (StringUtils.isNotEmpty(date) && !StringUtils.contains(date, "1900-01-01")){
			try {
				String day = date.substring(0, 2);
				String month = date.substring(3, 5);
				String year = date.substring(6, 10);
				
				return DatatypeFactory.newInstance().newXMLGregorianCalendar(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), 0, 0, 0, 0, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return defaultdate;
	}
	
/**
 * Kevin:
 * Again under section 8, for tax declarations we have the Start Date is '06/04/2003'. 
 * In the code the start date is currently (donation date 6 years). Does this need to change? 
 * 
 * Bolaji:
 * Start Date needs to be the start of fiscal year: sixth day in the month of april six years ago.
 * 
 * @param calendar donation.getDate()
 * @return
 * @author Maria.Urso
 */
	public XMLGregorianCalendar minusSixFiscalYears(XMLGregorianCalendar calendar) {
		if (calendar != null){
			GregorianCalendar gregorianCalendar = calendar.toGregorianCalendar();
			gregorianCalendar.set(Calendar.YEAR, gregorianCalendar.get(Calendar.YEAR) - 6);
			gregorianCalendar.set(Calendar.MONTH, Calendar.APRIL);
			gregorianCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
			gregorianCalendar.set(Calendar.DAY_OF_MONTH, 6);
			try {
				return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected void addPhoneEmail(DonorCplxType donor, String value, String type) {
		List<PhoneEmail> phoneEmails = donor.getPhoneEmail();
		if (StringUtils.isNotBlank(value)){
			PhoneEmail phoneEmail = new PhoneEmail();
			phoneEmail.setType(type);
			phoneEmail.setValue(value);
			phoneEmails.add(phoneEmail);
		}
	}

	protected void addDirectMarketingType(DonorCplxType donor, String type) {
		DirectMarketingType directMarketingType = new DirectMarketingType();
		directMarketingType.setCategory("Direct Marketing Type");
		directMarketingType.setDescription(type);
		directMarketingType.setDate(donor.getConsCodes().get(0).getDateFrom());
		directMarketingType.setComment("Obtained via " + supplierID);
		donor.getAttributes().setDirectMarketingType(directMarketingType);
	}

	protected boolean equalsIgnoreWhitespace(String string1, String string2) {
		string1 = StringUtils.deleteWhitespace(string1);
		string2 = StringUtils.deleteWhitespace(string2);
		return StringUtils.equals(string1, string2);
	}

	protected GOSHCC setUpGoshcc() {
		GOSHCC goshcc = new GOSHCC();
		goshcc.setBatchNo(String.valueOf(batchNumberGenerator.getBatchNumberFromReAndLog()));
		goshcc.setSupplierID(supplierID);
		return goshcc;
	}
}
