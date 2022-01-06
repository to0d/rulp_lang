/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.factor;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;

public class XRFactorValueOf extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorValueOf(String factorName) {
		super(factorName);
	}

	public static IRObject valueOf2(IRObject obj, IRInterpreter interpreter, IRFrame frame) throws RException {

		switch (obj.getType()) {
		case VAR:
			return RulpUtil.asVar(obj).getValue();

//		case MEMBER:
//
//			IRMember mbr = RuntimeUtil.getActualMember((IRMember) obj, interpreter, frame);
//			if (!RuntimeUtil.canAccess(mbr, interpreter, frame)) {
//				throw new RException("Can't access member<" + mbr + "> in frame<" + frame + ">");
//			}

		default:
			return obj;
		}
	}

	public static IRObject valueOf(IRObject obj, IRInterpreter interpreter, IRFrame frame) throws RException {

		switch (obj.getType()) {

		case VAR:
			return RulpUtil.asVar(obj).getValue();

		case ATOM:

			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, RulpUtil.asAtom(obj).getName());
			if (entry == null) {
				throw new RException("object not found: " + obj);
			}

			return valueOf2(entry.getObject(), interpreter, frame);

		case LIST:
			return interpreter.compute(frame, obj);

		case EXPR:
			return valueOf2(interpreter.compute(frame, obj), interpreter, frame);

		case MEMBER:

			IRMember mbr = RuntimeUtil.getActualMember((IRMember) obj, interpreter, frame);
			if (!RuntimeUtil.canAccess(mbr, interpreter, frame)) {
				throw new RException("Can't access member<" + mbr + "> in frame<" + frame + ">");
			}

			return mbr.getValue();

		case NIL:
		case BOOL:
		case INT:
		case LONG:
		case FLOAT:
		case DOUBLE:
		case STRING:
		case FACTOR:
		case TEMPLATE:
		case FUNC:
		case MACRO:
		case INSTANCE:
		case CLASS:
		case FRAME:
			return obj;

		default:
			throw new RException("invalid object: " + obj);
		}
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject obj = args.get(1);
		if (obj.getType() == RType.EXPR) {
			obj = interpreter.compute(frame, obj);
		}

		return valueOf(obj, interpreter, frame);
	}

}
