/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.attribute;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.AttrUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorIsRecursive extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorIsRecursive(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject obj = args.get(1);
		while (obj.getType() != RType.FUNC) {

			switch (obj.getType()) {
			case EXPR:
				obj = interpreter.compute(frame, obj);
				break;

			case ATOM:
				IRObject obj2 = RulpUtil.lookup(obj, interpreter, frame);
				if (obj2 == obj) {
					throw new RException("func not found: " + obj);
				}
				obj = obj2;
				break;

			default:
				throw new RException("not func: " + obj);
			}

		}

		return RulpFactory.createBoolean(AttrUtil.isRecursive((IRFunction) obj));
	}

}
