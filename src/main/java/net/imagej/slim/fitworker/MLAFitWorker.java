package net.imagej.slim.fitworker;

import net.imagej.ops.OpEnvironment;
import net.imagej.slim.FitParams;
import net.imagej.slim.FitResults;
import net.imglib2.type.numeric.RealType;
import slim.Float2DMatrix;
import slim.SLIMCurve;

public class MLAFitWorker<I extends RealType<I>> extends AbstractSingleFitWorker<I> {

	// reusable buffers
	private final Float2DMatrix covar, alpha, erraxes;

	private final RLDFitWorker<I> estimatorWorker;

	public MLAFitWorker(FitParams<I> params, FitResults results, OpEnvironment ops) {
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
				// System.out.print(Arrays.toString(results.param));
				break;
			}
		}
		super.beforeFit();
	}

	/**
	 * Performs an MLA fit.
	 */
	@Override
	public void doFit() {
		results.retCode = SLIMCurve.GCI_marquardt_fitting_engine(
				params.xInc, transBuffer, adjFitStart, adjFitEnd,
				params.instr, params.noise, params.sig, paramBuffer,
				params.paramFree,
				params.restrain,
				params.fitFunc,
				fittedBuffer, residualBuffer, chisqBuffer, covar, alpha, erraxes, 
				params.chisq_target, params.chisq_delta, params.chisq_percent
		);
	}

	@Override
	protected AbstractSingleFitWorker<I> duplicate(FitParams<I> params, FitResults rslts) {
		return new MLAFitWorker<>(params, rslts, ops);
	}
}
