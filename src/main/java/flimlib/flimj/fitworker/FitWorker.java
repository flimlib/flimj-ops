package flimlib.flimj.fitworker;

import java.util.List;
import flimlib.flimj.FitParams;
import flimlib.flimj.FitResults;
import net.imglib2.type.numeric.RealType;

public interface FitWorker<I extends RealType<I>> {

	/**
	 * The handler interface for fit events. For multithreaded workers like
	 * {@link AbstractSingleFitWorker}s, it is the user's responsibility to handle concurrency shall
	 * the handler functions be called by multiple fit workers at the same time.
	 * 
	 * @param <I> The parameter type
	 */
	public interface FitEventHandler<I extends RealType<I>> {
		/**
		 * The handler called by {@link FitWorker}s upon completion of all fits.
		 * 
		 * @param params  the params
		 * @param results the results
		 */
		default void onComplete(FitParams<I> params, FitResults results) {}

		/**
		 * The handler called by {@link FitWorker}s upon completion of a single fit.
		 * 
		 * @param pos     the x, y coordinate of the trans being fitted
		 * @param params  the params (volatile) of the completed fit
		 * @param results the results (volatile) from the completed fit
		 */
		default void onSingleComplete(int[] pos, FitParams<I> params, FitResults results) {}
	}

	/**
	 * How many parameters should there be in {@code results.param}? E.g. 3 for one-component
	 * {@link LMAFitWorker} and 5 for {@link PhasorFitWorker}.
	 * 
	 * @return The number of output parameters in the parameter array.
	 */
	int nParamOut();

	/**
	 * How many bins will be fitted?
	 * 
	 * @return {@code fitEnd - fitStart}
	 */
	int nDataOut();

	/**
	 * Fit all coordinates listed.
	 * 
	 * @param pos the coordinates of trans to fit
	 */
	default void fitBatch(List<int[]> pos) {
		fitBatch(pos, null);
	};

	/**
	 * Fit all coordinates listed and handles fit events.
	 * 
	 * @param pos     the coordinates of trans to fit
	 * @param handler the fit event handler
	 */
	void fitBatch(List<int[]> pos, FitEventHandler<I> handler);
}
