/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.factor;

import java.util.ArrayList;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;

public class XRFactorMakeArray extends AbsRFactorAdapter implements IRFactor {

	public XRFactorMakeArray(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		ArrayList<IRObject> elements = new ArrayList<>();

		IRIterator<? extends IRObject> it = args.listIterator(1);
		while (it.hasNext()) {
			elements.add(interpreter.compute(frame, it.next()));
		}

		return RulpFactory.createVaryArray(elements);

	}

	public boolean isThreadSafe() {
		return true;
	}

}
