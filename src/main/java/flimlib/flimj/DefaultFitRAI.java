package flimlib.flimj;

import org.scijava.plugin.Plugin;

import flimlib.flimj.FlimOps.BayesOp;
import flimlib.flimj.FlimOps.GlobalOp;
import flimlib.flimj.FlimOps.LMAOp;
import flimlib.flimj.FlimOps.PhasorOp;
import flimlib.flimj.FlimOps.RLDOp;
import flimlib.flimj.fitworker.*;
import flimlib.flimj.FitParams;
import flimlib.flimj.FitResults;
import net.imglib2.type.numeric.RealType;

/**
 * FLIMLib fitters on a {@link net.imglib2.RandomAccessibleInterval} of
 * time-resolved FLIM data.
 *
 * @author Dasong Gao
 */
public class DefaultFitRAI {

	private DefaultFitRAI() {
		// NB: Prevent instantiation of utility class.
	}

	@Plugin(type = LMAOp.class)
	public static class LMASingleFitRAI<I extends RealType<I>> extends AbstractFitRAI<I> {

		@Override
		public FitWorker<I> createWorker(FitParams<I> params, FitResults results) {
			return new LMAFitWorker<I>(params, results, ops());
		}
	}

	@Plugin(type = RLDOp.class)
	public static class RLDSingleFitRAI<I extends RealType<I>> extends AbstractFitRAI<I> {

		@Override
		public FitWorker<I> createWorker(FitParams<I> params, FitResults results) {
			return new RLDFitWorker<I>(params, results, ops());
		}
	}

	@Plugin(type = BayesOp.class)
	public static class BayesSingleFitRAI<I extends RealType<I>> extends AbstractFitRAI<I> {

		@Override
		public FitWorker<I> createWorker(FitParams<I> params, FitResults results) {
			return new BayesFitWorker<I>(params, results, ops());
		}
	}

	@Plugin(type = PhasorOp.class)
	public static class PhasorSingleFitRAI<I extends RealType<I>> extends AbstractFitRAI<I> {

		@Override
		public FitWorker<I> createWorker(FitParams<I> params, FitResults results) {
			return new PhasorFitWorker<I>(params, results, ops());
		}
	}

	@Plugin(type = GlobalOp.class)
	public static class LMAGlobalFitRAI<I extends RealType<I>> extends AbstractFitRAI<I> {

		@Override
		public FitWorker<I> createWorker(FitParams<I> params, FitResults results) {
			return new GlobalFitWorker<I>(params, results, ops());
		}
	}
}
