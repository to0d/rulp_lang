package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.F_DEFUN;
import static alpha.rulp.lang.Constant.F_DEFVAR;
import static alpha.rulp.lang.Constant.F_FOR;
import static alpha.rulp.lang.Constant.F_LOOP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.function.XRFunction;

public class NameSet {

	private Map<XRFunction, List<XRFunction>> funcDepMap;

	private Map<String, RType> localObjTypeMap;

	private NameSet parent = null;

	public NameSet() {
		this.funcDepMap = new HashMap<>();
	}

	public NameSet(NameSet parent) {
		this.parent = parent;
		this.funcDepMap = parent.funcDepMap;
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
		if (RulpUtil.isFactor(e0, F_DEFVAR)) {
			addVar(RulpUtil.asAtom(expr.get(1)).getName());
			return;
		}

		// (defun fun)
		if (RulpUtil.isFactor(e0, F_DEFUN)) {
			addFunName(RulpUtil.asAtom(expr.get(1)).getName());
			return;
		}

		// (loop for x in '(1 2 3) do ...
		// (loop for x from 1 to 3 do ...
		// (loop stmt1 ...
		if (RulpUtil.isFactor(e0, F_LOOP)) {

			if (RulpUtil.isAtom(expr.get(1), F_FOR)) {
				addVar(RulpUtil.asAtom(expr.get(2)).getName());
			}

			return;
		}
	}
}
