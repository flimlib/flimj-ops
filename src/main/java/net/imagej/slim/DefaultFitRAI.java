package net.imagej.slim;

import org.scijava.plugin.Plugin;

import net.imagej.slim.SlimOps.GlobalOp;
import net.imagej.slim.SlimOps.MLAOp;
import net.imagej.slim.SlimOps.PhasorOp;
import net.imagej.slim.SlimOps.RLDOp;
import net.imagej.slim.fitworker.*;
import net.imglib2.type.numeric.RealType;

/**
 * SLIM Curve fitters on a {@link net.imglib2.RandomAccessibleInterval} of
 * time-resolved FLIM data.
 *
 * @author Dasong Gao
 */
public class DefaultFitRAI {

	private DefaultFitRAI() {
		// NB: Prevent instantiation of utility class.
	}

	@Plugin(type = MLAOp.class)
	public static class MLASingleFitRAI<I extends RealType<I>> extends AbstractFitRAI<I> {

		@Override
		public FitWorker<I> createWorker() {
			return new MLAFitWorker<I>();
		}
	}

	@Plugin(type = RLDOp.class)
	public static class RLDSingleFitRAI<I extends RealType<I>> extends AbstractFitRAI<I> {

		@Override
		public FitWorker<I> createWorker() {
			return new RLDFitWorker<I>();
		}
	}

	@Plugin(type = PhasorOp.class)
	public static class PhasorSingleFitRAI<I extends RealType<I>> extends AbstractFitRAI<I> {

		@Override
		public FitWorker<I> createWorker() {
			return new PhasorFitWorker<I>();
		}
	}

	@Plugin(type = GlobalOp.class)
	public static class MLAGlobalFitRAI<I extends RealType<I>> extends AbstractFitRAI<I> {

		@Override
		public FitWorker<I> createWorker() {
			return new GlobalFitWorker<I>();
		}
	}
}