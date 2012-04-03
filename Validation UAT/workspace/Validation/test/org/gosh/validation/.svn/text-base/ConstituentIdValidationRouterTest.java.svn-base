package org.gosh.validation;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.gosh.re.dmcash.bindings.GOSHCC;
import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;
import org.gosh.validation.common.MessageHeaderName;
import org.gosh.validation.general.businessrules.ConstituentIdValidationTransformer;
import org.gosh.validation.general.error.ErrorReporter;
import org.gosh.validation.general.error.Reporter;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;

public class ConstituentIdValidationRouterTest extends MockObjectTestCase {
	public void testNonValidFromFileCase() throws Exception {
		ConstituentIdValidationTransformer constituentIdValidationTransformer = new ConstituentIdValidationTransformer();
		final Reporter reporter = mock(Reporter.class);
		constituentIdValidationTransformer.setReporter(reporter);
		final GOSHCC goshcc = getGoshccWithOneDonorWithIds(null, "internalId");
		final Message<GOSHCC> genericMessage = new GenericMessage<GOSHCC>(goshcc);
		checking(new Expectations(){{
			oneOf(reporter).log(genericMessage, goshcc.getDonorCplxType().get(0), "We had an internalId without a contituentID, this is probably wrong");
		}});
		constituentIdValidationTransformer.transform(genericMessage );
	}

	public void testNotValidFromDB() throws Exception {
		ConstituentIdValidationTransformer constituentIdValidationTransformer = new ConstituentIdValidationTransformer();
		constituentIdValidationTransformer.setReporter(new ErrorReporter());
		constituentIdValidationTransformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		GOSHCC goshcc = getGoshccWithOneDonorWithIds("this is not an id", null);
		Message<GOSHCC> message = constituentIdValidationTransformer.transform(new GenericMessage<GOSHCC>(goshcc)  );
		assertEquals("The ids in the file are not all in RE, we couldn't find: [this is not an id]", getFirstErrorMessage(message));
	}
	
	public void testValidAgainstDB() throws Exception {
		ConstituentIdValidationTransformer constituentIdValidationTransformer = new ConstituentIdValidationTransformer();
		constituentIdValidationTransformer.setReporter(new ErrorReporter());
		constituentIdValidationTransformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		GOSHCC goshcc = getGoshccWithOneDonorWithIds("1", null);
		Message<GOSHCC> message = constituentIdValidationTransformer.transform(new GenericMessage<GOSHCC>(goshcc) );
		assertNotNull(goshcc.getDonorCplxType().get(0).getInternalConstitID());
		assertNull("expected null but got " + getFirstErrorMessage(message), getFirstErrorMessage(message));
	}
	
	// this is really fast!!!
	public void testPerformance() {
		ConstituentIdValidationTransformer constituentIdValidationTransformer = new ConstituentIdValidationTransformer();
		constituentIdValidationTransformer.setReporter(new ErrorReporter());
		constituentIdValidationTransformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		GOSHCC goshcc = new GOSHCC();
		
		for (int i = 0; i<1000;i++){
			goshcc.getDonorCplxType().add(getDonorWithIds(Integer.toString(i), null));
		}
		
		constituentIdValidationTransformer.transform(new GenericMessage<GOSHCC>(goshcc));
	}
	
	public void testEmptyModel() {
		ConstituentIdValidationTransformer constituentIdValidationTransformer = new ConstituentIdValidationTransformer();
		constituentIdValidationTransformer.setReporter(new ErrorReporter());
		constituentIdValidationTransformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		GOSHCC goshcc = new GOSHCC();

		Message<GOSHCC> message = constituentIdValidationTransformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertNull(getFirstErrorMessage(message));
	}

	public void testNoIdsModel() {
		ConstituentIdValidationTransformer constituentIdValidationTransformer = new ConstituentIdValidationTransformer();
		constituentIdValidationTransformer.setReporter(new ErrorReporter());
		constituentIdValidationTransformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		GOSHCC goshcc = new GOSHCC();
		goshcc.getDonorCplxType().add(new DonorCplxType());

		Message<GOSHCC> message = constituentIdValidationTransformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertNull(getFirstErrorMessage(message));
	}
	
