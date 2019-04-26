package net.imagej.slim;

import java.util.Arrays;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.display.ColorTables;
import net.imagej.slim.SlimOps.DispRslt;
import net.imagej.slim.SlimOps.PseudocolorOp;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.converter.RealLUTConverter;
import net.imglib2.display.ColorTable;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class DefaultDispRslt {

	private DefaultDispRslt() {
		// NB: Prevent instantiation of utility class.
	}

	@Plugin(type = PseudocolorOp.class)
	public static class Pseudocolor extends DispRslt {

		@Parameter(required = false)
		private float[] colorRange;

		@Parameter(required = false)
		private float[] brightnessRange;

		@Parameter(required = false)
		private ColorTable lut = ColorTables.SPECTRUM;

		private RandomAccessibleInterval<FloatType> hRaw;
		private RandomAccessibleInterval<FloatType> bRaw;

		@Override
		public void initialize() {
			super.initialize();
			final FitResults rslt = in();
			hRaw = Views.hyperSlice(rslt.paramMap, rslt.ltAxis, 2);
			bRaw = Views.hyperSlice(rslt.paramMap, rslt.ltAxis, 1);
			if (colorRange == null) {
				colorRange = new float[2];
				IterableInterval<FloatType> hRawII = Views.iterable(hRaw);
				// System.out.println(ops().stats().max(hRawII));
				colorRange[0] = ops().stats().percentile(hRawII, 20).getRealFloat();
				colorRange[1] = ops().stats().percentile(hRawII, 80).getRealFloat();
				colorRange = new float[] {0.7f, 2.5f};
				
				System.out.println(colorRange[0] + ", " + colorRange[1]);
			}
		}

		@Override
		public RandomAccessibleInterval<ARGBType> calculate(FitResults rslt) {
			RealLUTConverter<FloatType> hConverter = new RealLUTConverter<>(colorRange[0], colorRange[1], lut);
			RandomAccessibleInterval<ARGBType> hImg = Converters.convert(hRaw, hConverter, new ARGBType());

			Img<ARGBType> colored = ops().create().img(hImg);
			Cursor<ARGBType> csr = colored.localizingCursor();
			RandomAccess<FloatType> bRA = bRaw.randomAccess();
			RandomAccess<ARGBType> hRA = hImg.randomAccess();
			while (csr.hasNext()) {
				csr.fwd();
				bRA.setPosition(csr);
				hRA.setPosition(csr);
				float b = Math.min((bRA.get().get() - brightnessRange[0]) / (brightnessRange[1] - brightnessRange[0]), 1);
				ARGBType h = hRA.get();
				h.mul(b);

				csr.get().set(h);
			}

			return colored;
		}
	}
}