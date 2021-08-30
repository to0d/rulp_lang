/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.namespace;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactorBody;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;

public class XRFactorBodyUseNameSpace implements IRFactorBody {

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject obj = args.get(2);
		if (obj.getType() == RType.ATOM) {

			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry((IRAtom) obj, frame);
			if (entry == null) {
				throw new RException("namespace not found: " + obj);
			}

			obj = entry.getValue();
		}

		IRSubject nameSpace = RulpUtil.asNameSpace(interpreter.compute(frame, obj));
		RulpUtil.setUsingNameSpace(frame, nameSpace);
		return nameSpace;
	}
}
