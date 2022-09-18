package alpha.rulp.ximpl.attribute;

import static alpha.rulp.lang.Constant.A_RETURN_TYPE;
import static alpha.rulp.lang.Constant.O_Nil;
import static alpha.rulp.lang.Constant.T_Array;
import static alpha.rulp.lang.Constant.T_Blob;
import static alpha.rulp.lang.Constant.T_Bool;
import static alpha.rulp.lang.Constant.T_Double;
import static alpha.rulp.lang.Constant.T_Float;
import static alpha.rulp.lang.Constant.T_Func;
import static alpha.rulp.lang.Constant.T_Int;
import static alpha.rulp.lang.Constant.T_Iterator;
import static alpha.rulp.lang.Constant.T_List;
import static alpha.rulp.lang.Constant.T_Long;
import static alpha.rulp.lang.Constant.T_Member;
import static alpha.rulp.lang.Constant.T_Native;
import static alpha.rulp.lang.Constant.T_String;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRConst;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.utils.AttrUtil;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.function.XRFunctionList;
import alpha.rulp.ximpl.optimize.OptUtil;

public class ReturnTypeUtil {

	static class TypeMap {

		private Map<String, IRAtom> localObjTypeMap;

		private TypeMap parent = null;

		public TypeMap() {
		}

		public TypeMap(TypeMap parent) {
			this.parent = parent;
		}

		private Map<String, IRAtom> _buildMap() {

			if (localObjTypeMap == null) {
				localObjTypeMap = new HashMap<>();
				if (parent != null) {
					localObjTypeMap.putAll(parent._findMap());
				}
			}

			return localObjTypeMap;
		}

		private Map<String, IRAtom> _findMap() {

			if (localObjTypeMap != null) {
				return localObjTypeMap;
			}

			return parent == null ? Collections.emptyMap() : parent._findMap();
		}

		public void addType(String name, IRAtom type) {
			_buildMap().put(name, type);
		}

		public IRAtom lookupType(String name) {

			if (localObjTypeMap != null) {
				return localObjTypeMap.get(name);
			}

			return parent == null ? null : parent.lookupType(name);
		}

		public TypeMap newBranch() {
			return new TypeMap(this);
		}

	}

