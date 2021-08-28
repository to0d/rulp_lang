/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.factor;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.rclass.XRFactorDefClass;

public class XRFactorDefvar extends AbsRFactorAdapter implements IRFactor {

	private boolean allowRedefine;

	private boolean returnVar;

	public XRFactorDefvar(String factorName, boolean rtVar, boolean allowRedefine) {
		super(factorName);
		this.returnVar = rtVar;
		this.allowRedefine = allowRedefine;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2 && args.size() != 3) {
			throw new RException("Invalid parameter number, expect(2 or 3): " + args);
		}

		IRObject varObj = args.get(1);
		IRObject valObj = args.size() > 2 ? args.get(2) : null;

		if (varObj.getType() == RType.ATOM) {
			return defVar(RulpUtil.asAtom(varObj).getName(), valObj, interpreter, frame);
		}

		if (varObj.getType() == RType.MEMBER) {
			return defMemberVar(RulpUtil.asMember(varObj), valObj, interpreter, frame);
		}

		throw new RException("Invalid var type, : " + args);
	}

	public IRObject defMemberVar(IRMember mbrObj, IRObject val, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		IRObject subObj = interpreter.compute(frame, mbrObj.getSubject());
		IRSubject sub = RulpUtil.asSubject(subObj);
		String varName = mbrObj.getName();

		if (val != null) {
			val = interpreter.compute(frame, val);
		}

		IRMember mbr = XRFactorDefClass.createMemberVar(sub, varName, val);
		sub.setMember(varName, mbr);

		return returnVar ? mbr.getValue() : val;
	}

	public IRObject defVar(String varName, IRObject valObj, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		if (!allowRedefine) {
			IRFrameEntry entry = frame.getEntry(varName);
			if (entry != null && entry.getFrame() == frame) {
				throw new RException("duplicate local variable: " + varName);
			}
		}

		IRVar var = RulpFactory.createVar(varName);
		frame.setEntry(varName, var);

		IRObject val = var.getValue();
		if (valObj != null) {
			val = interpreter.compute(frame, valObj);
			var.setValue(val);
		}

		return returnVar ? var : val;
	}

	@Override
	public boolean isStable() {
		return true;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
