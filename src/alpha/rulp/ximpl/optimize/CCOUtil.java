package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.*;
import static alpha.rulp.lang.Constant.A_OPT_CC0;
import static alpha.rulp.lang.Constant.F_CASE;
import static alpha.rulp.lang.Constant.F_CC1;
import static alpha.rulp.lang.Constant.F_CC2;
import static alpha.rulp.lang.Constant.F_DEFVAR;
import static alpha.rulp.lang.Constant.F_IF;
import static alpha.rulp.lang.Constant.O_COMPUTE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.control.XRFactorCase;
import alpha.rulp.ximpl.factor.XRFactorDefvar;
import alpha.rulp.ximpl.optimize.StableUtil.NameSet;

// (Compute Cache Optimization)
public class CCOUtil {

	static class CC0 {

		public IRExpr inputExpr = null;

		public IRExpr outputExpr = null;

		public void setInputExpr(IRExpr inputExpr) {
			this.inputExpr = inputExpr;
			this.outputExpr = null;
		}
	}

	protected static AtomicInteger CC0ComputeCount = new AtomicInteger(0);

	protected static AtomicInteger CC1CacheCount = new AtomicInteger(0);

	protected static AtomicInteger CC1CallCount = new AtomicInteger(0);

	protected static AtomicInteger CC1ExprCount = new AtomicInteger(0);

	protected static AtomicInteger CC1ReuseCount = new AtomicInteger(0);

	protected static AtomicInteger CC2CacheCount = new AtomicInteger(0);

	protected static AtomicInteger CC2CallCount = new AtomicInteger(0);

	protected static AtomicInteger CC2ExprCount = new AtomicInteger(0);

	private static IRExpr _asExpr(IRObject obj) throws RException {

		if (obj == null) {
			return RulpFactory.createExpression();
		}

		if (obj.getType() == RType.EXPR) {
			return (IRExpr) obj;
		} else {
			return RulpFactory.createExpression(O_COMPUTE, obj);
		}
	}

	private static boolean _isCC0Expr(IRObject e0, IRExpr expr, IRFrame frame) throws RException {

		if (!_isCC0Factor(e0, frame)) {
			return false;
		}

		if (!_isConstValue(expr.listIterator(1))) {
			return false;
		}

		return true;
	}

	private static boolean _isCC0Factor(IRObject obj, IRFrame frame) throws RException {

		if (obj.getType() != RType.FACTOR) {
			return false;
		}

		if (!RulpUtil.containAttribute(obj, A_OPT_CC0)) {
			return false;
		}

		return true;
	}

	private static boolean _isCC1Expr(IRObject e0, IRExpr expr, IRFrame frame) throws RException {

		if (!_isCC1Factor(e0, frame)) {
			return false;
		}

		if (!_isConstValue(expr.listIterator(1))) {
			return false;
		}

		return true;
	}

	private static boolean _isCC1Factor(IRObject obj, IRFrame frame) throws RException {

		if (_isCC0Factor(obj, frame)) {
			return false;
		}

		if (obj.getType() != RType.FACTOR && obj.getType() != RType.FUNC) {
			return false;
		}

		if (_isFactor(obj, F_RETURN)) {
			return false;
		}

		if (!StableUtil.isStable(obj, frame)) {
			return false;
		}

		return true;
	}

	private static boolean _isCC2Expr(IRObject e0, IRExpr expr, NameSet nameSet, IRFrame frame) throws RException {

		if (!_isCC1Factor(e0, frame)) {
			return false;
		}

		if (!_isLocalValue(expr.listIterator(1), nameSet)) {
			return false;
		}

		return true;
	}

	private static boolean _isConstValue(IRIterator<? extends IRObject> it) throws RException {

		while (it.hasNext()) {
			if (!_isConstValue(it.next())) {
				return false;
			}
		}

		return true;
	}

	private static boolean _isConstValue(IRObject obj) throws RException {

		switch (obj.getType()) {
		case BOOL:
		case INT:
		case FLOAT:
		case DOUBLE:
		case CONSTANT:
		case LONG:
		case NIL:
		case STRING:
			return true;

		case LIST:
			return _isConstValue(((IRList) obj).iterator());

		default:
			return false;
		}
	}

	private static boolean _isFactor(IRObject obj, String name) {
		return obj.getType() == RType.FACTOR && obj.asString().equals(name);
	}

