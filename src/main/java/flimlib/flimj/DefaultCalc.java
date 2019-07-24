package flimlib.flimj;

import org.scijava.plugin.Plugin;
import flimlib.flimj.FlimOps.Calc;
import flimlib.flimj.FlimOps.CalcTauMOp;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class DefaultCalc {

	private DefaultCalc() {
		// NB: Prevent instantiation of utility class.
	}

	@Plugin(type = CalcTauMOp.class)
	public static class CalcTauMean extends Calc {

		@Override
		public Img<FloatType> calculate(FitResults rslt) {
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
				RandomAccessibleInterval<FloatType> A =
						Views.hyperSlice(rslt.paramMap, rslt.ltAxis, c * 2 + 1);
				RandomAccess<FloatType> aRA = A.randomAccess();
				RandomAccessibleInterval<FloatType> Tau =
						Views.hyperSlice(rslt.paramMap, rslt.ltAxis, c * 2 + 2);
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
			// tauM = sum(a_i * tau_i ^ 2) / sum(a_j * tau_j) = sum(tau_i * (a_i * tau_i / sum(a_j *
			// tau_j)))
			while (csr.hasNext()) {
				csr.fwd();
				tauASumRA.setPosition(csr);
				csr.get().div(tauASumRA.get());
			}
			return tauM;
		}
	}
}
