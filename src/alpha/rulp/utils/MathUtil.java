/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utils;

import alpha.rulp.lang.IRBoolean;
import alpha.rulp.lang.IRDouble;
import alpha.rulp.lang.IRFloat;
import alpha.rulp.lang.IRInteger;
import alpha.rulp.lang.IRLong;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;

public class MathUtil {

	static RType calRstType[][] = new RType[RType.TYPE_NUM][RType.TYPE_NUM];

	static {
		calRstType[RType.NIL.getIndex()][RType.NIL.getIndex()] = RType.NIL;

		calRstType[RType.INT.getIndex()][RType.INT.getIndex()] = RType.INT;
		calRstType[RType.INT.getIndex()][RType.LONG.getIndex()] = RType.LONG;
		calRstType[RType.INT.getIndex()][RType.FLOAT.getIndex()] = RType.FLOAT;
		calRstType[RType.INT.getIndex()][RType.DOUBLE.getIndex()] = RType.DOUBLE;

		calRstType[RType.LONG.getIndex()][RType.INT.getIndex()] = RType.LONG;
		calRstType[RType.LONG.getIndex()][RType.LONG.getIndex()] = RType.LONG;
		calRstType[RType.LONG.getIndex()][RType.FLOAT.getIndex()] = RType.FLOAT;
		calRstType[RType.LONG.getIndex()][RType.DOUBLE.getIndex()] = RType.DOUBLE;

		calRstType[RType.FLOAT.getIndex()][RType.INT.getIndex()] = RType.FLOAT;
		calRstType[RType.FLOAT.getIndex()][RType.LONG.getIndex()] = RType.FLOAT;
		calRstType[RType.FLOAT.getIndex()][RType.FLOAT.getIndex()] = RType.FLOAT;
		calRstType[RType.FLOAT.getIndex()][RType.DOUBLE.getIndex()] = RType.DOUBLE;

		calRstType[RType.DOUBLE.getIndex()][RType.INT.getIndex()] = RType.DOUBLE;
		calRstType[RType.DOUBLE.getIndex()][RType.LONG.getIndex()] = RType.DOUBLE;
		calRstType[RType.DOUBLE.getIndex()][RType.FLOAT.getIndex()] = RType.DOUBLE;
		calRstType[RType.DOUBLE.getIndex()][RType.DOUBLE.getIndex()] = RType.DOUBLE;

		calRstType[RType.BOOL.getIndex()][RType.BOOL.getIndex()] = RType.BOOL;

		calRstType[RType.EXPR.getIndex()][RType.EXPR.getIndex()] = RType.EXPR;
		calRstType[RType.ATOM.getIndex()][RType.ATOM.getIndex()] = RType.ATOM;

	}

	public static RType getConvertType(RType a, RType b) {
		return calRstType[a.getIndex()][b.getIndex()];
	}

	public static boolean toBoolean(IRObject a) throws RException {
		switch (a.getType()) {
		case NIL:
			return false;

		case BOOL:
			return ((IRBoolean) a).asBoolean();

		case FLOAT:
		case DOUBLE:
		case EXPR:
			return true;

		case INT:
		case LONG:
			return toLong(a) != 0;

		default:
			throw new RException(String.format("Not support type: %s", a.toString()));
		}
	}

	public static float toFloat(IRObject a) throws RException {
		switch (a.getType()) {
		case FLOAT:
			return ((IRFloat) a).asFloat();

		case DOUBLE:
			return (float) ((IRDouble) a).asDouble();

		case INT:
			return ((IRInteger) a).asInteger();

		case LONG:
			return (float) ((IRLong) a).asLong();

		default:
			throw new RException(String.format("Not support type: %s", a.toString()));
		}
	}

	public static double toDouble(IRObject a) throws RException {
		switch (a.getType()) {
		case FLOAT:
			return ((IRFloat) a).asFloat();

		case DOUBLE:
			return ((IRDouble) a).asDouble();

		case INT:
			return ((IRInteger) a).asInteger();

		case LONG:
			return (double) ((IRLong) a).asLong();

		default:
			throw new RException(String.format("Not support type: %s", a.toString()));
		}
	}

	public static int toInt(IRObject a) throws RException {
		switch (a.getType()) {
		case FLOAT:
			return (int) ((IRFloat) a).asFloat();

		case DOUBLE:
			return (int) ((IRDouble) a).asDouble();

		case INT:
			return ((IRInteger) a).asInteger();

		case LONG:
			return (int) ((IRLong) a).asLong();

		default:
			throw new RException(String.format("Not support type: %s", a.toString()));
		}
	}

	public static long toLong(IRObject a) throws RException {
		switch (a.getType()) {
		case FLOAT:
			return (long) ((IRFloat) a).asFloat();

		case DOUBLE:
			return (long) ((IRDouble) a).asDouble();

		case INT:
			return ((IRInteger) a).asInteger();

		case LONG:
			return ((IRLong) a).asLong();

		default:
			throw new RException(String.format("Not support type: %s", a.toString()));
		}
	}

}
