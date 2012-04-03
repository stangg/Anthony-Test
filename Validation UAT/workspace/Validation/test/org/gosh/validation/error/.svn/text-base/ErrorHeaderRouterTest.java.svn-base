package org.gosh.validation.error;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.error.ErrorHeaderRouter;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

public class ErrorHeaderRouterTest extends TestCase {

	public void testErrorCase() throws Exception {
		ArrayList<String> errors = new ArrayList<String>();
		errors.add("anything");
		
		Message<String> message = MessageBuilder.withPayload("payload")
			.setHeader(MessageHeaderName.ERROR_HEADER.getName(), errors)
			.build();
		
		assertEquals("invalidOut", new ErrorHeaderRouter().route(message));
	}

	public void testValidCase() throws Exception {
		// Conspicuously no error header in this case!
		Message<String> message = MessageBuilder.withPayload("payload").build();
		assertEquals("validOut", new ErrorHeaderRouter().route(message));
	}

	public void testEmptyCase() throws Exception {
		Message<String> message = MessageBuilder
			.withPayload("payload")
			.setHeader(MessageHeaderName.ERROR_HEADER.getName(),  new ArrayList<String>())
			.build();
		assertEquals("validOut", new ErrorHeaderRouter().route(message));
	}
}
