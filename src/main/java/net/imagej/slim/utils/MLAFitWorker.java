package net.imagej.slim.utils;

import net.imglib2.type.numeric.RealType;
import slim.Float2DMatrix;
import slim.SLIMCurve;

public class MLAFitWorker<I extends RealType<I>> extends AbstractFitWorker<I> {

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
	public void do_fit(FitParams params, FitResults results) {
		System.out.println(transBuffer);
		System.out.println(params.noise);
		System.out.println(params.sig);
		System.out.println(params.param);
		System.out.println(params.paramFree);
		System.out.println(params.restrain);
		System.out.println(params.fitFunc);
		System.out.println(results.fitted);
		System.out.println(results.residuals);
		System.out.println(chisqBuffer);
		System.out.println(covar);
		System.out.println(alpha);
		System.out.println(erraxes);
		results.retCode = SLIMCurve.GCI_marquardt_fitting_engine(
				params.xInc, transBuffer, params.fitStart, params.fitEnd,
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
		results.covar = covar.asArray();
		results.alpha = alpha.asArray();
		results.errAxes = erraxes.asArray();
	}
}
