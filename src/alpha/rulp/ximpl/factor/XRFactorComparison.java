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
import alpha.rulp.utils.MathUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;

public class XRFactorComparison extends AbsRFactorAdapter implements IRFactor {

	public enum ComparisonType {
		Bigger, BiggerOrEqual, Equal, NotEqual, Smaller, SmallerOrEqual
	}

	public static boolean compare(ComparisonType op, IRObject a, IRObject b) throws RException {

		switch (op) {
		case Equal:
			return RulpUtil.equal(a, b);

		case NotEqual:
			return !RulpUtil.equal(a, b);

		default:
		}

		RType at = a.getType();
		RType bt = b.getType();

		RType rt = MathUtil.getConvertType(at, bt);
		if (rt == null) {
			throw new RException(String.format("Invalid op types: %s %s %s", op, a.toString(), b.toString()));
		}

		switch (rt) {

		case FLOAT: {

			float av = MathUtil.toFloat(a);
			float bv = MathUtil.toFloat(b);

			switch (op) {

			case Bigger:
				return av > bv;

			case BiggerOrEqual:
				return av >= bv;

			case Smaller:
				return av < bv;

			case SmallerOrEqual:
				return av <= bv;

			default:
			}

			break;
		}

		case INT: {
			int av = MathUtil.toInt(a);
			int bv = MathUtil.toInt(b);

			switch (op) {

			case Bigger:
				return av > bv;

			case BiggerOrEqual:
				return av >= bv;

			case Smaller:
				return av < bv;

			case SmallerOrEqual:
				return av <= bv;

			default:
			}
			break;
		}

		case LONG: {

			long av = MathUtil.toLong(a);
			long bv = MathUtil.toLong(b);

			switch (op) {

			case Bigger:
				return av > bv;

			case BiggerOrEqual:
				return av >= bv;

			case Smaller:
				return av < bv;

			case SmallerOrEqual:
				return av <= bv;

			default:
			}

			break;
		}

		default:

		}

		throw new RException(String.format("Not support type<%s> for: %s %s", op, a.toString(), b.toString()));
	}

	private ComparisonType op;

	public XRFactorComparison(String factorName, ComparisonType op) {
		super(factorName);
		this.op = op;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject a = interpreter.compute(frame, args.get(1));
		IRObject b = interpreter.compute(frame, args.get(2));

		return RulpFactory.createBoolean(compare(op, a, b));
	}

	@Override
	public boolean isStable() {
		return true;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
