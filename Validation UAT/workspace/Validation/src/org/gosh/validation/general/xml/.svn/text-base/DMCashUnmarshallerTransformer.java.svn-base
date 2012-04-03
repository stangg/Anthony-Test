package org.gosh.validation.general.xml;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

/**
 * Turns xml into an object model. 
 * 
 * @author Kevin.Savage
 */
public class DMCashUnmarshallerTransformer{
	private Reporter reporter;
	
	@Transformer
	public Message<GOSHCC> transform(String xml) {
		try {
			JAXBContext context = JAXBContext.newInstance("org.gosh.re.dmcash.bindings");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return new GenericMessage<GOSHCC>((GOSHCC)unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes())));
		} catch (JAXBException e) {
			e.printStackTrace();
			return reporter.log(new GenericMessage<GOSHCC>(new GOSHCC()),
				"Something when quite badly wrong reading the XML. We were unable to work out exactly what but it was related to the following message: " + e);
		}
	}
	
	@Autowired
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
}
