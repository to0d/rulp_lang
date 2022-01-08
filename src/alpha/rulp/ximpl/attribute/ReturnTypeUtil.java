package alpha.rulp.ximpl.attribute;

import static alpha.rulp.lang.Constant.A_RETURN_TYPE;
import static alpha.rulp.lang.Constant.O_Nil;
import static alpha.rulp.lang.Constant.T_Array;
import static alpha.rulp.lang.Constant.T_Atom;
import static alpha.rulp.lang.Constant.T_Blob;
import static alpha.rulp.lang.Constant.T_Bool;
import static alpha.rulp.lang.Constant.T_Double;
import static alpha.rulp.lang.Constant.T_Expr;
import static alpha.rulp.lang.Constant.T_Float;
import static alpha.rulp.lang.Constant.T_Int;
import static alpha.rulp.lang.Constant.T_List;
import static alpha.rulp.lang.Constant.T_Long;
import static alpha.rulp.lang.Constant.T_Member;
import static alpha.rulp.lang.Constant.T_Native;
import static alpha.rulp.lang.Constant.T_String;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRConst;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.AttrUtil;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;

public class ReturnTypeUtil {

	public static IRAtom _exprReturnTypeOf(IRExpr expr, IRFrame frame) throws RException {

		if (expr.size() == 0) {
			return O_Nil;
		}

		IRObject e0 = expr.get(0);

		if (e0.getType() == RType.ATOM) {

			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, RulpUtil.asAtom(e0).getName());
			// unknown factor
			if (entry != null && entry.getObject() != null) {
				e0 = entry.getObject();
			}
		}

		if (e0.getType() == RType.FACTOR) {

			IRObject value = AttrUtil.getAttributeValue(e0, A_RETURN_TYPE);
			if (value == null) {
				return O_Nil;
			}

			if (value.getType() == RType.ATOM) {
				return (IRAtom) value;
			}

			if (value.getType() == RType.INT) {

				int index = RulpUtil.asInteger(value).asInteger();
				if (index >= 0 && index < expr.size()) {
					return returnTypeOf(expr.get(index), frame);
				}
			}
		}

		return O_Nil;
	}

	public static IRAtom returnTypeOf(IRObject obj, IRFrame frame) throws RException {

		if (obj == null) {
			return O_Nil;
		}

		switch (obj.getType()) {

		case BOOL:
			return T_Bool;

		case INSTANCE:
			return RulpUtil.asInstance(obj).getRClass().getClassTypeAtom();

		case FLOAT:
			return T_Float;

		case DOUBLE:
			return T_Double;

		case INT:
			return T_Int;

		case LONG:
			return T_Long;

		case LIST:
			return T_List;

		case ARRAY:
			return T_Array;

		case NATIVE:
			return T_Native;

		case NIL:
			return O_Nil;

		case STRING:
			return T_String;

		case BLOB:
			return T_Blob;

		case MEMBER:
			return T_Member;

		case FACTOR:
			IRObject value = AttrUtil.getAttributeValue(obj, A_RETURN_TYPE);
			if (value == null || value.getType() != RType.ATOM) {
				return O_Nil;
			}

			return (IRAtom) value;

		case CONSTANT:
			return returnTypeOf(((IRConst) obj).getValue(), frame);

		case VAR:
			return returnTypeOf(((IRVar) obj).getValue(), frame);

		case EXPR:
			return _exprReturnTypeOf((IRExpr) obj, frame);

		case ATOM:

			String atomName = RulpUtil.asAtom(obj).getName();

			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, atomName);
			// is pure atom
			if (entry == null) {
				return T_Atom;
			}

			IRObject entryValue = entry.getObject();
			if (entryValue == null || entryValue.getType() == RType.ATOM) {
				return T_Atom;
			}

			return returnTypeOf(entryValue, frame);

		default:
			return O_Nil;
		}
	}
}
