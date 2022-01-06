/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.string;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorStrFormat extends AbsAtomFactorAdapter implements IRFactor {

	static Object toObj(IRObject obj) throws RException {

		switch (obj.getType()) {
		case BOOL:
			return RulpUtil.asBoolean(obj).asBoolean();

		case FLOAT:
			return RulpUtil.asFloat(obj).asFloat();

		case DOUBLE:
			return RulpUtil.asDouble(obj).asDouble();

		case INT:
			return RulpUtil.asInteger(obj).asInteger();

		case LONG:
			return RulpUtil.asLong(obj).asLong();

		case STRING:
			return RulpUtil.asString(obj).asString();

		default:
			throw new RException("not support format object: " + obj);
		}
	}

	public XRFactorStrFormat(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 3 || args.size() > 11) {
			throw new RException("Invalid parameters: " + args);
		}

		String format = interpreter.compute(frame, args.get(1)).asString();
		String out = null;

		switch (args.size()) {
		case 3:
			out = String.format(format, toObj(interpreter.compute(frame, args.get(2))));
			break;

		case 4:
			out = String.format(format, toObj(interpreter.compute(frame, args.get(2))),
					toObj(interpreter.compute(frame, args.get(3))));
			break;

		case 5:
			out = String.format(format, toObj(interpreter.compute(frame, args.get(2))),
					toObj(interpreter.compute(frame, args.get(3))), toObj(interpreter.compute(frame, args.get(4))));
			break;

		case 6:
			out = String.format(format, toObj(interpreter.compute(frame, args.get(2))),
					toObj(interpreter.compute(frame, args.get(3))), toObj(interpreter.compute(frame, args.get(4))),
					toObj(interpreter.compute(frame, args.get(5))));
			break;

		case 7:
			out = String.format(format, toObj(interpreter.compute(frame, args.get(2))),
					toObj(interpreter.compute(frame, args.get(3))), toObj(interpreter.compute(frame, args.get(4))),
					toObj(interpreter.compute(frame, args.get(5))), toObj(interpreter.compute(frame, args.get(6))));
			break;

		case 8:
			out = String.format(format, toObj(interpreter.compute(frame, args.get(2))),
					toObj(interpreter.compute(frame, args.get(3))), toObj(interpreter.compute(frame, args.get(4))),
					toObj(interpreter.compute(frame, args.get(5))), toObj(interpreter.compute(frame, args.get(6))),
					toObj(interpreter.compute(frame, args.get(7))));
			break;

		case 9:
			out = String.format(format, toObj(interpreter.compute(frame, args.get(2))),
					toObj(interpreter.compute(frame, args.get(3))), toObj(interpreter.compute(frame, args.get(4))),
					toObj(interpreter.compute(frame, args.get(5))), toObj(interpreter.compute(frame, args.get(6))),
					toObj(interpreter.compute(frame, args.get(7))), toObj(interpreter.compute(frame, args.get(8))));
			break;

		case 10:
			out = String.format(format, toObj(interpreter.compute(frame, args.get(2))),
					toObj(interpreter.compute(frame, args.get(3))), toObj(interpreter.compute(frame, args.get(4))),
					toObj(interpreter.compute(frame, args.get(5))), toObj(interpreter.compute(frame, args.get(6))),
					toObj(interpreter.compute(frame, args.get(7))), toObj(interpreter.compute(frame, args.get(8))),
					toObj(interpreter.compute(frame, args.get(9))));
			break;

		case 11:
			out = String.format(format, toObj(interpreter.compute(frame, args.get(2))),
					toObj(interpreter.compute(frame, args.get(3))), toObj(interpreter.compute(frame, args.get(4))),
					toObj(interpreter.compute(frame, args.get(5))), toObj(interpreter.compute(frame, args.get(6))),
					toObj(interpreter.compute(frame, args.get(7))), toObj(interpreter.compute(frame, args.get(8))),
					toObj(interpreter.compute(frame, args.get(9))), toObj(interpreter.compute(frame, args.get(10))));
			break;

		default:
			throw new RException("Invalid parameters: " + args);
		}

		return RulpFactory.createString(out);
	}

}