package org.gosh.validation.error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gosh.validation.general.error.MessageXmlTransformer;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

import junit.framework.TestCase;

public class MessageXmlTransformerTest extends TestCase {
	@SuppressWarnings("serial")
	public void testTransform() throws Exception {
		MessageXmlTransformer errorXmlTransformer = new MessageXmlTransformer();
		
		String payload = "<Goshcc/>";
		
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("error", new ArrayList<String>(){{add("error message 1");add("error message 2");}});
		headers.put("info", new ArrayList<String>(){{add("info message 1");add("info message 2");}});
	
		Message<String> transformedMessage = errorXmlTransformer.transform(new GenericMessage<String>(payload , headers ));
		
		String transformedPayload = new String(transformedMessage.getPayload());
		assertTrue(transformedPayload.contains("<!--error message 1-->"));
		assertTrue(transformedPayload.contains("<!--error message 2-->"));
		assertTrue(transformedPayload.contains("<!--info message 1-->"));
		assertTrue(transformedPayload.contains("<!--info message 2-->"));
	}
	
	public void testNullCase() throws Exception {
		MessageXmlTransformer errorXmlTransformer = new MessageXmlTransformer();
		Message<String> transformedMessage = errorXmlTransformer.transform(new GenericMessage<String>("<Goshcc/>"));
		String transformedPayload = new String(transformedMessage.getPayload());
		assertTrue(transformedPayload.contains("<!--This file has been successfully verified by the Validation 4.0.0-->"));
	}
}
