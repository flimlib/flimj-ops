package net.imagej.slim.utils;

import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;

public abstract class AbstractFitWorker<I extends RealType<I>> implements FitWorker <I> {

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

	public void fit(IterableInterval<I> trans, FitParams params, FitResults results) {
		int nParams = params.param.length;
		int nData = params.fitEnd + 1;

		chisqBuffer = Utils.reallocIfWeird(chisqBuffer, 1);

		transBuffer = Utils.ii2FloatArr(trans, params.fitStart, nData, transBuffer);

//		System.out.println(Arrays.toString(transBuffer));
		//////////////////////
//		final float y[] = {
//				// the first 38 data points are unused
//		/*		43, 39, 50, 46, 56, 63, 62, 74, 60, 72, 58, 47, 41, 69, 69, 58,
//		 *		55, 37, 55, 50, 52, 59, 51, 52, 51, 50, 53, 40, 45, 34, 54, 44,
//		 *		53, 47, 56, 62, 66, 82, */90, 108, 122, 323, 1155, 4072, 8278, 11919, 13152, 13071,
//				12654, 11946, 11299, 10859, 10618, 10045, 9576, 9208, 9113, 8631, 8455, 8143, 8102, 7672, 7384, 7463,
//				7254, 6980, 6910, 6411, 6355, 6083, 5894, 5880, 5735, 5528, 5343, 5224, 4933, 5026, 4914, 4845,
//				4681, 4426, 4485, 4271, 4295, 4183, 3989, 3904, 3854, 3801, 3600, 3595, 3434, 3457, 3291, 3280,
//				3178, 3132, 2976, 2973, 2940, 2770, 2969, 2851, 2702, 2677, 2460, 2536, 2528, 2347, 2382, 2380,
//				2234, 2251, 2208, 2115, 2136, 2000, 2006, 1970, 1985, 1886, 1898, 1884, 1744, 1751, 1797, 1702,
//				1637, 1547, 1526, 1570, 1602, 1557, 1521, 1417, 1391, 1332, 1334, 1290, 1336, 1297, 1176, 1189,
//				1220, 1209, 1217, 1140, 1079, 1059, 1074, 1061, 1013, 1075, 1021, 1012, 940, 982, 866, 881,
//				901, 883, 893, 845, 819, 831, 758, 794, 779, 772, 779, 791, 729, 732, 687, 690,
//				698, 661, 647, 668, 642, 619, 629, 656, 579, 579, 600, 563, 584, 531, 554, 526,
//				484, 530, 515, 493, 502, 479, 445, 439, 466, 431, 423, 451, 412, 415, 393, 404,
//				390, 398, 352, 394, 376, 338, 377, 367, 355, 352, 375, 339, 347, 316, 295, 322,
//				311, 294, 304, 264, 293, 294, 283, 278, 302, 253, 259, 252, 278, 254, 245, 246,
//				242, 226, 241, 222, 198, 197, 245, 221, 228, 224, 216, 174, 166, 163, 127, 122
//			};
//		transBuffer = y;
//		params.xInc = 0.058628749f;
//		params.fitStart = 46 - 38;
//		params.fitEnd = 255 - 38;
		//////////////////////
		results.fitted = Utils.reallocIfWeird(results.fitted, nData);
		results.residuals = Utils.reallocIfWeird(results.residuals, nData);
		results.param = Utils.reallocIfWeird(results.param, nParamOut());
		System.arraycopy(params.param, 0, results.param, 0, nParams);

		preFit(params, results);

		doFit(params, results);

		postFit(params, results);
		results.chisq = chisqBuffer[0];
	}
}
