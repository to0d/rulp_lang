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
import java.util.Collections;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorReverse extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorReverse(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter intepreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRList list = RulpUtil.asList(intepreter.compute(frame, args.get(1)));
		if (list.size() <= 1) {
			return list;
		}

		ArrayList<IRObject> rvsList = new ArrayList<>();
		IRIterator<? extends IRObject> it = list.iterator();
		while (it.hasNext()) {
			rvsList.add(it.next());
		}

		Collections.reverse(rvsList);
		return RulpFactory.createList(rvsList);
	}

}
