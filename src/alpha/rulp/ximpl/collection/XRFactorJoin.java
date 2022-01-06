/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.collection;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorJoin extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorJoin(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 2) {
			throw new RException("Invalid parameters: " + args);
		}

		XRCollection joinCol = null;

		for (int i = 1; i < args.size(); ++i) {

			IRObject arg = interpreter.compute(frame, args.get(i));
			if (arg.getType() != RType.LIST) {
				throw new RException(String.format("the type of arg %d is not %s: %s", 1, RType.LIST, args.get(1)));
			}

			XRCollection thisCol = new XRCollection();
			thisCol.add(arg);

			if (joinCol == null) {
				joinCol = thisCol;
			} else {
				joinCol.retainAll(thisCol);
			}

			if (joinCol.isEmpty()) {
				break;
			}
		}

		return joinCol.toList();
	}

}
