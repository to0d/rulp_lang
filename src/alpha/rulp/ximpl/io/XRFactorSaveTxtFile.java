/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.io;

import static alpha.rulp.lang.Constant.F_SAVE_TXT_FILE;
import static alpha.rulp.lang.Constant.O_Nil;

import java.io.IOException;
import java.util.ArrayList;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.FileUtil;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorSaveTxtFile extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorSaveTxtFile(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid parameters: " + args);
		}

		String path = RulpUtil.asString(interpreter.compute(frame, args.get(1))).asString();
		if (RulpUtil.isTrace(frame)) {
			interpreter.out(String.format("%s: save %s\n", F_SAVE_TXT_FILE, path));
		}

		IRList lineList = RulpUtil.asList(interpreter.compute(frame, args.get(2)));

		IRIterator<? extends IRObject> iter = lineList.iterator();
		ArrayList<String> lines = new ArrayList<>();
		while (iter.hasNext()) {
			lines.add(RulpUtil.asString(iter.next()).asString());
		}

		try {

			FileUtil.saveTxtFile(path, lines);
			return O_Nil;

		} catch (IOException e) {

			if (RulpUtil.isTrace(frame)) {
				e.printStackTrace();
			}

			throw new RException(e.toString());
		}
	}

}