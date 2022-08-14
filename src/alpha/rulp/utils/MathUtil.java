/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utils;

import java.util.Random;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RArithmeticOperator;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RRelationalOperator;
import alpha.rulp.lang.RType;
import alpha.rulp.ximpl.optimize.OptUtil;

public class MathUtil {

	static Random random = new Random();

	static RType result2Type[][] = new RType[RType.TYPE_NUM][RType.TYPE_NUM];

	static {

		result2Type[RType.NIL.getIndex()][RType.NIL.getIndex()] = RType.NIL;

		result2Type[RType.INT.getIndex()][RType.INT.getIndex()] = RType.INT;
		result2Type[RType.INT.getIndex()][RType.LONG.getIndex()] = RType.LONG;
		result2Type[RType.INT.getIndex()][RType.FLOAT.getIndex()] = RType.FLOAT;
		result2Type[RType.INT.getIndex()][RType.DOUBLE.getIndex()] = RType.DOUBLE;

		result2Type[RType.LONG.getIndex()][RType.INT.getIndex()] = RType.LONG;
		result2Type[RType.LONG.getIndex()][RType.LONG.getIndex()] = RType.LONG;
		result2Type[RType.LONG.getIndex()][RType.FLOAT.getIndex()] = RType.FLOAT;
		result2Type[RType.LONG.getIndex()][RType.DOUBLE.getIndex()] = RType.DOUBLE;

		result2Type[RType.FLOAT.getIndex()][RType.INT.getIndex()] = RType.FLOAT;
		result2Type[RType.FLOAT.getIndex()][RType.LONG.getIndex()] = RType.FLOAT;
		result2Type[RType.FLOAT.getIndex()][RType.FLOAT.getIndex()] = RType.FLOAT;
		result2Type[RType.FLOAT.getIndex()][RType.DOUBLE.getIndex()] = RType.DOUBLE;

		result2Type[RType.DOUBLE.getIndex()][RType.INT.getIndex()] = RType.DOUBLE;
		result2Type[RType.DOUBLE.getIndex()][RType.LONG.getIndex()] = RType.DOUBLE;
		result2Type[RType.DOUBLE.getIndex()][RType.FLOAT.getIndex()] = RType.DOUBLE;
		result2Type[RType.DOUBLE.getIndex()][RType.DOUBLE.getIndex()] = RType.DOUBLE;

		result2Type[RType.BOOL.getIndex()][RType.BOOL.getIndex()] = RType.BOOL;

		result2Type[RType.EXPR.getIndex()][RType.EXPR.getIndex()] = RType.EXPR;
		result2Type[RType.ATOM.getIndex()][RType.ATOM.getIndex()] = RType.ATOM;

	}

	public static IRObject computeArithmeticExpression(RArithmeticOperator op, IRObject a, IRObject b)
			throws RException {

		RType at = a.getType();
		RType bt = b.getType();

		RType rt = MathUtil.getTypeConvert(at, bt);
		if (rt == null) {
			throw new RException(String.format("Invalid op types: %s %s", a.toString(), b.toString()));
		}

		if (op == RArithmeticOperator.DIV && OptUtil.isConstNumber(b, 0)) {
			throw new RException(String.format("divide zero(%s %s %s)", "" + op, a.toString(), b.toString()));
		}

		switch (rt) {
		case FLOAT: {

			float av = RulpUtil.toFloat(a);
			float bv = RulpUtil.toFloat(b);

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

			double av = RulpUtil.toDouble(a);
			double bv = RulpUtil.toDouble(b);

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

			int av = RulpUtil.toInt(a);
			int bv = RulpUtil.toInt(b);

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

			case AND:
				av = av & bv;
				break;

			case OR:
				av = av | bv;
				break;

			case XOR:
				av = av ^ bv;
				break;

			default:
				throw new RException(String.format("Not support op: %s", op));
			}

			return RulpFactory.createInteger(av);
		}

		case LONG: {

			long av = RulpUtil.toLong(a);
			long bv = RulpUtil.toLong(b);

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

			case AND:
				av = av & bv;
				break;

			case OR:
				av = av | bv;
				break;

			case XOR:
				av = av ^ bv;
				break;

			default:
				throw new RException(String.format("Not support op: %s", op));
			}

			return RulpFactory.createLong(av);
		}

