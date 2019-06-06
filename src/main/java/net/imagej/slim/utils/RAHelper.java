package net.imagej.slim.utils;

import net.imagej.slim.FitParams;
import net.imagej.slim.FitResults;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;

public class RAHelper<I extends RealType<I>> {

	final RandomAccess<I> transRA;
	final RandomAccess<FloatType> intensityRA;
	final RandomAccess<FloatType> initialParamRA;
	final RandomAccess<FloatType> fittedParamRA;
	final RandomAccess<FloatType> fittedRA;
	final RandomAccess<FloatType> residualsRA;
	final RandomAccess<FloatType> chisqRA;
	final RandomAccess<IntType> retcodeRA;
	final int lifetimeAxis;

	public RAHelper(FitParams<I> params, FitResults rslts) {
		this.lifetimeAxis = params.ltAxis;
		transRA = params.transMap.randomAccess();
		intensityRA = rslts.intensityMap.randomAccess();
		initialParamRA = params.paramMap != null && params.paramMap.dimension(lifetimeAxis) >= params.nComp ?
		                                           params.paramMap.randomAccess()    : null;
		fittedParamRA =  params.getParamMap ?      rslts.paramMap.randomAccess()     : null;
		fittedRA =       params.getFittedMap ?     rslts.fittedMap.randomAccess()    : null;
		residualsRA =    params.getResidualsMap ?  rslts.residualsMap.randomAccess() : null;
		chisqRA =        params.getChisqMap ?      rslts.chisqMap.randomAccess()     : null;
		retcodeRA =      params.getReturnCodeMap ? rslts.retCodeMap.randomAccess()   : null;
	}

	public boolean loadData(float[] transBuffer, float[] paramBuffer, FitParams<I> params, int[] xytPos) {
		// intensity thresholding
		intensityRA.setPosition(xytPos);
		intensityRA.setPosition(0, lifetimeAxis);
		if (intensityRA.get().getRealFloat() < params.iThresh) {
			return false;
		}

		// load transient
		transRA.setPosition(xytPos);
		transRA.setPosition(params.fitStart, lifetimeAxis);
		for (int t = 0; t < transBuffer.length; t++, transRA.fwd(lifetimeAxis)) {
			transBuffer[t] = transRA.get().getRealFloat();
		}

		// fill initial values from params.paramMap
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
		// or if both local and global settings are not present, set to +Inf "no estimation"
		else {
			for (int p = 0; p < paramBuffer.length; p++) {
				paramBuffer[p] = Float.POSITIVE_INFINITY;
			}
		}
		return true;
	}
	
	public void commitRslts(FitParams<I> params, FitResults rslts, int[] xytPos) {
		// don't write to results if chisq is too big
		if (params.dropBad) {
			float chisq = rslts.chisq;
			if (chisq < 0 || chisq > 1E5 || Float.isNaN(chisq)) {
				return;
			}
		}
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
		xytPos[lifetimeAxis] = 0;
		ra.setPosition(xytPos);
		for (float f : arr) {
			ra.get().set(f);
			ra.fwd(lifetimeAxis);
		}
	}
}
