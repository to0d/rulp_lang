/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.factor;

import java.util.ArrayList;
import java.util.List;

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
import alpha.rulp.utils.StringUtil;

public class XRFactorCanCast extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorCanCast(String factorName) {
		super(factorName);
	}

	static boolean can_cast_int(IRObject obj) throws RException {

		if (obj == null) {
			return false;
		}

		switch (obj.getType()) {
		case INT:
		case LONG:
			return true;

		case STRING:
			return StringUtil.isNumber(RulpUtil.asString(obj).asString());

		case VAR:
			return can_cast_int(RulpUtil.asVar(obj).getValue());

		default:
			return false;
		}
	}

	static boolean can_cast_float(IRObject obj) throws RException {

		if (obj == null) {
			return false;
		}

		switch (obj.getType()) {
		case INT:
		case LONG:
		case FLOAT:
			return true;

		case STRING:

			String str = RulpUtil.asString(obj).asString();

			if (StringUtil.isNumber(str)) {
				return true;
			}

			try {
				Float.parseFloat(str);
				return true;
			} catch (Exception e) {
				return false;
			}

		case VAR:
			return can_cast_float(RulpUtil.asVar(obj).getValue());

		default:
			return false;
		}
	}

	static void buildCastTypes(IRObject toTypeObj, List<RType> toTypes) throws RException {

		if (toTypeObj.getType() == RType.ATOM) {

			RType toType = RType.toType(RulpUtil.asAtom(toTypeObj).asString());
			switch (toType) {

			case INT:
			case FLOAT:
				if (!toTypes.contains(toType)) {
					toTypes.add(toType);
				}
				return;

			default:
				throw new RException("not support type: " + toTypeObj);
			}

		} else if (toTypeObj.getType() == RType.LIST) {

			IRList typeList = RulpUtil.asList(toTypeObj);
			if (typeList.isEmpty()) {
				throw new RException("not support type: " + toTypeObj);
			}

			IRIterator<? extends IRObject> it = typeList.iterator();
			while (it.hasNext()) {
				buildCastTypes(it.next(), toTypes);
			}

		} else {
			throw new RException("not support type: " + toTypeObj);
		}
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject toTypeObj = interpreter.compute(frame, args.get(1));
		IRObject val = interpreter.compute(frame, args.get(2));
		ArrayList<RType> toTypes = new ArrayList<>();

		buildCastTypes(toTypeObj, toTypes);
		if (toTypes.isEmpty()) {
			throw new RException("not cast type found: " + toTypeObj);
		}

		boolean rc = false;

		for (RType toType : toTypes) {

			switch (toType) {

			case INT:
				rc = can_cast_int(val);
				break;

			case FLOAT:
				rc = can_cast_float(val);
				break;

			default:
				throw new RException("not support type: " + toType);
			}

			if (rc) {
				break;
			}
		}

		return RulpFactory.createBoolean(rc);
	}

}