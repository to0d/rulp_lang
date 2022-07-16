package alpha.rulp.string;

import static alpha.rulp.string.Constant.GBK;
import static alpha.rulp.string.Constant.UTF_8;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CharCaster {

	static class BasicCharsetCaster implements ICharsetCaster {

		private String charsetName = null;

		public BasicCharsetCaster(String charsetName) {
			super();
			this.charsetName = charsetName;
		}

		@Override
		public String cast(byte[] buffer, int offset, int len) throws UnsupportedEncodingException {
			return new String(buffer, offset, len, charsetName);
		}

	}

	private static interface ICharsetCaster {
		public String cast(byte[] buffer, int offset, int len) throws UnsupportedEncodingException;
	}

	static Map<String, ICharsetCaster> casterMap = new HashMap<String, ICharsetCaster>();

	private static ICharsetCaster utf8Caster = new BasicCharsetCaster(UTF_8);

	static {
		casterMap.put("utf-8", utf8Caster);
		casterMap.put("windows-1252", utf8Caster);
		casterMap.put("gbk", new BasicCharsetCaster(GBK));
		casterMap.put("gb2312", new BasicCharsetCaster(GBK));
	}

	protected static ICharsetCaster _caster(String charset) {

		ICharsetCaster caster = null;
		if (charset != null) {
			caster = casterMap.get(charset.toLowerCase());
		} else {
			caster = utf8Caster;
		}

		return caster;
	}

	public static String decodeByCharse(String charset, byte[] buffer, int offset, int len)
			throws UnsupportedEncodingException {

		ICharsetCaster caster = _caster(charset);
		if (caster == null) {
			throw new UnsupportedEncodingException("!!!No caster found<" + charset + ">");
		}

		return caster.cast(buffer, offset, len);
	}

	public static boolean hasCharset(String charset) {
		return charset != null && casterMap.containsKey(charset.toLowerCase());
	}
}
