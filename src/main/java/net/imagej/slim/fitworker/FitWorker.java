package net.imagej.slim.fitworker;

import java.util.List;

import net.imglib2.type.numeric.RealType;

public interface FitWorker<I extends RealType<I>> {

	/**
	 * How many parameters should there be in {@code results.param}?
	 * E.g. 3 for one-component {@link MLAFitWorker} and 5 for
	 * {@link PhasorFitWorker}.
	 * @return The number of output parameters in the parameter array.
	 */
	int nParamOut();

	/**
	 * How many bins will be fitted?
	 * @return {@code fitEnd - fitStart}
	 */
	int nDataOut();

	void fitBatch(List<int[]> pos);
}
