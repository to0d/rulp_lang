package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.F_DEFUN;
import static alpha.rulp.lang.Constant.F_DEFVAR;
import static alpha.rulp.lang.Constant.F_E_TRY;
import static alpha.rulp.lang.Constant.F_FOR;
import static alpha.rulp.lang.Constant.F_FOREACH;
import static alpha.rulp.lang.Constant.F_LET;
import static alpha.rulp.lang.Constant.F_LOOP;
import static alpha.rulp.lang.Constant.F_OPT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.error.RInterrupt;
import alpha.rulp.ximpl.function.XRFunction;
import alpha.rulp.ximpl.function.XRFunctionList;

public class StableUtil {

	// stable analyze
	// - expression: no external variable in expression, all elements is stable
	// - function: no external variable in function body, all elements is stable

	public static class NameSet {

		private Set<XRFunction> assuingFuncs;
		private List<XRFunction> checkingStack;
		private Map<XRFunction, List<XRFunction>> funcDepMap;
		private Map<String, RType> localObjTypeMap;
		private NameSet parent = null;

		public NameSet() {
			this.checkingStack = new ArrayList<>();
			this.funcDepMap = new HashMap<>();
			this.assuingFuncs = new HashSet<>();
		}

		public NameSet(NameSet parent) {
			this.parent = parent;
			this.checkingStack = parent.checkingStack;
			this.funcDepMap = parent.funcDepMap;
			this.assuingFuncs = parent.assuingFuncs;
		}

		private Map<String, RType> _buildMap() {

			if (localObjTypeMap == null) {
				localObjTypeMap = new HashMap<>();
				if (parent != null) {
					localObjTypeMap.putAll(parent._findMap());
				}
			}

			return localObjTypeMap;
		}

		private Map<String, RType> _findMap() {

			if (localObjTypeMap != null) {
				return localObjTypeMap;
			}

			return parent == null ? Collections.emptyMap() : parent._findMap();
		}

		public void _listAllUsedVars(IRList list, Set<String> varNames, List<IRAtom> varAtoms) throws RException {

			IRIterator<? extends IRObject> it = list.iterator();
			while (it.hasNext()) {

				IRObject obj = it.next();
				switch (obj.getType()) {
				case ATOM:

					String name = RulpUtil.asAtom(obj).getName();
					if (!varNames.contains(name) && this.lookupType(name) == RType.VAR) {
						varNames.add(name);
						varAtoms.add((IRAtom) obj);
					}

					break;

				case EXPR:
				case LIST:
					_listAllUsedVars((IRList) obj, varNames, varAtoms);
					break;

				}
			}
		}

		public void addDepFun(XRFunction curFun, XRFunction depFunc) {

			List<XRFunction> depFuncs = funcDepMap.get(curFun);
			if (depFuncs == null) {
				depFuncs = new ArrayList<>();
				funcDepMap.put(curFun, depFuncs);
			}

			if (!depFuncs.contains(depFunc)) {
				depFuncs.add(depFunc);
			}
		}

		public void addFunName(String name) {
			_buildMap().put(name, RType.FUNC);
		}

		public void addVar(String name) {
			_buildMap().put(name, RType.VAR);
		}

		public boolean hasDepFuncs(XRFunction curFun) {
			return funcDepMap != null && funcDepMap.containsKey(curFun);
		}

		public List<IRAtom> listAllVars(IRExpr expr) throws RException {

			ArrayList<IRAtom> vars = new ArrayList<>();
			_listAllUsedVars(expr, new HashSet<>(), vars);

			return vars;
		}

		public RType lookupType(String name) {

			if (localObjTypeMap != null) {
				return localObjTypeMap.get(name);
			}

			return parent == null ? null : parent.lookupType(name);
		}

		public NameSet newBranch() {
			return new NameSet(this);
		}

