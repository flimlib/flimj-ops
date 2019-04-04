package net.imagej.slim.utils;

import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;

public interface FitWorker<I extends RealType<I>> {
	/**
	 * Fits time-resolved intensity data in {@code trans} with {@code params}
	 * and stores generated results in {@code results}.
	 * @param trans - A series of time-resolved intensity data of a pixel
	 * @param params - The fitting parameters
	 * @param results - The fitted results
	 */
	void fit(IterableInterval<I> trans, FitParams params, FitResults results);

	/**
	 * How many parameters should there be in {@code results.param}?
	 * E.g. 3 for {@link net.imagej.slim.utils.MLAFitWorker} and 5 for
	 * {@link net.imagej.slim.utils.PhasorFitWorker}.
	 * @return The number of output parameters in the parameter array.
	 */
	int nParamOut();
}
