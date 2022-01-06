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
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;

public class XRFactorDoWhenVarChanged extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorDoWhenVarChanged(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		String varName = RulpUtil.asAtom(args.get(1)).getName();

		IRFrameEntry entry = frame.getEntry(varName);
		if (entry == null) {
			throw new RException("var not found: " + varName);
		}

		IRVar var = RulpUtil.asVar(entry.getObject());

		IRFunction fun = RulpUtil.asFunction(interpreter.compute(frame, args.get(2)));

		if (fun.getArgCount() != 3) {
			throw new RException("the func need 2 arguments");
		}

		var.addVarListener((_var, _old, _new) -> {
			RuntimeUtil.computeCallable(fun, RulpFactory.createNativeList(fun, var, _old, _new), interpreter, frame);
		});

		return O_Nil;
	}

}