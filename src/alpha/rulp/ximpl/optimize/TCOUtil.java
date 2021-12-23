package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.F_IF;
import static alpha.rulp.lang.Constant.F_RETURN;
import static alpha.rulp.lang.Constant.F_RETURN_TCO;
import static alpha.rulp.lang.Constant.O_Nil;
import static alpha.rulp.lang.Constant.O_RETURN_TCO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
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
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;

public class TCOUtil {

	static class TCONode {

		private ArrayList<IRObject> elements = null;

		private boolean expend = false;

		private IRExpr expr;

		private int indexOfParent;

		private TCONode parrent;

		public TCONode(TCONode parrent, int indexOfParent, IRExpr expr) throws RException {
			super();
			this.parrent = parrent;
			this.indexOfParent = indexOfParent;

			setExpr(expr);
		}

		public IRExpr getExpr() {
			return expr;
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

	protected static AtomicInteger TCOComputeCount = new AtomicInteger(0);

	protected static AtomicInteger TCOExprCount = new AtomicInteger(0);

	public static boolean TRACE = false;

	private static IRObject _computeTCO(IRObject obj, IRFrame frame) throws RException {

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

		case LIST: {

			IRList newList = RulpFactory.createVaryList();
			IRIterator<? extends IRObject> it = ((IRList) obj).iterator();
			while (it.hasNext()) {
				newList.add(returnTCO(it.next(), frame));
			}
			return newList;
		}

		case MEMBER: {

			IRMember mbr = (IRMember) obj;
			if (mbr.getValue() != null) {
				return mbr;
			}

			IRObject subObj = returnTCO(mbr.getSubject(), frame);
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
			if (!RulpUtil.asFactor(e0).isStable()) {
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

	private static void _incTCOComputeCount() {
		TCOComputeCount.getAndIncrement();
	}

	private static void _incTCOExprCount() {
		TCOExprCount.getAndIncrement();
	}

	private static boolean _listFunctionInReturn(IRExpr expr, Set<String> calleeNames, IRFrame frame)
			throws RException {

		IRObject e0 = expr.get(0);
		if (e0.getType() != RType.ATOM) {
			return true;
		}

		IRIterator<? extends IRObject> it = null;

		switch (RulpUtil.asAtom(e0).getName()) {
		case A_DO: {
			it = expr.listIterator(1);
			while (it.hasNext()) {
				IRObject e = it.next();
				if (e.getType() == RType.EXPR) {
					_listFunctionInReturn((IRExpr) e, calleeNames, frame);
				}
			}
		}
			break;

		case F_IF: {

			it = expr.listIterator(2);
			while (it.hasNext()) {
				IRObject e = it.next();
				if (e.getType() == RType.EXPR) {
					_listFunctionInReturn((IRExpr) e, calleeNames, frame);
				}
			}
		}
			break;

		case F_RETURN:
		case F_RETURN_TCO:

			if (expr.size() > 1 && expr.get(1).getType() == RType.EXPR) {
				IRExpr e1 = (IRExpr) expr.get(1);
				if (e1.size() > 0) {
					_findTCOCalleeInReturn(e1.get(0), e1, calleeNames, frame);
				}
			}

			return true;

		default:

		}
		return true;
	}

	public static IRObject computeTCO(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (expr.isEmpty()) {
			return O_Nil;
		}

		if (TRACE) {
			System.out.println("cps: compute, " + expr);
		}

		LinkedList<TCONode> cpsQueue = new LinkedList<>();
		cpsQueue.addLast(new TCONode(null, -1, expr));

		while (!cpsQueue.isEmpty()) {

			if (TRACE) {
				System.out.println("cps: queue, size=" + cpsQueue.size());
			}

			_incTCOComputeCount();

			TCONode topNode = cpsQueue.peekLast();

			// not expand
			if (!topNode.expend) {

				if (topNode.getExpr().isEmpty()) {

					cpsQueue.pollLast();
					topNode.parrent.elements.set(topNode.indexOfParent, O_Nil);

				} else {

					topNode.expend = true;

					boolean findExpr = false;
					{
						IRIterator<? extends IRObject> it = topNode.getExpr().iterator();
						while (it.hasNext()) {
							if (it.next().getType() == RType.EXPR) {
								findExpr = true;
								break;
							}
						}
					}

					if (findExpr) {

						topNode.elements = new ArrayList<>();

						IRIterator<? extends IRObject> it = topNode.getExpr().iterator();
						while (it.hasNext()) {
							topNode.elements.add(it.next());
						}

						for (int i = topNode.elements.size() - 1; i >= 0; --i) {

							IRObject obj = topNode.elements.get(i);
							if (obj.getType() == RType.EXPR) {
								cpsQueue.addLast(new TCONode(topNode, i, (IRExpr) obj));
							} else {
								topNode.elements.set(i, _computeTCO(obj, frame));
							}
						}
					}
				}
			}
			// expand node
			else {

				cpsQueue.pollLast();

				IRObject rst = null;
				IRObject e0 = topNode.getExpr().get(0);
				IRExpr newExpr = topNode.getExpr();

				if (topNode.elements != null) {
					e0 = topNode.elements.get(0);
					newExpr = topNode.getExpr().isEarly() ? RulpFactory.createExpressionEarly(topNode.elements)
							: RulpFactory.createExpression(topNode.elements);
				}

				topNode.setExpr(null); // dec ref of the expr

				switch (e0.getType()) {
				case FACTOR:
				case MACRO:
				case TEMPLATE:
					rst = RuntimeUtil.computeCallable((IRCallable) e0, newExpr, interpreter, frame);
					break;

				case FUNC:

					if (TRACE) {
						System.out.println("cps: call fun, " + newExpr);
					}

					rst = RuntimeUtil.computeFun((IRFunction) e0, newExpr, interpreter, frame);
					break;

				case MEMBER:

					IRObject e1m = ((IRMember) e0).getValue();
					if (e1m.getType() != RType.FUNC) {
						throw new RException("factor not found: " + newExpr);
					}

					rst = RuntimeUtil.computeFun((IRFunction) e1m, newExpr, interpreter, frame);
					break;

				default:
					throw new RException("factor not found: " + newExpr);
				}

				if (topNode.parrent != null) {
					if (rst.getType() == RType.EXPR) {
						cpsQueue.addLast(new TCONode(topNode.parrent, topNode.indexOfParent, (IRExpr) rst));
					} else {
						topNode.parrent.elements.set(topNode.indexOfParent, rst);
					}
				} else {

					if (rst.getType() == RType.EXPR) {
						cpsQueue.addLast(new TCONode(null, -1, (IRExpr) rst));
					} else {
						return rst;
					}
				}
			}
		}

		throw new RException("Should not run to here: " + expr);
	}

	public static int getTCOComputeCount() {
		return TCOComputeCount.get();
	}

	public static int getTCOExprCount() {
		return TCOExprCount.get();
	}

	public static boolean isCPSRecursive(IRExpr expr, IRFrame frame) throws RException {
		return false;
	}

	public static Set<String> listFunctionInReturn(IRExpr expr, IRFrame frame) throws RException {
		HashSet<String> calleeNames = new HashSet<>();
		_listFunctionInReturn(expr, calleeNames, frame);
		return calleeNames;
	}

	public static IRExpr rebuildTCO(IRExpr expr, IRFrame frame) throws RException {

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
					e = rebuildTCO((IRExpr) e, frame);
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
					e = rebuildTCO((IRExpr) e, frame);
				}

				newExpr.add(e);
			}

			return expr.isEarly() ? RulpFactory.createExpressionEarly(newExpr) : RulpFactory.createExpression(newExpr);
		}

		case F_RETURN:
			if (expr.size() == 2) {
				IRObject e1 = expr.get(1);
				if (e1.getType() == RType.EXPR) {
					_incTCOExprCount();
					return RulpFactory.createExpression(O_RETURN_TCO, e1);
				}
			}

			break;

		default:

		}

		return expr;
	}

	public static void reset() {

		TCOComputeCount.set(0);
		TCOExprCount.set(0);
	}

	public static IRObject returnTCO(IRObject obj, IRFrame frame) throws RException {

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
				newList.add(returnTCO(it.next(), frame));
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

			IRObject subObj = returnTCO(mbr.getSubject(), frame);
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

}