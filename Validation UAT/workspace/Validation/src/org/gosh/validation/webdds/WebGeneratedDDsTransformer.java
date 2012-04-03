package org.gosh.validation.webdds;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.Address;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GiftSubtype;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.Attributes;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.ConstituentAppeal;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.TaxDeclaration;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.Attributes.SolicitCodes;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.Attributes.Source;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.DonorBankCplxType;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType.DonationDetails.DirectDebitDonationCplxType.PDDUpgradeFlag;
import org.gosh.validation.convertion.AbstractCsvConverter;
import org.springframework.integration.annotation.Transformer;

/**
 * This converts the file supplied by RSM to our XML format. 
 */
public class WebGeneratedDDsTransformer extends AbstractCsvConverter{
	public static final String PAPERLESSDDS_HEADER = "Date,Time,Batch No,Ref,Campaign ID,Campaign,Donor Ref,Appeal Code,Title,Forename,Middle Name,Surname,Address 1,Address 2,Address 3,Town,County,Post Code,Country,Account Holder,Sort Code,Account No,Amount,Purchase Desc,Purchase 1,Purchase 2,Donation 1,Donation 2,Gift Aid,Start Date,Frequency,Bank Name,Bank Address 1,Bank Address 2,Bank Address 3,Bank Town,Bank County,Bank Post Code,Tel Type 1,Tel 1,Tel Type 2,Tel 2,Email,Data Protection Mail,Data Protection Email,Data Protection Phone,Data Protection SMS,Data Protection Sister,Data Protection Partner,Prompt Reason,Sex,DOB,Partner Forename,Partner Surname,Custom 1,Custom 2,Custom 3,Custom 4,Custom 5,Custom 6,Custom 7,Custom 8,Custom 9,Custom 10";
	
	@Transformer
	public String transform(String message) throws IOException, JAXBException {
		List<String> lines = new ArrayList<String>();
		CollectionUtils.addAll(lines, StringUtils.split(message, "\n"));
		CollectionUtils.filter(lines, new Predicate(){
			@Override
			public boolean evaluate(Object object) {
				return StringUtils.isNotBlank((String)object);
			}
		});
		
		GOSHCC goshcc = setUpGoshcc();
		loadDonations(lines, goshcc);
		
		return marshall(goshcc);
	}
	
