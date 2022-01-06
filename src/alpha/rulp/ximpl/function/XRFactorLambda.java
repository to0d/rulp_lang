/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.function;

import static alpha.rulp.lang.Constant.A_LAMBDA;

import java.util.ArrayList;
import java.util.List;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorLambda extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorLambda(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 3) {
			throw new RException("Invalid parameters: " + args);
		}

		/*****************************************************/
		// Parameter list
		/*****************************************************/
		IRObject paraObj = args.get(1);
		if (!RulpUtil.isPureAtomList(paraObj)) {
			throw new RException("Invalid para type: " + paraObj);
		}

		List<IRParaAttr> paraAttrs = new ArrayList<>();
		IRIterator<? extends IRObject> iter = ((IRList) paraObj).iterator();
		while (iter.hasNext()) {
			paraAttrs.add(RulpFactory.createParaAttr(RulpUtil.asAtom(iter.next()).getName()));
		}

		/*****************************************************/
		// Function body
		/*****************************************************/
		IRExpr funBody = null;
		if (args.size() == 3) {
			funBody = RulpUtil.asExpression(args.get(2));

		} else if (args.size() > 3) {
			funBody = RulpUtil.toDoExpr(args.listIterator(2));

		} else {

			throw new RException("Invalid args size: " + args.size());
		}

		return RulpFactory.createFunctionLambda(RulpFactory.createFunction(frame, A_LAMBDA, paraAttrs, funBody), frame);
	}

}
