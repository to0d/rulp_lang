package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.O_COMPUTE;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.ximpl.optimize.StableUtil.NameSet;

public class OptUtil {

	public static IRExpr _asExpr(IRObject obj) throws RException {

		if (obj == null) {
			return RulpFactory.createExpression();
		}

		if (obj.getType() == RType.EXPR) {
			return (IRExpr) obj;
		} else {
			return RulpFactory.createExpression(O_COMPUTE, obj);
		}
	}

	public static boolean _isLocalValue(IRObject obj, NameSet nameSet) throws RException {

		if (OptUtil.isConstValue(obj)) {
			return true;
		}

		if (obj.getType() == RType.ATOM && nameSet.lookupType(obj.asString()) != null) {
			return true;
		}

		return false;
	}

	public static boolean _isStableValue(IRIterator<? extends IRObject> it, NameSet nameSet, IRFrame frame)
			throws RException {

		while (it.hasNext()) {
			if (!_isStableValue(it.next(), nameSet, frame)) {
				return false;
			}
		}

		return true;
	}

	public static boolean _isStableValue(IRObject obj, NameSet nameSet, IRFrame frame) throws RException {

		if (_isLocalValue(obj, nameSet)) {
			return true;
		}

		if (StableUtil.isStable(obj, nameSet.newBranch(), frame)) {
			return true;
		}

		return false;
	}

	public static boolean isConstValue(IRIterator<? extends IRObject> it) throws RException {

		while (it.hasNext()) {
			if (!isConstValue(it.next())) {
				return false;
			}
		}

		return true;
	}

	public static boolean isConstValue(IRObject obj) throws RException {

		switch (obj.getType()) {
		case BOOL:
		case INT:
		case FLOAT:
		case DOUBLE:
		case CONSTANT:
		case LONG:
		case NIL:
		case STRING:
			return true;

		case LIST:
			return isConstValue(((IRList) obj).iterator());

		default:
			return false;
		}
	}

	public static boolean isFactor(IRObject obj, String name) throws RException {

		if (obj.getType() != RType.ATOM && obj.getType() != RType.FACTOR) {
			return false;
		}

		return obj.asString().equals(name);
	}
}
