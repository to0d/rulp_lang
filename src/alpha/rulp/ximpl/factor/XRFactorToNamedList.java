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

public class XRFactorToNamedList extends AbsRFactorAdapter implements IRFactor {

	public XRFactorToNamedList(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject nameObj = interpreter.compute(frame, args.get(1));
		IRList list = RulpUtil.asList(interpreter.compute(frame, args.get(2)));

		String name = null;
		switch (nameObj.getType()) {
		case ATOM:
			name = RulpUtil.asAtom(nameObj).getName();
			break;
		case STRING:
			name = RulpUtil.asString(nameObj).asString();
			break;
		default:
			throw new RException("Invalid parameters: " + args);
		}

		if (list.getNamedName() != null && name.equals(list.getNamedName())) {
			return list;
		}

		if (!list.isConst()) {
			IRList varList = RulpFactory.createVaryNamedList(name);
			RulpUtil.addAll(varList, list.iterator());
			return varList;
		}

		return RulpFactory.createNamedList(list.iterator(), name);
	}

	@Override
	public boolean isStable() {
		return true;
	}

	public boolean isThreadSafe() {
		return true;
	}

}