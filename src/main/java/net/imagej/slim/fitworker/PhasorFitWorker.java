package net.imagej.slim.fitworker;

import net.imagej.slim.utils.FitParams;
import net.imagej.slim.utils.FitResults;
import net.imagej.slim.utils.Utils;
import net.imglib2.type.numeric.RealType;
import slim.SLIMCurve;

public class PhasorFitWorker<I extends RealType<I>> extends AbstractSingleFitWorker<I> {

	private static final int NPARAMOUT = 6;
	// reusable buffers
	private float[] z, u, v, tau, tauPhi, tauMod;

	@Override
	protected void preFit(FitParams params, FitResults results) {
		// nothing more than ensuring the parameter buffers are valid
		z = Utils.reallocIfWeird(z, 1);
		u = Utils.reallocIfWeird(u, 1);
		v = Utils.reallocIfWeird(v, 1);
		tauPhi = Utils.reallocIfWeird(tauPhi, 1);
		tauMod = Utils.reallocIfWeird(tauMod, 1);
		tau = Utils.reallocIfWeird(tau, 1);
	}

	@Override
	protected void doFit(FitParams params, FitResults results) {
		results.retCode = SLIMCurve.GCI_Phasor(
				params.xInc, transBuffer, 0, params.fitEnd - params.fitStart,
				z, u, v, tauPhi, tauMod, tau,
				results.fitted, results.residuals, chisqBuffer
		);
	}

	@Override
	protected void postFit(FitParams params, FitResults results) {
		results.param = Utils.reallocIfWeird(results.param, 6);
		// and copies back
		results.param[0] = z[0];
		results.param[1] = u[0];
		results.param[2] = v[0];
		results.param[3] = tauPhi[0];
		results.param[4] = tauMod[0];
		results.param[5] = tau[0];
	}

	@Override
	public int nParamOut() {
		return NPARAMOUT;
	}
}
