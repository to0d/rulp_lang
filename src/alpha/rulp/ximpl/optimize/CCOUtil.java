package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_OPT_ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.AttrUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.attribute.StmtCountUtil;

// (Compute Cache Optimization)
public class CCOUtil {

	static class CCO {

		public IRExpr inputExpr = null;

		public IRExpr outputExpr = null;

		public void setInputExpr(IRExpr inputExpr) {
			this.inputExpr = inputExpr;
			this.outputExpr = null;
		}
	}

	protected static AtomicInteger CC2CacheCount = new AtomicInteger(0);

	protected static AtomicInteger CC2CallCount = new AtomicInteger(0);

	protected static AtomicInteger CC2RebuildCount = new AtomicInteger(0);

	protected static AtomicInteger exprCount = new AtomicInteger(0);

	static int MIN_CC0_STMT_COUNT = 16;

	protected static AtomicInteger reuseCount = new AtomicInteger(0);

	public static int getCC2CacheCount() {
		return CC2CacheCount.get();
	}

	public static int getCC2CallCount() {
		return CC2CallCount.get();
	}

	public static int getCC2ExprCount() {
		return exprCount.get();
	}

	public static int getCC2RebuildCount() {
		return CC2RebuildCount.get();
	}

	public static int getCC2ReuseCount() {
		return reuseCount.get();
	}

	public static void incCC2CacheCount() {
		CC2CacheCount.getAndIncrement();
	}

	public static void incCC2CallCount() {
		CC2CallCount.getAndIncrement();
	}

	public static IRExpr rebuild(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {

		CC2RebuildCount.getAndIncrement();

		NameSet nameSet = new NameSet();

		CCO cc0 = new CCO();
		cc0.setInputExpr(expr);

		CCOUtil ccoUtil = new CCOUtil(interpreter, frame);
		ccoUtil._rebuildCC2(cc0, nameSet);

		return cc0.outputExpr == null ? expr : cc0.outputExpr;
	}

	// (Op A1 A2 ... Ak), Op is Stable functions, Ak const value, or local
	// variables
	public static IRExpr rebuild(IRExpr expr, List<IRParaAttr> paras, String funcName, IRInterpreter interpreter,
			IRFrame frame) throws RException {

		CC2RebuildCount.getAndIncrement();

		NameSet nameSet = new NameSet();

		if (paras != null) {
			for (IRParaAttr para : paras) {
				nameSet.addVar(para.getParaName());
			}
		}

		if (funcName != null) {
			nameSet.addFunName(funcName);
		}

		CCO cc0 = new CCO();
		cc0.setInputExpr(expr);

		CCOUtil ccoUtil = new CCOUtil(interpreter, frame);

		// this function is stable
		if (AttrUtil.isStable(expr, frame)) {
			ccoUtil.stableFuncNames.add(funcName);
		}

		ccoUtil._rebuildCC2(cc0, nameSet);

		return cc0.outputExpr == null ? expr : cc0.outputExpr;
	}

	public static void reset() {

		exprCount.set(0);
		CC2CallCount.set(0);
		CC2CacheCount.set(0);
		CC2RebuildCount.set(0);
		reuseCount.set(0);
	}

	private Map<String, XRFactorCC2> cc2Map = new HashMap<>();

	private IRFrame frame;

	private IRInterpreter interpreter;

	private HashSet<String> stableFuncNames = new HashSet<>();

	private CCOUtil(IRInterpreter interpreter, IRFrame frame) {
		super();
		this.interpreter = interpreter;
		this.frame = frame;
	}

	private boolean _isCC2Expr(IRObject e0, IRExpr expr, NameSet nameSet) throws RException {

		if (!_isCC2Factor(e0)) {
			return false;
		}

		if (!AttrUtil.isStableValue(expr.listIterator(1), nameSet, frame)) {
			return false;
		}

		return true;
	}

	private boolean _isCC2Factor(IRObject obj) throws RException {

		if (obj.getType() != RType.FUNC) {
			return false;
		}

		if (!AttrUtil.isStable(obj, frame)) {
			return false;
		}

		int stmtCount = StmtCountUtil.getStmtCount(obj, interpreter, frame);
		if (stmtCount < 0) { // Recursive function
			return true;
		}

		if (stmtCount >= MIN_CC0_STMT_COUNT) {
			return true;
		}

		return false;
	}

	private boolean _rebuildCC2(CCO cc0, NameSet nameSet) throws RException {

		IRExpr expr = cc0.inputExpr;

		if (expr.isEmpty()) {
			return true;
		}

		IRObject e0 = EROUtil.lookup(expr.get(0), interpreter, frame);
		if (OptUtil.isNewFrameFactor(e0)) {
			nameSet = nameSet.newBranch();
		}

		nameSet.updateExpr(e0, expr);

		if (_isCC2Expr(e0, expr, nameSet)) {
			return true;
		}

		// recursion fun
		if (e0.getType() == RType.ATOM && stableFuncNames.contains(e0.asString())) {
			return true;
		}

		int size = expr.size();
		ArrayList<IRObject> rebuildList = new ArrayList<>();
		CCO childCC0 = new CCO();

		int childReBuild = 0;
		int childUpdate = 0;

		for (int i = 0; i < size; ++i) {

			IRObject ex = i == 0 ? e0 : expr.get(i);
			boolean reBuild = false;

			if (ex.getType() == RType.EXPR) {

				childCC0.setInputExpr((IRExpr) ex);
				reBuild = _rebuildCC2(childCC0, nameSet);

				if (reBuild) {
					rebuildList.add(childCC0.outputExpr);
				} else if (childCC0.outputExpr != null) {
					rebuildList.add(childCC0.outputExpr);
					childUpdate++;
				} else {
					rebuildList.add(ex);
				}

			} else {

				if (i == 0 && _isCC2Factor(ex)) {
					reBuild = true;
				} else {
					reBuild = OptUtil.isLocalValue(ex, nameSet);
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

						// recursive or stable function
						if ((childFactor.getType() == RType.ATOM && stableFuncNames.contains(childFactor.asString()))
								|| _isCC2Expr(childFactor, childExpr, nameSet)) {

							String funcName = childFactor.asString();

							XRFactorCC2 cc2 = cc2Map.get(funcName);
							if (cc2 == null) {

								int optId = OptUtil.getNextOptFactorId();
								cc2 = new XRFactorCC2(optId);
								AttrUtil.setAttribute(cc2, A_OPT_ID, RulpFactory.createInteger(optId));
								cc2Map.put(funcName, cc2);
								exprCount.getAndIncrement();

							} else {
								reuseCount.getAndIncrement();
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

}
