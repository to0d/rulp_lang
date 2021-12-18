package alpha.rulp.ximpl.optimize;

import java.util.Set;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpUtil;

public class StableUtil {

	// stable analyze
	// - expression: no external variable in expression, all elements is stable
	// - function: no external variable in function body, all elements is stable

	public static boolean isStable(IRObject obj) throws RException {
		return isStable(obj, null);
	}

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
		case EXPR:
			IRIterator<? extends IRObject> listIt = ((IRList) obj).iterator();
			while (listIt.hasNext()) {
				if (!isStable(listIt.next(), paraVarNames)) {
					return false;
				}
			}
			return true;

		case FUNC:

			if (RulpUtil.isFunctionList(obj)) {
				return false;

			}

			IRFunction func = (IRFunction) obj;

			// This call is coming from Function's isStable
			if (paraVarNames != null && paraVarNames.contains(func.getName())) {
				return true;
			}

			return func.isStable();

		default:

			return false;
		}
	}
}
