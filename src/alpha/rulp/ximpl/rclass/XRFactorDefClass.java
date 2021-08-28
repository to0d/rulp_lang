/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.rclass;

import static alpha.rulp.lang.Constant.A_DEFAULT;
import static alpha.rulp.lang.Constant.A_FINAL;
import static alpha.rulp.lang.Constant.A_PRIVATE;
import static alpha.rulp.lang.Constant.A_PUBLIC;
import static alpha.rulp.lang.Constant.A_STATIC;
import static alpha.rulp.lang.Constant.F_DEFUN;
import static alpha.rulp.lang.Constant.F_DEFVAR;
import static alpha.rulp.lang.Constant.F_DO;
import static alpha.rulp.lang.Constant.F_MBR_SUPER;

import java.util.ArrayList;
import java.util.List;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RAccessType;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRFunctionList;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;
import alpha.rulp.ximpl.runtime.XRFactorDefun;
import alpha.rulp.ximpl.runtime.XRFunctionList;

public class XRFactorDefClass extends AbsRFactorAdapter implements IRFactor {

	public static IRMember createMemberVar(IRSubject sub, String mbrName, IRObject varValue) throws RException {

		/*****************************************************/
		// Check the variable whether be defined
		/*****************************************************/
		IRMember oldMbr = sub.getMember(mbrName);
		if (oldMbr != null) {

			if (oldMbr.getValue().getType() != RType.VAR) {
				throw new RException(String.format("can't redefine member<%s> type from %s to %s", oldMbr,
						oldMbr.getValue().getType(), RType.VAR));
			}

			if (oldMbr.isFinal()) {
				throw new RException("can't redefine final member variable: " + oldMbr);
			}

			if (!oldMbr.isInherit()) {
				throw new RException("duplicate member variable: " + oldMbr);
			}
		}

		if (sub.isFinal()) {
			throw new RException("can't define member variable<" + mbrName + "> for final subject: " + sub);
		}

		IRVar var = RulpFactory.createVar(mbrName);
		if (varValue != null) {
			var.setValue(varValue);
		}

		return RulpFactory.createMember(sub, mbrName, var);
	}

	public static IRMember defineClassMemberFun(IRSubject sub, String mbrName, IRList mbrExpr,
			IRInterpreter interpreter, IRFrame frame) throws RException {

		IRMember mbr = null;

		/*****************************************************/
		// Function parameter list
		/*****************************************************/
		List<IRParaAttr> paraAttrs = XRFactorDefun.buildAttrList(mbrExpr.get(2), interpreter, frame);

		/*****************************************************/
		// Function body
		/*****************************************************/
		int mbrExprSize = mbrExpr.size();
		int bodyEndIndex = 3;
		while (bodyEndIndex < mbrExprSize && mbrExpr.get(bodyEndIndex).getType() == RType.EXPR) {
			++bodyEndIndex;
		}

		IRExpr funBody = null;
		if (bodyEndIndex <= 3) {
			throw new RException("no mbr body found: " + mbrExpr);

		} else if (bodyEndIndex == 4) {
			funBody = RulpUtil.asExpression(mbrExpr.get(3));

		} else {

			ArrayList<IRObject> newExpr = new ArrayList<>();
			newExpr.add(RulpFactory.createAtom(F_DO));
			RulpUtil.addAll(newExpr, mbrExpr, 3, bodyEndIndex);

			funBody = RulpFactory.createExpression(newExpr);
		}

		/*****************************************************/
		// Member
		/*****************************************************/
		IRMember oldMbr = sub.getMember(mbrName);
		if (oldMbr != null) {

			if (oldMbr.getValue().getType() != RType.FUNC) {
				throw new RException("can't redefine member<" + oldMbr + "> to fun");
			}

			if (oldMbr.isFinal()) {
				throw new RException("can't redefine final member: " + oldMbr);
			}

			if (oldMbr.isStatic()) {
				throw new RException("can't redefine static member: " + oldMbr);
			}

		} else {

			if (sub.isFinal()) {
				throw new RException("can't define member<" + mbrName + "> for final subject: " + sub);
			}
		}

		IRFunction newFunc = RulpFactory.createFunction(frame, mbrName, paraAttrs, funBody);
		if (oldMbr != null) {

			IRFunction oldFunc = (IRFunction) oldMbr.getValue();
			if (!RulpUtil.isFunctionList(oldFunc)) {

				/*****************************************************/
				// Override
				/*****************************************************/
				if (oldFunc.getSignature().contentEquals(newFunc.getSignature())) {
					XRFunctionList.tryOverride(oldFunc, newFunc);
				}
				/*****************************************************/
				// Create Function List
				/*****************************************************/
				else {

					IRFunctionList funList = RulpFactory.createFunctionList(mbrName);
					funList.addFunc(oldFunc);
					funList.addFunc(newFunc);
					newFunc = funList;
				}

			} else {

				IRFunctionList oldFunList = RulpUtil.asFunctionList(oldFunc);
				IRFunctionList newFunList = RulpFactory.createFunctionList(oldFunList.getName());

				for (IRFunction f : oldFunList.getAllFuncList()) {
					newFunList.addFunc(f);
				}

				newFunList.addFunc(newFunc);
				newFunc = newFunList;
			}
		}

		mbr = RulpFactory.createMember(sub, mbrName, newFunc);
		/*****************************************************/
		// Process modifier
		/*****************************************************/
		processModifier(mbr, mbrExpr, bodyEndIndex, mbrExprSize);

		sub.setMember(mbrName, mbr);

		return mbr;
	}

