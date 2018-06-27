package net.imagej.slim;

import java.util.Arrays;

import org.scijava.plugin.Parameter;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imagej.slim.SlimFit.FitII;
import net.imagej.slim.utils.FitParams;
import net.imagej.slim.utils.FitResults;
import net.imagej.slim.utils.FitWorker;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;

public abstract class AbstractFitII<I extends RealType<I>> extends
	AbstractUnaryHybridCF<IterableInterval<I>, FitResults> implements FitII<I> {

	@Parameter
	FitParams params;

	private FitWorker<I> worker;
	
	// TODO move to op
	public void setParams(FitParams p) {
		this.params = p;
	}

	@Override
	public void compute(IterableInterval<I> trans, FitResults results) {
		if (worker == null)
			worker = createWorker();
		worker.fit(trans, params, results);
	}

	@Override
	public FitResults createOutput(IterableInterval<I> trans) {
		FitResults results = new FitResults();
		int nData = (int) trans.size();
		results.param = Arrays.copyOf(params.param, params.param.length);
		results.fitted = new float[nData];
		results.residuals = new float[nData];
		return results;
	}

	/**
	 * Generates a worker for the actual fit.
	 * @return A {@link FitWorker}.
	 */
	protected abstract FitWorker<I> createWorker();
}
