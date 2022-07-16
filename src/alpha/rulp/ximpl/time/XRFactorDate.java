/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.time;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.XDay;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorDate extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorDate(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() > 2) {
			throw new RException("Invalid parameters: " + args);
		}

		if (args.size() == 2) {
			String dayString = RulpUtil.asString(interpreter.compute(frame, args.get(1))).asString();
			return RulpFactory.createString(XDay.getDay(dayString).toString());

		} else {
			return RulpFactory.createString(XDay.today().toString());
		}
	}

}