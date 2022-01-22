/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.system;

import static alpha.rulp.lang.Constant.O_AIX;
import static alpha.rulp.lang.Constant.O_LINUX;
import static alpha.rulp.lang.Constant.O_MAC;
import static alpha.rulp.lang.Constant.O_Nil;
import static alpha.rulp.lang.Constant.O_WIN;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.SystemUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorSystemOSType extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorSystemOSType(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 1) {
			throw new RException("Invalid parameters: " + args);
		}

		switch (SystemUtil.getOSType()) {
		case AIX:
			return O_AIX;

		case Linux:
			return O_LINUX;

		case Mac:
			return O_MAC;

		case Win:
			return O_WIN;

		default:
			return O_Nil;
		}

	}

}
