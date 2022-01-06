/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.factor;

import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RuntimeUtil;

public class XRFactorSubjectOf extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorSubjectOf(String factorName) {
		super(factorName);
	}

	public static IRObject subjectOf(IRObject obj, IRInterpreter interpreter, IRFrame frame) throws RException {

		switch (obj.getType()) {

		case ATOM:

			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, obj.asString());
			if (entry == null) {
				return O_Nil;
			}

			return entry.getFrame();

		case MEMBER:

			IRMember mbr = RuntimeUtil.getActualMember((IRMember) obj, interpreter, frame);
			if (!RuntimeUtil.canAccess(mbr, interpreter, frame)) {
				throw new RException("Can't access member<" + mbr + "> in frame<" + frame + ">");
			}

			return mbr.getSubject();

		default:
			return O_Nil;
		}
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		return subjectOf(args.get(1), interpreter, frame);
	}

}
