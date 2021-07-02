/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRMacro;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.ximpl.runtime.AbsRefCallableAdapter;

public class XRMacro extends AbsRefCallableAdapter implements IRMacro {

//	static class MacroUpdateIterator implements IRIterator<IRObject> {
//
//		private IRIterator<? extends IRObject> iter;
//		private Map<String, IRObject> macroMap;
//
//		public MacroUpdateIterator(IRIterator<? extends IRObject> iter, Map<String, IRObject> macroMap) {
//			super();
//			this.iter = iter;
//			this.macroMap = macroMap;
//		}
//
//		@Override
//		public boolean hasNext() throws RException {
//			return iter.hasNext();
//		}
//
//		@Override
//		public IRObject next() throws RException {
//			return updateMacroObj(iter.next(), macroMap);
//		}
//
//	}

	public static IRObject copyMacroObj(IRObject obj, Map<String, IRObject> macroMap) throws RException {

		if (obj == null) {
			return obj;
		}

		switch (obj.getType()) {
		case ATOM: {
			IRAtom atom = (IRAtom) obj;
			IRObject var = macroMap.get(atom.getName());
			return var == null ? obj : var;
		}

		case EXPR:
		case LIST: {

			ArrayList<IRObject> newList = new ArrayList<>();
			IRIterator<? extends IRObject> it = ((IRList) obj).iterator();
			while (it.hasNext()) {
				newList.add(copyMacroObj(it.next(), macroMap));
			}

			if (obj.getType() == RType.LIST) {
				return RulpFactory.createList(newList);
			}

			IRExpr expr = (IRExpr) obj;

			return expr.isEarly() ? RulpFactory.createExpressionEarly(newList) : RulpFactory.createExpression(newList);
		}

		case MEMBER: {

			IRMember mbr = (IRMember) obj;
			if (mbr.getValue() == null) {

				boolean update = false;

				IRObject sub = mbr.getSubject();
				String mbrName = mbr.getName();

				if (sub.getType() == RType.ATOM) {
					IRObject var = macroMap.get(((IRAtom) sub).getName());
					if (var != null) {
						sub = var;
						update = true;
					}
				}

				if (update) {
					return RulpFactory.createMember(sub, mbrName, null);
				}
			}

			return obj;
		}

		default:

			return obj;
		}
	}

//	public static IRObject updateMacroObj(IRObject obj, Map<String, IRObject> macroMap) throws RException {
//
//		if (obj == null) {
//			return obj;
//		}
//
//		switch (obj.getType()) {
//		case ATOM:
//			IRAtom atom = (IRAtom) obj;
//			IRObject mv = macroMap.get(atom.getName());
//			return mv == null ? obj : mv;
//
//		case EXPR:
//			return RulpFactory.createExpression(new MacroUpdateIterator(((IRList) obj).iterator(), macroMap));
//
//		case LIST:
//			return RulpFactory.createList(new MacroUpdateIterator(((IRList) obj).iterator(), macroMap));
//
//		default:
//			return obj;
//		}
//	}

	private String _macroSignature;

	protected IRList bodyList;

	protected String macroName;

	protected final List<String> paraNameList;

//	protected final Map<String, IRVar> macroMap;

//	protected final List<IRVar> paraVarList;

	public XRMacro(String macroName, List<String> paraNameList, IRList bodyList) throws RException {
		this.macroName = macroName;
		this.paraNameList = paraNameList;
		this.bodyList = bodyList;
//		this.macroMap = new HashMap<>();
//		this.paraVarList = new ArrayList<>();

//		for (String para : paraNameList) {
//			IRVar var = RulpFactory.createVar(para, O_Nil);
//			this.macroMap.put(para, var);
//			this.paraVarList.add(var);
//		}

//		this.bodyList = (IRList) rebuildMacroBody(bodyList, macroMap);
	}

	@Override
	public String asString() {
		return macroName;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter intepreter, IRFrame frame) throws RException {

		if (args.size() != (paraNameList.size() + 1)) {
			throw new RException("Invalid parameters: " + args);
		}

		Map<String, IRObject> macroMap = new HashMap<>();
		{
			IRIterator<? extends IRObject> valueIter = args.listIterator(1); // Skip factor head element
			Iterator<String> paraIter = paraNameList.iterator();
			while (valueIter.hasNext()) {
//				varIter.next().setValue(valueIter.next());
				macroMap.put(paraIter.next(), valueIter.next());
			}
		}

		return intepreter.compute(frame, copyMacroObj(bodyList, macroMap));
	}

	@Override
	public String getName() {
		return macroName;
	}

	@Override
	public String getSignature() {

		if (_macroSignature == null) {

		}

		return null;
	}

	@Override
	public RType getType() {
		return RType.MACRO;
	}

	@Override
	public boolean isStable() {
		return false;
	}

	public boolean isThreadSafe() {
		return true;
	}

	@Override
	public String toString() {
		return macroName;
	}
}
