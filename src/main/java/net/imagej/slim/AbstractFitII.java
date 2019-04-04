package net.imagej.slim;

import org.scijava.plugin.Parameter;

import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imagej.slim.SlimOps.FitII;
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

	@Override
	public void setParams(FitParams p) {
		this.params = p;
	}

	@Override
	public void initialize() {
		super.initialize();
		if (worker == null)
			worker = createWorker();
	}

	@Override
	public void compute(IterableInterval<I> trans, FitResults results) {
		worker.fit(trans, params, results);
	}

	@Override
	public FitResults createOutput(IterableInterval<I> trans) {
		FitResults results = new FitResults();
		int nIn  = (int) trans.size();
		int nOut = worker.nParamOut();
		results.param     = new float[nOut];
		results.fitted    = new float[nIn];
		results.residuals = new float[nIn];
		return results;
	}

	@Override
	public int getOutputSize() {
		return worker.nParamOut();
	}

	/**
	 * Generates a worker for the actual fit.
	 * @return A {@link FitWorker}.
	 */
	protected abstract FitWorker<I> createWorker();
}
