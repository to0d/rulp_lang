/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.optimize;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;

public class XRFactorCC extends AbsRFactorAdapter implements IRFactor {

	public XRFactorCC(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRExpr expr = RulpUtil.asExpression(args.get(1));

		// support cps already
		if (RuntimeUtil.isSupportOpCPS(frame)) {
			return interpreter.compute(frame, expr);
		}

		IRFrame cpsFrame = RulpFactory.createFrame(frame, "cps");

		try {

			RulpUtil.incRef(cpsFrame);

			IRObject e0 = expr.get(0);

			return CPSUtils.rebuildCpsTree(RulpUtil.asExpression(args.get(1)), frame);

		} finally {
			cpsFrame.release();
			RulpUtil.decRef(cpsFrame);
		}

	}

	@Override
	public boolean isStable() {
		return false;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
