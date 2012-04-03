package org.gosh.validation.general;

import static org.apache.commons.lang.StringUtils.*;
import org.gosh.validation.webcash.WebDonationsAggregator;
import org.gosh.validation.webdds.WebGeneratedDDsTransformer;
import org.springframework.integration.annotation.Router;

/**
 * This routes a file based on the content of the first line. Currently 
 * this is sufficient to work out if this is a web dd, web cash or other 
 * xml file. In the future it may be necessary to do more. 
 * 
 * @author Kevin.Savage
 */
public class FileFormatRouter {
	@Router
	public String route(String message) {
		if (startsWithIgnoreWhitespace(message, WebDonationsAggregator.DONATIONS_HEADER)
			|| startsWithIgnoreWhitespace(message, WebDonationsAggregator.DONORS_HEADER)){
			return "webDonations";
		} else if (startsWithIgnoreWhitespace(message, WebGeneratedDDsTransformer.PAPERLESSDDS_HEADER)){
			return "webGeneratedDDs";
		}
		return "messageIn";
	}
	
	private boolean startsWithIgnoreWhitespace(String string, String prefix) {
		string = remove(remove(remove(deleteWhitespace(string),'ï'),'»'),'¿');
		prefix = remove(remove(remove(deleteWhitespace(prefix),'ï'),'»'),'¿');
		return startsWith(string, prefix);
	}
}
