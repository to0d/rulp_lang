/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

import static alpha.rulp.lang.Constant.A_ARRAY;
import static alpha.rulp.lang.Constant.A_ATOM;
import static alpha.rulp.lang.Constant.A_BLOB;
import static alpha.rulp.lang.Constant.A_BOOL;
import static alpha.rulp.lang.Constant.A_CLASS;
import static alpha.rulp.lang.Constant.A_CONSTANT;
import static alpha.rulp.lang.Constant.A_DOUBLE;
import static alpha.rulp.lang.Constant.A_EXPRESSION;
import static alpha.rulp.lang.Constant.A_FACTOR;
import static alpha.rulp.lang.Constant.A_FLOAT;
import static alpha.rulp.lang.Constant.A_FRAME;
import static alpha.rulp.lang.Constant.A_FUNCTION;
import static alpha.rulp.lang.Constant.A_INSTANCE;
import static alpha.rulp.lang.Constant.A_INTEGER;
import static alpha.rulp.lang.Constant.A_ITERATOR;
import static alpha.rulp.lang.Constant.A_LIST;
import static alpha.rulp.lang.Constant.A_LONG;
import static alpha.rulp.lang.Constant.A_MACRO;
import static alpha.rulp.lang.Constant.A_MEMBER;
import static alpha.rulp.lang.Constant.A_NATIVE;
import static alpha.rulp.lang.Constant.A_NIL;
import static alpha.rulp.lang.Constant.A_STRING;
import static alpha.rulp.lang.Constant.A_TEMPLATE;
import static alpha.rulp.lang.Constant.A_VAR;
import static alpha.rulp.lang.Constant.O_Nan;
import static alpha.rulp.lang.Constant.O_Nil;
import static alpha.rulp.lang.Constant.T_Array;
import static alpha.rulp.lang.Constant.T_Atom;
import static alpha.rulp.lang.Constant.T_Blob;
import static alpha.rulp.lang.Constant.T_Bool;
import static alpha.rulp.lang.Constant.T_Class;
import static alpha.rulp.lang.Constant.T_Constant;
import static alpha.rulp.lang.Constant.T_Double;
import static alpha.rulp.lang.Constant.T_Expr;
import static alpha.rulp.lang.Constant.T_Factor;
import static alpha.rulp.lang.Constant.T_Float;
import static alpha.rulp.lang.Constant.T_Frame;
import static alpha.rulp.lang.Constant.T_Func;
import static alpha.rulp.lang.Constant.T_Instance;
import static alpha.rulp.lang.Constant.T_Int;
import static alpha.rulp.lang.Constant.T_Iterator;
import static alpha.rulp.lang.Constant.T_List;
import static alpha.rulp.lang.Constant.T_Long;
import static alpha.rulp.lang.Constant.T_Macro;
import static alpha.rulp.lang.Constant.T_Member;
import static alpha.rulp.lang.Constant.T_Native;
import static alpha.rulp.lang.Constant.T_String;
import static alpha.rulp.lang.Constant.T_Template;
import static alpha.rulp.lang.Constant.T_Var;

import java.util.concurrent.atomic.AtomicInteger;

public enum RType {

	NIL(0, A_NIL), //
	ATOM(1, A_ATOM), //
	BOOL(2, A_BOOL), //
	INT(3, A_INTEGER), //
	LONG(4, A_LONG), //
	FLOAT(5, A_FLOAT), //
	DOUBLE(6, A_DOUBLE), //
	STRING(7, A_STRING), //
	BLOB(8, A_BLOB), //
	LIST(9, A_LIST), //
	EXPR(10, A_EXPRESSION), //
	ARRAY(11, A_ARRAY), //
	VAR(12, A_VAR), //
	CONSTANT(13, A_CONSTANT), //
	FACTOR(14, A_FACTOR), //
	FUNC(15, A_FUNCTION), //
	TEMPLATE(16, A_TEMPLATE), //
	MACRO(17, A_MACRO), //
	INSTANCE(18, A_INSTANCE), //
	CLASS(19, A_CLASS), //
	NATIVE(20, A_NATIVE), //
	MEMBER(21, A_MEMBER), //
	FRAME(22, A_FRAME), //
	ITERATOR(23, A_ITERATOR);//

	public static final RType ALL_RTYPE[] = { NIL, ATOM, BOOL, INT, LONG, FLOAT, DOUBLE, STRING, BLOB, LIST, EXPR,
			ARRAY, VAR, CONSTANT, FACTOR, FUNC, TEMPLATE, MACRO, INSTANCE, CLASS, NATIVE, MEMBER, FRAME, ITERATOR };

	public static final int TYPE_NUM = 24;

	public static IRAtom toObject(RType type) {

		switch (type) {
		case ATOM:
			return T_Atom;

		case BOOL:
			return T_Bool;

		case INSTANCE:
			return T_Instance;

		case EXPR:
			return T_Expr;

		case FACTOR:
			return T_Factor;

		case FLOAT:
			return T_Float;

		case DOUBLE:
			return T_Double;

		case FUNC:
			return T_Func;

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

		case VAR:
			return T_Var;

		case CONSTANT:
			return T_Constant;

		case CLASS:
			return T_Class;

		case MEMBER:
			return T_Member;

		case FRAME:
			return T_Frame;

		case TEMPLATE:
			return T_Template;

		case ITERATOR:
			return T_Iterator;

		default:
			return O_Nan;
		}
	}

	public static RType toType(String name) {

		switch (name) {
		case A_ATOM:
			return ATOM;

		case A_BOOL:
			return BOOL;

		case A_INSTANCE:
			return INSTANCE;

		case A_EXPRESSION:
			return EXPR;

		case A_FACTOR:
			return FACTOR;

		case A_FLOAT:
			return FLOAT;

		case A_DOUBLE:
			return DOUBLE;

		case A_FUNCTION:
			return FUNC;

		case A_INTEGER:
			return INT;

		case A_LONG:
			return LONG;

		case A_LIST:
			return LIST;

		case A_MACRO:
			return MACRO;

		case A_NATIVE:
			return NATIVE;

		case A_NIL:
			return NIL;

		case A_STRING:
			return STRING;

		case A_BLOB:
			return BLOB;

		case A_VAR:
			return VAR;

		case A_CONSTANT:
			return CONSTANT;

		case A_CLASS:
			return CLASS;

		case A_MEMBER:
			return MEMBER;

		case A_FRAME:
			return FRAME;

		case A_ARRAY:
			return ARRAY;

		case A_TEMPLATE:
			return TEMPLATE;

		case A_ITERATOR:
			return ITERATOR;

		default:
			return null;
		}
	}

	private AtomicInteger createCount = new AtomicInteger(0);

	private AtomicInteger deleteCount = new AtomicInteger(0);

	private int index;

	private String name;

	private RType(int index, String name) {
		this.index = index;
		this.name = name;
	}

	public int getCreateCount() {
		return createCount.get();
	}

	public int getDeleteCount() {
		return deleteCount.get();
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public int incCreateCount() {
		return createCount.getAndIncrement();
	}

	public void incDeleteCount() {
		deleteCount.incrementAndGet();
	}

	public void reset() {
		createCount.set(0);
		deleteCount.set(0);
	}
}