	public static void defineSubjectMemberVar(IRSubject sub, IRList mbrExpr, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		/*****************************************************/
		// name
		/*****************************************************/
		String mbrName = RulpUtil.asAtom(mbrExpr.get(1)).getName();

		/*****************************************************/
		// value
		/*****************************************************/
		IRObject varValue = null;
		int mbrExprSize = mbrExpr.size();
		if (mbrExprSize >= 3) {
			varValue = interpreter.compute(frame, mbrExpr.get(2));
		}

		/*****************************************************/
		// member
		/*****************************************************/
		IRMember mbr = createMemberVar(sub, mbrName, varValue);

		/*****************************************************/
		// Process modifier
		/*****************************************************/
		processModifier(mbr, mbrExpr, 3, mbrExprSize);

		sub.setMember(mbrName, mbr);
	}

	static void processModifier(IRMember mbr, IRList mbrExpr, int fromIdx, int endIdx) throws RException {

		RAccessType accessType = null;
		boolean bFinal = false;
		boolean bStatic = false;

		for (int i = fromIdx; i < endIdx; ++i) {

			switch (RulpUtil.asAtom(mbrExpr.get(i)).getName()) {
			case A_PUBLIC:
				if (accessType != null) {
					throw new RException("duplicate modifier:" + mbrExpr.get(i));
				}
				accessType = RAccessType.PUBLIC;
				break;

			case A_PRIVATE:
				if (accessType != null) {
					throw new RException("duplicate modifier:" + mbrExpr.get(i));
				}
				accessType = RAccessType.PRIVATE;
				break;

			case A_DEFAULT:
				if (accessType != null) {
					throw new RException("duplicate modifier:" + mbrExpr.get(i));
				}
				accessType = RAccessType.DEFAULT;
				break;

			case A_FINAL:
				if (bFinal) {
					throw new RException("duplicate modifier:" + mbrExpr.get(i));
				}
				bFinal = true;
				break;

			case A_STATIC:
				if (bStatic) {
					throw new RException("duplicate modifier:" + mbrExpr.get(i));
				}
				bStatic = true;
				break;

			default:
				throw new RException("duplicate modifier:" + mbrExpr.get(i));
			}
		}

		if (accessType != null) {
			mbr.setAccessType(accessType);
		}

		if (bFinal) {
			mbr.setFinal(true);
		}

		if (bStatic) {
			mbr.setStatic(true);
		}

	}

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
					defineSubjectMemberVar(defClass, mbrExpr, interpreter, frame);
					break;

				case F_DEFUN:

					if (mbrExprSize < 4) {
						throw new RException("Invalid parameter number for member obj:" + mbrExpr);
					}

					defineClassMemberFun(defClass, RulpUtil.asAtom(mbrExpr.get(1)).getName(), mbrExpr, interpreter,
							frame);
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
