/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.collection;

import static alpha.rulp.lang.Constant.F_SORT;

import java.util.Collections;
import java.util.List;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorSort extends AbsAtomFactorAdapter implements IRFactor {

	static IRObject _compare_default(IRList list) throws RException {

		int size = list.size();

		boolean needSort = false;
		for (int i = 1; !needSort && i < size; ++i) {

			int d = RulpUtil.compare(list.get(i - 1), list.get(i));
			if (d != 0) {
				needSort = true;
			}
		}

		if (!needSort) {
			return list;
		}

		List<IRObject> newList = RulpUtil.toArray(list);
		Collections.sort(newList, (o1, o2) -> {
			try {
				return RulpUtil.compare(o1, o2);
			} catch (RException e) {
				e.printStackTrace();
				return 0;
			}
		});

		return RulpFactory.createList(newList);
	}

	public XRFactorSort(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2 && args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRList list = RulpUtil.asList(interpreter.compute(frame, args.get(1)));
		int size = list.size();
		if (size <= 1) {
			return list;
		}

		if (args.size() == 2) {
			return _compare_default(list);
		}

		IRFunction cmpFunc = RulpUtil.asFunction(interpreter.compute(frame, args.get(2)));
		IRFrame sortFrame = RulpFactory.createFrame(frame, F_SORT);
		RulpUtil.incRef(sortFrame);

		try {

			boolean needSort = false;
			for (int i = 1; !needSort && i < size; ++i) {
				int d = RulpUtil.asInteger(interpreter.compute(sortFrame,
						RulpFactory.createExpression(cmpFunc, list.get(i - 1), list.get(i)))).asInteger();
				if (d != 0) {
					needSort = true;
				}
			}

			if (!needSort) {
				return list;
			}

			List<IRObject> newList = RulpUtil.toArray(list);
			Collections.sort(newList, (o1, o2) -> {
				try {
					return RulpUtil
							.asInteger(interpreter.compute(sortFrame, RulpFactory.createExpression(cmpFunc, o1, o2)))
							.asInteger();
				} catch (RException e) {
					e.printStackTrace();
					return 0;
				}
			});

			return RulpFactory.createList(newList);

		} finally {
			sortFrame.release();
			RulpUtil.decRef(sortFrame);
		}

	}

}
