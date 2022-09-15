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

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import flimlib.flimj.FlimOps.Calc;
import flimlib.flimj.FlimOps.CalcAPercentOp;
import flimlib.flimj.FlimOps.CalcTauMOp;
import net.imagej.ops.math.MathNamespace;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class DefaultCalc {

	private DefaultCalc() {
		// NB: Prevent instantiation of utility class.
	}

	@Plugin(type = CalcTauMOp.class)
	public static class CalcTauMean extends Calc {

		@Override
		public Img<FloatType> calculate(FitResults rslt) {
			RandomAccessibleInterval<FloatType> paramMap = rslt.paramMap;
			int nComp = (int) (paramMap.dimension(rslt.ltAxis) - 1) / 2;

			long[] dim = new long[paramMap.numDimensions() - 1];
			Views.hyperSlice(paramMap, rslt.ltAxis, 0).dimensions(dim);

			ArrayImg<FloatType, FloatArray> tauM = ArrayImgs.floats(dim);
			ArrayImg<FloatType, FloatArray> tauASum = ArrayImgs.floats(dim);

			MathNamespace math = ops().math();
			// tauM = sum(a_i * tau_i ^ 2), tauASum = sum(a_j * tau_j)
			for (int c = 0; c < nComp; c++) {
				IntervalView<FloatType> A = getSlice(rslt, c * 2 + 1);
				IntervalView<FloatType> tau = getSlice(rslt, c * 2 + 2);
				math.add(tauM, tauM,
						math.multiply(A, math.multiply(tau, (IterableInterval<FloatType>) tau)));
				math.add(tauASum, tauASum, math.multiply(A, (IterableInterval<FloatType>) tau));
			}
			math.add(tauASum, Float.MIN_VALUE);
			math.divide(tauM, tauM, (IterableInterval<FloatType>) tauASum);
			return tauM;
		}
	}

	@Plugin(type = CalcAPercentOp.class)
	public static class CalcAPercent extends Calc {

		@Parameter
		int index;

		@Override
		public Img<FloatType> calculate(FitResults rslt) {
			RandomAccessibleInterval<FloatType> paramMap = rslt.paramMap;
			int nComp = (int) (paramMap.dimension(rslt.ltAxis) - 1) / 2;

			long[] dim = new long[paramMap.numDimensions() - 1];
			Views.hyperSlice(paramMap, rslt.ltAxis, 0).dimensions(dim);

			ArrayImg<FloatType, FloatArray> APercent = ArrayImgs.floats(dim);
			ArrayImg<FloatType, FloatArray> ASum = ArrayImgs.floats(dim);

			MathNamespace math = ops().math();
			for (int c = 0; c < nComp; c++)
				math.add(ASum, ASum, (IterableInterval<FloatType>) getSlice(rslt, c * 2 + 1));
			math.add(ASum, Float.MIN_VALUE);
			math.divide(APercent, getSlice(rslt, index * 2 + 1), (IterableInterval<FloatType>) ASum);

			return APercent;
		}
	}

	private static IntervalView<FloatType> getSlice(FitResults rslt, int index) {
		return Views.hyperSlice(rslt.paramMap, rslt.ltAxis, index);
	}
}
