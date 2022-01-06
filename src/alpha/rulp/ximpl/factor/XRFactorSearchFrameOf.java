/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.factor;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;

public class XRFactorSearchFrameOf extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorSearchFrameOf(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 1 && args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRFrame tgtFrame = frame;
		if (args.size() > 1) {
			tgtFrame = RulpUtil.asFrame(interpreter.compute(frame, args.get(1)));
		}

		return RulpFactory.createList(tgtFrame.getSearchFrameList());
	}

}
