package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.F_BREAK;
import static alpha.rulp.lang.Constant.F_B_AND;
import static alpha.rulp.lang.Constant.F_B_OR;
import static alpha.rulp.lang.Constant.F_CASE;
import static alpha.rulp.lang.Constant.F_DEFUN;
import static alpha.rulp.lang.Constant.F_DEFVAR;
import static alpha.rulp.lang.Constant.F_DEF_CONST;
import static alpha.rulp.lang.Constant.F_IF;
import static alpha.rulp.lang.Constant.F_LOOP;
import static alpha.rulp.lang.Constant.F_O_ADD;
import static alpha.rulp.lang.Constant.F_O_BY;
import static alpha.rulp.lang.Constant.F_O_DIV;
import static alpha.rulp.lang.Constant.F_O_EQ;
import static alpha.rulp.lang.Constant.F_O_GE;
import static alpha.rulp.lang.Constant.F_O_GT;
import static alpha.rulp.lang.Constant.F_O_LE;
import static alpha.rulp.lang.Constant.F_O_LT;
import static alpha.rulp.lang.Constant.F_O_MOD;
import static alpha.rulp.lang.Constant.F_O_NE;
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

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
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
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.control.XRFactorCase;
import alpha.rulp.ximpl.control.XRFactorLoop;

public class EROUtil {

	static class ArithmeticUtil {

