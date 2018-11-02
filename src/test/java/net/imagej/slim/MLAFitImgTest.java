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

import org.junit.BeforeClass;
import org.junit.Test;
import org.scijava.Context;

import io.scif.img.ImgOpener;
import io.scif.lifesci.SDTFormat;
import io.scif.lifesci.SDTFormat.Metadata;
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
public class MLAFitImgTest extends AbstractOpTest {

	private static Img<UnsignedShortType> in;

	private static Img<FloatType> out;

	private static FitParams param;

	private static long[] min, max;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void createImages() throws IOException {
		Reader r = new SDTFormat.Reader();
		r.setContext(new Context());
		r.setSource(new File("input.sdt"));
//		Metadata metadata = r.getMetadata();
//		io.scif.Metadata metadata2 = new ImgOpener().openImgs(r).get(0).getMetadata();
		in = (Img<UnsignedShortType>) new ImgOpener().openImgs(r).get(0).getImg();
		r.close();

		param = new FitParams();
		param.chisq_delta = 0.0001f;
		param.chisq_percent = 95;
		param.chisq_target = 1;
		param.fitStart = 9;
		param.fitEnd = 20;
		param.fitFunc = FitFunc.GCI_MULTIEXP_TAU;
		param.noise = NoiseType.NOISE_POISSON_FIT;
		param.param = new float[3];// { 0, 1059597.1f, 0.2f };
		param.paramFree = new boolean[] { true, true, true };
		param.restrain = RestrainType.ECF_RESTRAIN_DEFAULT;
		param.xInc = 0.195f;

		min = new long[]{ 0, 40, 40, 10  };
		max = new long[]{ 63, 87, 87, 15 };
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testRLDFitImgDemo() {
		//////////////////////////
		System.out.println(ops.help("slim.mlaFit"));
		//////////////////////////

		param.paramRA = null;
		min[0] = 0;
		max[0] = 63;

		RealMask roi = null;
		RectangleShape binningShape = null;
		int[] binningAxes = null;

		roi = new OpenWritableBox(new double[]{ min[1] - 1, min[2] - 1, min[3] - 1 }, new double[]{ max[1] + 1, max[2] + 1, max[3] + 1 });
//		binningShape = new RectangleShape(1, true); binningAxes = new int[] { 1, 2 };

//		IntervalView<UnsignedShortType> inView = Views.interval(in, min, max);
//		inView = Views.permute(inView, 0, 2);
//		inView = Views.permute(inView, 0, 1);
//		ImageJFunctions.show( inView );
		System.out.println("start");
		long ms = System.currentTimeMillis();
		out = (Img<FloatType>) ops.run("slim.fitRLD", out, in, param, 0, roi, binningShape, binningAxes);
//		out = (Img<FloatType>) ops.run(DefaultFitRAI.RLDFitRAI.class, out, in, param, 0, roi, binningShape, binningAxes);
		System.out.println(System.currentTimeMillis() - ms);
//		for (int i = 0; i < 3; i++) {
//			min[0] = max[0] = i;
//			IntervalView<FloatType> rsltView = Views.interval(out, min, max);
//			rsltView = Views.permute(rsltView, 0, 2);
//			rsltView = Views.permute(rsltView, 0, 1);
//			ImageJFunctions.show( rsltView );
//		}
//		while (true);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMLAFitImg() {
		param.paramRA = out;
		min[0] = 0;
		max[0] = 63;

		RealMask roi = null;
		RectangleShape binningShape = null;
		int[] binningAxes = null;

		roi = new OpenWritableBox(new double[]{ min[1] - 1, min[2] - 1, min[3] - 1 }, new double[]{ max[1] + 1, max[2] + 1, max[3] + 1 });
		binningShape = new RectangleShape(1, true); binningAxes = new int[] { 1, 2 };

		IntervalView<UnsignedShortType> inView = Views.interval(in, min, max);
		inView = Views.permute(inView, 0, 2);
		inView = Views.permute(inView, 0, 1);
		ImageJFunctions.show( inView );
		System.out.println("start");
		long ms = System.currentTimeMillis();
		out = (Img<FloatType>) ops.run(DefaultFitRAI.MLAFitRAI.class, out, in, param, 0, roi, binningShape, binningAxes);
		System.out.println(System.currentTimeMillis() - ms);

		for (int i = 0; i < 3; i++) {
			min[0] = max[0] = i;
			IntervalView<FloatType> rsltView = Views.interval(out, min, max);
			rsltView = Views.permute(rsltView, 0, 2);
			rsltView = Views.permute(rsltView, 0, 1);
			ImageJFunctions.show( rsltView );
		}
//		while (true);
//		//		trans = ImgView.wrap(Views.interval(trans, new long[]{ 0, 60, 60, 12  },
//		//		new long[]{ 63, 67, 67, 14 }), trans.factory());
//		//trans = ImgView.wrap(Views.interval(trans, new long[]{ 0, 0, 0, 12  },
//		//		new long[]{ 63, 127, 127, 12 }), trans.factory());
//		
//		//ImageJFunctions.show(trans);
//		System.out.println("start");
//		long ms = System.currentTimeMillis();
//		//ops.run(DefaultFitRAI.RLDFitRAI.class, null, trans, p, 0, new RectangleShape(1, true), new int[] { 1, 2, 3 });
//		//, new RectangleShape(1, true), new int[] { 1, 2 }
//		Img<FloatType> rslt = (Img<FloatType>) ops.run(DefaultFitRAI.MLAFitRAI.class, out, in, param, 0, new OpenWritableBox(new double[]{ 59, 59, 11  }, new double[]{ 68, 68, 15 }));
//		//, new OpenWritableBox(new double[]{ 59, 59, 11  }, new double[]{ 68, 68, 15 })
//		System.out.println(System.currentTimeMillis() - ms);
//		IntervalView<FloatType> interval = Views.interval(rslt, new long[]{ 0, 60, 60, 12  }, new long[]{ 0, 67, 67, 14 });
//		interval = Views.permute(interval, 0, 2);
//		interval = Views.permute(interval, 0, 1);
////		ImageJFunctions.show( interval );
	}
}
