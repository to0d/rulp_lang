/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.optimize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.factor.AbsRefFactorAdapter;

public class XRFactorCC2 extends AbsRefFactorAdapter implements IRCCFactor {

	private Map<String, IRObject> cacheMap = null;

	private int callCount = 0;

	private int hitCount = 0;

	private final int id;

	private IRFunction fun;

//	private int varIndex[];

	public XRFactorCC2(String factorName, int id) {
		super(factorName);
		this.id = id;
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

	private String _getKey(IRExpr expr) throws RException {

		StringBuffer sb = new StringBuffer();
		IRIterator<? extends IRObject> obj = expr.listIterator(1);
		while (obj.hasNext()) {
			sb.append(RulpUtil.toUniqString(obj.next()));
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
		++callCount;

		IRExpr expr = RulpUtil.asExpression(args.get(1));
		if (fun == null) {
			fun = RulpUtil.asFunction(RulpUtil.lookup(expr.get(0), interpreter, frame));
		}

		expr = (IRExpr) RuntimeUtil.rebuildFuncExpr(fun, expr, interpreter, frame);
		String key = _getKey(expr);
		IRObject cache = _getCache(key);
		if (cache == null) {
			cache = interpreter.compute(frame, expr);
			_putCache(key, cache);
		} else {
			CCOUtil.incCC2CacheCount();
			++hitCount;
		}

		return cache;
	}

	@Override
	public String getCCInformation() {

		String out = String.format("id=%d, type=CC2, call=%d, hit=%d, func=%s, cache=%d", id, callCount, hitCount,
				fun == null ? null : fun.getName(), cacheMap == null ? 0 : cacheMap.size());

		if (cacheMap != null) {

			ArrayList<String> keys = new ArrayList<>(cacheMap.keySet());
			Collections.sort(keys);

			int j = 0;
			for (String key : keys) {
				out += String.format(", %s=%s", key, cacheMap.get(key));
				if (++j >= 3) {
					out += "...";
					break;
				}
			}
		}

		return out;
	}

}
