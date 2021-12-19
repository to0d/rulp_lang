package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.F_IF;
import static alpha.rulp.lang.Constant.F_MAKE_CPS;
import static alpha.rulp.lang.Constant.F_O_ADD;
import static alpha.rulp.lang.Constant.F_O_AND;
import static alpha.rulp.lang.Constant.F_O_BY;
import static alpha.rulp.lang.Constant.F_O_DIV;
import static alpha.rulp.lang.Constant.F_O_NOT;
import static alpha.rulp.lang.Constant.F_O_OR;
import static alpha.rulp.lang.Constant.F_O_POWER;
import static alpha.rulp.lang.Constant.F_O_SUB;
import static alpha.rulp.lang.Constant.F_O_XOR;
import static alpha.rulp.lang.Constant.F_RETURN;
import static alpha.rulp.lang.Constant.O_Nil;

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

public class CPSUtils {

	static class CpsNode {

		private ArrayList<IRObject> elements = null;

		private boolean expend = false;

		private IRExpr expr;

		private int indexOfParent;

		private CpsNode parrent;

		public CpsNode(CpsNode parrent, int indexOfParent, IRExpr expr) throws RException {
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

	static final Set<String> cps_op = new HashSet<>();

	public static boolean CPS_TRACE = false;

	protected static AtomicInteger cpsCount = new AtomicInteger(0);

	static {
		cps_op.add(F_O_BY);
		cps_op.add(F_O_ADD);
		cps_op.add(F_O_DIV);
		cps_op.add(F_O_SUB);
		cps_op.add(F_O_POWER);
		cps_op.add(F_O_AND);
		cps_op.add(F_O_OR);
		cps_op.add(F_O_NOT);
		cps_op.add(F_O_XOR);
	}

	private static IRObject _compute(IRObject obj, IRFrame frame) throws RException {

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
				newList.add(rebuildCpsTree(it.next(), frame));
			}
			return newList;
		}

		case MEMBER: {
			IRMember mbr = (IRMember) obj;
			if (mbr.getValue() != null) {
				return mbr;
			}

			IRObject subObj = rebuildCpsTree(mbr.getSubject(), frame);
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

	private static boolean _findCPSCallee(IRExpr expr, Set<String> calleeNames, IRFrame frame) throws RException {

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
					_findCPSCallee((IRExpr) e, calleeNames, frame);
				}
			}
		}
			break;

		case F_IF: {

			it = expr.listIterator(2);
			while (it.hasNext()) {
				IRObject e = it.next();
				if (e.getType() == RType.EXPR) {
					_findCPSCallee((IRExpr) e, calleeNames, frame);
				}
			}
		}
			break;

		case F_RETURN:

			if (expr.size() > 1) {

				IRObject e = expr.get(1);
				if (e.getType() == RType.EXPR) {
					_findCPSCalleeInReturn((IRExpr) e, calleeNames, frame);
				}
			}

			return true;

		default:

		}
		return true;
	}

	private static void _findCPSCalleeInReturn(IRExpr expr, Set<String> calleeNames, IRFrame frame) throws RException {

		IRObject e0 = expr.get(0);

		if (_isSupportCPSFactor(e0, frame)) {

			calleeNames.add(e0.asString());

			IRIterator<? extends IRObject> it = expr.listIterator(1);
			while (it.hasNext()) {

				IRObject e = it.next();
				if (e.getType() != RType.EXPR) {
					continue;
				}

				_findCPSCalleeInReturn((IRExpr) e, calleeNames, frame);
			}
		}
	}

	private static boolean _isSupportCPSFactor(IRObject op, IRFrame frame) throws RException {

		// the cps callee should be "pure" function
		// -- no global (external) variable
		// -- no non-callee function called
		// -- no non-cps factor called
		// -- no lambda function

		if (op != null) {

			switch (op.getType()) {
			case ATOM:

				String opName = RulpUtil.asAtom(op).getName();
				IRFrameEntry entry = frame.getEntry(opName);

				// it maybe a undefined function
				if (entry == null) {
					return true;
				} else {
					return _isSupportCPSFactor(entry.getValue(), frame);
				}

			case FACTOR:
				return RulpUtil.asFactor(op).isStable();

			case FUNC:
				return true;

			default:

			}
		}

		return false;
	}

	public static IRExpr addMakeCpsExpr(IRExpr expr, IRFrame frame) throws RException {

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
					e = addMakeCpsExpr((IRExpr) e, frame);
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
					e = addMakeCpsExpr((IRExpr) e, frame);
				}

				newExpr.add(e);
			}

			return expr.isEarly() ? RulpFactory.createExpressionEarly(newExpr) : RulpFactory.createExpression(newExpr);
		}

		case F_RETURN:

			if (expr.size() == 2) {
				IRObject e1 = expr.get(1);
				if (e1.getType() == RType.EXPR) {
					return RulpFactory.createExpression(e0,
							RulpFactory.createExpression(RulpFactory.createAtom(F_MAKE_CPS), e1));
				}
			}

			break;

		default:

		}

		return expr;
	}

	public static IRObject computeCPSExpr(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (expr.isEmpty()) {
			return O_Nil;
		}

		if (CPS_TRACE) {
			System.out.println("cps: compute, " + expr);
		}

		LinkedList<CpsNode> cpsQueue = new LinkedList<>();
		cpsQueue.addLast(new CpsNode(null, -1, expr));

		while (!cpsQueue.isEmpty()) {

			if (CPS_TRACE) {
				System.out.println("cps: queue, size=" + cpsQueue.size());
			}

			cpsCount.getAndIncrement();

			CpsNode topNode = cpsQueue.peekLast();

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
								cpsQueue.addLast(new CpsNode(topNode, i, (IRExpr) obj));
							} else {
								topNode.elements.set(i, _compute(obj, frame));
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

					if (CPS_TRACE) {
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
						cpsQueue.addLast(new CpsNode(topNode.parrent, topNode.indexOfParent, (IRExpr) rst));
					} else {
						topNode.parrent.elements.set(topNode.indexOfParent, rst);
					}
				} else {

					if (rst.getType() == RType.EXPR) {
						cpsQueue.addLast(new CpsNode(null, -1, (IRExpr) rst));
					} else {
						return rst;
					}
				}
			}
		}

		throw new RException("Should not run to here: " + expr);
	}

	public static Set<String> findCPSCallee(IRExpr expr, IRFrame frame) throws RException {
		HashSet<String> calleeNames = new HashSet<>();
		_findCPSCallee(expr, calleeNames, frame);
		return calleeNames;
	}

	public static int getCPSCount() {
		return cpsCount.get();
	}

	public static IRObject rebuildCpsTree(IRObject obj, IRFrame frame) throws RException {

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
				newList.add(rebuildCpsTree(it.next(), frame));
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

			IRObject subObj = rebuildCpsTree(mbr.getSubject(), frame);
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

	public static void resetCpsLoopCount() {
		cpsCount.getAndSet(0);
	}

}
