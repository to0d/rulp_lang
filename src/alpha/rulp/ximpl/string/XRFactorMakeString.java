/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.string;

import static alpha.rulp.lang.Constant.S_EMPTY;

import alpha.rulp.lang.IRBlob;
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

public class XRFactorMakeString extends AbsAtomFactorAdapter implements IRFactor {

	static IRString makeString(IRBlob blob) throws RException {

		if (blob.length() == 0) {
			return S_EMPTY;
		}

		return RulpFactory.createString(new String(blob.getValue()));
	}

	public XRFactorMakeString(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		IRObject obj = interpreter.compute(frame, args.get(1));
		switch (obj.getType()) {
		case BLOB:
			return makeString(RulpUtil.asBlob(obj));

		default:
			throw new RException("not support: " + obj);
		}

	}

}