	public void testBlankIdsModel() {
		ConstituentIdValidationTransformer constituentIdValidationTransformer = new ConstituentIdValidationTransformer();
		constituentIdValidationTransformer.setReporter(new ErrorReporter());
		constituentIdValidationTransformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		GOSHCC goshcc = new GOSHCC();
		
		goshcc.getDonorCplxType().add(getDonorWithIds("", ""));

		Message<GOSHCC> message = constituentIdValidationTransformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertNull(getFirstErrorMessage(message));
	}
	
	public void testNoInternalIdGetsAdded() {
		ConstituentIdValidationTransformer constituentIdValidationTransformer = new ConstituentIdValidationTransformer();
		constituentIdValidationTransformer.setReporter(new ErrorReporter());
		constituentIdValidationTransformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		GOSHCC goshcc = new GOSHCC();
		
		goshcc.getDonorCplxType().add(getDonorWithIds("40544890", ""));

		Message<GOSHCC> message = constituentIdValidationTransformer.transform(new GenericMessage<GOSHCC>(goshcc));
		assertNull(getFirstErrorMessage(message));
		assertEquals("950759", message.getPayload().getDonorCplxType().get(0).getInternalConstitID());
	}
	
	public void testFailedCase() {
		ConstituentIdValidationTransformer constituentIdValidationTransformer = new ConstituentIdValidationTransformer();
		constituentIdValidationTransformer.setReporter(new ErrorReporter());
		constituentIdValidationTransformer.setDataSource(TestDataSourceFactory.getDataSource());
		
		GOSHCC goshcc = new GOSHCC();
		
		String[] ids = new String[]{"40444756","40346936","40468546","40447193","40476572","40381804","40456234","40477289","40456312","40450493","40030674","40472611","40480174","40469167","40463752","40442181","40468431","40475729","40466304","40472308","40456021","40472744","40454404","40468261","10937362","40476483","40464900","40475524","40468414","40467382","40468226","40120797","40456457","40469132","40467319","40469093","05389300","40469007","40468968","40252553","40468173","40456214","40469064","40472129","40474950","40468839","40442214","40468099","40469055","40466483","40440586","40058881","40472107","40469070","40429300","40468504","40468280","40467390","40284860","40476529","40477299","07305027","40469072","40467726","40470029","40474948","40456232","40453487","40105280","40468453","40381953","40050914","40170196","40072750","40152826","10923452","10571417","40088041","40051322","40473212","40466738","40247427","40450596","40475792","40477524","40073112","08577580","40473224","40468577","40474889","40250326","40162500","30014399","10157952","10936613","40469033","40469977","40474911","40081667","40450779","40025430","40471466","40068004","40464927","40464155","40466995","30007451","40469166","40473052","40153382","40468516","40463954","40472148","40480144","40464969","40472168","40467310","40382611","40450605","40468212","40301106","40468153","40467872","40464973","40208798","09032981","40204657","40473086","40456315","40477775","40466522","40468091","40468956","40455367","40130361","40476636","40450507","40454855","40466668","40463447","40378067","40463960","40466344","40021389","40467000","40464995","40450544","40473239","40480170","40468730","40469118","40473080","09095313","40041952","40118507","40468151","40379582","40383446","40380707","10243602","40478315","40455338","40018965","40467785","40467747","40463409","40380178","40468991","40029465","07866657","40368796","40469994","40380824","40432977","40478358","40465033","40464800","40468521","40274984","40466744","40476537","40464853","40477723","40451674","40468770","40467799","40478331","40468547","07953967","40130627","40467793","40465055","40463345","40439833","10133089","40474928","40468828","40219657","40434712","40450460","40456205","07090006","40476570","40184273","40474933","40466996","40472312","40466309","40467816","40299081","40463976","40427959","40463939","10737825","05629689","40473125","40467764","40468728","40468524","40476508","40475537","40384802","40476548","40475555","40468069","40465573","40161806","40474744","40435870","40467733","40469873","40466415","40477403","09118844","40016912","09539341","40385809","40467776","40090650","40450458","40466487","05336183","40467738","40149755","40472264","40472075","10133659","40379249","40472781","40022375","40468790","40097148","40476614","40476568","40477359","40462708","40062295","30006424","40476557","40468848","40379918","40474822","40450618","40467857","30012182","40474916","40463953","40462859","40450597","40140508","40472114","40132173","40477418","40474989","40385534","40464196","40466646","40455440","40469030","40478376","40467861","40468259","40468928","40473171","40468826","40468413","40477300","40462618","09156464","40473194","40463818","40209481","40479027","40466378","40463455","40464678","40462655","40466311","40061895","40474749","40473133","40450537","40466328","40468377","40466488","30011768","40160117","40036566","40456053","40465692","09362144","40477796","40463236","40462886","40466377","40380290","40473043","40464924","40467745","40285075","40362929","40466698","40055980","40471461","40469149","40475731","40091234","40477717","40473134","40472092","40242776","10531526","40475777","40477675","40468417","40477259","40470096","40205180","00030596","09632630","40473236","40013405","40437463","40468542","40468930","40031887","40150580","40466405","40474514","40465670","40477374","40114063","40467803","40467856","40473092","40477757","40469159","40468067","40456252","09030191","40465693","40359781","40467376","40469126","40446787","40468224","40385535","40475634","40464221","40477254","40472160","09326946","40469141","40468113","40466750","40468993","30003509","40477285","40447196","05644720","40473129","40084677","40475757","40455286","40477247","40468823","40013657","40467331","40468428","40467753","40464794","40468185","40379951","40468506","40477688","40434714","40469971","40380990","40467393","40383183","40473122","40450423","40477435","40308629","40456484","40454900","40465017","40479031","40431461","40468751","40472073","10848173","40468816","40468736","40466381","40469092","40472089","10959926","40476536","40445559","40477702","40470520","40437843","40171057","40466721","40021188","40469985","40470006","40450463","40383358","10849430","40472243","40474737","40450530","40464106","40384849","40467694","40474320","40120208","40479030","40181665","40477292","09309041","10267121","40451867","40451824","40468164","40446774","40475715","40381891","40466737","40466433","40047715","40474977","07100023","30011640","40468460","40016027","40464904","40385572","40462845","40386041","40473130","40146767","40466746","40467322","40475858","40462969","40469181","40464968","40021089","40477759","40139455","40456441","40477410","40454204","40477457","40468396","08160436","40466303","40456230","40453482","40024778","40474913","40463548","40477734","40477674","40469976","40434656","40463874","40475791","40450629","40477409","40467267","40443012","40438086","40468850","40381746","10986380","40464952","40205560","40469008","40475528","40464877","40478332","40110465","00136033","10236138","40468510","40480178","40473234","40470049","40474956","40452535","40474907","40468221","40465607","40463820","40466998","40469147","40474910","40465038","40007284","40476445","40141037","9562148","40450513","40464165","40469970","40466708","40468531","40477523","40464007","40473206","10960385","40469045","40381059","40474896","40455357","40295384","07142817","40468731","40468294","40468168","40468582","40472126","40473204","40469107","40473170","40468560","10852285","40453517","40473199","40467786","40477476","40455639","40464169","40466386","40065495","40473205","40450628","40383640","40469114","40380530","40465697","40475562","07571171","40466352","40463749","40467301","40469046","40468137","40463514","40475790","40454232","40062191","40468175","40467794","40456195","40476588","40462689","40464941","40450453","40464907","40469192","40378366","40455988","40450569","09888237","40472320","40450479","40468065","40467813","40469131","40464870","40468505","40473147","40443446","40380725","40475559","30010746","40450619","40469091","40468057","40477683","40463732","40468305","40464912","40477448","40476637","40465608","40079083","40473166","40218349","40464335","40089502","40472369","40472183","40464094","40107246","30010290","40035844","40129261","40213258","40477253","07049528","40464308","40477726","40472169","40475554","40450582","40464093","40466390","40466611","40139587","40464788","09152264","40477634","40314582","40454284","40463466","40466343","40476618","09791460","40476542","40473059","40463861","40474853","40468936","40472157","40472274","40468950","40472152","40474932","40443223","40450518","40456197","40464101","40208806","40445409","40477721","40211987","40469955","40450640","40052000","40468201","40379782","40477643","40450563","40454821","40473071","40121787","40467868","40469972","09304281","40463724","40463847","40471405","40462698","40467253","40468962","40450641","40467302","40468435","40065699","40468715","40466382","40469989","40477774","40448702","40464798","09788007","40472229","10850700","40297069","40477246","40468984","10532198","40464897","40464772","40443220","40087021","40476491","40456448","40433321","30004145","40478362","40456072","40468236","40446444","40472119","40468254","40450580","40474740","40468274","40072906","40336895","40467706","40468743","40338551","40467353","10848135","40472099","40455072","40469157","40110612","40466452","40477709","40468548","40463883","40466518","40463513","40474897","40476420","40474969","40472133","40466634","40158197","40454202","40466732","40469227","40469969","40436477","40473198","40468803","10113493","40468158","40477724","40154312","40462991","40385615","40475789","40450540","40468192","40472337","40474825","40076717","40135872","30006587","40466717","40440025","40468868","07031711","40480218","40475652","40079711","40469238","40383616","40474766","40477787","40472172","40468955","40473187","40468087","40123716","40456306","40477444","40468304","40462700","40463071","40023488","40450483","30005180","40477522","40464222","40082741","10925189","40476511","40476545","09556943","40092821","07248087","40148498","40184466","40466745","40467734","40464053","40463511","10147280","40450750","40474882","40445854","40466984","40464830","40476547","40476428","40472087","40473175","40469152","10244250","40099066","40469020","40465644","40477473","40476430","40466283","40466727","40467306","40464931","40474982","40447044","10849098","40472227","40155456","40465025","09198549","40472517","40094934","40469228","40204214","40453851","40384779","40032060","40477685","40477385","40475699","40469062"};
		
		for (String string : ids) {
			goshcc.getDonorCplxType().add(getDonorWithIds(string, null));
		}

		Message<GOSHCC> message = constituentIdValidationTransformer.transform(new GenericMessage<GOSHCC>(goshcc));
		
		//There was one that was genuinely wrong.
		checkAllButOneHaveInternalIds(message);
		assertEquals("The ids in the file are not all in RE, we couldn't find: [30012182, 9562148]", getFirstErrorMessage(message));
	}

