package net.imagej.slim;

import java.util.Arrays;
import java.util.List;

import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.RealType;

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

	private final List<int[]> pos;
	private final int lifetimeAxis;

	private final int nData, nTrans;

	private float[] sumAcrossTrans;
	private float[] sumOverTime;

	public ParamEstimator(FitParams<I> params, List<int[]> pos) {
		this.params = params;
		this.pos = pos;
		this.lifetimeAxis = params.ltAxis;
		nData = (int) params.transMap.dimension(lifetimeAxis);
		nTrans = pos.size();
		// don't bother sampling if nothing to estimate
		if (params.fitStart < 0 || params.iThresh < 0) {
			getSample((int) (nTrans * SAMPLE_RATE));
		}
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
		if (params.iThresh < 0) {
			Arrays.sort(sumOverTime);
			// TODO
			// System.out.println(Arrays.toString(sumOverTime));
		}
	}

	private void getSample(int n) {
		final int nSample = nTrans < n ? nTrans : n;

		RandomAccess<I> transRA = params.transMap.randomAccess();

		sumAcrossTrans = new float[nData];
		sumOverTime = new float[nSample];

		// sample equally spaced transients
		for (int i = 0; i < nSample; i++) {
			transRA.setPosition(pos.get(i * nTrans / nSample));
			for (int t = 0; t < sumAcrossTrans.length; t++, transRA.fwd(lifetimeAxis)) {
				float intensity = transRA.get().getRealFloat();
				sumAcrossTrans[t] += intensity;
				sumOverTime[i] += intensity;
			}
		}
	}
}