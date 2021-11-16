/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

import static alpha.rulp.lang.Constant.F_O_EQ;
import static alpha.rulp.lang.Constant.F_O_GE;
import static alpha.rulp.lang.Constant.F_O_GT;
import static alpha.rulp.lang.Constant.F_O_LE;
import static alpha.rulp.lang.Constant.F_O_LT;
import static alpha.rulp.lang.Constant.F_O_NE;

public enum RRelationalOperator {

	EQ, // >, Greater than
	GE, // >=, Greater than or equal
	GT, // =, Equal
	LE, // !=, Not equal
	LT, // <, Less than
	NE; // <=, Less than or equal

	public static RRelationalOperator toOpposite(RRelationalOperator op) {

		switch (op) {
		case EQ:
			return RRelationalOperator.EQ;

		case GE:
			return RRelationalOperator.LT;

		case GT:
			return RRelationalOperator.LE;

		case LE:
			return RRelationalOperator.GT;

		case LT:
			return RRelationalOperator.GE;

		case NE:
			return RRelationalOperator.NE;

		default:
			return null;
		}
	}

	public static String toSymbolString(RRelationalOperator op) {

		switch (op) {
		case EQ:
			return F_O_EQ;

		case GE:
			return F_O_GE;

		case GT:
			return F_O_GT;

		case LE:
			return F_O_LE;

		case LT:
			return F_O_LT;

		case NE:
			return F_O_NE;

		default:
			return null;
		}
	}
}
