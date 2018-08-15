package net.imagej.slim;

import org.scijava.plugin.Plugin;

import net.imagej.ops.AbstractNamespace;
import net.imagej.ops.Namespace;
import net.imagej.ops.OpMethod;
import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imagej.slim.utils.FitParams;
import net.imagej.slim.utils.FitResults;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

@Plugin(type = Namespace.class)
public class SlimFit extends AbstractNamespace {

	private static final String NS_NAME = "slimFitter";
	@Override
	public String getName() {
		return NS_NAME;
	}

	public <I extends RealType<I>> RandomAccessibleInterval<FloatType> rldFit() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@OpMethod(op = DefaultFitRAI.MLAFitRAI.class)
	public <I extends RealType<I>> RandomAccessibleInterval<FloatType> mlaFit(RandomAccessibleInterval<I> in, FitParams param, int lifetimeAxis) {
		return (RandomAccessibleInterval<FloatType>) ops().run(DefaultFitRAI.MLAFitRAI.class, null, in, param, lifetimeAxis, null, null, null);
	}

	public interface FitII<I extends RealType<I>>
		extends UnaryHybridCF<IterableInterval<I>, FitResults> {
		final String NAME = NS_NAME + ".fitII";

		void setParams(FitParams params);
	}

	public interface FitRAI<I extends RealType<I>>
		extends UnaryHybridCF<RandomAccessibleInterval<I>, RandomAccessibleInterval<FloatType>> {
		final String NAME = NS_NAME + ".fitRAI";

		void setParams(FitParams params);
	}
}
