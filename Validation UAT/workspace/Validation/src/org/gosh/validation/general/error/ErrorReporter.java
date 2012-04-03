package org.gosh.validation.general.error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.validation.common.MessageHeaderName;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

/**
 * This is used throughout to add consistent error headers to the messages.
 * @author Kevin.Savage
 */
public class ErrorReporter implements Reporter {
	@SuppressWarnings("unchecked")
	public <T> Message<T> log(Message<T> message, String error){
		List<String> errors = (List<String>) message.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		if (errors == null){
			errors = new ArrayList<String>();
		}
		errors.add(error);
		
		return MessageBuilder
			.fromMessage(message)
			.setHeader(MessageHeaderName.ERROR_HEADER.getName(), errors)
			.build();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Message<T> log(Message<T> message, Iterable<DonorCplxType> donors, String error) {
		
		if (donors == null || !donors.iterator().hasNext())
			return message; // Nothing to add here!
		
		List<String> errors = (List<String>) message.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		if (errors == null){
			errors = new ArrayList<String>();
		}
	
		String newErrorMessage = "There was an error for the following donors: ";
		for (DonorCplxType donor : donors) {
			newErrorMessage += "[" + donor.getFirstName() + " " + donor.getLastName() + " of " + donor.getPostCode() + "] ";
		}
		newErrorMessage += ": "+ error;
		
		errors.add(newErrorMessage);
		
		return MessageBuilder
			.fromMessage(message)
			.setHeader(MessageHeaderName.ERROR_HEADER.getName(), errors)
			.build();
	}
	
	@Override
	public <T> Message<T> log(Message<T> message, DonorCplxType donor, String error) {
		return log(message, Collections.singletonList(donor), error);
	}
	
	public <T> Message<T> log(Message<T> message, Map<DonorCplxType, String> newErrors){
		for (Entry<DonorCplxType, String> entry : newErrors.entrySet()) {
			message = log(message, entry.getKey(), entry.getValue());
		}
		return message;
	}

	@SuppressWarnings("unchecked")
	public <T> Message<T> log(Message<T> message, Collection<String> newErrors){
		List<String> errors = (List<String>) message.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		if (errors == null){
			errors = new ArrayList<String>();
		}
		errors.addAll(newErrors);
		
		return MessageBuilder
			.fromMessage(message)
			.setHeader(MessageHeaderName.ERROR_HEADER.getName(), errors)
			.build();
	}

	@SuppressWarnings("unchecked")
	public <T> Message<T> logInfo(Message<T> message, Iterable<String> newErrors){
		List<String> infos = (List<String>) message.getHeaders().get(MessageHeaderName.INFO_HEADER.getName());
		if (infos == null){
			infos = new ArrayList<String>();
		}
		
		for (String newError : newErrors) {
			infos.add(newError);
		}
		
		if (infos.size() > 0) 
			return MessageBuilder
				.fromMessage(message)
				.setHeader(MessageHeaderName.INFO_HEADER.getName(), infos)
				.build();
		
		return message;
	}

	public <T> Message<T> log(Message<T> message, List<String> infoMessages, List<String> errorMessages) {
		return log(logInfo(message, infoMessages), errorMessages);
	}
	
	public <T> Message<T> log(Message<T> message, Map<DonorCplxType, String> newErrors, List<String> infoMessages){
		for (Entry<DonorCplxType, String> entry : newErrors.entrySet()) {
			message = log(message, entry.getKey(), entry.getValue());
		}
		message = logInfo(message, infoMessages);
		return message;
	}
}
