/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.io;

import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.LoadUtil;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorLoad extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorLoad(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2 && args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject loadObj = interpreter.compute(frame, args.get(1));

		switch (loadObj.getType()) {
		case STRING:

			String charset = null;
			if (args.size() == 3) {
				charset = RulpUtil.asString(interpreter.compute(frame, args.get(2))).asString();
			}

			String name = RulpUtil.asString(loadObj).asString();
			if (name.endsWith(".jar")) {
				LoadUtil.loadJar(interpreter, frame, name);
			} else {
				LoadUtil.loadScript(interpreter, frame, name, charset);
			}

			break;

//		case ATOM:
//			LoadUtil.loadSystemRulp(interpreter, frame, RulpUtil.asAtom(loadObj).getName());
//			break;
//
//		case CLASS:
//			LoadUtil.loadSystemRulp(interpreter, frame, RulpUtil.asClass(loadObj).getClassName());
//			break;

		default:
			throw new RException("Invalid parameters: " + args);
		}

		return O_Nil;
	}

}