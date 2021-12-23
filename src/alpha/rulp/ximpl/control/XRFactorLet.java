/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.control;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorLet extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorLet(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRList varList = RulpUtil.asExpression(args.get(1));
		if (varList.size() < 2 || (varList.size() % 2) != 0) {
			throw new RException("Invalid parameters: " + args);
		}

		IRFrame letFrame = RulpFactory.createFrame(frame, "LET");

		try {

			RulpUtil.incRef(letFrame);

			IRIterator<? extends IRObject> it = varList.iterator();
			while (it.hasNext()) {
				String varName = (RulpUtil.asAtom(it.next())).getName();
				IRVar var = RulpFactory.createVar(varName);
				letFrame.setEntry(varName, var);
				IRObject val = interpreter.compute(letFrame, it.next());
				var.setValue(val);
			}

			return interpreter.compute(letFrame, args.get(2));

		} finally {
			letFrame.release();
			RulpUtil.decRef(letFrame);
		}
	}

	@Override
	public boolean isStable() {
		return true;
	}

	public boolean isThreadSafe() {
		return true;
	}
}