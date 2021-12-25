package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.A_OPT_CC0;
import static alpha.rulp.lang.Constant.F_BREAK;
import static alpha.rulp.lang.Constant.F_CASE;
import static alpha.rulp.lang.Constant.F_CC1;
import static alpha.rulp.lang.Constant.F_CC2;
import static alpha.rulp.lang.Constant.F_CC3;
import static alpha.rulp.lang.Constant.F_DEFUN;
import static alpha.rulp.lang.Constant.F_DEFVAR;
import static alpha.rulp.lang.Constant.F_IF;
import static alpha.rulp.lang.Constant.F_LOOP;
import static alpha.rulp.lang.Constant.F_RETURN;
import static alpha.rulp.lang.Constant.O_COMPUTE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRAtom;
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

	protected static AtomicInteger CC0ComputeCount = new AtomicInteger(0);

	protected static AtomicInteger CC1CacheCount = new AtomicInteger(0);

	protected static AtomicInteger CC1CallCount = new AtomicInteger(0);

	protected static AtomicInteger CC1ExprCount = new AtomicInteger(0);

	protected static AtomicInteger CC0RebuildCount = new AtomicInteger(0);

	protected static AtomicInteger CC1RebuildCount = new AtomicInteger(0);

	protected static AtomicInteger CC2RebuildCount = new AtomicInteger(0);

	protected static AtomicInteger CC3RebuildCount = new AtomicInteger(0);

	protected static AtomicInteger CC1ReuseCount = new AtomicInteger(0);

	protected static AtomicInteger CC2CacheCount = new AtomicInteger(0);

	protected static AtomicInteger CC2CallCount = new AtomicInteger(0);

	protected static AtomicInteger CC2ExprCount = new AtomicInteger(0);

	protected static AtomicInteger CC3CacheCount = new AtomicInteger(0);

	protected static AtomicInteger CC3CallCount = new AtomicInteger(0);

	protected static AtomicInteger CC3ExprCount = new AtomicInteger(0);

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

	static boolean _hasBreakExpr(IRObject obj) throws RException {

		if (obj == null) {
			return false;
		}

		switch (obj.getType()) {
		case ATOM:
		case FACTOR:
			String name = obj.asString();
			if (name.equals(F_RETURN) || name.equals(F_BREAK)) {
				return true;
			}

			return false;

		case EXPR:
			IRIterator<? extends IRObject> it = RulpUtil.asExpression(obj).iterator();
			while (it.hasNext()) {
				if (_hasBreakExpr(it.next())) {
					return true;
				}
			}
			return false;

		default:
			return false;
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

		if (obj.getType() == RType.FACTOR) {
			switch (obj.asString()) {
			case F_RETURN:
			case F_DEFVAR:
			case F_DEFUN:
				return false;
			}

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

	private static boolean _isCC3Expr(IRObject e0, IRExpr expr, NameSet nameSet, IRFrame frame) throws RException {

		if (!_isCC1Factor(e0, frame)) {
			return false;
		}

		if (!_isStableValue(expr.listIterator(1), nameSet, frame)) {
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

	private static boolean _isStableValue(IRIterator<? extends IRObject> it, NameSet nameSet, IRFrame frame)
			throws RException {

		while (it.hasNext()) {
			if (!_isStableValue(it.next(), nameSet, frame)) {
				return false;
			}
		}

		return true;
	}

	private static boolean _isStableValue(IRObject obj, NameSet nameSet, IRFrame frame) throws RException {

		if (_isLocalValue(obj, nameSet)) {
			return true;
		}

		if (StableUtil.isStable(obj, nameSet.newBranch(), frame)) {
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
		List<IRObject> rebuildList = new ArrayList<>();
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

			int pos = _removeEmptyExpr(rebuildList, 1);

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

		// (loop for x from 1 to 3 do ...
		if (_isFactor(e0, F_LOOP) && XRFactorLoop.isLoop2(expr)) {

			IRObject fromObj = XRFactorLoop.getLoop2FromObject(expr);
			IRObject toObj = XRFactorLoop.getLoop2ToObject(expr);

			if (fromObj.getType() == RType.INT && toObj.getType() == RType.INT) {

				int fromIndex = RulpUtil.asInteger(fromObj).asInteger();
				int toIndex = RulpUtil.asInteger(toObj).asInteger();

				// from 3 to 1 ==> empty expr
				if (fromIndex > toIndex) {
					cc0.outputExpr = _asExpr(null);
					incCC0ComputeCount();
					return false;
				}

				// from 1 to 1 ==> (do action)
				if (fromIndex == toIndex) {

					ArrayList<IRObject> doActions = new ArrayList<>();
					RulpUtil.addAll(doActions, XRFactorLoop.getLoop2DoList(expr));

					if (doActions.size() == 0) {
						cc0.outputExpr = _asExpr(null);
						incCC0ComputeCount();
						return false;
					}

					if (doActions.size() == 1) {
						cc0.outputExpr = _asExpr(doActions.get(0));
						incCC0ComputeCount();
						return false;
					}

					cc0.outputExpr = RulpUtil.toDoExpr(doActions);
					incCC0ComputeCount();
					return false;
				}
			}
		}

		// Check infinite loop: (loop a)
		if (_isFactor(e0, F_LOOP) && XRFactorLoop.isLoop3(expr)) {

			int pos = _removeEmptyExpr(rebuildList, 1);
			if (pos == 1) {
				throw new RException("infinite loop detected: input=" + expr + ", output="
						+ RulpFactory.createExpression(rebuildList.subList(0, pos)));
			}

			boolean findBreak = false;
			for (int i = 1; !findBreak && i < pos; ++i) {
				findBreak = _hasBreakExpr(rebuildList.get(i));
			}

			// no break clause found, infinite loop
			if (!findBreak) {
				throw new RException("infinite loop detected: input=" + expr + ", output="
						+ RulpFactory.createExpression(rebuildList.subList(0, pos)));
			}

			// empty expr found
			if (pos != size) {
				cc0.outputExpr = RulpFactory.createExpression(rebuildList.subList(0, pos));
				incCC0ComputeCount();
				return false;
			}

		}

		if (rebuildCount > 0 || childUpdate > 0) {
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

		nameSet.updateExpr(e0, expr);

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

				IRExpr cc2Expr = RulpUtil.asExpression(expr.get(i));
				int cc2Size = cc2Expr.size();
				int indexs[] = new int[cc2Size - 1];
				int k = 0;
				for (int j = 1; j < cc2Size; ++j) {
					IRObject cc2Obj = cc2Expr.get(j);
					if (!_isConstValue(cc2Obj)) {
						indexs[k++] = j;
					}
				}

				int newIndexs[] = new int[k];
				for (int j = 0; j < k; ++j) {
					newIndexs[j] = indexs[j];
				}

				newObj = RulpFactory.createExpression(new XRFactorCC2(F_CC2, newIndexs), expr.get(i));
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

	private static boolean _rebuildCC3(CC0 cc0, NameSet nameSet, IRInterpreter interpreter, IRFrame frame, int level)
			throws RException {

		IRExpr expr = cc0.inputExpr;

		if (expr.isEmpty()) {
			return true;
		}

		IRObject e0 = RulpUtil.lookup(expr.get(0), interpreter, frame);

		if (StableUtil.isNewFrameFactor(e0)) {
			nameSet = nameSet.newBranch();
		}

		nameSet.updateExpr(e0, expr);

		if (level > 0 && _isCC3Expr(e0, expr, nameSet, frame)) {
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
				reBuild = _rebuildCC3(childCC0, nameSet, interpreter, frame, level + 1);

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
					reBuild = _isStableValue(ex, nameSet, frame);
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

				IRExpr cc3Expr = RulpUtil.asExpression(expr.get(i));
				List<IRAtom> varAtoms = nameSet.listAllVars(cc3Expr);

				newObj = RulpFactory.createExpression(new XRFactorCC3(F_CC3, varAtoms), expr.get(i));
				rebuildList.set(i, newObj);
				rebuildCount++;
				incCC3ExprCount();
			}
		}

		if (rebuildCount > 0 || childUpdate > 0) {
			cc0.outputExpr = RulpFactory.createExpression(rebuildList);
		}

		return false;
	}

	static int _removeEmptyExpr(List<IRObject> exprList, int fromIndex) throws RException {

		int size = exprList.size();
		int pos = 1;

		for (int i = 1; i < size; ++i) {

			IRObject ei = exprList.get(i);

			// ignore empty expr or non-expr object
			if (ei.getType() != RType.EXPR || RulpUtil.asExpression(ei).isEmpty()) {
				continue;
			}

			// move
			if (i != pos) {
				exprList.set(pos, ei);
			}

			pos++;
		}

		return pos;
	}

	public static int getCC0ComputeCount() {
		return CC0ComputeCount.get();
	}

	public static int getCC0RebuildCount() {
		return CC0RebuildCount.get();
	}

	public static int getCC1RebuildCount() {
		return CC1RebuildCount.get();
	}

	public static int getCC2RebuildCount() {
		return CC2RebuildCount.get();
	}

	public static int getCC3RebuildCount() {
		return CC3RebuildCount.get();
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

	public static int getCC3CacheCount() {
		return CC3CacheCount.get();
	}

	public static int getCC3CallCount() {
		return CC3CallCount.get();
	}

	public static int getCC3ExprCount() {
		return CC3ExprCount.get();
	}

	public static void incCC0ComputeCount() {
		CC0ComputeCount.getAndIncrement();
	}

	public static void incCC0BuildCount() {
		CC0RebuildCount.getAndIncrement();
	}

	public static void incCC1BuildCount() {
		CC1RebuildCount.getAndIncrement();
	}

	public static void incCC2BuildCount() {
		CC2RebuildCount.getAndIncrement();
	}

	public static void incCC3BuildCount() {
		CC3RebuildCount.getAndIncrement();
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

	public static void incCC3CacheCount() {
		CC3CacheCount.getAndIncrement();
	}

	public static void incCC3CallCount() {
		CC3CallCount.getAndIncrement();
	}

	public static void incCC3ExprCount() {
		CC3ExprCount.getAndIncrement();
	}

	// (Op A1 A2 ... Ak), Op is CC0 factor, Ak is const value and return const value
	public static IRExpr rebuildCC0(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {

		incCC0BuildCount();

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

		incCC1BuildCount();

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

		_rebuildCC2(cc0, nameSet, interpreter, frame);

		return cc0.outputExpr == null ? expr : cc0.outputExpr;
	}

	// (Op A1 A2 ... Ak), Op is Stable factor or functions, Ak const value, local
	// variables or stable expression
	public static IRExpr rebuildCC3(IRExpr expr, List<IRParaAttr> paras, String funcName, IRInterpreter interpreter,
			IRFrame frame, boolean recursive) throws RException {

		incCC3BuildCount();

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

		_rebuildCC3(cc0, nameSet, interpreter, frame, recursive ? 1 : 0);

		return cc0.outputExpr == null ? expr : cc0.outputExpr;
	}

	public static void reset() {

		CC0ComputeCount.set(0);
		CC0RebuildCount.set(0);

		CC1ExprCount.set(0);
		CC1CallCount.set(0);
		CC1CacheCount.set(0);
		CC1ReuseCount.set(0);
		CC1RebuildCount.set(0);

		CC2ExprCount.set(0);
		CC2CallCount.set(0);
		CC2CacheCount.set(0);
		CC2RebuildCount.set(0);

		CC3ExprCount.set(0);
		CC3CallCount.set(0);
		CC3CacheCount.set(0);
		CC3RebuildCount.set(0);
	}

}
