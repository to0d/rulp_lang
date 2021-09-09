/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.rclass;

import static alpha.rulp.lang.Constant.A_FINAL;
import static alpha.rulp.lang.Constant.F_DEFUN;
import static alpha.rulp.lang.Constant.F_DEFVAR;
import static alpha.rulp.lang.Constant.F_MBR_SUPER;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.SubjectUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;

public class XRFactorDefClass extends AbsRFactorAdapter implements IRFactor {

	public XRFactorDefClass(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 3) {
			throw new RException("Invalid parameters: " + args);
		}

		/*****************************************************/
		// class name
		/*****************************************************/
		String className = RulpUtil.asAtom(args.get(1)).getName();

		/*****************************************************/
		// super class
		/*****************************************************/
		IRClass superClass = null;

		IRObject superExprObj = args.get(2);
		if (superExprObj.getType() != RType.EXPR) {
			throw new RException("Invalid super class parameter: " + superExprObj);
		}

		IRExpr expr = (IRExpr) superExprObj;
		if (expr.size() == 0) {

			// no super class

		} else if (expr.size() == 1) {

			superClass = RulpUtil.asClass(interpreter.compute(frame, expr.get(0)));
			if (superClass.isFinal()) {
				throw new RException("Can't inherit final class: " + superClass);
			}

		} else {

			throw new RException("Invalid super class parameter: " + superExprObj);
		}

		IRClass defClass = RulpFactory.createClassDefClass(className, frame, superClass);
		if (superClass != null) {
			RulpUtil.setMember(defClass, F_MBR_SUPER, superClass);
		}

		Boolean isFinal = null;

		/*****************************************************/
		// members
		/*****************************************************/
		IRIterator<? extends IRObject> it = args.listIterator(3);
		while (it.hasNext()) {

			IRObject mbrObj = it.next();
			switch (mbrObj.getType()) {
			case ATOM:
				switch (RulpUtil.asAtom(mbrObj).getName()) {
				case A_FINAL:
					if (isFinal != null) {
						throw new RException("duplicate modifier:" + mbrObj);
					}
					isFinal = true;
					break;
				default:
					throw new RException("invalid modifier: " + mbrObj);
				}
				break;

			case EXPR:

				IRExpr mbrExpr = (IRExpr) mbrObj;

				int mbrExprSize = mbrExpr.size();
				if (mbrExprSize < 2) {
					throw new RException("Invalid member expression: " + mbrExpr);
				}

				IRObject e0 = mbrExpr.get(0);
				if (e0.getType() != RType.ATOM) {
					throw new RException("Invalid member expression: " + mbrExpr);
				}

				switch (((IRAtom) e0).getName()) {
				case F_DEFVAR:
					SubjectUtil.defineMemberVar(defClass, mbrExpr, interpreter, frame);
					break;

				case F_DEFUN:

					if (mbrExprSize < 4) {
						throw new RException("Invalid parameter number for member obj:" + mbrExpr);
					}

					SubjectUtil.defineMemberFun(defClass, RulpUtil.asAtom(mbrExpr.get(1)).getName(), mbrExpr,
							interpreter, frame);
					break;

				default:
					throw new RException("Invalid member obj: " + mbrExpr);
				}

				break;

			default:
				throw new RException("Invalid member obj: " + mbrObj);
			}
		}

		if (isFinal != null) {
			defClass.setFinal(isFinal);
		}

		/*****************************************************/
		// Update Entry
		/*****************************************************/
		RulpUtil.addFrameObject(frame, defClass);

		return defClass;

	}

	public boolean isThreadSafe() {
		return true;
	}
}
