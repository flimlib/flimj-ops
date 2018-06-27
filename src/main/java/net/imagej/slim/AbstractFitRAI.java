package net.imagej.slim;

import java.util.Arrays;

import org.scijava.plugin.Parameter;

import net.imagej.ops.Contingent;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imagej.slim.SlimFit.FitII;
import net.imagej.slim.SlimFit.FitRAI;
import net.imagej.slim.utils.FitParams;
import net.imagej.slim.utils.FitResults;
import net.imagej.slim.utils.FitWorker;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.algorithm.neighborhood.Shape;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.roi.Masks;
import net.imglib2.roi.RealMask;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public abstract class AbstractFitRAI<I extends RealType<I>>
		extends AbstractUnaryHybridCF<RandomAccessibleInterval<I>, RandomAccessibleInterval<FloatType>> implements FitRAI<I>, Contingent {

	@Parameter
	FitParams params;

	@Parameter
	private int timeAxis;

	@Parameter(required=false)
	private RealMask roi;

	@Parameter(required=false)
	private Shape binningKnl;

	@Parameter(required=false)
	private int[] binningAxes;

	private FitII<I> fitII;

	public void setParams(FitParams params) {
		this.params = params;
	}

	@Override
	public boolean conforms() {
		if (binningKnl == null ^ binningAxes == null)
			return false;
		return true;
	}

	@Override
	public void initialize() {
		super.initialize();
		fitII = createFitII();
		fitII.setParams(params);

		if (binningAxes == null)
			binningAxes = new int[0];
		// dimension doesn't really matter
		if (roi == null)
			roi = Masks.allRealMask(0);
	}

	@Override
	public void compute(RandomAccessibleInterval<I> trans, RandomAccessibleInterval<FloatType> fitted) {
		// reusable buffer for each pixel's fitting results
		FitResults aSingleResult = new FitResults();

		// prepare subspace dimensions
		int nD = trans.numDimensions();
		int[] temporalAxes = new int[] { timeAxis };
		int[] nontemporalAxes = new int[nD - 1];
		int[] nonbinningAxes = new int[nD - 1 - binningAxes.length];
		Arrays.sort(binningAxes);
		for (int i = 0, j = 0, k = 0; i < nD; i++) {
			if (i != timeAxis)
				nontemporalAxes[j + k] = i;
			else
				continue;
			if (j < binningAxes.length && i == binningAxes[j])
				j++;
			else {
				nonbinningAxes[k] = i;
				k++;
			}
		}
		// chop coordinate into 3 parts: 1 time + m binning + n non-binning
		FinalInterval iTSpace = subspaceBounds(trans, temporalAxes);
		FinalInterval iNTSpace = subspaceBounds(trans, nontemporalAxes);
//		FinalInterval iBSpace = subspaceBounds(trans, binningAxes);
//		FinalInterval iNBSpace = subspaceBounds(trans, nonbinningAxes);
		// remove the gap at timeAxis in time-free space
		for (int i = 0; i < nonbinningAxes.length; i++)
			if (nonbinningAxes[i] > timeAxis)
				nonbinningAxes[i]--;

		// each point in this space is a series of transient data
		IntervalView<RandomAccessible<I>> nontemporalR = Views.interval(Views.hyperSlices(Views.extendZero(trans), temporalAxes), iNTSpace);
		Cursor<RandomAccessible<I>> ntRCsr = nontemporalR.cursor();
		// or a series of fitted params
		IntervalView<RandomAccessible<FloatType>> nontemporalW = Views.interval(Views.hyperSlices(fitted, temporalAxes), iNTSpace);
		Cursor<RandomAccessible<FloatType>> ntWCsr = nontemporalW.localizingCursor();

		Cursor<RandomAccessible<FloatType>> paramRCsr = null;
		if (params.paramRA != null) {
			IntervalView<RandomAccessible<FloatType>> paramR = Views.interval(Views.hyperSlices(params.paramRA, temporalAxes), iNTSpace);
			paramRCsr = paramR.localizingCursor();
		}

		// binning
		// RA of neighborhoods in nontemporal space
		RandomAccess<Neighborhood<RandomAccessible<I>>> ntNbhdsRA = null;
		// neighborhood center/neighborhood member coordinates
		long[] cntrCoord = null, nbhdCoord = null;
		// reusable trans buffer and its cursor
		IterableInterval<I> binnedTrans = null;
		Cursor<I> binnedTransCsr = null;
		if (binningKnl != null && binningAxes != null) {
			ntNbhdsRA = binningKnl.neighborhoodsRandomAccessible(nontemporalR).randomAccess();
			cntrCoord = new long[ntRCsr.numDimensions()];
			nbhdCoord = new long[ntRCsr.numDimensions()];
			binnedTrans = ops().copy().iterableInterval(Views.interval(ntRCsr.get(), iTSpace));
			binnedTransCsr = binnedTrans.cursor();
		}

		while (ntRCsr.hasNext()) {
			ntRCsr.fwd();
			ntWCsr.fwd();
			if (paramRCsr != null)
				paramRCsr.fwd();
			// not interested
			if (roi != null && !roi.test(ntRCsr))
				continue;

			// binning
			if (ntNbhdsRA != null) {
				// fetch the transient in question
				RandomAccess<I> transRA = ntRCsr.get().randomAccess();
				while (binnedTransCsr.hasNext()) {
					transRA.setPosition(binnedTransCsr);
					binnedTransCsr.next().set(transRA.get());
				}
				binnedTransCsr.reset();
//				ntCsr.localize(cntrCoord);
//				System.out.println(Arrays.toString(cntrCoord));
				// move neighborhood center to the trans in question
				ntNbhdsRA.setPosition(ntRCsr);
				ntRCsr.localize(cntrCoord);
//				System.out.println(Arrays.toString(cntrCoord));

				Cursor<RandomAccessible<I>> ntNbhdCsr = ntNbhdsRA.get().localizingCursor();
				while (ntNbhdCsr.hasNext()) {
					ntNbhdCsr.fwd();
					ntNbhdCsr.localize(nbhdCoord);
					// A trans in the same binning-space nbhd should have the same non-binning axis coordinates
					boolean inNbhd = true;
					for (int i = 0; i < nonbinningAxes.length; i++) {
						if (nbhdCoord[nonbinningAxes[i]] != cntrCoord[nonbinningAxes[i]]) {
							inNbhd = false;
							break;
						}
					}
					if (!inNbhd)
						continue;
					// System.out.print(Arrays.toString(nbhdCoord) + " ");

					// same nbhd, add to trans
					RandomAccess<I> nbhdTransRA = ntNbhdCsr.get().randomAccess();
					while (binnedTransCsr.hasNext()) {
						nbhdTransRA.setPosition(binnedTransCsr);
						binnedTransCsr.next().add(nbhdTransRA.get());
					}
					binnedTransCsr.reset();
//					for (I i: binnedTrans) {
//						System.out.print(i + " ");
//					}
//					System.out.println();
				}
			}
			else
				binnedTrans = Views.interval(ntRCsr.get(), iTSpace);

			if (paramRCsr != null) {
				RandomAccess<FloatType> paramRA = paramRCsr.get().randomAccess();
				for (int i = 0; i < params.param.length; i++) {
					params.param[i] = paramRA.get().getRealFloat();
					paramRA.fwd(0);
				}
			}

			fitII.compute(binnedTrans, aSingleResult);

			RandomAccess<FloatType> rsltRA = ntWCsr.get().randomAccess();
			for (float param : aSingleResult.param) {
				rsltRA.get().set(param);
				rsltRA.fwd(0);
			}

//			System.out.println(Arrays.toString(aSingleResult.param));
//			System.out.println(aSingleResult.chisq);
		}
	}

	@Override
	public RandomAccessibleInterval<FloatType> createOutput(RandomAccessibleInterval<I> trans) {
		// get dimensions and replace time axis with decay parameters
		long[] dimFit = new long[trans.numDimensions()];
		trans.dimensions(dimFit);
		dimFit[timeAxis] = params.param.length;
		return ArrayImgs.floats(dimFit);
	}

	/**
	 * Generates a II fitter for fitting each pixels.
	 * @return A {@link FitWorker}.
	 */
	protected abstract FitII<I> createFitII();

//	// populates "src" into "min" and "max" with "pos" value replaced with "pMin" and "pMax"
//	private static void populate(long[] src, long[] min, long[] max, long pMin, long pMax, int pos) {
//		for (int i = 0; i <= src.length; i++) {
//			if (i == pos) {
//				min[i] = pMin;
//				max[i] = pMax;
//			}
//			else
//				min[i] = max[i] = i < pos ? src[i] : src[i - 1];
//		}
//	}
//	private static void extractInnerOuter(long[] oriMin, long[] oriMax, int[] innerAxes, int[] outterAxes, long[] iMin, long[] iMax, long[] oMin, long[] oMax) {
//		Arrays.sort(innerAxes);
//		for (int i = 0, j = 0, k = 0; i < oriMin.length; i++)
//			if (j < innerAxes.length && i == innerAxes[j]) {
//				iMin[j] = oriMin[i];
//				iMax[j] = oriMax[i];
//				innerAxes[j] = i;
//				j++;
//			}
//			else {
//				oMin[k] = oriMin[i];
//				oMax[k] = oriMax[i];
//				outterAxes[k] = i;
//				k++;
//			}
//	}
//	private static void extractInnerOuter(int nD, int[] innerAxes, int[] outterAxes) {
//		Arrays.sort(innerAxes);
//		for (int i = 0, j = 0, k = 0; i < nD; i++)
//			if (j < innerAxes.length && i == innerAxes[j]) {
//				innerAxes[j] = i;
//				j++;
//			}
//			else {
//				outterAxes[k] = i;
//				k++;
//			}
//	}
	private static FinalInterval subspaceBounds(Interval in, int[] axes) {
		long[] min = new long[axes.length];
		long[] max = new long[axes.length];
		for (int i = 0; i < axes.length; i++){
			min[i] = in.min(axes[i]);
			max[i] = in.max(axes[i]);
		}
		return new FinalInterval(min, max);
	}
}
