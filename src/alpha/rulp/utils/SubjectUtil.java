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

	private static void _processModifier(IRMember mbr, IRList mbrExpr, int fromIdx, int endIdx) throws RException {

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

		RulpUtil.setPropertyFinal(mbr, bFinal);
		RulpUtil.setPropertyStatic(mbr, bStatic);
	}

	public static IRMember defineMemberFun(IRSubject sub, String mbrName, IRList mbrExpr, IRInterpreter interpreter,
			IRFrame frame) throws RException {

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
			funBody = RulpUtil.toDoExpr(RulpUtil.subList(mbrExpr, 3, bodyEndIndex));
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
		// Process modifier
		/*****************************************************/
		_processModifier(mbr, mbrExpr, bodyEndIndex, mbrExprSize);

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
		// Process modifier
		/*****************************************************/
		_processModifier(mbr, mbrExpr, 3, mbrExprSize);

		sub.setMember(mbrName, mbr);
	}
}
