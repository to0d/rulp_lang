package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.F_BREAK;
import static alpha.rulp.lang.Constant.F_CASE;
import static alpha.rulp.lang.Constant.F_IF;
import static alpha.rulp.lang.Constant.F_LOOP;
import static alpha.rulp.lang.Constant.F_RETURN;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.control.XRFactorCase;
import alpha.rulp.ximpl.control.XRFactorLoop;

public class EROUtil {

	static class CC0 {

		public IRExpr inputExpr = null;

		public IRExpr outputExpr = null;

		public void setInputExpr(IRExpr inputExpr) {
			this.inputExpr = inputExpr;
			this.outputExpr = null;
		}
	}

	protected static AtomicInteger CC0ComputeCount = new AtomicInteger(0);

	protected static AtomicInteger CC0RebuildCount = new AtomicInteger(0);

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

		if (!OptUtil.isAtomFactor(e0)) {
			return false;
		}

		if (!OptUtil.isConstValue(expr.listIterator(1))) {
			return false;
		}

		return true;
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

				if (i == 0 && OptUtil.isAtomFactor(ex)) {
					reBuild = true;
				} else {
					reBuild = OptUtil.isConstValue(ex);
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
		if (OptUtil.isFactor(e0, F_IF) && rebuildList.size() >= 3) {
			IRObject e1 = rebuildList.get(1);
			if (e1.getType() == RType.BOOL) {

				IRObject rst = null;
				if (RulpUtil.asBoolean(e1).asBoolean()) {
					rst = rebuildList.get(2);
				} else if (rebuildList.size() > 3) {
					rst = rebuildList.get(3);
				}

				cc0.outputExpr = OptUtil.asExpr(rst);
				incCC0ComputeCount();
				return false;
			}
		}

		// (case a (a action) (b action))
		if (OptUtil.isFactor(e0, F_CASE) && rebuildList.size() >= 3) {

			IRObject e1 = rebuildList.get(1);

			if (OptUtil.isConstValue(e1)) {

				boolean nonConstCaseValueFound = false;

				CHECK_CASE: for (int i = 2; i < size; ++i) {

					IRExpr caseClause = RulpUtil.asExpression(rebuildList.get(i));
					if (caseClause.size() != 2) {
						throw new RException("Invalid case clause: " + caseClause);
					}

					IRObject caseValue = caseClause.get(0);

					if (!OptUtil.isConstValue(caseValue)) {
						nonConstCaseValueFound = true;
						break CHECK_CASE;
					}

					if (XRFactorCase.matchCaseValue(e1, caseValue)) {
						cc0.outputExpr = OptUtil.asExpr(caseClause.get(1));
						incCC0ComputeCount();
						return false;
					}
				}

				// no any case match, return empty expression
				if (!nonConstCaseValueFound) {
					cc0.outputExpr = OptUtil.asExpr(null);
					incCC0ComputeCount();
					return false;
				}
			}

		}

		// (do () (b action))
		if (OptUtil.isFactor(e0, A_DO)) {

			int pos = _removeEmptyExpr(rebuildList, 1);

			switch (pos) {
			case 1: // no expr found
				cc0.outputExpr = OptUtil.asExpr(null);
				incCC0ComputeCount();
				return false;

			case 2: // only one expr found, remove DO factor
				cc0.outputExpr = OptUtil.asExpr(rebuildList.get(1));
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
		if (OptUtil.isFactor(e0, F_LOOP) && XRFactorLoop.isLoop2(expr)) {

			IRObject fromObj = XRFactorLoop.getLoop2FromObject(expr);
			IRObject toObj = XRFactorLoop.getLoop2ToObject(expr);

			if (fromObj.getType() == RType.INT && toObj.getType() == RType.INT) {

				int fromIndex = RulpUtil.asInteger(fromObj).asInteger();
				int toIndex = RulpUtil.asInteger(toObj).asInteger();

				// from 3 to 1 ==> empty expr
				if (fromIndex > toIndex) {
					cc0.outputExpr = OptUtil.asExpr(null);
					incCC0ComputeCount();
					return false;
				}

				// from 1 to 1 ==> (do action)
				if (fromIndex == toIndex) {

					ArrayList<IRObject> doActions = new ArrayList<>();
					RulpUtil.addAll(doActions, XRFactorLoop.getLoop2DoList(expr));

					if (doActions.size() == 0) {
						cc0.outputExpr = OptUtil.asExpr(null);
						incCC0ComputeCount();
						return false;
					}

					if (doActions.size() == 1) {
						cc0.outputExpr = OptUtil.asExpr(doActions.get(0));
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
		if (OptUtil.isFactor(e0, F_LOOP) && XRFactorLoop.isLoop3(expr)) {

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

	private static int _removeEmptyExpr(List<IRObject> exprList, int fromIndex) throws RException {

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

	public static void incCC0BuildCount() {
		CC0RebuildCount.getAndIncrement();
	}

	public static void incCC0ComputeCount() {
		CC0ComputeCount.getAndIncrement();
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
			cc0.outputExpr = OptUtil.asExpr(rst);
		}

		return cc0.outputExpr;
	}

	public static void reset() {

		CC0ComputeCount.set(0);
		CC0RebuildCount.set(0);
	}
}
