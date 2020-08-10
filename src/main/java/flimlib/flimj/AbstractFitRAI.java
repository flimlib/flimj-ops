package flimlib.flimj;

import java.util.ArrayList;
import java.util.List;

import org.scijava.plugin.Parameter;

import net.imagej.ops.Contingent;
import flimlib.flimj.FlimOps.FitRAI;
import flimlib.flimj.fitworker.FitWorker;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.roi.Masks;
import net.imglib2.roi.RealMask;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public abstract class AbstractFitRAI<I extends RealType<I>> extends FitRAI<I>
		implements Contingent {

	@Parameter(required = false)
	private RandomAccessibleInterval<I> kernel;

	@Parameter(required = false)
	private RealMask roi;

	private FitWorker<I> fitWorker;

	private int lifetimeAxis;

	private ParamEstimator<I> est;

	private List<int[]> roiPos;

	private FitParams<I> params;

	private FitResults rslts;

	@Override
	public boolean conforms() {
		FitParams<I> in = in();
		// requires a 3D image
		if (in.transMap == null || in.transMap.numDimensions() != 3) {
			return false;
		}
		// lifetime axis must be valid
		if (lifetimeAxis < 0 || lifetimeAxis >= in.transMap.numDimensions()) {
			return false;
		}

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
		lifetimeAxis = in().ltAxis;

		// dimension doesn't really matter
		if (roi == null) {
			roi = Masks.allRealMask(0);
		}

		// So that we bin the correct axis
		if (kernel != null) {
			kernel = Views.permute(kernel, 2, lifetimeAxis);
		}

		params = in().copy();
		initParam();
		rslts = new FitResults();
		fitWorker = createWorker(params, rslts);
		initRslt();
	}

	@Override
	public FitResults calculate(FitParams<I> params) {
		fitWorker.fitBatch(roiPos);
		return rslts;
	}

	/**
	 * Generates a worker for the actual fit.
	 * 
	 * @return A {@link FitWorker}.
	 */
	public abstract FitWorker<I> createWorker(FitParams<I> params, FitResults results);

	private void initParam() {
		// convolve the image if necessary
		params.transMap = kernel == null ? params.transMap
				: ops().filter().<I, I, I, I>convolve(params.transMap, kernel);

		roiPos = getRoiPositions(params.transMap);

		est = new ParamEstimator<I>(params, roiPos);
		est.estimateStartEnd();
		est.estimateIThreshold();
	}

	private void initRslt() {
		// get dimensions and replace time axis with decay parameters
		long[] dimFit = new long[params.transMap.numDimensions()];
		params.transMap.dimensions(dimFit);
		if (params.getParamMap) {
			dimFit[lifetimeAxis] = fitWorker.nParamOut();
			rslts.paramMap = ArrayImgs.floats(dimFit);
		}
		if (params.getFittedMap) {
			dimFit[lifetimeAxis] = fitWorker.nDataOut();
			rslts.fittedMap = ArrayImgs.floats(dimFit);
		}
		if (params.getResidualsMap) {
			dimFit[lifetimeAxis] = fitWorker.nDataOut();
			rslts.residualsMap = ArrayImgs.floats(dimFit);
		}
		if (params.getReturnCodeMap) {
			dimFit[lifetimeAxis] = 1;
			rslts.retCodeMap = ArrayImgs.ints(dimFit);
		}
		if (params.getChisqMap) {
			dimFit[lifetimeAxis] = 1;
			rslts.chisqMap = ArrayImgs.floats(dimFit);
		}
		rslts.ltAxis = lifetimeAxis;

		rslts.intensityMap = est.getIntensityMap();
	}

	private List<int[]> getRoiPositions(RandomAccessibleInterval<I> trans) {
		final List<int[]> interested = new ArrayList<>();
		final IntervalView<I> xyPlane = Views.hyperSlice(trans, lifetimeAxis, 0);
		final Cursor<I> xyCursor = xyPlane.localizingCursor();

		// work to do
		while (xyCursor.hasNext()) {
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
		return interested;
	}
}
