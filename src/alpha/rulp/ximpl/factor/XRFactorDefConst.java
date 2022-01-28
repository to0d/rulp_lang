/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.factor;

import alpha.rulp.lang.IRConst;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;

public class XRFactorDefConst extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorDefConst(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		String constantName = RulpUtil.asAtom(args.get(1)).getName();
		IRObject valObj = interpreter.compute(frame, args.get(2));

		IRFrameEntry entry = frame.getEntry(constantName);
		if (entry != null && entry.getFrame() == frame) {
			throw new RException("duplicate local variable: " + constantName);
		}

		switch (valObj.getType()) {
		case ARRAY:
		case ATOM:
		case BLOB:
		case BOOL:
		case DOUBLE:
		case FLOAT:
		case FUNC:
		case INSTANCE:
		case INT:
		case LIST:
		case LONG:
		case STRING:
			break;
		default:
			throw new RException("invalid constant value: " + valObj);
		}

		IRConst constant = RulpFactory.createConstant(constantName, RulpUtil.toConst(valObj, frame));
		frame.setEntry(constantName, constant);

		return constant;
	}

}
