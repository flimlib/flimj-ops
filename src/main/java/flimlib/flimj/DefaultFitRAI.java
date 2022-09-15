/*-
 * #%L
 * Fluorescence lifetime analysis in ImageJ.
 * %%
 * Copyright (C) 2017 - 2022 Board of Regents of the University of Wisconsin-Madison.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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