	private static IRAtom _exprReturnTypeOf(IRExpr expr, TypeMap typeMap, IRFrame frame) throws RException {

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
					return _returnTypeOf(expr.get(index), typeMap, frame);
				}
			}
		}

		if (e0.getType() == RType.FUNC) {

			IRAtom attrValue = _returnTypeOf(e0, typeMap, frame);
			if (attrValue != O_Nil) {
				return attrValue;
			}

			if (!((IRFunction) e0).isList() && expr.size() > 1) {
				return _funcRuntimeReturnTypeOf((IRFunction) e0, expr, typeMap, frame);
			}

//			return _funReturnTypeOf(e0, TypeMap, frame);
		}

		return O_Nil;
	}

	private static IRAtom _funcDefinedReturnTypeOf(IRFunction func, TypeMap typeMap, IRFrame frame) throws RException {

		if (func.isList()) {

			XRFunctionList listFunc = (XRFunctionList) func;
			IRAtom listRT = null;

			for (IRFunction childFunc : listFunc.getAllFuncList()) {

				IRAtom childType = _funcDefinedReturnTypeOf(childFunc, typeMap, frame);
				if (listRT == null) {
					listRT = childType;
				} else {
					if (!RulpUtil.equal(childType, listRT)) {
						return O_Nil;
					}
				}
			}

			if (listRT == null) {
				return O_Nil;
			}

			return listRT;
		}

		typeMap = typeMap.newBranch();
		typeMap.addType(func.getName(), T_Func);
		if (func.getParaAttrs() != null) {
			for (IRParaAttr para : func.getParaAttrs()) {
				IRAtom paraType = para.getParaType();
				if (paraType != O_Nil) {
					typeMap.addType(para.getParaName(), paraType);
				}
			}
		}

		ArrayList<IRObject> returnList = new ArrayList<>();
		OptUtil.listReturnObject(func.getFunBody(), returnList);
		if (returnList.isEmpty()) {
			return O_Nil;
		}

		IRAtom funcRT = null;

		for (IRObject rtObj : returnList) {

			IRAtom type = _returnTypeOf(rtObj, typeMap, frame);
			if (funcRT == null) {
				funcRT = type;
			} else {
				if (!RulpUtil.equal(funcRT, type)) {
					return O_Nil;
				}
			}
		}

		return funcRT == null ? O_Nil : funcRT;
	}

	private static IRAtom _funcRuntimeReturnTypeOf(IRFunction func, IRExpr expr, TypeMap typeMap, IRFrame frame)
			throws RException {

		typeMap = typeMap.newBranch();
		typeMap.addType(func.getName(), T_Func);
		if (func.getParaAttrs() != null) {

			int index = 1;
			for (IRParaAttr para : func.getParaAttrs()) {
				IRAtom paraType = para.getParaType();
				if (paraType != O_Nil) {
					typeMap.addType(para.getParaName(), paraType);
				} else {
					IRObject value = expr.get(index);
					IRAtom valueType = _returnTypeOf(value, typeMap, frame);
					typeMap.addType(para.getParaName(), valueType);
				}

				++index;
			}
		}

		ArrayList<IRObject> returnList = new ArrayList<>();
		OptUtil.listReturnObject(func.getFunBody(), returnList);
		if (returnList.isEmpty()) {
			return O_Nil;
		}

		IRAtom funcRT = null;

		for (IRObject rtObj : returnList) {

			IRAtom type = _returnTypeOf(rtObj, typeMap, frame);
			if (funcRT == null) {
				funcRT = type;
			} else {
				if (!RulpUtil.equal(funcRT, type)) {
					return O_Nil;
				}
			}
		}

		return funcRT == null ? O_Nil : funcRT;
	}

	private static IRAtom _returnTypeOf(IRObject obj, TypeMap typeMap, IRFrame frame) throws RException {

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

		case ITERATOR:
			return T_Iterator;

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

		case FACTOR: {
			IRObject attrValue = AttrUtil.getAttributeValue(obj, A_RETURN_TYPE);
			if (attrValue == null || attrValue.getType() != RType.ATOM) {
				return O_Nil;
			}

			return (IRAtom) attrValue;
		}

		case CONSTANT:
			return _returnTypeOf(((IRConst) obj).getValue(), typeMap, frame);

		case VAR:
			return _returnTypeOf(((IRVar) obj).getValue(), typeMap, frame);

		case EXPR:
			return _exprReturnTypeOf((IRExpr) obj, typeMap, frame);

		case FUNC: {
			IRObject attrValue = AttrUtil.getAttributeValue(obj, A_RETURN_TYPE);
			if (attrValue == null) {
				attrValue = _funcDefinedReturnTypeOf((IRFunction) obj, typeMap, frame);
				AttrUtil.setAttribute(obj, A_RETURN_TYPE, attrValue);
			}

			return (IRAtom) attrValue;
		}

		case ATOM:

			String atomName = RulpUtil.asAtom(obj).getName();

			IRAtom _type = typeMap.lookupType(atomName);
			if (_type != null) {
				return _type;
			}

			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, atomName);
			// is pure atom
			if (entry == null) {
				return O_Nil;
			}

			IRObject entryValue = entry.getObject();
			if (entryValue == null || entryValue.getType() == RType.ATOM) {
				return O_Nil;
			}

			return _returnTypeOf(entryValue, typeMap, frame);

		default:
			return O_Nil;
		}
	}

	public static IRAtom returnTypeOf(IRObject obj, IRFrame frame) throws RException {
		return _returnTypeOf(obj, new TypeMap(), frame);
	}
}
