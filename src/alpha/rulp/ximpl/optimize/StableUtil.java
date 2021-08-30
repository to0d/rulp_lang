package alpha.rulp.ximpl.optimize;

import java.util.HashSet;
import java.util.Set;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpUtil;

public class StableUtil {

	public static boolean isStable(IRObject obj, Set<String> paraVarNames) throws RException {

		switch (obj.getType()) {
		case ATOM:
		case BOOL:
		case CLASS:
		case DOUBLE:
		case FLOAT:
		case INSTANCE:
		case INT:
		case LONG:
		case MEMBER:
		case NATIVE:
		case NIL:
		case STRING:
		case FRAME:
			return true;

		case VAR:
			return paraVarNames != null && paraVarNames.contains(RulpUtil.asVar(obj).getName());

		case FACTOR:
		case TEMPLATE:
		case MACRO:
			return ((IRCallable) obj).isStable();

		case LIST:
			IRIterator<? extends IRObject> listIt = RulpUtil.asList(obj).iterator();
			while (listIt.hasNext()) {
				if (!isStable(listIt.next(), paraVarNames)) {
					return false;
				}
			}
			return true;

		case EXPR:
			IRIterator<? extends IRObject> exprIt = RulpUtil.asExpression(obj).iterator();
			while (exprIt.hasNext()) {
				if (!isStable(exprIt.next(), paraVarNames)) {
					return false;
				}
			}

			return true;

		case FUNC:

			if (RulpUtil.isFunctionList(obj)) {
				return false;

			} else {
				IRFunction func = (IRFunction) obj;
				return isStable(func.getFunBody(), paraVarNames);
			}

		default:

			return false;
		}
	}

	public static boolean isStableFuncion(IRFunction func) throws RException {

		Set<String> paraVarNames = new HashSet<>();
		for (IRParaAttr pa : func.getParaAttrs()) {
			paraVarNames.add(pa.getParaName());
		}

		return isStable(func, paraVarNames);
	}
}
