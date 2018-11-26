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

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scijava.Context;

import io.scif.img.ImgOpener;
import io.scif.lifesci.SDTFormat;
import io.scif.lifesci.SDTFormat.Reader;
import net.imagej.ops.AbstractOpTest;
import net.imagej.ops.convert.RealTypeConverter;
import net.imagej.ops.convert.imageType.ConvertIIs;
import net.imagej.slim.utils.FitParams;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.geom.real.OpenWritableBox;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
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

	static Img<UnsignedShortType> in;

	static FitParams param;

	static int lifetimeAxis;

	static long[] min, max, vMin, vMax;

	static RealMask roi;

	static RectangleShape binningShape;

	static int[] binningAxes;

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

		// input and output boundaries
		min = new long[]{  0, 40, 40, 10 };
		max = new long[]{ 63, 87, 87, 15 };
		vMin = min.clone();
		vMax = max.clone();

		// +/- 1 because those dimensions are the closure of the box
		roi = new OpenWritableBox(
				new double[]{ min[1] - 1, min[2] - 1, min[3] - 1 },
				new double[]{ max[1] + 1, max[2] + 1, max[3] + 1 });

		// radius one, without center
		binningShape = new RectangleShape(1, true);
		binningAxes = new int[] { 1, 2 };
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testRLDFitImg() {
		long ms = System.currentTimeMillis();
		Img<FloatType> out = (Img<FloatType>) ops.run("slim.fitRLD", null, in,
				param, lifetimeAxis, roi, binningShape, binningAxes);
		System.out.println("RLD finished in " + (System.currentTimeMillis() - ms) + " ms");
		param.paramRA = out;

		// 3 parameter layers
		vMax[0] = 3;
		float[] act = getRandPos(Views.interval(out, vMin, vMax), NSAMPLE);
		float[] exp = { 0.1399244f, 1.784222f, 0.22872002f, 912.638f, 0.2821034f };
		Assert.assertArrayEquals(exp, act, TOLERANCE);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMLAFitImg() {
		// prerequisite
		if (param.paramRA == null) {
			param.paramRA = (Img<FloatType>) ops.run("slim.fitRLD", null, in,
					param, lifetimeAxis, roi, binningShape, binningAxes);
		}

		long ms = System.currentTimeMillis();
		Img<FloatType> out = (Img<FloatType>) ops.run("slim.fitMLA", null, in,
				param, lifetimeAxis, roi, binningShape, binningAxes);
		System.out.println("MLA finished in " + (System.currentTimeMillis() - ms) + " ms");
		param.paramRA = null;

		// 3 parameter layers
		vMax[0] = 3;
		float[] act = getRandPos(Views.interval(out, vMin, vMax), NSAMPLE);
		float[] exp = { 0.59168506f, 2.6430013f, 0.16300254f, 799.6821f, 0.20999064f };
		Assert.assertArrayEquals(exp, act, TOLERANCE);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPhasorFitImg() {
		long ms = System.currentTimeMillis();
		Img<FloatType> out = (Img<FloatType>) ops.run("slim.fitPhasor", null, in,
				param, lifetimeAxis, roi, binningShape, binningAxes);
		System.out.println("Phasor finished in " + (System.currentTimeMillis() - ms) + " ms");

		// 5 parameter layers
		vMax[0] = 5;
		float[] act = getRandPos(Views.interval(out, vMin, vMax), NSAMPLE);
		float[] exp = { 0.0f, 0.21051976f, 0.4112079f, 0.41977262f, 0.32243326f };
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
