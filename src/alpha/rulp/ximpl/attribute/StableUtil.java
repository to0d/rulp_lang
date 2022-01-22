package alpha.rulp.ximpl.attribute;

import static alpha.rulp.lang.Constant.A_STABLE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.AttrUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.error.RInterrupt;
import alpha.rulp.ximpl.function.XRFunction;
import alpha.rulp.ximpl.function.XRFunctionLambda;
import alpha.rulp.ximpl.function.XRFunctionList;
import alpha.rulp.ximpl.optimize.NameSet;
import alpha.rulp.ximpl.optimize.OptUtil;

public class StableUtil {

	// stable analyze
	// - expression: no external variable in expression, all elements is stable
	// - function: no external variable in function body, all elements is stable

//	private static void _findFunCallee(IRIterator<? extends IRObject> it, Set<String> calleeNames, IRFrame frame)
//			throws RException {
//		while (it.hasNext()) {
//			_findFunCallee(it.next(), calleeNames, frame);
//		}
//	}
//
//	private static void _findFunCallee(IRObject e0, IRExpr expr, Set<String> calleeNames, IRFrame frame)
//			throws RException {
//
//		switch (e0.getType()) {
//		case ATOM:
//			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, RulpUtil.asAtom(e0).getName());
//			if (entry != null) {
//				_findFunCallee(entry.getObject(), expr, calleeNames, frame);
//			} else {
//				_findFunCallee(expr.iterator(), calleeNames, frame);
//			}
//
//			return;
//
//		case FACTOR:
//			_findFunCallee(expr.listIterator(1), calleeNames, frame);
//			return;
//
//		case FUNC:
//			calleeNames.add(RulpUtil.asFunction(e0).getName());
//			_findFunCallee(expr.listIterator(1), calleeNames, frame);
//			return;
//
//		case TEMPLATE:
//		case MACRO:
//		default:
//			return;
//		}
//	}
//
//	private static void _findFunCallee(IRObject obj, Set<String> calleeNames, IRFrame frame) throws RException {
//
//		if (obj == null || obj.getType() != RType.EXPR) {
//			return;
//		}
//
//		IRExpr expr = (IRExpr) obj;
//		if (expr.isEmpty()) {
//			return;
//		}
//
//		_findFunCallee(expr.get(0), expr, calleeNames, frame);
//	}
//
//	public static Set<String> findFunCallee(IRExpr expr, IRFrame frame) throws RException {
//
//		if (expr.isEmpty()) {
//			return Collections.emptySet();
//		}
//
//		Set<String> callee = new HashSet<>();
//		_findFunCallee(expr, callee, frame);
//		return callee;
//	}

	public static boolean isStable(IRObject obj, NameSet nameSet, IRFrame frame) throws RException {

		if (obj == null) {
			return true;
		}

		switch (obj.getType()) {
		case BOOL:
		case DOUBLE:
		case FLOAT:
		case INT:
		case LONG:
		case NATIVE:
		case NIL:
		case STRING:
		case CONSTANT:
			return true;

		case FACTOR:
			return AttrUtil.containAttribute(obj, A_STABLE);

		case ATOM: {

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

			return isStable(entryValue, nameSet, frame);
		}

		case MACRO:
		case TEMPLATE:
		case MEMBER:
		case FRAME:
		case CLASS:
		case INSTANCE:
		case ARRAY:
		case BLOB:
			return false;

		default:

		}

		if (nameSet == null) {
			nameSet = new NameSet();
		}

		return new StableUtil()._isStable(obj, nameSet, frame);
	}

	private Set<XRFunction> assuingFuncs;

	private List<XRFunction> checkingStack;

	private StableUtil() {
	}

	private void _addAssuingFunc(XRFunction func) {

		if (assuingFuncs == null) {
			assuingFuncs = new HashSet<>();
		}

		assuingFuncs.add(func);
	}

//	static int deep = 0;

	private boolean _containAssuingFunc(XRFunction func) {
		return assuingFuncs != null && assuingFuncs.contains(func);
	}

	private boolean _isStable(IRObject obj, NameSet nameSet, IRFrame frame) throws RException {

		if (obj == null) {
			return true;
		}

		switch (obj.getType()) {
		case BOOL:
		case DOUBLE:
		case FLOAT:
		case INT:
		case LONG:
		case NATIVE:
		case NIL:
		case STRING:
		case CONSTANT:
			return true;

		case FACTOR:
			return AttrUtil.containAttribute(obj, A_STABLE);

		case VAR:
			return nameSet.lookupType(RulpUtil.asVar(obj).getName()) != null;

		case ATOM: {

			String atomName = RulpUtil.asAtom(obj).getName();

			// is local atom
			if (nameSet.lookupType(atomName) == RType.VAR) {
				return true;
			}

			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, atomName);

			// is pure atom
			if (entry == null) {
				return true;
			}

			IRObject entryValue = entry.getObject();
			if (entryValue == null || entryValue.getType() == RType.ATOM) {
				return true;
			}

			return _isStable(entryValue, nameSet, frame);
		}

		case EXPR:

			IRExpr expr = (IRExpr) obj;
			if (expr.size() == 0) {
				return true;
			}

			IRObject e0 = expr.get(0);

			// Local defined function
			if (e0.getType() == RType.ATOM && nameSet.lookupType(e0.asString()) == RType.FUNC) {

				// need new branch
				return _isStableList(expr.listIterator(1), nameSet.newBranch(), frame);
			}

			// External defined function
			if (e0.getType() == RType.ATOM) {
				IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, RulpUtil.asAtom(e0).getName());
				if (entry != null && entry.getValue() != null && entry.getValue().getType() == RType.FUNC) {
					return _isStableList(expr.listIterator(0), nameSet, frame);
				}
			}

