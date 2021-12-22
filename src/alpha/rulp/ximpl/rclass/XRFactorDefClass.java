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

		int argSize = args.size();
		if (args.size() < 2) {
			throw new RException("Invalid parameters: " + args);
		}

		/*****************************************************/
		// class name
		/*****************************************************/
		String className = RulpUtil.asAtom(args.get(1)).getName();

		/*****************************************************/
		// find super expression
		/*****************************************************/
		IRClass superClass = null;
		int superExprIndex = -1;
		for (int i = 2; i < argSize; ++i) {
			IRExpr superExpr = RulpUtil.asExpression(args.get(i));

			if (!superExpr.isEmpty() && superExpr.get(0).asString().equals(F_MBR_SUPER)) {

				if (superExpr.size() != 2) {
					throw new RException("Invalid super expression: " + superExpr);
				}

				superExprIndex = i;
				superClass = RulpUtil.asClass(interpreter.compute(frame, superExpr.get(1)));
				if (superClass.isFinal()) {
					throw new RException("Can't inherit final class: " + superClass);
				}
				break;
			}
		}

		IRClass defClass = RulpFactory.createClassDefClass(className, frame, superClass);
		if (superClass != null) {
			RulpUtil.setMember(defClass, F_MBR_SUPER, superClass);
		}

		/*****************************************************/
		// members
		/*****************************************************/
		for (int i = 2; i < argSize; ++i) {

			IRExpr mbrExpr = RulpUtil.asExpression(args.get(i));

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
				SubjectUtil.defineMemberFun(defClass, RulpUtil.asAtom(mbrExpr.get(1)).getName(), mbrExpr, interpreter,
						frame);
				break;

			case F_MBR_SUPER:
				if (superExprIndex != i) {
					throw new RException("duplicated super expression: " + mbrExpr);
				}

				break;

			default:
				throw new RException("Invalid member obj: " + mbrExpr);
			}
		}

		if (RulpUtil.hasAttributeList(args)) {
			for (String attr : RulpUtil.getAttributeList(args)) {
				switch (attr) {
				case A_FINAL:
					defClass.setFinal(true);
					break;

				default:
					throw new RException("unknown attribute:" + attr);
				}

				RulpUtil.addAttribute(defClass, attr);
			}
		}

		/*****************************************************/
		// Update Entry
		/*****************************************************/
		RulpUtil.addFrameObject(frame, defClass);

		return defClass;

	}

	@Override
	public boolean isStable() {
		return true;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
