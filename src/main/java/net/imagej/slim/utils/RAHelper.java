package net.imagej.slim.utils;

import net.imagej.slim.FitParams;
import net.imagej.slim.FitResults;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;

public class RAHelper<I extends RealType<I>> {

	final RandomAccess<I> transRA;
	final RandomAccess<FloatType> initialParamRA;
	final RandomAccess<FloatType> fittedParamRA;
	final RandomAccess<FloatType> fittedRA;
	final RandomAccess<FloatType> residualsRA;
	final RandomAccess<FloatType> chisqRA;
	final RandomAccess<IntType> retcodeRA;
	final int lifetimeAxis;

	public RAHelper(FitParams<I> params, FitResults rslts, int lifetimeAxis) {
		transRA = params.transMap.randomAccess();
		initialParamRA = params.paramMap != null ? params.paramMap.randomAccess()    : null;
		fittedParamRA =  params.getParamMap ?      rslts.paramMap.randomAccess()     : null;
		fittedRA =       params.getFittedMap ?     rslts.fittedMap.randomAccess()    : null;
		residualsRA =    params.getResidualsMap ?  rslts.residualsMap.randomAccess() : null;
		chisqRA =        params.getChisqMap ?      rslts.chisqMap.randomAccess()     : null;
		retcodeRA =      params.getReturnCodeMap ? rslts.retCodeMap.randomAccess()   : null;
		this.lifetimeAxis = lifetimeAxis;
	}

	public void loadData(float[] transBuffer, float[] paramBuffer, FitParams<I> params, int[] xytPos) {
		transRA.setPosition(xytPos);
		transRA.setPosition(params.fitStart, lifetimeAxis);
		// fill transient buffer
		for (int t = 0; t < transBuffer.length; t++, transRA.fwd(lifetimeAxis)) {
			transBuffer[t] = transRA.get().getRealFloat();
		}
		// fill initial values from params.param
		if (initialParamRA != null) {
			initialParamRA.setPosition(xytPos);
			for (int p = 0; p < paramBuffer.length; p++, initialParamRA.fwd(lifetimeAxis)) {
				paramBuffer[p] = initialParamRA.get().getRealFloat();
			}
		}
		// try to fill initial values from map if per-pixel parameter not present
		else if (params.param != null) {
			for (int p = 0; p < paramBuffer.length; p++) {
				paramBuffer[p] = params.param[p];
			}
		}
	}
	
	public void commitRslts(FitParams<I> params, FitResults rslts, int[] xytPos) {
		// fill in maps on demand
		if (params.getParamMap) {
			fillRA(fittedParamRA, xytPos, rslts.param);
		}
		if (params.getFittedMap) {
			fillRA(fittedRA, xytPos, rslts.fitted);
		}
		if (params.getResidualsMap) {
			fillRA(residualsRA, xytPos, rslts.residuals);
		}
		if (params.getChisqMap) {
			chisqRA.setPosition(xytPos);
			chisqRA.get().set(rslts.chisq);
		}
		if (params.getReturnCodeMap) {
			retcodeRA.setPosition(xytPos);
			retcodeRA.get().set(rslts.retCode);
		}
	}

	private void fillRA(RandomAccess<FloatType> ra, int[] xytPos, float[] arr) {
		ra.setPosition(xytPos);
		for (float f : arr) {
			ra.get().set(f);
			ra.fwd(lifetimeAxis);
		}
	}
}
