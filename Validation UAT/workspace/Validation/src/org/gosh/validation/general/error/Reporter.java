package org.gosh.validation.general.error;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.springframework.integration.core.Message;

public interface Reporter {
	public <T> Message<T> log(Message<T> message, Map<DonorCplxType, String> errors);
	public <T> Message<T> log(Message<T> message, DonorCplxType donor, String error);
	public <T> Message<T> log(Message<T> message, Iterable<DonorCplxType> donor, String error);
	public <T> Message<T> log(Message<T> message, String error);
	public <T> Message<T> log(Message<T> message, Collection<String> newErrors);
	public <T> Message<T> logInfo(Message<T> message, Iterable<String> newErrors);
	public <T> Message<T> log(Message<T> message, List<String> infoMessages, List<String> errorMessages);
}