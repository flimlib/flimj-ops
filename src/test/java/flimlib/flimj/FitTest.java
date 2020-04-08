/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2014 - 2017 ImageJ developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package flimlib.flimj;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scijava.Context;
import flimlib.flimj.FlimOps.FitRAI;
import io.scif.img.ImgOpener;
import io.scif.lifesci.SDTFormat;
import io.scif.lifesci.SDTFormat.Reader;
import net.imagej.ops.AbstractOpTest;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.geom.real.OpenWritableBox;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;

/**
 * Regression tests for {@link FitRAI} ops.
 * 
 * @author Dasong Gao
 */
public class FitTest extends AbstractOpTest {

	static RandomAccessibleInterval<UnsignedShortType> in;

	static FitParams<UnsignedShortType> param_master;
	static FitParams<UnsignedShortType> param;

	static long[] min, max, vMin, vMax;

	static RealMask roi;

	private static final long SEED = 0x1226;

	private static final Random rng = new Random(SEED);

	private static final int NSAMPLE = 5;

	private static final float TOLERANCE = 1e-5f;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void init() throws IOException {
		Reader r = new SDTFormat.Reader();
		r.setContext(new Context());
		r.setSource("test_files/input.sdt");
		in = (Img<UnsignedShortType>) new ImgOpener().openImgs(r).get(0).getImg();

		// input and output boundaries
		min = new long[] { 0, 40, 40 };
		max = new long[] { 63, 87, 87 };
		vMin = min.clone();
		vMax = max.clone();

		in = Views.hyperSlice(in, 3, 12);
		r.close();

		param_master = new FitParams<UnsignedShortType>();
		param_master.ltAxis = 0;
		param_master.xInc = 0.195f;
		param_master.transMap = in;
		param_master.fitStart = 9;
		param_master.fitEnd = 20;
		param_master.paramFree = new boolean[] { true, true, true };
		param_master.dropBad = false;

		// +/- 1 because those dimensions are the closure of the box
		roi = new OpenWritableBox(new double[] { min[1] - 1, min[2] - 1 }, new double[] { max[1] + 1, max[2] + 1 });
	}

	@Before
	public void initParam() {
		param = param_master.copy();
	}

	@Test
	public void testRLDFitImg() {
		long ms = System.currentTimeMillis();
		FitResults out = (FitResults) ops.run("flim.fitRLD", param);
		System.out.println("RLD finished in " + (System.currentTimeMillis() - ms) + " ms");

		float[] exp = { 2.5887516f, 1.3008053f, 0.1802666f, 4.498526f, 0.20362994f };
		assertSampleEquals(out.paramMap, exp);
	}
	
	@Test
	public void testBinning() {
		long ms = System.currentTimeMillis();
		FitResults out = (FitResults) ops.run("flim.fitRLD", param, roi, FlimOps.SQUARE_KERNEL_3);
		System.out.println("RLD with binning finished in " + (System.currentTimeMillis() - ms) + " ms");
		
		float[] exp = { 15.917448f, 34.33285f, 0.17224349f, 53.912094f, 0.19115955f };
		assertSampleEquals(out.paramMap, exp);
	}
	
	@Test
	public void testLMAFitImg() {
		// estimation using RLD
		param.paramMap = param.paramMap = ((FitResults) ops.run("flim.fitRLD", param, roi)).paramMap;
		
		long ms = System.currentTimeMillis();
		FitResults out = (FitResults) ops.run("flim.fitLMA", param, roi);
		System.out.println("LMA finished in " + (System.currentTimeMillis() - ms) + " ms");
		
		float[] exp = { 2.8199558f, 2.1738043f, 0.15078613f, 5.6381326f, 0.18440692f };
		assertSampleEquals(out.paramMap, exp);
	}

	@Test
	public void testBayesFitImg() {
		// estimation using RLD
		param.getChisqMap = true;
		// param.multithread = false;
		long ms = System.currentTimeMillis();
		FitResults out = (FitResults) ops.run("flim.fitBayes", param, roi);
		System.out.println("Bayes finished in " + (System.currentTimeMillis() - ms) + " ms");

		float[] exp = { 0.0f, 0.0f, 0.20058449f, 0.0f, 0.26743606f };
		assertSampleEquals(out.paramMap, exp);
	}

	@Test
	public void testInstr() {
		// estimation using RLD
		param.paramMap = param.paramMap = ((FitResults) ops.run("flim.fitRLD", param, roi)).paramMap;

		// a trivial IRF
		param.instr = new float[12];
		param.instr[0] = 1;
		
		long ms = System.currentTimeMillis();
		FitResults out = (FitResults) ops.run("flim.fitLMA", param, roi);
		System.out.println("LMA with instr finished in " + (System.currentTimeMillis() - ms) + " ms");
		
		float[] exp = { 2.8199558f, 2.1738043f, 0.15078613f, 5.6381326f, 0.18440692f };
		assertSampleEquals(out.paramMap, exp);
	}

	@Test
	public void testPhasorFitImg() {
		long ms = System.currentTimeMillis();
		FitResults out = (FitResults) ops.run("flim.fitPhasor", param, roi);
		System.out.println("Phasor finished in " + (System.currentTimeMillis() - ms) + " ms");

		float[] exp = { 0, 0.17804292f, 0.41997245f, 0.18927118f, 0.39349627f };
		assertSampleEquals(out.paramMap, exp);
	}

	@Test
	public void testGlobalFitImg() {
		long ms = System.currentTimeMillis();
		FitResults out = (FitResults) ops.run("flim.fitGlobal", param, roi);
		System.out.println("Global fit finished in " + (System.currentTimeMillis() - ms) + " ms");

		float[] exp = { 2.5887516f, 1.3008053f, 0.16449152f, 4.498526f, 0.16449152f };
		assertSampleEquals(out.paramMap, exp);
	}

	@Test
	public void testGlobalFitImgMultiExp() {
		param.nComp = 2;
		param.paramFree = new boolean[] { true, true, true, true, true };
		long ms = System.currentTimeMillis();
		FitResults out = (FitResults) ops.run("flim.fitGlobal", param, roi);
		System.out.println("Global fit (Multi) finished in " + (System.currentTimeMillis() - ms) + " ms");

		float[] exp = { 301.6971f, 0.1503315f, 430.5284f, 0.17790353f, 0.1503315f };
		assertSampleEquals(out.paramMap, exp);
	}

	private static <T extends RealType<T>> float[] getRandPos(IterableInterval<T> ii, int n, long...seed) {
		float[] arr = new float[n];
		rng.setSeed(seed.length == 0 ? SEED : seed[0]);
		int sz = (int) ii.size();
		Cursor<T> cursor = ii.cursor();
		long cur = 0;
		for (int i = 0; i < n; i++) {
			long next = rng.nextInt(sz);
			cursor.jumpFwd(next - cur);
			cur = next;
			arr[i] = cursor.get().getRealFloat();
		}
		return arr;
	}
	
	private static <T extends RealType<T>> void assertSampleEquals(RandomAccessibleInterval<T> map, float[] exp) {
		vMax[0] = map.max(param_master.ltAxis);
		float[] act = getRandPos(Views.interval(map, vMin, vMax), NSAMPLE);
		try {
			Assert.assertArrayEquals(exp, act, TOLERANCE);
		} catch (Error e) {
			System.out.println("Actual: " + Arrays.toString(act));
			throw e;
		}
	}
}
