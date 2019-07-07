package net.imagej.slim;

import java.util.Arrays;
import java.util.List;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * ParamEstimator
 * 
 * @param <I> The type of transient data
 */
public class ParamEstimator<I extends RealType<I>> {

	private static final float SAMPLE_RATE = 0.05f;
	/** (fitEnd - fitStart) / (nData - fitStart) */
	private static final float END_PERSENTAGE = 0.9f;

	private final FitParams<I> params;

	private final Img<FloatType> iMap;

	private final List<int[]> pos;
	private final int lifetimeAxis;

	private final int nData, nTrans;

	private final float[] sumAcrossTrans;
	private final float[] iSmpls;

	public ParamEstimator(FitParams<I> params, List<int[]> pos) {
		this.params = params;
		this.pos = pos;
		this.lifetimeAxis = params.ltAxis;
		nData = (int) params.transMap.dimension(lifetimeAxis);
		nTrans = pos.size();
		// don't bother sampling if nothing to estimate
		// if only percentage is set, calculate the value
		iSmpls = params.iThresh < 0 && params.iThreshPercent > 0 ? new float[(int) (nTrans * SAMPLE_RATE)] : null;
		
		// create intensity image
		sumAcrossTrans = new float[nData];
		
		iMap = calcIMap();
	}

	public void estimateStartEnd() {
		// don't touch if not required
		if (params.fitStart < 0) {
			int max_idx = 0;
			for (int t = 0; t < sumAcrossTrans.length; t++) {
				max_idx = sumAcrossTrans[t] > sumAcrossTrans[max_idx] ? t : max_idx;
			}
			params.fitStart = max_idx;
		}
		if (params.fitEnd < 0 || params.fitEnd <= params.fitStart) {
			params.fitEnd = (int) (params.fitStart + (nData - params.fitStart) * END_PERSENTAGE);
		}
	}

	public void estimateIThreshold() {
		if (iSmpls != null) {
			Arrays.sort(iSmpls);
			params.iThreshPercent = Math.min(params.iThreshPercent, 100);
			params.iThresh = iSmpls[(int) (params.iThreshPercent / 100.0 * (iSmpls.length - 1))];
		}
	}

	public Img<FloatType> getIntensityMap() {
		return iMap;
	}

	private Img<FloatType> calcIMap() {
		// the intensity image has the same dim as say chisqMap
		long[] dimFit = new long[params.transMap.numDimensions()];
		params.transMap.dimensions(dimFit);
		dimFit[lifetimeAxis] = 1;
		Img<FloatType> iMap = ArrayImgs.floats(dimFit);

		// calculate the intensity of each interested trans
		RandomAccess<I> transRA = params.transMap.randomAccess();
		RandomAccess<FloatType> iMapRA = iMap.randomAccess();
		int iSmplCnt = 0;
		for (int i = 0; i < pos.size(); i++) {
			int[] xytPos = pos.get(i);
			transRA.setPosition(xytPos);
			float intensity = 0;
			for (int t = 0; t < nData; t++, transRA.fwd(lifetimeAxis)) {
				float count = transRA.get().getRealFloat();
				intensity += count;
				sumAcrossTrans[t] += count;
			}
			// sample intensity every other nTrans * SAMPLE_RATE
			if (iSmpls != null && iSmplCnt + 1 <= i * SAMPLE_RATE) {
				iSmpls[iSmplCnt++] = intensity;
			}
			iMapRA.setPosition(xytPos);
			iMapRA.get().set(intensity);
		}
		return iMap;
	}
}