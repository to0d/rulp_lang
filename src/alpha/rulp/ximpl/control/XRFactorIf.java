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
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorIf extends AbsAtomFactorAdapter implements IRFactor {

	public static boolean isIf1(IRList args) throws RException {

		// (if condition true-expr)
		return args.size() == 3;
	}

	public static boolean isIf2(IRList args) throws RException {

		// (if condition true-expr false-expr)
		return args.size() == 4 && !RulpUtil.isAtom(args.get(2), A_DO);
	}

	public static boolean isIf3(IRList args) throws RException {

		// (if condition do expr1 expr2 expr3 ...)
		return args.size() >= 4 && RulpUtil.isAtom(args.get(2), A_DO);
	}

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

		// (if condition true-expr)
		if (isIf1(args)) {

			// condition
			if (!RulpUtil.toBoolean(interpreter.compute(frame, args.get(1)))) {
				return O_Nil;
			}

			return interpreter.compute(frame, args.get(2));
		}

		// (if condition true-expr false-expr)
		if (isIf2(args)) {

			// condition
			if (!RulpUtil.toBoolean(interpreter.compute(frame, args.get(1)))) {
				return interpreter.compute(frame, args.get(3));
			}

			return interpreter.compute(frame, args.get(2));
		}

		// (if condition do expr1 expr2 expr3 ...)
		if (isIf3(args)) {

			// condition
			if (!RulpUtil.toBoolean(interpreter.compute(frame, args.get(1)))) {
				return O_Nil;
			}

			if (args.size() == 4) {
				return interpreter.compute(frame, args.get(3));
			}

			return interpreter.compute(frame, RulpUtil.toDoExpr(args.listIterator(3)));
		}

		throw new RException("Invalid parameters: " + args);
	}

}
