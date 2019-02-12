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

import java.io.IOException;

import net.imagej.ops.convert.RealTypeConverter;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

/**
 * Demonstrates the uses of {@link RealTypeConverter} ops.
 * 
 * @author Dasong Gao
 */
public class Demo extends FitTest {
	@SuppressWarnings("unchecked")
	public static void init() throws IOException {
		FitTest.init();
	}

	@SuppressWarnings("unchecked")
	public void RLDFitImgDemo() {
		Img<FloatType> out = (Img<FloatType>) ops.run("slim.fitRLD", null, in,
				param, lifetimeAxis, roi, binningShape, binningAxes);
		showResults(out);
	}

	@SuppressWarnings("unchecked")
	public void MLAFitImgDemo() {
		Img<FloatType> out = (Img<FloatType>) ops.run("slim.fitMLA", null, in,
				param, lifetimeAxis, roi, binningShape, binningAxes);
		showResults(out);
	}

	@SuppressWarnings("unchecked")
	public void PhasorFitImgDemo() {
		Img<FloatType> out = (Img<FloatType>) ops.run("slim.fitPhasor", null, in,
				param, lifetimeAxis, roi, binningShape, binningAxes);
		showResults(out);
	}

	private static void showResults(RandomAccessibleInterval<FloatType> out) {
		for (int i = 0; i < out.max(lifetimeAxis); i++) {
			vMin[0] = vMax[0] = i;
			IntervalView<FloatType> rsltView = Views.interval(out, vMin, vMax);
			rsltView = Views.permute(rsltView, 0, 2);
			rsltView = Views.permute(rsltView, 0, 1);
			ImageJFunctions.show( rsltView );
		}
	}
}
