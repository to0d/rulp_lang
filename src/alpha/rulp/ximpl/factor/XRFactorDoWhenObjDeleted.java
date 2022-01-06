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
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;

public class XRFactorDoWhenObjDeleted extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorDoWhenObjDeleted(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject obj = args.get(1);
		if (obj.getType() == RType.ATOM) {
			String objName = RulpUtil.asAtom(obj).getName();
			IRFrameEntry entry = frame.getEntry(objName);
			if (entry == null) {
				throw new RException("object not found: " + objName);
			}

			obj = entry.getObject();
		}

		IRFunction fun = RulpUtil.asFunction(interpreter.compute(frame, args.get(2)));
		if (fun.getArgCount() != 1) {
			throw new RException("the func need 2 arguments");
		}

		RulpUtil.incRef(fun);

		obj.addObjectDeletedListener((_obj) -> {
			RuntimeUtil.computeCallable(fun, RulpFactory.createList(fun, _obj), interpreter, frame);
			RulpUtil.decRef(fun);
		});

		return fun;
	}

}