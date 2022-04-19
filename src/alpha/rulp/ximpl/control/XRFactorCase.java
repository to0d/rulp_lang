/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.control;

import static alpha.rulp.lang.Constant.A_QUESTION;
import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorCase extends AbsAtomFactorAdapter implements IRFactor {

	public static boolean matchCaseValue(IRObject value, IRObject caseValue) throws RException {

		if (caseValue.getType() == RType.ATOM && caseValue.asString().equals(A_QUESTION)) {
			return true;
		}

		if (RulpUtil.equal(value, caseValue)) {
			return true;
		}

		if (caseValue.getType() == RType.LIST) {

			IRIterator<? extends IRObject> it = RulpUtil.asList(caseValue).iterator();
			while (it.hasNext()) {
				if (RulpUtil.equal(value, it.next())) {
					return true;
				}
			}
		}

		return false;
	}

	public XRFactorCase(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject value = interpreter.compute(frame, args.get(1));

		IRIterator<? extends IRObject> it = args.listIterator(2);
		while (it.hasNext()) {

			IRExpr caseClause = RulpUtil.asExpression(it.next());
			if (caseClause.size() != 2) {
				throw new RException("Invalid case clause: " + caseClause);
			}

			IRObject caseValue = interpreter.compute(frame, caseClause.get(0));
			if (matchCaseValue(value, caseValue)) {
				return interpreter.compute(frame, caseClause.get(1));
			}
		}

		return O_Nil;
	}

}
