/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.optimize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorIsTCORecursive extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorIsTCORecursive(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRFunction fun = RulpUtil.asFunction(interpreter.compute(frame, args.get(1)));
		if (fun.isList()) {
			return RulpFactory.emptyConstList();
		}

		Set<String> calleeNames = TCOUtil.listFunctionInReturn(fun.getFunBody(), frame);
		if (calleeNames.isEmpty()) {
			return RulpFactory.emptyConstList();
		}

		ArrayList<String> nameList = new ArrayList<>(calleeNames);
		Collections.sort(nameList);

		return RulpFactory.createListOfString(nameList);
	}

}
