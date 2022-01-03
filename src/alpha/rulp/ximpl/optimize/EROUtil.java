package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.*;
import static alpha.rulp.lang.Constant.F_BREAK;
import static alpha.rulp.lang.Constant.F_CASE;
import static alpha.rulp.lang.Constant.F_IF;
import static alpha.rulp.lang.Constant.F_LOOP;
import static alpha.rulp.lang.Constant.F_RETURN;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRConst;
import alpha.rulp.lang.IRDouble;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFloat;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRInteger;
import alpha.rulp.lang.IRLong;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RArithmeticOperator;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.MathUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.control.XRFactorCase;
import alpha.rulp.ximpl.control.XRFactorLoop;

public class EROUtil {

	static class ERO {

		public IRExpr inputExpr = null;

		public IRObject outputObj = null;

		public void setInputExpr(IRExpr inputExpr) {
			this.inputExpr = inputExpr;
			this.outputObj = null;
		}
	}

	protected static AtomicInteger computeCount = new AtomicInteger(0);

	protected static AtomicInteger rebuildCount = new AtomicInteger(0);

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

	private static void _incComputeCount() {
		computeCount.getAndIncrement();
	}

	private static boolean _isEROExpr(IRObject e0, IRExpr expr, IRFrame frame) throws RException {

		if (!OptUtil.isAtomFactor(e0)) {
			return false;
		}

		if (!OptUtil.isConstValue(expr.listIterator(1))) {
			return false;
		}

		return true;
	}

