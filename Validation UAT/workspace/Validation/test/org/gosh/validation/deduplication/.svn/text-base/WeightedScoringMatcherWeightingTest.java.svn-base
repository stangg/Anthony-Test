package org.gosh.validation.deduplication;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.gosh.re.dmcash.bindings.Address;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.PhoneEmail;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.validation.general.deduplication.ExtendedDataSetModel;
import org.gosh.validation.general.deduplication.WeightedScoringMatcher;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * 
		private String title;
		private String firstName;
		private String middleName;
		private String lastName;
		private int sex;
		private String addressBlock;
		private String postCode;
		private String accountName;
		private String branchName;
		private String sortCode;
		private String phoneNumber;
		private String email;

 * @author Kevin.Savage
 *
 */
public class WeightedScoringMatcherWeightingTest extends AbstractDependencyInjectionSpringContextTests {
	public void testABunchOfCasesCauseAMatch() throws Exception {
		checkMatchesIfTheseFieldsMatch("title","firstName","lastName","sex","addressBlock","postCode");
		checkMatchesIfTheseFieldsMatch("title","firstName","middleName","lastName","addressBlock","postCode");
		checkMatchesIfTheseFieldsMatch("title","firstName","lastName","sex","branchName","sortCode","accountName");
		checkMatchesIfTheseFieldsMatch("firstName","lastName","sex","addressBlock","postCode");
		checkMatchesIfTheseFieldsMatch("firstName","middleName","lastName","addressBlock","postCode");
		checkMatchesIfTheseFieldsMatch("title","firstName","middleName","lastName","sex","addressBlock","postCode","accountName","branchName","sortCode","phoneNumber","email");
		checkMatchesIfTheseFieldsMatch("title","firstName","middleName","lastName","sex","addressBlock","postCode","accountName","branchName","sortCode","phoneNumber");
		checkMatchesIfTheseFieldsMatch("title","firstName","middleName","lastName","sex","addressBlock","postCode","accountName","branchName","sortCode","email");
		checkMatchesIfTheseFieldsMatch("title","firstName","middleName","lastName","sex","addressBlock","postCode","accountName","branchName","sortCode");
		checkMatchesIfTheseFieldsMatch("title","firstName","lastName","sex","phoneNumber","email");
		checkMatchesIfTheseFieldsMatch("title","firstName","lastName","sex","email");
		checkMatchesIfTheseFieldsMatch("title","firstName","lastName","sex","addressBlock","postCode","email");
	}
	
	public void testABunchOfCasesThatShouldntCauseAMatch() throws Exception {
		checkDoesntMatchIfTheseFieldsMatch("title","firstName","lastName");
		checkDoesntMatchIfTheseFieldsMatch("firstName","lastName","postCode");
		checkDoesntMatchIfTheseFieldsMatch("title","addressBlock","postCode");
		checkDoesntMatchIfTheseFieldsMatch("title","sex","postCode");
		checkDoesntMatchIfTheseFieldsMatch("title","lastName","sex","addressBlock","postCode");
		checkDoesntMatchIfTheseFieldsMatch("title","firstName","lastName","sex","accountName");
		checkDoesntMatchIfTheseFieldsMatch("title","firstName","lastName","phoneNumber");
		checkDoesntMatchIfTheseFieldsMatch("title","firstName","sex","sortCode","branchName");
		checkDoesntMatchIfTheseFieldsMatch("title","firstName","sex","sortCode","postCode");
		checkDoesntMatchIfTheseFieldsMatch("title","firstName","sex","addressBlock","postCode");
		checkDoesntMatchIfTheseFieldsMatch("title","lastName","sex","addressBlock","postCode","sortCode","phoneNumber");
	}
	
	private void generateDatabaseModelWith(String... fields) {
		extendedDataSetModel = new ExtendedDataSetModel();
		PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(ExtendedDataSetModel.class);
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if	(ArrayUtils.contains(fields, propertyDescriptor.getName())){
				if (propertyDescriptor.getPropertyType() == String.class){
					try {
						PropertyUtils.setProperty(extendedDataSetModel, propertyDescriptor.getName(), propertyDescriptor.getName());
					} catch (Exception e) {
						// this happens and is ok.
					}
				}
				if (propertyDescriptor.getPropertyType() == Integer.TYPE){
					try {
						PropertyUtils.setProperty(extendedDataSetModel, propertyDescriptor.getName(), 1);
					} catch (Exception e) {
						// this happens and is ok.
					}
				}
				
			}
		}
	}
	
	private void checkMatchesIfTheseFieldsMatch(String... fields) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		generateDatabaseModelWith(fields);
		DonorCplxType donorCplxType = createModel(fields);
		Set<ExtendedDataSetModel> bestMatches = matcher.bestMatches(donorCplxType, Collections.singletonList(extendedDataSetModel), new ArrayList<String>());
		assertTrue(!bestMatches.isEmpty());
	}

	private void checkDoesntMatchIfTheseFieldsMatch(String... fields) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		generateDatabaseModelWith(fields);
		DonorCplxType donorCplxType = createModel(fields);
		Set<ExtendedDataSetModel> bestMatches = matcher.bestMatches(donorCplxType, Collections.singletonList(extendedDataSetModel), new ArrayList<String>());
		assertTrue(bestMatches.isEmpty());
	}

	private DonorCplxType createModel(String... fields)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setTitle1(getFieldValue("title", fields));
		donorCplxType.setFirstName(getFieldValue("firstName", fields));
		donorCplxType.setMiddleName(getFieldValue("middleName", fields));
		donorCplxType.setLastName(getFieldValue("lastName", fields));
		donorCplxType.setGender(ArrayUtils.contains(fields,"sex")?"Male":"somethingElse");
		
		Address address = new Address();
		donorCplxType.setAddress(address);
		address.getAddressLine().add(getFieldValue("addressBlock", fields));
		
		donorCplxType.setPostCode(getFieldValue("postCode", fields));
		
		
		PhoneEmail phone = new PhoneEmail();
		phone.setValue(getFieldValue("phoneNumber", fields));
		phone.setType("Home");
		donorCplxType.getPhoneEmail().add(phone);

		PhoneEmail email = new PhoneEmail();
		email.setValue(getFieldValue("email", fields));
		email.setType("Email");
		donorCplxType.getPhoneEmail().add(email);
		
		DonationDetails donationDetails = new DonationDetails();
		donorCplxType.setDonationDetails(donationDetails);
		DirectDebitDonationCplxType directDebitDonationCplxType = new DirectDebitDonationCplxType();
		donationDetails.setDirectDebitDonationCplxType(directDebitDonationCplxType);
		DonorBankCplxType donorBankCplxType = new DonorBankCplxType();
		directDebitDonationCplxType.setDonorBankCplxType(donorBankCplxType);
		
		donorBankCplxType.setBankAccName(getFieldValue("accountName", fields));
		donorBankCplxType.setBankName(getFieldValue("branchName", fields));
		donorBankCplxType.setBankSort(getFieldValue("sortCode", fields));
		return donorCplxType;
	}

	private String getFieldValue(String field, String... fields)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		return ArrayUtils.contains(fields,field)?(String)PropertyUtils.getProperty(extendedDataSetModel, field):"somethingElse";
	}
	
    protected String[] getConfigLocations() {
        return new String[] { "classpath:config.xml" };
    }
    
    private WeightedScoringMatcher matcher;
	private ExtendedDataSetModel extendedDataSetModel;

    public void setMatcher(WeightedScoringMatcher matcher) {
		this.matcher = matcher;
	}
}
