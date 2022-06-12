package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.A_OPT_ID;
import static alpha.rulp.lang.Constant.F_IF;
import static alpha.rulp.lang.Constant.F_O_BY;
import static alpha.rulp.lang.Constant.F_RETURN;
import static alpha.rulp.lang.Constant.O_Nil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.AttrUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;

public class TCOUtil {

	static class TCONode {

		private ArrayList<IRObject> elements = null;

		private int expandIndex = -1;

		private IRExpr expr;

		private final int indexOfParent;

		private final int level;

		public int optId = OPT_NONE;

		private TCONode parrent;

		public TCONode(TCONode parrent, int indexOfParent) {
			super();
			this.parrent = parrent;
			this.indexOfParent = indexOfParent;

			if (this.parrent == null) {
				this.level = 0;
			} else {
				this.level = this.parrent.level + 1;
			}
		}

		public IRExpr getExpr() {
			return expr;
		}

		public int getLevel() {
			return level;
		}

		public void setExpr(IRExpr newExpr) throws RException {

			IRExpr oldExpr = this.expr;
			this.expr = newExpr;

			RulpUtil.incRef(newExpr);
			RulpUtil.decRef(oldExpr);
		}

		public String toString() {
			return expr.toString();
		}
	}

	protected static AtomicInteger callCount = new AtomicInteger(0);

	protected static AtomicInteger computeCount = new AtomicInteger(0);

	protected static AtomicInteger exprCount = new AtomicInteger(0);

	protected static AtomicInteger maxLevelCount = new AtomicInteger(0);

	protected static AtomicInteger maxStackSize = new AtomicInteger(0);

	protected static AtomicInteger nodeCount = new AtomicInteger(0);

	static final int OPT_BY = 1; // * by

	static final int OPT_NONE = 0;

	protected static AtomicInteger rebuildCount = new AtomicInteger(0);

	public static boolean TRACE = false;

	private static IRObject _computeTCO(IRObject e0, IRExpr newExpr, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		switch (e0.getType()) {
		case FACTOR:
		case MACRO:
		case TEMPLATE:
			return RuntimeUtil.computeCallable((IRCallable) e0, newExpr, interpreter, frame);

		case FUNC:

			if (TRACE) {
				System.out.println("cps: call fun, " + newExpr);
			}

			return RuntimeUtil.computeFun((IRFunction) e0, newExpr, interpreter, frame);

		case MEMBER:

			IRObject e1m = ((IRMember) e0).getValue();
			if (e1m.getType() != RType.FUNC) {
				throw new RException("factor not found: " + newExpr);
			}

			return RuntimeUtil.computeFun((IRFunction) e1m, newExpr, interpreter, frame);

		default:
			throw new RException("factor not found: " + newExpr);
		}

	}

	private static void _findTCOCalleeInReturn(IRObject e0, IRExpr expr, Set<String> calleeNames, IRFrame frame)
			throws RException {

		switch (e0.getType()) {
		case ATOM:

			String opName = RulpUtil.asAtom(e0).getName();
			IRFrameEntry entry = frame.getEntry(opName);

			if (entry != null && entry.getValue() != null && entry.getValue().getType() != RType.ATOM) {
				_findTCOCalleeInReturn(entry.getValue(), expr, calleeNames, frame);
				return;
			}

			// it maybe a undefined function
			calleeNames.add(e0.asString());
			break;

		case FACTOR:

			// does't support unstable factor
			if (!AttrUtil.isStable(e0, frame)) {
				return;
			}

			break;

		case FUNC:
			calleeNames.add(e0.asString());
			break;

		default:

			// unsupport type
			return;
		}

		IRIterator<? extends IRObject> it = expr.listIterator(1);
		while (it.hasNext()) {

			IRObject e = it.next();
			if (e.getType() != RType.EXPR) {
				continue;
			}

			IRExpr ex = (IRExpr) e;
			if (ex.isEmpty()) {
				continue;
			}

			_findTCOCalleeInReturn(ex.get(0), ex, calleeNames, frame);
		}
	}

	private static void _push(Stack<TCONode> stack, TCONode parrent, int indexOfParent, IRExpr expr) throws RException {

		TCONode node = new TCONode(parrent, indexOfParent);
		node.setExpr(expr);

		int level = node.getLevel();
		if (level > maxLevelCount.get()) {
			maxLevelCount.set(level);
		}

		nodeCount.incrementAndGet();

		stack.push(node);
		int size = stack.size();
		if (size > maxStackSize.get()) {
			maxStackSize.set(size);
		}
	}

