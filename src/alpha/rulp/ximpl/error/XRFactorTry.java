/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.error;

import static alpha.rulp.lang.Constant.C_ERROR_DEFAULT;
import static alpha.rulp.lang.Constant.C_HANDLE;
import static alpha.rulp.lang.Constant.C_HANDLE_ANY;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorTry extends AbsAtomFactorAdapter implements IRFactor {

	public static void defineHandleCase(IRFrame tryFrame, IRExpr handleExpression) throws RException {

		// (e1 (action1) (action2))

		if (handleExpression.size() < 2) {
			throw new RException("invalid handle expression: " + handleExpression);
		}

		IRAtom errId = RulpUtil.asAtom(handleExpression.get(0));

		if (RulpUtil.isVarAtom(errId)) {

			tryFrame.setEntry(C_HANDLE_ANY, RulpFactory.createList(handleExpression.listIterator(1)));

			// Save default error id
			tryFrame.setEntry(C_ERROR_DEFAULT, errId);

		} else {

			tryFrame.setEntry(C_HANDLE + errId.getName(), RulpFactory.createList(handleExpression.listIterator(1)));
		}

	}

	public XRFactorTry(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject try_exp = args.get(1);
		IRFrame tryFrame = RulpFactory.createFrame(frame, "TRY");

		try {

			RulpUtil.incRef(tryFrame);

			IRIterator<? extends IRObject> iter = args.listIterator(2);
			while (iter.hasNext()) {
				defineHandleCase(tryFrame, RulpUtil.asExpression(iter.next()));
			}

			return interpreter.compute(tryFrame, try_exp);

		} finally {
			tryFrame.release();
			RulpUtil.decRef(tryFrame);
		}

	}
}
