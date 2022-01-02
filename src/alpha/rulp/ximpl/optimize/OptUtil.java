package alpha.rulp.ximpl.optimize;

import static alpha.rulp.lang.Constant.*;
import static alpha.rulp.lang.Constant.O_COMPUTE;

import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRConst;
import alpha.rulp.lang.IRDouble;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFloat;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRInteger;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRLong;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;

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

		if (!RulpUtil.containAttribute(obj, A_ATOM)) {
			return false;
		}

		return true;
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

	public static boolean isFactor(IRObject obj, String name) throws RException {

		if (obj.getType() != RType.ATOM && obj.getType() != RType.FACTOR) {
			return false;
		}

		return obj.asString().equals(name);
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

	public static boolean isStableValue(IRIterator<? extends IRObject> it, NameSet nameSet, IRFrame frame)
			throws RException {

		while (it.hasNext()) {
			if (!isStableValue(it.next(), nameSet, frame)) {
				return false;
			}
		}

		return true;
	}

	public static boolean isStableValue(IRObject obj, NameSet nameSet, IRFrame frame) throws RException {

		if (isLocalValue(obj, nameSet)) {
			return true;
		}

		if (StableUtil.isStable(obj, nameSet.newBranch(), frame)) {
			return true;
		}

		return false;
	}

	public static void reset() {

		OptFactorCount.set(0);
	}
}
