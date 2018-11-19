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
 * Tests {@link ConvertIIs} + {@link RealTypeConverter} ops.
 * 
 * @author Dasong Gao
 */
public class FitTest extends AbstractOpTest {

	private static Img<UnsignedShortType> in;

	private static FitParams param;

	private static int lifetimeAxis;

	private static long[] min, max;

	private static RealMask roi;

	private static RectangleShape binningShape;

	private static int[] binningAxes;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void createImages() throws IOException {
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

		min = new long[]{ 0, 40, 40, 10  };
		max = new long[]{ 63, 87, 87, 15 };

		// +/- 1 because those dimensions are the closure of the box
		roi = new OpenWritableBox(
				new double[]{ min[1] - 1, min[2] - 1, min[3] - 1 },
				new double[]{ max[1] + 1, max[2] + 1, max[3] + 1 });

		// radius one, without center
		binningShape = new RectangleShape(1, true);
		binningAxes = new int[] { 1, 2 };

//		IntervalView<UnsignedShortType> inView = Views.interval(in, min, max);
//		inView = Views.permute(inView, 0, 2);
//		inView = Views.permute(inView, 0, 1);
//		ImageJFunctions.show( inView );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testRLDFitImgDemo() {
		System.out.println("start");
		long ms = System.currentTimeMillis();
		Img<FloatType> out = null;
		out = (Img<FloatType>) ops.run("slim.fitRLD", out, in,
				param, lifetimeAxis, roi, binningShape, binningAxes);
		System.out.println(System.currentTimeMillis() - ms);
		param.paramRA = out;

//		long[] vMin = FitTest.min.clone();
//		long[] vMax = FitTest.max.clone();
//		for (int i = 0; i < 3; i++) {
//			vMin[0] = vMax[0] = i;
//			IntervalView<FloatType> rsltView = Views.interval(out, vMin, vMax);
//			rsltView = Views.permute(rsltView, 0, 2);
//			rsltView = Views.permute(rsltView, 0, 1);
//			ImageJFunctions.show( rsltView );
//		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMLAFitImg() {
		System.out.println("start");
		long ms = System.currentTimeMillis();
		Img<FloatType> out = null;
		out = (Img<FloatType>) ops.run("slim.fitMLA", out, in,
				param, lifetimeAxis, roi, binningShape, binningAxes);
		System.out.println(System.currentTimeMillis() - ms);
		param.paramRA = null;

//		long[] vMin = FitTest.min.clone();
//		long[] vMax = FitTest.max.clone();
//		for (int i = 0; i < 3; i++) {
//			vMin[0] = vMax[0] = i;
//			IntervalView<FloatType> rsltView = Views.interval(out, vMin, vMax);
//			rsltView = Views.permute(rsltView, 0, 2);
//			rsltView = Views.permute(rsltView, 0, 1);
//			ImageJFunctions.show( rsltView );
//		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPhasorFitImg() {
		long ms = System.currentTimeMillis();
		Img<FloatType> out = null;
		out = (Img<FloatType>) ops.run("slim.fitPhasor", out, in,
				param, lifetimeAxis, roi, binningShape, binningAxes);
		System.out.println(System.currentTimeMillis() - ms);

//		long[] vMin = FitTest.min.clone();
//		long[] vMax = FitTest.max.clone();
//		long[] dim = new long[10];
//		out.dimensions(dim);
//		System.out.println(Arrays.toString(dim));
//		for (int i = 0; i < 6; i++) {
//			vMin[0] = vMax[0] = i;
//			IntervalView<FloatType> rsltView = Views.interval(out, vMin, vMax);
//			rsltView = Views.permute(rsltView, 0, 2);
//			rsltView = Views.permute(rsltView, 0, 1);
//			ImageJFunctions.show( rsltView );
//		}
//		while (true);
	}
}
