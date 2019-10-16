package flimlib.flimj.fitworker;

import java.util.List;

import net.imagej.ops.OpEnvironment;
import flimlib.flimj.FitParams;
import flimlib.flimj.FitResults;
import flimlib.flimj.utils.RAHelper;
import net.imglib2.type.numeric.RealType;
import flimlib.FitType;
import flimlib.Float2DMatrix;
import flimlib.FLIMLib;

public class GlobalFitWorker<I extends RealType<I>> extends AbstractFitWorker<I> {

	public GlobalFitWorker(FitParams<I> params, FitResults results, OpEnvironment ops) {
		super(params, results, ops);
	}

	@Override
	public void fitBatch(List<int[]> pos) {
		int nTrans = pos.size();

		// trans data and fitted parameters for each trans
		final float[][] trans = new float[nTrans][nDataTotal];
		final float[][] param = new float[nTrans][nParam];

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
		Float2DMatrix fittedMat = new Float2DMatrix(1, nDataTotal);
		Float2DMatrix residualMat = new Float2DMatrix(1, nDataTotal);
		// $\chi^2$ for each trans
		float[] chisq = new float[nTrans];
		// global $\chi^2$
		float[] chisqGlobal = new float[1];
		// degrees of freedom (used to reduce $\chi^2$)
		int[] df = new int[1];

		FLIMLib.GCI_marquardt_global_exps_instr(params.xInc, transMat, adjFitStart, adjFitEnd,
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
}
