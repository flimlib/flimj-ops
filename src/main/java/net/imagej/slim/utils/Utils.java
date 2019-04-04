package net.imagej.slim.utils;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;

public class Utils {
	// allocates space for an array if null or incorrect size
	public static float[] reallocIfWeird(float[] arr, int len) {
		return (arr == null || arr.length < len) ? new float[len] : arr;
	}

	public static <I extends RealType<I>> float[] ii2FloatArr(IterableInterval<I> trans, int start, int len, float[] transBuffer) {
		transBuffer = reallocIfWeird(transBuffer, len);
		Cursor<I> cur = null;
		if (trans != null) {
			cur = trans.cursor();
			cur.jumpFwd(start);
		}
		// assume the access order of trans is sequential in time
		for (int i = 0; i < len; i++)
			transBuffer[i] = cur != null ? cur.next().getRealFloat() : 0;
		return transBuffer;
	}
}
