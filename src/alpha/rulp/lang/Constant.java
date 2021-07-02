/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

import alpha.rulp.utils.RulpFactory;

public interface Constant {

	String A_ARRAY = "array";

	String A_ATOM = "atom";

	String A_BOOL = "bool";

	String A_CLASS = "class";

	String A_COLUMN = "column";

	String A_CORE = "core";

	String A_DEFAULT = "default";

	String A_DOUBLE = "double";

	String A_ERROR = "error";

	String A_EXPRESSION = "expr";

	String A_FACTOR = "factor";

	String A_FALSE = "false";

	String A_FINAL = "final";

	String A_FLOAT = "float";

	String A_FRAME = "frame";

	String A_FROM = "from";

	String A_FUN_PRE = "_$fun$_";

	String A_FUNCTION = "func";

	String A_INSTANCE = "instance";

	String A_INTEGER = "int";

	String A_INTO = "into";

	String A_LAMBDA = "lambda";

	String A_LIST = "list";

	String A_LOAD_PATHS = "?load-paths";

	String A_LOCAL = "local";

	String A_LONG = "long";

	String A_MACRO = "macro";

	String A_MAIN = "main";

	String A_MAP = "map";

	String A_MEMBER = "member";

	String A_NAN = "nan";

	String A_NATIVE = "native";

	String A_NIL = "nil";

	String A_NOCLASS = "noclass";

	String A_NULL = "null";

	String A_OP_CPS = "?op-cps";

	String A_OP_STABLE = "?op-stable";

	String A_PARENT = "parent";

	String A_PRIVATE = "private";

	String A_PUBLIC = "public";

	String A_ROOT = "root";

	String A_SET = "set";

	String A_SOCKET = "socket";

	String A_STATIC = "static";

	String A_STRING = "string";

	String A_SYSTEM = "system";

	String A_TRACE = "?trace";

	String A_TRUE = "true";

	String A_VALUES = "values";

	String A_VAR = "var";

	IRAtom C_COLUMN = RulpFactory.createAtom(A_COLUMN);

	String C_ERROR_DEFAULT = "_$error$_";

	String C_FUN_ARG_SEP = "_$arg$_";

	String C_HANDLE = "_$handle$_";

	String C_HANDLE_ANY = "_$handle$any$_";

	String F_ACCESS = "access";

	String F_ADD = "add";

	String F_ADD_ALL = "add-all";

	String F_ALIAS = "alias";

	String F_B_AND = "and";

	String F_B_NOT = "not";

	String F_B_OR = "or";

	String F_BREAK = "break";

	String F_CLASS_OF = "class-of";

	String F_CMP = "cmp";

	String F_CMP_DEF = F_CMP + "_def";

	String F_CONTINUE = "continue";

	String F_DATE = "date";

	String F_DEFCLASS = "defclass";

	String F_DEFMACRO = "defmacro";

	String F_DEFUN = "defun";

	String F_DEFVAR = "defvar";

	String F_DELETE = "delete";

	String F_DO = "do";

	String F_DO_Parallel = "do-p";

	String F_DO_WHEN_OBJ_DELETED = "do-when-obj-deleted";

	String F_DO_WHEN_VAR_CHANGED = "do-when-var-changed";

	String F_E_ERROR = "error";

	String F_E_ERROR_VALUE = "error-value";

	String F_E_TRY = "try";

	String F_EQUAL = "equal";

	String F_EQUAL_DEFAULT = F_EQUAL + "_def";

	String F_FOR = "for";

	String F_FOREACH = "foreach";

	String F_GET_OF_LIST = "get-of-list";

	String F_GET_OF_ARRAY = "get-of-array";

	String F_IF = "if";

	String F_IN = "in";

	String F_JOIN = "join";

	String F_LAMBDA = "lambda";

	String F_LET = "let";

	String F_LOAD = "load";

	String F_LOOP = "loop";

	String F_LS = "ls";

	String F_MAKE_CPS = "make-cps";

	String F_MBR_INIT = "init";

	String F_MBR_SUPER = "super";

	String F_MBR_THIS = "this";

	String F_MBR_UNINIT = "~init";

	String F_NAME_OF = "name-of";

	String F_NEW = "new";

	String F_O_ADD = "+";

	String F_O_BY = "*";

	String F_O_DIV = "/";

	String F_O_EQ = "=";

	String F_O_GE = ">=";

	String F_O_GT = ">";

	String F_O_LE = "<=";

	String F_O_LT = "<";

	String F_O_MBR = "::";

	String F_O_MOD = "%";

	String F_O_NE = "!=";

	String F_O_POWER = "^";

	String F_O_REF = "&";

	String F_O_SUB = "-";

	String F_OPEN_TXT_FILE = "open-txt-file";

	String F_OUT_TO_FILE = "out-to-file";

	String F_PRINT = "print";

	String F_PRINT_SUBJECT = "print-subject";