	private static IRExpr _rebuild(IRExpr expr, IRFrame frame) throws RException {

		IRObject e0 = expr.get(0);
		if (e0.getType() != RType.ATOM) {
			return expr;
		}

		switch (RulpUtil.asAtom(e0).getName()) {
		case A_DO: {

			ArrayList<IRObject> newExpr = new ArrayList<>();
			newExpr.add(e0);

			IRIterator<? extends IRObject> it = expr.listIterator(1);
			while (it.hasNext()) {

				IRObject e = it.next();
				if (e.getType() == RType.EXPR) {
					e = _rebuild((IRExpr) e, frame);
				}

				newExpr.add(e);
			}

			return expr.isEarly() ? RulpFactory.createExpressionEarly(newExpr) : RulpFactory.createExpression(newExpr);
		}

		case F_IF: {

			ArrayList<IRObject> newExpr = new ArrayList<>();
			newExpr.add(e0);
			newExpr.add(expr.get(1));

			IRIterator<? extends IRObject> it = expr.listIterator(2);
			while (it.hasNext()) {
				IRObject e = it.next();
				if (e.getType() == RType.EXPR) {
					e = _rebuild((IRExpr) e, frame);
				}

				newExpr.add(e);
			}

			return expr.isEarly() ? RulpFactory.createExpressionEarly(newExpr) : RulpFactory.createExpression(newExpr);
		}

		case F_RETURN:

			if (expr.size() == 2) {
				IRObject e1 = expr.get(1);
				if (e1.getType() == RType.EXPR) {
					int optId = OptUtil.getNextOptFactorId();
					XRFactorCPS cps = new XRFactorCPS(optId);
					AttrUtil.setAttribute(cps, A_OPT_ID, RulpFactory.createInteger(optId));
					exprCount.getAndIncrement();
					return RulpFactory.createExpression(cps, e1);
				}
			}

			break;

		default:

		}

		return expr;
	}

	private static IRObject _updateResult(Stack<TCONode> stack, TCONode node, IRObject rst) throws RException {

		if (node.parrent != null) {

			if (rst.getType() == RType.EXPR) {
				_push(stack, node.parrent, node.indexOfParent, (IRExpr) rst);
			} else {
				node.parrent.elements.set(node.indexOfParent, rst);
			}

		} else {

			if (rst.getType() == RType.EXPR) {
				_push(stack, null, -1, (IRExpr) rst);
			} else {
				return rst;
			}
		}

		return null;
	}

	public static IRObject computeTCO(IRExpr tcoExpr, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (tcoExpr.isEmpty()) {
			return O_Nil;
		}

		if (TRACE) {
			System.out.println("cps: compute, " + tcoExpr);
		}

		Stack<TCONode> stack = new Stack<>();
		_push(stack, null, -1, tcoExpr);

		QUEUE: while (!stack.isEmpty()) {

			if (TRACE) {
				System.out.println("cps: queue, size=" + stack.size());
			}

			incComputeCount();
			TCONode node = stack.peek();

			/*******************************************/
			// init expand list
			/*******************************************/
			if (node.expandIndex == -1) {

				if (node.getExpr().isEmpty()) {
					stack.pop();
					node.parrent.elements.set(node.indexOfParent, O_Nil);
					continue QUEUE;
				}

				boolean findExpr = false;
				{
					IRIterator<? extends IRObject> it = node.getExpr().iterator();
					while (it.hasNext()) {
						if (it.next().getType() == RType.EXPR) {
							findExpr = true;
							break;
						}
					}
				}

				// no expr element, compute
				if (!findExpr) {
					stack.pop();
					IRObject rst = _computeTCO(node.getExpr().get(0), node.getExpr(), interpreter, frame);
					rst = _updateResult(stack, node, rst);
					if (rst != null) {
						return rst;
					}
				}

				node.expandIndex = 0;
				node.elements = new ArrayList<>();
				RulpUtil.addAll(node.elements, node.getExpr());
				continue QUEUE;
			}

			/*******************************************/
			// expand expr element
			/*******************************************/
			if (node.elements != null) {

				while (node.expandIndex < node.elements.size()) {

					IRObject obj = node.elements.get(node.expandIndex);

					if (node.expandIndex == 0) {
						if (obj.getType() == RType.ATOM || obj.getType() == RType.FACTOR) {
							switch (obj.asString()) {
							case F_O_BY:
								node.optId = OPT_BY;
								break;
							default:
							}
						}
					}

					if (obj.getType() == RType.EXPR) {
						_push(stack, node, node.expandIndex, (IRExpr) obj);
						continue QUEUE;
					}

					if (node.optId != OPT_NONE && node.expandIndex > 0) {

						switch (node.optId) {
						case OPT_BY:
							// (* a 0 (expr)) => 0
							if (OptUtil.isConstNumber(obj, 0)) {
								stack.pop();
								IRObject rst = _updateResult(stack, node, obj);
								if (rst != null) {
									return rst;
								}
								continue QUEUE;
							}

							break;

						default:
						}
					}

					node.elements.set(node.expandIndex, obj);
					node.expandIndex++;
				}
			}

			/*******************************************/
			// element compute completed
			/*******************************************/
			stack.pop();

			IRObject e0 = node.getExpr().get(0);
			IRExpr expr = node.getExpr();

			if (node.elements != null) {
				e0 = node.elements.get(0);
				expr = node.getExpr().isEarly() ? RulpFactory.createExpressionEarly(node.elements)
						: RulpFactory.createExpression(node.elements);
			}

			node.setExpr(null); // dec ref of the expr
			IRObject rst = _computeTCO(e0, expr, interpreter, frame);
			rst = _updateResult(stack, node, rst);
			if (rst != null) {
				return rst;
			}

		} // while (!cpsQueue.isEmpty())

		throw new RException("Should not run to here: " + tcoExpr);
	}

