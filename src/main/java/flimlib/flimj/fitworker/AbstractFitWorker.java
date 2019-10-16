package flimlib.flimj.fitworker;

import java.util.Arrays;

import net.imagej.ops.OpEnvironment;
import flimlib.flimj.FitParams;
import flimlib.flimj.FitResults;
import net.imglib2.type.numeric.RealType;

/**
 * AbstractFitWorker
 */
public abstract class AbstractFitWorker <I extends RealType<I>> implements FitWorker<I> {

	/** The {@link OpEnvironment} the worker may use */
	protected final OpEnvironment ops;

	/** The fit parameters for this worker */
	protected final FitParams<I> params;

	/** The fit results for this worker */
	protected final FitResults results;

	/** Should be self-explanatory */
	protected final int nData, nParam;

	/** The adjusted {@link FitParams#fitStart} and {@link FitParams#fitEnd} taking into account leading instr
	 * prefix (see below)
	 */
	protected final int adjFitStart, adjFitEnd;

	/** The number of data copied (including instr prefix/suffix and the part to fit, see below) */
	protected final int nDataTotal;

	public AbstractFitWorker(FitParams<I> params, FitResults results, OpEnvironment ops) {
		this.params = params;
		this.results = results;
		this.ops = ops;

		nData = nDataOut();
		nParam = nParamOut();

		// we want to copy a little bit more around the interval to correctly conv with instr
		int instrLen = params.instr == null ? 0 : params.instr.length;
		int prefixLen = Math.min(instrLen, params.fitStart);
		int suffixLen = Math.min(instrLen, (int) params.transMap.dimension(params.ltAxis) - params.fitEnd);
		nDataTotal = prefixLen + nData + suffixLen;

		// adjust fitStart and fitEnd by the length of instr prefix
		adjFitStart = prefixLen;
		adjFitEnd = adjFitStart + nData;

		// assume params are free if not specified
		int fillStart;
		if (params.paramFree == null) {
			params.paramFree = new boolean[nParamOut()];
			fillStart = 0;
		}
		else if (params.paramFree.length < nParamOut()) {
			params.paramFree = Arrays.copyOf(params.paramFree, nParamOut());
			fillStart = params.paramFree.length;
		}
		else {
			fillStart = params.paramFree.length;
		}
		for (int i = fillStart; i < params.paramFree.length; i++) {
			params.paramFree[i] = true;
		}
		
		// the target is compared with raw chisq, so multiply by dof first
		params.chisq_target *= nData - nParam;
		
		results.ltAxis = params.ltAxis;
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
