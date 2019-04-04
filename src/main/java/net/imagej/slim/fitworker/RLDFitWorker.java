package net.imagej.slim.fitworker;

import net.imagej.slim.utils.FitParams;
import net.imagej.slim.utils.FitResults;
import net.imagej.slim.utils.Utils;
import net.imglib2.type.numeric.RealType;
import slim.SLIMCurve;

public class RLDFitWorker<I extends RealType<I>> extends AbstractSingleFitWorker<I> {
	// reusable buffers
	private float[] z, a, tau;
	
	@Override
	protected void preFit(FitParams params, FitResults results) {
		// nothing more than ensuring the parameter buffers are valid
		z = Utils.reallocIfWeird(z, 1);
		a = Utils.reallocIfWeird(a, 1);
		tau = Utils.reallocIfWeird(tau, 1);
	}

	/**
	 * Performs the RLD fit.
	 */
	@Override
	protected void doFit(FitParams params, FitResults results) {
		results.retCode = SLIMCurve.GCI_triple_integral_fitting_engine(
				params.xInc, transBuffer, 0, params.fitEnd - params.fitStart,
				params.instr, params.noise, params.sig, z, a, tau,
				results.fitted, results.residuals, chisqBuffer,
				params.chisq_target
		);
	}

	@Override
	protected void postFit(FitParams params, FitResults results) {
		// and copies back
		results.param[0] = z[0];
		results.param[1] = a[0];
		results.param[2] = tau[0];
	}
}
