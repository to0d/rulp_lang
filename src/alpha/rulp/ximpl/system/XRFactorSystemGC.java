/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.system;

import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;

public class XRFactorSystemGC extends AbsRFactorAdapter implements IRFactor {

	public XRFactorSystemGC(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		long timestamp = System.currentTimeMillis();
		System.gc();
		interpreter.out("GC: time=" + (System.currentTimeMillis() - timestamp) + " m");
		return O_Nil;
	}

	@Override
	public boolean isStable() {
		return false;
	}

	public boolean isThreadSafe() {
		return true;
	}

}
