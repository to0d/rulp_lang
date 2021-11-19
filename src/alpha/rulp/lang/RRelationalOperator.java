/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

import static alpha.rulp.lang.Constant.O_EQ;
import static alpha.rulp.lang.Constant.O_GE;
import static alpha.rulp.lang.Constant.O_GT;
import static alpha.rulp.lang.Constant.O_LE;
import static alpha.rulp.lang.Constant.O_LT;
import static alpha.rulp.lang.Constant.O_NE;

public enum RRelationalOperator {

	EQ(O_EQ), // >, Greater than
	GE(O_GE), // >=, Greater than or equal
	GT(O_GT), // =, Equal
	LE(O_LE), // !=, Not equal
	LT(O_LT), // <, Less than
	NE(O_NE);

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

	private IRAtom atom;

	private RRelationalOperator(IRAtom atom) {
		this.atom = atom;
	}

	// <=, Less than or equal

	public IRAtom getAtom() {
		return atom;
	}

}
