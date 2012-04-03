package org.gosh.validation.general.businessrules;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;

/**
 * This logs blank surnames. This is different to the xml validation 
 * because the schema currently allows spaces so that you can have a
 * de Menze, etc. The side effect is that you are allowed " " which is 
 * not what we want. 
 * 
 * @author Kevin.Savage
 */
public class BlankSurnameTransformer {
	private Reporter reporter;
	
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message){
		List<DonorCplxType> donorsWithBlankNames = new ArrayList<DonorCplxType>();
		
		List<DonorCplxType> donorCplxType = message.getPayload().getDonorCplxType();
		for (DonorCplxType donor : donorCplxType) {
			if (StringUtils.isBlank(donor.getLastName())){
				donorsWithBlankNames.add(donor);
			}
		}
		
		if (donorsWithBlankNames.isEmpty()){
			return message;
		} else {
			return reporter.log(message, donorsWithBlankNames, "The surname is blank.");
		}
	}
	
	@Autowired @Required 
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
}
