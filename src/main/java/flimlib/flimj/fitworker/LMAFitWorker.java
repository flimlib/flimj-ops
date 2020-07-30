package flimlib.flimj.fitworker;

import net.imagej.ops.OpEnvironment;
import flimlib.flimj.FitParams;
import flimlib.flimj.FitResults;
import net.imglib2.type.numeric.RealType;
import flimlib.Float2DMatrix;
import flimlib.FLIMLib;

public class LMAFitWorker<I extends RealType<I>> extends AbstractSingleFitWorker<I> {

	// reusable buffers
	private final Float2DMatrix covar, alpha, erraxes;

	private final RLDFitWorker<I> estimatorWorker;

	public LMAFitWorker(FitParams<I> params, FitResults results, OpEnvironment ops) {
		super(params, results, ops);
		covar = new Float2DMatrix(nParam, nParam);
		alpha = new Float2DMatrix(nParam, nParam);
		erraxes = new Float2DMatrix(nParam, nParam);
		// in case both param and paramMap are not set
		estimatorWorker = new RLDFitWorker<>(params, results, ops);
	}

	@Override
	protected void beforeFit() {
		// needs RLD estimation
		for (float param : paramBuffer) {
			// no estimation (+Inf was set by RAHelper#loadData)
			if (param == Float.POSITIVE_INFINITY) {
				estimatorWorker.fitSingle();
				break;
			}
		}
		super.beforeFit();
	}

	/**
	 * Performs an LMA fit.
	 */
	@Override
	public void doFit() {
		final int retCode = FLIMLib.GCI_marquardt_fitting_engine(
				params.xInc, transBuffer, adjFitStart, adjFitEnd,
				params.instr, params.noise, params.sig, paramBuffer,
				params.paramFree,
				params.restrain,
				params.fitFunc,
				fittedBuffer, residualBuffer, chisqBuffer, covar, alpha, erraxes, 
				rawChisq_target, params.chisq_delta, params.chisq_percent
		);

		switch (retCode) {
			case -1: // initial estimation failed
			case -2: // max iteration reached before converge
			case -3: // iteration failed
			case -4: // final iteration failed
			case -5: // error estimation failed
				results.retCode = FitResults.RET_BAD_FIT_DIVERGED;
				break;

			default: // non-negative: iteration count
				results.retCode = retCode >= 0 ? FitResults.RET_OK : FitResults.RET_UNKNOWN;
				break;
		}
	}

	@Override
	protected AbstractSingleFitWorker<I> duplicate(FitParams<I> params, FitResults rslts) {
		return new LMAFitWorker<>(params, rslts, ops);
	}
}
