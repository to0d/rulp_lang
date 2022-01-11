/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.array;

import alpha.rulp.lang.IRArray;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorSeta extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorSeta(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 4) {
			throw new RException("Invalid parameters: " + args.toString());
		}

		IRArray arr = RulpUtil.asArray(interpreter.compute(frame, args.get(1)));
		int index = RulpUtil.asInteger(interpreter.compute(frame, args.get(2))).asInteger();
		IRObject obj = interpreter.compute(frame, args.get(3));
		arr.set(index, obj);

		return arr;
	}

}
