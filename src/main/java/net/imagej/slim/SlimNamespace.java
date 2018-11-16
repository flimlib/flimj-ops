package net.imagej.slim;

import org.scijava.plugin.Plugin;

import net.imagej.ops.AbstractNamespace;
import net.imagej.ops.Namespace;
import net.imagej.ops.OpMethod;
import net.imagej.slim.utils.FitParams;
import net.imagej.slim.utils.FitResults;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.Shape;
import net.imglib2.roi.RealMask;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

@Plugin(type = Namespace.class)
public class SlimNamespace extends AbstractNamespace {

	private static final String NS_NAME = "slim";
	@Override
	public String getName() {
		return NS_NAME;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitII.RLDFitII.class)
	public <I extends RealType<I>> FitResults fitRLD(final IterableInterval<I> in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitII.RLDFitII.class, in, params);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitII.RLDFitII.class)
	public <I extends RealType<I>> FitResults fitRLD(final FitResults out, final IterableInterval<I> in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitII.RLDFitII.class, out, in, params);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitII.MLAFitII.class)
	public <I extends RealType<I>> FitResults fitMLA(final IterableInterval<I> in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitII.MLAFitII.class, in, params);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitII.MLAFitII.class)
	public <I extends RealType<I>> FitResults fitMLA(final FitResults out, final IterableInterval<I> in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitII.MLAFitII.class, out, in, params);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.RLDFitRAI.class)
	public <I extends RealType<I>> RandomAccessibleInterval<I> fitRLD(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(net.imagej.slim.DefaultFitRAI.RLDFitRAI.class, out, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.RLDFitRAI.class)
	public <I extends RealType<I>> RandomAccessibleInterval<I> fitRLD(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis, final RealMask roi) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(net.imagej.slim.DefaultFitRAI.RLDFitRAI.class, out, in, params, lifetimeAxis, roi);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.RLDFitRAI.class)
	public <I extends RealType<I>> RandomAccessibleInterval<I> fitRLD(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis, final RealMask roi, final Shape binningKnl) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(net.imagej.slim.DefaultFitRAI.RLDFitRAI.class, out, in, params, lifetimeAxis, roi, binningKnl);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.RLDFitRAI.class)
	public <I extends RealType<I>> RandomAccessibleInterval<I> fitRLD(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis, final RealMask roi, final Shape binningKnl, final int... binningAxes) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(net.imagej.slim.DefaultFitRAI.RLDFitRAI.class, out, in, params, lifetimeAxis, roi, binningKnl, binningAxes);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLAFitRAI.class)
	public <I extends RealType<I>> RandomAccessibleInterval<I> fitMLA(final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(net.imagej.slim.DefaultFitRAI.MLAFitRAI.class, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLAFitRAI.class)
	public <I extends RealType<I>> RandomAccessibleInterval<I> fitMLA(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(net.imagej.slim.DefaultFitRAI.MLAFitRAI.class, out, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLAFitRAI.class)
	public <I extends RealType<I>> RandomAccessibleInterval<I> fitMLA(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis, final RealMask roi) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(net.imagej.slim.DefaultFitRAI.MLAFitRAI.class, out, in, params, lifetimeAxis, roi);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLAFitRAI.class)
	public <I extends RealType<I>> RandomAccessibleInterval<I> fitMLA(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis, final RealMask roi, final Shape binningKnl) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(net.imagej.slim.DefaultFitRAI.MLAFitRAI.class, out, in, params, lifetimeAxis, roi, binningKnl);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLAFitRAI.class)
	public <I extends RealType<I>> RandomAccessibleInterval<I> fitMLA(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis, final RealMask roi, final Shape binningKnl, final int... binningAxes) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(net.imagej.slim.DefaultFitRAI.MLAFitRAI.class, out, in, params, lifetimeAxis, roi, binningKnl, binningAxes);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.RLDFitRAI.class)
	public <I extends RealType<I>> RandomAccessibleInterval<I> fitRLD(final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(net.imagej.slim.DefaultFitRAI.RLDFitRAI.class, in, params, lifetimeAxis);
		return result;
	}

	@SuppressWarnings("unchecked")
	@OpMethod(op = DefaultFitRAI.MLAFitRAI.class)
	public <I extends RealType<I>> RandomAccessibleInterval<FloatType> mlaFit(RandomAccessibleInterval<I> in, FitParams param, int lifetimeAxis) {
		return (RandomAccessibleInterval<FloatType>) ops().run(DefaultFitRAI.MLAFitRAI.class, null, in, param, lifetimeAxis, null, null, null);
	}
}