	private static boolean _isLocalValue(IRIterator<? extends IRObject> it, NameSet nameSet) throws RException {

		while (it.hasNext()) {
			if (!_isLocalValue(it.next(), nameSet)) {
				return false;
			}
		}

		return true;
	}

	private static boolean _isLocalValue(IRObject obj, NameSet nameSet) throws RException {

		if (_isConstValue(obj)) {
			return true;
		}

		if (obj.getType() == RType.ATOM && nameSet.lookupType(obj.asString()) != null) {
			return true;
		}

		return false;
	}

	private static boolean _rebuildCC0(CC0 cc0, IRInterpreter interpreter, IRFrame frame) throws RException {

		IRExpr expr = cc0.inputExpr;

		if (expr.isEmpty()) {
			return true;
		}

		IRObject e0 = RulpUtil.lookup(expr.get(0), interpreter, frame);
		if (_isCC0Expr(e0, expr, frame)) {
			return true;
		}

		int size = expr.size();
		ArrayList<IRObject> rebuildList = new ArrayList<>();
		CC0 childCC0 = new CC0();

		int childReBuild = 0;
		int childUpdate = 0;

		for (int i = 0; i < size; ++i) {

			IRObject ex = i == 0 ? e0 : expr.get(i);
			boolean reBuild = false;

			if (ex.getType() == RType.EXPR) {

				childCC0.setInputExpr((IRExpr) ex);
				reBuild = _rebuildCC0(childCC0, interpreter, frame);

				if (reBuild) {
					rebuildList.add(childCC0.outputExpr);
				} else if (childCC0.outputExpr != null) {
					rebuildList.add(childCC0.outputExpr);
					childUpdate++;
				} else {
					rebuildList.add(ex);
				}

			} else {

				if (i == 0 && _isCC0Factor(ex, frame)) {
					reBuild = true;
				} else {
					reBuild = _isConstValue(ex);
				}

				rebuildList.add(ex);
			}

			if (reBuild) {
				childReBuild++;
			}
		}

		// No child rebuild, return directly
		if (childReBuild == 0 && childUpdate == 0) {
			return false;
		}

		// All child rebuild, return
		if (childReBuild == size) {
			return true;
		}

		int rebuildCount = 0;

		// part rebuild
		for (int i = 0; i < size; ++i) {

			IRObject newObj = rebuildList.get(i);

			// Need rebuild element
			if (newObj == null) {

				// Replace element with cc0 factor
				newObj = interpreter.compute(frame, expr.get(i));
				rebuildList.set(i, newObj);
				rebuildCount++;
				incCC0ComputeCount();
			}
		}

		if (rebuildCount > 0 || childUpdate > 0) {

			// (if true A B) or (if false A B)
			if (_isFactor(e0, F_IF) && rebuildList.size() >= 3) {
				IRObject e1 = rebuildList.get(1);
				if (e1.getType() == RType.BOOL) {

					IRObject rst = null;
					if (RulpUtil.asBoolean(e1).asBoolean()) {
						rst = rebuildList.get(2);
					} else if (rebuildList.size() > 3) {
						rst = rebuildList.get(3);
					}

					cc0.outputExpr = _asExpr(rst);
					incCC0ComputeCount();
					return false;
				}
			}

			// (case a (a action) (b action))
			if (_isFactor(e0, F_CASE) && rebuildList.size() >= 3) {

				IRObject e1 = rebuildList.get(1);

				if (_isConstValue(e1)) {

					boolean nonConstCaseValueFound = false;

					CHECK_CASE: for (int i = 2; i < size; ++i) {

						IRExpr caseClause = RulpUtil.asExpression(rebuildList.get(i));
						if (caseClause.size() != 2) {
							throw new RException("Invalid case clause: " + caseClause);
						}

						IRObject caseValue = caseClause.get(0);

						if (!_isConstValue(caseValue)) {
							nonConstCaseValueFound = true;
							break CHECK_CASE;
						}

						if (XRFactorCase.matchCaseValue(e1, caseValue)) {
							cc0.outputExpr = _asExpr(caseClause.get(1));
							incCC0ComputeCount();
							return false;
						}
					}

					// no any case match, return empty expression
					if (!nonConstCaseValueFound) {
						cc0.outputExpr = _asExpr(null);
						incCC0ComputeCount();
						return false;
					}
				}

			}

			// (do () (b action))
			if (_isFactor(e0, A_DO)) {

				int pos = 1;

				for (int i = 1; i < size; ++i) {

					IRObject ei = rebuildList.get(i);

					// not empty expr
					if (ei.getType() == RType.EXPR && !RulpUtil.asExpression(ei).isEmpty()) {

						if (i != pos) {
							rebuildList.set(pos, ei);
						}

						pos++;
					}
				}

				switch (pos) {
				case 1: // no expr found
					cc0.outputExpr = _asExpr(null);
					incCC0ComputeCount();
					return false;

				case 2: // only one expr found, remove DO factor
					cc0.outputExpr = _asExpr(rebuildList.get(1));
					incCC0ComputeCount();
					return false;

				default:

					// empty expr found
					if (pos != size) {
						cc0.outputExpr = RulpFactory.createExpression(rebuildList.subList(0, pos));
						incCC0ComputeCount();
						return false;
					}

				}

			}

			cc0.outputExpr = RulpFactory.createExpression(rebuildList);
		}

		return false;
	}

