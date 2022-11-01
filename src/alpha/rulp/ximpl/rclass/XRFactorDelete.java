/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.rclass;

import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorDelete extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorDelete(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2 && args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		String entryName = RulpUtil.asAtom(args.get(1)).getName();

		if (args.size() >= 3) {
			frame = RulpUtil.asFrame(interpreter.compute(frame, args.get(2)));
		}

		IRFrameEntry oldEntry = frame.removeEntry(entryName);
		if (oldEntry == null) {
			throw new RException(String.format("unable to delete: obj %s not found, frame=%s", entryName, frame));
		}

		IRObject obj = oldEntry.getObject();
		if (obj.getType() == RType.INSTANCE && !obj.isDeleted()) {
			RulpUtil.asInstance(obj).delete(interpreter, frame);
		}

		return O_Nil;
	}

}