	protected void loadDonations(List<String> donorLines, GOSHCC goshcc) {
		for (String donorLine : donorLines) {
			if (donorLines.get(0).equals(donorLine)){
				// this means we have the header line
				continue;
			}
			String[] fields = getFields(donorLine, ",");

			DonorCplxType donor = new DonorCplxType();
			if (fields.length != 64){
				throw new UnsupportedOperationException("One of the records had an unexpected number of columns " + String.valueOf(fields.length)+ ". It was the following line: " + donorLine);
			}
			// This is a bug but nobody noticed it before on test
			donor.setSupplierDonorID(fields[3]);
			donor.setTitle1(fields[8]);
			donor.setFirstName(fields[9]);
			donor.setLastName(fields[11]);
			donor.setChangeOfName(false); // from Bolaji's Data Mapping Doc
			
			ArrayList<String> addressLines = new ArrayList<String>();
			addressLines.add(fields[12]);
			addressLines.add(fields[13]);
			addressLines.add(fields[14]);
			donor.setAddress(new Address());
			donor.getAddress().getAddressLine().addAll(addressLines);
			donor.setPrimaryAddress(false); // from Bolaji's Data Mapping Doc
			
			donor.setCity(fields[15]);
			donor.setCounty(fields[16]);
			donor.setPostCode(fields[17]);
			if ("GBR".equals(fields[18])){
				donor.setCountry("United Kingdom");
			}else{
			donor.setCountry(fields[18]); // from Bolaji's Data Mapping Doc
			}
			XMLGregorianCalendar date = toDate(fields[51], "yyyy-MM-dd");
			if (date != null){
			donor.setDateOfBirth(date);
			}
			donor.setGender("Unknown");
			
			GOSHCC.DonorCplxType.ConsCodes consCodes = new GOSHCC.DonorCplxType.ConsCodes();
			consCodes.setCode("DMGen");
			consCodes.setDateFrom(toDate(fields[0], "yyyy-MM-dd")); // from Bolaji's Data Mapping Doc
			donor.getConsCodes().add(consCodes);
			
			addPhoneEmail(donor, fields[42], "Email"); // from Bolaji's Data Mapping Doc
			addPhoneEmail(donor, fields[39], "Home"); // from Bolaji's Data Mapping Doc
			addPhoneEmail(donor, fields[41], "Mobile"); // from Bolaji's Data Mapping Doc
			
			// Attributes 
			Attributes attributes = new GOSHCC.DonorCplxType.Attributes();
			donor.setAttributes(attributes);
		
			if ("Y".equals(fields[44])){
				addSolicitCodes(donor, "Email Opt-In"); // from Bolaji's Data Mapping Doc
			}
			if ("Y".equals(fields[45])){
				addSolicitCodes(donor, "Phone Opt-In");
			}
			if ("Y".equals(fields[46])){
				addSolicitCodes(donor, "SMS Opt-In"); // from Bolaji's Data Mapping Doc
			}
			if ("Y".equals(fields[47])){
				addSolicitCodes(donor, "3rd Party Data Protection Opt Out"); // from Bolaji's Data Mapping Doc
			}
			
			addSourceOnline(donor);
			
			addDirectMarketingType(donor, "Bankers Order");
			
			// Donation
			GOSHCC.DonorCplxType.DonationDetails donationDetails = new GOSHCC.DonorCplxType.DonationDetails();
			donor.setDonationDetails(donationDetails);
			
			DirectDebitDonationCplxType donation = new DirectDebitDonationCplxType();
		
			donation.setType("Pledge");
			// Live Reported Bug that BS retains was part of the spec and does not want to raise as CR. It was 'Donations - 1010'.
			donation.setSubType(GiftSubtype.DONATIONS_INTERNET_1014);
			donation.setDate(toDate(fields[0], "yyyy-MM-dd"));
			donation.setAmount(new BigDecimal(fields[22]));
			donation.setFund("UNGEN");
			donation.setCampaign(createCampaignFromCR104(fields[6]));
			donation.setAppeal(createAppealFromCR104(fields[7], fields[54]));			
			donation.setPackage("GEN");
			donation.setPostStatus("Not Posted");
			donation.setPaymentType("Direct Debit");
			donation.setLetterCode(""); //leave blank
			PDDUpgradeFlag flag = new PDDUpgradeFlag();
			flag.setPDDUpgradeFlag(false);
			flag.setPreviousPaymentMethod("Not Applicable");
			flag.setGiftimportID("");
			donation.setPDDUpgradeFlag(flag);
			donation.setInstallmentFreq(fields[30]);
			if (StringUtils.equals(fields[30], "Monthly")){
				donation.setNoOfInstalments(BigInteger.valueOf(new Long(120)));
			} else if (StringUtils.equals(fields[30], "Annually")){
				donation.setNoOfInstalments(BigInteger.valueOf(new Long(10)));
			} else if (StringUtils.equals(fields[30], "Semi-Annually")){
				donation.setNoOfInstalments(BigInteger.valueOf(new Long(20)));
			} else if (StringUtils.equals(fields[30], "Quarterly")){
				donation.setNoOfInstalments(BigInteger.valueOf(new Long(40)));
			}  else if (StringUtils.equals(fields[30], "Bimonthly")){
				donation.setNoOfInstalments(BigInteger.valueOf(new Long(240)));
			}  else if (StringUtils.equals(fields[30], "Quarterly")){
				donation.setNoOfInstalments(BigInteger.valueOf(new Long(40)));
			}
			donation.setScheduleMonthlyType("Specific Day");
			donation.setScheduleDayOfMonth(setupDaySchedule(fields[29]));
			donation.setScheduleSpacing(BigInteger.valueOf(new Long(1)));
			donation.setScheduleWeeklyDayOfWeek(BigInteger.valueOf(new Long(0)));
			donation.setDate1StPayment(toDate(fields[29], "yyyy-MM-dd"));		
			donation.setRefNo("1");
			donation.setGiftStatus("Active");
				
			//Bank
			DonorBankCplxType bank = new DonorBankCplxType();
			String clean = cleanupField(fields[31]);
			bank.setBankName(clean);
			bank.setBranchName(" ");
			ArrayList<String> bAddLines = new ArrayList<String>();
			bAddLines.add(fields[32]);
			bAddLines.add(fields[33]);
			bAddLines.add(fields[34]);
			bank.setBankAddress(new Address());
			bank.getBankAddress().getAddressLine().addAll(bAddLines);
			bank.setBankCity(fields[35]);
			bank.setBankCounty(fields[36]);
			if (StringUtils.isBlank(fields[37])){
				bank.setBankPost("AAAAAAA");
			}else{
				bank.setBankPost(fields[37]);
			}
			bank.setBankAccName(fields[19]);
			bank.setBankAccNo(fields[21]);
			bank.setBankSort(formatSortCode(fields[20]));
			
			donation.setDonorBankCplxType(bank);
			
			//Tax		
			if ("Y".equals(fields[28])){
				TaxDeclaration declaration = new GOSHCC.DonorCplxType.TaxDeclaration(); // from Bolaji's Data Mapping Doc
				declaration.setDeclarationDate(donation.getDate());
				declaration.setDeclarationIndicator("Oral");
				declaration.setStartDate(donation.getDate());
				declaration.setTaxPayer(true);
				declaration.setConfirmationDate(donation.getDate());
				donor.setTaxDeclaration(declaration);
			}
						
			donationDetails.setDirectDebitDonationCplxType(donation);
			
			addConstituentAppeal(donor, donation);
			
			goshcc.getDonorCplxType().add(donor);
		}
	}
	
