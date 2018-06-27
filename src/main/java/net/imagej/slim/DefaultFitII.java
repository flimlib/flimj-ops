package net.imagej.slim;

import org.scijava.plugin.Plugin;

import net.imagej.slim.SlimFit.FitII;
import net.imagej.slim.utils.FitWorker;
import net.imagej.slim.utils.MLAFitWorker;
import net.imagej.slim.utils.RLDFitWorker;
import net.imglib2.type.numeric.RealType;

/**
 * SLIM Curve fitters on a {@link net.imglib2.IterableInterval} of time-resolved
 * FLIM data.
 *
 * @author Dasong Gao
 */
public class DefaultFitII {
	private DefaultFitII() {
		// NB: Prevent instantiation of utility class.
	}

	@Plugin(type = SlimFit.FitII.class)
	public static class MLAFitII<I extends RealType<I>>
		extends AbstractFitII<I> implements FitII<I> {

		@Override
		protected FitWorker<I> createWorker() {
			return new MLAFitWorker<I>();
		}
	}

	@Plugin(type = SlimFit.FitII.class)
	public static class RLDFitII<I extends RealType<I>>
		extends AbstractFitII<I> implements FitII<I> {

		@Override
		protected FitWorker<I> createWorker() {
			return new RLDFitWorker<I>();
		}
	}
}
