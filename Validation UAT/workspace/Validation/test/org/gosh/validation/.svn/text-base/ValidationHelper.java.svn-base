/**
 * 
 */
package org.gosh.validation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

/**
 * @author gayathri.polavaram
 *
 */
public class ValidationHelper {

	/** Schema location*/
	private static final String SCHEMA_BINDING_LOCATION = "org.gosh.re.dmcash.bindings";

	
	/** Helper method to convert an xml file to GOSHCC message.*/
	public Message<GOSHCC> xml2Message(final String relativeFileName) throws JAXBException, FileNotFoundException {
		JAXBContext context = JAXBContext.newInstance(SCHEMA_BINDING_LOCATION);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		GOSHCC goshcc = (GOSHCC)unmarshaller.unmarshal(new FileInputStream( relativeFileName));
		Message<GOSHCC> message = new GenericMessage<GOSHCC>(goshcc);
		return message;
	}

}
