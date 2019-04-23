package net.imagej.slim.fitworker;

import net.imagej.ops.OpEnvironment;
import net.imagej.slim.FitParams;
import net.imagej.slim.FitResults;
import net.imglib2.type.numeric.RealType;
import slim.SLIMCurve;

public class RLDFitWorker<I extends RealType<I>> extends AbstractSingleFitWorker<I> {
	// RLD's own buffers
	private float[] z, a, tau;

	public RLDFitWorker(FitParams<I> params, FitResults results, OpEnvironment ops) {
		super(params, results, ops);
		z = new float[1];
		a = new float[1];
		tau = new float[1];
	}

	@Override
	protected void beforeFit() {
		super.beforeFit();
		// setup params
		z[0] =   paramBuffer[0];
		a[0] =   paramBuffer[1];
		tau[0] = paramBuffer[2];
	}

	/**
	 * Performs the RLD fit.
	 */
	@Override
	protected void doFit() {
		results.retCode = SLIMCurve.GCI_triple_integral_fitting_engine(
				params.xInc, transBuffer, 0, nData,
				params.instr, params.noise, params.sig, z, a, tau,
				fittedBuffer, residualBuffer, chisqBuffer,
				params.chisq_target
		);
	}

	@Override
	protected void postFit() {
		// and copies back
		paramBuffer[0] = z[0];
		paramBuffer[1] = a[0];
		paramBuffer[2] = tau[0];
		super.postFit();
	}

	@Override
	protected AbstractSingleFitWorker<I> duplicate(FitParams<I> params, FitResults rslts) {
		return new RLDFitWorker<>(params, rslts, ops);
	}
}
