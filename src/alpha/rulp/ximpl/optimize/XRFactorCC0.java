/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.optimize;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRefFactorAdapter;

public class XRFactorCC0 extends AbsRefFactorAdapter implements IRFactor {

	private IRObject rst = null;

	public XRFactorCC0(String factorName) {
		super(factorName);
	}

	@Override
	protected void _delete() throws RException {

		if (rst != null) {
			RulpUtil.decRef(rst);
			rst = null;
		}

		super._delete();
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		if (rst == null) {
			rst = interpreter.compute(frame, args.get(1));
			RulpUtil.incRef(rst);
		}

		return rst;
	}

}