		public List<XRFunction> removeDepFun(XRFunction curFun) {
			return funcDepMap == null ? null : funcDepMap.remove(curFun);
		}

		public void updateExpr(IRObject e0, IRExpr expr) throws RException {

			// (defvar ?x)
			if (isFactor(e0, F_DEFVAR)) {
				addVar(RulpUtil.asAtom(expr.get(1)).getName());
				return;
			}

			// (defun fun)
			if (isFactor(e0, F_DEFUN)) {
				addFunName(RulpUtil.asAtom(expr.get(1)).getName());
				return;
			}

			// (loop for x in '(1 2 3) do ...
			// (loop for x from 1 to 3 do ...
			// (loop stmt1 ...
			if (isFactor(e0, F_LOOP)) {

				if (RulpUtil.isAtom(expr.get(1), F_FOR)) {
					addVar(RulpUtil.asAtom(expr.get(2)).getName());
				}

				return;
			}
		}
	}

	private static void _findFunCallee(IRIterator<? extends IRObject> it, Set<String> calleeNames, IRFrame frame)
			throws RException {
		while (it.hasNext()) {
			_findFunCallee(it.next(), calleeNames, frame);
		}
	}

	private static void _findFunCallee(IRObject e0, IRExpr expr, Set<String> calleeNames, IRFrame frame)
			throws RException {

		switch (e0.getType()) {
		case ATOM:
			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, RulpUtil.asAtom(e0).getName());
			if (entry != null) {
				_findFunCallee(entry.getObject(), expr, calleeNames, frame);
			} else {
				_findFunCallee(expr.iterator(), calleeNames, frame);
			}

			return;

		case FACTOR:
			_findFunCallee(expr.listIterator(1), calleeNames, frame);
			return;

		case FUNC:
			calleeNames.add(RulpUtil.asFunction(e0).getName());
			_findFunCallee(expr.listIterator(1), calleeNames, frame);
			return;

		case TEMPLATE:
		case MACRO:
		default:
			return;
		}
	}

	private static void _findFunCallee(IRObject obj, Set<String> calleeNames, IRFrame frame) throws RException {

		if (obj == null || obj.getType() != RType.EXPR) {
			return;
		}

		IRExpr expr = (IRExpr) obj;
		if (expr.isEmpty()) {
			return;
		}

		_findFunCallee(expr.get(0), expr, calleeNames, frame);
	}

	private static boolean _isStableFunc(XRFunction func, NameSet nameSet, IRFrame frame) throws RException {

		Boolean bRc = func.getIsStable();
		if (bRc != null) {
			return bRc;
		}

		int checkSize = nameSet.checkingStack.size();

		// assuming
		if (nameSet.assuingFuncs.contains(func)) {
			return true;
		}

		int lastPos = nameSet.checkingStack.indexOf(func);
		if (lastPos != -1) {

			// recursion self
			if ((lastPos + 1) == checkSize) {
				return true; // not update function's table value
			}

			// recursion cycle detected: A call B, B call C, C call A
			for (int i = (lastPos + 1); i < checkSize; ++i) {

				// Add B and C to A's dep list
				nameSet.addDepFun(func, nameSet.checkingStack.get(i));
			}

			// jump to B
			throw new RInterrupt(nameSet.checkingStack.get(lastPos + 1), frame);
		}

		for (IRParaAttr pa : func.getParaAttrs()) {
			nameSet.addVar(pa.getParaName());
		}

		nameSet.checkingStack.add(func);

		try {

			boolean stable = isStable(func.getFunBody(), nameSet, frame);

			// assume A is stable, check all dep functions again
			if (stable && nameSet.hasDepFuncs(func)) {

				for (XRFunction depFunc : nameSet.removeDepFun(func)) {

					if (nameSet.assuingFuncs.contains(func)) {
						throw new RException("duplicated assume func: " + func);
					}

					nameSet.assuingFuncs.add(func);

					try {

						// find one non-stable function
						if (!isStable(depFunc, nameSet.newBranch(), frame)) {
							stable = false;
							break;
						}

					} finally {
						nameSet.assuingFuncs.remove(func);
					}

				}
			}

			// Updating dep function list
			if (nameSet.hasDepFuncs(func)) {
				for (XRFunction depFunc : nameSet.removeDepFun(func)) {
					depFunc.setIsStable(stable);
				}
			}

			// Updating function's table value
			func.setIsStable(stable);

			return stable;

		} catch (RInterrupt e) {

			IRObject fromFunc = e.getFromObject();
			if (fromFunc != func) {
				throw e;
			}

			return true; // return true, will check B later

		} finally {
			nameSet.checkingStack.remove(checkSize);
		}

	}

	private static boolean _isStableFuncList(XRFunctionList func, NameSet nameSet, IRFrame frame) throws RException {

		Boolean bRc = func.getIsStable();
		if (bRc != null) {
			return bRc;
		}

		boolean rc = true;
		for (IRFunction childFunc : func.getAllFuncList()) {
			if (!isStable(childFunc, nameSet, frame)) {
				rc = false;
				break;
			}
		}

		func.setIsStable(rc);
		return rc;
	}

