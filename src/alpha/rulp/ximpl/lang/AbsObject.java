package alpha.rulp.ximpl.lang;

import static alpha.rulp.lang.Constant.O_Nil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.optimize.OptUtil;

public abstract class AbsObject implements IRObject {

	protected List<String> _attributeKeyList = null;

	protected Map<String, IRObject> attributeMap = null;

	public void addAttribute(String key) throws RException {
		setAttribute(key, null);
	}

	public boolean containAttribute(String key) {

		if (attributeMap == null) {
			return false;
		}

		String attrKey = RulpUtil.toValidAttribute(key);
		if (attrKey == null) {
			return false;
		}

		return attributeMap.containsKey(attrKey);
	}

	public int getAttributeCount() {
		return attributeMap == null ? 0 : attributeMap.size();
	}

	public List<String> getAttributeKeyList() {

		if (_attributeKeyList == null) {

			if (attributeMap == null) {
				return Collections.emptyList();
			}

			_attributeKeyList = new ArrayList<>(attributeMap.keySet());
			Collections.sort(_attributeKeyList);
		}

		return _attributeKeyList;
	}

	public Map<String, IRObject> getAttributeMap() {
		return attributeMap;
	}

	public IRObject getAttributeValue(String key) throws RException {

		if (attributeMap == null) {
			return null;
		}

		String attrKey = RulpUtil.toValidAttribute(key);
		if (attrKey == null) {
			throw new RException("invalid attribute key: " + key);
		}

		return attributeMap.get(key);
	}

	public void removeAllAttributes() throws RException {
		attributeMap = null;
		_attributeKeyList = null;
	}

	public IRObject removeAttribute(String key) throws RException {

		if (attributeMap == null) {
			return null;
		}

		String attrKey = RulpUtil.toValidAttribute(key);
		if (attrKey == null) {
			throw new RException("invalid attribute key: " + key);
		}

		IRObject value = attributeMap.remove(attrKey);
		if (attributeMap.isEmpty()) {
			attributeMap = null;
		}

		if (value != null) {
			_attributeKeyList = null;
		}

		return value;
	}

	public void setAttribute(String key, IRObject value) throws RException {

		String attrKey = RulpUtil.toValidAttribute(key);
		if (attrKey == null) {
			throw new RException("invalid attribute key: " + key);
		}

		if (value == null) {
			value = O_Nil;
		} else if (value.getType() != RType.ATOM && !OptUtil.isConstValue(value)) {
			throw new RException("invalid attribute value: " + value);
		}

		if (attributeMap == null) {
			attributeMap = new HashMap<>();
		}

		attributeMap.put(attrKey, value);
		_attributeKeyList = null;
	}

	@Override
	public String toString() {
		return asString();
	}
}