	public static int getCallCount() {
		return callCount.get();
	}

	public static int getComputeCount() {
		return computeCount.get();
	}

	public static int getExprCount() {
		return exprCount.get();
	}

	public static int getMaxLevelCount() {
		return maxLevelCount.get();
	}

	public static int getMaxStackSize() {
		return maxStackSize.get();
	}

	public static int getNodeCount() {
		return nodeCount.get();
	}

	public static int getRebuildCount() {
		return rebuildCount.get();
	}

	public static void incCallCount() {
		callCount.getAndIncrement();
	}

	public static void incComputeCount() {
		computeCount.getAndIncrement();
	}

	public static void incTCORebuildCount() {
		rebuildCount.getAndIncrement();
	}

	public static boolean isCPSRecursive(IRExpr expr, IRFrame frame) throws RException {
		return false;
	}

	public static Set<String> listFunctionInReturn(IRExpr expr, IRFrame frame) throws RException {

		ArrayList<IRObject> returnList = new ArrayList<>();
		OptUtil.listReturnObject(expr, returnList);
		if (returnList.isEmpty()) {
			return Collections.emptySet();
		}

		HashSet<String> calleeNames = new HashSet<>();
		for (IRObject rtObj : returnList) {
			if (rtObj.getType() == RType.EXPR) {

				IRExpr rtExpr = (IRExpr) rtObj;
				if (rtExpr.size() > 0) {
					_findTCOCalleeInReturn(rtExpr.get(0), rtExpr, calleeNames, frame);
				}

			}

		}

		return calleeNames;
	}

	public static IRObject makeCPS(IRObject obj, IRFrame frame) throws RException {

		switch (obj.getType()) {

		case VAR: {

			IRVar var = (IRVar) obj;
			if (var.getValue() != null) {
				return var.getValue();
			}

			IRFrameEntry entry = frame.getEntry(var.getName());
			if (entry != null) {
				return RulpUtil.asVar(entry.getObject());
			}

			throw new RException("var entry not found: " + var);
		}

		case ATOM: {

			IRAtom atom = (IRAtom) obj;
			IRFrameEntry entry = frame.getEntry(atom.getName());
			IRObject rst = entry == null ? obj : entry.getObject();

			if (rst != null && rst.getType() == RType.VAR) {
				return ((IRVar) rst).getValue();
			}

			return rst;
		}

		case LIST:
		case EXPR: {

			ArrayList<IRObject> newList = new ArrayList<>();

			IRIterator<? extends IRObject> it = ((IRList) obj).iterator();
			while (it.hasNext()) {
				newList.add(makeCPS(it.next(), frame));
			}

			if (obj.getType() == RType.LIST) {
				return RulpFactory.createList(newList);
			}

			if (((IRExpr) obj).isEarly()) {
				return RulpFactory.createExpressionEarly(newList);
			}

			return RulpFactory.createExpression(newList);
		}

		case MEMBER:

		{
			IRMember mbr = (IRMember) obj;
			if (mbr.getValue() != null) {
				return mbr;
			}

			IRObject subObj = makeCPS(mbr.getSubject(), frame);
			if (subObj == null) {
				throw new RException("subject<" + mbr.getSubject() + "> not found");
			}

			IRSubject sub = RulpUtil.asSubject(subObj);

			// Get root object
			IRMember actMbr = sub.getMember(mbr.getName());
			if (actMbr == null) {
				throw new RException("member<" + mbr + "> not found in " + sub);
			}

			return mbr.getValue();
		}

		default:
			return obj;
		}
	}

	public static IRExpr rebuild(IRExpr expr, IRFrame frame) throws RException {

		incTCORebuildCount();

		return _rebuild(expr, frame);
	}

	public static void reset() {
		exprCount.set(0);
		callCount.set(0);
		computeCount.set(0);
		rebuildCount.set(0);
		maxLevelCount.set(0);
		maxStackSize.set(0);
		nodeCount.set(0);
	}

}
