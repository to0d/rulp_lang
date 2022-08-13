/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.string;

import static alpha.rulp.lang.Constant.F_TO_STRING;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRFunctionList;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorToString extends AbsAtomFactorAdapter implements IRFactor {

	public static IRObject computeInstanceToString(IRInstance ins, IRInterpreter interpreter) throws RException {

		IRMember mbr = ins.getMember(F_TO_STRING);
		if (mbr == null || mbr.getValue() == null || mbr.getValue().getType() != RType.FUNC) {
			return null;
		}

		IRFunction func = null;
		IRFunction mbrFun = RulpUtil.asFunction(mbr.getValue());
		if (!mbrFun.isList()) {
			if (mbrFun.getParaAttrs().size() == 0) {
				func = mbrFun;
			}
		} else {
			IRFunctionList funList = (IRFunctionList) mbrFun;
			for (IRFunction f : funList.getAllFuncList()) {
				if (f.getParaAttrs().size() == 0) {
					func = f;
				}
			}
		}

		if (func == null) {
			return null;
		}

		return interpreter.compute(ins.getSubjectFrame(),
				RulpFactory.createExpression(RulpFactory.createMember(ins, F_TO_STRING, func)));
	}

	public static IRObject toString(IRObject obj, IRInterpreter interpreter) throws RException {

		if (obj.getType() == RType.STRING) {
			return obj;
		}

		switch (obj.getType()) {

		case INSTANCE:
			IRObject rst = computeInstanceToString((IRInstance) obj, interpreter);
			if (rst != null) {
				return rst;
			}
			break;

		default:

		}

		return RulpFactory.createString(RulpUtil.toString(obj));
	}

	public XRFactorToString(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		return toString(interpreter.compute(frame, args.get(1)), interpreter);
	}

}