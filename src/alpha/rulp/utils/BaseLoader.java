/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utils;

import static alpha.rulp.lang.Constant.*;

import java.io.IOException;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.RArithmeticOperator;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RRelationalOperator;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRObjectLoader;
import alpha.rulp.ximpl.array.XRFactorAddArrayToList;
import alpha.rulp.ximpl.array.XRFactorAddListToArray;
import alpha.rulp.ximpl.array.XRFactorAddListToList;
import alpha.rulp.ximpl.array.XRFactorMakeArray;
import alpha.rulp.ximpl.array.XRFactorSeta;
import alpha.rulp.ximpl.attribute.XRFactorAddAttribute;
import alpha.rulp.ximpl.attribute.XRFactorAttributeOf;
import alpha.rulp.ximpl.attribute.XRFactorGetAttribute;
import alpha.rulp.ximpl.attribute.XRFactorIsConst;
import alpha.rulp.ximpl.attribute.XRFactorIsRecursive;
import alpha.rulp.ximpl.attribute.XRFactorIsStable;
import alpha.rulp.ximpl.attribute.XRFactorIsThreadSafe;
import alpha.rulp.ximpl.attribute.XRFactorReturnTypeOf;
import alpha.rulp.ximpl.attribute.XRFactorStmtCountOf;
import alpha.rulp.ximpl.blob.XRFactorBlobLength;
import alpha.rulp.ximpl.blob.XRFactorMakeBlob;
import alpha.rulp.ximpl.blob.XRFactorToBlob;
import alpha.rulp.ximpl.blob.XRFactorWriteBlob;
import alpha.rulp.ximpl.bool.XRFactorBitNot;
import alpha.rulp.ximpl.bool.XRFactorBoolAnd;
import alpha.rulp.ximpl.bool.XRFactorBoolNot;
import alpha.rulp.ximpl.bool.XRFactorBoolOr;
import alpha.rulp.ximpl.bool.XRFactorEqual;
import alpha.rulp.ximpl.collection.XRFactorForeach;
import alpha.rulp.ximpl.collection.XRFactorGetOfArray;
import alpha.rulp.ximpl.collection.XRFactorGetOfList;
import alpha.rulp.ximpl.collection.XRFactorJoin;
import alpha.rulp.ximpl.collection.XRFactorRemoveAllList;
import alpha.rulp.ximpl.collection.XRFactorReverse;
import alpha.rulp.ximpl.collection.XRFactorSizeOfArray;
import alpha.rulp.ximpl.collection.XRFactorSizeOfList;
import alpha.rulp.ximpl.collection.XRFactorSort;
import alpha.rulp.ximpl.collection.XRFactorUnion;
import alpha.rulp.ximpl.collection.XRFactorUniq;
import alpha.rulp.ximpl.control.XRFactorBreak;
import alpha.rulp.ximpl.control.XRFactorCase;
import alpha.rulp.ximpl.control.XRFactorContinue;
import alpha.rulp.ximpl.control.XRFactorDo;
import alpha.rulp.ximpl.control.XRFactorIf;
import alpha.rulp.ximpl.control.XRFactorLet;
import alpha.rulp.ximpl.control.XRFactorLoop;
import alpha.rulp.ximpl.control.XRFactorReturn;
import alpha.rulp.ximpl.control.XRFactorThrow;
import alpha.rulp.ximpl.control.XRFactorTry;
import alpha.rulp.ximpl.control.XRFactorWhen;
import alpha.rulp.ximpl.debug.XRFactorEnddbg;
import alpha.rulp.ximpl.debug.XRFactorIsDebugActive;
import alpha.rulp.ximpl.debug.XRFactorStrdbg;
import alpha.rulp.ximpl.error.XRFactorErrorValue;
import alpha.rulp.ximpl.factor.XRFactorAddSearchFrame;
import alpha.rulp.ximpl.factor.XRFactorAlias;
import alpha.rulp.ximpl.factor.XRFactorCanCast;
import alpha.rulp.ximpl.factor.XRFactorClone;
import alpha.rulp.ximpl.factor.XRFactorCompare;
import alpha.rulp.ximpl.factor.XRFactorCompute;
import alpha.rulp.ximpl.factor.XRFactorDefConst;
import alpha.rulp.ximpl.factor.XRFactorDefvar;
import alpha.rulp.ximpl.factor.XRFactorDoWhenObjDeleted;
import alpha.rulp.ximpl.factor.XRFactorDoWhenVarChanged;
import alpha.rulp.ximpl.factor.XRFactorFrameOf;
import alpha.rulp.ximpl.factor.XRFactorListOf;
import alpha.rulp.ximpl.factor.XRFactorMakeList;
import alpha.rulp.ximpl.factor.XRFactorNameOf;
import alpha.rulp.ximpl.factor.XRFactorParentOf;
import alpha.rulp.ximpl.factor.XRFactorRef;
import alpha.rulp.ximpl.factor.XRFactorRulpObjectCount;
import alpha.rulp.ximpl.factor.XRFactorSearchFrameOf;
import alpha.rulp.ximpl.factor.XRFactorSetq;
import alpha.rulp.ximpl.factor.XRFactorSubjectOf;
import alpha.rulp.ximpl.factor.XRFactorToAtom;
import alpha.rulp.ximpl.factor.XRFactorToConst;
import alpha.rulp.ximpl.factor.XRFactorToExpr;
import alpha.rulp.ximpl.factor.XRFactorToNamedList;
import alpha.rulp.ximpl.factor.XRFactorToNoNamedList;
import alpha.rulp.ximpl.factor.XRFactorToVary;
import alpha.rulp.ximpl.factor.XRFactorTypeOf;
import alpha.rulp.ximpl.factor.XRFactorValueOf;
import alpha.rulp.ximpl.factor.XRFactorValueTypeOf;
import alpha.rulp.ximpl.fs.XRFactorFileDelete;
import alpha.rulp.ximpl.fs.XRFactorFileExist;
import alpha.rulp.ximpl.fs.XRFactorFileIsFolder;
import alpha.rulp.ximpl.fs.XRFactorFileList;
import alpha.rulp.ximpl.fs.XRFactorFileMkdirs;
import alpha.rulp.ximpl.fs.XRFactorFileParent;
import alpha.rulp.ximpl.function.XRFactorDefun;
import alpha.rulp.ximpl.function.XRFactorLambda;
import alpha.rulp.ximpl.io.XRFactorFormat;
import alpha.rulp.ximpl.io.XRFactorLoad;
import alpha.rulp.ximpl.io.XRFactorLoadClass;
import alpha.rulp.ximpl.io.XRFactorOpenTxtFile;
import alpha.rulp.ximpl.io.XRFactorOutToFile;
import alpha.rulp.ximpl.io.XRFactorPrint;
import alpha.rulp.ximpl.io.XRFactorPrintFrameTree;
import alpha.rulp.ximpl.io.XRFactorPrintGlobalInfo;
import alpha.rulp.ximpl.io.XRFactorPrintObject;
import alpha.rulp.ximpl.io.XRFactorPrintSubject;
import alpha.rulp.ximpl.io.XRFactorReadLine;
import alpha.rulp.ximpl.io.XRFactorRun;
import alpha.rulp.ximpl.io.XRFactorSaveTxtFile;
import alpha.rulp.ximpl.io.XRFactorTrace;
import alpha.rulp.ximpl.iterator.XRFactorHasNext;
import alpha.rulp.ximpl.iterator.XRFactorMakeListIterator;
import alpha.rulp.ximpl.iterator.XRFactorNext;
import alpha.rulp.ximpl.macro.XRFactorDefMacro;
import alpha.rulp.ximpl.math.XRFactorArithmetic;
import alpha.rulp.ximpl.math.XRFactorComparison;
import alpha.rulp.ximpl.math.XRFactorRandom;
import alpha.rulp.ximpl.math.XRFactorRandomFloat;
import alpha.rulp.ximpl.math.XRFactorRandomInt;
import alpha.rulp.ximpl.math.XRFactorToDouble;
import alpha.rulp.ximpl.math.XRFactorToFloat;
import alpha.rulp.ximpl.math.XRFactorToInteger;
import alpha.rulp.ximpl.math.XRFactorToLong;
import alpha.rulp.ximpl.namespace.XRNameSpaceClass;
import alpha.rulp.ximpl.optimize.XFactorReduct;
import alpha.rulp.ximpl.optimize.XRFactorCC;
import alpha.rulp.ximpl.optimize.XRFactorListFunctionInReturn;
import alpha.rulp.ximpl.optimize.XRFactorOptStatus;
import alpha.rulp.ximpl.optimize.XRFactorPrintImpl;
import alpha.rulp.ximpl.rclass.XRFactorAccess;
import alpha.rulp.ximpl.rclass.XRFactorClassOf;
import alpha.rulp.ximpl.rclass.XRFactorDefClass;
import alpha.rulp.ximpl.rclass.XRFactorDelete;
import alpha.rulp.ximpl.rclass.XRFactorGetMbr;
import alpha.rulp.ximpl.rclass.XRFactorHasMbr;
import alpha.rulp.ximpl.rclass.XRFactorNew;
import alpha.rulp.ximpl.rclass.XRFactorPropertyOf;
import alpha.rulp.ximpl.rclass.XRNoClass;
import alpha.rulp.ximpl.runtime.XRFactorRuntimeCallCount;
import alpha.rulp.ximpl.string.XRFactorMakeString;
import alpha.rulp.ximpl.string.XRFactorStrCat;
import alpha.rulp.ximpl.string.XRFactorStrCharAt;
import alpha.rulp.ximpl.string.XRFactorStrEndWith;
import alpha.rulp.ximpl.string.XRFactorStrEqual;
import alpha.rulp.ximpl.string.XRFactorStrEqualNoCase;
import alpha.rulp.ximpl.string.XRFactorStrFormat;
import alpha.rulp.ximpl.string.XRFactorStrIndexOf;
import alpha.rulp.ximpl.string.XRFactorStrLastIndexOf;
import alpha.rulp.ximpl.string.XRFactorStrLength;
import alpha.rulp.ximpl.string.XRFactorStrMatch;
import alpha.rulp.ximpl.string.XRFactorStrReplace;
import alpha.rulp.ximpl.string.XRFactorStrSplit;
import alpha.rulp.ximpl.string.XRFactorStrStartsWith;
import alpha.rulp.ximpl.string.XRFactorStrSubStr;
import alpha.rulp.ximpl.string.XRFactorStrTrim;
import alpha.rulp.ximpl.string.XRFactorStrTrimHead;
import alpha.rulp.ximpl.string.XRFactorStrTrimTail;
import alpha.rulp.ximpl.string.XRFactorStrUpper;
import alpha.rulp.ximpl.string.XRFactorToString;
import alpha.rulp.ximpl.system.XRFactorSystemFreeMemory;
import alpha.rulp.ximpl.system.XRFactorSystemGC;
import alpha.rulp.ximpl.system.XRFactorSystemOSType;
import alpha.rulp.ximpl.system.XRFactorSystemSetProperty;
import alpha.rulp.ximpl.system.XRFactorSystemTotalMemory;
import alpha.rulp.ximpl.template.XRFactorDefTemplate;
import alpha.rulp.ximpl.thread.XRFactorDoParallel;
import alpha.rulp.ximpl.thread.XRFactorSleep;
import alpha.rulp.ximpl.time.XFactorDayOfWeek;
import alpha.rulp.ximpl.time.XRFactorDate;
import alpha.rulp.ximpl.time.XRFactorDayNumber;
import alpha.rulp.ximpl.time.XRFactorSystemTime;

