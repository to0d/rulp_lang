package alpha.rulp.utils;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import alpha.rulp.lang.IRAtom;

public class AttributeUtil {

	public static void addAttribute(List<IRAtom> attributeList, String key) {

		if (containAttribute(attributeList, key)) {
			return;
		}

		attributeList.add(RulpUtil.toAtom(key));
		Collections.sort(attributeList, (a1, a2) -> {
			return a1.getName().compareTo(a2.getName());
		});
	}

	public static boolean containAttribute(List<IRAtom> attributeList, String key) {

		for (IRAtom attr : attributeList) {
			if (attr.getName().equals(key)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isTrue(Boolean b) {
		return b != null && b == true;
	}

	public static IRAtom removeAttribute(List<IRAtom> attributeList, String key) {

		Iterator<IRAtom> it = attributeList.iterator();
		while (it.hasNext()) {
			IRAtom attr = it.next();
			if (attr.getName().equals(key)) {
				it.remove();
				return attr;
			}
		}

		return null;
	}

	public static void setAttribute(List<IRAtom> attributeList, String attr, boolean b) {

		if (b) {
			addAttribute(attributeList, attr);
		} else {
			removeAttribute(attributeList, attr);
		}
	}

}
