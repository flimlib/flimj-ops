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
