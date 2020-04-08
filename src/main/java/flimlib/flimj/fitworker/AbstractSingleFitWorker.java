package flimlib.flimj.fitworker;

import java.util.List;

import net.imagej.ops.OpEnvironment;
import net.imagej.ops.thread.chunker.ChunkerOp;
import net.imagej.ops.thread.chunker.CursorBasedChunk;
import flimlib.flimj.FitParams;
import flimlib.flimj.FitResults;
import flimlib.flimj.utils.RAHelper;
import net.imglib2.type.numeric.RealType;

public abstract class AbstractSingleFitWorker<I extends RealType<I>> extends AbstractFitWorker<I> {

	/** Data buffers, all except for {@code transBuffer} are writable */
	protected final float[] paramBuffer, transBuffer, chisqBuffer, fittedBuffer, residualBuffer;

	public AbstractSingleFitWorker(FitParams<I> params, FitResults results, OpEnvironment ops) {
		super(params, results, ops);

		// setup input buffers
		if (results.param == null || results.param.length != nParam) {
			results.param = new float[nParam];
		}
		if (params.trans == null || params.trans.length != nDataTotal) {
			params.trans = new float[nDataTotal];
		}
		paramBuffer = results.param;
		transBuffer = params.trans;

		// setup output buffers
		chisqBuffer = new float[1];
		if (results.fitted == null || results.fitted.length != nDataTotal) {
			results.fitted = new float[nDataTotal];
		}
		if (results.residuals == null || results.residuals.length != nDataTotal) {
			results.residuals = new float[nDataTotal];
		}
		fittedBuffer = results.fitted;
		residualBuffer = results.residuals;
	}

	/**
	 * A routine called before {@link #doFit()}. Can be used to throw away the left-overs from the
	 * previous run.
	 */
	protected void beforeFit() {
		chisqBuffer[0] = -1;
	}

	/**
	 * Does the actual implementation-specific fitting routine.
	 */
	protected abstract void doFit();

	/**
	 * A routine called after {@link #doFit()}. Can be used to copy back results from buffers.
	 */
	protected void afterFit() {
		// reduced by degree of freedom
		results.chisq = chisqBuffer[0] / (nData - nParam);
	}

	/**
	 * Fit the data in the buffer.
	 */
	protected void fitSingle() {
		beforeFit();

		doFit();

		afterFit();
	}

	/**
	 * Make a worker of the same kind but does not share any writable buffers (thread safe) if that
	 * buffer is null.
	 * 
	 * @param params the parameters
	 * @param rslts  the results
	 * @return a worker of the same kind.
	 */
	protected abstract AbstractSingleFitWorker<I> duplicate(FitParams<I> params, FitResults rslts);

	@Override
	public void fitBatch(List<int[]> pos) {
		final AbstractSingleFitWorker<I> thisWorker = this;
		ops.run(ChunkerOp.class, new CursorBasedChunk() {

			@Override
			public void execute(long startIndex, long stepSize, long numSteps) {
				if (!params.multithread) {
					// let the first fitting thread do all the work
					if (startIndex != 0) {
						return;
					}
					numSteps = pos.size();
				}

				// thread-local reusable read/write buffers
				final FitParams<I> lParams;
				final FitResults lResults;
				final AbstractSingleFitWorker<I> fitWorker;
				// don't make copy in single thread mode
				if (!params.multithread || pos.size() == 1) {
					lParams = params;
					lResults = results;
					fitWorker = thisWorker;
				} else {
					lParams = params.copy();
					lResults = results.copy();
					// grab your own buffer
					lParams.param = lParams.trans =
							lResults.param = lResults.fitted = lResults.residuals = null;
					fitWorker = duplicate(lParams, lResults);
				}
				final RAHelper<I> helper = new RAHelper<>(params, results);

				for (int i = (int) startIndex; i < startIndex + numSteps; i += stepSize) {
					final int[] xytPos = pos.get(i);

					if (!helper.loadData(fitWorker.transBuffer, fitWorker.paramBuffer, params,
							xytPos)) {
						continue;
					}

					fitWorker.fitSingle();


					helper.commitRslts(lParams, lResults, xytPos);
				}
			}

		}, pos.size());
	}
}
