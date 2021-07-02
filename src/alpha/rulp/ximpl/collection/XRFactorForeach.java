/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.collection;

import static alpha.rulp.lang.Constant.O_Nan;

import java.util.ArrayList;

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
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;

public class XRFactorForeach extends AbsRFactorAdapter implements IRFactor {

	static IRObject getResultObject(IRList args, IRInterpreter interpreter, IRFrame factorFrame) throws RException {

		try {

			IRObject rstObj = null;
			IRIterator<? extends IRObject> iter = args.listIterator(2);
			while (iter.hasNext()) {
				rstObj = interpreter.compute(factorFrame, iter.next());
			}
			return rstObj;

		} catch (RReturn r) {
			return r.getReturnValue();

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

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		// (foreach (var cond) (action))

		if (args.size() < 3 || !isForeachParaList(args.get(1))) {
			throw new RException("Invalid parameters: " + args);
		}

		ArrayList<IRObject> rstList = new ArrayList<>();
		IRList paraObj = (IRList) args.get(1);

		IRFrame factorFrame = RulpFactory.createFrame(frame, "FOREACH");

		try {

			RulpUtil.incRef(factorFrame);

			IRVar var = factorFrame.addVar(RulpUtil.asAtom(paraObj.get(0)).getName());
			IRObject cond = interpreter.compute(factorFrame, paraObj.get(1));

			try {

				switch (cond.getType()) {
				case LIST:

					IRIterator<? extends IRObject> iter = ((IRList) cond).iterator();
					while (iter.hasNext()) {
						var.setValue(iter.next());
						IRObject rst = getResultObject(args, interpreter, factorFrame);
						if (rst != O_Nan) {
							rstList.add(rst);
						}
					}

					break;

				case ATOM:
					var.setValue(cond);
					IRObject rst = getResultObject(args, interpreter, factorFrame);
					if (rst != O_Nan) {
						rstList.add(rst);
					}

					break;

				default:
					throw new RException("Not support cond type: " + cond);
				}

			} catch (RBreak b) {
				// break the loop
			}

			return RulpFactory.createList(rstList);

		} finally {
			factorFrame.release();
			RulpUtil.decRef(factorFrame);
		}
	}

	public boolean isThreadSafe() {
		return true;
	}

}
