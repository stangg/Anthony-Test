package org.gosh.validation;

import junit.framework.TestCase;

import org.gosh.validation.webcash.FixedStringCorrelationSetter;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

public class FixedStringCorrelationSetterTest extends TestCase {
	public void test() throws Exception {
		FixedStringCorrelationSetter transformer = new FixedStringCorrelationSetter();
		Message<String> transform1 = transformer.transform(new GenericMessage<String>(""));
		Message<String> transform2 = transformer.transform(new GenericMessage<String>(""));
		assertEquals(correlationIdOf(transform1),correlationIdOf(transform2));
	}

	private Object correlationIdOf(Message<String> transform1) {
		return transform1.getHeaders().getCorrelationId();
	}
}
