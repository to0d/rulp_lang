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
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;

public class XRFactorAdd extends AbsRFactorAdapter implements IRFactor {

	public XRFactorAdd(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 3) {
			throw new RException("Invalid parameters: " + args);
		}

		ArrayList<IRObject> rstList = new ArrayList<>();
		RulpUtil.addAll(rstList, RulpUtil.asList(interpreter.compute(frame, args.get(1))));

		IRIterator<? extends IRObject> it = args.listIterator(2);
		while (it.hasNext()) {
			rstList.add(interpreter.compute(frame, it.next()));
		}

		return RulpFactory.createList(rstList);
	}

	public boolean isThreadSafe() {
		return true;
	}

}
