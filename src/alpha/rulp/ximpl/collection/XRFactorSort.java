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
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;

public class XRFactorSort extends AbsRFactorAdapter implements IRFactor {

	public XRFactorSort(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRList list = RulpUtil.asList(interpreter.compute(frame, args.get(1)));
		int size = list.size();
		if (size <= 1) {
			return list;
		}

		boolean needSort = false;

		for (int i = 1; !needSort && i < size; ++i) {
			int d = list.get(i - 1).toString().compareTo(list.get(i).toString());
			// need
			if (d > 0) {
				needSort = true;
			}
		}

		if (!needSort) {
			return list;
		}

		ArrayList<IRObject> allObjs = new ArrayList<>();
		for (int i = 0; i < size; ++i) {
			allObjs.add(list.get(i));
		}

		Collections.sort(allObjs, (o1, o2) -> {
			return o1.toString().compareTo(o2.toString());
		});

		return RulpFactory.createList(allObjs);
	}

	@Override
	public boolean isStable() {
		return true;
	}
}
