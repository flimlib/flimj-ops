package net.imagej.slim;

import net.imagej.ops.Op;
import net.imagej.ops.special.function.AbstractUnaryFunctionOp;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imagej.slim.FitParams;
import net.imagej.slim.FitResults;
import net.imagej.slim.fitworker.*;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class SlimOps {

	// grab & go kernels
	public static final Img<DoubleType> SQUARE_KERNEL_3 = makeSquareKernel(3);
	public static final Img<DoubleType> SQUARE_KERNEL_5 = makeSquareKernel(5);

	private static Img<DoubleType> makeSquareKernel(int size) {
		Img<DoubleType> out = ArrayImgs.doubles(new long[] { size , size, 1 });
		Cursor<DoubleType> cursor = out.cursor();
		while (cursor.hasNext()) {
			cursor.fwd();
			cursor.get().set(1.0 / (size * size));
		}
		return out;
	}

	private SlimOps() {
		// NB: Prevent instantiation of utility class.
	}

	// the exposed interfaces
	public interface RLDOp extends Op {
		String NAME = "slim.fitRLD";
	}

	public interface MLAOp extends Op {
		String NAME = "slim.fitMLA";
	}

	public interface GlobalOp extends Op {
		String NAME = "slim.fitGlobal";
	}

	public interface PhasorOp extends Op {
		String NAME = "slim.fitPhasor";
	}

	// TODO
	// public interface SPAOp extends Op {
	// 	String NAME = "slim.spa";
	// }

	public interface PseudocolorOp extends Op {
		String NAME = "slim.showPseudocolor";
	}

	interface FitOps<I extends RealType<I>> {

		/**
		 * Generates a worker for the actual fit.
		 * 
		 * @param params The {@link FitParams} associated with this worker.
		 * @param results The {@link FitResults} associated with this worker.
		 * @return A {@link FitWorker}.
		 */
		FitWorker<I>  createWorker(FitParams<I> params, FitResults results);
	}
	
	// for grouping ops on the same data type
	static abstract class FitII<I extends RealType<I>>
		extends AbstractUnaryHybridCF<IterableInterval<I>, FitResults>  implements FitOps<I> {

		abstract int getOutputSize();
	}

	static abstract class FitRAI<I extends RealType<I>>
		extends AbstractUnaryFunctionOp<FitParams<I>, FitResults> implements FitOps<I> {
	
	}

	static abstract class DispRslt
		extends AbstractUnaryFunctionOp<FitResults, RandomAccessibleInterval<ARGBType>> {
	
	}
}
