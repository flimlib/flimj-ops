package net.imagej.slim.utils;

import net.imglib2.type.numeric.RealType;
import slim.SLIMCurve;

public class RLDFitWorker<I extends RealType<I>> extends AbstractFitWorker<I> {

	// reusable buffers
	private float[] Z, A, tau;
	
	@Override
	protected void preFit(FitParams params, FitResults results) {
		// nothing more than ensuring the parameter buffers are valid
		Z = reallocIfWeird(Z, 1);
		A = reallocIfWeird(A, 1);
		tau = reallocIfWeird(tau, 1);
	}

	/**
	 * Performs the RLD fit.
	 */
	@Override
	protected void do_fit(FitParams params, FitResults results) {
		results.retCode = SLIMCurve.GCI_triple_integral_fitting_engine(
				params.xInc, transBuffer, params.fitStart, params.fitEnd,
				params.instr, params.noise, params.sig, Z, A, tau,
				results.fitted, results.residuals, chisqBuffer,
				params.chisq_target
		);
	}

	@Override
	protected void postFit(FitParams params, FitResults results) {
		// and copies back
		results.param[0] = Z[0];
		results.param[1] = A[0];
		results.param[2] = tau[0];
	}
}
