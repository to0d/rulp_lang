/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.F_CC2;
import static alpha.rulp.lang.Constant.O_INFINITE_LOOP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RError;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.error.RInfiniteLoop;
import alpha.rulp.ximpl.factor.AbsRefFactorAdapter;

public class XRFactorCC2 extends AbsRefFactorAdapter implements IRRebuild, IRFactor {

	enum CC2_STATUS {
		WORK, DONE, EXCP, INF

	}

	public String toString(CC2_STATUS status) {
		switch (status) {
		case DONE:
			return "D";

		case EXCP:
			return "E";

		case INF:
			return "I";

		case WORK:
			return "W";

		default:
			return "U";
		}

	}

	static class CC2 {
//		String expr;
		IRObject result;
		CC2_STATUS status = CC2_STATUS.WORK;
		int hitCount = 0;
	}

	private Map<String, CC2> cacheMap = null;

	private int callCount = 0;

	private IRFunction fun;

	private int hitCount = 0;

	private final int id;

	public XRFactorCC2(int id) {
		super(F_CC2);
		this.id = id;
	}

	@Override
	protected void _delete() throws RException {

		if (cacheMap != null) {
			for (CC2 cc2 : cacheMap.values()) {
				if (cc2.result != null) {
					RulpUtil.decRef(cc2.result);
				}
			}
			cacheMap = null;
		}

		super._delete();
	}

	private CC2 _getCache(String key) {
		return cacheMap == null ? null : cacheMap.get(key);
	}

	private CC2 _makeCache(String key) {

		CC2 cc2 = new CC2();

		if (cacheMap == null) {
			cacheMap = new HashMap<>();
		}

		cacheMap.put(key, cc2);
		return cc2;
	}

//	private void _removeCache(String key) {
//		cacheMap.remove(key);
//	}

	private String _getKey(IRExpr expr) throws RException {

		StringBuffer sb = new StringBuffer();
		IRIterator<? extends IRObject> obj = expr.listIterator(1);
		while (obj.hasNext()) {
			sb.append(RulpUtil.toUniqString(obj.next()));
		}

		return sb.toString();
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
		CC2 cc2 = _getCache(key);

		// cache was not computed
		if (cc2 == null) {

			cc2 = _makeCache(key);
//			cc2.expr = "" + expr;

			try {

				cc2.result = interpreter.compute(frame, expr);
				cc2.status = CC2_STATUS.DONE;
				return cc2.result;

			} catch (RInfiniteLoop e) {
				cc2.status = CC2_STATUS.INF;
				throw e;

			} catch (RException e) {
				cc2.status = CC2_STATUS.EXCP;
				throw e;
			}

		} else {

			switch (cc2.status) {

			case WORK:
				throw new RError(frame, this, RulpFactory.createError(interpreter, O_INFINITE_LOOP, expr));

			case DONE:
				CCOUtil.incCC2CacheCount();
				++hitCount;
				cc2.hitCount++;
				return cc2.result;

			case EXCP:
				throw new RInfiniteLoop("previous exception record found: (cc2 " + expr + ")");

			case INF:
				throw new RInfiniteLoop("previous infinite record found: (cc2 " + expr + ")");

			default:
				throw new RInfiniteLoop("unknown status found: (cc2 " + expr + ")");
			}
		}

	}

	@Override
	public String getRebuildInformation() {

		String out = String.format("id=%d, name=%s, call=%d, hit=%d, func=%s, cache=%d", id, getName(), callCount,
				hitCount, fun == null ? null : fun.getName(), cacheMap == null ? 0 : cacheMap.size());

		if (cacheMap != null) {

			ArrayList<String> keys = new ArrayList<>(cacheMap.keySet());
			Collections.sort(keys);

			out += ", data:";

			int j = 0;
			for (String key : keys) {

				if (j != 0) {
					out += " ";
				}

				CC2 cc2 = cacheMap.get(key);
				out += String.format("[%s/%s/%s/%d]", key, cc2.result, toString(cc2.status), cc2.hitCount);
				if (++j >= 3) {
					out += "...";
					break;
				}
			}
		}

		return out;
	}

}
