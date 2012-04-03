package org.gosh.validation.webcash;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

/**
 * This is so that the {@link WebDonationsAggregator} can work out 
 * that the two web donations files correlate. 
 * 
 * @author Kevin.Savage
 */
public class FixedStringCorrelationSetter {
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	private long currentlyUsedCorrelationId = -1;
	
	public <T> Message<T> transform(Message<T> message) {
		long timestamp = new Date().getTime();
		if (currentlyUsedCorrelationId == -1 || timestamp-currentlyUsedCorrelationId > 10000){
			currentlyUsedCorrelationId = timestamp;
		}
		
		log.info("We're about to set a correlationId of " + currentlyUsedCorrelationId + " on message " + message);
		return MessageBuilder
			.fromMessage(message).setCorrelationId(currentlyUsedCorrelationId)
			.build();
	}
}
