package alpha.rulp.utils;

import static alpha.rulp.lang.Constant.A_RECURSIVE;
import static alpha.rulp.lang.Constant.A_THREAD_UNSAFE;

import java.util.List;

import alpha.rulp.lang.IRArray;
import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRFunctionList;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.ximpl.attribute.StableUtil;
import alpha.rulp.ximpl.lang.AbsObject;
import alpha.rulp.ximpl.optimize.NameSet;
import alpha.rulp.ximpl.optimize.OptUtil;

public class AttrUtil {

	private static boolean _isRecursiveFunc(IRFunction func) throws RException {

		if (func.isList()) {

			IRFunctionList funcList = (IRFunctionList) func;
			for (IRFunction childFunc : funcList.getAllFuncList()) {
				if (isRecursive(childFunc)) {
					return true;
				}
			}

			return false;
		}

		return RulpUtil.containFunc(func.getFunBody(), func.getName());
	}

	public static void addAttribute(IRObject obj, String key) throws RException {
		((AbsObject) obj).addAttribute(key);
	}

	public static boolean containAttribute(IRObject obj, String attr) {
		return ((AbsObject) obj).containAttribute(attr);
	}

	public static List<String> getAttributeKeyList(IRObject obj) {
		return ((AbsObject) obj).getAttributeKeyList();
	}

	public static IRObject getAttributeValue(IRObject obj, String key) throws RException {
		return ((AbsObject) obj).getAttributeValue(key);
	}

	public static boolean hasAttributeList(IRObject obj) {
		return ((AbsObject) obj).getAttributeCount() > 0;
	}

	public static boolean isConst(IRObject obj, IRFrame frame) throws RException {

		switch (obj.getType()) {
		case STRING:
		case MACRO:
		case INT:
		case FLOAT:
		case DOUBLE:
		case BOOL:
		case LONG:
		case FACTOR:
		case CONSTANT:
		case NIL:
			return true;

		case CLASS:
			return ((IRClass) obj).isConst();

		case FUNC:
			return !((IRFunction) obj).isList();

		case LIST:
		case EXPR:
			return ((IRList) obj).isConst();

		case ARRAY:
			return ((IRArray) obj).isConst();

		case ATOM:

			String atomName = RulpUtil.asAtom(obj).getName();

			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, atomName);
			// is pure atom
			if (entry == null) {
				return true;
			}

			IRObject entryValue = entry.getObject();
			if (entryValue == null || entryValue.getType() == RType.ATOM) {
				return true;
			}

			return isConst(entryValue, frame);

		default:
			return false;
		}
	}

	public static boolean isRecursive(IRFunction func) throws RException {

		IRObject value = AttrUtil.getAttributeValue(func, A_RECURSIVE);
		if (value != null) {
			return RulpUtil.asBoolean(value).asBoolean();
		}

		boolean rc = _isRecursiveFunc(func);
		AttrUtil.setAttribute(func, A_RECURSIVE, RulpFactory.createBoolean(rc));

		return rc;
	}

	public static boolean isStable(IRObject obj, IRFrame frame) throws RException {
		return StableUtil.isStable(obj, null, frame);
	}

	public static boolean isStableValue(IRIterator<? extends IRObject> it, NameSet nameSet, IRFrame frame)
			throws RException {

		while (it.hasNext()) {
			if (!isStableValue(it.next(), nameSet, frame)) {
				return false;
			}
		}

		return true;
	}

	public static boolean isStableValue(IRObject obj, NameSet nameSet, IRFrame frame) throws RException {

		if (OptUtil.isLocalValue(obj, nameSet)) {
			return true;
		}

		if (StableUtil.isStable(obj, nameSet.newBranch(), frame)) {
			return true;
		}

		return false;
	}

	public static boolean isThreadSafe(IRObject obj, IRFrame frame) throws RException {

		switch (obj.getType()) {
		case FUNC:
		case MACRO:
		case TEMPLATE:
			return true;

		case FACTOR:
			return !containAttribute(obj, A_THREAD_UNSAFE);

		case ATOM:

			String atomName = RulpUtil.asAtom(obj).getName();

			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, atomName);
			// is pure atom
			if (entry == null) {
				return true;
			}

			IRObject entryValue = entry.getObject();
			if (entryValue == null || entryValue.getType() == RType.ATOM) {
				return true;
			}

			return isThreadSafe(entryValue, frame);

		default:
			return false;
		}
	}

	public static void removeAllAttributes(IRObject obj) throws RException {
		((AbsObject) obj).removeAllAttributes();
	}

	public static IRObject removeAttribute(IRObject obj, String attr) throws RException {
		return ((AbsObject) obj).removeAttribute(attr);
	}

	public static void setAttribute(IRObject obj, String key, IRObject value) throws RException {
		((AbsObject) obj).setAttribute(key, value);
	}
}
