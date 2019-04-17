package net.imagej.slim.fitworker;

import java.util.List;

import net.imagej.ops.OpEnvironment;
import net.imagej.ops.thread.chunker.ChunkerOp;
import net.imagej.ops.thread.chunker.CursorBasedChunk;
import net.imagej.slim.FitParams;
import net.imagej.slim.FitResults;
import net.imagej.slim.utils.RAHelper;
import net.imglib2.type.numeric.RealType;

public abstract class AbstractSingleFitWorker<I extends RealType<I>> implements FitWorker<I> {

	protected final OpEnvironment ops;

	protected final FitParams<I> params;
	protected final FitResults results;

	protected final int nData, nParam;

	protected final float[] paramBuffer;
	protected final float[] transBuffer;
	protected final float[] chisqBuffer;
	protected final float[] fittedBuffer;
	protected final float[] residualBuffer;

	protected static final int DEFAULT_NPARAMOUT = 3;

	public AbstractSingleFitWorker(FitParams<I> params, FitResults results, OpEnvironment ops) {
		this.params = params;
		this.results = results;
		this.ops = ops;

		// [start, end)
		nData = params.fitEnd - params.fitStart;
		nParam = nParamOut();

		// setup input buffers
		paramBuffer = results.param = new float[nParam];
		transBuffer = params.trans = new float[nData];

		// setup output buffers
		chisqBuffer = new float[1];
		fittedBuffer = results.fitted = new float[nData];
		residualBuffer = results.residuals = new float[nData];
	}

	/**
	 * How many parameters should there be in {@code results.param}?
	 * E.g. 3 for {@link MLAFitWorker} and 5 for
	 * {@link PhasorFitWorker}.
	 * @return The number of output parameters in the parameter array.
	 */
	public int nParamOut() {
		return DEFAULT_NPARAMOUT;
	}

	/**
	 * Does the actual implementation-specific fitting routine.
	 */
	protected abstract void doFit();

	/**
	 * A routine called before {@link #doFit()}. Can be used to copy back
	 * results from buffers.
	 */
	protected void postFit() {
		results.chisq = chisqBuffer[0];
	}

	public void fitSingle(FitParams<I> params, FitResults results) {
		doFit();

		postFit();
	}

	protected abstract AbstractSingleFitWorker<I> duplicate(FitParams<I> params, FitResults rslts);

	@Override
	public void fitBatch(FitParams<I> params, FitResults rslts, List<int[]> pos, int lifetimeAxis) {
		ops.run(ChunkerOp.class, new CursorBasedChunk() {

			@Override
			public void execute(int startIndex, int stepSize, int numSteps) {
				// thread-local reusable read/write buffers
				final FitParams<I> lParams = params.copy();
				final FitResults lResults = results.copy();
				final AbstractSingleFitWorker<I> fitWorker = duplicate(lParams, lResults);
				final RAHelper<I> helper = new RAHelper<>(params, rslts, lifetimeAxis);

				for (int i = startIndex; i < startIndex + numSteps; i += stepSize) {
					final int[] xytPos = pos.get(i);

					helper.loadData(fitWorker.transBuffer, fitWorker.paramBuffer, lParams, xytPos);

					fitWorker.fitSingle(lParams, lResults);

					helper.commitRslts(lParams, lResults, xytPos);
				}
			}

		}, pos.size());
	}
}
