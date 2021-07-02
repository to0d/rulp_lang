/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.rclass;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;

public class XRFactorUse extends AbsRFactorAdapter implements IRFactor {

	public XRFactorUse(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject subObj = interpreter.compute(frame, args.get(1));
		IRSubject sub = RulpUtil.asSubject(subObj);
		IRFrame subFrame = RulpFactory.createFrameSubject(sub, frame);
		IRIterator<? extends IRObject> it = args.listIterator(2);
		
		if (it.hasNext()) {
			interpreter.compute(subFrame, it.next());
		}

		return subObj;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
