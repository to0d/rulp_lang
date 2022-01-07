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
import static alpha.rulp.lang.Constant.T_Frame;
import static alpha.rulp.lang.Constant.T_Int;
import static alpha.rulp.lang.Constant.T_List;
import static alpha.rulp.lang.Constant.T_Long;
import static alpha.rulp.lang.Constant.T_Macro;
import static alpha.rulp.lang.Constant.T_Member;
import static alpha.rulp.lang.Constant.T_Native;
import static alpha.rulp.lang.Constant.T_String;
import static alpha.rulp.lang.Constant.T_Template;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRConst;
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

	public static IRAtom returnTypeOf(IRObject obj, IRFrame frame) throws RException {

		if (obj == null) {
			return O_Nil;
		}

		switch (obj.getType()) {

		case BOOL:
			return T_Bool;

		case INSTANCE:
			return RulpUtil.asInstance(obj).getRClass().getClassTypeAtom();

		case EXPR:
			return T_Expr;

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

		case MACRO:
			return T_Macro;

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

		case FRAME:
			return T_Frame;

		case TEMPLATE:
			return T_Template;

		case FACTOR:
			IRObject value = AttrUtil.getAttributeValue(obj, A_RETURN_TYPE);
			if (value == null) {
				return O_Nil;
			}
			return RulpUtil.asAtom(value);

		case CONSTANT:
			return returnTypeOf(((IRConst) obj).getValue(), frame);

		case VAR:
			return returnTypeOf(((IRVar) obj).getValue(), frame);

		case CLASS:
			return ((IRClass) obj).getClassTypeAtom();

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