		private static int _rebuildAdd(List<IRObject> list, int fromIndex, int toIndex) throws RException {

			int update = 0;

			// expand: (+ a (+ b c)) ==> (+ a b c)
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

			// (+ a (* 2 a)) ==> (+ 0 (* 3 a))
			{
				if (size == -1) {
					size = toIndex - fromIndex;
				}

				NEXT_ATOM: for (int i = 0; i < (size - 1); ++i) {

					IRObject ex = list.get(fromIndex + i);
					if (!RulpUtil.isAtom(ex)) {
						continue;
					}

					String atomName = RulpUtil.asAtom(ex).getName();
					for (int j = i + 1; i < size; ++i) {

						IRObject ey = list.get(fromIndex + j);
						if (!RulpUtil.isExpr(ey, F_O_BY)) {
							continue;
						}

						// (* 3 a)
						IRExpr byExpr = (IRExpr) ey;
						if (byExpr.size() != 3 || !OptUtil.isConstNumber(byExpr.get(1))
								|| !RulpUtil.isAtom(byExpr.get(2), atomName)) {
							continue;
						}

						IRExpr newExpr = RulpFactory.createExpression(byExpr.get(0),
								MathUtil.computeArithmeticExpression(RArithmeticOperator.ADD, byExpr.get(1),
										RulpFactory.createInteger(1)),
								byExpr.get(2));

						list.set(fromIndex + i, RulpFactory.createInteger(0));
						list.set(fromIndex + j, newExpr);
						update++;
						continue NEXT_ATOM;
					}

				} // NEXT_ATOM
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

			// expand: (* a (* b c)) ==> (* a b c)
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

			// (* a (power a 2)) ==> (* 1 (power a 3))
			{
				if (size == -1) {
					size = toIndex - fromIndex;
				}

				NEXT_ATOM: for (int i = 0; i < (size - 1); ++i) {

					IRObject ex = list.get(fromIndex + i);
					if (!RulpUtil.isAtom(ex)) {
						continue;
					}

					String atomName = RulpUtil.asAtom(ex).getName();
					for (int j = i + 1; i < size; ++i) {

						IRObject ey = list.get(fromIndex + j);
						if (!RulpUtil.isExpr(ey, F_O_POWER)) {
							continue;
						}

						// (power a 2)
						IRExpr powerExpr = (IRExpr) ey;
						if (powerExpr.size() != 3 || !RulpUtil.isAtom(powerExpr.get(1), atomName)
								|| !OptUtil.isConstNumber(powerExpr.get(2))) {
							continue;
						}

						IRExpr newExpr = RulpFactory.createExpression(powerExpr.get(0), powerExpr.get(1),
								MathUtil.computeArithmeticExpression(RArithmeticOperator.ADD, powerExpr.get(2),
										RulpFactory.createInteger(1)));

						list.set(fromIndex + i, RulpFactory.createInteger(1));
						list.set(fromIndex + j, newExpr);
						update++;
						continue NEXT_ATOM;
					}
				} // NEXT_ATOM
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

			// (- (+ x c1 c2) c3 x c4) == > (- (+ 0 c1 c2) c3 0 c4)
			if (size > 1) {

				IRObject e0 = list.get(fromIndex);

				if (RulpUtil.isExpr(e0, F_O_ADD)) {

					IRExpr e0Expr = (IRExpr) e0;
					ArrayList<String> e0UniqList = new ArrayList<>();
					int e0Size = e0Expr.size() - 1;
					int update = 0;
					FIND_MATCH: for (int i = fromIndex + 1; i < toIndex; ++i) {

						IRObject ey = list.get(i);
						String eyUniq = _toUniqString(ey);

						for (int j = 0; j < e0Size; ++j) {

							String exUniq = null;
							if (j < e0UniqList.size()) {
								exUniq = e0UniqList.get(j);
							} else {
								exUniq = _toUniqString(e0Expr.get(j + 1));
								e0UniqList.add(exUniq);
							}

							// exUniq is null means it was reduced before
							if (exUniq != null && exUniq.equals(eyUniq)) {
								list.set(i, O_INT_0);
								e0UniqList.set(j, null);
								update++;
								continue FIND_MATCH;
							}
						}
					}

					if (update > 0) {

						int nullCount = 0;
						for (String uniq : e0UniqList) {
							if (uniq == null) {
								nullCount++;
							}
						}

						if (e0Size == nullCount) {
							list.set(fromIndex, O_INT_0);

						} else if ((nullCount + 1) == e0Size) {

							IRObject leftObj = null;
							for (int j = 0; j < e0Size; ++j) {
								if (j < e0UniqList.size() && e0UniqList.get(j) == null) {
									continue;
								}

								leftObj = e0Expr.get(j + 1);
								break;
							}
							list.set(fromIndex, leftObj);

						} else {

							ArrayList<IRObject> newList = new ArrayList<>();
							newList.add(e0Expr.get(0));
							for (int j = 0; j < e0Size; ++j) {
								if (j < e0UniqList.size() && e0UniqList.get(j) == null) {
									continue;
								}
								newList.add(e0Expr.get(j + 1));
								break;
							}

							list.set(fromIndex, RulpFactory.createExpression(newList));
						}
					}
				}
			}

			// (- a b a) ==> (- 0 b)
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

			switch (op) {
			case SUB:
				size = _rebuildSub(rebuildList, 1, size);
				break;

			case POWER:

				// (power a) ==> a
				if (size == 2) {
					return rebuildList.get(1);
				}

				size = _rebuildPower(rebuildList, 1, size);
				break;

			case DIV:
				size = _rebuildDiv(rebuildList, 1, size);
				break;

			case ADD:
				if (_isTypeOf(rebuildList, 1, size, RType.STRING)) {
					return null;
				}
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

	static class FakeFrame {

		private Map<String, IRObject> constValueMap = null;

		private FakeFrame parent = null;

		public FakeFrame() {
			super();
			constValueMap = new HashMap<>();
		}

		public FakeFrame(FakeFrame parent) {
			super();
			this.parent = parent;
		}

		private Map<String, IRObject> _getConstMap() {

			if (constValueMap == null) {
				constValueMap = new HashMap<>();
				if (parent != null) {
					constValueMap.putAll(parent._getConstMap());
				}
			}

			return constValueMap;
		}

		public void addConst(String name, IRObject value) {
			_getConstMap().put(name, value);
		}

		public IRObject getConstValue(String name) {
			return _getConstMap().get(name);
		}

		public FakeFrame newBranch() {
			return new FakeFrame(this);
		}
	}

	static class RelationalUtil {

		static boolean isSameOperand(List<IRObject> rebuildList) throws RException {
			return rebuildList.size() == 3
					&& _toUniqString(rebuildList.get(1)).equals(_toUniqString(rebuildList.get(2)));
		}

		public static IRObject rebuildBigger(List<IRObject> rebuildList) throws RException {

			// (> a a)
			if (isSameOperand(rebuildList)) {
				return RulpFactory.createBoolean(false);
			}

			return null;
		}

		public static IRObject rebuildBiggerOrEqual(List<IRObject> rebuildList) throws RException {

			// (>= a a)
			if (isSameOperand(rebuildList)) {
				return RulpFactory.createBoolean(true);
			}

			return null;
		}

		public static IRObject rebuildEqual(List<IRObject> rebuildList) throws RException {

			// (= a a)
			if (isSameOperand(rebuildList)) {
				return RulpFactory.createBoolean(true);
			}

			return null;
		}

		public static IRObject rebuildNotEqual(List<IRObject> rebuildList) throws RException {

			// (!= a a)
			if (isSameOperand(rebuildList)) {
				return RulpFactory.createBoolean(false);
			}

			return null;
		}

		public static IRObject rebuildSmaller(List<IRObject> rebuildList) throws RException {

			// (< a a)
			if (isSameOperand(rebuildList)) {
				return RulpFactory.createBoolean(false);
			}

			return null;
		}

		public static IRObject rebuildSmallerOrEqual(List<IRObject> rebuildList) throws RException {

			// (<= a a)
			if (isSameOperand(rebuildList)) {
				return RulpFactory.createBoolean(true);
			}

			return null;
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

	static IRObject _computeContant(IRObject obj, IRInterpreter interpreter, IRFrame frame) throws RException {

		IRObject value = obj;

		if (obj.getType() == RType.ATOM) {
			value = RulpUtil.lookup(value, interpreter, frame);
		}

		if (value.getType() != RType.CONSTANT) {
			return obj;
		}

		return RulpUtil.asConstant(value).getValue();
	}

	private static IRObject _expandDoExpr(IRObject obj) throws RException {

		if (obj == null || !RulpUtil.isExpr(obj, A_DO)) {
			return obj;
		}

		IRExpr expr = (IRExpr) obj;
		int size = expr.size();

		ArrayList<IRObject> newList = null;
		int remove = 0;

		for (int i = 1; i < size; ++i) {

			IRObject ex = expr.get(i);
			if (ex == null || RulpUtil.isEmptyExpr(ex)) {
				++remove;
				continue;
			}

			IRObject ey = _expandDoExpr(ex);
			if (ex == ey) {
				if (newList != null) {
					newList.add(ex);
				}
				continue;
			}

			if (newList == null) {
				newList = new ArrayList<>();
				for (int j = 0; j < i; ++j) {
					newList.add(expr.get(j));
				}
			}

			if (RulpUtil.isExpr(ey, A_DO)) {
				RulpUtil.addAll(newList, RulpUtil.asExpression(ey).listIterator(1));
			} else {
				newList.add(ey);
			}
		}

		if (newList == null) {

			// no valid expr
			if (remove > 0) {
				return OptUtil.asExpr(null);
			}

			return obj;
		}

		return RulpFactory.createExpression(newList);
	}

	private static int _expandExpr(List<IRObject> list, int fromIndex, int toIndex, String name) throws RException {

		int endIndex = toIndex;
		int update = 0;

		for (int i = fromIndex; i < toIndex; ++i) {

			IRObject obj = list.get(i);
			if (obj.getType() != RType.EXPR) {
				continue;
			}

			IRExpr expr = (IRExpr) obj;
			if (expr.size() <= 1 || !RulpUtil.isFactor(expr.get(0), name)) {
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

	private static int _getTypePriority(RType type) {
		return typePriority[type.getIndex()];
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

	private static boolean _isTypeOf(List<IRObject> list, int fromIndex, int toIndex, RType... types) {

		NEXT: for (int i = fromIndex; i < toIndex; ++i) {

			IRObject obj = list.get(i);
			RType type = obj.getType();
			for (RType t : types) {
				if (t == type) {
					continue NEXT;
				}
			}

			return false;
		}

		return true;
	}

	private static boolean _rebuild(ERO cc0, IRInterpreter interpreter, IRFrame frame) throws RException {

		IRList expr = cc0.inputExpr;

		if (expr.isEmpty()) {
			return true;
		}

		IRObject e0 = EROUtil.lookup(expr.get(0), interpreter, frame);
		if (_isEROExpr(e0, expr, frame)) {
			return true;
		}

		int size = expr.size();
		List<IRObject> rebuildList = new ArrayList<>();
		ERO childCC0 = new ERO();

		int rebuildCount = 0;

		for (int i = 0; i < size; ++i) {

			IRObject ex = i == 0 ? e0 : expr.get(i);

			if (ex.getType() == RType.EXPR) {

				childCC0.setInputExpr((IRExpr) ex);

				if (_rebuild(childCC0, interpreter, frame) || childCC0.outputObj != null) {
					rebuildList.add(childCC0.outputObj);
					rebuildCount++;
				} else {
					rebuildList.add(ex);
				}

			} else {

				if (i == 0 && OptUtil.isAtomFactor(ex)) {
					rebuildCount++;

				} else if (OptUtil.isConstValue(ex)) {
					rebuildCount++;

				} else {

					IRObject value = _valueOf(ex, frame);
					if (value != null) {
						ex = value;
						rebuildCount++;
					}
				}

				rebuildList.add(ex);
			}

		}

		// part rebuild
		if (rebuildCount > 0) {
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

			// (do () (b action))
			case F_DEFUN:
				rebuildObj = _rebuildDefun(rebuildList);
				break;

			// (loop for x from 1 to 3 do ..
			// (loop a)
			case F_LOOP:
				rebuildObj = _rebuildLoop(e0, expr, rebuildList, interpreter, frame);
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

			case F_O_EQ:
				rebuildObj = RelationalUtil.rebuildEqual(rebuildList);
				break;

			case F_O_NE:
				rebuildObj = RelationalUtil.rebuildNotEqual(rebuildList);
				break;

			case F_O_GT:
				rebuildObj = RelationalUtil.rebuildBigger(rebuildList);
				break;

			case F_O_LT:
				rebuildObj = RelationalUtil.rebuildSmaller(rebuildList);
				break;

			case F_O_GE:
				rebuildObj = RelationalUtil.rebuildBiggerOrEqual(rebuildList);
				break;

			case F_O_LE:
				rebuildObj = RelationalUtil.rebuildSmallerOrEqual(rebuildList);
				break;

			default:
			}

			if (rebuildObj != null) {
				cc0.outputObj = rebuildObj;
				_incComputeCount();
				return false;
			}
		}

		if (rebuildCount > 0) {
			cc0.outputObj = RulpFactory.createExpression(rebuildList);
		}

		return false;
	}

	// (Op A1 A2 ... Ak), Op is CC0 factor, Ak is const value and return const value
	private static IRObject _rebuild(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {

		ERO ero = new ERO();
		ero.setInputExpr(expr);

		if (!_rebuild(ero, interpreter, frame)) {
			return ero.outputObj == null ? expr : ero.outputObj;
		}

		if (ero.outputObj != null) {
			return ero.outputObj;
		}

		IRObject rst = interpreter.compute(frame, expr);
		_incComputeCount();
		return rst;
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

	private static IRObject _rebuildDefun(List<IRObject> rebuildList) throws RException {

		IRExpr funBody = null;
		if (rebuildList.size() == 4) {
			funBody = RulpUtil.asExpression(rebuildList.get(3));

		} else if (rebuildList.size() > 4) {
			funBody = RulpUtil.toDoExpr(rebuildList.listIterator(3));

		} else {
			return null;
		}

		return _rebuildFuncBody(funBody);
	}

	// (do () (b action))
	private static IRExpr _rebuildDo(List<IRObject> rebuildList) throws RException {

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

	private static IRExpr _rebuildFuncBody(IRExpr expr) throws RException {
		return _removeExprAfterReturn(RulpUtil.asExpression(_expandDoExpr(expr)));
	}

	private static IRObject _rebuildFuncBody(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {

		IRExpr newBody = _rebuildFuncBody(expr);
		if (newBody != null) {
			expr = newBody;
			_incComputeCount();
		}

		ERO ero = new ERO();
		ero.setInputExpr(expr);

		if (!_rebuild(ero, interpreter, frame)) {
			return ero.outputObj == null ? expr : ero.outputObj;
		}

		if (ero.outputObj != null) {
			return ero.outputObj;
		}

		IRObject rst = interpreter.compute(frame, expr);
		_incComputeCount();
		return rst;
	}

	// (if true A B) or (if false A B)
	private static IRObject _rebuildIf(List<IRObject> rebuildList) throws RException {

		int size = rebuildList.size();
		if (size < 3) {
			return null;
		}

		// (if true ...) or (if false ...)
		IRObject e1 = rebuildList.get(1);
		if (e1.getType() == RType.BOOL) {

			if (RulpUtil.asBoolean(e1).asBoolean()) {

				// (if true A)
				if (rebuildList.size() == 3) {
					return OptUtil.asExpr(rebuildList.get(2));
				}

				// (if true A B)
				if (rebuildList.size() == 4 && !RulpUtil.isAtom(rebuildList.get(2), A_DO)) {

					return OptUtil.asExpr(rebuildList.get(2));
				}

				// (if true do expr1 expr2 expr3 ...)
				if (rebuildList.size() >= 4 && RulpUtil.isAtom(rebuildList.get(2), A_DO)) {

					if (rebuildList.size() == 4) {
						return OptUtil.asExpr(rebuildList.get(3));
					}

					// remove empty or useless expression
					return RulpUtil.toDoExpr(rebuildList.subList(3, rebuildList.size()));
				}

			} else {

				// (if false A)
				if (rebuildList.size() == 3) {
					return OptUtil.asExpr(null);
				}

				// (if false A B)
				if (rebuildList.size() == 4 && !RulpUtil.isAtom(rebuildList.get(2), A_DO)) {
					return OptUtil.asExpr(rebuildList.get(3));
				}

				// (if false do expr1 expr2 expr3 ...)
				if (rebuildList.size() >= 4 && RulpUtil.isAtom(rebuildList.get(2), A_DO)) {
					return OptUtil.asExpr(null);
				}

			}

		} else {

			// (if (condition) do expr1 expr2 expr3 ...)
//			if (rebuildList.size() >= 4 && RulpUtil.isAtom(rebuildList.get(2), A_DO)) {
//
//			}
		}

		// (if condition same-expr same-expr)
		if (rebuildList.size() == 4 && !RulpUtil.isAtom(rebuildList.get(2), A_DO)
				&& _toUniqString(rebuildList.get(2)).equals(_toUniqString(rebuildList.get(3)))) {
			return rebuildList.get(2);
		}

		// (if condition do expr1 expr2 expr3 ...)
		if (rebuildList.size() >= 4 && RulpUtil.isAtom(rebuildList.get(2), A_DO)) {

			IRObject doExprObj = RulpUtil.toDoExpr(rebuildList.listIterator(3));
			if (RulpUtil.isEmptyExpr(doExprObj)) {
				return OptUtil.asExpr(null);
			}

			IRObject newObj = _removeMultiReturn(doExprObj);
			if (newObj != null) {
				return RulpFactory.createExpression(rebuildList.get(0), rebuildList.get(1), rebuildList.get(2), newObj);
			}
		}

		return null;
	}

	private static IRObject _rebuildLoop(IRObject e0, IRList expr, List<IRObject> rebuildList,
			IRInterpreter interpreter, IRFrame frame) throws RException {

		int size = rebuildList.size();

		// (loop for x from 1 to 3 do ...
		if (XRFactorLoop.isLoop2(expr)) {

			IRObject fromObj = _computeContant(XRFactorLoop.getLoop2FromObject(expr), interpreter, frame);
			IRObject toObj = _computeContant(XRFactorLoop.getLoop2ToObject(expr), interpreter, frame);

			if (fromObj.getType() == RType.INT && toObj.getType() == RType.INT) {

				int fromIndex = RulpUtil.asInteger(fromObj).asInteger();
				int toIndex = RulpUtil.asInteger(toObj).asInteger();

				// from 3 to 1 ==> empty expr
				if (fromIndex > toIndex) {
					return OptUtil.asExpr(null);
				}

				// from 1 to 1 ==> (do action)
				if (fromIndex == toIndex) {

					List<IRObject> doActions = new ArrayList<>();
					RulpUtil.addAll(doActions, XRFactorLoop.getLoop2DoList(expr));

					int pos = _removeEmptyExpr(doActions, 0);
					if (pos == 0) {
						return OptUtil.asExpr(null);
					}

					if (pos < doActions.size()) {
						doActions = doActions.subList(0, pos);
					}

					IRExpr doExpr = RulpUtil.toDoExpr(doActions);
					Map<String, IRObject> replaceMap = new HashMap<>();
					replaceMap.put(RulpUtil.asAtom(expr.get(2)).getName(), fromObj);

					return RuntimeUtil.rebuild(doExpr, replaceMap);
				}

				IRObject doExprObj = rebuild(RulpUtil.toDoExpr(XRFactorLoop.getLoop2DoList(expr)), interpreter, frame);
				if (RulpUtil.isEmptyExpr(doExprObj)) {
					return OptUtil.asExpr(null);
				}

				IRObject newObj = _removeMultiReturn(doExprObj);
				if (newObj != null) {
					Map<String, IRObject> replaceMap = new HashMap<>();
					String indexName = RulpUtil.asAtom(XRFactorLoop.getLoop2IndexObject(expr)).getName();
					replaceMap.put(indexName, fromObj);
					return RuntimeUtil.rebuild(newObj, replaceMap);
				}
			}
		}

		// Check infinite loop: (loop a)
		if (XRFactorLoop.isLoop3(expr)) {

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

			IRObject doExprObj = rebuild(RulpUtil.toDoExpr(XRFactorLoop.getLoop3DoList(expr)), interpreter, frame);
			if (RulpUtil.isEmptyExpr(doExprObj)) {
				return OptUtil.asExpr(null);
			}

			IRObject newObj = _removeMultiReturn(doExprObj);
			if (newObj != null) {
				return newObj;
			}
		}

		// (loop for x from 3 to 1 by 1 do ...
		if (XRFactorLoop.isLoop4(expr)) {

			IRObject fromObj = _computeContant(XRFactorLoop.getLoop4FromObject(expr), interpreter, frame);
			IRObject toObj = _computeContant(XRFactorLoop.getLoop4ToObject(expr), interpreter, frame);
			IRObject byObj = _computeContant(XRFactorLoop.getLoop4ByObject(expr), interpreter, frame);

			if (fromObj.getType() == RType.INT && toObj.getType() == RType.INT && byObj.getType() == RType.INT) {

				int fromIndex = RulpUtil.asInteger(fromObj).asInteger();
				int toIndex = RulpUtil.asInteger(toObj).asInteger();

				// from 1 to 1 ==> (do action)
				if (fromIndex == toIndex) {

					List<IRObject> doActions = new ArrayList<>();
					RulpUtil.addAll(doActions, XRFactorLoop.getLoop4DoList(expr));

					int pos = _removeEmptyExpr(doActions, 0);
					if (pos == 0) {
						return OptUtil.asExpr(null);
					}

					if (pos < doActions.size()) {
						doActions = doActions.subList(0, pos);
					}

					IRExpr doExpr = RulpUtil.toDoExpr(doActions);
					Map<String, IRObject> replaceMap = new HashMap<>();
					replaceMap.put(RulpUtil.asAtom(expr.get(2)).getName(), fromObj);

					return RuntimeUtil.rebuild(doExpr, replaceMap);
				}

				int byValue = RulpUtil.asInteger(byObj).asInteger();

				// from 3 to 1 by 1 ==> empty expr
				if (fromIndex > toIndex && byValue > 0) {
					return OptUtil.asExpr(null);
				}

				// from 1 to 3 by -1 ==> empty expr
				if (fromIndex < toIndex && byValue < 0) {
					return OptUtil.asExpr(null);
				}
			}
		}

		return null;
	}

	private static IRExpr _rebuildPrecompute(IRExpr expr) throws RException {
		return (IRExpr) _rebuildPrecompute(expr, new FakeFrame());
	}

	private static IRObject _rebuildPrecompute(IRObject obj, FakeFrame frame) throws RException {

		if (obj == null) {
			return obj;
		}

		IRObject _v;

		switch (obj.getType()) {
		case ATOM:
			_v = frame.getConstValue(RulpUtil.asAtom(obj).getName());
			if (_v != null) {
				return _v;
			}
			break;

		case CONSTANT:
			_v = frame.getConstValue(RulpUtil.asConstant(obj).getName());
			if (_v != null) {
				return _v;
			}
			break;

		case EXPR:

			IRExpr expr = (IRExpr) obj;
			if (expr.size() <= 1) {
				return obj;
			}

			IRObject e0 = expr.get(0);
			int fromIndex = 0;
			int size = expr.size();

//			boolean tryExpand = true;
//			String 

			if (e0.getType() == RType.ATOM || e0.getType() == RType.FACTOR) {

				fromIndex = 1;

				switch (e0.asString()) {
				case F_DEF_CONST:
					if (size == 3 && expr.get(1).getType() == RType.ATOM && OptUtil.isConstValue(expr.get(2))) {
						frame.addConst(RulpUtil.asAtom(expr.get(1)).getName(), expr.get(2));
						return OptUtil.asExpr(null);
					}
					break;

				case F_DEFVAR:
				case F_DEFUN:
					return obj;

//				case F_O_ADD:
//				case F_O_SUB:

				default:

					if (OptUtil.isNewFrameFactor(e0)) {
						frame = frame.newBranch();
					}
				}
			}

			ArrayList<IRObject> newElements = null;
			for (; fromIndex < size; ++fromIndex) {

				IRObject ex = expr.get(fromIndex);
				IRObject ey = _rebuildPrecompute(ex, frame);

				if (ey != ex) {
					if (newElements == null) {
						newElements = new ArrayList<>();
						for (int i = 0; i < fromIndex; ++i) {
							newElements.add(expr.get(i));
						}
					}
					newElements.add(ey);
				} else {
					if (newElements != null) {
						newElements.add(ex);
					}
				}
			}

			if (newElements != null) {
				return RulpFactory.createExpression(newElements);
			}

			break;

		default:
		}

		return obj;
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

			int d = _getTypePriority(e1.element.getType()) - _getTypePriority(e2.element.getType());
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
		int pos = fromIndex;

		for (int i = fromIndex; i < size; ++i) {

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

	private static IRExpr _removeExprAfterReturn(IRExpr expr) throws RException {

		int size = expr.size();
		if (RulpUtil.isExpr(expr, A_DO)) {

			int rtIdx = -1;

			for (int i = 1; i < size; ++i) {
				if (RulpUtil.isExpr(expr.get(i), F_RETURN)) {
					rtIdx = i;
					break;
				}
			}

			if (rtIdx != -1 && (rtIdx + 1) < size) {

				ArrayList<IRObject> doList = new ArrayList<>();
				for (int i = 1; i <= rtIdx; ++i) {
					doList.add(expr.get(i));
				}

				return RulpUtil.toDoExpr(doList);
			}
		}

		return null;
	}

	private static IRObject _removeMultiReturn(IRObject obj) throws RException {

		if (obj.getType() != RType.EXPR) {
			return null;
		}

		if (RulpUtil.isExpr(obj, F_RETURN)) {
			return obj;
		}

		obj = _expandDoExpr(obj);
		if (!RulpUtil.isExpr(obj, A_DO)) {
			return null;
		}

		int returnIndex = -1;
		IRExpr doExpr = RulpUtil.asExpression(obj);
		NEXT_STMT: for (int i = 1; i < doExpr.size(); ++i) {
			if (RulpUtil.isExpr(doExpr.get(i), F_RETURN)) {
				returnIndex = i;
				break NEXT_STMT;
			}
		}

		switch (returnIndex) {
		case -1:
			return null;

		case 1:
			return (IRExpr) doExpr.get(1);

		default:

			List<IRObject> doStmts = new ArrayList<>();
			for (int i = 1; i <= returnIndex; ++i) {
				doStmts.add(doExpr.get(i));
			}

			return RulpUtil.toDoExpr(doStmts);
		}
	}

	private static <T> void _set(List<T> list, int index, T obj) {

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

	private static IRObject _valueOf(IRObject obj, IRFrame frame) throws RException {

		if (obj != null) {

			switch (obj.getType()) {
			case ATOM:
				IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, ((IRAtom) obj).getName());
				if (entry == null) {
					return null;
				}

				IRObject val = entry.getObject();
				if (val == obj || val.getType() == RType.ATOM) {
					return null;
				}

				return _valueOf(val, frame);

			case CONSTANT:
				return RulpUtil.asConstant(obj).getValue();

			default:

			}

		}

		return null;
	}

	public static int getComputeCount() {
		return computeCount.get();
	}

	public static int getRebuildCount() {
		return rebuildCount.get();
	}

	public static IRObject lookup(IRObject obj, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (obj.getType() == RType.ATOM) {
			obj = RulpUtil.lookup(obj, interpreter, frame);
		}

		return obj;
	}

	public static IRObject rebuild(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {

		rebuildCount.getAndIncrement();

		expr = _rebuildPrecompute(expr);

		IRObject rst = _rebuild(expr, interpreter, frame);
		if (rst != expr && rst.getType() == RType.EXPR) {
			rst = _rebuild((IRExpr) rst, interpreter, frame);
		}

		return rst;
	}

	public static IRObject rebuildFuncBody(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {

		rebuildCount.getAndIncrement();

		expr = _rebuildPrecompute(expr);

		IRObject rst = _rebuildFuncBody(expr, interpreter, frame);
		if (rst != expr && rst.getType() == RType.EXPR) {
			rst = _rebuildFuncBody((IRExpr) rst, interpreter, frame);
		}

		return rst;
	}

	public static void reset() {

		computeCount.set(0);
		rebuildCount.set(0);
	}
}
