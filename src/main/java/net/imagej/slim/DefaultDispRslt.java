package net.imagej.slim;

import java.util.LinkedList;
import java.util.List;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.util.ColorRGB;

import net.imagej.slim.SlimOps.DispRslt;
import net.imagej.slim.SlimOps.PseudocolorOp;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.converter.RealLUTConverter;
import net.imglib2.display.ColorTable;
import net.imglib2.display.ColorTable8;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
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
		private ColorTable lut = tri2();

		private RandomAccessibleInterval<FloatType> hRaw;
		private RandomAccessibleInterval<FloatType> bRaw;

		@Override
		public void initialize() {
			super.initialize();
			final FitResults rslt = in();
			List<RandomAccessibleInterval<FloatType>> hRaws = new LinkedList<>();
			// List<RandomAccessibleInterval<FloatType>> bRaws = new LinkedList<>();
			int nComp = (int) (rslt.paramMap.dimension(rslt.ltAxis) - 1) / 2;
			for (int c = 0; c < nComp; c++) {
				hRaws.add(Views.hyperSlice(rslt.paramMap, rslt.ltAxis, c * 2 + 2));
				// bRaws.add(Views.hyperSlice(rslt.paramMap, rslt.ltAxis, c * 2 + 1));
			}
			hRaws.add(averageTau(rslt));
			hRaw = ops().transform().stackView(hRaws);
			// bRaw = ops().transform().stackView(bRaws);
			bRaw = rslt.intensityMap;
			bRaw = rslt.ltAxis <= 0 ? ops().transform().permuteView(bRaw, 0, 1) : bRaw;
			bRaw = rslt.ltAxis <= 1 ? ops().transform().permuteView(bRaw, 1, 2) : bRaw;
			// min, max = 20%, 80%
			IterableInterval<FloatType> hRawII = Views.iterable(hRaw);
			if (cMin == null) {
				cMin = ops().stats().mean(hRawII).getRealFloat() * 0.2f;
				System.out.println("color_min automatically set to " + cMin);
			}
			if (cMax == null) {
				cMax = ops().stats().percentile(hRawII, 95).getRealFloat();
				System.out.println("color_max automatically set to " + cMax);
			}
			// min, max = 0%, 99.5%
			IterableInterval<FloatType> bRawII = Views.iterable(rslt.intensityMap);
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
		public Img<ARGBType> calculate(FitResults rslt) {
			// RealLUTConverter<FloatType> hConverter = new RealLUTConverter<>(cMax, cMin, lut);
			RealLUTConverter<FloatType> hConverter = new RealLUTConverter<>(cMin, cMax, lut);
			RandomAccessibleInterval<ARGBType> hImg = Converters.convert(hRaw, hConverter, new ARGBType());

			Img<ARGBType> colored = ops().create().img(hImg);
			Cursor<ARGBType> csr = colored.localizingCursor();
			RandomAccess<FloatType> bRA = bRaw.randomAccess();
			RandomAccess<ARGBType> hRA = hImg.randomAccess();
			while (csr.hasNext()) {
				csr.fwd();
				bRA.setPosition(csr); bRA.setPosition(0, 2);
				hRA.setPosition(csr);
				float b = Math.min(Math.max(bRA.get().get() - bMin, 0) / (bMax - bMin), 1);
				// System.out.print(b + " ");
				ARGBType h = hRA.get();
				// b = 1;
				h.mul(b);

				csr.get().set(h);
			}
			System.out.println();

			return colored;
		}

		private static RandomAccessibleInterval<FloatType> averageTau(FitResults rslt) {
			RandomAccessibleInterval<FloatType> paramMap = rslt.paramMap;
			int nComp = (int) (paramMap.dimension(rslt.ltAxis) - 1) / 2;
			long[] dim = new long[paramMap.numDimensions() - 1];
			Views.hyperSlice(paramMap, rslt.ltAxis, 0).dimensions(dim);
			Img<FloatType> tauM = ArrayImgs.floats(dim);
			Img<FloatType> tauASum = ArrayImgs.floats(dim);

			Cursor<FloatType> csr = tauM.localizingCursor();
			RandomAccess<FloatType> tauASumRA = tauASum.randomAccess();
			// tauM = sum(a_i * tau_i ^ 2), tauASum = sum(a_j * tau_j)
			for (int c = 0; c < nComp; c++) {
				RandomAccessibleInterval<FloatType> A = Views.hyperSlice(rslt.paramMap, rslt.ltAxis, c * 2 + 1);
				RandomAccess<FloatType> aRA = A.randomAccess();
				RandomAccessibleInterval<FloatType> Tau = Views.hyperSlice(rslt.paramMap, rslt.ltAxis, c * 2 + 2);
				RandomAccess<FloatType> tRA = Tau.randomAccess();
				csr.reset();
				while (csr.hasNext()) {
					csr.fwd();
					aRA.setPosition(csr);
					tRA.setPosition(csr);
					float a = aRA.get().getRealFloat();
					float tau = tRA.get().getRealFloat();
					tauASumRA.setPosition(csr);
					tauASumRA.get().add(new FloatType(a * tau));
					csr.get().add(new FloatType(a * tau * tau));
					// csr.get().add(new FloatType(tau));
				}
			}
			csr.reset();
			// tauM = sum(a_i * tau_i ^ 2) / sum(a_j * tau_j) = sum(tau_i * (a_i * tau_i / sum(a_j * tau_j)))
			while (csr.hasNext()) {
				csr.fwd();
				tauASumRA.setPosition(csr);
				csr.get().div(tauASumRA.get());
			}
			return tauM;
		}
	}

	private static ColorTable8 tri2() {
		final byte[] r = new byte[256], g = new byte[256], b = new byte[256];
		final int[] c = new int[] {0,0,0,255,3,0,255,6,0,255,9,0,255,12,0,255,15,0,255,18,0,255,21,0,255,24,0,255,27,0,255,30,0,255,33,0,255,36,0,255,39,0,255,42,0,255,45,0,255,48,0,255,51,0,255,54,0,255,57,0,255,60,0,255,63,0,255,66,0,255,69,0,255,72,0,255,75,0,255,78,0,255,81,0,255,84,0,255,87,0,255,90,0,255,93,0,255,96,0,255,99,0,255,102,0,255,105,0,255,108,0,255,111,0,255,114,0,255,117,0,255,120,0,255,123,0,255,126,0,255,129,0,255,132,0,255,135,0,255,138,0,255,141,0,255,144,0,255,147,0,255,150,0,255,153,0,255,156,0,255,159,0,255,162,0,255,165,0,255,168,0,255,171,0,255,174,0,255,177,0,255,180,0,255,183,0,255,186,0,255,189,0,255,192,0,255,195,0,255,198,0,255,201,0,255,204,0,255,207,0,255,210,0,255,213,0,255,216,0,255,219,0,255,222,0,255,225,0,255,228,0,255,231,0,255,234,0,255,237,0,255,240,0,255,243,0,255,246,0,255,249,0,255,252,0,255,255,0,252,255,3,249,255,6,246,255,9,243,255,12,240,255,15,237,255,18,234,255,21,231,255,24,228,255,27,225,255,30,222,255,33,219,255,36,216,255,39,213,255,42,210,255,45,207,255,48,204,255,51,201,255,54,198,255,57,195,255,60,192,255,63,189,255,66,186,255,69,183,255,72,180,255,75,177,255,78,174,255,81,171,255,84,168,255,87,165,255,90,162,255,93,159,255,96,156,255,99,153,255,102,150,255,105,147,255,108,144,255,111,141,255,114,138,255,117,135,255,120,132,255,123,129,255,126,126,255,129,123,255,132,120,255,135,117,255,138,114,255,141,111,255,144,108,255,147,105,255,150,102,255,153,99,255,156,96,255,159,93,255,162,90,255,165,87,255,168,84,255,171,81,255,174,78,255,177,75,255,180,72,255,183,69,255,186,66,255,189,63,255,192,60,255,195,57,255,198,54,255,201,51,255,204,48,255,207,45,255,210,42,255,213,39,255,216,36,255,219,33,255,222,30,255,225,27,255,228,24,255,231,21,255,234,18,255,237,15,255,240,12,255,243,9,255,246,6,255,249,3,255,252,0,255,255,0,252,255,0,249,255,0,246,255,0,243,255,0,240,255,0,237,255,0,234,255,0,231,255,0,228,255,0,225,255,0,222,255,0,219,255,0,216,255,0,213,255,0,210,255,0,207,255,0,204,255,0,201,255,0,198,255,0,195,255,0,192,255,0,189,255,0,186,255,0,183,255,0,180,255,0,177,255,0,174,255,0,171,255,0,168,255,0,165,255,0,162,255,0,159,255,0,156,255,0,153,255,0,150,255,0,147,255,0,144,255,0,141,255,0,138,255,0,135,255,0,132,255,0,129,255,0,126,255,0,123,255,0,120,255,0,117,255,0,114,255,0,111,255,0,108,255,0,105,255,0,102,255,0,99,255,0,96,255,0,93,255,0,90,255,0,87,255,0,84,255,0,81,255,0,78,255,0,75,255,0,72,255,0,69,255,0,66,255,0,63,255,0,60,255,0,57,255,0,54,255,0,51,255,0,48,255,0,45,255,0,42,255,0,39,255,0,36,255,0,33,255,0,30,255,0,27,255,0,24,255,0,21,255,0,18,255,0,15,255,0,12,255,0,9,255,0,6,255,0,3,255,0,0,255,};
		// for (int i = 1; i < 256; i++) {
		// 	final ColorRGB c = ColorRGB.fromHSVColor(i / 255d * 240d / 360d, 1d, 1d);
		// 	r[i] = (byte) c.getRed();
		// 	g[i] = (byte) c.getGreen();
		// 	b[i] = (byte) c.getBlue();
		// }
		for (int i = 0; i < 256; i++) {
			int idx = i * 3;
			r[i] = (byte) c[idx];
			g[i] = (byte) c[idx + 1];
			b[i] = (byte) c[idx + 2];
		}
		return new ColorTable8(r, g, b);
	}

	private static ColorTable8 spci() {
		final byte[] r = new byte[256], g = new byte[256], b = new byte[256];
		for (int i = 0; i < 256; i++) {
			final ColorRGB c = ColorRGB.fromHSVColor((i / 255d * 200d + 20) / 360d, 1d, 1d);
			r[i] = (byte) c.getRed();
			g[i] = (byte) c.getGreen();
			b[i] = (byte) c.getBlue();
		}
		return new ColorTable8(r, g, b);
	}
}