	private static boolean _rebuildCC1(CC0 cc0, Map<String, XRFactorCC1> cc1Map, IRInterpreter interpreter,
			IRFrame frame) throws RException {

		IRExpr expr = cc0.inputExpr;

		if (expr.isEmpty()) {
			return true;
		}

		IRObject e0 = RulpUtil.lookup(expr.get(0), interpreter, frame);
		if (_isCC1Expr(e0, expr, frame)) {
			return true;
		}

		int size = expr.size();
		ArrayList<IRObject> rebuildList = new ArrayList<>();
		CC0 childCC0 = new CC0();

		int childReBuild = 0;
		int childUpdate = 0;

		for (int i = 0; i < size; ++i) {

			IRObject ex = i == 0 ? e0 : expr.get(i);
			boolean reBuild = false;

			if (ex.getType() == RType.EXPR) {

				childCC0.setInputExpr((IRExpr) ex);
				reBuild = _rebuildCC1(childCC0, cc1Map, interpreter, frame);

				if (reBuild) {
					rebuildList.add(childCC0.outputExpr);
				} else if (childCC0.outputExpr != null) {
					rebuildList.add(childCC0.outputExpr);
					childUpdate++;
				} else {
					rebuildList.add(ex);
				}

			} else {

				if (i == 0 && _isCC1Factor(ex, frame)) {
					reBuild = true;
				} else {
					reBuild = _isConstValue(ex);
				}

				rebuildList.add(ex);
			}

			if (reBuild) {
				childReBuild++;
			}
		}

		// No child rebuild, return directly
		if (childReBuild == 0 && childUpdate == 0) {
			return false;
		}

		// All child rebuild, return
		if (childReBuild == size) {
			return true;
		}

		int rebuildCount = 0;

		// part rebuild
		for (int i = 0; i < size; ++i) {

			IRObject newObj = rebuildList.get(i);

			// Need rebuild element
			if (newObj == null) {

				// Replace element with cc0 factor
				IRObject oldObj = expr.get(i);
				String cc1Key = RulpUtil.toUniqString(oldObj);

				XRFactorCC1 factor = cc1Map.get(cc1Key);
				if (factor == null) {
					factor = new XRFactorCC1(F_CC1);
					cc1Map.put(cc1Key, factor);
				} else {
					incCC1ReuseCount();
				}

				newObj = RulpFactory.createExpression(factor, oldObj);
				rebuildList.set(i, newObj);
				rebuildCount++;
				incCC1ExprCount();
			}
		}

		if (rebuildCount > 0 || childUpdate > 0) {
			cc0.outputExpr = RulpFactory.createExpression(rebuildList);
		}

		return false;
	}

