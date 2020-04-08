package flimlib.flimj;

import net.imagej.ops.Op;
import net.imagej.ops.special.function.AbstractUnaryFunctionOp;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import flimlib.flimj.FitParams;
import flimlib.flimj.FitResults;
import flimlib.flimj.fitworker.*;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

public class FlimOps {

	// grab & go kernels
	public static final Img<DoubleType> SQUARE_KERNEL_3 = makeSquareKernel(3);
	public static final Img<DoubleType> SQUARE_KERNEL_5 = makeSquareKernel(5);

	public static Img<DoubleType> makeSquareKernel(int size) {
		Img<DoubleType> out = ArrayImgs.doubles(new long[] {size, size, 1});
		Cursor<DoubleType> cursor = out.cursor();
		while (cursor.hasNext()) {
			cursor.fwd();
			cursor.get().set(1.0);
		}
		return out;
	}

	private FlimOps() {
		// NB: Prevent instantiation of utility class.
	}

	// the exposed interfaces
	public interface RLDOp extends Op {
		String NAME = "flim.fitRLD";
	}

	public interface LMAOp extends Op {
		String NAME = "flim.fitLMA";
	}

	public interface BayesOp extends Op {
		String NAME = "flim.fitBayes";
	}

	public interface GlobalOp extends Op {
		String NAME = "flim.fitGlobal";
	}

	public interface PhasorOp extends Op {
		String NAME = "flim.fitPhasor";
	}

	// TODO
	// public interface SPAOp extends Op {
	// 	String NAME = "flim.spa";
	// }

	public interface CalcTauMOp extends Op {
		String NAME = "flim.calcTauMean";
	}

	public interface CalcAPercentOp extends Op {
		String NAME = "flim.calcAPercent";
	}

	public interface PseudocolorOp extends Op {
		String NAME = "flim.showPseudocolor";
	}

	interface FitOps<I extends RealType<I>> {

		/**
		 * Generates a worker for the actual fit.
		 * 
		 * @param params  The {@link FitParams} associated with this worker.
		 * @param results The {@link FitResults} associated with this worker.
		 * @return A {@link FitWorker}.
		 */
		FitWorker<I> createWorker(FitParams<I> params, FitResults results);
	}

	// for grouping ops on the same data type
	static abstract class FitRAI<I extends RealType<I>>
			extends AbstractUnaryFunctionOp<FitParams<I>, FitResults> implements FitOps<I> {

	}

	static abstract class DispRslt extends AbstractUnaryFunctionOp<FitResults, Img<ARGBType>> {

	}

	static abstract class Calc extends AbstractUnaryFunctionOp<FitResults, Img<FloatType>> {

	}
}
