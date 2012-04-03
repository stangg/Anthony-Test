package org.gosh.validation.general.error;

import java.util.List;

import org.gosh.validation.common.MessageHeaderName;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.core.Message;

/**
 * This decides if the file was valid or not. The files will 
 * get written to different places in the file system, etc. This is
 * described in outputs.xml.
 * 
 * @author Kevin.Savage
 */
public class ErrorHeaderRouter {
	@SuppressWarnings({ "unchecked" })
	@Router
	public <T> String route(Message<T> message) {
		if (message.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()) != null && 
				!((List<String>)message.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).isEmpty()){
			return "invalidOut";
		} else {
			return "validOut";
		}
	}
}
