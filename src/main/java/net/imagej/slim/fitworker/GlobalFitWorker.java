package net.imagej.slim.fitworker;

import net.imagej.slim.utils.FitParams;
import net.imagej.slim.utils.FitResults;
import net.imglib2.type.numeric.RealType;
import slim.FitType;
import slim.Float2DMatrix;
import slim.SLIMCurve;

public class GlobalFitWorker<I extends RealType<I>> implements FitWorker<I> {

	@Override
	public void fitSingle(float[] trans, FitParams params, FitResults results) {
		throw new UnsupportedOperationException("Image fitting worker is not applicable for single-pixel fitting.");
	}

	@Override
	public void fitGlobal(float[][] trans, FitParams params, FitResults[] results) {
		int nTrans = trans.length;
		int nData = trans[0].length;
		// each row is a transient series
		Float2DMatrix transMat = new Float2DMatrix(trans);
		// each row is a parameter series
		Float2DMatrix paramMat = new Float2DMatrix(nTrans, 3);
		// only the first row is used
		Float2DMatrix fittedMat = new Float2DMatrix(1, nData);
		Float2DMatrix residualMat = new Float2DMatrix(1, nData);
		// $\chi^2$ for each trans
		float[] chisq = new float[nTrans];
		// global $\chi^2$
		float[] chisqGlobal = new float[1];
		// degrees of freedom (used to reduce $\chi^2$)
		int[] df = new int[1];

		SLIMCurve.GCI_marquardt_global_exps_instr(params.xInc, transMat, 0, nData,
			params.instr, params.noise, params.sig, FitType.FIT_GLOBAL_MULTIEXP,
			paramMat, params.paramFree, params.restrain, params.chisq_delta,
			fittedMat, residualMat, chisq, chisqGlobal, df, 1);
		
		float[][] paramArray = paramMat.asArray();

		for (int i = 0; i < nTrans; i++) {
			results[i] = new FitResults();
			results[i].chisq = chisq[i];
			results[i].param = paramArray[i];
		}
	}

	@Override
	public int nParamOut() {
		return 3;
	}
}
