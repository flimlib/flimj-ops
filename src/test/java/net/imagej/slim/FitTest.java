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

package net.imagej.slim;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scijava.Context;

import io.scif.img.ImgOpener;
import io.scif.lifesci.SDTFormat;
import io.scif.lifesci.SDTFormat.Reader;
import net.imagej.ops.AbstractOpTest;
import net.imagej.ops.convert.RealTypeConverter;
import net.imagej.slim.utils.FitParams;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.geom.real.OpenWritableBox;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import slim.FitFunc;
import slim.NoiseType;
import slim.RestrainType;

/**
 * Regression tests for {@link RealTypeConverter} ops.
 * 
 * @author Dasong Gao
 */
public class FitTest extends AbstractOpTest {

	static RandomAccessibleInterval<UnsignedShortType> in;

	static FitParams param;

	static int lifetimeAxis;

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
		r.setSource(new File("input.sdt"));
		in = (Img<UnsignedShortType>) new ImgOpener().openImgs(r).get(0).getImg();

		// input and output boundaries
		min = new long[] { 0, 40, 40 };
		max = new long[] { 63, 87, 87 };
		vMin = min.clone();
		vMax = max.clone();

		// in = Views.interval(in, vMin, vMax);
		in = Views.hyperSlice(in, 3, 12);
		r.close();

		lifetimeAxis = 0;

		param = new FitParams();
		param.chisq_delta = 0.0001f;
		param.chisq_percent = 95;
		param.chisq_target = 1;
		param.fitStart = 9;
		param.fitEnd = 20;
		param.fitFunc = FitFunc.GCI_MULTIEXP_TAU;
		param.noise = NoiseType.NOISE_POISSON_FIT;
		param.param = new float[3];
		param.paramFree = new boolean[] { true, true, true };
		param.restrain = RestrainType.ECF_RESTRAIN_DEFAULT;
		param.xInc = 0.195f;

		// +/- 1 because those dimensions are the closure of the box
		roi = new OpenWritableBox(new double[] { min[1] - 1, min[2] - 1 }, new double[] { max[1] + 1, max[2] + 1 });

	}

	@Test
	@SuppressWarnings("unchecked")
	public void testRLDFitImg() {
		long ms = System.currentTimeMillis();
		Img<FloatType> out = (Img<FloatType>) ops.run("slim.fitRLD", null, in, param, lifetimeAxis);
		System.out.println("RLD finished in " + (System.currentTimeMillis() - ms) + " ms");
		param.paramRA = out;

		// 3 parameter layers
		vMin[0] = 0;
		vMax[0] = 2;
		float[] act = getRandPos(Views.interval(out, vMin, vMax), NSAMPLE);
		float[] exp = { 2.5887516f, 1.3008053f, 0.1802666f, 4.498526f, 0.20362994f };
		Assert.assertArrayEquals(exp, act, TOLERANCE);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testBinning() {
		long ms = System.currentTimeMillis();
		Img<FloatType> out = (Img<FloatType>) ops.run("slim.fitRLD", null, in, param, lifetimeAxis, roi, SlimOps.SQUARE_KERNEL_3);
		System.out.println("RLD binning finished in " + (System.currentTimeMillis() - ms) + " ms");
		param.paramRA = out;

		// 3 parameter layers
		vMin[0] = 0;
		vMax[0] = 2;
		float[] act = getRandPos(Views.interval(out, vMin, vMax), NSAMPLE);
		float[] exp = { 1.7686111f, 3.8147495f, 0.17224349f, 5.990236f, 0.19115955f };
		Assert.assertArrayEquals(exp, act, TOLERANCE);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMLAFitImg() {
		// prerequisite
		if (param.paramRA == null) {
			param.paramRA = (Img<FloatType>) ops.run("slim.fitRLD", null, in, param, lifetimeAxis);
		}

		long ms = System.currentTimeMillis();
		Img<FloatType> out = (Img<FloatType>) ops.run("slim.fitMLA", null, in, param, lifetimeAxis);
		System.out.println("MLA finished in " + (System.currentTimeMillis() - ms) + " ms");
		param.paramRA = null;

		// 3 parameter layers
		vMin[0] = 0;
		vMax[0] = 2;
		float[] act = getRandPos(Views.interval(out, vMin, vMax), NSAMPLE);
		float[] exp = { 2.8199558f, 2.1738043f, 0.15078613f, 5.6381326f, 0.18440692f };
		Assert.assertArrayEquals(exp, act, TOLERANCE);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPhasorFitImg() {
		long ms = System.currentTimeMillis();
		Img<FloatType> out = (Img<FloatType>) ops.run("slim.fitPhasor", null, in, param, lifetimeAxis, roi);
		System.out.println("Phasor finished in " + (System.currentTimeMillis() - ms) + " ms");

		// 6 parameter layers
		vMin[0] = 0;
		vMax[0] = 5;
		float[] act = getRandPos(Views.interval(out, vMin, vMax), NSAMPLE);
		float[] exp = { 0, 0.17804292f, 0.41997245f, 0.18927118f, 0.39349627f };
		Assert.assertArrayEquals(exp, act, TOLERANCE);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGlobalFitImg() {
		long ms = System.currentTimeMillis();
		Img<FloatType> out = (Img<FloatType>) ops.run("slim.fitGlobal", null, in, param, lifetimeAxis, roi);
		System.out.println("Global fit finished in " + (System.currentTimeMillis() - ms) + " ms");

		// 6 parameter layers
		vMin[0] = 0;
		vMax[0] = 2;
		float[] act = getRandPos(Views.interval(out, vMin, vMax), NSAMPLE);
		float[] exp = { 1.2399514f, 0.7768593f, 0.16449152f, 6.9181957f, 0.16449152f };
		Assert.assertArrayEquals(exp, act, TOLERANCE);
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
}
