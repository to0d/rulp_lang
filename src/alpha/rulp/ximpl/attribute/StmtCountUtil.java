package alpha.rulp.ximpl.attribute;

import static alpha.rulp.lang.Constant.A_STMT_COUNT;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRFunctionList;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRMacro;
import alpha.rulp.utils.AttrUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;

public class StmtCountUtil {

	private static int _getExprStmtCount(IRExpr expr, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (expr.isEmpty()) {
			return 1;
		}

		IRObject e0 = RulpUtil.lookup(expr.get(0), interpreter, frame);
		int count = 1 + _getStmtCount(e0, interpreter, frame);

		IRIterator<? extends IRObject> it = expr.listIterator(1);
		while (it.hasNext()) {
			count += _getStmtCount(it.next(), interpreter, frame);
		}

		return count;
	}

	private static int _getFactorStmtCount(IRFactor factor, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		IRObject attrValue = AttrUtil.getAttributeValue(factor, A_STMT_COUNT);
		if (attrValue != null) {
			return RulpUtil.asInteger(attrValue).asInteger();
		}

		return 1;
	}

	private static int _getFuncStmtCount(IRFunction func, IRInterpreter interpreter, IRFrame frame) throws RException {

		IRObject attrValue = AttrUtil.getAttributeValue(func, A_STMT_COUNT);
		if (attrValue != null) {
			return RulpUtil.asInteger(attrValue).asInteger();
		}

		int count = 0;

		if (AttrUtil.isRecursive(func)) {
			count = -1;

		} else {

			if (func.isList()) {
				IRFunctionList funcList = (IRFunctionList) func;
				for (IRFunction childFunc : funcList.getAllFuncList()) {
					int childCount = _getFuncStmtCount(childFunc, interpreter, frame);
					if (count < childCount) {
						count = childCount;
					}
				}
			} else {
				count = _getStmtCount(func.getFunBody(), interpreter, frame);
			}
		}

		AttrUtil.setAttribute(func, A_STMT_COUNT, RulpFactory.createInteger(count));
		return count;
	}

	private static int _getListStmtCount(IRList list, IRInterpreter interpreter, IRFrame frame) throws RException {

		int count = 0;

		IRIterator<? extends IRObject> it = list.iterator();
		while (it.hasNext()) {
			count += _getStmtCount(it.next(), interpreter, frame);
		}

		return count;
	}

	private static int _getMacroStmtCount(IRMacro macro, IRInterpreter interpreter, IRFrame frame) throws RException {

		IRObject attrValue = AttrUtil.getAttributeValue(macro, A_STMT_COUNT);
		if (attrValue != null) {
			return RulpUtil.asInteger(attrValue).asInteger();
		}

		int count = _getStmtCount(macro.getMacroBody(), interpreter, frame);
		AttrUtil.setAttribute(macro, A_STMT_COUNT, RulpFactory.createInteger(count));
		return count;
	}

	private static int _getStmtCount(IRObject obj, IRInterpreter interpreter, IRFrame frame) throws RException {

//		if (level++ > 100) {
//			System.out.println();
//		}

		switch (obj.getType()) {

		case FACTOR:
			return _getFactorStmtCount((IRFactor) obj, interpreter, frame);

		case FUNC:
			return _getFuncStmtCount((IRFunction) obj, interpreter, frame);

		case MACRO:
			return _getMacroStmtCount((IRMacro) obj, interpreter, frame);

		case TEMPLATE:
			return 1;

		case EXPR:
			return _getExprStmtCount((IRExpr) obj, interpreter, frame);

		case LIST:
			return _getListStmtCount((IRList) obj, interpreter, frame);

		case ATOM:

			String atomName = RulpUtil.asAtom(obj).getName();

			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, atomName);
			// is pure atom
			if (entry == null) {
				return 0;
			}

			IRObject entryValue = entry.getObject();
			if (entryValue == null || entryValue.getType() == RType.ATOM) {
				return 0;
			}

			return _getStmtCount(entryValue, interpreter, frame);

		default:
			return 0;
		}
	}

//	static int level = 0;

	public static int getStmtCount(IRObject obj, IRInterpreter interpreter, IRFrame frame) throws RException {

//		level = 0;

		return _getStmtCount(obj, interpreter, frame);
	}
}
