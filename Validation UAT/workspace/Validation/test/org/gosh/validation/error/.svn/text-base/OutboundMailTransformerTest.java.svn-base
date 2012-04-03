package org.gosh.validation.error;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.error.OutboundMailTransformer;
import org.springframework.integration.core.Message;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.message.MessageBuilder;

public class OutboundMailTransformerTest extends TestCase {
	public void testValid() {
		Message<GOSHCC> messageIn = MessageBuilder
			.withPayload(new GOSHCC())
			.setHeader(FileHeaders.FILENAME, "filename")
			.build();
		
		Message<String> transform = new OutboundMailTransformer().transform(messageIn);
		assertEquals("Validation just processed a file called filename. This file was valid. Processing it took 0ms.", transform.getPayload());
	}
	
	@SuppressWarnings("serial")
	public void testInvalid() throws Exception {
		Message<GOSHCC> messageIn = MessageBuilder
			.withPayload(new GOSHCC())
			.setHeader(FileHeaders.FILENAME, "filename")
			.setHeader(MessageHeaderName.ERROR_HEADER.getName(), new ArrayList<String>(){{add("error");}})
			.build();
	
		Message<String> transform = new OutboundMailTransformer().transform(messageIn);
		assertEquals("Validation just processed a file called filename. This file was invalid. Processing it took 0ms.", transform.getPayload());
	}
}
