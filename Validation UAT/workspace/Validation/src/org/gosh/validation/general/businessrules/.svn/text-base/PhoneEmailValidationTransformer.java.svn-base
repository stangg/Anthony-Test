package org.gosh.validation.general.businessrules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.PhoneEmail;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;

/**
 * This checks that the phones and emails are not duplicated, 
 * not blank and phones are numbers, emails are not. 
 * 
 * @author Kevin.Savage
 */
public class PhoneEmailValidationTransformer {
	private Reporter reporter;

	@SuppressWarnings("unchecked")
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) {
		GOSHCC payload = message.getPayload();
		List<DonorCplxType> donors= payload.getDonorCplxType();
		
		List<DonorCplxType> donorsWithDuplicatePhoneTypes = new ArrayList<DonorCplxType>();
		List<DonorCplxType> donorsWithBlankPhonesOrEmails = new ArrayList<DonorCplxType>();
		
		for (DonorCplxType donor: donors) {
			List<PhoneEmail> phoneEmails = donor.getPhoneEmail();
			List<String> types = new ArrayList<String>();
			for (PhoneEmail phoneEmail : phoneEmails) {
				types.add(phoneEmail.getType());
			}
			Map<String,Integer> cardinalityMap = CollectionUtils.getCardinalityMap(types);
			if (CollectionUtils.exists(cardinalityMap.values(), new Predicate(){
					@Override
					public boolean evaluate(Object object) {
						Integer integer = (Integer)object;
						return integer > 1;
					}
				})){
				donorsWithDuplicatePhoneTypes.add(donor);
			}
			
			for (PhoneEmail phoneEmail : phoneEmails) {
				/**
				 * CR 25 as of modification on 11/06/2009 14:58
				 * No whitespace or hyphens in phone values. 
				 */
				if (hasBlankPhonesOrEmails(phoneEmail)) {
					donorsWithBlankPhonesOrEmails.add(donor);
				} else if ((isPhone(phoneEmail) &&  StringUtils.isNumeric(phoneEmail.getValue()) && StringUtils.isNotBlank(phoneEmail.getValue())) || (isPhone(phoneEmail) && StringUtils.isNotBlank(phoneEmail.getValue()) && StringUtils.isNumeric(StringUtils.deleteWhitespace(phoneEmail.getValue())))){
					//cleanup 
					String phValue = StringUtils.remove(phoneEmail.getValue(), "-");
					phoneEmail.setValue(StringUtils.deleteWhitespace(phValue));
				}
			}

			for (PhoneEmail phoneEmail : phoneEmails) {
				phoneEmail.setValue(StringUtils.deleteWhitespace(phoneEmail.getValue()));
			}
		}
		
		if (!donorsWithDuplicatePhoneTypes.isEmpty()){
			message = reporter.log(message, donorsWithDuplicatePhoneTypes, "There are duplicate phone types.");
		}
		if (!donorsWithBlankPhonesOrEmails.isEmpty()){
			message = reporter.log(message, donorsWithBlankPhonesOrEmails, "One of the phones or emails are blank.");
		}

		return message;
	}

	private boolean hasBlankPhonesOrEmails(PhoneEmail phoneEmail) {
		return (StringUtils.isBlank(phoneEmail.getValue()))||
			(isPhone(phoneEmail) && !StringUtils.isNumeric(StringUtils.remove(StringUtils.deleteWhitespace(phoneEmail.getValue()),"+")))||
			(!isPhone(phoneEmail) && StringUtils.isNumeric(phoneEmail.getValue()));
	}

	private boolean isPhone(PhoneEmail phoneEmail) {
		return "Mobile".equals(phoneEmail.getType()) || "Home".equals(phoneEmail.getType()) || "Business".equals(phoneEmail.getType());
	}

	@Required @Autowired
	public void setErrorReporter(Reporter reporter) {
		this.reporter = reporter;
	}
}