//	static int deep = 0;

	private static boolean _isStableList(IRIterator<? extends IRObject> it, NameSet nameSet, IRFrame frame)
			throws RException {

		while (it.hasNext()) {
			if (!isStable(it.next(), nameSet, frame)) {
				return false;
			}
		}
		return true;
	}

	public static Set<String> findFunCallee(IRExpr expr, IRFrame frame) throws RException {

		if (expr.isEmpty()) {
			return Collections.emptySet();
		}

		Set<String> callee = new HashSet<>();
		_findFunCallee(expr, callee, frame);
		return callee;
	}

	public static boolean isFactor(IRObject obj, String name) throws RException {

		if (obj.getType() != RType.ATOM && obj.getType() != RType.FACTOR) {
			return false;
		}

		return obj.asString().equals(name);
	}

	public static boolean isNewFrameFactor(IRObject obj) throws RException {

		if (obj.getType() != RType.ATOM && obj.getType() != RType.FACTOR) {
			return false;
		}

		switch (obj.asString()) {
		case F_FOREACH:
		case F_E_TRY:
		case A_DO:
		case F_LET:
		case F_LOOP:
		case F_OPT:
			return true;
		}

		return false;
	}

	public static boolean isStable(IRObject obj, IRFrame frame) throws RException {
//		deep = 0;
		return isStable(obj, new NameSet(), frame);
	}

	public static boolean isStable(IRObject obj, NameSet nameSet, IRFrame frame) throws RException {

//		if (deep++ > 100) {
//			System.out.println();
//		}

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

		case VAR:
			return nameSet.lookupType(RulpUtil.asVar(obj).getName()) != null;

		case ATOM: {

			String atomName = RulpUtil.asAtom(obj).getName();

			// is local atom
			if (nameSet.lookupType(atomName) == RType.VAR) {
				return true;
			}

			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, RulpUtil.asAtom(obj).getName());

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

		case FACTOR:
		case TEMPLATE:
		case MACRO:
			return ((IRCallable) obj).isStable();

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
			if (isNewFrameFactor(e0)) {

				// need new branch
				return _isStableList(expr.listIterator(1), nameSet.newBranch(), frame);
			}

			nameSet.updateExpr(e0, expr);

			return _isStableList(expr.listIterator(0), nameSet, frame);

		case LIST:
			return _isStableList(((IRList) obj).listIterator(0), nameSet, frame);

		case FUNC:

			if (RulpUtil.isFunctionList(obj)) {
				return _isStableFuncList((XRFunctionList) obj, nameSet.newBranch(), frame);
			} else {
				return _isStableFunc((XRFunction) obj, nameSet.newBranch(), frame);
			}

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
}
