package net.imagej.slim;

import java.util.Arrays;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import slim.*;

/**
 * The collection of all fit parameters required to perform a single fit of an
 * image. Fields named {@code xxMap} are image representations of the particular
 * attribute. Other fields are not intended to be used by external programs and
 * should be ignored when this object is processed with in the fitting ops.
 * 
 * @param <I> The type of the transient data.
 */
public class FitParams<I extends RealType<I>> {
	/** Fields with this value are uninitialized */
	public static final int UNINIT = -1;

	/** The time increment between two consecutive data points */
	public float xInc = UNINIT;

	/** The transient data to fit */
	public float[] trans;

	public int ltAxis = UNINIT;

	/** The image representation of the dataset */
	public RandomAccessibleInterval<I> transMap;

	/** The start of the decay interval */
	public int fitStart = UNINIT;

	/** The end of the decay interval */
	public int fitEnd = UNINIT;

	/** The array of instrument response (optional) */
	public float[] instr;

	/** The assumed noise model of the fit (Poisson by default) @see NoiseType */
	public NoiseType noise = NoiseType.NOISE_POISSON_FIT;

	/**
	 * The standard deviation (sigma) of the data, used for calculating
	 * chi-squared if {@link #noise} is {@link NoiseType#NOISE_CONST} or
	 * {@link NoiseType#NOISE_GIVEN}.
	 */
	public float[] sig;

	/**
	 * The number of exponential components of the fit (1 by default). This
	 * parameter is only used by MLA and Global ops, ignored otherwise.
	 */
	public int nComp = 1;

	/** The estimated parameters of the fit (global setting) */
	public float[] param;

	/**
	 * The image representation of the estimated parameters the fit (per-pixel setting,
	 * overides {@link #param})
	 */
	public RandomAccessibleInterval<FloatType> paramMap;

	/** The indicators of which of the parameters can be changed */
	public boolean[] paramFree;

	/** The fit restraint ({@link RestrainType#ECF_RESTRAIN_DEFAULT} by default) */
	public RestrainType restrain = RestrainType.ECF_RESTRAIN_DEFAULT;

	/** The fitting model to use (Z + A_1e^(-t/tau_1) + A_2e^(-t/tau_2) + ... 
	 * by default)
	 */
	public FitFunc fitFunc = FitFunc.GCI_MULTIEXP_TAU;

	/**
	 * Stopping condition 1: stop if chi-squared is less than {@link #chisq_target}
	 * (1 by default)
	 */
	public float chisq_target = 1;

	/**
	 * Stopping condition 2: stop if change in chi-squared is less than
	 * {@link #chisq_target} (1E-4 by default)
	 */
	public float chisq_delta = 0.0001f;

	/** Confidence interval when calculating the error axes (95% by default) */
	public int chisq_percent = 95;

	public float iThresh = UNINIT;

	// FitResults Settings

	public boolean dropBad = true;

	/**
	 * Whether to generate an image representation for the return codes
	 * ({@code false} by default)
	 */
	public boolean getReturnCodeMap = false;

	/**
	 * Whether to generate an image representation for fitted parameters
	 * ({@code true} by default)
	 */
	public boolean getParamMap = true;

	/**
	 * Whether to generate an image representation for fitted transients
	 * ({@code false} by default)
	 */
	public boolean getFittedMap = false;
	
	/**
	 * Whether to generate an image representation for residuals
	 * ({@code false} by default)
	 */
	public boolean getResidualsMap = false;

	/**
	 * Whether to generate an image representation for chi-squred
	 * ({@code false} by default)
	 */
	public boolean getChisqMap = false;

	/**
	 * Create a new instance of {@link FitParams} with shallow copy (maps are
	 * not duplicated).
	 * 
	 * @return A clone of the current instance.
	 */
	public FitParams<I> copy() {
		FitParams<I> newParams = new FitParams<>();
		newParams.xInc = xInc;
		newParams.trans = trans;
		newParams.ltAxis = ltAxis;
		newParams.transMap = transMap;
		newParams.fitStart = fitStart;
		newParams.fitEnd = fitEnd;
		newParams.instr = instr;
		newParams.noise = noise;
		newParams.sig = sig;
		newParams.nComp = nComp;
		newParams.param = param;
		newParams.paramMap = paramMap;
		newParams.paramFree = paramFree;
		newParams.restrain = restrain;
		newParams.fitFunc = fitFunc;
		newParams.chisq_target = chisq_target;
		newParams.chisq_delta = chisq_delta;
		newParams.chisq_percent = chisq_percent;
		newParams.iThresh = iThresh;
		newParams.dropBad = dropBad;
		newParams.getParamMap = getParamMap;
		newParams.getFittedMap = getFittedMap;
		newParams.getResidualsMap = getResidualsMap;
		newParams.getChisqMap = getChisqMap;
		newParams.getReturnCodeMap = getReturnCodeMap;
		return newParams;
	}

	@Override
	public String toString() {
		String str = String.format("xInc: %f, interval: [%d, %d], intensity threshold: %f, instr: %s, noise: %s, sig: %s, param: %s, paramFree: %s, restrain: %s, fitFunc: %s, chisq_target: %f, chisq_delta: %f, chisq_percent: %d", xInc, fitStart, fitEnd, iThresh, Arrays.toString(instr), noise.name(), Arrays.toString(sig), Arrays.toString(param), Arrays.toString(paramFree), restrain.name(), fitFunc, chisq_target, chisq_delta, chisq_percent);
		return str;
	}
}
