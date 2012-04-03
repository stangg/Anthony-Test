package org.gosh.validation.tributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.Relationship;
import org.gosh.re.dmcash.bindings.GOSHCC.Tribute;
import org.gosh.validation.general.error.Reporter;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.Message;

/**
 * This validates mainly the ID's that are used across different 
 * sections of the file. Specifically it checks that the ID's that 
 * the tribute sets for the "honer" ([SIC] Blackbaud's spelling) and
 * any ack's are existent. 
 * 
 * Also, this checks the relationships for acks excluding the honor
 * for the case of self acknowledgees.
 * 
 * @author Kevin.Savage
 */
public class TributeValidationTransformer {
	private Log log = LogFactory.getFactory().getInstance(this.getClass());
	private Reporter reporter;
	
	@Transformer
	public Message<GOSHCC> transform(Message<GOSHCC> message) {
		log.info("Start Transformation");
		List<String> existingIds = new ArrayList<String>();
		GOSHCC payload = message.getPayload();
		List<DonorCplxType> donors = payload.getDonorCplxType();
		List<Tribute> tributes = payload.getTribute();
		
		log.info("TributeValidationTransformer --- " + donors.size() + " donors " + tributes.size() + " tributes");
		for (DonorCplxType donorCplxType : donors) {
			existingIds.add(donorCplxType.getSupplierDonorID());
		}
		
		Map<String, List<String>> relationsThatShouldExist = new HashMap<String, List<String>>();
		for (Tribute tribute : tributes) {
			List<String> idsToCheck = new ArrayList<String>();
			idsToCheck.add(tribute.getHonerSupplierDonorID());
			for (String ackSupplierDonorId : tribute.getAcknowledgeeSupplierDonorID()) {
				if(!tribute.getHonerSupplierDonorID().equals(ackSupplierDonorId)){
					idsToCheck.add(ackSupplierDonorId);		
				}
			}
			
			if (!existingIds.containsAll(idsToCheck)){
				return reporter.log(message, "Some of the IDs linking tributes to donors were not valid. There is a tribute that contains " + idsToCheck + " when the ids for donors were " + existingIds);
			}
		
			Collection<String> nonSelfAcknowledgeeSupplierDonorIDs = getNonSelfAcknowledgeeSupplierDonorIds(tribute);
			if (!nonSelfAcknowledgeeSupplierDonorIDs.isEmpty()){
				relationsThatShouldExist.put(tribute.getHonerSupplierDonorID(), tribute.getAcknowledgeeSupplierDonorID());
			}
		}
				
		for (Entry<String, List<String>> entry: relationsThatShouldExist.entrySet()) {
			List<Relationship> relationships = message.getPayload().getRelationship();
			boolean matched = false;
			for (Relationship relationship : relationships) {
				if (
				(entry.getKey().equals(relationship.getSupplierDonorID())&&entry.getValue().contains(relationship.getRelatedSupplierDonorID()))||
				(entry.getKey().equals(relationship.getRelatedSupplierDonorID())&&entry.getValue().contains(relationship.getSupplierDonorID()))
				){
					matched = true;
					continue;
				}
			}
			
			if (matched) {
				continue;
			} else {
				return reporter.log(message, "Some of the relationships linking honors and acknowledgees did not exist. For example the following were not all mapped [" + entry.getKey() + "->" + entry.getValue()+"]");
			}
		}
		log.info("End of Transformation");
		return message;
	}

	@SuppressWarnings("unchecked")
	private List<String> getNonSelfAcknowledgeeSupplierDonorIds(Tribute tribute) {
		List<String> nonSelfAcknowledgeeSupplierDonorIDs = ListUtils.removeAll(tribute.getAcknowledgeeSupplierDonorID(), Collections.singletonList(tribute.getHonerSupplierDonorID()));
		return nonSelfAcknowledgeeSupplierDonorIDs;
	}
	
	@Required
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
}
