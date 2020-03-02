package flimlib.flimj.fitworker;

import java.util.Arrays;

import net.imagej.ops.OpEnvironment;
import flimlib.FLIMLib;
import flimlib.RestrainType;
import flimlib.flimj.FitParams;
import flimlib.flimj.FitResults;
import net.imglib2.type.numeric.RealType;

/**
 * AbstractFitWorker
 */
public abstract class AbstractFitWorker<I extends RealType<I>> implements FitWorker<I> {

	/** The {@link OpEnvironment} the worker may use */
	protected final OpEnvironment ops;

	/** The fit parameters for this worker */
	protected final FitParams<I> params;

	/** The fit results for this worker */
	protected final FitResults results;

	/** Should be self-explanatory */
	protected final int nData, nParam;

	/**
	 * The adjusted {@link FitParams#fitStart} and {@link FitParams#fitEnd} taking
	 * into account leading instr prefix (see below)
	 */
	protected int adjFitStart, adjFitEnd;

	/**
	 * The number of data copied (including instr prefix/suffix and the part to fit,
	 * see below)
	 */
	protected int nDataTotal;

	/** The raw chisq target (params.chisq is reduced by DOF) */
	protected float rawChisq_target;

	public AbstractFitWorker(FitParams<I> params, FitResults results, OpEnvironment ops) {
		this.params = params;
		this.results = results;
		this.ops = ops;

		nData = nDataOut();
		nParam = nParamOut();

		// assume params are free if not specified
		int fillStart;
		if (params.paramFree == null) {
			params.paramFree = new boolean[nParam];
			fillStart = 0;
		} else if (params.paramFree.length < nParam) {
			fillStart = params.paramFree.length;
			params.paramFree = Arrays.copyOf(params.paramFree, nParam);
		} else {
			fillStart = params.paramFree.length;
		}
		for (int i = fillStart; i < params.paramFree.length; i++) {
			params.paramFree[i] = true;
		}

		populate();
	}

	/**
	 * The settings passed into the fit worker is mutable. This method refreshes the
	 * fit worker by updating cached information and probably re-allocating buffers.
	 */
	public void populate() {
		// we want to copy a little bit more around the interval to correctly conv with
		// instr
		int instrLen = params.instr == null ? 0 : params.instr.length;
		int prefixLen = Math.min(instrLen, params.fitStart);
		int suffixLen = Math.min(instrLen, (int) params.transMap.dimension(params.ltAxis) - params.fitEnd);
		nDataTotal = prefixLen + nData + suffixLen;

		// adjust fitStart and fitEnd by the length of instr prefix
		adjFitStart = prefixLen;
		adjFitEnd = adjFitStart + nData;

		// the target is compared with raw chisq, so multiply by dof first
		rawChisq_target = params.chisq_target * (nData - nParam);

		results.ltAxis = params.ltAxis;

		if (params.restrain.equals(RestrainType.ECF_RESTRAIN_USER)
				&& (params.restraintMin != null || params.restraintMax != null)) {
			boolean[] restrain = new boolean[this.nParam];
			float[] rMinOrig = params.restraintMin;
			float[] rMaxOrig = params.restraintMax;
			float[] rMin = new float[this.nParam];
			float[] rMax = new float[this.nParam];

			for (int i = 0; i < restrain.length; i++) {
				// only restrain the parameter if at least one of the restraints are valid (finite
				// or inf)
				boolean restrainCurrent = false;
				if (rMinOrig != null && i < rMinOrig.length && !Float.isNaN(rMinOrig[i])) {
					rMin[i] = rMinOrig[i];
					restrainCurrent = true;
				} else
					rMin[i] = Float.NEGATIVE_INFINITY;

				if (rMaxOrig != null && i < rMaxOrig.length && !Float.isNaN(rMaxOrig[i])) {
					rMax[i] = rMaxOrig[i];
					restrainCurrent = true;
				} else
					rMax[i] = Float.POSITIVE_INFINITY;

				restrain[i] = restrainCurrent;
			}

			FLIMLib.GCI_set_restrain_limits(restrain, rMin, rMax);
		}
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
