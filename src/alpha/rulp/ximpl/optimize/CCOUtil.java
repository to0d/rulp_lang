package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.A_ID;
import static alpha.rulp.lang.Constant.A_OPT_CC0;
import static alpha.rulp.lang.Constant.F_BREAK;
import static alpha.rulp.lang.Constant.F_CASE;
import static alpha.rulp.lang.Constant.F_CC1;
import static alpha.rulp.lang.Constant.F_CC2;
import static alpha.rulp.lang.Constant.F_IF;
import static alpha.rulp.lang.Constant.F_LOOP;
import static alpha.rulp.lang.Constant.F_RETURN;
import static alpha.rulp.lang.Constant.O_COMPUTE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.control.XRFactorCase;
import alpha.rulp.ximpl.control.XRFactorLoop;
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

	protected static AtomicInteger CC2ReuseCount = new AtomicInteger(0);

	protected static AtomicInteger CC2CacheCount = new AtomicInteger(0);

	protected static AtomicInteger CC2CallCount = new AtomicInteger(0);

	protected static AtomicInteger CC2ExprCount = new AtomicInteger(0);

	protected static AtomicInteger CC2RebuildCount = new AtomicInteger(0);

//	private static boolean _isCC1Expr(IRObject e0, IRExpr expr, IRFrame frame) throws RException {
//
//		if (!_isCC1Factor(e0, frame)) {
//			return false;
//		}
//
//		if (!_isConstValue(expr.listIterator(1))) {
//			return false;
//		}
//
//		return true;
//	}

//	private static boolean _isCC1Factor(IRObject obj, IRFrame frame) throws RException {
//
//		if (obj.getType() != RType.FUNC) {
//			return false;
//		}
//
//		if (!StableUtil.isStable(obj, frame)) {
//			return false;
//		}
//
//		return true;
//	}

	private static boolean _isCC2Expr(IRObject e0, IRExpr expr, NameSet nameSet, IRFrame frame) throws RException {

		if (!_isCC2Factor(e0, frame)) {
			return false;
		}

		if (!OptUtil._isStableValue(expr.listIterator(1), nameSet, frame)) {
			return false;
		}

		return true;
	}

	private static boolean _isCC2Factor(IRObject obj, IRFrame frame) throws RException {

		if (obj.getType() != RType.FUNC) {
			return false;
		}

		if (!StableUtil.isStable(obj, frame)) {
			return false;
		}

		return true;
	}

//	private static boolean _isCC3Factor(IRObject obj, IRFrame frame) throws RException {
//
//		if (obj.getType() != RType.FUNC) {
//			return false;
//		}
//
//		if (!StableUtil.isStable(obj, frame)) {
//			return false;
//		}
//
//		return true;
//	}
//
//	private static boolean _isCC3Expr(IRObject e0, IRExpr expr, NameSet nameSet, IRFrame frame) throws RException {
//
//		if (!_isCC3Factor(e0, frame)) {
//			return false;
//		}
//
//		if (!_isStableValue(expr.listIterator(1), nameSet, frame)) {
//			return false;
//		}
//
//		return true;
//	}

//	private static boolean _isFactor(IRObject obj, String name) {
//		return obj.getType() == RType.FACTOR && obj.asString().equals(name);
//	}

