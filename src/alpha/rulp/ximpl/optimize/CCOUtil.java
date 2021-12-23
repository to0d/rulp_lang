package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_OPT_CC0;
import static alpha.rulp.lang.Constant.*;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;

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

	static final int SCO_LEVEL_CC0 = 0;

	static final int SCO_LEVEL_CC1 = 1;

	static final int SCO_LEVEL_MAX = 0;

	static final int SCO_LEVEL_NONE = -1;

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
			if (e0.getType() == RType.FACTOR && e0.asString().equals(F_IF) && rebuildList.size() >= 3) {
				IRObject e1 = rebuildList.get(1);
				if (e1.getType() == RType.BOOL) {

					IRObject rst = null;
					if (RulpUtil.asBoolean(e1).asBoolean()) {
						rst = rebuildList.get(2);
					} else if (rebuildList.size() > 3) {
						rst = rebuildList.get(3);
					}

					incCC0ComputeCount();
					cc0.outputExpr = _asExpr(rst);
					return false;
				}
			}

			// (case a (a action) (b action))
			if (e0.getType() == RType.FACTOR && e0.asString().equals(F_CASE) && rebuildList.size() >= 3) {
				
				IRObject e1 = rebuildList.get(1);
				
				if (e1.getType() == RType.BOOL) {

					IRObject rst = null;
					if (RulpUtil.asBoolean(e1).asBoolean()) {
						rst = rebuildList.get(2);
					} else if (rebuildList.size() > 3) {
						rst = rebuildList.get(3);
					}

					incCC0ComputeCount();
					cc0.outputExpr = _asExpr(rst);
					return false;
				}
			}

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

	public static void reset() {

		CC0ComputeCount.set(0);
		CC1ExprCount.set(0);
		CC1CallCount.set(0);
		CC1CacheCount.set(0);

	}

	// (Op A1 A2 ... Ak), Op is CC0 factor, Ak is const value
	public static boolean supportCC0(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (expr.isEmpty()) {
			return false;
		}

		if (_isCC0Expr(RulpUtil.lookup(expr.get(0), interpreter, frame), expr, frame)) {
			return true;
		}

		IRIterator<? extends IRObject> it = expr.iterator();
		while (it.hasNext()) {
			IRObject ex = it.next();
			if (ex.getType() == RType.EXPR) {
				if (supportCC0((IRExpr) ex, interpreter, frame)) {
					return true;
				}
			}
		}

		return false;
	}

	// (Op A1 A2 ... Ak), Op is CC0 factor, Ak const value, or local variables
	public static boolean supportCC1(IRExpr expr, IRFrame frame) throws RException {
		return false;
	}
}
