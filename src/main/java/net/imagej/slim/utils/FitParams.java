package net.imagej.slim.utils;

import net.imglib2.RandomAccessible;
import net.imglib2.type.numeric.real.FloatType;
import slim.*;

public class FitParams {
	public float xInc;
	public int fitStart;
	public int fitEnd;
	public float[] instr;
	public NoiseType noise;
	public float[] sig;
	public float[] param;
	public RandomAccessible<FloatType> paramRA;
	public boolean[] paramFree;
	public RestrainType restrain;
	public FitFunc fitFunc;
	public float chisq_target;
	public float chisq_delta;
	public int chisq_percent;
}
