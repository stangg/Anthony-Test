package org.gosh.validation.batchlog;

import java.util.List;

import junit.framework.TestCase;

import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.validation.TestDataSourceFactory;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.batchlog.REBatchNumberValidationTransformer;
import org.gosh.validation.general.error.ErrorReporter;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

public class REBatchNumberValidationTransformerTest extends TestCase {
	@SuppressWarnings("unchecked")
	public void testInvalidCase() throws Exception {
		REBatchNumberValidationTransformer transformer = new REBatchNumberValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		transformer.setReporter(new ErrorReporter());
		GOSHCC goshcc = new GOSHCC();
		goshcc.setBatchNo(String.valueOf(10000));
		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertEquals("In RE there was already a batch number of 10000", ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));
	}

	public void testValidCase() throws Exception {
		REBatchNumberValidationTransformer transformer = new REBatchNumberValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		transformer.setReporter(new ErrorReporter());
		GOSHCC goshcc = new GOSHCC();
		goshcc.setBatchNo(String.valueOf(100000000));
		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertNull(transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName()));
	}
	@SuppressWarnings("unchecked")
	public void testNullCase() throws Exception {
		REBatchNumberValidationTransformer transformer = new REBatchNumberValidationTransformer();
		transformer.setDataSource(TestDataSourceFactory.getDataSource());
		transformer.setReporter(new ErrorReporter());
		Message<GOSHCC> transform = transformer.transform(new GenericMessage<GOSHCC>(new GOSHCC()));
		assertEquals("There is a null batch number in this file.", ((List<String>)transform.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName())).get(0));
	}
}
