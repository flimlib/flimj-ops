package net.imagej.slim;

import org.scijava.plugin.Plugin;

import net.imagej.ops.AbstractNamespace;
import net.imagej.ops.Namespace;
import net.imagej.ops.OpMethod;
import net.imagej.slim.DefaultFitII.MLAFitII;
import net.imagej.slim.DefaultFitII.PhasorFitII;
import net.imagej.slim.DefaultFitII.RLDFitII;
import net.imagej.slim.DefaultFitRAI.MLAFitRAI;
import net.imagej.slim.DefaultFitRAI.PhasorFitRAI;
import net.imagej.slim.DefaultFitRAI.RLDFitRAI;
import net.imagej.slim.utils.FitParams;
import net.imagej.slim.utils.FitResults;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.Shape;
import net.imglib2.roi.RealMask;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

@Plugin(type = Namespace.class)
@SuppressWarnings("unchecked")
public class SlimNamespace<I extends RealType<I>> extends AbstractNamespace {

	private static final String NS_NAME = "slim";

	@Override
	public String getName() {
		return NS_NAME;
	}

	@OpMethod(op = RLDFitII.class)
	public  FitResults fitRLD(final IterableInterval<I> in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(RLDFitII.class, in, params);
		return result;
	}

	@OpMethod(op = RLDFitII.class)
	public  FitResults fitRLD(final FitResults out, final IterableInterval<I> in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(RLDFitII.class, out, in, params);
		return result;
	}

	@OpMethod(op = MLAFitII.class)
	public  FitResults fitMLA(final IterableInterval<I> in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(MLAFitII.class, in, params);
		return result;
	}

	@OpMethod(op = MLAFitII.class)
	public  FitResults fitMLA(final FitResults out, final IterableInterval<I> in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(MLAFitII.class, out, in, params);
		return result;
	}

	@OpMethod(op = RLDFitRAI.class)
	public  RandomAccessibleInterval<I> fitRLD(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(RLDFitRAI.class, out, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = RLDFitRAI.class)
	public  RandomAccessibleInterval<I> fitRLD(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis, final RealMask roi) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(RLDFitRAI.class, out, in, params, lifetimeAxis, roi);
		return result;
	}

	@OpMethod(op = RLDFitRAI.class)
	public  RandomAccessibleInterval<I> fitRLD(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis, final RealMask roi, final Shape binningKnl) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(RLDFitRAI.class, out, in, params, lifetimeAxis, roi, binningKnl);
		return result;
	}

	@OpMethod(op = RLDFitRAI.class)
	public  RandomAccessibleInterval<I> fitRLD(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis, final RealMask roi, final Shape binningKnl, final int... binningAxes) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(RLDFitRAI.class, out, in, params, lifetimeAxis, roi, binningKnl, binningAxes);
		return result;
	}

	@OpMethod(op = MLAFitRAI.class)
	public  RandomAccessibleInterval<I> fitMLA(final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(MLAFitRAI.class, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = MLAFitRAI.class)
	public  RandomAccessibleInterval<I> fitMLA(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(MLAFitRAI.class, out, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = MLAFitRAI.class)
	public  RandomAccessibleInterval<I> fitMLA(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis, final RealMask roi) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(MLAFitRAI.class, out, in, params, lifetimeAxis, roi);
		return result;
	}

	@OpMethod(op = MLAFitRAI.class)
	public  RandomAccessibleInterval<I> fitMLA(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis, final RealMask roi, final Shape binningKnl) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(MLAFitRAI.class, out, in, params, lifetimeAxis, roi, binningKnl);
		return result;
	}

	@OpMethod(op = MLAFitRAI.class)
	public  RandomAccessibleInterval<I> fitMLA(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis, final RealMask roi, final Shape binningKnl, final int... binningAxes) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(MLAFitRAI.class, out, in, params, lifetimeAxis, roi, binningKnl, binningAxes);
		return result;
	}

	@OpMethod(op = RLDFitRAI.class)
	public  RandomAccessibleInterval<I> fitRLD(final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(RLDFitRAI.class, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = DefaultFitRAI.MLAFitRAI.class)
	public  RandomAccessibleInterval<FloatType> mlaFit(RandomAccessibleInterval<I> in, FitParams param, int lifetimeAxis) {
		return (RandomAccessibleInterval<FloatType>) ops().run(DefaultFitRAI.MLAFitRAI.class, null, in, param, lifetimeAxis, null, null, null);
	}

	@OpMethod(op = PhasorFitII.class)
	public  FitResults fitPhasor(final IterableInterval<I> in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(PhasorFitII.class, in, params);
		return result;
	}

	@OpMethod(op = PhasorFitII.class)
	public  FitResults fitPhasor(final FitResults out, final IterableInterval<I> in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(PhasorFitII.class, out, in, params);
		return result;
	}
	@OpMethod(op = PhasorFitRAI.class)
	public  RandomAccessibleInterval<I> fitPhasor(final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(PhasorFitRAI.class, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = PhasorFitRAI.class)
	public RandomAccessibleInterval<I> fitPhasor(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(PhasorFitRAI.class, out, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = PhasorFitRAI.class)
	public RandomAccessibleInterval<I> fitPhasor(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis, final RealMask roi) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(PhasorFitRAI.class, out, in, params, lifetimeAxis, roi);
		return result;
	}

	@OpMethod(op = PhasorFitRAI.class)
	public RandomAccessibleInterval<I> fitPhasor(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis, final RealMask roi, final Shape binningKnl) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(PhasorFitRAI.class, out, in, params, lifetimeAxis, roi, binningKnl);
		return result;
	}

	@OpMethod(op = PhasorFitRAI.class)
	public RandomAccessibleInterval<I> fitPhasor(final RandomAccessibleInterval<I> out, final RandomAccessibleInterval<I> in, final FitParams params, final int lifetimeAxis, final RealMask roi, final Shape binningKnl, final int... binningAxes) {
		final RandomAccessibleInterval<I> result =
			(RandomAccessibleInterval<I>) ops().run(PhasorFitRAI.class, out, in, params, lifetimeAxis, roi, binningKnl, binningAxes);
		return result;
	}
}
