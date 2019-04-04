package net.imagej.slim;

import net.imagej.ops.Op;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imagej.slim.utils.FitParams;
import net.imagej.slim.utils.FitResults;
import net.imagej.slim.fitworker.*;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

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

	interface FitOps<I extends RealType<I>> {

		void setParams(FitParams params);
		
		/**
		 * Generates a worker for the actual fit.
		 * @return A {@link FitWorker}.
		 */
		FitWorker<I>  createWorker();
	}
	
	// for grouping ops on the same data type
	static abstract class FitII<I extends RealType<I>>
		extends AbstractUnaryHybridCF<IterableInterval<I>, FitResults>  implements FitOps<I> {

		abstract int getOutputSize();
	}

	static abstract class FitRAI<I extends RealType<I>>
		extends AbstractUnaryHybridCF<RandomAccessibleInterval<I>, RandomAccessibleInterval<FloatType>> implements FitOps<I> {
	
	}
}
