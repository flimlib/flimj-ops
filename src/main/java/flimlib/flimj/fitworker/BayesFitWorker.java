package flimlib.flimj.fitworker;

import java.util.Arrays;
import net.imagej.ops.OpEnvironment;
import flimlib.FLIMLib;
import flimlib.flimj.FitParams;
import flimlib.flimj.FitResults;
import net.imglib2.type.numeric.RealType;

public class BayesFitWorker<I extends RealType<I>> extends AbstractSingleFitWorker<I> {

	// Bayes's own buffers
	private final float[] error, minusLogProb;
	private final int[] nPhotons;

	private float laserPeriod;

	public BayesFitWorker(FitParams<I> params, FitResults results, OpEnvironment ops) {
		super(params, results, ops);
		error = new float[nParam];
		minusLogProb = new float[1];
		nPhotons = new int[1];
	}

	@Override
	protected void beforeFit() {
		super.beforeFit();
		// TODO
		laserPeriod = params.xInc * (adjFitEnd - adjFitStart);
	}

	/**
	 * Performs an Bayes fit.
	 */
	@Override
	public void doFit() {
		// FLIMLib.Bayes_fitting_engine(params.xInc, transBuffer, adjFitStart, adjFitEnd,
		// 		laserPeriod, params.instr, paramBuffer, params.paramFree, fittedBuffer,
		// 		residualBuffer, error, minusLogProb, nPhotons, chisqBuffer);
	}

	@Override
	protected AbstractSingleFitWorker<I> duplicate(FitParams<I> params, FitResults rslts) {
		return new BayesFitWorker<>(params, rslts, ops);
	}
}
