/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.control;

import static alpha.rulp.lang.Constant.O_Nan;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.ximpl.error.RReturn;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorReturn extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorReturn(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		int size = args.size();
		if (size > 2) {
			throw new RException("Invalid parameters: " + args);
		}

		throw new RReturn(this, frame, size == 1 ? O_Nan : interpreter.compute(frame, args.get(1)));
	}

}
