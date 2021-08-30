/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.factor;

import static alpha.rulp.lang.Constant.A_NAN;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;

public class XRFactorNameOf extends AbsRFactorAdapter implements IRFactor {

	public XRFactorNameOf(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		return getName(args.get(1), frame);
	}

	private IRObject getName(IRObject obj, IRFrame frame) throws RException {

		switch (obj.getType()) {

		case ATOM:
			IRFrameEntry entry = frame.getEntry(RulpUtil.asAtom(obj).getName());
			if (entry != null && entry.getObject() != obj) {
				return getName(entry.getObject(), frame);
			}

			return RulpFactory.createString(RulpUtil.asAtom(obj).getName());

		case INSTANCE:
			return RulpFactory.createString(RulpUtil.asInstance(obj).getInstanceName());

		case CLASS:
			return RulpFactory.createString(RulpUtil.asClass(obj).getClassName());

		case FACTOR:
			return RulpFactory.createString(RulpUtil.asFactor(obj).getName());

		case TEMPLATE:
			return RulpFactory.createString(RulpUtil.asTemplate(obj).getName());

		case FUNC:
			return RulpFactory.createString(RulpUtil.asFunction(obj).getSignature());

		case MACRO:
			return RulpFactory.createString(RulpUtil.asMacro(obj).getName());

		case NIL:
			return obj;

		case VAR:
			return RulpFactory.createString(RulpUtil.asVar(obj).getName());

		case MEMBER:
			return RulpFactory.createString(RulpUtil.asMember(obj).getName());

		case FRAME:
			return RulpFactory.createString(RulpUtil.asFrame(obj).getFrameName());

		default:
			return RulpFactory.createString(A_NAN);

		}

	}

	@Override
	public boolean isStable() {
		return true;
	}

	public boolean isThreadSafe() {
		return true;
	}
}
