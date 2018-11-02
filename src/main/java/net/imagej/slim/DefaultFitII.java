package net.imagej.slim;

import org.scijava.plugin.Plugin;

import net.imagej.slim.SlimOps.FitII;
import net.imagej.slim.SlimOps.MLAOp;
import net.imagej.slim.SlimOps.RLDOp;
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

	@Plugin(type = MLAOp.class)
	public static class MLAFitII<I extends RealType<I>> extends AbstractFitII<I>
		implements FitII<I> {

		@Override
		protected FitWorker<I> createWorker() {
			return new MLAFitWorker<I>();
		}
	}

	@Plugin(type = RLDOp.class)
	public static class RLDFitII<I extends RealType<I>> extends AbstractFitII<I>
		implements FitII<I> {

		@Override
		protected FitWorker<I> createWorker() {
			return new RLDFitWorker<I>();
		}
	}
}
