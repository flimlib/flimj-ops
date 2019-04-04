package net.imagej.slim.fitworker;

import net.imagej.slim.utils.FitParams;
import net.imagej.slim.utils.FitResults;
import net.imagej.slim.utils.Utils;
import net.imglib2.type.numeric.RealType;

public abstract class AbstractSingleFitWorker<I extends RealType<I>> implements FitWorker<I> {

	protected float[] transBuffer;
	protected float[] chisqBuffer;

	protected static final int DEFAULT_NPARAMOUT = 3;

	/**
	 * How many parameters should there be in {@code results.param}?
	 * E.g. 3 for {@link net.imagej.slim.utils.MLAFitWorker} and 5 for
	 * {@link net.imagej.slim.utils.PhasorFitWorker}.
	 * @return The number of output parameters in the parameter array.
	 */
	public int nParamOut() {
		return DEFAULT_NPARAMOUT;
	}

	/**
	 * A routine called before {@link #doFit(FitParams, FitResults)}. Can be used to setup
	 * parameters.
	 * @param params - The fitting parameters
	 * @param results - The fitted results
	 */
	protected void preFit(FitParams params, FitResults results) {}

	/**
	 * Does the actual implementation-specific fitting routine.
	 * @param params - The fitting parameters
	 * @param results - The fitted results
	 */
	protected abstract void doFit(FitParams params, FitResults results);

	/**
	 * A routine called before {@link #doFit(FitParams, FitResults)}. Can be used to copy back
	 * results from buffers.
	 * @param params - The fitting parameters
	 * @param results - The fitted results
	 */
	protected void postFit(FitParams params, FitResults results) {}

	/**
	 * @inheritDoc
	 */
	@Override
	public void fitSingle(float[] trans, FitParams params, FitResults results) {
		int nParams = params.param.length;
		int nData = params.fitEnd + 1;

		chisqBuffer = Utils.reallocIfWeird(chisqBuffer, 1);

		transBuffer = trans == null ? transBuffer : trans;

		results.fitted = Utils.reallocIfWeird(results.fitted, nData);
		results.residuals = Utils.reallocIfWeird(results.residuals, nData);
		results.param = Utils.reallocIfWeird(results.param, nParamOut());
		System.arraycopy(params.param, 0, results.param, 0, nParams);

		preFit(params, results);

		doFit(params, results);

		postFit(params, results);
		results.chisq = chisqBuffer[0];
	}

	@Override
	public void fitGlobal(float[][] trans, FitParams params, FitResults[] results) {
		throw new UnsupportedOperationException("Single-pixel fitting worker is not applicable for image fitting.");
	}
}
