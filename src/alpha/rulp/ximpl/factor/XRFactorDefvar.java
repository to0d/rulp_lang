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

public class XRFactorDefvar extends AbsRFactorAdapter implements IRFactor {

	private boolean allowRedefine;

	private boolean rtVar;

	public XRFactorDefvar(String factorName, boolean rtVar, boolean allowRedefine) {
		super(factorName);
		this.rtVar = rtVar;
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

	public IRObject defMemberVar(IRMember mbr, IRObject valObj, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		IRObject subObj = interpreter.compute(frame, mbr.getSubject());
		IRSubject sub = RulpUtil.asSubject(subObj);

		String varName = mbr.getName();
		IRVar var = null;
		IRMember actMbr = sub.getMember(mbr.getName());
		if (actMbr != null) {

			if (actMbr.getValue().getType() != RType.VAR) {
				throw new RException("can't redefine member<" + actMbr + "> to var");
			}

			if (actMbr.isFinal()) {
				throw new RException("can't redefine final member: " + actMbr);
			}

			var = RulpUtil.asVar(actMbr.getValue());

		} else {

			if (sub.isFinal()) {
				throw new RException("can't define member<" + mbr + "> for final subject: " + sub);
			}

			var = RulpFactory.createVar(varName);
			sub.setMember(varName, RulpFactory.createMember(sub, varName, var));
		}

		IRObject val = var.getValue();
		if (valObj != null) {
			val = interpreter.compute(frame, valObj);
			var.setValue(val);
		}

		return rtVar ? var : val;
	}

	public IRObject defVar(String varName, IRObject valObj, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		if (!allowRedefine) {
			IRFrameEntry entry = frame.getEntry(varName);
			if (entry != null) {
				return RulpUtil.asVar(entry.getObject());
			}
		}

		IRVar var = RulpFactory.createVar(varName);
		frame.setEntry(varName, var);

		IRObject val = var.getValue();
		if (valObj != null) {
			val = interpreter.compute(frame, valObj);
			var.setValue(val);
		}

		return rtVar ? var : val;
	}

	@Override
	public boolean isStable() {
		return true;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
