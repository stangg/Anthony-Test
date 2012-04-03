package org.gosh.validation.general.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.integration.core.Message;
import org.springframework.util.xml.SimpleSaxErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;

/**
 * Validates against the Generic schema (RaisersEdgeImportSchema_v1.xsd) 
 * 
 * @author Kevin.Savage
 */
public class SchemaValidationTransformer {
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	private Reporter reporter;
	private File schemaFile;
	private String errorString = "The file was not valid against the schema.";

	public Message<GOSHCC> transform(Message<GOSHCC> message) {
		Document document = null;
		try {
			// Load Schema
			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source schemaSource = new StreamSource(schemaFile);
			Schema schema = factory.newSchema(schemaSource);

			// Load XML
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
			DocumentBuilder parser = docFactory.newDocumentBuilder();
			document = parser.parse(new ByteArrayInputStream(helper(message).getBytes()));
			Node nextSibling = document.getFirstChild().getNextSibling();
			if (nextSibling != null){
				NamedNodeMap attributes = nextSibling.getAttributes();
				if (attributes!=null){
					attributes.getNamedItem("xsi:noNamespaceSchemaLocation").setTextContent(schemaFile.getAbsolutePath());
					attributes.getNamedItem("xmlns:xsi").setTextContent("http://www.w3.org/2001/XMLSchema-instance");
				}
			}
			
			// Validate
			Validator validator = schema.newValidator();
			ErrorHandler errHandler = new SimpleSaxErrorHandler(log);
			validator.setErrorHandler(errHandler);
			DOMResult result = new DOMResult();
			validator.validate(new DOMSource(document), result);
		} catch (Exception e) {
			return reporter.log(message, errorString);
		}
		return message;
	}

	// I figured it's ok to throw the exception here because if it's wrong, somethings really wrong!
	public void setSchemaLocation(String schemaLocation) throws IOException {
		this.schemaFile = new DefaultResourceLoader().getResource(schemaLocation).getFile();
	}

	public void setSchemaFile(File schemaFile) {
		this.schemaFile = schemaFile;
	}

	@Autowired
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
	
	public Reporter getReporter() {
		return reporter;
	}
	
	public String helper(Message<GOSHCC> message) {
		
		GOSHCC goshcc = message.getPayload();
		try {
			JAXBContext context = JAXBContext.newInstance("org.gosh.re.dmcash.bindings");
			Marshaller marshaller = context.createMarshaller();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			marshaller.setProperty("jaxb.formatted.output", true);
			marshaller.marshal(goshcc, byteArrayOutputStream);
			return byteArrayOutputStream.toString();
		} catch (JAXBException e) {
			log.error("Error while converting XML to String format", e);
		}
		return null;
	}
}
