package net.imagej.slim;

import org.scijava.plugin.Plugin;

import net.imagej.ops.AbstractNamespace;
import net.imagej.ops.Namespace;
import net.imagej.ops.OpMethod;
import net.imagej.slim.utils.FitParams;
import net.imagej.slim.utils.FitResults;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.RealMask;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Namespace.class)
@SuppressWarnings("unchecked")
public class SlimNamespace<I extends RealType<I>> extends AbstractNamespace {

	private static final String NS_NAME = "slim";

	@Override
	public String getName() {
		return NS_NAME;
	}
	
	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class)
	public RandomAccessibleInterval fitGlobal(final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class)
	public RandomAccessibleInterval fitGlobal(final RandomAccessibleInterval out, final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class, out, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class)
	public RandomAccessibleInterval fitGlobal(final RandomAccessibleInterval out, final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis, final RealMask roi) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class, out, in, params, lifetimeAxis, roi);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class)
	public RandomAccessibleInterval fitGlobal(final RandomAccessibleInterval out, final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis, final RealMask roi, final RandomAccessibleInterval kernel) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class, out, in, params, lifetimeAxis, roi, kernel);
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
	public RandomAccessibleInterval fitMLA(final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class)
	public RandomAccessibleInterval fitMLA(final RandomAccessibleInterval out, final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class, out, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class)
	public RandomAccessibleInterval fitMLA(final RandomAccessibleInterval out, final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis, final RealMask roi) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class, out, in, params, lifetimeAxis, roi);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class)
	public RandomAccessibleInterval fitMLA(final RandomAccessibleInterval out, final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis, final RealMask roi, final RandomAccessibleInterval kernel) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class, out, in, params, lifetimeAxis, roi, kernel);
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
	public RandomAccessibleInterval fitPhasor(final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class)
	public RandomAccessibleInterval fitPhasor(final RandomAccessibleInterval out, final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class, out, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class)
	public RandomAccessibleInterval fitPhasor(final RandomAccessibleInterval out, final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis, final RealMask roi) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class, out, in, params, lifetimeAxis, roi);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class)
	public RandomAccessibleInterval fitPhasor(final RandomAccessibleInterval out, final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis, final RealMask roi, final RandomAccessibleInterval kernel) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class, out, in, params, lifetimeAxis, roi, kernel);
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
	public RandomAccessibleInterval fitRLD(final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class)
	public RandomAccessibleInterval fitRLD(final RandomAccessibleInterval out, final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class, out, in, params, lifetimeAxis);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class)
	public RandomAccessibleInterval fitRLD(final RandomAccessibleInterval out, final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis, final RealMask roi) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class, out, in, params, lifetimeAxis, roi);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class)
	public RandomAccessibleInterval fitRLD(final RandomAccessibleInterval out, final RandomAccessibleInterval in, final FitParams params, final int lifetimeAxis, final RealMask roi, final RandomAccessibleInterval kernel) {
		final RandomAccessibleInterval result =
			(RandomAccessibleInterval) ops().run(net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class, out, in, params, lifetimeAxis, roi, kernel);
		return result;
	}


}
