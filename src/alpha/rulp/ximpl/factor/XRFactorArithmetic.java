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
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.MathUtil;
import alpha.rulp.utils.RulpFactory;

public class XRFactorArithmetic extends AbsRFactorAdapter implements IRFactor {

	public enum ArithmeticType {
		ADD, BY, DIV, MOD, POWER, SUB
	}

	public static IRObject calculate(ArithmeticType op, IRObject a, IRObject b) throws RException {

		RType at = a.getType();
		RType bt = b.getType();

		RType rt = MathUtil.getConvertType(at, bt);
		if (rt == null) {
			throw new RException(String.format("Invalid op types: %s %s", a.toString(), b.toString()));
		}

		switch (rt) {
		case FLOAT: {

			float av = MathUtil.toFloat(a);
			float bv = MathUtil.toFloat(b);

			switch (op) {
			case ADD:
				av += bv;
				break;

			case BY:
				av *= bv;
				break;

			case SUB:
				av -= bv;
				break;

			case DIV:
				av /= bv;
				break;

			case MOD:
				av %= bv;
				break;

			case POWER:
				av = (float) Math.pow(av, bv);
				break;

			default:
				throw new RException(String.format("Not support op: %s", op));
			}

			return RulpFactory.createFloat(av);
		}

		case DOUBLE: {

			double av = MathUtil.toDouble(a);
			double bv = MathUtil.toDouble(b);

			switch (op) {
			case ADD:
				av += bv;
				break;

			case BY:
				av *= bv;
				break;

			case SUB:
				av -= bv;
				break;

			case DIV:
				av /= bv;
				break;

			case MOD:
				av %= bv;
				break;

			case POWER:
				av = Math.pow(av, bv);
				break;

			default:
				throw new RException(String.format("Not support op: %s", op));
			}

			return RulpFactory.createDouble(av);
		}

		case INT: {

			int av = MathUtil.toInt(a);
			int bv = MathUtil.toInt(b);

			switch (op) {
			case ADD:
				av += bv;
				break;

			case BY:
				av *= bv;
				break;

			case SUB:
				av -= bv;
				break;

			case DIV:
				av /= bv;
				break;

			case MOD:
				av %= bv;
				break;

			case POWER:
				av = (int) Math.pow(av, bv);
				break;

			default:
				throw new RException(String.format("Not support op: %s", op));
			}

			return RulpFactory.createInteger(av);
		}

		case LONG: {

			long av = MathUtil.toLong(a);
			long bv = MathUtil.toLong(b);

			switch (op) {
			case ADD:
				av += bv;
				break;

			case BY:
				av *= bv;
				break;

			case SUB:
				av -= bv;
				break;

			case DIV:
				av /= bv;
				break;

			case MOD:
				av %= bv;
				break;

			case POWER:
				av = (long) Math.pow(av, bv);
				break;

			default:
				throw new RException(String.format("Not support op: %s", op));
			}

			return RulpFactory.createLong(av);
		}

		default:
			throw new RException(String.format("Not support type: %s %s", a.toString(), b.toString()));
		}

	}

	private ArithmeticType type;

	public XRFactorArithmetic(String factorName, ArithmeticType type) {
		super(factorName);
		this.type = type;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRIterator<? extends IRObject> it = args.listIterator(1);

		IRObject rst = interpreter.compute(frame, it.next());
		while (it.hasNext()) {
			IRObject next = interpreter.compute(frame, it.next());
			rst = calculate(type, rst, next);
		}

		return rst;
	}

	@Override
	public boolean isStable() {
		return true;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