//	private static boolean _isLocalValue(IRIterator<? extends IRObject> it, NameSet nameSet) throws RException {
//
//		while (it.hasNext()) {
//			if (!_isLocalValue(it.next(), nameSet)) {
//				return false;
//			}
//		}
//
//		return true;
//	}

	private static boolean _rebuildCC2(CC0 cc0, NameSet nameSet, Map<String, XRFactorCC2> cc2Map,
			HashSet<String> stableFuncNames, IRInterpreter interpreter, IRFrame frame) throws RException {

		IRExpr expr = cc0.inputExpr;

		if (expr.isEmpty()) {
			return true;
		}

		IRObject e0 = RulpUtil.lookup(expr.get(0), interpreter, frame);

		if (StableUtil.isNewFrameFactor(e0)) {
			nameSet = nameSet.newBranch();
		}

		nameSet.updateExpr(e0, expr);

		if (_isCC2Expr(e0, expr, nameSet, frame)) {
			return true;
		}

		// recursion fun
		if (e0.getType() == RType.ATOM && stableFuncNames.contains(e0.asString())) {
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
				reBuild = _rebuildCC2(childCC0, nameSet, cc2Map, stableFuncNames, interpreter, frame);

				if (reBuild) {
					rebuildList.add(childCC0.outputExpr);
				} else if (childCC0.outputExpr != null) {
					rebuildList.add(childCC0.outputExpr);
					childUpdate++;
				} else {
					rebuildList.add(ex);
				}

			} else {

				if (i == 0 && _isCC2Factor(ex, frame)) {
					reBuild = true;
				} else {
					reBuild = OptUtil._isLocalValue(ex, nameSet);
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

				newObj = expr.get(i);

				if (newObj.getType() == RType.EXPR) {

					IRExpr childExpr = RulpUtil.asExpression(newObj);
					if (!childExpr.isEmpty()) {

						IRObject childFactor = RulpUtil.lookup(childExpr.get(0), interpreter, frame);
						if ((childFactor.getType() == RType.ATOM && stableFuncNames.contains(childFactor.asString()))
								|| _isCC2Expr(childFactor, childExpr, nameSet, frame)) {

							String funcName = childFactor.asString();
							XRFactorCC2 cc2 = cc2Map.get(funcName);
							if (cc2 == null) {
								int ccId = incCC2ExprCount();
								cc2 = new XRFactorCC2(F_CC2, ccId);
								RulpUtil.addAttribute(cc2, String.format("%s=%d", A_ID, ccId));
								cc2Map.put(funcName, cc2);

							} else {
								incCC2ReuseCount();
							}

							newObj = RulpFactory.createExpression(cc2, childExpr);
							rebuildList.set(i, newObj);
							rebuildCount++;
						}
					}
				}

				rebuildList.set(i, newObj);

			}
		}

		if (rebuildCount > 0 || childUpdate > 0) {
			cc0.outputExpr = RulpFactory.createExpression(rebuildList);
		}

		return false;
	}