	private String formatSortCode(String sortCode) {
		String s1 = sortCode.substring(0, 2) ;
		String s2 = sortCode.substring(2, 4) ;
		String s3 = sortCode.substring(4, 6) ;
		return s1 + "-" + s2 + "-" + s3;
	}

	private String cleanupField(String field) {
		String clean = StringUtils.substringBefore(field, ",");
		String clean1 = StringUtils.strip(clean, "\"");
		String clean2 = StringUtils.strip(clean1, "'");
		String clean3 = StringUtils.trimToEmpty(clean2);
		return clean3;
	}

	private int setupDaySchedule(String startDate) {
		String dayFromStartDate = startDate.substring(8,10);
		if (dayFromStartDate.equals("01")){
			return 1;
		} else if (dayFromStartDate.equals("05")){
			return 5;
		} else if (dayFromStartDate.equals("15")){
			return 15;
		} 
		return 1;
	}
	/**
	 * @return string appealid
	 * @param string fields 7 (Appeal Code), string fields 54 (Custom 1)
	 * <p> Code according to CR RE-IT-104:
	 * <p>1)	Pre-coded within the landing pages, retain these codes (this also includes other none DM codes that are pre-populated). 
		<br/>			Appeal Code DMC0910B. Donor Ref (RE CampID) DM09

	 * <p>2)	Code to be populated based on data within the Custom 1 field as follows:
		<br/>			If 'TV Ad' then Appeal Code: DMC0110N, Donor Ref: DM01
		<br/>			If 'Other' then Appeal Code: DMC0110P, Donor Ref: DM01
		<br/>			If 'Mailing' then Appeal Code: DMC01109, Donor Ref: DM01

	 * <p>3) All other gifts that are neither pre-coded (1) or have Custom 1 fields populated are to be defaulted to:
		<br/>			Appeal Code: DMC011009, Donor Ref: DM01
	 */
	public String createAppealFromCR104(String fields7, String field54) {
		if (StringUtils.isBlank(fields7)){
			if (StringUtils.equalsIgnoreCase("TV ad", field54)){
				return "DMC0110N";
			} else if (StringUtils.equalsIgnoreCase("Other", field54)){
				return "DMC0110P";
			} else if (StringUtils.equalsIgnoreCase("Mailing", field54)){
				return "DMC01109";
			} else {
				return "DMC011009";
			}
		}
		return fields7;
	}
	
	/**
	 * @return string campaignid
	 * @param string fields 6 (Donor Ref)
	 * <p> Code according to CR RE-IT-104:
	 * <p>1)	Pre-coded within the landing pages, retain these codes (this also includes other none DM codes that are pre-populated). 
		<br/>			Appeal Code DMC0910B. Donor Ref (RE CampID) DM09

	 * <p>2)	Code to be populated based on data within the Custom 1 field as follows:
		<br/>			If TV Ad then Appeal Code: DMC0110N, Donor Ref: DM01
		<br/>			If Other then Appeal Code: DMC0110P, Donor Ref: DM01
		<br/>			If Mailing then Appeal Code: DMC01109, Donor Ref: DM01

	 * <p>3) All other gifts that are neither pre-coded (1) or have Custom 1 fields populated are to be defaulted to:
		<br/>			Appeal Code: DMC011009, Donor Ref: DM01
	 */
	private String createCampaignFromCR104(String fields6) {
		if (StringUtils.isBlank(fields6)){			
			return "DM01";	
		} 
		return fields6;
	}
	
	private void addSourceOnline(DonorCplxType donor) {
		Source source = new Source();
		source.setDescription("Online");
		source.setCategory("Source");
		source.setDate(donor.getConsCodes().get(0).getDateFrom());
		source.setComment("Obtained via RSM");
		donor.getAttributes().setSource(source);
	}

	private void addConstituentAppeal(DonorCplxType donor, DirectDebitDonationCplxType donation) {
		ConstituentAppeal constituentAppeal = new ConstituentAppeal();
		constituentAppeal.setAppealID(donation.getAppeal());
		constituentAppeal.setPackageID(donation.getPackage());
		constituentAppeal.setMarketingSourceCode(donor.getSupplierDonorID()); // This has been reported as live bug but the logic is already in the code.
		constituentAppeal.setDate(donation.getDate());
		constituentAppeal.setResponse("Responded");
		donor.setConstituentAppeal(constituentAppeal);
	}

	private void addSolicitCodes(DonorCplxType donor, String description){
		List<SolicitCodes> solicitCodes = donor.getAttributes().getSolicitCodes();
		SolicitCodes solicitCode = new SolicitCodes();
		solicitCode.setDescription(description);
		solicitCode.setCategory("Solicit Codes");
		solicitCode.setDate(donor.getConsCodes().get(0).getDateFrom());
		solicitCode.setComment("Obtained via RSM");
		solicitCodes.add(solicitCode);
	}
}