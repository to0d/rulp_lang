package alpha.rulp.utils;

import static alpha.rulp.lang.Constant.A_DEFAULT;
import static alpha.rulp.lang.Constant.A_FINAL;
import static alpha.rulp.lang.Constant.A_PRIVATE;
import static alpha.rulp.lang.Constant.A_PUBLIC;
import static alpha.rulp.lang.Constant.A_STATIC;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRFunctionList;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.ximpl.runtime.XRFactorDefun;
import alpha.rulp.ximpl.runtime.XRFunctionList;

public class SubjectUtil {

	private static IRMember _createMemberVar(IRSubject sub, String mbrName, IRObject varValue) throws RException {

		/*****************************************************/
		// Check the variable whether be defined
		/*****************************************************/
		IRMember oldMbr = sub.getMember(mbrName);
		if (oldMbr != null) {

			if (oldMbr.getValue().getType() != RType.VAR) {
				throw new RException(String.format("can't redefine member<%s> type from %s to %s", oldMbr,
						oldMbr.getValue().getType(), RType.VAR));
			}

			if (RulpUtil.isPropertyFinal(oldMbr)) {
				throw new RException("can't redefine final member variable: " + oldMbr);
			}

			if (!RulpUtil.isPropertyInherit(oldMbr)) {
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

	private static IRMember _processModifier(IRMember mbr, IRList mbrExpr, int fromIdx, int endIdx, boolean fun)
			throws RException {

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

		/****************************************************/
		// set subject frame for static function
		/****************************************************/
		if (fun && bStatic) {
			mbr = RulpFactory.createMember(mbr.getSubject(), mbr.getName(), RulpFactory.createFunctionLambda(
					RulpUtil.asFunction(mbr.getValue()), RulpUtil.asSubject(mbr.getSubject()).getSubjectFrame()));
		}

		RulpUtil.setPropertyFinal(mbr, bFinal);
		RulpUtil.setPropertyStatic(mbr, bStatic);

		return mbr;
	}

	private static IRMember _processMemberAttribute(IRMember mbr, String attr, boolean fun) throws RException {

		switch (attr) {
		case A_PUBLIC:
			mbr.setAccessType(RAccessType.PUBLIC);
			break;

		case A_PRIVATE:
			mbr.setAccessType(RAccessType.PRIVATE);
			break;

		case A_DEFAULT:
			mbr.setAccessType(RAccessType.DEFAULT);
			break;

		case A_FINAL:
			RulpUtil.setPropertyFinal(mbr, true);
			break;

		case A_STATIC:
			RulpUtil.setPropertyStatic(mbr, true);
			/****************************************************/
			// set subject frame for static function
			/****************************************************/
			if (fun) {
				mbr = RulpFactory.createMember(mbr.getSubject(), mbr.getName(), RulpFactory.createFunctionLambda(
						RulpUtil.asFunction(mbr.getValue()), RulpUtil.asSubject(mbr.getSubject()).getSubjectFrame()));
			}
			break;

		default:
			throw new RException("unknown attribute:" + attr);
		}

		RulpUtil.addAttribute(mbr, attr);

		return mbr;
	}

	public static IRMember defineMemberFun(IRSubject sub, String mbrName, IRList mbrExpr, IRInterpreter interpreter,
			IRFrame frame) throws RException {

		int mbrExprSize = mbrExpr.size();
		if (mbrExprSize < 4) {
			throw new RException("Invalid member parameters:" + mbrExpr);
		}

		IRMember mbr = null;

		/*****************************************************/
		// Function parameter list
		/*****************************************************/
		List<IRParaAttr> paraAttrs = XRFactorDefun.buildAttrList(mbrExpr.get(2), interpreter, frame);

		// Check duplicate parameters
		if (paraAttrs.size() > 1) {
			Set<String> paraNames = new HashSet<>();
			for (IRParaAttr pa : paraAttrs) {
				if (paraNames.contains(pa.getParaName())) {
					throw new RException("duplicate parameter: " + pa.getParaName());
				}
				paraNames.add(pa.getParaName());
			}
		}

		/*****************************************************/
		// Function body
		/*****************************************************/
		IRExpr funBody = null;
		if (mbrExprSize == 4) {
			funBody = RulpUtil.asExpression(mbrExpr.get(3));

		} else {
			funBody = RulpUtil.toDoExpr(mbrExpr.listIterator(3));
		}

		/*****************************************************/
		// Member
		/*****************************************************/
		IRMember oldMbr = sub.getMember(mbrName);
		if (oldMbr != null) {

			if (oldMbr.getValue().getType() != RType.FUNC) {
				throw new RException("can't redefine member<" + oldMbr + "> to fun");
			}

			if (RulpUtil.isPropertyStatic(oldMbr)) {
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

					if (RulpUtil.isPropertyFinal(oldMbr)) {
						throw new RException("can't redefine final member: " + oldMbr);
					}

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
		// Process attribute
		/*****************************************************/
		if (RulpUtil.hasAttributeList(mbrExpr)) {
			for (String attr : RulpUtil.getAttributeList(mbrExpr)) {
				mbr = _processMemberAttribute(mbr, attr, true);
			}
		}

		sub.setMember(mbrName, mbr);
		return mbr;
	}

	/**
	 * Usage: (defvar mbr:var)
	 */
	public static IRVar defineMemberVar(IRMember mbrObj, IRObject val, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		IRObject subObj = interpreter.compute(frame, mbrObj.getSubject());
		IRSubject sub = RulpUtil.asSubject(subObj);
		String varName = mbrObj.getName();

		if (val != null) {
			val = interpreter.compute(frame, val);
		}

		IRMember mbr = SubjectUtil._createMemberVar(sub, varName, val);
		sub.setMember(varName, mbr);

		return (IRVar) mbr.getValue();
	}

	public static void defineMemberVar(IRSubject sub, IRList mbrExpr, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		if (mbrExpr.size() != 2 && mbrExpr.size() != 3) {
			throw new RException("Invalid member parameters: " + mbrExpr);
		}

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
		IRMember mbr = _createMemberVar(sub, mbrName, varValue);

		/*****************************************************/
		// Process attribute
		/*****************************************************/
		if (RulpUtil.hasAttributeList(mbrExpr)) {
			for (String attr : RulpUtil.getAttributeList(mbrExpr)) {
				mbr = _processMemberAttribute(mbr, attr, false);
			}
		}

		sub.setMember(mbrName, mbr);
	}
}