	private static boolean _rebuildCC2(CC0 cc0, NameSet nameSet, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		IRExpr expr = cc0.inputExpr;

		if (expr.isEmpty()) {
			return true;
		}

		IRObject e0 = RulpUtil.lookup(expr.get(0), interpreter, frame);

		if (StableUtil.isNewFrameFactor(e0)) {
			nameSet = nameSet.newBranch();
		}

		if (_isFactor(e0, F_DEFVAR)) {
			nameSet.addVar(XRFactorDefvar.getVarName(expr));
			return false;
		}

//		if (isFactor(e0, F_LOOP)) {
//			nameSet.addVar(RulpUtil.asAtom(expr.get(1)).getName());
//			return false;
//		}

		if (_isCC2Expr(e0, expr, nameSet, frame)) {
			return true;
		}

		int size = expr.size();
		ArrayList<IRObject> rebuildList = new ArrayList<>();
		CC0 childCC0 = new CC0();

		int childReBuild = 0;
		int childUpdate = 0;

		for (int i = 0; i < size; ++i) {

			IRObject ex = i == 0 ? e0 : expr.get(i);
			boolean reBuild = false;

			if (ex.getType() == RType.EXPR) {

				childCC0.setInputExpr((IRExpr) ex);
				reBuild = _rebuildCC2(childCC0, nameSet, interpreter, frame);

				if (reBuild) {
					rebuildList.add(childCC0.outputExpr);
				} else if (childCC0.outputExpr != null) {
					rebuildList.add(childCC0.outputExpr);
					childUpdate++;
				} else {
					rebuildList.add(ex);
				}

			} else {

				if (i == 0 && _isCC1Factor(ex, frame)) {
					reBuild = true;
				} else {
					reBuild = _isLocalValue(ex, nameSet);
				}

				rebuildList.add(ex);
			}

			if (reBuild) {
				childReBuild++;
			}
		}

		// No child rebuild, return directly
		if (childReBuild == 0 && childUpdate == 0) {
			return false;
		}

		// All child rebuild, return
		if (childReBuild == size) {
			return true;
		}

		int rebuildCount = 0;

		// part rebuild
		for (int i = 0; i < size; ++i) {

			IRObject newObj = rebuildList.get(i);

			// Need rebuild element
			if (newObj == null) {

				newObj = RulpFactory.createExpression(new XRFactorCC2(F_CC2), expr.get(i));
				rebuildList.set(i, newObj);
				rebuildCount++;
				incCC2ExprCount();
			}
		}

		if (rebuildCount > 0 || childUpdate > 0) {
			cc0.outputExpr = RulpFactory.createExpression(rebuildList);
		}

		return false;
	}

	public static int getCC0ComputeCount() {
		return CC0ComputeCount.get();
	}

	public static int getCC1CacheCount() {
		return CC1CacheCount.get();
	}

	public static int getCC1CallCount() {
		return CC1CallCount.get();
	}

	public static int getCC1ExprCount() {
		return CC1ExprCount.get();
	}

	public static int getCC1ReuseCount() {
		return CC1ReuseCount.get();
	}

	public static int getCC2CacheCount() {
		return CC2CacheCount.get();
	}

	public static int getCC2CallCount() {
		return CC2CallCount.get();
	}

	public static int getCC2ExprCount() {
		return CC2ExprCount.get();
	}

	public static void incCC0ComputeCount() {
		CC0ComputeCount.getAndIncrement();
	}

	public static void incCC1CacheCount() {
		CC1CacheCount.getAndIncrement();
	}

	public static void incCC1CallCount() {
		CC1CallCount.getAndIncrement();
	}

	public static void incCC1ExprCount() {
		CC1ExprCount.getAndIncrement();
	}

	public static void incCC1ReuseCount() {
		CC1ReuseCount.getAndIncrement();
	}

	public static void incCC2CacheCount() {
		CC2CacheCount.getAndIncrement();
	}

	public static void incCC2CallCount() {
		CC2CallCount.getAndIncrement();
	}

	public static void incCC2ExprCount() {
		CC2ExprCount.getAndIncrement();
	}

