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
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;

public class XRFactorSetq extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorSetq(String factorName) {
		super(factorName);
	}

	public static IRVar setVar(IRObject obj, IRObject val, IRInterpreter interpreter, IRFrame frame) throws RException {

		IRVar var = null;

		switch (obj.getType()) {
		case ATOM:
			String varName = RulpUtil.asAtom(obj).getName();
			IRFrameEntry entry = frame.getEntry(varName);
			if (entry == null) {
				throw new RException("var not found: " + varName);
			}
			var = RulpUtil.asVar(entry.getObject());
			break;

		case VAR:
			var = RuntimeUtil.getActualVar((IRVar) obj, interpreter, frame);
			break;

		case EXPR:
			return setVar(interpreter.compute(frame, obj), val, interpreter, frame);

		case MEMBER:

			IRMember mbr = RuntimeUtil.getActualMember(RulpUtil.asMember(obj), interpreter, frame);
			if (!RuntimeUtil.canAccess(mbr, interpreter, frame)) {
				throw new RException("Can't access member<" + mbr + "> in frame<" + frame + ">");
			}

			if (RulpUtil.isPropertyFinal(mbr)) {
				throw new RException("Can't update final member: " + mbr);
			}

			return setVar(mbr.getValue(), val, interpreter, frame);

		default:
			throw new RException("Invalid var: " + obj.toString());
		}

		var.setValue(interpreter.compute(frame, val));
		return var;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args.toString());
		}

		return setVar(args.get(1), args.get(2), interpreter, frame);
	}

}
