/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.collection;

import java.util.ArrayList;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;

public class XRFactorAddListToList extends AbsRFactorAdapter implements IRFactor {

	public XRFactorAddListToList(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRList l1 = RulpUtil.asList(interpreter.compute(frame, args.get(1)));
		IRList l2 = RulpUtil.asList(interpreter.compute(frame, args.get(2)));

		if (l2.isEmpty()) {
			return l1;
		}

		if (l1.isEmpty()) {
			return l2;
		}

		ArrayList<IRObject> rstList = new ArrayList<>();
		RulpUtil.addAll(rstList, l1);
		RulpUtil.addAll(rstList, l2);

		return RulpFactory.createList(rstList);
	}

	public boolean isThreadSafe() {
		return true;
	}

}
