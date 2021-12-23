/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.function;

import static alpha.rulp.lang.Constant.A_OPT_CC0;
import static alpha.rulp.lang.Constant.A_OPT_TCO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRFunctionList;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.SubjectUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;
import alpha.rulp.ximpl.optimize.OpTcoUtil;
import alpha.rulp.ximpl.optimize.SCOUtil;

public class XRFactorDefun extends AbsAtomFactorAdapter implements IRFactor {

	public static List<IRParaAttr> buildAttrList(IRObject paraObj, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		if (paraObj.getType() != RType.LIST && paraObj.getType() != RType.EXPR) {
			throw new RException("Invalid para type: " + paraObj);
		}

		List<IRParaAttr> paraAttrs = new LinkedList<>();

		IRIterator<? extends IRObject> iter = ((IRList) paraObj).iterator();
		while (iter.hasNext()) {

			IRObject element = iter.next();

			if (element.getType() == RType.ATOM) {
				paraAttrs.add(RulpFactory.createParaAttr(RulpUtil.asAtom(element).getName()));
				continue;
			}

			if (element.getType() == RType.EXPR) {

				IRList argExpr = (IRList) element;

				int argExprSize = argExpr.size();
				int argPos = 0;

				IRObject name = argExpr.get(argPos++);
				if (name.getType() != RType.ATOM) {
					throw new RException("Invalid para name: " + name + ", expr=" + argExpr);
				}

				IRAtom type = null;

				if (argPos < argExprSize) {

					IRObject typeObj = interpreter.compute(frame, argExpr.get(argPos++));

					if (typeObj.getType() == RType.ATOM) {
						type = RulpUtil.asAtom(typeObj);
						if (RType.toType(type.getName()) == null) {
							throw new RException("Undefined para type: " + type);
						}
					} else if (typeObj.getType() == RType.CLASS) {
						type = ((IRClass) typeObj).getClassTypeAtom();

					} else {
						throw new RException("Invalid para type: " + argExpr);
					}
				}

				if (argPos != argExprSize) {
					throw new RException("Invalid para expression: " + argExpr);
				}

				paraAttrs.add(RulpFactory.createParaAttr(RulpUtil.asAtom(name).getName(), type));
				continue;
			}

			throw new RException("Invalid para type: " + paraObj);
		}

		return paraAttrs;
	}

	public static IRObject defFun(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		/*****************************************************/
		// Function name
		/*****************************************************/
		String funName = RulpUtil.asAtom(args.get(1)).getName();

		/*****************************************************/
		// Function parameter list
		/*****************************************************/
		List<IRParaAttr> paraAttrs = buildAttrList(args.get(2), interpreter, frame);

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
		if (args.size() == 4) {
			funBody = RulpUtil.asExpression(args.get(3));

		} else if (args.size() > 4) {
			funBody = RulpUtil.toDoExpr(args.listIterator(3));

		} else {
			throw new RException("Invalid args size: " + args.size());
		}

		ArrayList<String> attrList = null;

		/*****************************************************/
		// Process function attribute
		/*****************************************************/
		if (RulpUtil.hasAttributeList(args)) {

			attrList = new ArrayList<>();

			for (String attr : RulpUtil.getAttributeList(args)) {
				switch (attr) {
				case A_OPT_TCO:
					// recursive function call in return expression
					if (!attrList.contains(attr) && OpTcoUtil.listFunctionInReturn(funBody, frame).contains(funName)) {
						funBody = OpTcoUtil.rebuildTCO(funBody, frame);
						attrList.add(attr);
					}
					break;

				case A_OPT_CC0:
					if (!attrList.contains(attr) && SCOUtil.supportCC0(funBody, frame)) {
						funBody = SCOUtil.rebuildCC0(funBody, frame);
						attrList.add(attr);
					}
					break;

				default:
					throw new RException("unknown attr: " + attr);
				}

			}
		}

		IRFunction newFun = RulpFactory.createFunction(frame, funName, paraAttrs, funBody);
		/*****************************************************/
		// Update attribute list
		/*****************************************************/
		if (attrList != null) {
			for (String attr : attrList) {
				RulpUtil.addAttribute(newFun, attr);
			}
		}

		/*****************************************************/
		// Function
		/*****************************************************/
		IRFrameEntry entry = frame.getEntry(funName);
		if (entry == null) {
			frame.setEntry(funName, newFun);
			return newFun;
		}

		IRObject entryObj = entry.getObject();
		if (entryObj.getType() != RType.FUNC) {
			throw new RException("Duplicated entry found: " + funName);
		}

		IRFunction oldFunc = RulpUtil.asFunction(entryObj);

		/*****************************************************/
		// Copy function
		/*****************************************************/
		if (entry.getFrame() != frame) {

			// Copy function list
			if (RulpUtil.isFunctionList(oldFunc)) {

				IRFunctionList oldFunList = RulpUtil.asFunctionList(oldFunc);
				IRFunctionList newFunList = RulpFactory.createFunctionList(oldFunList.getName());

				for (IRFunction f : oldFunList.getAllFuncList()) {
					newFunList.addFunc(f);
				}

				frame.setEntry(funName, newFunList);
				oldFunc = newFunList;

			}
			// Copy function
			else {
				frame.setEntry(funName, oldFunc);
			}
		}

		if (!RulpUtil.isFunctionList(oldFunc)) {

			/*****************************************************/
			// Override
			/*****************************************************/
			if (oldFunc.getSignature().contentEquals(newFun.getSignature())) {
				XRFunctionList.tryOverride(oldFunc, newFun);
				frame.setEntry(funName, newFun);

				return newFun;
			}
			/*****************************************************/
			// Create Function List
			/*****************************************************/
			else {

				IRFunctionList funList = RulpFactory.createFunctionList(funName);
				funList.addFunc(oldFunc);
				funList.addFunc(newFun);

				frame.setEntry(funName, funList);
				return funList;
			}

		}

		/*****************************************************/
		// Function List (overload)
		/*****************************************************/
		IRFunctionList funList = RulpUtil.asFunctionList(oldFunc);
		funList.addFunc(newFun);

		return funList;
	}

	public static IRObject defMemberFun(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		/*****************************************************/
		// Function name
		/*****************************************************/
		IRMember funMbr = RulpUtil.asMember(args.get(1));
		IRSubject sub = RulpUtil.asSubject(interpreter.compute(frame, funMbr.getSubject()));

		return SubjectUtil.defineMemberFun(sub, funMbr.getName(), args, interpreter, frame);

	}

	public XRFactorDefun(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 4) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject nameObj = args.get(1);

		if (nameObj.getType() == RType.ATOM) {
			return defFun(args, interpreter, frame);
		}

		if (nameObj.getType() == RType.MEMBER) {
			return defMemberFun(args, interpreter, frame);
		}

		throw new RException("Invalid parameters: " + args);
	}

	@Override
	public boolean isStable() {
		return true;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
