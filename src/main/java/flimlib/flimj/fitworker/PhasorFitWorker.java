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
		final int retCode = FLIMLib.GCI_Phasor(
				params.xInc, transBuffer, adjFitStart, adjFitEnd,
				z, u, v, tauPhi, tauMod, tau,
				fittedBuffer, residualBuffer, chisqBuffer
		);

		switch (retCode) {
			case -1: // PHASOR_ERR_INVALID_DATA (data == null)
			case -2: // PHASOR_ERR_INVALID_WINDOW (nbins < 0)
				results.retCode = FitResults.RET_BAD_SETTING;
				break;

			default: // non-negative: iteration count
				results.retCode = retCode >= 0 ? FitResults.RET_OK : FitResults.RET_UNKNOWN;
				break;
		}
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
