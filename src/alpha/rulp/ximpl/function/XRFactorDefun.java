/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.function;

//import static alpha.rulp.lang.Constant.A_OPT_CC3;
import static alpha.rulp.lang.Constant.A_OPT_CCO;
import static alpha.rulp.lang.Constant.A_OPT_ERO;
import static alpha.rulp.lang.Constant.A_OPT_FULL;
import static alpha.rulp.lang.Constant.A_OPT_LCO;
import static alpha.rulp.lang.Constant.A_OPT_TCO;

import java.util.ArrayList;
import java.util.Collections;
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
import alpha.rulp.utils.AttrUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.SubjectUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;
import alpha.rulp.ximpl.optimize.CCOUtil;
import alpha.rulp.ximpl.optimize.EROUtil;
import alpha.rulp.ximpl.optimize.LCOUtil;
import alpha.rulp.ximpl.optimize.OptUtil;
import alpha.rulp.ximpl.optimize.TCOUtil;

public class XRFactorDefun extends AbsAtomFactorAdapter implements IRFactor {

	static class OPT {
		ArrayList<String> attrList;
		IRFrame frame;
		IRExpr funBody;
		String funName;
		IRInterpreter interpreter;
		List<IRParaAttr> paraAttrs;
	}

	static int _getOptOrder(String optName) {

		switch (optName) {
		case A_OPT_ERO:
			return 0;

		case A_OPT_CCO:
			return 2;

		case A_OPT_TCO:
			return 4;

		default:
			return -1;
		}
	}

	private static boolean _optCC0(OPT opt) throws RException {

		IRObject rst = EROUtil.rebuildFuncBody(opt.funBody, opt.interpreter, opt.frame);
		if (rst == opt.funBody) {
			return false;
		}

		opt.funBody = OptUtil.asExpr(rst);
		return true;
	}

	private static boolean _optCC2(OPT opt) throws RException {

		IRExpr newExpr = CCOUtil.rebuild(opt.funBody, opt.paraAttrs, opt.funName, opt.interpreter, opt.frame);
		if (newExpr == opt.funBody) {
			return false;
		}

		opt.funBody = newExpr;
		return true;
	}

	private static boolean _optLCO(OPT opt) throws RException {
		return LCOUtil.rebuild(opt.paraAttrs);
	}

	private static boolean _optTCO(OPT opt) throws RException {

		if (!TCOUtil.listFunctionInReturn(opt.funBody, opt.frame).contains(opt.funName)) {
			return false;
		}

		// recursive function call in return expression
		IRExpr newExpr = TCOUtil.rebuild(opt.funBody, opt.frame);
		if (newExpr == opt.funBody) {
			return false;
		}

		opt.funBody = newExpr;
		return true;
	}

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

				int aszie = argExpr.size();
				if (aszie == 0 || aszie > 3) {
					throw new RException("Invalid para expression: " + argExpr);
				}

				IRObject a0 = argExpr.get(0);
				if (a0.getType() != RType.ATOM) {
					throw new RException("Invalid para name: " + a0 + ", expr=" + argExpr);
				}

				// (?a)
				if (aszie == 1) {
					paraAttrs.add(RulpFactory.createParaAttr(RulpUtil.asAtom(a0).getName(), null));
					continue;
				}

				IRObject a1 = interpreter.compute(frame, argExpr.get(1));

				// (?a int)
				if (aszie == 2 && a1.getType() == RType.ATOM) {

					IRAtom type = RulpUtil.asAtom(a1);
					if (RType.toType(type.getName()) == null) {
						throw new RException("Undefined para type: " + type);
					}

					paraAttrs.add(RulpFactory.createParaAttr(RulpUtil.asAtom(a0).getName(), type));
					continue;
				}

				// (?a class-name)
				if (aszie == 2 && a1.getType() == RType.CLASS) {
					IRAtom type = ((IRClass) a1).getClassTypeAtom();
					paraAttrs.add(RulpFactory.createParaAttr(RulpUtil.asAtom(a0).getName(), type));
					continue;
				}

				// (?a '(default-value))
//				if (aszie == 1 && a1.getType() == RType.LIST) {
//
//					IRList defValList = (IRList) a1;
//					if (defValList.size() != 1) {
//						throw new RException("invalid para default value list: " + defValList);
//					}
//					
//					IRObject defVal = defValList.get(0);
//					
//
//					paraAttrs.add(RulpFactory.createParaAttr(RulpUtil.asAtom(a0).getName(), type));
//					continue;
//				}

				throw new RException("Invalid para expression: " + argExpr);
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
		if (AttrUtil.hasAttributeList(args)) {

			attrList = new ArrayList<>();

			Set<String> uniqOptAttributeSet = new HashSet<>();
			for (String attr : AttrUtil.getAttributeKeyList(args)) {

				switch (attr) {
				case A_OPT_TCO:
					uniqOptAttributeSet.add(A_OPT_TCO);
					break;

				case A_OPT_ERO:
					uniqOptAttributeSet.add(A_OPT_ERO);
					break;

				case A_OPT_CCO:
					uniqOptAttributeSet.add(A_OPT_CCO);
					break;

				case A_OPT_LCO:
					uniqOptAttributeSet.add(A_OPT_LCO);
					break;

				case A_OPT_FULL:
					uniqOptAttributeSet.add(A_OPT_ERO);
					uniqOptAttributeSet.add(A_OPT_CCO);
					uniqOptAttributeSet.add(A_OPT_LCO);
					uniqOptAttributeSet.add(A_OPT_TCO);
					break;

				}
			}

			ArrayList<String> uniqOptAttributeList = new ArrayList<>(uniqOptAttributeSet);
			Collections.sort(uniqOptAttributeList, (n1, n2) -> {
				return _getOptOrder(n1) - _getOptOrder(n2);
			});

			int optSize = uniqOptAttributeList.size();
			int optIndex = 0;
			OPT opt = new OPT();

			opt.interpreter = interpreter;
			opt.frame = frame;
			opt.funBody = funBody;
			opt.funName = funName;
			opt.attrList = attrList;
			opt.paraAttrs = paraAttrs;

			int optCount = 0;

			while (optIndex < optSize) {

				String attr = uniqOptAttributeList.get(optIndex++);

				boolean update = false;

				switch (attr) {
				case A_OPT_TCO:
					update = _optTCO(opt);
					break;

				case A_OPT_ERO:
					update = _optCC0(opt);
					break;

				case A_OPT_CCO:
					update = _optCC2(opt);
					break;

				case A_OPT_LCO:
					update = _optLCO(opt);
					break;

				default:
					throw new RException("unknown attr: " + attr);
				}

				if (update) {

					if (!opt.attrList.contains(attr)) {
						opt.attrList.add(attr);
					}
					optCount++;
				}
			}

			if (optCount > 0) {
				funBody = opt.funBody;
			}
		}

		IRFunction newFun = RulpFactory.createFunction(frame, funName, paraAttrs, funBody);
		/*****************************************************/
		// Update attribute list
		/*****************************************************/
		if (attrList != null) {
			for (String attr : attrList) {
				AttrUtil.addAttribute(newFun, attr);
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
			throw new RException(String.format("Can't overide object: obj=%s, type=%s", entryObj, entryObj.getType()));
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

}
