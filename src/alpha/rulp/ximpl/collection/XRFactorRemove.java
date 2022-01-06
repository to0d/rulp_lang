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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

public class XRFactorRemove extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorRemove(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 3) {
			throw new RException("Invalid parameters: " + args);
		}

		Map<String, List<IRObject>> uniqMap = new HashMap<>();
		ArrayList<IRObject> rstList = new ArrayList<>();

		IRList list = RulpUtil.asList(interpreter.compute(frame, args.get(1)));
		{
			IRIterator<? extends IRObject> it = list.iterator();
			while (it.hasNext()) {

				IRObject obj = it.next();
				rstList.add(obj);

				String uniqName = RulpUtil.toUniqString(obj);
				List<IRObject> uniqList = uniqMap.get(uniqName);
				if (uniqList == null) {
					uniqList = new LinkedList<>();
					uniqMap.put(uniqName, uniqList);
				}

				uniqList.add(obj);
			}
		}

		IRIterator<? extends IRObject> it = args.listIterator(2);
		while (it.hasNext()) {

			IRObject obj = it.next();
			String uniqName = RulpUtil.toUniqString(obj);

			List<IRObject> uniqList = uniqMap.get(uniqName);
			if (uniqList == null) {
				continue;
			}

			rstList.removeAll(uniqList);
		}

		return RulpFactory.createList(rstList);
	}

}
