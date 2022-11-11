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

	String A_AIX = "aix";

	String A_ARRAY = "array";

	String A_ATOM = "atom";

	String A_BLOB = "blob";

	String A_BOOL = "bool";

	String A_By = "by";

	String A_CATCH = "catch";

	String A_CLASS = "class";

	String A_COLUMN = "column";

	String A_CONST = "const";

	String A_CONSTANT = "constant";

	String A_CORE = "core";

	String A_DEBUG = "debug";

	String A_DEFAULT = "default";

	String A_DO = "do";

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

	String A_ITERATOR = "iterator";

	String A_LAMBDA = "lambda";

	String A_LINUX = "linux";

	String A_LIST = "list";

	String A_LOAD_CLASS = "?load-class";

	String A_LOAD_JAR = "?load-jar";

	String A_LOAD_SCRIPT = "?load-script";

	String A_LOCAL = "local";

	String A_LONG = "long";

	String A_MAC = "mac";

	String A_MACRO = "macro";

	String A_MAIN = "main";

	String A_MAP = "map";

	String A_MATH = "math";

	String A_MEMBER = "member";

	String A_NAMESPACE = "namespace";

	String A_NAN = "nan";

	String A_NATIVE = "native";

	String A_NIL = "nil";

	String A_NOCLASS = "noclass";

//	String A_OPT_FULL = "opt-full";

	String A_OPT_ID = "opt-id";

	String A_OPT_LCO = "opt-lco";

//	String A_NULL = "null";

	String A_PARENT = "parent";

	String A_PATH = "?path";

	String A_PRIVATE = "private";

	String A_PUBLIC = "public";

	String A_QUESTION = "?";

	char A_QUESTION_C = '?';

	String A_QUESTION_LIST = "?...";

	String A_QUEUE = "queue";

	String A_RECURSIVE = "recursive";

	String A_RETURN_TYPE = "return-type";

	String A_RETURN_VAR = "?rv";

	String A_ROOT = "root";

	String A_RULP_SUFFIX = ".rulp";

	String A_SET = "set";

	String A_SOCKET = "socket";

	String A_STABLE = "stable";

	String A_STACK = "stack";

	String A_STATIC = "static";

	String A_STMT_COUNT = "stmt-count";

	String A_STRING = "string";

	String A_SUPER = "super";

	String A_SYSTEM = "system";

	String A_TEMPLATE = "template";

	String A_THREAD_UNSAFE = "thread-unsafe";

	String A_TOOL = "tool";

	String A_TRACE = "?trace";

	String A_TRUE = "true";

	String A_TYPE = "type#";

	String A_VALUES = "values";

	String A_VAR = "var";

	String A_WIN = "win";

	IRAtom C_COLUMN = RulpFactory.createAtom(A_COLUMN);

	String C_ERROR_DEFAULT = "_$error$_";

	String C_FUN_ARG_SEP = "_$arg$_";

	String C_HANDLE = "_$handle$_";

	String C_HANDLE_ANY = "_$handle$any$_";

	String E_INFINITE_LOOP = "infinite-loop";

	String F_ACCESS = "access";

	String F_ADD_ARRAY_TO_LIST = "add-array-to-list";

	String F_ADD_ATTRIBUTE = "add-attribute";

	String F_ADD_LIST_TO_ARRAY = "add-list-to-array";

	String F_ADD_LIST_TO_LIST = "add-list-to-list";

	String F_ADD_SEARCH_FRAME = "add_search_frame";

	String F_ALIAS = "alias";

	String F_ATTRIBUTE_OF = "attribute-of";

	String F_B_AND = "and";

	String F_B_NOT = "not";

	String F_B_OR = "or";

	String F_BLOB_LENGTH = "blob-length";

	String F_BREAK = "break";

	String F_CAN_CAST = "can-cast";

	String F_CASE = "case";

	String F_CLASS_OF = "class-of";

	String F_CLONE = "clone";

	String F_CMP = "cmp";

	String F_CMP_DEF = F_CMP + "_def";

	String F_COMPUTE = "compute";

	String F_CONTINUE = "continue";

	String F_CPS = "cps";

	String F_DATE = "date";

	String F_DAY_NUMBER = "day-number";

	String F_DAY_OF_WEEK = "day-of-week";

	String F_DEF_CONST = "defconst";

	String F_DEFCLASS = "defclass";

	String F_DEFMACRO = "defmacro";

	String F_DEFTEMPLATE = "deftemplate";

	String F_DEFUN = "defun";

	String F_DEFVAR = "defvar";

	String F_DELETE = "delete";

	String F_DO_Parallel = "do-p";

	String F_DO_WHEN_OBJ_DELETED = "do-when-obj-deleted";

	String F_DO_WHEN_VAR_CHANGED = "do-when-var-changed";

	String F_E_ERROR_VALUE = "error-value";

	String F_ENDDBG = "enddbg";

	String F_EQUAL = "equal";

	String F_EQUAL_DEFAULT = F_EQUAL + "_def";

	String F_EXTENDS = "extends";

	String F_FILE_DELETE = "file-delete";

	String F_FILE_EXIST = "file-exist";

	String F_FILE_IS_FOLDER = "file-is-folder";

	String F_FILE_LIST = "file-list";

	String F_FILE_MKDIRS = "file-mkdirs";

	String F_FILE_PARENT = "file-parent";

	String F_FOR = "for";

	String F_FOREACH = "foreach";

	String F_FORMAT = "format";

	String F_FREAME_OF = "frame-of";

	String F_GET_ATTRIBUTE = "get-attribute";

	String F_GET_OF_ARRAY = "get-of-array";

	String F_GET_OF_LIST = "get-of-list";

	String F_HAS_MBR = "has-member";

	String F_HAS_NEXT = "has-next";

	String F_IF = "if";

	String F_IN = "in";

	String F_INIT = "init";

	String F_IS_CONST = "is-const";

	String F_IS_DEBUG_ACTIBE = "is-debug-active";

	String F_IS_RECURSIVE = "is-recursive";

	String F_IS_STABLE = "is-stable";

	String F_IS_THREAD_SAFE = "is-thread-safe";

	String F_JOIN = "join";

	String F_LAMBDA = "lambda";

	String F_LET = "let";

