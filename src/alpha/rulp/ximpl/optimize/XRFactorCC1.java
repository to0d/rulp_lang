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
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRefFactorAdapter;

public class XRFactorCC1 extends AbsRefFactorAdapter implements IRCCFactor {

	private IRObject cache = null;

	private final int id;

	private int callCount = 0;

	private int hitCount = 0;

	public XRFactorCC1(String factorName, int id) {
		super(factorName);
		this.id = id;
	}

	@Override
	protected void _delete() throws RException {

		if (cache != null) {
			RulpUtil.decRef(cache);
			cache = null;
		}

		super._delete();
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		CCOUtil.incCC1CallCount();
		++callCount;

		if (cache == null) {
			cache = interpreter.compute(frame, args.get(1));
			RulpUtil.incRef(cache);
		} else {
			CCOUtil.incCC1CacheCount();
			++hitCount;
		}

		return cache;
	}

	@Override
	public String getCCInformation() {
		return String.format("id=%d, type=CC1, call=%d, hit=%d, cache=%s", id, callCount, hitCount, "" + cache);
	}

}
