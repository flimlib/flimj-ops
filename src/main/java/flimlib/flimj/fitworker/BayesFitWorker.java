package flimlib.flimj.fitworker;

import net.imagej.ops.OpEnvironment;
import flimlib.FLIMLib;
import flimlib.flimj.FitParams;
import flimlib.flimj.FitResults;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class BayesFitWorker<I extends RealType<I>> extends AbstractSingleFitWorker<I> {

	// Bayes's own buffers
	private final float[] error, minusLogProb;
	private final int[] nPhotons;

	private float laserPeriod;

	private float[] gridMin, gridMax;

	public BayesFitWorker(FitParams<I> params, FitResults results, OpEnvironment ops) {
		super(params, results, ops);

		if (nParam != 3 && nParam != 5)
			throw new IllegalArgumentException(
					"Bayesian analysis only takes 3 (single) or 5 (double) parameters");

		error = new float[nParam];
		minusLogProb = new float[1];
		nPhotons = new int[1];

		if (gridMin == null || gridMax == null)
			estimateGrid();

		FLIMLib.Bayes_set_search_grid(gridMin, gridMax);
	}

	private void estimateGrid() {
		gridMin = new float[nParam];
		gridMax = new float[nParam];

		FitParams<I> copyParams = params.copy();
		copyParams.getChisqMap = true;
		copyParams.param = null;
		FitResults estResults = ((FitResults) ops.run("flim.fitRLD", copyParams));
		Img<FloatType> paramMap = estResults.paramMap;
		Img<FloatType> chisqMap = estResults.chisqMap;

		float chisqCutoff = ops.stats().percentile(chisqMap, 20).getRealFloat();

		// calculate mean and std (exluding Inf and NaN)
		for (int i = 0; i <= paramMap.max(params.ltAxis); i++) {
			double mean = 0;
			double std = 0;
			double count = 0;

			IntervalView<FloatType> paramPlane = Views.hyperSlice(paramMap, params.ltAxis, i);
			Cursor<FloatType> ppCursor = paramPlane.cursor();
			Cursor<FloatType> xmCursor = chisqMap.cursor();

			// calculate the mean and std of best 20% fit
			while (ppCursor.hasNext()) {
				float pf = ppCursor.next().getRealFloat();
				float xf = xmCursor.next().getRealFloat();
				if (xf <= chisqCutoff && Float.isFinite(pf)) {
					mean += pf;
					std += pf * pf;
					count++;
				}
			}
			mean /= count;
			std /= count;

			// Global will give a std of 0 for taus
			double tauStdCompensation = (i == 2 || i == 4) ? 10 : 0;
			std = Math.sqrt(std - mean * mean + tauStdCompensation);

			// min[i] = (float) Math.max(mean - std, 0);
			gridMin[i] = 0;
			gridMax[i] = (float) Math.max(mean + std * 2, 0);
		}
	}

	@Override
	protected void beforeFit() {
		super.beforeFit();
		// TODO: expose as a parameter
		laserPeriod = params.xInc * (adjFitEnd - adjFitStart);
	}

	/**
	 * Performs an Bayes fit.
	 */
	@Override
	public void doFit() {
		final int retCode = FLIMLib.Bayes_fitting_engine(params.xInc, transBuffer, adjFitStart,
				adjFitEnd, laserPeriod, params.instr, paramBuffer, params.paramFree, fittedBuffer,
				residualBuffer, error, minusLogProb, nPhotons, chisqBuffer);

		switch (retCode) {
			case -1: // Bayes: Invalid data
			case -2: // Bayes: Invalid data window
			case -3: // Bayes: Invalid model
			case -4: // Bayes: Functionality not supported
			case -5: // Bayes: Invalid fixed parameter value
			case -6: // Bayes: All parameter values are fixed
			case -8: // Bayes: No rapid grid for parameter estimation
			case -14: // Bayes: Insufficient gridimation failure
			results.retCode = FitResults.RET_BAD_SETTING;
			break;
			
			case -7: // Bayes: Parameter estError in Ave & Errs
			case -9: // Bayes: Model selection parameter estimation failure
			case -10: // Bayes: Model selection Hessian error
			case -11: // Bayes: w max not found, pdf too sharp, too many counts?
			case -12: // Bayes: Error in Ave & Errs (MP Vals only)
			case -13: // Bayes: Error in Ave & Errs
			case -99: // BAYES__RESULT_USER_CANCEL
				results.retCode = FitResults.RET_BAD_FIT_DIVERGED;
				break;

			default:
				results.retCode = retCode >= 0 ? FitResults.RET_OK : FitResults.RET_UNKNOWN;
				break;
		}
	}

	@Override
	protected AbstractSingleFitWorker<I> duplicate(FitParams<I> params, FitResults rslts) {
		// child will inherit the estimated grid config
		BayesFitWorker<I> child = new BayesFitWorker<>(params, rslts, ops);
		child.gridMin = this.gridMin;
		child.gridMax = this.gridMax;
		return child;
	}
}
