package net.imagej.slim;

import java.util.ArrayList;
import java.util.List;

import org.scijava.plugin.Parameter;

import net.imagej.ops.Contingent;
import net.imagej.ops.thread.chunker.ChunkerOp;
import net.imagej.ops.thread.chunker.CursorBasedChunk;
import net.imagej.slim.SlimOps.FitRAI;
import net.imagej.slim.fitworker.FitWorker;
import net.imagej.slim.fitworker.GlobalFitWorker;
import net.imagej.slim.utils.FitParams;
import net.imagej.slim.utils.FitResults;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.roi.Masks;
import net.imglib2.roi.RealMask;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public abstract class AbstractFitRAI<I extends RealType<I>> extends FitRAI<I> implements Contingent {

	@Parameter
	FitParams params;

	@Parameter
	private int lifetimeAxis;

	@Parameter(required = false)
	private RealMask roi;

	@Parameter(required = false)
	private RandomAccessibleInterval<I> kernel;

	private FitWorker<I> fitWorker;

	@Override
	public void setParams(FitParams params) {
		this.params = params;
	}

	@Override
	public boolean conforms() {
		// requires a 3D image
		if (in().numDimensions() != 3)
			return false;

		// and pissibly a 2D mask
		if (roi != null && roi.numDimensions() != 2) {
			return false;
		}

		// and pissibly a 3D kernel
		if (kernel != null && kernel.numDimensions() != 3) {
			return false;
		}
		return true;
	}

	@Override
	public void initialize() {
		super.initialize();
		if (fitWorker == null) {
			fitWorker = createWorker();
		}

		// dimension doesn't really matter
		if (roi == null) {
			roi = Masks.allRealMask(0);
		}

		// So that we bin the correct axis
		if (kernel != null) {
			kernel = Views.permute(kernel, 2, lifetimeAxis);
		}
	}

	@Override
	public void compute(RandomAccessibleInterval<I> trans, RandomAccessibleInterval<FloatType> fitted) {
		final RandomAccessibleInterval<I> binnedTrans = kernel == null ? trans : (RandomAccessibleInterval<I>) ops().filter().convolve(trans, kernel);
		final List<int[]> interested = new ArrayList<>();
		final IntervalView<I> xyPlane = Views.hyperSlice(binnedTrans, lifetimeAxis, 0);
		final Cursor<I> xyCursor = xyPlane.localizingCursor();

		// work to do
		while(xyCursor.hasNext()) {
		// while(xyCursor.hasNext() && interested.size() < 12) {
			xyCursor.fwd();
			if (roi.test(xyCursor)) {
				int[] pos = new int[3];
				xyCursor.localize(pos);
				// swap in lifetime axis
				for (int i = 2; i > lifetimeAxis; i--) {
					int tmp = pos[i];
					pos[i] = pos[i - 1];
					pos[i - 1] = tmp;
				}
				pos[lifetimeAxis] = 0;
				interested.add(pos);
			}
		}

		if (fitWorker instanceof GlobalFitWorker) {
			int nTrans = interested.size();
			int nData = params.fitEnd - params.fitStart;
			
			// trans data and fitted parameters for each trans
			final float[][] transArr = new float[nTrans][nData];
			final FitResults[] results = new FitResults[nTrans];

			final RandomAccess<I> transRA = binnedTrans.randomAccess();
			final RandomAccess<FloatType> fittedRA = fitted.randomAccess();
			for (int i = 0; i < nTrans; i++) {
				transRA.setPosition(interested.get(i));
				transRA.setPosition(params.fitStart, lifetimeAxis);
				// fill transient buffer
				for (int t = 0; t < nData; t++, transRA.fwd(lifetimeAxis)) {
					transArr[i][t] = transRA.get().getRealFloat();
				}
			}

			fitWorker.fitGlobal(transArr, params, results);

			// copy back
			for (int i = 0; i < nTrans; i++) {
				final int[] xytPos = interested.get(i);
				fittedRA.setPosition(xytPos);
				for (float param : results[i].param) {
					fittedRA.get().set(param);
					fittedRA.fwd(lifetimeAxis);
				}
			}
			return;
		}
		
		ops().run(ChunkerOp.class, new CursorBasedChunk() {

			@Override
			public void execute(int startIndex, int stepSize, int numSteps) {
				// thread-local reusable read/write buffers
				final FitParams lParams = params.copy();
				// final FitParams lParams = params.paramRA == null ? params : params.copy();
				float[] transBuffer = new float[lParams.fitEnd - lParams.fitStart];
				final FitResults aSingleResult = new FitResults();
				final FitWorker<I> fitWorker = createWorker();
				
				final RandomAccess<I> transRA = binnedTrans.randomAccess();
				final RandomAccess<FloatType> paramRA = lParams.paramRA == null ? null : lParams.paramRA.randomAccess();
				final RandomAccess<FloatType> fittedRA = fitted.randomAccess();

				for (int i = startIndex; i < startIndex + numSteps; i += stepSize) {
					final int[] xytPos = interested.get(i);

					transRA.setPosition(xytPos);
					transRA.setPosition(lParams.fitStart, lifetimeAxis);
					// fill transient buffer
					for (int t = 0; t < lParams.fitEnd - lParams.fitStart; t++, transRA.fwd(lifetimeAxis)) {
						transBuffer[t] = transRA.get().getRealFloat();
					}
					// fill initial values
					if (paramRA != null) {
						paramRA.setPosition(xytPos);
						for (int p = 0; p < lParams.param.length; p++, paramRA.fwd(lifetimeAxis)) {
							lParams.param[p] = paramRA.get().getRealFloat();
						}
					}

					fitWorker.fitSingle(transBuffer, lParams, aSingleResult);

					// fill the resulting parameters
					fittedRA.setPosition(xytPos);
					for (float param : aSingleResult.param) {
						fittedRA.get().set(param);
						fittedRA.fwd(lifetimeAxis);
					}
				}
			}

		}, interested.size());
	}

	@Override
	public RandomAccessibleInterval<FloatType> createOutput(RandomAccessibleInterval<I> trans) {
		// get dimensions and replace time axis with decay parameters
		long[] dimFit = new long[trans.numDimensions()];
		trans.dimensions(dimFit);
		dimFit[lifetimeAxis] = fitWorker.nParamOut();
		return ArrayImgs.floats(dimFit);
	}

	/**
	 * Generates a worker for the actual fit.
	 * @return A {@link FitWorker}.
	 */
	public abstract FitWorker<I> createWorker();
}
