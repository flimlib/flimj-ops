package net.imagej.slim.fitworker;

import net.imagej.ops.OpEnvironment;
import net.imagej.slim.FitParams;
import net.imagej.slim.FitResults;
import net.imglib2.type.numeric.RealType;
import slim.SLIMCurve;

public class PhasorFitWorker<I extends RealType<I>> extends AbstractSingleFitWorker<I> {

	private static final int NPARAMOUT = 6;
	// Phasor's own buffers
	private final float[] z, u, v, tau, tauPhi, tauMod;

	public PhasorFitWorker(FitParams<I> params, FitResults results, OpEnvironment ops) {
		super(params, results, ops);
		z = new float[1];
		u = new float[1];
		v = new float[1];
		tauPhi = new float[1];
		tauMod = new float[1];
		tau = new float[1];
	}

	@Override
	protected void beforeFit() {
		for (int i = 0; i < paramBuffer.length; i++) {
			// no estimation (+Inf was set by RAHelper#loadData)
			// this value makes phasor explode
			if (paramBuffer[i] == Float.POSITIVE_INFINITY) {
				paramBuffer[i] = 0;
			}
		}
		super.beforeFit();
		// setup params
		z[0] =      paramBuffer[0];
		u[0] = 		paramBuffer[1];
		v[0] = 		paramBuffer[2];
		tauPhi[0] = paramBuffer[3];
		tauMod[0] = paramBuffer[4];
		tau[0] = 	paramBuffer[5];
	}

	@Override
	protected void doFit() {
		results.retCode = SLIMCurve.GCI_Phasor(
				params.xInc, transBuffer, adjFitStart, adjFitEnd,
				z, u, v, tauPhi, tauMod, tau,
				fittedBuffer, residualBuffer, chisqBuffer
		);
	}

	@Override
	protected void afterFit() {
		// and copies back
		paramBuffer[0] = z[0];
		paramBuffer[1] = u[0];
		paramBuffer[2] = v[0];
		paramBuffer[3] = tauPhi[0];
		paramBuffer[4] = tauMod[0];
		paramBuffer[5] = tau[0];
		super.afterFit();
	}

	@Override
	public int nParamOut() {
		return NPARAMOUT;
	}

	@Override
	protected AbstractSingleFitWorker<I> duplicate(FitParams<I> params, FitResults rslts) {
		return new PhasorFitWorker<>(params, rslts, ops);
	}
}
