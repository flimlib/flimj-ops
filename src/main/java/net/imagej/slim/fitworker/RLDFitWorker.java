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
				params.xInc, transBuffer, adjFitStart, adjFitEnd,
				params.instr, params.noise, params.sig, z, a, tau,
				fittedBuffer, residualBuffer, chisqBuffer,
				params.chisq_target
		);
	}

	@Override
	protected void afterFit() {
		// Barber, P. R. et al. (2008). Multiphoton time-domain fluorescence
		//   lifetime imaging microscopy: practical application to protein–protein
		//   interactions using global analysis. Journal of The Royal Society
		//   Interface, 6(suppl_1), S93-S105.
		paramBuffer[0] = z[0];
		paramBuffer[1] = a[0];
		paramBuffer[2] = tau[0];
		// splitting a and tau across components. This is how TRI2 does it. See:
		if (params.nComp >= 2) {
			paramBuffer[1] = a[0]   * 3 / 4;
			paramBuffer[3] = a[0]   * 1 / 4;
			paramBuffer[4] = tau[0] * 2 / 3;
		}
		if (params.nComp >= 3) {
			paramBuffer[3] = a[0]   * 1 / 6;
			paramBuffer[5] = a[0]   * 1 / 6;
			paramBuffer[6] = tau[0] * 1 / 3;
		}
		if (params.nComp >= 4) {
			// doesn't really matter, used estimation for global
			// see slim-curve:slim-curve/src/main/c/EcfGlobal.c
			for (int i = 7; i < nParam; i += 2) {
				paramBuffer[i] = a[0]   / i;
				paramBuffer[i] = tau[0] / i;
			}
		}
		super.afterFit();
	}

	@Override
	protected AbstractSingleFitWorker<I> duplicate(FitParams<I> params, FitResults rslts) {
		return new RLDFitWorker<>(params, rslts, ops);
	}
}
