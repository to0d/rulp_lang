/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.io;

import static alpha.rulp.lang.Constant.F_OPEN_TXT_FILE;

import java.io.IOException;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.FileUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorOpenTxtFile extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorOpenTxtFile(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2 && args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		String path = RulpUtil.asString(interpreter.compute(frame, args.get(1))).asString();
		if (RulpUtil.isTrace(frame)) {
			interpreter.out(String.format("%s: read %s\n", F_OPEN_TXT_FILE, path));
		}

		String charset = null;
		if (args.size() == 3) {
			charset = RulpUtil.asString(interpreter.compute(frame, args.get(2))).asString();
		}

		try {
			return RulpFactory.createListOfString(FileUtil.openTxtFile(path, charset));
		} catch (IOException e) {
			if (RulpUtil.isTrace(frame)) {
				e.printStackTrace();
			}
			throw new RException(e.toString());
		}
	}

}