			// do foreach, let
			if (OptUtil.isNewFrameFactor(e0)) {

				// need new branch
				return _isStableList(expr.listIterator(1), nameSet.newBranch(), frame);
			}

			nameSet.updateExpr(e0, expr);

			return _isStableList(expr.listIterator(0), nameSet, frame);

		case LIST:
			return _isStableList(((IRList) obj).listIterator(0), nameSet, frame);

		case FUNC:

			IRObject attrValue = AttrUtil.getAttributeValue(obj, A_STABLE);
			if (attrValue != null) {
				return RulpUtil.asBoolean(attrValue).asBoolean();
			}

			boolean rc = false;

			if (RulpUtil.isFunctionList(obj)) {
				rc = _isStableFuncList((XRFunctionList) obj, nameSet.newBranch(), frame);

			} else if (((IRFunction) obj).isLambda()) {

				XRFunctionLambda lambda = (XRFunctionLambda) obj;
				rc = StableUtil.isStable(lambda.getFunBody(), null, lambda.getDefineFrame());

			} else {
				rc = _isStableFunc((XRFunction) obj, nameSet.newBranch(), frame);
			}

			// Updating function's stable value
			AttrUtil.setAttribute(obj, A_STABLE, RulpFactory.createBoolean(rc));

			return rc;

		case TEMPLATE:
		case MACRO:
		case MEMBER:
		case FRAME:
		case CLASS:
		case INSTANCE:
		case ARRAY:
		case BLOB:
		default:
			return false;
		}
	}

	private boolean _isStableFunc(XRFunction func, NameSet nameSet, IRFrame frame) throws RException {

		if (checkingStack == null) {
			checkingStack = new ArrayList<>();
		}

		int checkSize = checkingStack.size();

		// assuming
		if (_containAssuingFunc(func)) {
			return true;
		}

		int lastPos = checkingStack.indexOf(func);
		if (lastPos != -1) {

			// recursion self
			if ((lastPos + 1) == checkSize) {
				return true; // not update function's table value
			}

			// recursion cycle detected: A call B, B call C, C call A
			for (int i = (lastPos + 1); i < checkSize; ++i) {

				// Add B and C to A's dep list
				nameSet.addDepFun(func, checkingStack.get(i));
			}

			// jump to B
			throw new RInterrupt(checkingStack.get(lastPos + 1), frame);
		}

		for (IRParaAttr pa : func.getParaAttrs()) {
			nameSet.addVar(pa.getParaName());
		}

		checkingStack.add(func);

		try {

			boolean stable = _isStable(func.getFunBody(), nameSet, frame);

			// assume A is stable, check all dep functions again
			if (stable && nameSet.hasDepFuncs(func)) {

				for (XRFunction depFunc : nameSet.removeDepFun(func)) {

					if (_containAssuingFunc(func)) {
						throw new RException("duplicated assume func: " + func);
					}

					_addAssuingFunc(func);

					try {

						// find one non-stable function
						if (!_isStable(depFunc, nameSet.newBranch(), frame)) {
							stable = false;
							break;
						}

					} finally {
						_removeAssuingFunc(func);
					}

				}
			}

			// Updating dep function list
			if (nameSet.hasDepFuncs(func)) {
				for (XRFunction depFunc : nameSet.removeDepFun(func)) {
					AttrUtil.setAttribute(depFunc, A_STABLE, RulpFactory.createBoolean(stable));
				}
			}

			return stable;

		} catch (RInterrupt e) {

			IRObject fromFunc = e.getFromObject();
			if (fromFunc != func) {
				throw e;
			}

			return true; // return true, will check B later

		} finally {
			checkingStack.remove(checkSize);
		}

	}

	private boolean _isStableFuncList(XRFunctionList func, NameSet nameSet, IRFrame frame) throws RException {

		for (IRFunction childFunc : func.getAllFuncList()) {
			if (!_isStable(childFunc, nameSet, frame)) {
				return false;
			}
		}

		return true;
	}

	private boolean _isStableList(IRIterator<? extends IRObject> it, NameSet nameSet, IRFrame frame) throws RException {

		while (it.hasNext()) {
			if (!_isStable(it.next(), nameSet, frame)) {
				return false;
			}
		}
		return true;
	}

	private void _removeAssuingFunc(XRFunction func) {

		if (assuingFuncs != null) {
			assuingFuncs.remove(func);
		}
	}
}