	private void checkAllButOneHaveInternalIds(Message<GOSHCC> message) {
		int successCount = 0;
		for (DonorCplxType donor : message.getPayload().getDonorCplxType()) {
			if (StringUtils.isNotBlank(donor.getInternalConstitID())){
				successCount++;
			}
		}
		assertEquals(message.getPayload().getDonorCplxType().size(), successCount+2);
	}

	private GOSHCC getGoshccWithOneDonorWithIds(String constitiuentId, String internalId) {
		GOSHCC goshcc = new GOSHCC();
		DonorCplxType donorCplxType = getDonorWithIds(constitiuentId,internalId);
		goshcc.getDonorCplxType().add(donorCplxType);
		return goshcc;
	}

	private DonorCplxType getDonorWithIds(String constitiuentId,
			String internalId) {
		DonorCplxType donorCplxType = new DonorCplxType();
		donorCplxType.setConstituentID(constitiuentId);
		donorCplxType.setInternalConstitID(internalId);
		return donorCplxType;
	}
	
	@SuppressWarnings("unchecked")
	private Object getFirstErrorMessage(Message<GOSHCC> message) {
		List<String> errors = (List<String>) message.getHeaders().get(MessageHeaderName.ERROR_HEADER.getName());
		return errors==null||errors.isEmpty()?null:errors.get(0);
	}
}
