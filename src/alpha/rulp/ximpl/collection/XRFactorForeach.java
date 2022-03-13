/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.collection;

import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.F_FOREACH;
import static alpha.rulp.lang.Constant.O_Nan;

import alpha.rulp.lang.IRArray;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.error.RBreak;
import alpha.rulp.ximpl.error.RContinue;
import alpha.rulp.ximpl.error.RReturn;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorForeach extends AbsAtomFactorAdapter implements IRFactor {

	static IRObject getResultObject(IRList args, IRInterpreter interpreter, IRFrame frame, IRFrame rtFrame)
			throws RException {

		try {

			IRObject rstObj = null;
			IRIterator<? extends IRObject> iter = args.listIterator(2);
			while (iter.hasNext()) {
				rstObj = interpreter.compute(frame, iter.next());
			}
			return rstObj;

		} catch (RReturn r) {
			return r.returnValue(rtFrame);

		} catch (RContinue c) {
			return O_Nan;
		}
	}

	public static boolean isForeachParaList(IRObject obj) throws RException {

		if (obj.getType() != RType.EXPR) {
			return false;
		}

		IRList list = (IRList) obj;

		return list.size() == 2 && RulpUtil.isVarAtom(list.get(0));
	}

	public XRFactorForeach(String factorName) {
		super(factorName);
	}

	public static IRObject _do_foreach(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		IRFrame doFrame = RulpFactory.createFrame(frame, A_DO);
		RulpUtil.incRef(doFrame);

		try {
			return getResultObject(args, interpreter, doFrame, frame);
		} finally {
			doFrame.release();
			RulpUtil.decRef(doFrame);
		}
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		// (foreach (var cond) (action))

		if (args.size() < 3 || !isForeachParaList(args.get(1))) {
			throw new RException("Invalid parameters: " + args);
		}

		IRList paraObj = (IRList) args.get(1);

		IRFrame factorFrame = RulpFactory.createFrame(frame, F_FOREACH);
		RulpUtil.incRef(factorFrame);

		try {

			IRList rstList = RulpFactory.createVaryList();

			IRVar var = RulpUtil.addVar(frame, RulpUtil.asAtom(paraObj.get(0)).getName());

			IRObject cond = interpreter.compute(factorFrame, paraObj.get(1));
			RulpUtil.incRef(cond);

			try {

				switch (cond.getType()) {
				case LIST:

					IRIterator<? extends IRObject> iter = ((IRList) cond).iterator();
					while (iter.hasNext()) {
						var.setValue(iter.next());
						IRObject rst = _do_foreach(args, interpreter, factorFrame);
						if (rst != null && rst != O_Nan) {
							rstList.add(rst);
						}
					}

					break;

				case ARRAY:

					IRArray arr = RulpUtil.asArray(cond);
					int size = arr.size();
					for (int i = 0; i < size; ++i) {
						var.setValue(arr.get(i));
						IRObject rst = _do_foreach(args, interpreter, factorFrame);
						if (rst != null && rst != O_Nan) {
							rstList.add(rst);
						}
					}

					break;

				case ATOM:

					var.setValue(cond);
					IRObject rst = getResultObject(args, interpreter, factorFrame, factorFrame);
					if (rst != O_Nan) {
						rstList.add(rst);
					}

					break;

				default:
					throw new RException("Not support cond type: " + cond);
				}

			} catch (RBreak b) {
				// break the loop
			} finally {
				RulpUtil.decRef(cond);
			}

			return rstList;

		} finally {
			factorFrame.release();
			RulpUtil.decRef(factorFrame);
		}
	}

}
