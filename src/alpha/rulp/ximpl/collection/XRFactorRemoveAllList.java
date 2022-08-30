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
import java.util.HashSet;
import java.util.Set;

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

public class XRFactorRemoveAllList extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorRemoveAllList(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRList list1 = RulpUtil.asList(interpreter.compute(frame, args.get(1)));
		if (list1.isEmpty()) {
			return list1;
		}

		IRList list2 = RulpUtil.asList(interpreter.compute(frame, args.get(2)));
		if (list2.isEmpty()) {
			return list1;
		}

		Set<String> removedNameSet = new HashSet<>();
		{
			IRIterator<? extends IRObject> it = list2.iterator();
			while (it.hasNext()) {
				removedNameSet.add(RulpUtil.toUniqString(it.next()));
			}
		}

		ArrayList<IRObject> rstList = null;
		int size = list1.size();
		int deleteCount = 0;
		for (int i = 0; i < size; ++i) {

			IRObject obj = list1.get(i);

			// remove
			if (removedNameSet.contains(RulpUtil.toUniqString(obj))) {
				if (rstList == null) {
					rstList = new ArrayList<>();
					for (int j = 0; j < i; ++j) {
						rstList.add(list1.get(j));
					}
				}

				deleteCount++;
			}
			// keep
			else {
				if (rstList != null) {
					rstList.add(obj);
				}
			}
		}

		if (deleteCount == 0) {
			return list1;
		}

		return RulpFactory.createList(rstList);
	}

}
