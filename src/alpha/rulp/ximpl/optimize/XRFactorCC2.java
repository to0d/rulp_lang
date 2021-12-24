/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.optimize;

import java.util.HashMap;
import java.util.Map;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRefFactorAdapter;

public class XRFactorCC2 extends AbsRefFactorAdapter implements IRFactor {

	private Map<String, IRObject> cacheMap = null;

	private int varIndex[];

	public XRFactorCC2(String factorName, int[] varIndex) {
		super(factorName);
		this.varIndex = varIndex;
	}

	@Override
	protected void _delete() throws RException {

		if (cacheMap != null) {

			for (IRObject cache : cacheMap.values()) {
				RulpUtil.decRef(cache);
			}

			cacheMap = null;
		}

		super._delete();
	}

	private IRObject _getCache(String key) {
		return cacheMap == null ? null : cacheMap.get(key);
	}

	private String _getKey(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {

		StringBuffer sb = new StringBuffer();
		for (int index : varIndex) {
			sb.append(RulpUtil.toUniqString(interpreter.compute(frame, expr.get(index))));
		}

		return sb.toString();
	}

	private void _putCache(String key, IRObject cache) throws RException {

		if (cacheMap == null) {
			cacheMap = new HashMap<>();
		}

		cacheMap.put(key, cache);
		RulpUtil.incRef(cache);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		CCOUtil.incCC2CallCount();

		IRExpr expr = RulpUtil.asExpression(args.get(1));

		String key = _getKey(expr, interpreter, frame);
		IRObject cache = _getCache(key);
		if (cache == null) {
			cache = interpreter.compute(frame, expr);
			_putCache(key, cache);
		} else {
			CCOUtil.incCC2CacheCount();
		}

		return cache;
	}

}