	String F_PROPERTY_OF = "property-of";

	String F_REF = "ref";

	String F_REMOVE = "remove";

	String F_RETURN = "return";

	String F_REVERSE = "reverse";

	String F_RULP_OBJ_COUNT = "rulp-object-count";

	String F_SAVE_TXT_FILE = "save-txt-file";

	String F_SETQ = "setq";

	String F_SIZE_OF_LIST = "size-of-list";
	
	String F_SIZE_OF_ARRAY = "size-of-array";

	String F_SORT = "sort";

	String F_STR_CHAR_AT = "str-char-at";

	String F_STR_END_WITH = "str-end-with";

	String F_STR_EQUAL = "str-equal";

	String F_STR_EQUAL_NOCASE = "str-equal-nocase";

	String F_STR_FORMAT = "str-format";

	String F_STR_INDEX_OF = "str-index-of";

	String F_STR_LAST_INDEX_OF = "str-last-index-of";

	String F_STR_LENGTH = "str-length";

	String F_STR_MATCH = "str-match";

	String F_STR_START_WITH = "str-start-with";

	String F_STR_SUBSTR = "str-substr";

	String F_STR_TRIM = "str-trim";

	String F_STR_TRIM_HEAD = "str-trim-head";

	String F_STR_TRIM_TAIL = "str-trim-tail";

	String F_STRCAT = "strcat";

	String F_SYS_GC = "sys-gc";

	String F_SYS_TIME = "sys-time";

	String F_T_SLEEP = "sleep";

	String F_TO = "to";

	String F_TO_ATOM = "to-atom";

	String F_TO_INT = "to-int";

	String F_TO_STRING = "to-string";

	String F_TYPE_OF = "type-of";

	String F_UNION = "union";

	String F_UNIQ = "uniq";

	String F_USE = "use";

	String F_VALUE_OF = "value-of";

	String F_VALUE_TYPE_OF = "value-type-of";

	String F_WHEN = "when";

	int I_FRAME_ID_MIN = 3;

	int I_FRAME_MAIN_ID = 2;

	int I_FRAME_NULL_ID = -1;

	int I_FRAME_ROOT_ID = 0;

	int I_FRAME_SYSTEM_ID = 1;

	String M_CLASS = "#class";

	int MAX_COUNTER_SIZE = 64;

	int MAX_TOSTRING_LEN = 256;

	IRAtom O_Default = RulpFactory.createAtom(A_DEFAULT);

	IRBoolean O_False = RulpFactory.createBoolean(false);

	IRAtom O_Final = RulpFactory.createAtom(A_FINAL);

	IRAtom O_From = RulpFactory.createAtom(A_FROM);

	IRAtom O_Nan = RulpFactory.createAtom(A_NAN);

//	IRClass O_NoClass = RulpFactory.createNoClass();

	IRAtom O_Nil = RulpFactory.createNil();

	IRAtom O_Private = RulpFactory.createAtom(A_PRIVATE);

	IRAtom O_Public = RulpFactory.createAtom(A_PUBLIC);

	IRAtom O_Static = RulpFactory.createAtom(A_STATIC);

	IRBoolean O_True = RulpFactory.createBoolean(true);

	String P_TYPE = "type#";

	String S_QUESTION = "?";

	char S_QUESTION_C = '?';

	String S_QUESTION_LIST = "?...";

	IRAtom T_Array = RulpFactory.createAtom(A_ARRAY);

	IRAtom T_Atom = RulpFactory.createAtom(A_ATOM);

	IRAtom T_Bool = RulpFactory.createAtom(A_BOOL);

	IRAtom T_Class = RulpFactory.createAtom(A_CLASS);

	IRAtom T_Double = RulpFactory.createAtom(A_DOUBLE);

	IRAtom T_Expr = RulpFactory.createAtom(A_EXPRESSION);

	IRAtom T_Factor = RulpFactory.createAtom(A_FACTOR);

	IRAtom T_Float = RulpFactory.createAtom(A_FLOAT);

	IRAtom T_Frame = RulpFactory.createAtom(A_FRAME);

	IRAtom T_Func = RulpFactory.createAtom(A_FUNCTION);

	IRAtom T_Instance = RulpFactory.createAtom(A_INSTANCE);

	IRAtom T_Int = RulpFactory.createAtom(A_INTEGER);

	IRAtom T_List = RulpFactory.createAtom(A_LIST);

	IRAtom T_Long = RulpFactory.createAtom(A_LONG);

	IRAtom T_Macro = RulpFactory.createAtom(A_MACRO);

	IRAtom T_Member = RulpFactory.createAtom(A_MEMBER);

	IRAtom T_Native = RulpFactory.createAtom(A_NATIVE);

	IRAtom T_Null = RulpFactory.createAtom(A_NULL);

	IRAtom T_String = RulpFactory.createAtom(A_STRING);

	IRAtom T_Var = RulpFactory.createAtom(A_VAR);

}
