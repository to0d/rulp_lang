/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.string;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRString;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorStrSubStr extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorStrSubStr(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3 && args.size() != 4) {
			throw new RException("Invalid parameters: " + args);
		}

		IRString strObj = RulpUtil.asString(interpreter.compute(frame, args.get(1)));

		String str = strObj.asString();
		int str_len = str.length();

		int beginIndex = RulpUtil.asInteger(interpreter.compute(frame, args.get(2))).asInteger();
		if (beginIndex < 0) {
			beginIndex = 0;
		}

		if (beginIndex >= str_len) {
			return RulpFactory.createString();
		}

		if (args.size() == 3) {
			if (beginIndex == 0) {
				return strObj;
			}

			return RulpFactory.createString(str.substring(beginIndex));

		} else {

			int endIndex = RulpUtil.asInteger(interpreter.compute(frame, args.get(3))).asInteger();
			if (endIndex <= beginIndex) {
				return RulpFactory.createString();
			}

			if (endIndex > str_len) {
				endIndex = str_len;
			}

			if (beginIndex == 0 && endIndex == str_len) {
				return strObj;
			}

			return RulpFactory.createString(str.substring(beginIndex, endIndex));
		}
	}

}