	// (Op A1 A2 ... Ak), Op is CC0 factor, Ak is const value and return const value
	public static IRExpr rebuildCC0(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {

		CC0 cc0 = new CC0();
		cc0.setInputExpr(expr);

		if (!_rebuildCC0(cc0, interpreter, frame)) {
			return cc0.outputExpr == null ? expr : cc0.outputExpr;
		}

		if (cc0.outputExpr == null) {

			IRObject rst = interpreter.compute(frame, expr);
			incCC0ComputeCount();
			cc0.outputExpr = _asExpr(rst);
		}

		return cc0.outputExpr;
	}

	// (Op A1 A2 ... Ak), Op is Stable factor or functions, Ak const value
	public static IRExpr rebuildCC1(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {

		CC0 cc0 = new CC0();
		cc0.setInputExpr(expr);

		if (!_rebuildCC1(cc0, new HashMap<>(), interpreter, frame)) {
			return cc0.outputExpr == null ? expr : cc0.outputExpr;
		}

		if (cc0.outputExpr == null) {
			cc0.outputExpr = RulpFactory.createExpression(new XRFactorCC1(F_CC1), expr);
			incCC1ExprCount();
		}

		return cc0.outputExpr;
	}

	// (Op A1 A2 ... Ak), Op is Stable factor or functions, Ak const value, or local
	// variables
	public static IRExpr rebuildCC2(IRExpr expr, List<IRParaAttr> paras, String funcName, IRInterpreter interpreter,
			IRFrame frame) throws RException {

		NameSet nameSet = new NameSet();

		if (paras != null) {
			for (IRParaAttr para : paras) {
				nameSet.addVar(para.getParaName());
			}
		}

		if (funcName != null) {
			nameSet.addFunName(funcName);
		}

		CC0 cc0 = new CC0();
		cc0.setInputExpr(expr);

		if (!_rebuildCC2(cc0, nameSet, interpreter, frame)) {
			return cc0.outputExpr == null ? expr : cc0.outputExpr;
		}

		if (cc0.outputExpr == null) {
			cc0.outputExpr = RulpFactory.createExpression(new XRFactorCC1(F_CC2), expr);
			incCC2ExprCount();
		}

		return cc0.outputExpr;
	}

	public static void reset() {

		CC0ComputeCount.set(0);

		CC1ExprCount.set(0);
		CC1CallCount.set(0);
		CC1CacheCount.set(0);
		CC1ReuseCount.set(0);

		CC2ExprCount.set(0);
		CC2CallCount.set(0);
		CC2CacheCount.set(0);
	}

//	// (Op A1 A2 ... Ak), Op is CC0 factor, Ak is const value and return const value
//	public static boolean supportCC0(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {
//
//		if (expr.isEmpty()) {
//			return false;
//		}
//
//		if (_isCC0Expr(RulpUtil.lookup(expr.get(0), interpreter, frame), expr, frame)) {
//			return true;
//		}
//
//		IRIterator<? extends IRObject> it = expr.iterator();
//		while (it.hasNext()) {
//			IRObject ex = it.next();
//			if (ex.getType() == RType.EXPR) {
//				if (supportCC0((IRExpr) ex, interpreter, frame)) {
//					return true;
//				}
//			}
//		}
//
//		return false;
//	}
//
//	// (Op A1 A2 ... Ak), Op is Stable factor or functions, Ak const value
//	public static boolean supportCC1(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {
//
//		if (expr.isEmpty()) {
//			return false;
//		}
//
//		if (_isCC1Expr(RulpUtil.lookup(expr.get(0), interpreter, frame), expr, frame)) {
//			return true;
//		}
//
//		IRIterator<? extends IRObject> it = expr.iterator();
//		while (it.hasNext()) {
//			IRObject ex = it.next();
//			if (ex.getType() == RType.EXPR) {
//				if (supportCC1((IRExpr) ex, interpreter, frame)) {
//					return true;
//				}
//			}
//		}
//
//		return false;
//	}
//
//	// (Op A1 A2 ... Ak), Op is Stable factor or functions, Ak const value, or local
//	// variables
//	public static boolean supportCC2(IRExpr expr, List<IRParaAttr> paras, String funcName, IRInterpreter interpreter,
//			IRFrame frame) throws RException {
//
//		if (expr.isEmpty()) {
//			return false;
//		}
//
//		NameSet nameSet = new NameSet();
//
//		if (paras != null) {
//			for (IRParaAttr para : paras) {
//				nameSet.addVar(para.getParaName());
//			}
//		}
//
//		if (funcName != null) {
//			nameSet.addFunName(funcName);
//		}
//
//		return supportCC2(expr, nameSet, interpreter, frame);
//	}
//
//	public static boolean supportCC2(IRExpr expr, NameSet nameSet, IRInterpreter interpreter, IRFrame frame)
//			throws RException {
//
//		if (expr.isEmpty()) {
//			return false;
//		}
//
//		if (_isCC2Expr(RulpUtil.lookup(expr.get(0), interpreter, frame), expr, nameSet, frame)) {
//			return true;
//		}
//
//		IRIterator<? extends IRObject> it = expr.iterator();
//		while (it.hasNext()) {
//			IRObject ex = it.next();
//			if (ex.getType() == RType.EXPR) {
//				if (supportCC2((IRExpr) ex, nameSet, interpreter, frame)) {
//					return true;
//				}
//			}
//		}
//
//		return false;
//	}
}