	private static boolean _rebuild(ERO cc0, IRInterpreter interpreter, IRFrame frame) throws RException {

		IRExpr expr = cc0.inputExpr;

		if (expr.isEmpty()) {
			return true;
		}

		IRObject e0 = RulpUtil.lookup(expr.get(0), interpreter, frame);
		if (_isEROExpr(e0, expr, frame)) {
			return true;
		}

		int size = expr.size();
		List<IRObject> rebuildList = new ArrayList<>();
		ERO childCC0 = new ERO();

		int childReBuild = 0;
		int childUpdate = 0;

		for (int i = 0; i < size; ++i) {

			IRObject ex = i == 0 ? e0 : expr.get(i);
			boolean reBuild = false;

			if (ex.getType() == RType.EXPR) {

				childCC0.setInputExpr((IRExpr) ex);
				reBuild = _rebuild(childCC0, interpreter, frame);

				if (reBuild) {
					rebuildList.add(childCC0.outputObj);
				} else if (childCC0.outputObj != null) {
					rebuildList.add(childCC0.outputObj);
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
				_incComputeCount();
			}
		}

		if (e0.getType() == RType.FACTOR) {

			IRObject rebuildObj = null;

			switch (e0.asString()) {

			// (if true A B) or (if false A B)
			case F_IF:
				rebuildObj = _rebuildIf(rebuildList);
				break;

			// (case a (a action) (b action))
			case F_CASE:
				rebuildObj = _rebuildCase(rebuildList);
				break;

			// (do () (b action))
			case A_DO:
				rebuildObj = _rebuildDo(rebuildList);
				break;

			// (loop for x from 1 to 3 do ..
			// (loop a)
			case F_LOOP:
				rebuildObj = _rebuildLoop(e0, expr, rebuildList);
				break;

			case F_O_BY:
				rebuildObj = _rebuildBy(rebuildList);
				break;

			case F_O_ADD:
				rebuildObj = _rebuildAdd(rebuildList);
				break;

			case F_O_SUB:
				rebuildObj = _rebuildSub(rebuildList);
				break;

			case F_O_POWER:
				rebuildObj = _rebuildPower(rebuildList);
				break;

			default:
			}

			if (rebuildObj != null) {
				cc0.outputObj = rebuildObj;
				_incComputeCount();
				return false;
			}
		}

		if (rebuildCount > 0 || childUpdate > 0) {
			cc0.outputObj = RulpFactory.createExpression(rebuildList);
		}

		return false;
	}

	private static IRObject _rebuildAdd(List<IRObject> rebuildList) throws RException {

		int size = rebuildList.size();

		// (+)
		if (size == 1) {
			return OptUtil.asExpr(null);
		}

		// (+ a)
		if (size == 2) {
			return rebuildList.get(1);
		}

		size = _rebuildAddBy(rebuildList, 1, size, RArithmeticOperator.ADD);
		switch (size) {
		case -1:
			return null;

		case 0:
			return O_INT_0;

		case 1:
			return rebuildList.get(1);

		default:
			return RulpFactory.createExpression(rebuildList.subList(0, size + 1));
		}

	}

	private static int _rebuildAddBy(List<IRObject> list, int fromIndex, int toIndex, RArithmeticOperator op)
			throws RException {

		int size = toIndex - fromIndex;

		// (*)
		// (* 1)
		if (size < 2) {
			return -1;
		}

		int numFirstPos = -1;
		IRObject numObj = null;
		for (int i = 0; i < size; ++i) {
			IRObject ex = list.get(fromIndex + i);
			if (OptUtil.isConstNumber(ex)) {
				numFirstPos = i;
				numObj = ex;
				break;
			}
		}

		if (numFirstPos == -1) {
			return -1;
		}

		// (* 0)
		if (RArithmeticOperator.BY == op && OptUtil.isConstNumber(numObj, 0)) {
			list.set(fromIndex, O_INT_0);
			return 1;
		}

		int pos = numFirstPos + 1;
		for (int i = numFirstPos + 1; i < size; ++i) {

			IRObject ex = list.get(fromIndex + i);
			if (OptUtil.isConstNumber(ex)) {

				numObj = MathUtil.computeArithmeticExpression(op, numObj, ex);
				if (RArithmeticOperator.BY == op && OptUtil.isConstNumber(numObj, 0)) {
					list.set(fromIndex, O_INT_0);
					return 1;
				}

			} else {

				if (pos != i) {
					list.set(fromIndex + pos, ex);
				}

				pos++;
			}
		}

		// (* 1 a b c) ==> (* a b c)
		// (+ 0 a b c) ==> (+ a b c)
		if ((RArithmeticOperator.BY == op && OptUtil.isConstNumber(numObj, 1))
				|| (RArithmeticOperator.ADD == op && OptUtil.isConstNumber(numObj, 0))) {

			// (* 0) ==> 0
			// (+ 0) ==> 0
			if (pos == 1) {
				list.set(fromIndex, O_INT_0);
				return 1;
			}

			// Move left
			for (int i = numFirstPos; i < (pos - 1); ++i) {
				list.set(fromIndex + i, list.get(fromIndex + i + 1));
			}

			return pos - 1;
		}

		// Move right
		// (+ a b 0 c) ==> (+ a a b c)
		for (int i = numFirstPos; i > 0; --i) {
			list.set(i + fromIndex, list.get(i + fromIndex - 1));
		}

		// (+ a a b c) ==> (+ n a b c)
		list.set(fromIndex, numObj);
		return pos;
	}

	private static IRObject _rebuildBy(List<IRObject> rebuildList) throws RException {
		return _rebuildBy(rebuildList, 0, rebuildList.size());
	}

	private static IRObject _rebuildBy(List<IRObject> elementList, int fromIndex, int toIndex) throws RException {

		int size = toIndex - fromIndex;

		// (*)
		if (size == 1) {
			return OptUtil.asExpr(null);
		}

		// (* a)
		if (size == 2) {
			return elementList.get(fromIndex + 1);
		}

		ArrayList<IRObject> nums = null;
		int pos = 1;

		// (* x n y)
		for (int i = 1; i < size; ++i) {

			IRObject ex = elementList.get(fromIndex + i);
			if (OptUtil.isConstNumber(ex)) {

				if (nums == null) {
					nums = new ArrayList<>();
				}

				nums.add(ex);

			} else {

				if (pos != i) {
					elementList.set(fromIndex + pos, ex);
				}

				pos++;
			}
		}

		// (* a b c)
		if (nums == null) {
			return null;
		}

		IRObject numObj = nums.get(0);
		for (int i = 1; i < nums.size(); ++i) {
			numObj = MathUtil.computeArithmeticExpression(RArithmeticOperator.BY, numObj, nums.get(i));
		}

		if (pos == 1) {
			return numObj;
		}

		// (* 0 a b) ==> 0
		if (OptUtil.isConstNumber(numObj, 0)) {
			return O_INT_0;
		}

		// (* 1 a b) ==> (* a b)
		if (OptUtil.isConstNumber(numObj, 1)) {

			if (pos == 2) {
				return elementList.get(fromIndex + 1); // (* 1 a) ==> a
			}

			// (* 1 a b) ==>(+ a b)
			return RulpFactory.createExpression(elementList.subList(fromIndex, fromIndex + pos));
		}

		// (* a b 2) ==> (* 2 a b)
		for (int i = fromIndex + pos; i > fromIndex + 1; --i) {
			elementList.set(i, elementList.get(i - 1));
		}
		elementList.set(fromIndex + 1, numObj);
		return RulpFactory.createExpression(elementList.subList(fromIndex, fromIndex + pos + 1));
	}

	// (case a (a action) (b action))
	private static IRObject _rebuildCase(List<IRObject> rebuildList) throws RException {

		int size = rebuildList.size();
		if (size < 3) {
			return null;
		}

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
					return OptUtil.asExpr(caseClause.get(1));
				}
			}

