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
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorUnion extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorUnion(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		/********************************************/
		// Check parameters
		/********************************************/
		if (args.size() < 2) {
			throw new RException("Invalid parameters: " + args);
		}

		XRCollection unionCol = new XRCollection();

		if (args.size() == 2) {

			IRObject arg = interpreter.compute(frame, args.get(1));
			if (arg.getType() != RType.LIST) {
				throw new RException(String.format("the type of arg %d is not %s: %s", 1, RType.LIST, args.get(1)));
			}

			IRIterator<? extends IRObject> iter = ((IRList) arg).iterator();
			while (iter.hasNext()) {
				IRObject obj = iter.next();

				if (obj.getType() != RType.LIST) {
					throw new RException(String.format("the type of obj %d is not %s: %s", 1, RType.LIST, arg));
				}
				unionCol.add(obj);
			}

		} else {

			for (int i = 1; i < args.size(); ++i) {
				IRObject arg = interpreter.compute(frame, args.get(i));
				if (arg.getType() != RType.LIST) {
					throw new RException(String.format("the type of arg %d is not %s: %s", 1, RType.LIST, arg));
				}
				unionCol.add(arg);
			}
		}

		return unionCol.toList();
	}

}
