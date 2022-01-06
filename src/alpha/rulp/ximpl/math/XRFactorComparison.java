/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.math;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RRelationalOperator;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.MathUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorComparison extends AbsAtomFactorAdapter implements IRFactor {

	private RRelationalOperator operator;

	public XRFactorComparison(String factorName, RRelationalOperator op) {
		super(factorName);
		this.operator = op;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject a = interpreter.compute(frame, args.get(1));
		IRObject b = interpreter.compute(frame, args.get(2));

		return RulpFactory.createBoolean(MathUtil.computeRelationalExpression(operator, a, b));
	}

}
