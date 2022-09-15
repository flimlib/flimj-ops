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

import java.io.IOException;
import org.scijava.Context;
import io.scif.img.ImgOpener;
import io.scif.lifesci.SDTFormat;
import io.scif.lifesci.SDTFormat.Reader;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.geom.real.OpenWritableBox;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;
import org.scijava.io.location.FileLocation;

/**
 * Demonstrates the uses of {@link FlimOps} ops.
 * 
 * @author Dasong Gao
 */
public class Demo {

	private static void sleep20s() {
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static <I extends RealType<I>> void showResults(RandomAccessibleInterval<I> out) {
		long[] vMin = new long[3];
		long[] vMax = new long[3];
		out.min(vMin);
		out.max(vMax);
		for (int i = 0; i <= out.max(0); i++) {
			vMin[0] = vMax[0] = i;
			IntervalView<I> rsltView = Views.interval(out, vMin, vMax);
			rsltView = Views.permute(rsltView, 0, 2);
			rsltView = Views.permute(rsltView, 0, 1);
			ImageJFunctions.show( rsltView );
		}
	}

	public static void main(String[] args) throws IOException {
		Context ctx = new Context();
		OpService ops = ctx.service(OpService.class);
		
		FitTest.init();
		RealMask roi = new OpenWritableBox(new double[] { 49 - 1, 18 - 1 }, new double[] { 57 + 1, 24 + 1 });
		roi = new OpenWritableBox(new double[] { 55 - 1, 24 - 1 }, new double[] { 57 + 1, 24 + 1 });
		roi = null;
		Reader r = new SDTFormat.Reader();
		// io.scif.formats.ICSFormat.Reader r = new io.scif.formats.ICSFormat.Reader();
		r.setContext(new Context());
		r.setSource(new FileLocation("test_files/test2.sdt"));
		// r.setSource(".../Csarseven.ics");
		FitTest.param = new FitParams<UnsignedShortType>();
		FitTest.param.ltAxis = 0;
		FitTest.param.xInc = 10.006715f / 256;
		FitTest.param.transMap = (Img<UnsignedShortType>) new ImgOpener().openImgs(r).get(0).getImg();
		// FitTest.param.xInc = 12.5f / 64;
		// FitTest.param.transMap = in;
		// FitTest.param.getChisqMap = true;
		// FitTest.param.getResidualsMap = true;
		// FitTest.param.FitTest.paramFree = new boolean[] { false };
		// FitTest.param.FitTest.param = new float[] { 1.450f, 9.233f, 1.054f, 3.078f, 0.7027f };
		// FitTest.param.dropBad = true;
		// FitTest.param.iThresh = 90f;
		FitTest.param.iThreshPercent = 10;
		FitTest.param.nComp = 2;
		FitTest.param.chisq_target = 0;
		
		long ms = System.currentTimeMillis();
		// FitTest.param.dropBad = false;
		Img<DoubleType> knl = FlimOps.SQUARE_KERNEL_3;
		// knl = null;
		// FitTest.param.fitStart = 40;
		// FitTest.param.FitTest.param = new float[] { 0f, 25f, 0f };
		// FitTest.param.FitTest.paramMap = ((FitResults) ops.run("flim.fitRLD", FitTest.param, roi, knl)).FitTest.paramMap;
		// FitTest.param.fitStart = 41;
		// FitTest.param.dropBad = true;
		FitResults out = (FitResults) ops.run("flim.fitLMA", FitTest.param, roi, knl);
		System.out.println("Finished in " + (System.currentTimeMillis() - ms) + " ms");
		// System.out.println(ops.stats().min((IterableInterval)out.retCodeMap));
		// Demo.showResults(out.FitTest.paramMap);
		// ImageJFunctions.show(out.residualsMap);
		
		// input
		// ImageJFunctions.show( (RandomAccessibleInterval<ARGBType>)  ops.run("flim.showPseudocolor", out, 0.2f, 2.5f) );
		
		// test2
		// ImageJFunctions.show( (RandomAccessibleInterval<ARGBType>)  ops.run("flim.showPseudocolor", out, 0.821f, 1.184f) );
		// ImageJFunctions.show( (RandomAccessibleInterval<ARGBType>)  ops.run("flim.showPseudocolor", out, 0.6f, 1.35f) );
		// ImageJFunctions.show( (RandomAccessibleInterval<ARGBType>)  ops.run("flim.showPseudocolor", out, 0.6f, 1.5f) );
		ImageJFunctions.show( (RandomAccessibleInterval<ARGBType>)  ops.run("flim.showPseudocolor", out, 0.0178, 2.695f) );
		
		// // test
		// // ImageJFunctions.show( (RandomAccessibleInterval<ARGBType>)  ops.run("flim.showPseudocolor", out, 0.5f, 2.323f) );
		Demo.sleep20s();
	}
}
