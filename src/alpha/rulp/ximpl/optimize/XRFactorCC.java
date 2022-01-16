/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_OPT_CCO;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.AttrUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorCC extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorCC(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRExpr expr = RulpUtil.asExpression(args.get(1));
		IRFunction func = RulpUtil.asFunction(RulpUtil.lookup(expr.get(0), interpreter, frame));
		if (!AttrUtil.containAttribute(func, A_OPT_CCO)) {

			if (func.isList()) {
				throw new RException("not support list: " + args);
			}

			IRExpr funBody = func.getFunBody();
			String funcName = func.getName();
			IRExpr funBody2 = CCOUtil.rebuild(func.getFunBody(), func.getParaAttrs(), funcName, interpreter, frame);
			if (funBody2 != funBody) {
				func = RulpFactory.createFunction(frame, funcName, func.getParaAttrs(), funBody2);
				frame.setEntry(funcName, func);
			}

			AttrUtil.addAttribute(func, A_OPT_CCO);
		}

		return interpreter.compute(frame, expr);

	}

}
