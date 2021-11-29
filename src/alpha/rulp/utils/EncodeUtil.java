package alpha.rulp.utils;

public class EncodeUtil {

	public final static int INT_MASK_0_BYTE = 0xff;

	public final static int LENGTH_INT = 4;

	public final static int LENGTH_LONG = 8;

	public final static long LONG_MASK_0_BYTE = 0xff;

	public static String convertBytesToHexString(byte[] b, int pos, int len) {

		StringBuilder sb = new StringBuilder();

		for (int i = pos; i < pos + len; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}

			sb.append(hex.toUpperCase());
		}

		return sb.toString();
	}

	public static int encode(double value, byte[] buf, int offset) {
		Double.doubleToLongBits(value);
		return encode(Double.doubleToLongBits(value), buf, offset);
	}

	public static int encode(float value, byte[] buf, int offset) {

		return encode(Float.floatToRawIntBits(value), buf, offset);
	}

	public static int encode(int value, byte[] buf, int offset) {

		buf[offset++] = (byte) ((value >>> 24) & INT_MASK_0_BYTE);
		buf[offset++] = (byte) ((value >>> 16) & INT_MASK_0_BYTE);
		buf[offset++] = (byte) ((value >>> 8) & INT_MASK_0_BYTE);
		buf[offset++] = (byte) ((value >>> 0) & INT_MASK_0_BYTE);

		return LENGTH_INT;
	}

	public static int encode(long value, byte[] buf, int offset) {

		buf[offset++] = (byte) ((value >>> 56) & LONG_MASK_0_BYTE);
		buf[offset++] = (byte) ((value >>> 48) & LONG_MASK_0_BYTE);
		buf[offset++] = (byte) ((value >>> 40) & LONG_MASK_0_BYTE);
		buf[offset++] = (byte) ((value >>> 32) & LONG_MASK_0_BYTE);
		buf[offset++] = (byte) ((value >>> 24) & LONG_MASK_0_BYTE);
		buf[offset++] = (byte) ((value >>> 16) & LONG_MASK_0_BYTE);
		buf[offset++] = (byte) ((value >>> 8) & LONG_MASK_0_BYTE);
		buf[offset++] = (byte) ((value >>> 0) & LONG_MASK_0_BYTE);

		return LENGTH_LONG;
	}
}
