package alpha.rulp.ximpl.lang;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import alpha.rulp.lang.IRObject;
import alpha.rulp.utils.AttributeUtil;

public abstract class AbsObject implements IRObject {

	protected List<String> attributeList = null;

	@Override
	public void addAttribute(String attr) {

		if ((attr = AttributeUtil.toValidAttribute(attr)) == null) {
			return;
		}

		if (attributeList == null) {
			attributeList = new LinkedList<>();
		} else if (attributeList.contains(attr)) {
			return;
		}

		attributeList.add(attr);
		Collections.sort(attributeList);
	}

	@Override
	public boolean containAttribute(String attr) {

		if (attributeList == null) {
			return false;
		}

		if ((attr = AttributeUtil.toValidAttribute(attr)) == null) {
			return false;
		}

		return attributeList.contains(attr);
	}

	@Override
	public List<String> getAttributeList() {
		return attributeList;
	}

	@Override
	public boolean removeAttribute(String attr) {

		if (attributeList == null) {
			return false;
		}

		if ((attr = AttributeUtil.toValidAttribute(attr)) == null) {
			return false;
		}

		boolean rc = false;

		Iterator<String> it = attributeList.iterator();
		while (it.hasNext()) {
			if (it.next().equals(attr)) {
				it.remove();
				rc = true;
				break;
			}
		}

		if (rc && attributeList.isEmpty()) {
			attributeList = null;
		}

		return rc;
	}

	@Override
	public String toString() {
		return asString();
	}
}