//	String F_LOAD_JAR = "load-jar";

	String F_LIST_OF = "list-of";

	String F_LOAD = "load";

	String F_LOAD_CLASS = "load-class";

	String F_LOOP = "loop";

	String F_MAKE_ARRAY = "make-array";

	String F_MAKE_BLOB = "make-blob";

	String F_MAKE_LIST = "make-list";

	String F_MAKE_LIST_ITERATOR = "make-list-Iterator";

	String F_MAKE_STRING = "make-string";

	String F_MBR_THIS = "this";

	String F_NAME_OF = "name-of";

	String F_NEW = "new";

	String F_NEXT = "next";

	String F_NOT_EQUAL = "not-equal";

	String F_O_ADD = "+";

	String F_O_AND = "&";

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

	String F_O_NOT = "~";

	String F_O_OR = "|";

	String F_O_POWER = "power";

	String F_O_REF = "&";

	String F_O_SUB = "-";

	String F_O_XOR = "^";

	String F_OPEN_TXT_FILE = "open-txt-file";

	String F_OPT = "opt";

	String F_OPT_STATUS = "opt-status";

	String F_OUT_TO_FILE = "out-to-file";

	String F_PARENT_OF = "parent-of";

	String F_PRINT = "print";

	String F_PRINT_FRAME_TREE = "print-frame-tree";

	String F_PRINT_GLOBAL_INFO = "print-global-info";

	String F_PRINT_IMPL = "print-impl";

	String F_PRINT_OBJECT = "print-object";

	String F_PRINT_SUBJECT = "print-subject";

	String F_PROPERTY_OF = "property-of";

	String F_RANDOM = "random";

	String F_RANDOM_DOUBLE = "random-double";

	String F_RANDOM_FLOAT = "random-float";

	String F_RANDOM_INT = "random-int";

	String F_READ_LINE = "read-line";

	String F_REF = "ref";

	String F_REMOVE_ALL_LIST = "remove-all-list";

	String F_RETURN = "return";

	String F_RETURN_TYPE_OF = "return-type-of";

	String F_REVERSE = "reverse";

	String F_RULP_OBJ_COUNT = "rulp-object-count";

	String F_RUN = "run";

	String F_RUNTIME_CALL_COUNT = "runtime-call-count";

	String F_SAVE_TXT_FILE = "save-txt-file";

	String F_SEARCH_FRAEM_OF = "search-frame-of";

	String F_SETA = "seta";

	String F_SETQ = "setq";

	String F_SIZE_OF_ARRAY = "size-of-array";

	String F_SIZE_OF_LIST = "size-of-list";

	String F_SLEEP = "sleep";

	String F_SORT = "sort";

	String F_SORT_LIST = "sort-list";

	String F_STMT_COUNT_OF = "stmt-count-of";

	String F_STR_CHAR_AT = "str-char-at";

	String F_STR_END_WITH = "str-end-with";

	String F_STR_EQUAL = "str-equal";

	String F_STR_EQUAL_NOCASE = "str-equal-nocase";

	String F_STR_FORMAT = "str-format";

	String F_STR_INDEX_OF = "str-index-of";

	String F_STR_LAST_INDEX_OF = "str-last-index-of";

	String F_STR_LENGTH = "str-length";

	String F_STR_MATCH = "str-match";

