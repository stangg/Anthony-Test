package org.gosh.validation.general.businessrules

import org.springframework.integration.transformer.Transformer
import org.springframework.integration.core.Message
import org.springframework.integration.message.MessageBuilder
import org.gosh.validation.general.LoggingTransformer

/**
 * Adds "c" to the end of caf file batch numbers if there is not one already.
 * Adds "n" to the end of non-caf cash batch numbers if there is not one already.
 */
public class AdjustCafFileBatchNumbersTransformer extends LoggingTransformer{
	Message transform(Message message){
		if (message.payload.supplierID == "VALLDATA"){
			boolean isCafFile = message.payload.donorCplxType.any{
				it.getDonationDetails() != null &&
				it.donationDetails.cashDonationCplxType.any{
					"CAF".equals(it.agency)
				}
			}
			boolean thereAreCashDonations = message.payload.donorCplxType.any{
				it.getDonationDetails() != null && !it.donationDetails.cashDonationCplxType.isEmpty()
			}
			
			String letterToAdd = isCafFile? "c" : "n";
			if (thereAreCashDonations && message.payload.batchNo != null && !((String)message.payload.batchNo).contains(letterToAdd)){
				message.payload.batchNo += letterToAdd
				return MessageBuilder
					.fromMessage(message)
					.build()
			} 
		}
		return message
	}
}