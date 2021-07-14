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
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.StringUtil;

public class XRFactorCanCast extends AbsRFactorAdapter implements IRFactor {

	public XRFactorCanCast(String factorName) {
		super(factorName);
	}

	static boolean can_cast_int(IRObject obj) throws RException {

		if (obj == null) {
			return false;
		}

		switch (obj.getType()) {
		case INT:
		case LONG:
			return true;

		case STRING:
			return StringUtil.isNumber(RulpUtil.asString(obj).asString());

		case VAR:
			return can_cast_int(RulpUtil.asVar(obj).getValue());

		default:
			return false;
		}
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		RType toType = RType.toType(RulpUtil.asAtom(interpreter.compute(frame, args.get(1))).asString());
		IRObject val = interpreter.compute(frame, args.get(2));
		boolean rc = false;
		switch (toType) {

		case INT:
			rc = can_cast_int(val);
			break;

		default:
			throw new RException("not support type: " + toType);
		}

		return RulpFactory.createBoolean(rc);
	}

	@Override
	public boolean isStable() {
		return true;
	}

	public boolean isThreadSafe() {
		return true;
	}

}