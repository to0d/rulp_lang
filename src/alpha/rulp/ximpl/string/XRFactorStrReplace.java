/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.string;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorStrReplace extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorStrReplace(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 4) {
			throw new RException("Invalid parameters: " + args);
		}

		String str = RulpUtil.asString(interpreter.compute(frame, args.get(1))).asString();
		String target = RulpUtil.asString(interpreter.compute(frame, args.get(2))).asString();
		String replacement = RulpUtil.asString(interpreter.compute(frame, args.get(3))).asString();

		return RulpFactory.createString(str.replace(target, replacement));
	}

	public boolean isThreadSafe() {
		return true;
	}
}