package net.imagej.slim;

import org.scijava.plugin.Parameter;

import net.imagej.slim.SlimOps.FitII;
import net.imagej.slim.FitParams;
import net.imagej.slim.FitResults;
import net.imagej.slim.fitworker.*;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;

public abstract class AbstractFitII<I extends RealType<I>> extends FitII<I> {

	@Parameter
	FitParams<I> params;

	private FitWorker<I> worker;

	@Override
	public void initialize() {
		super.initialize();
		if (worker == null)
			worker = createWorker(params, out());
	}

	@Override
	public void compute(IterableInterval<I> trans, FitResults results) {
		// float[] transBuffer = Utils.ii2FloatArr(trans, params.fitStart, params.fitEnd + 1, null);
		// TODO copy transBuffer to params.trans
		// worker.fitSingle(params, results);
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
}
