/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.factor;

import alpha.rulp.lang.IRArray;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.AttrUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;

public class XRFactorToVary extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorToVary(String factorName) {
		super(factorName);
	}

	static IRObject toVary(IRObject obj, IRFrame frame) throws RException {

		if (!AttrUtil.isConst(obj, frame)) {
			return obj;
		}

		switch (obj.getType()) {
		case ARRAY:

			IRArray varyArray = RulpFactory.createVaryArray();
			IRArray constArray = RulpUtil.asArray(obj);

			int size = constArray.size();
			for (int i = 0; i < size; ++i) {
				varyArray.add(constArray.get(i));
			}

			return varyArray;

		default:
			throw new RException("Can't to-vary: " + obj);
		}
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		return toVary(interpreter.compute(frame, args.get(1)), frame);
	}

}