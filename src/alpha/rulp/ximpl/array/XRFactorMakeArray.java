/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.array;

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

public class XRFactorMakeArray extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorMakeArray(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() > 3) {
			throw new RException("Invalid parameters: " + args);
		}

		int dimension = args.size() - 1;
		if (dimension == 0) {
			return RulpFactory.createVaryArray();
		}

		int sizes[] = new int[dimension];
		int index = 0;

		IRIterator<? extends IRObject> it = args.listIterator(1);
		while (it.hasNext()) {
			sizes[index++] = RulpUtil.asInteger(interpreter.compute(frame, it.next())).asInteger();
		}

		return RulpFactory.createVaryArray(sizes);
	}

}
