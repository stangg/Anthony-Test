package org.gosh.validation.xml;

import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.validation.ValidationHelper;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.HeaderAdditionTransformer;
import org.gosh.validation.general.error.ErrorReporter;
import org.gosh.validation.general.xml.SchemaValidationTransformer;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

public class SchemaValidationTransformerTest extends TestCase {
	
	private ValidationHelper helper = null;
	private SchemaValidationTransformer classToTest = null; 
	
	protected void setUp() throws Exception {
		super.setUp();
		classToTest = new SchemaValidationTransformer();
		helper = new ValidationHelper();
	}
	
	
	public void testValidData() throws Exception {
		SchemaValidationTransformer schemaValidationTransformer = new SchemaValidationTransformer();
		schemaValidationTransformer.setReporter(new ErrorReporter());
		DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
		schemaValidationTransformer.setSchemaLocation("classpath:RaisersEdgeImportSchema_v1.xsd");
		Resource resource = defaultResourceLoader.getResource("classpath:testdata.xml");
		Message<GOSHCC> message = helper.xml2Message("test/testdata.xml");
		
		System.out.println("message in " + message.getPayload());
		
		Message<GOSHCC> transformedMessage = schemaValidationTransformer.transform(message);

		System.out.println("message out " + transformedMessage.getPayload());
		
		assertNull("expected a null error list, but got this:" + transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()), transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()));
	}
	
	@SuppressWarnings("unchecked")
	public void testInvalidXMLData() throws Exception {
		SchemaValidationTransformer schemaValidationTransformer = new SchemaValidationTransformer();
		schemaValidationTransformer.setReporter(new ErrorReporter());
		schemaValidationTransformer.setSchemaLocation("classpath:RaisersEdgeImportSchema_v1.xsd");
		Message<GOSHCC> genericMessage = helper.xml2Message("test/data/invalidtestXML.xml");
		Message<GOSHCC> transformedMessage = schemaValidationTransformer.transform(genericMessage);
		List<String> errors= (List<String>) transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		assertNotNull(errors);
		assertEquals("The file was not valid against the schema.", errors.get(0));
	}
	
	@SuppressWarnings("unchecked")
	public void testInvalidXMLData2() throws Exception {
		SchemaValidationTransformer schemaValidationTransformer = new SchemaValidationTransformer();
		schemaValidationTransformer.setReporter(new ErrorReporter());
		schemaValidationTransformer.setSchemaLocation("classpath:RaisersEdgeImportSchema_v1.xsd");
		Message<GOSHCC> genericMessage = helper.xml2Message("test/data/invalidtestXML2.xml");
		Message<GOSHCC> transformedMessage = schemaValidationTransformer.transform(genericMessage);
		List<String> errors= (List<String>) transformedMessage.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		assertNotNull(errors);
		assertEquals("The file was not valid against the schema.", errors.get(0));
	}
}
