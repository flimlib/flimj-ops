package net.imagej.slim.fitworker;

import java.util.Arrays;
import java.util.List;

import net.imagej.ops.OpEnvironment;
import net.imagej.slim.FitParams;
import net.imagej.slim.FitResults;
import net.imagej.slim.utils.RAHelper;
import net.imglib2.type.numeric.RealType;
import slim.FitType;
import slim.Float2DMatrix;
import slim.SLIMCurve;

public class GlobalFitWorker<I extends RealType<I>> implements FitWorker<I> {

	private final FitParams<I> params;
	private final FitResults results;

	public GlobalFitWorker(FitParams<I> params, FitResults results, OpEnvironment ops) {
		this.params = params;
		this.results = results;
	}

	@Override
	public void fitBatch(List<int[]> pos) {
		int nTrans = pos.size();
		int nData = params.fitEnd - params.fitStart;

		// trans data and fitted parameters for each trans
		final float[][] trans = new float[nTrans][nData];
		final float[][] param = new float[nTrans][nParamOut()];
		// assume free if not specified
		int fillStart = 0;
		if (params.paramFree == null) {
			params.paramFree = new boolean[nParamOut()];
		}
		else if (params.paramFree.length < nParamOut()) {
			fillStart = params.paramFree.length;
			params.paramFree = Arrays.copyOf(params.paramFree, nParamOut());
		}
		for (int i = fillStart; i < params.paramFree.length; i++) {
			params.paramFree[i] = true;
		}
		final RAHelper<I> helper = new RAHelper<>(params, results);

		// fetch parameters from RA
		for (int i = 0; i < nTrans; i++) {
			helper.loadData(trans[i], param[i], params, pos.get(i));
		}

		// each row is a transient series
		Float2DMatrix transMat = new Float2DMatrix(trans);
		// each row is a parameter series
		Float2DMatrix paramMat = new Float2DMatrix(param);
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
			fittedMat, residualMat, chisq, chisqGlobal, df, params.dropBad ? 1 : 0);
		
		// fetch fitted stuff from native
		float[][] fittedParam  = params.getParamMap     ? paramMat.asArray()    : null;
		float[][] fitted       = params.getFittedMap    ? fittedMat.asArray()   : null;
		float[][] residual     = params.getResidualsMap ? residualMat.asArray() : null;

		// copy back
		for (int i = 0; i < nTrans; i++) {
			results.param     = params.getParamMap     ? fittedParam[i] : null;
			results.fitted    = params.getFittedMap    ? fitted[i]      : null;
			results.residuals = params.getResidualsMap ? residual[i]    : null;
			results.chisq = chisq[i];
			helper.commitRslts(params, results, pos.get(i));
		}
		results.chisq = chisqGlobal[0];
	}

	@Override
	public int nParamOut() {
		// Z, A_i, tau_i
		return params.nComp * 2 + 1;
	}

	@Override
	public int nDataOut() {
		return params.fitEnd - params.fitStart;
	}
}
