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
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorUniq extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorUniq(String factorName) {
		super(factorName);
	}

	static String _getKey(IRFunction fun, IRObject obj, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (fun == null) {
			return RulpUtil.toString(obj);
		}

		return RulpUtil.toString(RuntimeUtil.computeFun2(fun, interpreter, frame, obj));
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2 && args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRList list = RulpUtil.asList(interpreter.compute(frame, args.get(1)));

		// @TODO: this cause issue in p2d - query-stmt
//		if (list.size() <= 1) {
//			return list;
//		}

		IRFunction fun = args.size() == 3 ? RulpUtil.asFunction(interpreter.compute(frame, args.get(2))) : null;

		try {

			RulpUtil.incRef(fun);

			ArrayList<IRObject> newList = new ArrayList<IRObject>();

			Set<String> keySet = new HashSet<>();
			IRIterator<? extends IRObject> it = list.iterator();

			while (it.hasNext()) {

				IRObject obj = it.next();
				String key = _getKey(fun, obj, interpreter, frame);
				if (!keySet.contains(key)) {
					keySet.add(key);
					newList.add(obj);
				}
			}

			if (newList.size() == list.size()) {
				return list;
			}

			return RulpFactory.createList(newList);

		} finally {

			RulpUtil.decRef(fun);
		}

	}

}
