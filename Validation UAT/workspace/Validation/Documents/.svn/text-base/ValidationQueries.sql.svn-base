-- Below are the SQL queries used in Validation to fetch data from Raisers' Edge.

-- Used in ExistingDDsValidationTransformer
select gift.CONSTIT_ID from records, gift where gift.CONSTIT_ID = records.ID and 
	gift.TYPE = 8 and gift.gift_status = 1 and CONSTITUENT_ID in (<insert list of donor constituent IDs here>)

-- Used in ExistingDDsValidationTransformer
select * from GIFT where gift.IMPORT_ID in (<insert previous gift import IDs>)

-- Used in GenericBankValidationTransformer (for DD and DDU) to fetch bank ids
SELECT bank.ID FROM bank WHERE bank.SORT_CODE='<insert sort code>'

-- Used in GenericBankValidationTransformer (for DD and DDU) to fetch bank ids
SELECT bank.ID FROM bank, tableentries WHERE bank.SORT_CODE='<insert sort code>' and 
	(bank.BRANCH_NAME='' or bank.BRANCH_NAME is null or bank.BRANCH_NAME='<insert branch name>') and 
	(tableentries.LONGDESCRIPTION ='insert bank name' or tableentries.SHORTDESCRIPTION ='insert bank name') and 
	tableentries.ACTIVE = -1 and tableentries.CODETABLESID=5087 and tableentries.TABLEENTRIESID=bank.bank

-- Used in DDUpgradeBankValidationTransformer to fetch constituent bank id
SELECT constituent_bank.ID FROM records, bank, constituent_bank WHERE 
	records.CONSTITUENT_ID = '<insert donor constituent id>' AND 
	bank.ID=constituent_bank.BRANCH_ID AND constituent_bank.CONSTIT_ID=records.ID AND 
	bank.SORT_CODE='insert sort code'

-- Used in DDUpgradeBankValidationTransformer to fetch constituent bank id
SELECT gift.CONSTITUENTBANKID FROM gift WHERE gift.CONSTIT_ID = '<insert donor internal constituent id>'  AND  
	gift.IMPORT_ID = '<insert gift import id>'

-- Refer the 
select DESCRIPTION from AttributeTypes WHERE TYPEOFDATA in 
	(<insert code in fieldToCodeTable for attributeType>) and RECORDTYPE IN (1,2) and 
	lower(DESCRIPTION) in (list of values)

-- Used in ConstituentIdValidationTransformer to find internal constituent id
select CONSTITUENT_ID, ID from records where CONSTITUENT_ID = '<insert donor constituent id>'