/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.control;

import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.MathUtil;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorIf extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorIf(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		// (if condition true-expr)
		// (if condition true-expr false-expr)
		// (if condition do expr1 expr2 expr3 ...)

		if (args.size() < 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject condition = interpreter.compute(frame, args.get(1));

		// (if condition do expr1 expr2 expr3 ...)
		if (RulpUtil.isAtom(args.get(2), A_DO)) {

			if (args.size() < 4) {
				throw new RException("Invalid parameters: " + args);
			}

			if (!MathUtil.toBoolean(condition)) {
				return O_Nil;
			}

			return interpreter.compute(frame, RulpUtil.toDoExpr(args.listIterator(3)));
		}
		// (if condition true-expr)
		// (if condition true-expr false-expr)
		else {

			if (args.size() != 3 && args.size() != 4) {
				throw new RException("Invalid parameters: " + args);
			}

			IRObject actionClause = null;

			if (MathUtil.toBoolean(condition)) {
				actionClause = args.get(2); // trueClause
			} else {
				actionClause = args.size() > 3 ? args.get(3) : null; // falseClause
			}

			if (actionClause == null) {
				return O_Nil;
			}

			return interpreter.compute(frame, actionClause);
		}
	}

}
