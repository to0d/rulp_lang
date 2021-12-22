/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_OP_CPS;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.error.RReturn;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorReturnCPS extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorReturnCPS(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject rst = CPSUtil.returnCPS(RulpUtil.asExpression(args.get(1)), frame);
		if (rst.getType() == RType.EXPR) {
			RulpUtil.addAttribute(rst, A_OP_CPS);
		}

		throw new RReturn(this, frame, rst);
	}

	@Override
	public boolean isThreadSafe() {
		return true;
	}

	@Override
	public boolean isStable() {
		return true;
	}
}