/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.math;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorToInteger extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorToInteger(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject obj = interpreter.compute(frame, args.get(1));
		int val = 0;
		if (obj != null) {

			switch (obj.getType()) {
			case INT:
				return obj;

			case STRING:
				val = Integer.valueOf(RulpUtil.asString(obj).asString());
				break;

			case LONG:
				val = (int) RulpUtil.asLong(obj).asLong();
				break;

			case FLOAT:
				val = (int) RulpUtil.asFloat(obj).asFloat();
				break;

			case DOUBLE:
				val = (int) RulpUtil.asDouble(obj).asDouble();
				break;

			default:
				throw new RException("Can't conver to integer: " + obj);

			}
		}

		return RulpFactory.createInteger(val);
	}

	public boolean isThreadSafe() {
		return true;
	}

}