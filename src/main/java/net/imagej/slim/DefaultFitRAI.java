package net.imagej.slim;

import org.scijava.plugin.Plugin;

import net.imagej.slim.DefaultFitII.MLAFitII;
import net.imagej.slim.DefaultFitII.RLDFitII;
import net.imagej.slim.SlimFit.FitII;
import net.imagej.slim.SlimFit.FitRAI;
import net.imglib2.type.numeric.RealType;

/**
 * SLIM Curve fitters on a {@link net.imglib2.RandomAccessibleInterval} of time-resolved
 * FLIM data.
 *
 * @author Dasong Gao
 */
public class DefaultFitRAI {
	private DefaultFitRAI() {
		// NB: Prevent instantiation of utility class.
	}

	@Plugin(type = SlimFit.FitRAI.class)
	public static class MLAFitRAI<I extends RealType<I>>
		extends AbstractFitRAI<I> implements FitRAI<I> {

		@Override
		protected FitII<I> createFitII() {
			return new MLAFitII<I>();
		}
	}

	@Plugin(type = SlimFit.FitRAI.class)
	public static class RLDFitRAI<I extends RealType<I>>
		extends AbstractFitRAI<I> implements FitRAI<I> {

		@Override
		protected FitII<I> createFitII() {
			return new RLDFitII<I>();
		}
	}
}