package net.imagej.slim;

import net.imagej.ops.Op;
import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imagej.slim.utils.FitParams;
import net.imagej.slim.utils.FitResults;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public class SlimOps {

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

	public interface PhasorOp extends Op {
		String NAME = "slim.fitPhasor";
	}

	// for grouping ops on the same data type
	interface FitII<I extends RealType<I>>
		extends UnaryHybridCF<IterableInterval<I>, FitResults> {

		void setParams(FitParams params);
		int getOutputSize();
	}

	interface FitRAI<I extends RealType<I>>
		extends UnaryHybridCF<RandomAccessibleInterval<I>, RandomAccessibleInterval<FloatType>> {
	
		void setParams(FitParams params);
	}
}
