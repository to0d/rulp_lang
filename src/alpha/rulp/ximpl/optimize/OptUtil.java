package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.A_ATOM;
import static alpha.rulp.lang.Constant.A_DO;
import static alpha.rulp.lang.Constant.F_CPS;
import static alpha.rulp.lang.Constant.F_FOREACH;
import static alpha.rulp.lang.Constant.F_IF;
import static alpha.rulp.lang.Constant.F_LET;
import static alpha.rulp.lang.Constant.F_LOOP;
import static alpha.rulp.lang.Constant.F_OPT;
import static alpha.rulp.lang.Constant.F_RETURN;
import static alpha.rulp.lang.Constant.F_TRY;
import static alpha.rulp.lang.Constant.O_COMPUTE;
import static alpha.rulp.lang.Constant.O_DOUBLE_0;
import static alpha.rulp.lang.Constant.O_DOUBLE_1;
import static alpha.rulp.lang.Constant.O_FLOAT_0;
import static alpha.rulp.lang.Constant.O_FLOAT_1;
import static alpha.rulp.lang.Constant.O_INT_0;
import static alpha.rulp.lang.Constant.O_INT_1;
import static alpha.rulp.lang.Constant.O_LONG_0;
import static alpha.rulp.lang.Constant.O_LONG_1;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRConst;
import alpha.rulp.lang.IRDouble;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFloat;
import alpha.rulp.lang.IRInteger;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRLong;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.AttrUtil;
import alpha.rulp.utils.RulpFactory;

public class OptUtil {

	protected static AtomicInteger OptFactorCount = new AtomicInteger(0);

	public static IRExpr asExpr(IRObject obj) throws RException {

		if (obj == null) {
			return RulpFactory.createExpression();
		}

		if (obj.getType() == RType.EXPR) {
			return (IRExpr) obj;
		} else {
			return RulpFactory.createExpression(O_COMPUTE, obj);
		}
	}

	public static int getExprLevel(IRObject obj) throws RException {

		switch (obj.getType()) {
		case LIST:
		case EXPR:

			int max_level = 0;
			IRIterator<? extends IRObject> it = ((IRList) obj).iterator();
			while (it.hasNext()) {

				int level = getExprLevel(it.next());
				if (max_level < level) {
					max_level = level;
				}
			}

			return obj.getType() == RType.LIST ? max_level : (max_level + 1);

		default:
			return 0;
		}
	}

	public static int getNextOptFactorId() {
		return OptFactorCount.getAndIncrement();
	}

	public static IRObject getNumber0(RType type) throws RException {

		switch (type) {
		case INT:
			return O_INT_0;

		case FLOAT:
			return O_FLOAT_0;

		case DOUBLE:
			return O_DOUBLE_0;

		case LONG:
			return O_LONG_0;

		default:
			throw new RException("invalid type: " + type);
		}
	}

	public static IRObject getNumber1(RType type) throws RException {

		switch (type) {
		case INT:
			return O_INT_1;

		case FLOAT:
			return O_FLOAT_1;

		case DOUBLE:
			return O_DOUBLE_1;

		case LONG:
			return O_LONG_1;

		default:
			throw new RException("invalid type: " + type);
		}
	}

	public static boolean isAtomFactor(IRObject obj) throws RException {

		if (obj.getType() != RType.FACTOR) {
			return false;
		}

		if (!AttrUtil.containAttribute(obj, A_ATOM)) {
			return false;
		}

		return true;
	}

	public static boolean isConstNumber(IRObject obj) throws RException {

		switch (obj.getType()) {
		case INT:
		case FLOAT:
		case DOUBLE:
		case LONG:
			return true;

		case CONSTANT:
			return isConstNumber(((IRConst) obj).getValue());

		default:
			return false;
		}
	}

	public static boolean isConstNumber(IRObject obj, double value) throws RException {

		switch (obj.getType()) {
		case INT:
			return ((IRInteger) obj).asInteger() == value;

		case FLOAT:
			return ((IRFloat) obj).asFloat() == value;

		case DOUBLE:
			return ((IRDouble) obj).asDouble() == value;

		case LONG:
			return ((IRLong) obj).asLong() == value;

		case CONSTANT:
			return isConstNumber(((IRConst) obj).getValue(), value);

		default:
			return false;
		}
	}

	public static boolean isConstValue(IRIterator<? extends IRObject> it) throws RException {

		while (it.hasNext()) {
			if (!isConstValue(it.next())) {
				return false;
			}
		}

		return true;
	}

	public static boolean isConstValue(IRObject obj) throws RException {

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
			return isConstValue(((IRList) obj).iterator());

		default:
			return false;
		}
	}

	public static boolean isLocalValue(IRObject obj, NameSet nameSet) throws RException {

		if (OptUtil.isConstValue(obj)) {
			return true;
		}

		if (obj.getType() == RType.ATOM && nameSet.lookupType(obj.asString()) != null) {
			return true;
		}

		return false;
	}

	public static boolean isNewFrameFactor(IRObject obj) throws RException {

		if (obj.getType() != RType.ATOM && obj.getType() != RType.FACTOR) {
			return false;
		}

		switch (obj.asString()) {
		case F_FOREACH:
		case F_TRY:
		case A_DO:
		case F_LET:
		case F_LOOP:
		case F_OPT:
			return true;
		}

		return false;
	}

	public static void listReturnObject(IRExpr expr, ArrayList<IRObject> exprList) throws RException {

		IRObject e0 = expr.get(0);
		if (e0.getType() != RType.ATOM && e0.getType() != RType.FACTOR) {
			return;
		}

		IRIterator<? extends IRObject> it = null;

		switch (e0.asString()) {
		case A_DO: {
			it = expr.listIterator(1);
			while (it.hasNext()) {
				IRObject e = it.next();
				if (e.getType() == RType.EXPR) {
					listReturnObject((IRExpr) e, exprList);
				}
			}
		}
			break;

		case F_IF: {

			it = expr.listIterator(2);
			while (it.hasNext()) {
				IRObject e = it.next();
				if (e.getType() == RType.EXPR) {
					listReturnObject((IRExpr) e, exprList);
				}
			}
		}
			break;

		case F_RETURN:
		case F_CPS:

			if (expr.size() > 1) {
				exprList.add(expr.get(1));
			}

			break;

		default:

		}
	}

	public static void reset() {

		OptFactorCount.set(0);
	}
}
