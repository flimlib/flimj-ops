package net.imagej.slim;

import org.scijava.plugin.Plugin;

import net.imagej.slim.SlimOps.MLAOp;
import net.imagej.slim.SlimOps.PhasorOp;
import net.imagej.slim.SlimOps.RLDOp;
import net.imagej.slim.fitworker.*;
import net.imagej.slim.FitParams;
import net.imagej.slim.FitResults;
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
	public static class MLAFitII<I extends RealType<I>> extends AbstractFitII<I> {

		@Override
		public FitWorker<I> createWorker(FitParams<I> params, FitResults results) {
			return new MLAFitWorker<I>(params, results, ops());
		}
	}

	@Plugin(type = RLDOp.class)
	public static class RLDFitII<I extends RealType<I>> extends AbstractFitII<I> {

		@Override
		public FitWorker<I> createWorker(FitParams<I> params, FitResults results) {
			return new RLDFitWorker<I>(params, results, ops());
		}
	}

	@Plugin(type = PhasorOp.class)
	public static class PhasorFitII<I extends RealType<I>> extends AbstractFitII<I> {

		@Override
		public FitWorker<I> createWorker(FitParams<I> params, FitResults results) {
			return new PhasorFitWorker<I>(params, results, ops());
		}
	}
}