			// no any case match, return empty expression
			if (!nonConstCaseValueFound) {
				return OptUtil.asExpr(null);
			}
		}

		return null;
	}

	// (do () (b action))
	private static IRObject _rebuildDo(List<IRObject> rebuildList) throws RException {

		int size = rebuildList.size();
		int pos = _removeEmptyExpr(rebuildList, 1);

		switch (pos) {
		case 1: // no expr found
			return OptUtil.asExpr(null);

		case 2: // only one expr found, remove DO factor
			return OptUtil.asExpr(rebuildList.get(1));

		default:

			// empty expr found
			if (pos != size) {
				return RulpFactory.createExpression(rebuildList.subList(0, pos));
			}
		}

		return null;
	}

	// (if true A B) or (if false A B)
	private static IRObject _rebuildIf(List<IRObject> rebuildList) throws RException {

		int size = rebuildList.size();
		if (size < 3) {
			return null;
		}

		// (if true A B) or (if false A B)
		IRObject e1 = rebuildList.get(1);
		if (e1.getType() == RType.BOOL) {

			IRObject rst = null;
			if (RulpUtil.asBoolean(e1).asBoolean()) {
				rst = rebuildList.get(2);
			} else if (rebuildList.size() > 3) {
				rst = rebuildList.get(3);
			}

			return OptUtil.asExpr(rst);
		}

		return null;
	}

	private static IRObject _rebuildLoop(IRObject e0, IRExpr expr, List<IRObject> rebuildList) throws RException {

		int size = rebuildList.size();

		// (loop for x from 1 to 3 do ...
		if (OptUtil.isFactor(e0, F_LOOP) && XRFactorLoop.isLoop2(expr)) {

			IRObject fromObj = XRFactorLoop.getLoop2FromObject(expr);
			IRObject toObj = XRFactorLoop.getLoop2ToObject(expr);

			if (fromObj.getType() == RType.INT && toObj.getType() == RType.INT) {

				int fromIndex = RulpUtil.asInteger(fromObj).asInteger();
				int toIndex = RulpUtil.asInteger(toObj).asInteger();

				// from 3 to 1 ==> empty expr
				if (fromIndex > toIndex) {
					return OptUtil.asExpr(null);
				}

				// from 1 to 1 ==> (do action)
				if (fromIndex == toIndex) {

					ArrayList<IRObject> doActions = new ArrayList<>();
					RulpUtil.addAll(doActions, XRFactorLoop.getLoop2DoList(expr));

					if (doActions.size() == 0) {
						return OptUtil.asExpr(null);
					}

					if (doActions.size() == 1) {
						return OptUtil.asExpr(doActions.get(0));
					}

					return RulpUtil.toDoExpr(doActions);
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
				return RulpFactory.createExpression(rebuildList.subList(0, pos));
			}
		}

		return null;
	}

	private static IRObject _rebuildPower(List<IRObject> rebuildList) throws RException {

		int size = rebuildList.size();

		// (*)
		if (size == 1) {
			return OptUtil.asExpr(null);
		}

		// (power a) ==> a
		if (size == 2) {
			return rebuildList.get(1);
		}

		int indexSize = _rebuildSubDivPower(rebuildList, 1, size, RArithmeticOperator.POWER);
		if (indexSize == -1) {
			return null;
		}

		switch (indexSize) {

		// no change
		case -1:
			return null;

		// 0
		case 0:
			return O_INT_0;

		case 1:
			return rebuildList.get(1);

		default:
			return RulpFactory.createExpression(rebuildList.subList(0, indexSize + 1));
		}

	}

	private static IRObject _rebuildSub(List<IRObject> rebuildList) throws RException {

		int size = rebuildList.size();

		// (-)
		if (size == 1) {
			return OptUtil.asExpr(null);
		}

		// (- a) ==> a
		if (size == 2) {
			return rebuildList.get(1);
		}

		int indexSize = _rebuildSubDivPower(rebuildList, 1, size, RArithmeticOperator.SUB);
		if (indexSize == -1) {
			return null;
		}

		switch (indexSize) {

		// no change
		case -1:
			return null;

		// 0
		case 0:
			return O_INT_0;

		case 1:
			return rebuildList.get(1);

		default:
			return RulpFactory.createExpression(rebuildList.subList(0, indexSize + 1));
		}

	}

	private static int _rebuildSubDivPower(List<IRObject> list, int fromIndex, int toIndex, RArithmeticOperator op)
			throws RException {

		int size = toIndex - fromIndex;

		// (power)
		// (- 1)
		// (/ 5)
		if (size < 2) {
			return -1;
		}

		IRObject e1 = list.get(fromIndex);

		// (x^y)^z ==> x^(y*z)
		// (a-b-c) ==> (a-(b+c))
		// (a/b/c) ==> (a/(b*c))

		RArithmeticOperator op2 = null;
		switch (op) {
		case POWER:
			op2 = RArithmeticOperator.BY;
			// (power 1 e2 e3 e4...) ==> 1
			if (OptUtil.isConstNumber(e1, 1)) {
				list.set(fromIndex, O_INT_1);
				return 1;
			}
			break;

		case SUB:
			op2 = RArithmeticOperator.ADD;
			break;

		case DIV:
			op2 = RArithmeticOperator.BY;
			// (/ 0 e2 e3 e4...) ==> 1
			if (OptUtil.isConstNumber(e1, 0)) {
				list.set(fromIndex, O_INT_0);
				return 1;
			}
			break;
		default:
			throw new RException("not support: " + op);
		}

		final int indexSize = _rebuildAddBy(list, fromIndex + 1, toIndex, op2);
		int rightSize = indexSize;
		if (indexSize == -1) {
			rightSize = size - 1;
		}

		// (power a 1 b) => (power a b)
		if (rightSize > 0 && RArithmeticOperator.POWER == op && OptUtil.isConstNumber(list.get(fromIndex + 1), 1)) {

			if (rightSize == 1) {
				list.set(fromIndex, e1);
				return 1;
			}

			// Move left
			// (power a 2 b c) ==> (power a b c)
			for (int i = 0; i < rightSize - 1; ++i) {
				list.set(i + fromIndex + 1, list.get(i + fromIndex + 2));
			}

			rightSize--;
		}

		if (rightSize == 0 || (rightSize == 1 && OptUtil.isConstNumber(list.get(fromIndex + 1), 0))) {

			switch (op) {

			// (power a 0) ==> 1
			case POWER:
				list.set(fromIndex, O_INT_1);
				return 1;

			// (- a 0) ==> a
			case SUB:
				list.set(fromIndex, e1);
				return 1;

			// (/ a 0) ==> a / 0
			case DIV:
				throw new RException("Can't div 0: " + list.subList(fromIndex, toIndex));

			default:
				throw new RException("not support: " + op);
			}
		}

		IRObject e2 = list.get(fromIndex + 1);
		if (OptUtil.isConstNumber(e1) && OptUtil.isConstNumber(e2)) {

			e1 = MathUtil.computeArithmeticExpression(op, e1, e2);

			// (/ 4 2)
			if (size == 2) {
				list.set(fromIndex, e1);
				return 1;
			}

			// (/ 0 a b) => 0
			if (RArithmeticOperator.DIV == op && OptUtil.isConstNumber(e1, 0)) {
				return 0;
			}

			// (power 1 a b)
			if (RArithmeticOperator.POWER == op && OptUtil.isConstNumber(e1, 1)) {
				list.set(fromIndex, O_INT_1);
				return 1;
			}

			// Move left
			// (power 2 2 a b) ==> (power 4 a b)
			for (int i = fromIndex + 1; i < toIndex - 1; ++i) {
				list.set(i, list.get(i + 1));
			}

			list.set(fromIndex, e1);
			return size - 1;
		}

		if (indexSize == -1) {
			return -1; // no change
		}

		return rightSize + 1;
	}

//	private static IRObject _rebuildSub(List<IRObject> rebuildList) throws RException {
//		return _rebuildSub(rebuildList, 0, rebuildList.size());
//	}
//
//	private static IRObject _rebuildSub(List<IRObject> elementList, int fromIndex, int toIndex) throws RException {
//
//		int size = toIndex - fromIndex;
//
//		// (-)
//		if (size == 1) {
//			return OptUtil.asExpr(null);
//		}
//
//		// (- a)
//		if (size == 2) {
//			return elementList.get(fromIndex + 1);
//		}
//
//		IRObject e1 = elementList.get(fromIndex + 1);
//
//		// (- 3 a 2 b) ==> (- 1 a b)
//		if (OptUtil.isConstNumber(e1)) {
//
//			int pos = 2;
//
//			for (int i = 2; i < size; ++i) {
//
//				IRObject ex = elementList.get(fromIndex + i);
//				if (OptUtil.isConstNumber(ex)) {
//					e1 = MathUtil.computeArithmeticExpression(RArithmeticOperator.SUB, e1, ex);
//
//				} else {
//
//					if (pos != i) {
//						elementList.set(fromIndex + pos, ex);
//					}
//
//					pos++;
//				}
//			}
//
//			// (- 3 a b) ==> no change
//			if (pos == size) {
//				return null;
//			}
//
//			elementList.set(fromIndex + 1, e1);
//			return RulpFactory.createExpression(elementList.subList(fromIndex, fromIndex + pos));
//
//		}
//		// (- a 3 2 b) ==> (- a 5 b)
//		else {
//
//			ArrayList<IRObject> nums = null;
//			int pos = 2;
//
//			for (int i = 2; i < size; ++i) {
//
//				IRObject ex = elementList.get(fromIndex + i);
//				if (OptUtil.isConstNumber(ex)) {
//
//					if (nums == null) {
//						nums = new ArrayList<>();
//					}
//
//					nums.add(ex);
//
//				} else {
//
//					if (pos != i) {
//						elementList.set(fromIndex + pos, ex);
//					}
//
//					pos++;
//				}
//			}
//
//			// (- a b) ==> no change
//			if (nums == null) {
//				return null;
//			}
//
//		}
//
//		// (- x n y)
//		for (int i = 1; i < size; ++i) {
//
//			IRObject ex = elementList.get(fromIndex + i);
//			if (OptUtil.isConstNumber(ex)) {
//
//				if (nums == null) {
//					nums = new ArrayList<>();
//				}
//
//				nums.add(ex);
//
//			} else {
//
//				if (pos != i) {
//					elementList.set(fromIndex + pos, ex);
//				}
//
//				pos++;
//			}
//		}
//
//		// (+ a b c)
//		if (nums == null) {
//			return null;
//		}
//
//		IRObject numObj = nums.get(0);
//		for (int i = 1; i < nums.size(); ++i) {
//			numObj = MathUtil.computeArithmeticExpression(RArithmeticOperator.ADD, numObj, nums.get(i));
//		}
//
//		if (pos == 1) {
//			return numObj;
//		}
//
//		if (OptUtil.isConstNumber(numObj, 0)) {
//
//			if (pos == 2) {
//				return elementList.get(fromIndex + 1); // (+ 0 a) ==> a
//			}
//
//			// (- 0 a b) ==>(- a b)
//			return RulpFactory.createExpression(elementList.subList(fromIndex, fromIndex + pos));
//		}
//
//		// (- 2 a b) ==> (- a b 2)
//		elementList.set(fromIndex + (pos++), numObj);
//		return RulpFactory.createExpression(elementList.subList(fromIndex, fromIndex + pos));
//	}

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

	public static int getComputeCount() {
		return computeCount.get();
	}

	public static int getRebuildCount() {
		return rebuildCount.get();
	}

	// (Op A1 A2 ... Ak), Op is CC0 factor, Ak is const value and return const value
	public static IRObject rebuild(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {

		rebuildCount.getAndIncrement();

		ERO cc0 = new ERO();
		cc0.setInputExpr(expr);

		if (!_rebuild(cc0, interpreter, frame)) {
			return cc0.outputObj == null ? expr : cc0.outputObj;
		}

		if (cc0.outputObj != null) {
			return cc0.outputObj;
		}

		IRObject rst = interpreter.compute(frame, expr);
		_incComputeCount();
		return rst;
	}

	public static void reset() {

		computeCount.set(0);
		rebuildCount.set(0);
	}
}
