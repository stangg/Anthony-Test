package org.gosh.validation.deduplication;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.Address;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.PhoneEmail;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.validation.general.deduplication.ExtendedDataSetModel;
import org.gosh.validation.general.deduplication.WeightedScoringMatcher;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class WeightedScoringMatcherTest extends AbstractDependencyInjectionSpringContextTests {
	public void testCompleteMatch() throws Exception {
		ExtendedDataSetModel extendedDataSetModel = generateTestExtendedDataModel();
		DonorCplxType donorCplxType = generateMatchingDonor(extendedDataSetModel);
		
		Set<ExtendedDataSetModel> bestMatches = matcher.bestMatches(donorCplxType, Collections.singletonList(extendedDataSetModel), new ArrayList<String>());
		assertEquals(1, bestMatches.size());
		assertEquals(155, matcher.score(donorCplxType, extendedDataSetModel));
	}

	public void testPartialMatch() throws Exception {
		ExtendedDataSetModel extendedDataSetModel = generateTestExtendedDataModel();
		DonorCplxType donorCplxType = generateMatchingDonor(extendedDataSetModel);
		
		extendedDataSetModel.setAccountName("oneFieldThatDoesn'tMatch");
		extendedDataSetModel.setLastName("anotherFieldThatDoesn'tMatch");
		
		Set<ExtendedDataSetModel> bestMatches = matcher.bestMatches(donorCplxType, Collections.singletonList(extendedDataSetModel), new ArrayList<String>());
		assertEquals(1, bestMatches.size());
		assertTrue(154 > matcher.score(donorCplxType, extendedDataSetModel));
	}
	
	public void testNonMatch() throws Exception {
		ExtendedDataSetModel originalExtendedDataSetModel = generateTestExtendedDataModel();
		DonorCplxType donorCplxType = generateMatchingDonor(originalExtendedDataSetModel);
		ExtendedDataSetModel nonMatchingExtendedDataModel = generateNonMatchingDataModel();
		
		Set<ExtendedDataSetModel> bestMatches = matcher.bestMatches(donorCplxType, Collections.singletonList(nonMatchingExtendedDataModel), new ArrayList<String>());
		assertEquals(0, bestMatches.size());
		assertEquals(0, matcher.score(donorCplxType, nonMatchingExtendedDataModel));
	}
	
	public void testNullCase() throws Exception {
		ExtendedDataSetModel originalExtendedDataSetModel = new ExtendedDataSetModel();
		DonorCplxType donorCplxType = new DonorCplxType();
		
		Set<ExtendedDataSetModel> bestMatches = matcher.bestMatches(donorCplxType, Collections.singletonList(originalExtendedDataSetModel), new ArrayList<String>());
		assertEquals(0, bestMatches.size());
		assertEquals(0, matcher.score(donorCplxType, originalExtendedDataSetModel));
	}
	
	public void testCompleteMatchWithShortFirstName() throws Exception {
		ExtendedDataSetModel extendedDataSetModel = generateTestExtendedDataModel();
		extendedDataSetModel.setFirstName("K");
		DonorCplxType donorCplxType = generateMatchingDonor(extendedDataSetModel);
		donorCplxType.setFirstName("K");
		
		Set<ExtendedDataSetModel> bestMatches = matcher.bestMatches(donorCplxType, Collections.singletonList(extendedDataSetModel), new ArrayList<String>());
		assertEquals(1, bestMatches.size());
		assertEquals(152, matcher.score(donorCplxType, extendedDataSetModel));
	}	
	
	private ExtendedDataSetModel generateTestExtendedDataModel() {
		ExtendedDataSetModel extendedDataSetModel = new ExtendedDataSetModel();
		PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(ExtendedDataSetModel.class);
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if (propertyDescriptor.getPropertyType() == String.class){
				try {
					PropertyUtils.setProperty(extendedDataSetModel, propertyDescriptor.getName(), propertyDescriptor.getName());
				} catch (Exception e) {
					// this happens and is ok.
				}
			}
		}
		return extendedDataSetModel;
	}
	
	private ExtendedDataSetModel generateNonMatchingDataModel() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ExtendedDataSetModel extendedDataSetModel = new ExtendedDataSetModel();
		PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(ExtendedDataSetModel.class);
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if (propertyDescriptor.getPropertyType() == String.class){
				try {
					PropertyUtils.setProperty(extendedDataSetModel, propertyDescriptor.getName(), StringUtils.reverse(propertyDescriptor.getName()));
				} catch (Exception e) {
					// this happens and is ok.
				}
			}
		}
		extendedDataSetModel.setId(100);
		extendedDataSetModel.setSex(1);
		return extendedDataSetModel;
	}

	private DonorCplxType generateMatchingDonor(ExtendedDataSetModel extendedDataSetModel) {
		DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setConstituentID(extendedDataSetModel.getConstituentId());
		donorCplxType.setInternalConstitID(String.valueOf(extendedDataSetModel.getId()));
		donorCplxType.setTitle1(extendedDataSetModel.getTitle());
		donorCplxType.setFirstName(extendedDataSetModel.getFirstName());
		donorCplxType.setMiddleName(extendedDataSetModel.getMiddleName());
		donorCplxType.setLastName(extendedDataSetModel.getLastName());
		donorCplxType.setGender(extendedDataSetModel.getGender());
		
		Address address = new Address();
		donorCplxType.setAddress(address);
		address.getAddressLine().add(extendedDataSetModel.getFirstLineOfAddress());

		PhoneEmail phone = new PhoneEmail();
		phone.setValue(extendedDataSetModel.getPhoneNumber());
		phone.setType("Home");
		donorCplxType.getPhoneEmail().add(phone);

		PhoneEmail email = new PhoneEmail();
		email.setValue(extendedDataSetModel.getEmail());
		email.setType("Email");
		donorCplxType.getPhoneEmail().add(email);
		
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		DonorBankCplxType donorBankCplxType = new DonorBankCplxType();
		directDebitDonationCplxType.setDonorBankCplxType(donorBankCplxType);
		
		donorBankCplxType.setBankAccName(extendedDataSetModel.getAccountName());
		donorBankCplxType.setBankName(extendedDataSetModel.getBranchName());
		donorBankCplxType.setBankSort(extendedDataSetModel.getSortCode());
		return donorCplxType;
	}
	
    protected String[] getConfigLocations() {
        return new String[] { "classpath:config.xml" };
    }
    
    private WeightedScoringMatcher matcher;

    public void setMatcher(WeightedScoringMatcher matcher) {
		this.matcher = matcher;
	}
}
