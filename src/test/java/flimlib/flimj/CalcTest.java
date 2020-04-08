/*
 * #%L ImageJ software for multidimensional image processing and analysis. %% Copyright (C) 2014 -
 * 2017 ImageJ developers. %% Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. #L%
 */

package flimlib.flimj;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.util.Random;
import org.junit.BeforeClass;
import org.junit.Test;
import flimlib.flimj.FlimOps.Calc;
import net.imagej.ops.AbstractOpTest;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Tests for {@link Calc} ops.
 * 
 * @author Dasong Gao
 */
@SuppressWarnings("unchecked")
public class CalcTest extends AbstractOpTest {

	private static FitResults rslt;

	private static long[] DIM = {2, 3, 7};
	private static float[] data = new float[(int) (DIM[0] * DIM[1] * DIM[2])];

	private static final Random rng = new Random(0x1226);

	private static final float TOLERANCE = 1e-5f;

	@BeforeClass
	public static void init() throws IOException {
		// create a image of size DIM filled with random values
		rslt = new FitResults();
		rslt.paramMap = ArrayImgs.floats(DIM);
		rslt.ltAxis = 2;

		int i = 0;
		for (final FloatType pix : rslt.paramMap) {
			data[i] = rng.nextFloat();
			pix.set(data[i++]);
		}
	}

	@Test
	public void testCalcTauMean() {
		int i = 0;
		final Img<FloatType> tauM = (Img<FloatType>) ops.run("flim.calcTauMean", rslt);
		for (final FloatType pix : tauM) {
			int nComp = (int) (DIM[2] - 1) / 2;
			float[] AjTauj = new float[nComp];
			float sumAjTauj = 0;

			for (int j = 0; j < nComp; j++) {
				final float Aj = getVal(i, j * 2 + 1);
				final float tauj = getVal(i, j * 2 + 2);
				AjTauj[j] = Aj * tauj;
				sumAjTauj += AjTauj[j];
			}
			float exp = 0;
			for (int j = 0; j < nComp; j++) {
				final float tauj = getVal(i, j * 2 + 2);
				exp += tauj * AjTauj[j] / sumAjTauj;
			}

			assertEquals(exp, pix.get(), TOLERANCE);
			i++;
		}
	}

	@Test
	public void testCalcAPercent() {
		int i = 0;
		final Img<FloatType> A1Perc = (Img<FloatType>) ops.run("flim.calcAPercent", rslt, 0);
		for (final FloatType pix : A1Perc) {
			int nComp = (int) (DIM[2] - 1) / 2;
			float sumAj = 0;

			for (int j = 0; j < nComp; j++)
				sumAj += getVal(i, j * 2 + 1);
			final float A1 = getVal(i, 1);
			final float exp = A1 / sumAj;

			assertEquals(exp, pix.get(), TOLERANCE);
			i++;
		}
	}

	/**
	 * Gets the i'th pixel of plane p from an ram image of dimension
	 */
	private static float getVal(int i, int p) {
		return data[(int) (i + DIM[0] * DIM[1] * p)];
	}
}
