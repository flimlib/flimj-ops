package net.imagej.slim.fitworker;

import java.util.List;

import net.imagej.slim.FitParams;
import net.imagej.slim.FitResults;
import net.imglib2.type.numeric.RealType;

public interface FitWorker<I extends RealType<I>> {

	/**
	 * How many parameters should there be in {@code results.param}?
	 * E.g. 3 for {@link MLAFitWorker} and 5 for
	 * {@link PhasorFitWorker}.
	 * @return The number of output parameters in the parameter array.
	 */
	int nParamOut();

	void fitBatch(FitParams<I> params, FitResults rslts, List<int[]> pos, int lifetimeAxis);
}
