/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.attribute;

import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.AttrUtil;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorAddAttribute extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorAddAttribute(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3 && args.size() != 4) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject obj = RulpUtil.lookup(args.get(1), interpreter, frame);
		String attr = interpreter.compute(frame, args.get(2)).asString();

		if (args.size() == 3) {
			AttrUtil.addAttribute(obj, attr);
		} else {
			AttrUtil.setAttribute(obj, attr, args.get(3));
		}

		return O_Nil;
	}

}