//	private static boolean _rebuildCC3(CC0 cc0, NameSet nameSet, IRInterpreter interpreter, IRFrame frame, int level)
//			throws RException {
//
//		IRExpr expr = cc0.inputExpr;
//
//		if (expr.isEmpty()) {
//			return true;
//		}
//
//		IRObject e0 = RulpUtil.lookup(expr.get(0), interpreter, frame);
//
//		if (StableUtil.isNewFrameFactor(e0)) {
//			nameSet = nameSet.newBranch();
//		}
//
//		nameSet.updateExpr(e0, expr);
//
//		if (level > 0 && _isCC3Expr(e0, expr, nameSet, frame)) {
//			return true;
//		}
//
//		int size = expr.size();
//		ArrayList<IRObject> rebuildList = new ArrayList<>();
//		CC0 childCC0 = new CC0();
//
//		int childReBuild = 0;
//		int childUpdate = 0;
//
//		for (int i = 0; i < size; ++i) {
//
//			IRObject ex = i == 0 ? e0 : expr.get(i);
//			boolean reBuild = false;
//
//			if (ex.getType() == RType.EXPR) {
//
//				childCC0.setInputExpr((IRExpr) ex);
//				reBuild = _rebuildCC3(childCC0, nameSet, interpreter, frame, level + 1);
//
//				if (reBuild) {
//					rebuildList.add(childCC0.outputExpr);
//				} else if (childCC0.outputExpr != null) {
//					rebuildList.add(childCC0.outputExpr);
//					childUpdate++;
//				} else {
//					rebuildList.add(ex);
//				}
//
//			} else {
//
//				if (i == 0 && _isCC3Factor(ex, frame)) {
//					reBuild = true;
//				} else {
//					reBuild = _isStableValue(ex, nameSet, frame);
//				}
//
//				rebuildList.add(ex);
//			}
//
//			if (reBuild) {
//				childReBuild++;
//			}
//		}
//
//		// No child rebuild, return directly
//		if (childReBuild == 0 && childUpdate == 0) {
//			return false;
//		}
//
//		// All child rebuild, return
//		if (childReBuild == size) {
//			return true;
//		}
//
//		int rebuildCount = 0;
//
//		// part rebuild
//		for (int i = 0; i < size; ++i) {
//
//			IRObject newObj = rebuildList.get(i);
//
//			// Need rebuild element
//			if (newObj == null) {
//
//				IRExpr oldExpr = RulpUtil.asExpression(expr.get(i));
//				if (_isCC3Expr(oldExpr.get(0), oldExpr, nameSet, frame)) {
//
//					List<IRAtom> varAtoms = nameSet.listAllVars(oldExpr);
//
//					int ccId = incCC3ExprCount();
//					XRFactorCC3 factor = new XRFactorCC3(F_CC3, ccId, varAtoms);
//					RulpUtil.addAttribute(factor, String.format("%s=%d", A_ID, ccId));
//					newObj = RulpFactory.createExpression(factor, oldExpr);
//					rebuildList.set(i, newObj);
//					rebuildCount++;
//
//				} else {
//
//					rebuildList.set(i, oldExpr);
//				}
//
//			}
//		}
//
//		if (rebuildCount > 0 || childUpdate > 0) {
//			cc0.outputExpr = RulpFactory.createExpression(rebuildList);
//		}
//
//		return false;
//	}

	public static int getCC2ReuseCount() {
		return CC2ReuseCount.get();
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

	public static int getCC2RebuildCount() {
		return CC2RebuildCount.get();
	}

	public static void incCC2ReuseCount() {
		CC2ReuseCount.getAndIncrement();
	}

	public static void incCC2BuildCount() {
		CC2RebuildCount.getAndIncrement();
	}

	public static void incCC2CacheCount() {
		CC2CacheCount.getAndIncrement();
	}

	public static void incCC2CallCount() {
		CC2CallCount.getAndIncrement();
	}

	public static int incCC2ExprCount() {
		return CC2ExprCount.getAndIncrement();
	}

//	public static void incCC3BuildCount() {
//		CC3RebuildCount.getAndIncrement();
//	}
//
//	public static void incCC3CacheCount() {
//		CC3CacheCount.getAndIncrement();
//	}
//
//	public static void incCC3CallCount() {
//		CC3CallCount.getAndIncrement();
//	}
//
//	public static int incCC3ExprCount() {
//		return CC3ExprCount.getAndIncrement();
//	}

//	// (Op A1 A2 ... Ak), Op is Stable factor or functions, Ak const value
//	public static IRExpr rebuildCC1(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {
//
//		incCC1BuildCount();
//
//		CC0 cc0 = new CC0();
//		cc0.setInputExpr(expr);
//
//		if (!_rebuildCC1(cc0, new HashMap<>(), interpreter, frame)) {
//			return cc0.outputExpr == null ? expr : cc0.outputExpr;
//		}
//
//		if (cc0.outputExpr == null) {
//			int ccId = incCC1ExprCount();
//			XRFactorCC1 factor = new XRFactorCC1(F_CC1, ccId);
//			RulpUtil.addAttribute(factor, String.format("%s=%d", A_ID, ccId));
//			cc0.outputExpr = RulpFactory.createExpression(factor, expr);
//		}
//
//		return cc0.outputExpr;
//	}

	// (Op A1 A2 ... Ak), Op is Stable factor or functions, Ak const value, or local
	// variables
	public static IRExpr rebuildCC2(IRExpr expr, List<IRParaAttr> paras, String funcName, IRInterpreter interpreter,
			IRFrame frame) throws RException {

		incCC2BuildCount();

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

		HashSet<String> stableFuncNames = new HashSet<>();

		// this function is stable
		if (StableUtil.isStable(expr, frame)) {
			stableFuncNames.add(funcName);
		}

		_rebuildCC2(cc0, nameSet, new HashMap<>(), stableFuncNames, interpreter, frame);

		return cc0.outputExpr == null ? expr : cc0.outputExpr;
	}

	// (Op A1 A2 ... Ak), Op is Stable factor or functions, Ak const value, local
	// variables or stable expression
//	public static IRExpr rebuildCC3(IRExpr expr, List<IRParaAttr> paras, String funcName, IRInterpreter interpreter,
//			IRFrame frame, boolean recursive) throws RException {
//
//		incCC3BuildCount();
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
//		CC0 cc0 = new CC0();
//		cc0.setInputExpr(expr);
//
//		_rebuildCC3(cc0, nameSet, interpreter, frame, recursive ? 1 : 0);
//
//		return cc0.outputExpr == null ? expr : cc0.outputExpr;
//	}

	public static void reset() {

//		CC1ExprCount.set(0);
//		CC1CallCount.set(0);
//		CC1CacheCount.set(0);

//		CC1RebuildCount.set(0);

		CC2ExprCount.set(0);
		CC2CallCount.set(0);
		CC2CacheCount.set(0);
		CC2RebuildCount.set(0);
		CC2ReuseCount.set(0);

//		CC3ExprCount.set(0);
//		CC3CallCount.set(0);
//		CC3CacheCount.set(0);
//		CC3RebuildCount.set(0);
	}

}
