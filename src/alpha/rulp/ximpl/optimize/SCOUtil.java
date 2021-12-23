package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_OPT_CC0;
import static alpha.rulp.lang.Constant.F_CC0;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;

// SCO (Stable Cache Optimization)
public class SCOUtil {

	static class CC0 {

		public IRExpr inputExpr = null;

		public IRExpr outputExpr = null;

		public void setInputExpr(IRExpr inputExpr) {
			this.inputExpr = inputExpr;
			this.outputExpr = null;
		}
	}

	protected static AtomicInteger CC0CacheCount = new AtomicInteger(0);

	protected static AtomicInteger CC0CallCount = new AtomicInteger(0);

	protected static AtomicInteger CC0ExprCount = new AtomicInteger(0);

	static final int SCO_LEVEL_CC0 = 0;

	static final int SCO_LEVEL_CC1 = 1;

	static final int SCO_LEVEL_MAX = 0;

	static final int SCO_LEVEL_NONE = -1;

	private static boolean _isCC0Expr(IRExpr expr, IRFrame frame) throws RException {

		if (expr.isEmpty()) {
			return false;
		}

		if (!_isCC0Factor(expr.get(0), frame)) {
			return false;
		}

		if (!_isConstValue(expr.listIterator(1))) {
			return false;
		}

		return true;
	}

	private static boolean _isCC0Factor(IRObject obj, IRFrame frame) throws RException {

		if (obj.getType() == RType.ATOM) {
			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, RulpUtil.asAtom(obj).getName());
			if (entry != null) {
				obj = entry.getValue();
			}
		}

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

	private static boolean _rebuildCC0(CC0 cc0, IRFrame frame) throws RException {

		if (cc0.inputExpr.isEmpty()) {
			return true;
		}

		if (_isCC0Expr(cc0.inputExpr, frame)) {
			return true;
		}

		int size = cc0.inputExpr.size();
		ArrayList<IRObject> rebuildList = new ArrayList<>();
		CC0 childCC0 = new CC0();

		int childReBuild = 0;
		int childUpdate = 0;

		for (int i = 0; i < size; ++i) {

			IRObject ex = cc0.inputExpr.get(i);
			boolean reBuild = false;

			if (ex.getType() == RType.EXPR) {

				childCC0.setInputExpr((IRExpr) ex);
				reBuild = _rebuildCC0(childCC0, frame);

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
				newObj = RulpFactory.createExpression(new XRFactorCC0(F_CC0), cc0.inputExpr.get(i));
				rebuildList.set(i, newObj);
				rebuildCount++;
				incCC0ExprCount();
			}
		}

		if (rebuildCount > 0 || childUpdate > 0) {
			cc0.outputExpr = RulpFactory.createExpression(rebuildList);
		}

		return false;
	}

	public static int getCC0CacheCount() {
		return CC0CacheCount.get();
	}

	public static int getCC0CallCount() {
		return CC0CallCount.get();
	}

	public static int getCC0ExprCount() {
		return CC0ExprCount.get();
	}

	public static void incCC0CacheCount() {
		CC0CacheCount.getAndIncrement();
	}

	public static void incCC0CallCount() {
		CC0CallCount.getAndIncrement();
	}

	public static void incCC0ExprCount() {
		CC0ExprCount.getAndIncrement();
	}

	public static IRExpr rebuildCC0(IRExpr expr, IRFrame frame) throws RException {

		CC0 cc0 = new CC0();
		cc0.setInputExpr(expr);

		if (!_rebuildCC0(cc0, frame)) {
			return cc0.outputExpr == null ? expr : cc0.outputExpr;
		}

		if (cc0.outputExpr == null) {
			cc0.outputExpr = RulpFactory.createExpression(new XRFactorCC0(F_CC0), expr);
			incCC0ExprCount();
		}

		return cc0.outputExpr;
	}

	public static void reset() {
		CC0ExprCount.set(0);
		CC0CallCount.set(0);
		CC0CacheCount.set(0);
	}

	// (Op A1 A2 ... Ak), Op is simple stable factor, Ak is number or string
	public static boolean supportCC0(IRExpr expr, IRFrame frame) throws RException {

		if (_isCC0Expr(expr, frame)) {
			return true;
		}

		IRIterator<? extends IRObject> it = expr.iterator();
		while (it.hasNext()) {
			IRObject ex = it.next();
			if (ex.getType() == RType.EXPR) {
				if (supportCC0((IRExpr) ex, frame)) {
					return true;
				}
			}
		}

		return false;
	}
}
