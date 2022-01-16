/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_LAMBDA;
import static alpha.rulp.lang.Constant.A_OPT_CCO;

import java.util.ArrayList;
import java.util.Collections;

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
		IRFunction fun = RulpUtil.asFunction(RulpUtil.lookup(expr.get(0), interpreter, frame));
		if (fun.isList()) {
			throw new RException("not support list: " + args);
		}

		IRExpr funBody = fun.getFunBody();
		IRExpr funBody2 = CCOUtil.rebuild(fun.getFunBody(), fun.getParaAttrs(), fun.getName(), interpreter, frame);
		if (funBody2 == funBody) {
			return interpreter.compute(frame, expr);
		}

		if (!AttrUtil.containAttribute(fun, A_OPT_CCO)) {

		}

		IRFunction lambda = RulpFactory
				.createFunctionLambda(RulpFactory.createFunction(frame, A_LAMBDA, fun.getParaAttrs(), funBody2), frame);

		ArrayList<IRObject> newExpr = new ArrayList<>();
		newExpr.add(lambda);
		RulpUtil.addAll(newExpr, expr.listIterator(1));

		return interpreter.compute(frame, RulpFactory.createExpression(newExpr));
	}

}
