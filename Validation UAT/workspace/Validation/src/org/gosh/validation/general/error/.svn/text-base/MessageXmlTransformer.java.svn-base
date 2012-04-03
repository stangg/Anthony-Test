package org.gosh.validation.general.error;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gosh.validation.application.ValidationPropertyReader;
import org.gosh.validation.common.MessageHeaderName;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;

/**
 * This takes anything we logged against the message and appends it to 
 * the xml so that it can be read by a human. 
 */
public class MessageXmlTransformer {

	String validationVersion = null;
	
	public MessageXmlTransformer() {
		ValidationPropertyReader propertyReader = new ValidationPropertyReader();
		propertyReader.readProperties();
		validationVersion = propertyReader.getVersion();
	}
	
	@SuppressWarnings("unchecked")
	@org.springframework.integration.annotation.Transformer
	public Message<String> transform(Message<String> message) {
		List<String> messages = new ArrayList<String>();
		
		List<String> errors = (List<String>)message.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		List<String> infos = (List<String>) message.getHeaders().get(MessageHeaderName.INFO_HEADER.getName());
		
		if (errors == null || errors.isEmpty()){
			if (infos == null){
				infos = new ArrayList<String>();
			}
			
			infos.add("This file has been successfully verified by the Validation " + validationVersion);
		}
		
		if (errors != null){
			messages.addAll(errors);
		}

		if (infos != null){
			messages.addAll(infos);
		}
		
		Document document = null;
		try {
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = parser.parse(new ByteArrayInputStream(message.getPayload().getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			return message;
		}
		
		if (document != null) {
			for (String errorMessage : messages) {
				Comment comment = document.createComment(errorMessage);
				document.appendChild(comment);
			}

			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans;
			try {
				trans = transfac.newTransformer();
				trans.setOutputProperty(OutputKeys.INDENT, "yes");

				StringWriter sw = new StringWriter();
				StreamResult result = new StreamResult(sw);
				DOMSource source = new DOMSource(document);
				trans.transform(source, result);
				String xmlString = sw.toString();
				return new GenericMessage<String>(xmlString, message.getHeaders());
			} catch (Exception e1) {
				e1.printStackTrace();
				return message;
			}	
		}
		return message;
	}
}
