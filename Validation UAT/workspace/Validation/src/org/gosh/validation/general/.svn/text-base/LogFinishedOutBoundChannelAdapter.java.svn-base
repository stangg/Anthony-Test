/**
 * 
 */
package org.gosh.validation.general;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.integration.core.Message;

/**
 * Writes to the application log when the process is finished.
 */
public class LogFinishedOutBoundChannelAdapter {
	private Log log = LogFactory.getFactory().getInstance(this.getClass());

	public <T> void  writeToLog(Message<T> message){
		log.info("We just finished with the following message: " + message);
	}
}
