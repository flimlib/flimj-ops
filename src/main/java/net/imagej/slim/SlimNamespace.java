package net.imagej.slim;

import org.scijava.plugin.Plugin;

import net.imagej.ops.AbstractNamespace;
import net.imagej.ops.Namespace;
import net.imagej.ops.OpMethod;
import net.imagej.slim.FitParams;
import net.imagej.slim.FitResults;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.display.ColorTable;
import net.imglib2.img.Img;
import net.imglib2.roi.RealMask;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;

@SuppressWarnings("unchecked")
@Plugin(type = Namespace.class)
public class SlimNamespace<I extends RealType<I>> extends AbstractNamespace {

	private static final String NS_NAME = "slim";

	@Override
	public String getName() {
		return NS_NAME;
	}
	
	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class)
	public FitResults fitGlobal(final FitParams<I> in) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class, in);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class)
	public FitResults fitGlobal(final FitParams<I> in, final RealMask roi) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class, in, roi);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class)
	public FitResults fitGlobal(final FitParams<I> in, final RealMask roi, final RandomAccessibleInterval<I> kernel) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.MLAGlobalFitRAI.class, in, roi, kernel);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class)
	public FitResults fitMLA(final FitParams<I> in) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class)
	public FitResults fitMLA(final FitParams<I> in, final RealMask roi) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class, roi);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class)
	public FitResults fitMLA(final FitParams<I> in, final RealMask roi, final RandomAccessibleInterval<I> kernel) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.MLASingleFitRAI.class, roi, kernel);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class)
	public FitResults fitPhasor(final FitParams<I> in) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class)
	public FitResults fitPhasor(final FitParams<I> in, final RealMask roi) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class, roi);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class)
	public FitResults fitPhasor(final FitParams<I> in, final RealMask roi, final RandomAccessibleInterval<I> kernel) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.PhasorSingleFitRAI.class, roi, kernel);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class)
	public FitResults fitRLD(final FitParams<I> in) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class)
	public FitResults fitRLD(final FitParams<I> in, final RealMask roi) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class, roi);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class)
	public FitResults fitRLD(final FitParams<I> in, final RealMask roi, final RandomAccessibleInterval<I> kernel) {
		final FitResults result =
			(FitResults) ops().run(net.imagej.slim.DefaultFitRAI.RLDSingleFitRAI.class, roi, kernel);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultDispRslt.Pseudocolor.class)
	public Img<ARGBType> showPseudocolor(final FitResults in) {
		final Img<ARGBType> result =
			(Img<ARGBType>) ops().run(net.imagej.slim.DefaultDispRslt.Pseudocolor.class, in);
		return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultDispRslt.Pseudocolor.class)
	public Img<ARGBType> showPseudocolor(final FitResults in, final Float colorMin) {
			final Img<ARGBType> result =
					(Img<ARGBType>) ops().run(net.imagej.slim.DefaultDispRslt.Pseudocolor.class, in, colorMin);
			return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultDispRslt.Pseudocolor.class)
	public Img<ARGBType> showPseudocolor(final FitResults in, final Float colorMin, final Float colorMax) {
			final Img<ARGBType> result =
					(Img<ARGBType>) ops().run(net.imagej.slim.DefaultDispRslt.Pseudocolor.class, in, colorMin, colorMax);
			return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultDispRslt.Pseudocolor.class)
	public Img<ARGBType> showPseudocolor(final FitResults in, final Float colorMin, final Float colorMax, final Float brightnessMin) {
			final Img<ARGBType> result =
					(Img<ARGBType>) ops().run(net.imagej.slim.DefaultDispRslt.Pseudocolor.class, in, colorMin, colorMax, brightnessMin);
			return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultDispRslt.Pseudocolor.class)
	public Img<ARGBType> showPseudocolor(final FitResults in, final Float colorMin, final Float colorMax, final Float brightnessMin, final Float brightnessMax) {
			final Img<ARGBType> result =
					(Img<ARGBType>) ops().run(net.imagej.slim.DefaultDispRslt.Pseudocolor.class, in, colorMin, colorMax, brightnessMin, brightnessMax);
			return result;
	}

	@OpMethod(op = net.imagej.slim.DefaultDispRslt.Pseudocolor.class)
	public Img<ARGBType> showPseudocolor(final FitResults in, final Float colorMin, final Float colorMax, final Float brightnessMin, final Float brightnessMax, final ColorTable lut) {
			final Img<ARGBType> result =
					(Img<ARGBType>) ops().run(net.imagej.slim.DefaultDispRslt.Pseudocolor.class, in, colorMin, colorMax, brightnessMin, brightnessMax, lut);
			return result;
	}
}
