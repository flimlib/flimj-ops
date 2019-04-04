package net.imagej.slim.fitworker;

import net.imagej.slim.utils.FitParams;
import net.imagej.slim.utils.FitResults;
import net.imglib2.type.numeric.RealType;
import slim.Float2DMatrix;
import slim.SLIMCurve;

public class MLAFitWorker<I extends RealType<I>> extends AbstractSingleFitWorker<I> {

	// reusable buffers
	private int oldNParams = -1;
	private Float2DMatrix covar, alpha, erraxes;

	@Override
	public void preFit(FitParams params, FitResults results) {
		// don't bother doing slow jni calls if size is the same
		int nParams = params.param.length;
		if (oldNParams != nParams) {
			covar = new Float2DMatrix(nParams, nParams);
			alpha = new Float2DMatrix(nParams, nParams);
			erraxes = new Float2DMatrix(nParams, nParams);
			oldNParams = nParams;
		}
	}

	/**
	 * Performs an MLA fit.
	 */
	@Override
	public void doFit(FitParams params, FitResults results) {
		results.retCode = SLIMCurve.GCI_marquardt_fitting_engine(
				params.xInc, transBuffer, 0, params.fitEnd - params.fitStart,
				params.instr, params.noise, params.sig, results.param,
				params.paramFree,
				params.restrain,
				params.fitFunc,
				results.fitted, results.residuals, chisqBuffer, covar, alpha, erraxes, 
				params.chisq_target, params.chisq_delta, params.chisq_percent
		);
	}

	@Override
	public void postFit(FitParams params, FitResults results) {
		// TODO put into image
//		results.covar = covar.asArray();
//		results.alpha = alpha.asArray();
//		results.errAxes = erraxes.asArray();
	}
}
