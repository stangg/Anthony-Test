package org.gosh.validation.general.deduplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gosh.re.dmcash.bindings.GOSHCC.DonorCplxType;

public interface ExtendedDatasetLookup {
	HashMap<DonorCplxType, List<ExtendedDataSetModel>> lookup(Map<DonorCplxType, List<PossibleMatchModel>> fuzzyMatches);
}
