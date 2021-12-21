package alpha.rulp.utils;

import alpha.rulp.lang.IRObject;

public class AttributeUtil {

	public static String toValidAttribute(String attr) {

		if (attr == null) {
			return null;
		}

		attr = attr.trim();
		if (attr.isEmpty()) {
			return null;
		}

		return attr;
	}

	public static boolean containAttribute(IRObject obj, String attr) {
		return obj.containAttribute(attr);
	}

}
