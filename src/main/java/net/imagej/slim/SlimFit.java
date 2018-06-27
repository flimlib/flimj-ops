package net.imagej.slim;

import org.scijava.plugin.Plugin;

import net.imagej.ops.AbstractNamespace;
import net.imagej.ops.Namespace;
import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imagej.slim.utils.FitParams;
import net.imagej.slim.utils.FitResults;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

@Plugin(type = Namespace.class)
public class SlimFit extends AbstractNamespace {
	@Override
	public String getName() {
		return "SlimFit";
	}

	public interface FitII<I extends RealType<I>>
		extends UnaryHybridCF<IterableInterval<I>, FitResults> {
		String NAME = "FitII";

		void setParams(FitParams params);
	}

	public interface FitRAI<I extends RealType<I>>
		extends UnaryHybridCF<RandomAccessibleInterval<I>, RandomAccessibleInterval<FloatType>> {
		String NAME = "FitRAI";

		void setParams(FitParams params);
	}
}
