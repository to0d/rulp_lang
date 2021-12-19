/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.collection;

import alpha.rulp.lang.IRArray;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;

public class XRFactorGetOfArray extends AbsRFactorAdapter implements IRFactor {

	public XRFactorGetOfArray(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRArray array = RulpUtil.asArray(interpreter.compute(frame, args.get(1)));
		int dim = array.getDimension();
		if (dim != (args.size() - 2)) {
			throw new RException(String.format("Unmatch dimension: expect=%d, actual=%d", args.size() - 2, dim));
		}

		int[] indexs = new int[dim];
		for (int i = 0; i < dim; ++i) {
			indexs[i] = RulpUtil.asInteger(interpreter.compute(frame, args.get(2 + i))).asInteger();
		}

		return array.get(indexs);
	}

	@Override
	public boolean isStable() {
		return true;
	}

	public boolean isThreadSafe() {
		return true;
	}
}