//	String F_STR_SPLIT_LINE = "str-split-line";

	String F_STR_REPLACE = "str-replace";

	String F_STR_SPLIT = "str-split";

	String F_STR_START_WITH = "str-start-with";

	String F_STR_SUBSTR = "str-substr";

	String F_STR_TRIM = "str-trim";

	String F_STR_TRIM_HEAD = "str-trim-head";

	String F_STR_TRIM_TAIL = "str-trim-tail";

	String F_STR_UPPER = "str-upper";

	String F_STRCAT = "strcat";

	String F_STRDBG = "strdbg";

	String F_SUBJECT_OF = "subject-of";

	String F_SYS_FREE_MEMORY = "sys-free-memory";

	String F_SYS_GC = "sys-gc";

	String F_SYS_OS_TYPE = "sys-os-type";

	String F_SYS_SET_PROP = "sys-set-property";

	String F_SYS_TIME = "sys-time";

	String F_SYS_TOTAL_MEMORY = "sys-total-memory";

	String F_THROW = "throw";

	String F_TO = "to";

	String F_TO_ATOM = "to-atom";

	String F_TO_BLOB = "to-blob";

	String F_TO_CONST = "to-const";

	String F_TO_DOUBLE = "to-double";

	String F_TO_EXPR = "to-expr";

	String F_TO_FLOAT = "to-float";

	String F_TO_INT = "to-int";

	String F_TO_LONG = "to-long";

	String F_TO_NAMED_LIST = "to-named-list";

	String F_TO_NONAMED_LIST = "to-nonamed-list";

	String F_TO_STRING = "to-string";

	String F_TO_VALID_PATH = "to-valid-path";

	String F_TO_VARY = "to-vary";

	String F_TRACE = "trace";

	String F_TRY = "try";

	String F_TYPE_OF = "type-of";

	String F_UNINIT = "~init";

	String F_UNION = "union";

	String F_UNIQ = "uniq";

	String F_USE = "use";

	String F_VALUE_OF = "value-of";

	String F_VALUE_TYPE_OF = "value-type-of";

	String F_WHEN = "when";

	String F_WRITE_BLOB = "write-blob";

	int I_FRAME_ID_MIN = 3;

	int I_FRAME_MAIN_ID = 2;

	int I_FRAME_NULL_ID = -1;

	int I_FRAME_ROOT_ID = 0;

	int I_FRAME_SYSTEM_ID = 1;

	String M_CLASS = "#class";

	int MAX_COUNTER_SIZE = 64;

	int MAX_TOSTRING_LEN = 256;

	IRAtom O_ADD = RulpFactory.createAtom(F_O_ADD);

	IRAtom O_AIX = RulpFactory.createAtom(A_AIX);

	IRAtom O_AND = RulpFactory.createAtom(F_O_AND);

	IRAtom O_B_AND = RulpFactory.createAtom(F_B_AND);

	IRAtom O_B_NOT = RulpFactory.createAtom(F_B_NOT);

	IRAtom O_B_OR = RulpFactory.createAtom(F_B_OR);

	IRAtom O_BY = RulpFactory.createAtom(F_O_BY);

	IRAtom O_COMPUTE = RulpFactory.createAtom(F_COMPUTE);

	IRAtom O_CONST = RulpFactory.createAtom(A_CONST);

	IRAtom O_Default = RulpFactory.createAtom(A_DEFAULT);

	IRAtom O_DIV = RulpFactory.createAtom(F_O_DIV);

	IRDouble O_DOUBLE_0 = RulpFactory.createDouble(0);

	IRDouble O_DOUBLE_1 = RulpFactory.createDouble(1);

	IRAtom O_EMPTY = RulpFactory.createAtom("");

	IRAtom O_EQ = RulpFactory.createAtom(F_O_EQ);

	IRBoolean O_False = RulpFactory.createBoolean(false);

	IRAtom O_Final = RulpFactory.createAtom(A_FINAL);

	IRFloat O_FLOAT_0 = RulpFactory.createFloat(0);

	IRFloat O_FLOAT_1 = RulpFactory.createFloat(1);

	IRAtom O_From = RulpFactory.createAtom(A_FROM);

	IRAtom O_GE = RulpFactory.createAtom(F_O_GE);

	IRAtom O_GT = RulpFactory.createAtom(F_O_GT);

	IRAtom O_INFINITE_LOOP = RulpFactory.createAtom(E_INFINITE_LOOP);

	IRInteger O_INT_0 = RulpFactory.createInteger(0);

	IRInteger O_INT_1 = RulpFactory.createInteger(1);

	IRAtom O_LAMBDA = RulpFactory.createAtom(A_LAMBDA);

	IRAtom O_LE = RulpFactory.createAtom(F_O_LE);

	IRAtom O_LINUX = RulpFactory.createAtom(A_LINUX);

	IRLong O_LONG_0 = RulpFactory.createLong(0);

	IRLong O_LONG_1 = RulpFactory.createLong(1);

	IRAtom O_LT = RulpFactory.createAtom(F_O_LT);

	IRAtom O_MAC = RulpFactory.createAtom(A_MAC);

	IRAtom O_MOD = RulpFactory.createAtom(F_O_MOD);

	IRAtom O_Nan = RulpFactory.createAtom(A_NAN);

	IRAtom O_NE = RulpFactory.createAtom(F_O_NE);

	IRAtom O_New = RulpFactory.createAtom(F_NEW);

	IRAtom O_Nil = RulpFactory.createNil();

	IRAtom O_NOT = RulpFactory.createAtom(F_O_NOT);

	IRAtom O_OPT_ID = RulpFactory.createAtom(A_OPT_ID);

	IRAtom O_OR = RulpFactory.createAtom(F_O_OR);

	IRAtom O_POWER = RulpFactory.createAtom(F_O_POWER);

	IRAtom O_Private = RulpFactory.createAtom(A_PRIVATE);

	IRAtom O_Public = RulpFactory.createAtom(A_PUBLIC);

	IRAtom O_QUESTION = RulpFactory.createAtom(A_QUESTION);

	IRAtom O_QUESTION_LIST = RulpFactory.createAtom(A_QUESTION_LIST);

	IRAtom O_RETURN = RulpFactory.createAtom(F_RETURN);

	IRAtom O_RETURN_TYPE = RulpFactory.createAtom(A_RETURN_TYPE);

	IRAtom O_STABLE = RulpFactory.createAtom(A_STABLE);

	IRAtom O_Static = RulpFactory.createAtom(A_STATIC);

	IRAtom O_SUB = RulpFactory.createAtom(F_O_SUB);

	IRAtom O_THREAD_UNSAFE = RulpFactory.createAtom(A_THREAD_UNSAFE);

	IRBoolean O_True = RulpFactory.createBoolean(true);

	IRAtom O_WIN = RulpFactory.createAtom(A_WIN);

	IRAtom O_XOR = RulpFactory.createAtom(F_O_XOR);

	int P_FINAL = 0x0001;

	int P_INHERIT = 0x0004;

	int P_STATIC = 0x0002;

	IRString S_EMPTY = RulpFactory.createString();

	IRAtom T_Array = RulpFactory.createAtom(A_ARRAY);

	IRAtom T_Atom = RulpFactory.createAtom(A_ATOM);

	IRAtom T_Blob = RulpFactory.createAtom(A_BLOB);

	IRAtom T_Bool = RulpFactory.createAtom(A_BOOL);

	IRAtom T_Class = RulpFactory.createAtom(A_CLASS);

	IRAtom T_Constant = RulpFactory.createAtom(A_CONSTANT);

	IRAtom T_Double = RulpFactory.createAtom(A_DOUBLE);

	IRAtom T_Expr = RulpFactory.createAtom(A_EXPRESSION);

	IRAtom T_Factor = RulpFactory.createAtom(A_FACTOR);

	IRAtom T_Float = RulpFactory.createAtom(A_FLOAT);

	IRAtom T_Frame = RulpFactory.createAtom(A_FRAME);

	IRAtom T_Func = RulpFactory.createAtom(A_FUNCTION);

	IRAtom T_Instance = RulpFactory.createAtom(A_INSTANCE);

	IRAtom T_Int = RulpFactory.createAtom(A_INTEGER);

	IRAtom T_Iterator = RulpFactory.createAtom(A_ITERATOR);

	IRAtom T_List = RulpFactory.createAtom(A_LIST);

	IRAtom T_Long = RulpFactory.createAtom(A_LONG);

	IRAtom T_Macro = RulpFactory.createAtom(A_MACRO);

	IRAtom T_Member = RulpFactory.createAtom(A_MEMBER);

	IRAtom T_Native = RulpFactory.createAtom(A_NATIVE);

//	IRAtom T_Null = RulpFactory.createAtom(A_NULL);

	IRAtom T_String = RulpFactory.createAtom(A_STRING);

	IRAtom T_Template = RulpFactory.createAtom(A_TEMPLATE);

	IRAtom T_Var = RulpFactory.createAtom(A_VAR);

	String V_FILE_SEP_CHAR = "?file-separator";
}