public class BaseLoader implements IRObjectLoader {

	@Override
	public void load(IRInterpreter interpreter, IRFrame frame) throws RException, IOException {

		// Objects
		RulpUtil.addFrameObject(frame, O_Nil);
		RulpUtil.addFrameObject(frame, O_True);
		RulpUtil.addFrameObject(frame, O_False);
		RulpUtil.addFrameObject(frame, O_Nan);
		RulpUtil.addFrameObject(frame, O_Public);
		RulpUtil.addFrameObject(frame, O_Private);
		RulpUtil.addFrameObject(frame, O_Default);
		RulpUtil.addFrameObject(frame, O_Final);
		RulpUtil.addFrameObject(frame, O_Static);
		RulpUtil.addFrameObject(frame, O_From);

		RulpUtil.addFrameObject(frame, T_Atom);
		RulpUtil.addFrameObject(frame, T_Bool);
		RulpUtil.addFrameObject(frame, T_Int);
		RulpUtil.addFrameObject(frame, T_Int);
		RulpUtil.addFrameObject(frame, T_Long);
		RulpUtil.addFrameObject(frame, T_Float);
		RulpUtil.addFrameObject(frame, T_Double);
		RulpUtil.addFrameObject(frame, T_String);
		RulpUtil.addFrameObject(frame, T_Blob);
		RulpUtil.addFrameObject(frame, T_List);
		RulpUtil.addFrameObject(frame, T_Expr);
		RulpUtil.addFrameObject(frame, T_Array);
		RulpUtil.addFrameObject(frame, T_Var);
		RulpUtil.addFrameObject(frame, T_Constant);
		RulpUtil.addFrameObject(frame, T_Factor);
		RulpUtil.addFrameObject(frame, T_Func);
		RulpUtil.addFrameObject(frame, T_Macro);
		RulpUtil.addFrameObject(frame, T_Instance);
		RulpUtil.addFrameObject(frame, T_Class);
		RulpUtil.addFrameObject(frame, T_Native);
		RulpUtil.addFrameObject(frame, T_Member);
		RulpUtil.addFrameObject(frame, T_Frame);
		RulpUtil.addFrameObject(frame, T_Iterator);

		RulpUtil.addFrameObject(frame, new XRFactorNameOf(F_NAME_OF));
		RulpUtil.addFrameObject(frame, new XRFactorTypeOf(F_TYPE_OF));
		RulpUtil.addFrameObject(frame, new XRFactorValueOf(F_VALUE_OF));
		RulpUtil.addFrameObject(frame, new XRFactorValueTypeOf(F_VALUE_TYPE_OF));
		RulpUtil.addFrameObject(frame, new XRFactorAlias(F_ALIAS));
		RulpUtil.addFrameObject(frame, new XRFactorEqual(F_EQUAL_DEFAULT));
		RulpUtil.addFrameObject(frame, new XRFactorToAtom(F_TO_ATOM));
		RulpUtil.addFrameObject(frame, new XRFactorSizeOfList(F_SIZE_OF_LIST));
		RulpUtil.addFrameObject(frame, new XRFactorSizeOfArray(F_SIZE_OF_ARRAY));
		RulpUtil.addFrameObject(frame, new XRFactorSubjectOf(F_SUBJECT_OF));
		RulpUtil.addFrameObject(frame, new XRFactorFrameOf(F_FREAME_OF));
		RulpUtil.addFrameObject(frame, new XRFactorParentOf(F_PARENT_OF));
		RulpUtil.addFrameObject(frame, new XRFactorAddSearchFrame(F_ADD_SEARCH_FRAME));
		RulpUtil.addFrameObject(frame, new XRFactorSearchFrameOf(F_SEARCH_FRAEM_OF));
		RulpUtil.addFrameObject(frame, new XRFactorIsConst(F_IS_CONST));
		RulpUtil.addFrameObject(frame, new XRFactorMakeList(F_MAKE_LIST));
		RulpUtil.addFrameObject(frame, new XRFactorMakeArray(F_MAKE_ARRAY));
		RulpUtil.addFrameObject(frame, new XRFactorToExpr(F_TO_EXPR));
		RulpUtil.addFrameObject(frame, new XRFactorCompute(F_COMPUTE));
		RulpUtil.addFrameObject(frame, new XRFactorAttributeOf(F_ATTRIBUTE_OF));
		RulpUtil.addFrameObject(frame, new XRFactorAddAttribute(F_ADD_ATTRIBUTE));
		RulpUtil.addFrameObject(frame, new XRFactorReturnTypeOf(F_RETURN_TYPE_OF));
		RulpUtil.addFrameObject(frame, new XRFactorGetAttribute(F_GET_ATTRIBUTE));
		RulpUtil.addFrameObject(frame, new XRFactorStmtCountOf(F_STMT_COUNT_OF));
		RulpUtil.addFrameObject(frame, new XRFactorIsRecursive(F_IS_RECURSIVE));
		RulpUtil.addFrameObject(frame, new XRFactorClone(F_CLONE));

		// Class
		RulpUtil.addFrameObject(frame, new XRNoClass(A_NOCLASS, frame));
		RulpUtil.addFrameObject(frame, new XRFactorClassOf(F_CLASS_OF));
		RulpUtil.addFrameObject(frame, new XRFactorPropertyOf(F_PROPERTY_OF));
		RulpUtil.addFrameObject(frame, new XRFactorAccess(F_ACCESS));
		RulpUtil.addFrameObject(frame, new XRFactorNew(F_NEW));
		RulpUtil.addFrameObject(frame, new XRFactorDelete(F_DELETE));
		RulpUtil.addFrameObject(frame, new XRFactorDefClass(F_DEFCLASS));
		RulpUtil.addFrameObject(frame, new XRFactorGetMbr(F_O_MBR));
		RulpUtil.addFrameObject(frame, new XRFactorHasMbr(F_HAS_MBR));
		RulpUtil.addFrameObject(frame, new XRFactorListOf(F_LIST_OF));
		RulpUtil.addFrameObject(frame, new XRNameSpaceClass(A_NAMESPACE, frame));

		// IO
		RulpUtil.addFrameObject(frame, new XRFactorPrint(F_PRINT));
		RulpUtil.addFrameObject(frame, new XRFactorLoad(F_LOAD));
		RulpUtil.addFrameObject(frame, new XRFactorOutToFile(F_OUT_TO_FILE));
		RulpUtil.addFrameObject(frame, new XRFactorOpenTxtFile(F_OPEN_TXT_FILE));
		RulpUtil.addFrameObject(frame, new XRFactorSaveTxtFile(F_SAVE_TXT_FILE));
		RulpUtil.addFrameObject(frame, new XRFactorPrintSubject(F_PRINT_SUBJECT));
		RulpUtil.addFrameObject(frame, new XRFactorPrintFrameTree(F_PRINT_FRAME_TREE));
		RulpUtil.addFrameObject(frame, new XRFactorPrintGlobalInfo(F_PRINT_GLOBAL_INFO));
		RulpUtil.addFrameObject(frame, new XRFactorPrintImpl(F_PRINT_IMPL));
		RulpUtil.addFrameObject(frame, new XRFactorRun(F_RUN));
		RulpUtil.addFrameObject(frame, new XRFactorLoadClass(F_LOAD_CLASS));
		RulpUtil.addFrameObject(frame, new XRFactorPrintObject(F_PRINT_OBJECT));
		RulpUtil.addFrameObject(frame, new XRFactorTrace(F_TRACE));
		RulpUtil.addFrameObject(frame, new XRFactorReadLine(F_READ_LINE));
		RulpUtil.addFrameObject(frame, new XRFactorFormat(F_FORMAT));

		// FS
		RulpUtil.addFrameObject(frame, new XRFactorFileExist(F_FILE_EXIST));
		RulpUtil.addFrameObject(frame, new XRFactorFileIsFolder(F_FILE_IS_FOLDER));
		RulpUtil.addFrameObject(frame, new XRFactorFileMkdirs(F_FILE_MKDIRS));
		RulpUtil.addFrameObject(frame, new XRFactorFileList(F_FILE_LIST));
		RulpUtil.addFrameObject(frame, new XRFactorFileDelete(F_FILE_DELETE));
		RulpUtil.addFrameObject(frame, new XRFactorFileParent(F_FILE_PARENT));

		// Variable, Value & Expression
		RulpUtil.addFrameObject(frame, new XRFactorDefvar(F_DEFVAR, true, false));
		RulpUtil.addFrameObject(frame, new XRFactorSetq(F_SETQ));
		RulpUtil.addFrameObject(frame, new XRFactorDoWhenVarChanged(F_DO_WHEN_VAR_CHANGED));
		RulpUtil.addFrameObject(frame, new XRFactorDoWhenObjDeleted(F_DO_WHEN_OBJ_DELETED));
		RulpUtil.addFrameObject(frame, new XRFactorRef(F_REF));
		RulpUtil.addFrameObject(frame, new XRFactorDefConst(F_DEF_CONST));

		// Blob
		RulpUtil.addFrameObject(frame, new XRFactorMakeBlob(F_MAKE_BLOB));
		RulpUtil.addFrameObject(frame, new XRFactorBlobLength(F_BLOB_LENGTH));
		RulpUtil.addFrameObject(frame, new XRFactorToBlob(F_TO_BLOB));
		RulpUtil.addFrameObject(frame, new XRFactorWriteBlob(F_WRITE_BLOB));
		RulpUtil.addFrameObject(frame, new XRFactorMakeString(F_MAKE_STRING));

		// String
		RulpUtil.addFrameObject(frame, new XRFactorToString(F_TO_STRING));
		RulpUtil.addFrameObject(frame, new XRFactorToInteger(F_TO_INT));
		RulpUtil.addFrameObject(frame, new XRFactorToLong(F_TO_LONG));
		RulpUtil.addFrameObject(frame, new XRFactorToFloat(F_TO_FLOAT));
		RulpUtil.addFrameObject(frame, new XRFactorToDouble(F_TO_DOUBLE));
		RulpUtil.addFrameObject(frame, new XRFactorStrCat(F_STRCAT));
		RulpUtil.addFrameObject(frame, new XRFactorStrEqual(F_STR_EQUAL));
		RulpUtil.addFrameObject(frame, new XRFactorStrEqualNoCase(F_STR_EQUAL_NOCASE));
		RulpUtil.addFrameObject(frame, new XRFactorStrTrim(F_STR_TRIM));
		RulpUtil.addFrameObject(frame, new XRFactorStrTrimHead(F_STR_TRIM_HEAD));
		RulpUtil.addFrameObject(frame, new XRFactorStrTrimTail(F_STR_TRIM_TAIL));
		RulpUtil.addFrameObject(frame, new XRFactorStrMatch(F_STR_MATCH));
		RulpUtil.addFrameObject(frame, new XRFactorStrStartsWith(F_STR_START_WITH));
		RulpUtil.addFrameObject(frame, new XRFactorStrEndWith(F_STR_END_WITH));
		RulpUtil.addFrameObject(frame, new XRFactorStrSubStr(F_STR_SUBSTR));
		RulpUtil.addFrameObject(frame, new XRFactorStrLength(F_STR_LENGTH));
		RulpUtil.addFrameObject(frame, new XRFactorStrIndexOf(F_STR_INDEX_OF));
		RulpUtil.addFrameObject(frame, new XRFactorStrLastIndexOf(F_STR_LAST_INDEX_OF));
		RulpUtil.addFrameObject(frame, new XRFactorStrFormat(F_STR_FORMAT));
		RulpUtil.addFrameObject(frame, new XRFactorStrCharAt(F_STR_CHAR_AT));
		RulpUtil.addFrameObject(frame, new XRFactorStrSplit(F_STR_SPLIT));
		RulpUtil.addFrameObject(frame, new XRFactorStrReplace(F_STR_REPLACE));
		RulpUtil.addFrameObject(frame, new XRFactorStrUpper(F_STR_UPPER));
//		RulpUtil.addFrameObject(frame, new XRFactorStrSplitLines(F_STR_SPLIT_LINE));

		// Iterator
		RulpUtil.addFrameObject(frame, new XRFactorMakeListIterator(F_MAKE_LIST_ITERATOR));
		RulpUtil.addFrameObject(frame, new XRFactorHasNext(F_HAS_NEXT));
		RulpUtil.addFrameObject(frame, new XRFactorNext(F_NEXT));

		// Arithmetic
		RulpUtil.addFrameObject(frame, new XRFactorArithmetic(F_O_ADD, RArithmeticOperator.ADD));
		RulpUtil.addFrameObject(frame, new XRFactorArithmetic(F_O_SUB, RArithmeticOperator.SUB));
		RulpUtil.addFrameObject(frame, new XRFactorArithmetic(F_O_BY, RArithmeticOperator.BY));
		RulpUtil.addFrameObject(frame, new XRFactorArithmetic(F_O_DIV, RArithmeticOperator.DIV));
		RulpUtil.addFrameObject(frame, new XRFactorArithmetic(F_O_MOD, RArithmeticOperator.MOD));
		RulpUtil.addFrameObject(frame, new XRFactorArithmetic(F_O_POWER, RArithmeticOperator.POWER));
		RulpUtil.addFrameObject(frame, new XRFactorArithmetic(F_O_AND, RArithmeticOperator.AND));
		RulpUtil.addFrameObject(frame, new XRFactorArithmetic(F_O_OR, RArithmeticOperator.OR));
		RulpUtil.addFrameObject(frame, new XRFactorArithmetic(F_O_XOR, RArithmeticOperator.XOR));
		RulpUtil.addFrameObject(frame, new XRFactorBitNot(F_O_NOT));

		// Boolean
		RulpUtil.addFrameObject(frame, new XRFactorBoolNot(F_B_NOT));
		RulpUtil.addFrameObject(frame, new XRFactorBoolAnd(F_B_AND));
		RulpUtil.addFrameObject(frame, new XRFactorBoolOr(F_B_OR));

		// Relational
		RulpUtil.addFrameObject(frame, new XRFactorComparison(F_O_EQ, RRelationalOperator.EQ)); // =
		RulpUtil.addFrameObject(frame, new XRFactorComparison(F_O_NE, RRelationalOperator.NE)); // !=
		RulpUtil.addFrameObject(frame, new XRFactorComparison(F_O_GT, RRelationalOperator.GT)); // >
		RulpUtil.addFrameObject(frame, new XRFactorComparison(F_O_LT, RRelationalOperator.LT)); // <
		RulpUtil.addFrameObject(frame, new XRFactorComparison(F_O_GE, RRelationalOperator.GE)); // >=
		RulpUtil.addFrameObject(frame, new XRFactorComparison(F_O_LE, RRelationalOperator.LE));// <=

		RulpUtil.addFrameObject(frame, new XRFactorCompare(F_CMP_DEF));
		RulpUtil.addFrameObject(frame, new XRFactorCanCast(F_CAN_CAST));

		// Macro & Function
		RulpUtil.addFrameObject(frame, new XRFactorDefMacro(F_DEFMACRO));
		RulpUtil.addFrameObject(frame, new XRFactorDefun(F_DEFUN));
		RulpUtil.addFrameObject(frame, new XRFactorDefTemplate(F_DEFTEMPLATE));
		RulpUtil.addFrameObject(frame, new XRFactorOptStatus(F_OPT_STATUS));
		RulpUtil.addFrameObject(frame, new XRFactorListFunctionInReturn(F_LIST_FUNC_IN_RETURN));
		RulpUtil.addFrameObject(frame, new XRFactorIsStable(F_IS_STABLE));
		RulpUtil.addFrameObject(frame, new XRFactorIsThreadSafe(F_IS_THREAD_SAFE));
		RulpUtil.addFrameObject(frame, new XFactorReduct(F_Reduct));
		RulpUtil.addFrameObject(frame, new XRFactorCC(F_CC));

		// Lambda
		RulpUtil.addFrameObject(frame, new XRFactorLet(F_LET));
		RulpUtil.addFrameObject(frame, new XRFactorLambda(F_LAMBDA));

		// Control
		RulpUtil.addFrameObject(frame, new XRFactorIf(F_IF));
		RulpUtil.addFrameObject(frame, new XRFactorWhen(F_WHEN));
		RulpUtil.addFrameObject(frame, new XRFactorLoop(F_LOOP));
		RulpUtil.addFrameObject(frame, new XRFactorReturn(F_RETURN));
		RulpUtil.addFrameObject(frame, new XRFactorContinue(F_CONTINUE));
		RulpUtil.addFrameObject(frame, new XRFactorBreak(F_BREAK));
		RulpUtil.addFrameObject(frame, new XRFactorDo(A_DO));
		RulpUtil.addFrameObject(frame, new XRFactorCase(F_CASE));

		// Exception
		RulpUtil.addFrameObject(frame, new XRFactorThrow(F_THROW));
		RulpUtil.addFrameObject(frame, new XRFactorErrorValue(F_E_ERROR_VALUE));
		RulpUtil.addFrameObject(frame, new XRFactorTry(F_TRY));

		// Collection
		RulpUtil.addFrameObject(frame, new XRFactorForeach(F_FOREACH));
		RulpUtil.addFrameObject(frame, new XRFactorGetOfList(F_GET_OF_LIST));
		// RulpUtil.addFrameObject(rootFrame, new XRFactorAddToList(F_ADD_TO_LIST));
		RulpUtil.addFrameObject(frame, new XRFactorAddListToList(F_ADD_LIST_TO_LIST));
		RulpUtil.addFrameObject(frame, new XRFactorRemoveAllList(F_REMOVE_ALL_LIST));
		RulpUtil.addFrameObject(frame, new XRFactorJoin(F_JOIN));
		RulpUtil.addFrameObject(frame, new XRFactorUnion(F_UNION));
		RulpUtil.addFrameObject(frame, new XRFactorUniq(F_UNIQ));
		RulpUtil.addFrameObject(frame, new XRFactorSort(F_SORT_LIST));
		RulpUtil.addFrameObject(frame, new XRFactorReverse(F_REVERSE));
		RulpUtil.addFrameObject(frame, new XRFactorGetOfArray(F_GET_OF_ARRAY));
		RulpUtil.addFrameObject(frame, new XRFactorToNamedList(F_TO_NAMED_LIST));
		RulpUtil.addFrameObject(frame, new XRFactorToNoNamedList(F_TO_NONAMED_LIST));
		RulpUtil.addFrameObject(frame, new XRFactorAddListToArray(F_ADD_LIST_TO_ARRAY));
		RulpUtil.addFrameObject(frame, new XRFactorAddArrayToList(F_ADD_ARRAY_TO_LIST));
		RulpUtil.addFrameObject(frame, new XRFactorSeta(F_SETA));
		RulpUtil.addFrameObject(frame, new XRFactorToVary(F_TO_VARY));
		RulpUtil.addFrameObject(frame, new XRFactorToConst(F_TO_CONST));

		// Thread
		RulpUtil.addFrameObject(frame, new XRFactorSleep(F_SLEEP));
		RulpUtil.addFrameObject(frame, new XRFactorDoParallel(F_DO_Parallel));

		// Time
		RulpUtil.addFrameObject(frame, new XRFactorDate(F_DATE));
		RulpUtil.addFrameObject(frame, new XRFactorDayNumber(F_DAY_NUMBER));
		RulpUtil.addFrameObject(frame, new XRFactorSystemTime(F_SYS_TIME));
		RulpUtil.addFrameObject(frame, new XFactorDayOfWeek(F_DAY_OF_WEEK));

		// Math
		RulpUtil.addFrameObject(frame, new XRFactorRandom(F_RANDOM));
		RulpUtil.addFrameObject(frame, new XRFactorRandomInt(F_RANDOM_INT));
		RulpUtil.addFrameObject(frame, new XRFactorRandomFloat(F_RANDOM_FLOAT));
		RulpUtil.addFrameObject(frame, new XRFactorRandom(F_RANDOM_DOUBLE));

		// System
		RulpUtil.addFrameObject(frame, new XRFactorSystemGC(F_SYS_GC));
		RulpUtil.addFrameObject(frame, new XRFactorSystemTotalMemory(F_SYS_TOTAL_MEMORY));
		RulpUtil.addFrameObject(frame, new XRFactorSystemFreeMemory(F_SYS_FREE_MEMORY));
		RulpUtil.addFrameObject(frame, new XRFactorSystemOSType(F_SYS_OS_TYPE));
		RulpUtil.addFrameObject(frame, new XRFactorSystemSetProperty(F_SYS_SET_PROP));

		// Runtime
		RulpUtil.addFrameObject(frame, new XRFactorRuntimeCallCount(F_RUNTIME_CALL_COUNT));

		// Debugger
		RulpUtil.addFrameObject(frame, new XRFactorStrdbg(F_STRDBG));
		RulpUtil.addFrameObject(frame, new XRFactorEnddbg(F_ENDDBG));
		RulpUtil.addFrameObject(frame, new XRFactorIsDebugActive(F_IS_DEBUG_ACTIBE));

		// Rulp Utils
		RulpUtil.addFrameObject(frame, new XRFactorRulpObjectCount(F_RULP_OBJ_COUNT));

	}

}
