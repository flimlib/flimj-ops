package net.imagej.slim;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
		private Float cMin;

		@Parameter(required = false)
		private Float cMax;

		@Parameter(required = false)
		private Float bMin;

		@Parameter(required = false)
		private Float bMax;


		@Parameter(required = false)
		private ColorTable lut = ColorTables.SPECTRUM;

		private RandomAccessibleInterval<FloatType> hRaw;
		private RandomAccessibleInterval<FloatType> bRaw;

		@Override
		public void initialize() {
			super.initialize();
			final FitResults rslt = in();
			List<RandomAccessibleInterval<FloatType>> hRaws = new LinkedList<>();
			List<RandomAccessibleInterval<FloatType>> bRaws = new LinkedList<>();
			int nComp = (int) (rslt.paramMap.dimension(rslt.ltAxis) - 1) / 2;
			for (int c = 0; c < nComp; c++) {
				hRaws.add(Views.hyperSlice(rslt.paramMap, rslt.ltAxis, c * 2 + 2));
				bRaws.add(Views.hyperSlice(rslt.paramMap, rslt.ltAxis, c * 2 + 1));
			}
			hRaw = ops().transform().stackView(hRaws);
			bRaw = ops().transform().stackView(bRaws);
			// min, max = 20%, 80%
			IterableInterval<FloatType> hRawII = Views.iterable(hRaw);
			if (cMin == null) {
				cMin = ops().stats().percentile(hRawII, 20).getRealFloat();
				System.out.println("color_min automatically set to " + cMin);
			}
			if (cMax == null) {
				cMax = ops().stats().percentile(hRawII, 80).getRealFloat();
				System.out.println("color_max automatically set to " + cMin);
			}
			// min, max = 0%, 99.5%
			IterableInterval<FloatType> bRawII = Views.iterable(bRaw);
			if (bMin == null) {
				bMin = new Float(0);
				System.out.println("brightness_min automatically set to 0.0");
			}
			if (bMax == null) {
				bMax = ops().stats().percentile(bRawII, 99.5).getRealFloat();
				System.out.println("brightness_max automatically set to " + bMax);
			}
		}

		@Override
		public RandomAccessibleInterval<ARGBType> calculate(FitResults rslt) {
			RealLUTConverter<FloatType> hConverter = new RealLUTConverter<>(cMin, cMax, lut);
			RandomAccessibleInterval<ARGBType> hImg = Converters.convert(hRaw, hConverter, new ARGBType());

			Img<ARGBType> colored = ops().create().img(hImg);
			Cursor<ARGBType> csr = colored.localizingCursor();
			RandomAccess<FloatType> bRA = bRaw.randomAccess();
			RandomAccess<ARGBType> hRA = hImg.randomAccess();
			while (csr.hasNext()) {
				csr.fwd();
				bRA.setPosition(csr);
				hRA.setPosition(csr);
				float b = Math.min((bRA.get().get() - bMin) / (bMax - bMin), 1);
				ARGBType h = hRA.get();
				h.mul(b);

				csr.get().set(h);
			}

			return colored;
		}
	}
}