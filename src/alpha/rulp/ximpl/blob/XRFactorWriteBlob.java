/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.blob;

import alpha.rulp.lang.IRBlob;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorWriteBlob extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorWriteBlob(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 6) {
			throw new RException("Invalid parameters: " + args);
		}

		IRBlob dstBlob = RulpUtil.asBlob(interpreter.compute(frame, args.get(1)));
		int blobPos = RulpUtil.asInteger(interpreter.compute(frame, args.get(2))).asInteger();
		IRBlob srcBlob = RulpUtil.asBlob(interpreter.compute(frame, args.get(3)));
		int srcPos = RulpUtil.asInteger(interpreter.compute(frame, args.get(4))).asInteger();
		int cpyLen = RulpUtil.asInteger(interpreter.compute(frame, args.get(5))).asInteger();
		int len = dstBlob.write(blobPos, srcBlob.getValue(), srcPos, cpyLen);
		
		return RulpFactory.createInteger(len);
	}

}
