/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.string;

import java.util.ArrayList;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorStrSplitLines extends AbsAtomFactorAdapter implements IRFactor {
	public XRFactorStrSplitLines(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject obj = interpreter.compute(frame, args.get(1));

		ArrayList<String> lines = new ArrayList<>();

		String str = RulpUtil.asString(obj).asString();
		int pos2;
		int pos1 = 0;
		while ((pos2 = str.indexOf('\n', pos1)) != -1) {
			lines.add(str.substring(pos1, pos2));
			pos1 = pos2 + 1;
		}

		if (pos1 < str.length()) {
			lines.add(str.substring(pos1));
		} else if (pos1 == str.length()) {
			lines.add("");
		}

		return RulpFactory.createListOfString(lines);
	}

	@Override
	public boolean isStable() {
		return true;
	}

	public boolean isThreadSafe() {
		return true;
	}

}