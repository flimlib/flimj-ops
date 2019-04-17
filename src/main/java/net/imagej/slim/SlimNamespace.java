package net.imagej.slim;

import org.scijava.plugin.Plugin;

import net.imagej.ops.AbstractNamespace;
import net.imagej.ops.Namespace;
import net.imagej.ops.OpMethod;
import net.imagej.slim.FitParams;
import net.imagej.slim.FitResults;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.RealMask;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Namespace.class)
public class SlimNamespace<I extends RealType<I>> extends AbstractNamespace {

	private static final String NS_NAME = "slim";

	@Override
	public String getName() {
		return NS_NAME;
	}
	
	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class)
	public FitResults fitGlobal(final FitParams in, final int lifetimeAxis) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class, in, lifetimeAxis);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class)
	public FitResults fitGlobal(final FitParams in, final int lifetimeAxis, final RealMask roi) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class, in, lifetimeAxis, roi);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class)
	public FitResults fitGlobal(final FitParams in, final int lifetimeAxis, final RealMask roi, final RandomAccessibleInterval kernel) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class, in, lifetimeAxis, roi, kernel);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitII.MLAFitII.class)
	public FitResults fitMLA(final IterableInterval in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitII.MLAFitII.class, in, params);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitII.MLAFitII.class)
	public FitResults fitMLA(final FitResults out, final IterableInterval in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitII.MLAFitII.class, out, in, params);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class)
	public FitResults fitMLA(final FitParams in, final int lifetimeAxis) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class, in, lifetimeAxis);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class)
	public FitResults fitMLA(final FitParams in, final int lifetimeAxis, final RealMask roi) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class, in, lifetimeAxis, roi);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class)
	public FitResults fitMLA(final FitParams in, final int lifetimeAxis, final RealMask roi, final RandomAccessibleInterval kernel) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class, in, lifetimeAxis, roi, kernel);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitII.PhasorFitII.class)
	public FitResults fitPhasor(final IterableInterval in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitII.PhasorFitII.class, in, params);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitII.PhasorFitII.class)
	public FitResults fitPhasor(final FitResults out, final IterableInterval in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitII.PhasorFitII.class, out, in, params);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class)
	public FitResults fitPhasor(final FitParams in, final int lifetimeAxis) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class, in, lifetimeAxis);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class)
	public FitResults fitPhasor(final FitParams in, final int lifetimeAxis, final RealMask roi) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class, in, lifetimeAxis, roi);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class)
	public FitResults fitPhasor(final FitParams in, final int lifetimeAxis, final RealMask roi, final RandomAccessibleInterval kernel) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class, in, lifetimeAxis, roi, kernel);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitII.RLDFitII.class)
	public FitResults fitRLD(final IterableInterval in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitII.RLDFitII.class, in, params);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitII.RLDFitII.class)
	public FitResults fitRLD(final FitResults out, final IterableInterval in, final FitParams params) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitII.RLDFitII.class, out, in, params);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class)
	public FitResults fitRLD(final FitParams in, final int lifetimeAxis) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class, in, lifetimeAxis);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class)
	public FitResults fitRLD(final FitParams in, final int lifetimeAxis, final RealMask roi) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class, in, lifetimeAxis, roi);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class)
	public FitResults fitRLD(final FitParams in, final int lifetimeAxis, final RealMask roi, final RandomAccessibleInterval kernel) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class, in, lifetimeAxis, roi, kernel);
		return result;
	}
}
