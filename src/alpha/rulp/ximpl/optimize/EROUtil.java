package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.F_BREAK;
import static alpha.rulp.lang.Constant.F_B_AND;
import static alpha.rulp.lang.Constant.F_B_OR;
import static alpha.rulp.lang.Constant.F_CASE;
import static alpha.rulp.lang.Constant.F_IF;
import static alpha.rulp.lang.Constant.F_LOOP;
import static alpha.rulp.lang.Constant.F_O_ADD;
import static alpha.rulp.lang.Constant.F_O_BY;
import static alpha.rulp.lang.Constant.F_O_DIV;
import static alpha.rulp.lang.Constant.F_O_MOD;
import static alpha.rulp.lang.Constant.F_O_POWER;
import static alpha.rulp.lang.Constant.F_O_SUB;
import static alpha.rulp.lang.Constant.F_RETURN;
import static alpha.rulp.lang.Constant.O_BY;
import static alpha.rulp.lang.Constant.O_False;
import static alpha.rulp.lang.Constant.O_INT_0;
import static alpha.rulp.lang.Constant.O_INT_1;
import static alpha.rulp.lang.Constant.O_POWER;
import static alpha.rulp.lang.Constant.O_True;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
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

	static class ArithmeticUtil {

		private static int _rebuildAdd(List<IRObject> list, int fromIndex, int toIndex) throws RException {

			int update = 0;
			int endIndex = _expandExpr(list, fromIndex, toIndex, F_O_ADD);
			if (endIndex != -1) {
				++update;
			} else {
				endIndex = toIndex;
			}

			int size = _rebuildAddBy(list, fromIndex, endIndex, RArithmeticOperator.ADD);
			if (size != -1) {
				update++;
			}

			int listSize = size;
			if (listSize == -1) {
				listSize = endIndex - fromIndex;
			}

			if (listSize >= 2) {
				int size2 = _rebuildSameElement(list, fromIndex, fromIndex + listSize, RArithmeticOperator.BY);
				if (size2 != -1) {
					size = size2;
					update++;
				}
			}

			return update == 0 ? -1 : size;
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

		private static int _rebuildBy(List<IRObject> list, int fromIndex, int toIndex) throws RException {

			int update = 0;
			int endIndex = _expandExpr(list, fromIndex, toIndex, F_O_BY);
			if (endIndex != -1) {
				++update;
			} else {
				endIndex = toIndex;
			}

			int size = _rebuildAddBy(list, fromIndex, endIndex, RArithmeticOperator.BY);
			if (size != -1) {
				update++;
			}

			int listSize = size;
			if (listSize == -1) {
				listSize = endIndex - fromIndex;
			}

			if (listSize >= 2) {
				int size2 = _rebuildSameElement(list, fromIndex, fromIndex + listSize, RArithmeticOperator.POWER);
				if (size2 != -1) {
					size = size2;
					update++;
				}
			}

			return update == 0 ? -1 : size;
		}

		private static int _rebuildDiv(List<IRObject> list, int fromIndex, int toIndex) throws RException {

			int size = toIndex - fromIndex;

			if (size > 1) {

				IRObject e0 = list.get(fromIndex);
				if (!OptUtil.isConstNumber(e0)) {

					String uniq0 = _toUniqString(e0);

					int findIndex = -1;
					for (int i = fromIndex + 1; i < toIndex; ++i) {
						IRObject ex = list.get(i);
						if (!OptUtil.isConstNumber(ex) && _toUniqString(ex).equals(uniq0)) {
							findIndex = i;
							break;
						}
					}

					if (findIndex != -1) {

						list.set(fromIndex, O_INT_1);
						for (int i = findIndex + 1; i < toIndex; ++i) {
							list.set(i - 1, list.get(i));
						}

						size--;
					}
				}

			}

			int size2 = _rebuildSubDivPower(list, fromIndex, fromIndex + size, RArithmeticOperator.DIV);
			int listSize;
			if (size2 == -1) {
				listSize = size;
			} else {
				listSize = size2;
			}

			if (listSize >= 3) {
				int size3 = _rebuildSameElement(list, fromIndex + 1, fromIndex + listSize, RArithmeticOperator.POWER);
				if (size3 != -1) {
					listSize = size3 + 1;
				}
			}

			if ((toIndex - fromIndex) == listSize) {
				return -1;
			}

			return listSize;
		}

		private static int _rebuildMod(List<IRObject> list, int fromIndex, int toIndex) throws RException {

			int size = toIndex - fromIndex;

			// (%)
			// (% 1)
			if (size < 2) {
				return -1;
			}

			// (% 0 a b) ==> 0
			IRObject e1 = list.get(fromIndex);
			if (OptUtil.isConstNumber(e1, 0)) {
				list.set(fromIndex, O_INT_0);
				return 1;
			}

			IRObject e2 = list.get(fromIndex + 1);

			// (% a a b) ==> 0
			if (RulpUtil.equal(e1, e2)) {
				list.set(fromIndex, O_INT_0);
				return 1;
			}

			// (% a 0) ==> err
			if (OptUtil.isConstNumber(e2, 0)) {
				throw new RException("Can't mode 0: " + list.subList(fromIndex, toIndex));
			}

			IRObject lastObj = e2;
			int pos = fromIndex + 2;

			for (int i = fromIndex + 2; i < toIndex; ++i) {

				IRObject ex = list.get(i);

				// (% a b 0)
				if (OptUtil.isConstNumber(ex, 0)) {
					throw new RException("Can't mode 0: " + list.subList(fromIndex, toIndex));
				}

				// (% a b 1 c) ==> 0
				if (OptUtil.isConstNumber(ex, 1)) {
					list.set(fromIndex, O_INT_0);
					return 1;
				}

				// (% a b b c) ==> (% a b c)
				if (!RulpUtil.equal(ex, lastObj)) {

					if (pos != i) {
						list.set(pos, ex);
					}

					pos++;

					lastObj = ex;
				}
			}

			// no change
			if (pos == toIndex) {
				return -1;
			}

			return pos - fromIndex;
		}

		private static int _rebuildPower(List<IRObject> list, int fromIndex, int toIndex) throws RException {

			int size = toIndex - fromIndex;

			// (power)
			// (- 1)
			// (/ 5)
			if (size < 2) {
				return -1;
			}

			IRObject e1 = list.get(fromIndex);

			// (power 1 e2 e3 e4...) ==> 1
			if (OptUtil.isConstNumber(e1, 1)) {
				list.set(fromIndex, O_INT_1);
				return 1;
			}

			int listSize = size;
			boolean update = false;

			// (power a b c) == (power a (* b c))
			if (listSize >= 3) {

				int size2 = _rebuildBy(list, fromIndex + 1, fromIndex + listSize);
				if (size2 == -1) {
					size2 = listSize - 1;
				} else {
					update = true;
				}

				if (size2 > 1) {

					IRObject[] byExpr = new IRObject[size2 + 1];
					byExpr[0] = O_BY;

					for (int i = 0; i < size2; ++i) {
						byExpr[i + 1] = list.get(fromIndex + 1 + i);
					}

					list.set(fromIndex + 1, RulpFactory.createExpression(byExpr));
					listSize = 2;
					update = true;

				} else {
					listSize = size2 + 1;
				}
			}

			if (listSize == 2) {

				// (power a 0) ==> 1
				if (OptUtil.isConstNumber(list.get(fromIndex + 1), 0)) {
					list.set(fromIndex, O_INT_1);
					return 1;
				}

				// (power a 1) ==> a
				if (OptUtil.isConstNumber(list.get(fromIndex + 1), 1)) {
					list.set(fromIndex, e1);
					return 1;
				}
			}

			if (!update) {
				return -1;
			}

			return listSize;
		}

		private static int _rebuildSub(List<IRObject> list, int fromIndex, int toIndex) throws RException {

			int size = toIndex - fromIndex;

			if (size > 1) {

				IRObject e0 = list.get(fromIndex);
				if (!OptUtil.isConstNumber(e0)) {

					String uniq0 = _toUniqString(e0);

					int findIndex = -1;
					for (int i = fromIndex + 1; i < toIndex; ++i) {
						IRObject ex = list.get(i);
						if (!OptUtil.isConstNumber(ex) && _toUniqString(ex).equals(uniq0)) {
							findIndex = i;
							break;
						}
					}

					if (findIndex != -1) {

						list.set(fromIndex, O_INT_0);
						for (int i = findIndex + 1; i < toIndex; ++i) {
							list.set(i - 1, list.get(i));
						}

						size--;
					}
				}

			}

			int size2 = _rebuildSubDivPower(list, fromIndex, fromIndex + size, RArithmeticOperator.SUB);
			int listSize;
			if (size2 == -1) {
				listSize = size;
			} else {
				listSize = size2;
			}

			if (listSize >= 3) {
				int size3 = _rebuildSameElement(list, fromIndex + 1, fromIndex + listSize, RArithmeticOperator.BY);
				if (size3 != -1) {
					listSize = size3 + 1;
				}
			}

			if ((toIndex - fromIndex) == listSize) {
				return -1;
			}

			return listSize;
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

			if (rightSize == 0 || (rightSize == 1 && OptUtil.isConstNumber(list.get(fromIndex + 1), 0))) {

				switch (op) {

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

		public static IRObject rebuild(List<IRObject> rebuildList, RArithmeticOperator op) throws RException {

			int size = rebuildList.size();

			// (*)
			if (size == 1) {
				return OptUtil.asExpr(null);
			}

			// (power a) ==> a
			if (size == 2) {
				return rebuildList.get(1);
			}

			switch (op) {
			case SUB:
				size = _rebuildSub(rebuildList, 1, size);
				break;

			case POWER:
				size = _rebuildPower(rebuildList, 1, size);
				break;

			case DIV:
				size = _rebuildDiv(rebuildList, 1, size);
				break;

			case ADD:
				size = _rebuildAdd(rebuildList, 1, size);
				break;

			case BY:
				size = _rebuildBy(rebuildList, 1, size);
				break;

			case MOD:
				size = _rebuildMod(rebuildList, 1, size);
				break;

			default:
				throw new RException("not support: " + op);
			}

			switch (size) {

			// no change
			case -1:
				return null;

			// 0
			case 0:
				return O_INT_0;

			case 1:
				return rebuildList.get(1);

			default:
				return RulpFactory.createExpression(rebuildList.subList(0, size + 1));
			}

		}

	}

	static class BoolUtil {

		private static int _rebuildAnd(List<IRObject> list, int fromIndex, int toIndex) throws RException {

			int size = toIndex - fromIndex;

			// (and true) => true
			// (and false) => false
			// (or true) => true
			// (or false) => false

			if (size < 2) {
				return -1;
			}

			int pos = fromIndex;

			NEXT: for (int i = fromIndex; i < toIndex; ++i) {

				IRObject obj = list.get(i);

				if (obj.getType() == RType.BOOL) {

					// (and a true) ==> (and a)
					if (RulpUtil.asBoolean(obj).asBoolean()) {
						continue NEXT;
					}
					// (and a false) ==> false
					else {
						list.set(fromIndex, O_False);
						return 1;
					}

				} else {

					if (pos != i) {
						list.set(pos, obj);
					}

					pos++;
				}
			}

			if (pos == toIndex) {
				return -1;
			}

			return pos - fromIndex;
		}

		private static int _rebuildOr(List<IRObject> list, int fromIndex, int toIndex) throws RException {

			int size = toIndex - fromIndex;

			// (and true) => true
			// (and false) => false
			// (or true) => true
			// (or false) => false

			if (size < 2) {
				return -1;
			}

			int pos = fromIndex;

			NEXT: for (int i = fromIndex; i < toIndex; ++i) {

				IRObject obj = list.get(i);

				if (obj.getType() == RType.BOOL) {

					// (or a true) ==> true
					if (RulpUtil.asBoolean(obj).asBoolean()) {
						list.set(fromIndex, O_True);
						return 1;
					}
					// (or a false) ==> (or a)
					else {
						continue NEXT;
					}

				} else {

					if (pos != i) {
						list.set(pos, obj);
					}

					pos++;
				}
			}

			if (pos == toIndex) {
				return -1;
			}

			return pos - fromIndex;
		}

		public static IRObject rebuildAnd(List<IRObject> rebuildList) throws RException {

			int size = rebuildList.size();
			size = _rebuildAnd(rebuildList, 1, size);
			int listSize;
			if (size == -1) {
				listSize = rebuildList.size();
			} else {
				listSize = size + 1;
			}

			if (listSize >= 3) {
				int size2 = _rebuildSameElement(rebuildList, 1, listSize, null);
				if (size2 != -1) {
					size = size2;
				}
			}

			switch (size) {

			// no change
			case -1:
				return null;

			// 0
			case 0:
				return O_True;

			case 1:
				return rebuildList.get(1);

			default:
				return RulpFactory.createExpression(rebuildList.subList(0, size + 1));
			}

		}

		public static IRObject rebuildOr(List<IRObject> rebuildList) throws RException {

			int size = rebuildList.size();
			size = _rebuildOr(rebuildList, 1, size);
			int listSize;
			if (size == -1) {
				listSize = rebuildList.size();
			} else {
				listSize = size + 1;
			}

			if (listSize >= 3) {
				int size2 = _rebuildSameElement(rebuildList, 1, listSize, null);
				if (size2 != -1) {
					size = size2;
				}
			}

			switch (size) {

			// no change
			case -1:
				return null;

			// 0
			case 0:
				return O_True;

			case 1:
				return rebuildList.get(1);

			default:
				return RulpFactory.createExpression(rebuildList.subList(0, size + 1));
			}

		}
	}

	static class ERO {

		public IRList inputExpr = null;

		public IRObject outputObj = null;

		public void setInputExpr(IRList inputExpr) {
			this.inputExpr = inputExpr;
			this.outputObj = null;
		}
	}

	static class UniqElement {
		int count = 0;
		IRObject element;
		int exprLevel = 0;
		String uniqName;
	}

	protected static AtomicInteger computeCount = new AtomicInteger(0);

	protected static AtomicInteger rebuildCount = new AtomicInteger(0);

	static int typePriority[] = new int[RType.TYPE_NUM];

	static {
		typePriority[RType.INT.getIndex()] = 0;
		typePriority[RType.LONG.getIndex()] = 1;
		typePriority[RType.FLOAT.getIndex()] = 2;
		typePriority[RType.DOUBLE.getIndex()] = 3;
		typePriority[RType.CONSTANT.getIndex()] = 4;
		typePriority[RType.BOOL.getIndex()] = 5;
		typePriority[RType.STRING.getIndex()] = 6;
		typePriority[RType.NIL.getIndex()] = 7;
		typePriority[RType.ATOM.getIndex()] = 8;
		typePriority[RType.BLOB.getIndex()] = 9;
		typePriority[RType.ARRAY.getIndex()] = 10;
		typePriority[RType.LIST.getIndex()] = 11;
		typePriority[RType.FACTOR.getIndex()] = 12;
		typePriority[RType.MACRO.getIndex()] = 13;
		typePriority[RType.NATIVE.getIndex()] = 14;
		typePriority[RType.EXPR.getIndex()] = 15;
		typePriority[RType.FUNC.getIndex()] = 16;
		typePriority[RType.TEMPLATE.getIndex()] = 17;
		typePriority[RType.VAR.getIndex()] = 18;
		typePriority[RType.CLASS.getIndex()] = 19;
		typePriority[RType.INSTANCE.getIndex()] = 20;
		typePriority[RType.FRAME.getIndex()] = 21;
		typePriority[RType.MEMBER.getIndex()] = 22;
	}

	private static int _expandExpr(List<IRObject> list, int fromIndex, int toIndex, String name) throws RException {

		int endIndex = toIndex;
		int update = 0;

		// expand: (+ a (+ b c)) ==> (+ a b c)
		for (int i = fromIndex; i < toIndex; ++i) {

			IRObject obj = list.get(i);
			if (obj.getType() != RType.EXPR) {
				continue;
			}

			IRExpr expr = (IRExpr) obj;
			if (expr.size() <= 1 || !OptUtil.isFactor(expr.get(0), name)) {
				continue;
			}

			_set(list, i, expr.get(1));

			for (int j = 2; j < expr.size(); ++j) {
				_set(list, endIndex++, expr.get(j));
			}

			++update;
		}

		return update == 0 ? -1 : endIndex;
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

	private static void _incComputeCount() {
		computeCount.getAndIncrement();
	}

	private static boolean _isEROExpr(IRObject e0, IRList expr, IRFrame frame) throws RException {

		if (!OptUtil.isAtomFactor(e0)) {
			return false;
		}

		if (!OptUtil.isConstValue(expr.listIterator(1))) {
			return false;
		}

		return true;
	}

	private static boolean _rebuild(ERO cc0, IRInterpreter interpreter, IRFrame frame) throws RException {

		IRList expr = cc0.inputExpr;

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
				rebuildObj = ArithmeticUtil.rebuild(rebuildList, RArithmeticOperator.BY);
				break;

			case F_O_ADD:
				rebuildObj = ArithmeticUtil.rebuild(rebuildList, RArithmeticOperator.ADD);
				break;

			case F_O_SUB:
				rebuildObj = ArithmeticUtil.rebuild(rebuildList, RArithmeticOperator.SUB);
				break;

			case F_O_POWER:
				rebuildObj = ArithmeticUtil.rebuild(rebuildList, RArithmeticOperator.POWER);
				break;

			case F_O_DIV:
				rebuildObj = ArithmeticUtil.rebuild(rebuildList, RArithmeticOperator.DIV);
				break;

			case F_O_MOD:
				rebuildObj = ArithmeticUtil.rebuild(rebuildList, RArithmeticOperator.MOD);
				break;

			case F_B_AND:
				rebuildObj = BoolUtil.rebuildAnd(rebuildList);
				break;

			case F_B_OR:
				rebuildObj = BoolUtil.rebuildOr(rebuildList);
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

	private static IRObject _rebuildLoop(IRObject e0, IRList expr, List<IRObject> rebuildList) throws RException {

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

	private static int _rebuildSameElement(List<IRObject> list, int fromIndex, int toIndex, RArithmeticOperator op)
			throws RException {

		int size = toIndex - fromIndex;
		if (size < 2) {
			return -1;
		}

		ArrayList<UniqElement> uniqList = new ArrayList<>();
		Map<String, UniqElement> uniqMap = new HashMap<>();

		AtomicInteger update = new AtomicInteger(0);

		for (int i = fromIndex; i < toIndex; ++i) {

			IRObject obj = list.get(i);
			String uniqName = _toUniqString(list.get(i));

			UniqElement uniqElement = uniqMap.get(uniqName);
			if (uniqElement == null) {
				uniqElement = new UniqElement();
				uniqElement.element = obj;
				uniqElement.count = 1;
				uniqElement.uniqName = uniqName;
				uniqElement.exprLevel = OptUtil.getExprLevel(obj);

				uniqMap.put(uniqName, uniqElement);
				uniqList.add(uniqElement);

			} else {

				uniqElement.count++;
				update.incrementAndGet();
			}
		}

		if (update.get() > 0) {
			for (UniqElement e : uniqList) {
				IRObject obj = e.element;
				if (op != null && e.count > 1) {
					switch (op) {
					case POWER:
						obj = RulpFactory.createExpression(O_POWER, obj, RulpFactory.createInteger(e.count));
						break;

					case BY:
						obj = RulpFactory.createExpression(O_BY, RulpFactory.createInteger(e.count), obj);
						break;

					default:
						throw new RException("not support: " + op);
					}
				}
				e.element = obj;
			}
		}

		Collections.sort(uniqList, (e1, e2) -> {

			int d = getTypePriority(e1.element.getType()) - getTypePriority(e2.element.getType());
			if (d == 0) {
				d = e1.exprLevel - e2.exprLevel;
			}

			if (d == 0) {
				d = e1.uniqName.compareTo(e2.uniqName);
			}

			if (d < 0) {
				update.incrementAndGet();
			}

			return d;
		});

		if (update.get() == 0) {
			return -1;
		}

		int pos = fromIndex;
		for (UniqElement e : uniqList) {
			list.set(pos++, e.element);
		}

		return pos - fromIndex;
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

	static <T> void _set(List<T> list, int index, T obj) {

		if (index >= list.size()) {
			for (int i = list.size(); i <= index; ++i) {
				list.add(null);
			}
		}

		list.set(index, obj);
	}

	private static String _toUniqString(IRObject obj) throws RException {
		return RulpUtil.toString(obj);
	}

	public static int getComputeCount() {
		return computeCount.get();
	}

	public static int getRebuildCount() {
		return rebuildCount.get();
	}

	private static int getTypePriority(RType type) {
		return typePriority[type.getIndex()];
	}

	// (Op A1 A2 ... Ak), Op is CC0 factor, Ak is const value and return const value
	public static IRObject rebuild(IRList expr, IRInterpreter interpreter, IRFrame frame) throws RException {

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
