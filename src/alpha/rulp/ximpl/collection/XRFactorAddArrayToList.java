/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.collection;

import static alpha.rulp.lang.Constant.O_Nil;

import java.util.ArrayList;

import alpha.rulp.lang.IRArray;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;

public class XRFactorAddArrayToList extends AbsRFactorAdapter implements IRFactor {

	public XRFactorAddArrayToList(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRList l1 = RulpUtil.asList(interpreter.compute(frame, args.get(1)));
		IRArray arr2 = RulpUtil.asArray(interpreter.compute(frame, args.get(2)));

		if (arr2.isEmpty()) {
			return l1;
		}

		ArrayList<IRObject> rstList = new ArrayList<>();
		RulpUtil.addAll(rstList, l1);

		int size = arr2.size();
		for (int i = 0; i < size; ++i) {
			IRObject obj = arr2.get(i);
			if (obj == null) {
				obj = O_Nil;
			}
			
			rstList.add(obj);
		}

		return RulpFactory.createList(rstList);
	}

	public boolean isThreadSafe() {
		return true;
	}

}
