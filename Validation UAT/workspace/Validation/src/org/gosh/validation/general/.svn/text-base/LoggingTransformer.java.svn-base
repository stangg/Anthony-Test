package org.gosh.validation.general;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.springframework.integration.core.Message;

/**
 * This logs before and after is starts to try and aid analysis
 */
public abstract class LoggingTransformer {
	private Log log = LogFactory.getFactory().getInstance(this.toString());

	public Message<GOSHCC> logAndTransform(Message<GOSHCC> message) throws Exception {
		log.info("transformer started to process message");
		Message<GOSHCC> transformedMessage = transform(message);
		log.info("transformer finished processing message");
		return transformedMessage;
	}
	
	abstract public Message<GOSHCC> transform(Message<GOSHCC> message) throws Exception;
}
