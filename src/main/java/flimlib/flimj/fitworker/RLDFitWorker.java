/*-
 * #%L
 * Fluorescence lifetime analysis in ImageJ.
 * %%
 * Copyright (C) 2017 - 2022 Board of Regents of the University of Wisconsin-Madison.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package flimlib.flimj.fitworker;

import net.imagej.ops.OpEnvironment;
import flimlib.flimj.FitParams;
import flimlib.flimj.FitResults;
import net.imglib2.type.numeric.RealType;
import flimlib.FLIMLib;

public class RLDFitWorker<I extends RealType<I>> extends AbstractSingleFitWorker<I> {
	// RLD's own buffers
	private final float[] z, a, tau;

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
		final int retCode = FLIMLib.GCI_triple_integral_fitting_engine(
				params.xInc, transBuffer, adjFitStart, adjFitEnd,
				params.instr, params.noise, params.sig, z, a, tau,
				fittedBuffer, residualBuffer, chisqBuffer,
				rawChisq_target
		);

		// -1: malloc failed
		if (retCode < 0)
			results.retCode =
					retCode == -1 ? FitResults.RET_INTERNAL_ERROR : FitResults.RET_UNKNOWN;
		else
			// non-negative: iteration count
			results.retCode = FitResults.RET_OK;
	}

	@Override
	protected void afterFit() {
		// Barber, P. R. et al. (2008). Multiphoton time-domain fluorescence
		//   lifetime imaging microscopy: practical application to protein–protein
		//   interactions using global analysis. Journal of The Royal Society
		//   Interface, 6(suppl_1), S93-S105.
		if (params.paramFree[0]) {
			paramBuffer[0] = z[0];
		}
		if (params.paramFree[1]) {
			paramBuffer[1] = a[0];
		}
		if (params.paramFree[2]) {
			paramBuffer[2] = tau[0];
		}
		// splitting a and tau across components. This is how TRI2 does it. See:
		if (params.nComp >= 2) {
			if (params.paramFree[1]) {
				paramBuffer[1] = a[0]   * 3 / 4;
			}
			if (params.paramFree[3]) {
				paramBuffer[3] = a[0]   * 1 / 4;
			}
			if (params.paramFree[4]) {
				paramBuffer[4] = tau[0] * 2 / 3;
			}
		}
		if (params.nComp >= 3) {
			if (params.paramFree[3]) {
				paramBuffer[3] = a[0]   * 1 / 6;
			}
			if (params.paramFree[5]) {
				paramBuffer[5] = a[0]   * 1 / 6;
			}
			if (params.paramFree[6]) {
				paramBuffer[6] = tau[0] * 1 / 3;
			}
		}
		if (params.nComp >= 4) {
			// doesn't really matter, used estimation for global
			// see flimlib:flimlib/src/main/c/EcfGlobal.c
			for (int i = 7; i < nParam; i += 2) {
				if (params.paramFree[i]) {
					paramBuffer[i] = a[0]   / i;
				}
				if (params.paramFree[i]) {
					paramBuffer[i] = tau[0] / i;
				}
			}
		}
		super.afterFit();
	}

	@Override
	protected AbstractSingleFitWorker<I> duplicate(FitParams<I> params, FitResults rslts) {
		return new RLDFitWorker<>(params, rslts, ops);
	}
}