		default:
		}

		throw new RException(
				String.format("Invalid arithmetic expression: (%s %s %s)", op, a.toString(), b.toString()));

	}

	public static boolean computeRelationalExpression(RRelationalOperator op, IRObject a, IRObject b)
			throws RException {

		if (isNumber(a)) {

			if (!isNumber(b)) {
				throw new RException(
						String.format("Invalid rational expression: (%s %s %s)", op, a.toString(), b.toString()));
			}

		} else {
			switch (op) {
			case EQ:
				return RulpUtil.equal(a, b);

			case NE:
				return !RulpUtil.equal(a, b);

			default:
			}
		}

		RType at = a.getType();
		RType bt = b.getType();

		RType rt = MathUtil.getTypeConvert(at, bt);
		if (rt == null) {
			throw new RException(String.format("Invalid op types: %s %s %s", op, a.toString(), b.toString()));
		}

		switch (rt) {

		case FLOAT: {

			float av = RulpUtil.toFloat(a);
			float bv = RulpUtil.toFloat(b);

			switch (op) {

			case GT: // Greater than
				return av > bv;

			case GE: // Greater than or equal
				return av >= bv;

			case LT: // Less than
				return av < bv;

			case LE: // Less than or equal
				return av <= bv;

			case EQ: // Equal
				return av == bv;

			case NE: // not equal
				return av != bv;

			default:
			}

			break;
		}

		case DOUBLE: {

			double av = RulpUtil.toDouble(a);
			double bv = RulpUtil.toDouble(b);

			switch (op) {

			case GT: // Greater than
				return av > bv;

			case GE: // Greater than or equal
				return av >= bv;

			case LT: // Less than
				return av < bv;

			case LE: // Less than or equal
				return av <= bv;

			case EQ: // Equal
				return av == bv;

			case NE: // not equal
				return av != bv;

			default:
			}

			break;
		}

		case INT: {
			int av = RulpUtil.toInt(a);
			int bv = RulpUtil.toInt(b);

			switch (op) {

			case GT: // Greater than
				return av > bv;

			case GE: // Greater than or equal
				return av >= bv;

			case LT: // Less than
				return av < bv;

			case LE: // Less than or equal
				return av <= bv;

			case EQ: // Equal
				return av == bv;

			case NE: // not equal
				return av != bv;

			default:
			}
			break;
		}

		case LONG: {

			long av = RulpUtil.toLong(a);
			long bv = RulpUtil.toLong(b);

			switch (op) {

			case GT: // Greater than
				return av > bv;

			case GE: // Greater than or equal
				return av >= bv;

			case LT: // Less than
				return av < bv;

			case LE: // Less than or equal
				return av <= bv;

			case EQ: // Equal
				return av == bv;

			case NE: // not equal
				return av != bv;

			default:
			}

			break;
		}

		default:

		}

		throw new RException(String.format("Invalid rational expression: (%s %s %s)", op, a.toString(), b.toString()));
	}

	public static RType getTypeConvert(RType a, RType b) {
		return result2Type[a.getIndex()][b.getIndex()];
	}

	public static boolean isNumber(IRObject obj) {

		switch (obj.getType()) {
		case DOUBLE:
		case FLOAT:
		case INT:
		case LONG:
			return true;

		default:
			return false;

		}
	}

	public static double random_double() {
		return random.nextDouble();
	}

	public static float random_float() {
		return random.nextFloat();
	}

	public static int random_int() {
		return random.nextInt();
	}

	public static int random_int(int bound) {
		return random.nextInt(bound);
	}

}
