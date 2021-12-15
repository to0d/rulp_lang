/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.factor;

import static alpha.rulp.lang.Constant.O_False;
import static alpha.rulp.lang.Constant.O_True;

import alpha.rulp.lang.IRArray;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;

public class XRFactorIsConst extends AbsRFactorAdapter implements IRFactor {

	public XRFactorIsConst(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject obj = args.get(1);
		switch (obj.getType()) {
		case CONSTANT:
		case DOUBLE:
		case ATOM:
		case BOOL:
		case EXPR:
		case FACTOR:
		case FLOAT:
		case INT:
		case LONG:
		case NIL:
		case STRING:
		case MACRO:
			return O_True;

		case LIST:
			return ((IRList) obj).isConst() ? O_True : O_False;

		case ARRAY:
			return ((IRArray) obj).isConst() ? O_True : O_False;

		case BLOB:
		case CLASS:
		case FUNC:
		case FRAME:
		case INSTANCE:
		case MEMBER:
		case NATIVE:
		case TEMPLATE:
		case VAR:
		default:
			return O_False;
		}
	}

	@Override
	public boolean isStable() {
		return true;
	}

	public boolean isThreadSafe() {
		return true;
	}
}