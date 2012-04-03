package org.gosh.validation.general.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;

/**
 * Turns an object model into an XML string. There is some 
 * stuff in spring integration to do this but I couldn't
 * make it work. 
 * 
 * @author Kevin.Savage
 */
public class DMCashMarshallerTransformer{
	@Transformer
	public String transform(Message<GOSHCC> message) {
		System.out.println("we have just received the message: " + message + " with payload " + message.getPayload());
		
		GOSHCC goshcc = message.getPayload();
		try {
			JAXBContext context = JAXBContext.newInstance("org.gosh.re.dmcash.bindings");
			Marshaller marshaller = context.createMarshaller();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			marshaller.setProperty("jaxb.formatted.output", true);
			marshaller.marshal(goshcc, byteArrayOutputStream);
			return byteArrayOutputStream.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
}
