package org.gosh.validation.general.error;

import java.util.List;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.validation.common.MessageHeaderName;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.message.GenericMessage;

/**
 * Turns a message into a "finished" email. Doesn't do any sending, 
 * this is handled by spring integration.
 * @author Kevin.Savage
 *
 */
public class OutboundMailTransformer {
	@SuppressWarnings("unchecked")
	@Transformer
	public Message<String> transform(Message<GOSHCC> message){
		List<String> errors = (List<String>)message.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		String filename = (String) message.getHeaders().get(FileHeaders.FILENAME);
		String processingTimeString = "Processing it took " + (System.currentTimeMillis() - message.getHeaders().getTimestamp()) + "ms.";
		if (errors==null||errors.isEmpty()){
			return new GenericMessage<String>("Validation just processed a file called " + filename + ". This file was valid. " + processingTimeString);
		} else {
			return new GenericMessage<String>("Validation just processed a file called " + filename + ". This file was invalid. " + processingTimeString);
		}
	}
}
