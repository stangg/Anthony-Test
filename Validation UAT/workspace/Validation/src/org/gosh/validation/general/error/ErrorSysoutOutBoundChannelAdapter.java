package org.gosh.validation.general.error;

/**
 * In the event of an error, messages get written to an error channel. 
 * This catches these and writes a log so that we can find out later 
 * what happened to them. 
 * 
 * @author Kevin.Savage
 */
public class ErrorSysoutOutBoundChannelAdapter{
	public void writeToSysout(Throwable throwable) {
		throwable.printStackTrace();
	}
}
