package net.imagej.slim.utils;

import java.util.Arrays;

import net.imglib2.RandomAccessible;
import net.imglib2.type.numeric.real.FloatType;
import slim.*;

public class FitParams {
	public float xInc;
	public int fitStart;
	public int fitEnd;
	public float[] instr;
	public NoiseType noise = NoiseType.NOISE_GAUSSIAN_FIT;
	public float[] sig;
	public float[] param;
	public RandomAccessible<FloatType> paramRA;
	public boolean[] paramFree;
	public RestrainType restrain = RestrainType.ECF_RESTRAIN_DEFAULT;
	public FitFunc fitFunc = FitFunc.GCI_MULTIEXP_TAU;
	public float chisq_target = 1;
	public float chisq_delta = 0.0001f;
	public int chisq_percent = 95;

	public FitParams copy() {
		FitParams newParams = new FitParams();
		newParams.xInc = xInc;
		newParams.fitStart = fitStart;
		newParams.fitEnd = fitEnd;
		newParams.instr = instr;
		newParams.noise = noise;
		newParams.sig = sig;
		newParams.param = Arrays.copyOf(param, param.length);
		newParams.paramRA = paramRA;
		newParams.paramFree = paramFree;
		newParams.restrain = restrain;
		newParams.fitFunc = fitFunc;
		newParams.chisq_target = chisq_target;
		newParams.chisq_delta = chisq_delta;
		newParams.chisq_percent = chisq_percent;
		return newParams;
	}
}
