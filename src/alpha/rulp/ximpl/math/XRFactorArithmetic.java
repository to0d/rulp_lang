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
import alpha.rulp.lang.RArithmeticOperator;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.MathUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;
import alpha.rulp.ximpl.optimize.OptUtil;
import alpha.rulp.ximpl.string.XRFactorToString;

public class XRFactorArithmetic extends AbsAtomFactorAdapter implements IRFactor {

	private RArithmeticOperator operator;

	public XRFactorArithmetic(String factorName, RArithmeticOperator type) {
		super(factorName);
		this.operator = type;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRIterator<? extends IRObject> it = args.listIterator(1);
		IRObject rst = interpreter.compute(frame, it.next());

		if (operator == RArithmeticOperator.ADD && rst.getType() == RType.STRING) {

			if (!it.hasNext()) {
				return rst;
			}

			StringBuffer sb = new StringBuffer();
			sb.append(rst.asString());

			while (it.hasNext()) {
				IRObject next = interpreter.compute(frame, it.next());
				sb.append(RulpUtil.asString(XRFactorToString.toString(next, interpreter)).asString());
			}

			rst = RulpFactory.createString(sb.toString());

		} else {

			while (it.hasNext()) {

				// (* 0 ...) ==> 0

				if (OptUtil.isConstNumber(rst, 0)) {

					if (operator == RArithmeticOperator.BY) {
						return rst;
					}

					if (operator == RArithmeticOperator.POWER) {
						return rst;
					}
				}

				IRObject next = interpreter.compute(frame, it.next());
				rst = MathUtil.computeArithmeticExpression(operator, rst, next);
			}
		}

		return rst;
	}

	@Override
	public boolean isStable() {
		return true;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
