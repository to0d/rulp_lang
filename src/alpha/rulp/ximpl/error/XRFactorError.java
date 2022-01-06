/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.error;

import static alpha.rulp.lang.Constant.C_ERROR_DEFAULT;
import static alpha.rulp.lang.Constant.C_HANDLE;
import static alpha.rulp.lang.Constant.C_HANDLE_ANY;
import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRError;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RError;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorError extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorError(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2 && args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRAtom errId = RulpUtil.asAtom(interpreter.compute(frame, args.get(1)));
		IRObject errValue = null;
		if (args.size() == 3) {
			errValue = interpreter.compute(frame, args.get(2));
		}

		IRError err = RulpFactory.createError(interpreter, errId, errValue);

		String handleName = C_HANDLE + errId.getName();
		String valueName = errId.getName();

		IRFrameEntry handlEntry = frame.getEntry(handleName);
		if (handlEntry == null) {

			handlEntry = frame.getEntry(C_HANDLE_ANY);
			if (handlEntry == null) {
				throw new RError(frame, this, err);
			}

			valueName = RulpUtil.asAtom(frame.getEntry(C_ERROR_DEFAULT).getObject()).getName();
		}

		IRList actionList = RulpUtil.asList(handlEntry.getObject());
		frame.setEntry(valueName, err);

		IRIterator<? extends IRObject> iter = actionList.iterator();
		while (iter.hasNext()) {
			interpreter.compute(frame, iter.next());
		}

		return O_Nil;
	}

}
