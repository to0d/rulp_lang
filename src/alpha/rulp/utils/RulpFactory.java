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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRArray;
import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRBoolean;
import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRDouble;
import alpha.rulp.lang.IRError;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFloat;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.IRInteger;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRLong;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRNative;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.IRString;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRFunctionList;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRMacro;
import alpha.rulp.runtime.IRObjectLoader;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.runtime.IRThreadContext;
import alpha.rulp.runtime.IRTokener;
import alpha.rulp.runtime.RName;
import alpha.rulp.ximpl.collection.XRFactorAdd;
import alpha.rulp.ximpl.collection.XRFactorAddAll;
import alpha.rulp.ximpl.collection.XRFactorForeach;
import alpha.rulp.ximpl.collection.XRFactorGetOfArray;
import alpha.rulp.ximpl.collection.XRFactorGetOfList;
import alpha.rulp.ximpl.collection.XRFactorJoin;
import alpha.rulp.ximpl.collection.XRFactorRemove;
import alpha.rulp.ximpl.collection.XRFactorReverse;
import alpha.rulp.ximpl.collection.XRFactorSizeOfArray;
import alpha.rulp.ximpl.collection.XRFactorSizeOfList;
import alpha.rulp.ximpl.collection.XRFactorSort;
import alpha.rulp.ximpl.collection.XRFactorUnion;
import alpha.rulp.ximpl.collection.XRFactorUniq;
import alpha.rulp.ximpl.collection.XRMap;
import alpha.rulp.ximpl.collection.XRSet;
import alpha.rulp.ximpl.error.XRError;
import alpha.rulp.ximpl.error.XRFactorBreak;
import alpha.rulp.ximpl.error.XRFactorContinue;
import alpha.rulp.ximpl.error.XRFactorError;
import alpha.rulp.ximpl.error.XRFactorErrorValue;
import alpha.rulp.ximpl.error.XRFactorReturn;
import alpha.rulp.ximpl.error.XRFactorTry;
import alpha.rulp.ximpl.factor.XRFactorAlias;
import alpha.rulp.ximpl.factor.XRFactorArithmetic;
import alpha.rulp.ximpl.factor.XRFactorArithmetic.ArithmeticType;
import alpha.rulp.ximpl.factor.XRFactorBoolAnd;
import alpha.rulp.ximpl.factor.XRFactorBoolNot;
import alpha.rulp.ximpl.factor.XRFactorBoolOr;
import alpha.rulp.ximpl.factor.XRFactorCanCast;
import alpha.rulp.ximpl.factor.XRFactorCompare;
import alpha.rulp.ximpl.factor.XRFactorComparison;
import alpha.rulp.ximpl.factor.XRFactorComparison.ComparisonType;
import alpha.rulp.ximpl.factor.XRFactorDefvar;
import alpha.rulp.ximpl.factor.XRFactorDo;
import alpha.rulp.ximpl.factor.XRFactorDoWhenObjDeleted;
import alpha.rulp.ximpl.factor.XRFactorDoWhenVarChanged;
import alpha.rulp.ximpl.factor.XRFactorEqual;
import alpha.rulp.ximpl.factor.XRFactorIf;
import alpha.rulp.ximpl.factor.XRFactorLet;
import alpha.rulp.ximpl.factor.XRFactorLoop;
import alpha.rulp.ximpl.factor.XRFactorNameOf;
import alpha.rulp.ximpl.factor.XRFactorRef;
import alpha.rulp.ximpl.factor.XRFactorSetq;
import alpha.rulp.ximpl.factor.XRFactorToAtom;
import alpha.rulp.ximpl.factor.XRFactorToInteger;
import alpha.rulp.ximpl.factor.XRFactorToString;
import alpha.rulp.ximpl.factor.XRFactorTypeOf;
import alpha.rulp.ximpl.factor.XRFactorValueOf;
import alpha.rulp.ximpl.factor.XRFactorValueTypeOf;
import alpha.rulp.ximpl.factor.XRFactorWhen;
import alpha.rulp.ximpl.io.XRFactorLoad;
import alpha.rulp.ximpl.io.XRFactorOpenTxtFile;
import alpha.rulp.ximpl.io.XRFactorOutToFile;
import alpha.rulp.ximpl.io.XRFactorPrint;
import alpha.rulp.ximpl.io.XRFactorPrintSubject;
import alpha.rulp.ximpl.io.XRFactorSaveTxtFile;
import alpha.rulp.ximpl.lang.XRArray;
import alpha.rulp.ximpl.lang.XRAtom;
import alpha.rulp.ximpl.lang.XRBoolean;
import alpha.rulp.ximpl.lang.XRDouble;
import alpha.rulp.ximpl.lang.XRFloat;
import alpha.rulp.ximpl.lang.XRInteger;
import alpha.rulp.ximpl.lang.XRIteratorAdatper;
import alpha.rulp.ximpl.lang.XRListArray;
import alpha.rulp.ximpl.lang.XRListBuilderIterator;
import alpha.rulp.ximpl.lang.XRListEmpty;
import alpha.rulp.ximpl.lang.XRListIterator;
import alpha.rulp.ximpl.lang.XRListIteratorR;
import alpha.rulp.ximpl.lang.XRListList;
import alpha.rulp.ximpl.lang.XRLong;
import alpha.rulp.ximpl.lang.XRMacro;
import alpha.rulp.ximpl.lang.XRNative;
import alpha.rulp.ximpl.lang.XRString;
import alpha.rulp.ximpl.lang.XRVar;
import alpha.rulp.ximpl.network.XRSocket;
import alpha.rulp.ximpl.optimize.XRFactorMakeCPS;
import alpha.rulp.ximpl.rclass.XRDefClass;
import alpha.rulp.ximpl.rclass.XRDefInstance;
import alpha.rulp.ximpl.rclass.XRFactorAccess;
import alpha.rulp.ximpl.rclass.XRFactorClassOf;
import alpha.rulp.ximpl.rclass.XRFactorDefClass;
import alpha.rulp.ximpl.rclass.XRFactorDelete;
import alpha.rulp.ximpl.rclass.XRFactorGetMbr;
import alpha.rulp.ximpl.rclass.XRFactorLs;
import alpha.rulp.ximpl.rclass.XRFactorNew;
import alpha.rulp.ximpl.rclass.XRFactorPropertyOf;
import alpha.rulp.ximpl.rclass.XRFactorUse;
import alpha.rulp.ximpl.rclass.XRMember;
import alpha.rulp.ximpl.rclass.XRNoClass;
import alpha.rulp.ximpl.rclass.XRSubjectFrame;
import alpha.rulp.ximpl.runtime.XRFactorDefmacro;
import alpha.rulp.ximpl.runtime.XRFactorDefun;
import alpha.rulp.ximpl.runtime.XRFactorLambda;
import alpha.rulp.ximpl.runtime.XRFactorRulpObjectCount;
import alpha.rulp.ximpl.runtime.XRFrame;
import alpha.rulp.ximpl.runtime.XRFrameEntry;
import alpha.rulp.ximpl.runtime.XRFrameProtected;
import alpha.rulp.ximpl.runtime.XRFunction;
import alpha.rulp.ximpl.runtime.XRFunctionLambda;
import alpha.rulp.ximpl.runtime.XRFunctionList;
import alpha.rulp.ximpl.runtime.XRInterpreter;
import alpha.rulp.ximpl.runtime.XRParaAttr;
import alpha.rulp.ximpl.runtime.XRParser;
import alpha.rulp.ximpl.runtime.XRThreadContext;
import alpha.rulp.ximpl.runtime.XRTokener;
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
import alpha.rulp.ximpl.string.XRFactorStrStartsWith;
import alpha.rulp.ximpl.string.XRFactorStrSubStr;
import alpha.rulp.ximpl.string.XRFactorStrTrim;
import alpha.rulp.ximpl.string.XRFactorStrTrimHead;
import alpha.rulp.ximpl.string.XRFactorStrTrimTail;
import alpha.rulp.ximpl.system.XRFactorDate;
import alpha.rulp.ximpl.system.XRFactorSystemGC;
import alpha.rulp.ximpl.system.XRFactorSystemTime;
import alpha.rulp.ximpl.thread.XRFactorDoParallel;
import alpha.rulp.ximpl.thread.XRFactorSleep;

public final class RulpFactory {

	static class IntegerCache {

		static IRInteger[] cache;

		static final int high = 127;

		static final int low = -128;

		static {

			cache = new IRInteger[(high - low) + 1];

			int j = low;
			for (int k = 0; k < cache.length; k++) {
				cache[k] = new XRInteger(j++);
			}
		}
	}

	public static final IRExpr EMPTY_EXPR;

	public static final IRList EMPTY_LIST;

	private static final IRString EMPTY_STR = new XRString("");

	private static final XRBoolean False = new XRBoolean(false);

	private static final int FRAME_POOL_1_MAX = 256;

	private static AtomicInteger frameEntryDefaultMaxId = new AtomicInteger(0);

	private static AtomicInteger frameEntryProtectedMaxId = new AtomicInteger(0);

	private static AtomicInteger frameUnNameCount = new AtomicInteger(0);

	private static int globalFrameMaxId = I_FRAME_ID_MIN;

	private static XRFrame[] globalFramePool1 = new XRFrame[FRAME_POOL_1_MAX];

	private static ArrayList<XRFrame> globalFramePool2 = new ArrayList<>();

	private static LinkedList<Integer> globalFreeFrameIdList = new LinkedList<>();

	private static AtomicInteger interpreterCount = new AtomicInteger(0);

	private static AtomicInteger lambdaCount = new AtomicInteger(0);

	private static final Set<Class<? extends IRObjectLoader>> loaderClasses = new HashSet<>();

	private static final List<IRObjectLoader> rulpLoaders = new LinkedList<>();

	private static final XRBoolean True = new XRBoolean(true);

	static {
		EMPTY_LIST = new XRListList(Collections.<IRObject>emptyList(), RType.LIST, null, false);
		EMPTY_EXPR = new XRListList(Collections.<IRObject>emptyList(), RType.EXPR, null, false);
	}

	private static synchronized void _allocateFrameId(XRFrame frame) throws RException {

		int nextFrameId = -1;

		if (!globalFreeFrameIdList.isEmpty()) {
			nextFrameId = globalFreeFrameIdList.pollLast();
		} else {
			nextFrameId = globalFrameMaxId++;
		}

		/*********************************************/
		// Allocate from pool1
		/*********************************************/
		if (nextFrameId < FRAME_POOL_1_MAX) {

			if (globalFramePool1[nextFrameId] != null) {
				throw new RException("Global frame pool 1 is not clear at: " + nextFrameId);
			}

			globalFramePool1[nextFrameId] = frame;

		}
		/*********************************************/
		// Allocate from pool2
		/*********************************************/
		else {

			int pool2Index = nextFrameId - FRAME_POOL_1_MAX;

			if (pool2Index < globalFramePool2.size()) {

				if (globalFramePool2.get(pool2Index) != null) {
					throw new RException("Global frame pool 2 is not clear at: " + nextFrameId);
				}

				globalFramePool2.set(pool2Index, frame);

			} else {

				while (globalFramePool2.size() < pool2Index) {
					globalFramePool2.add(null);
				}

				globalFramePool2.add(frame);
			}
		}

		frame.setFrameId(nextFrameId);
		RType.FRAME.incCreateCount();
	}

	public static IRArray createArray(List<? extends IRObject> elements) {

		RType.ARRAY.incCreateCount();
		return XRArray.buildArray(elements);
	}

	public static IRAtom createAtom(RName rname) {
		RType.ATOM.incCreateCount();
		return new XRAtom(rname.fullName, rname);
	}

	public static IRAtom createAtom(String name) {
		RType.ATOM.incCreateCount();
		return new XRAtom(name);
	}

	public static IRBoolean createBoolean(boolean value) {
		return value ? True : False;
	}

	public static IRClass createClassDefClass(String className, IRFrame definedFrame, IRClass superClass) {
		RType.CLASS.incCreateCount();
		return new XRDefClass(className, definedFrame, superClass);
	}

	public static IRDouble createDouble(double value) {
		RType.DOUBLE.incCreateCount();
		return new XRDouble(value);
	}

	public static IRError createError(IRInterpreter interpreter, IRAtom id, IRObject value) throws RException {
		return new XRError(RuntimeUtil.getNoClass(interpreter), id, value);
	}

	public static IRExpr createExpression() {
		return EMPTY_EXPR;
	}

	public static IRExpr createExpression(IRIterator<? extends IRObject> iter) throws RException {

		if (iter == null || !iter.hasNext()) {
			return EMPTY_EXPR;
		}

		RType.EXPR.incCreateCount();
		return new XRListIteratorR(iter, RType.EXPR, null);
	}

	public static IRExpr createExpression(IRObject... elements) {
		RType.EXPR.incCreateCount();
		return new XRListArray(elements, RType.EXPR, null);
	}

	public static IRExpr createExpression(Iterator<? extends IRObject> iter) {

		if (iter == null || !iter.hasNext()) {
			return EMPTY_EXPR;
		}

		RType.EXPR.incCreateCount();
		return new XRListIterator(iter, RType.EXPR, null);
	}

	public static IRExpr createExpression(List<? extends IRObject> list) {

		if (list == null || list.isEmpty()) {
			return EMPTY_EXPR;
		}

		RType.EXPR.incCreateCount();
		return new XRListList(list, RType.EXPR, null, false);
	}

	public static IRExpr createExpressionEarly(List<? extends IRObject> list) {

		if (list == null || list.isEmpty()) {
			return EMPTY_EXPR;
		}

		RType.EXPR.incCreateCount();
		return new XRListList(list, RType.EXPR, null, true);
	}

	public static IRFloat createFloat(float value) {
		RType.FLOAT.incCreateCount();
		return new XRFloat(value);
	}

	public static IRFrame createFrame(IRFrame parentFrame, String name) throws RException {

		if (name == null) {
			name = String.format("frame-%d", frameUnNameCount.getAndIncrement());
		}

		XRFrame frame = new XRFrame();
		frame.setParentFrame(parentFrame);
		frame.setFrameName(name);
		frame.setThreadContext(parentFrame.getThreadContext());

		_allocateFrameId(frame);

		return frame;
	}

	public static IRFrameEntry createFrameEntry(IRFrame frame, String name, IRObject object) {
		int frameId = frameEntryDefaultMaxId.incrementAndGet();
		return new XRFrameEntry(frameId, frame, name, object);
	}

	public static IRFrameEntry createFrameEntryProtected(IRFrame frame, String name, IRObject object) {
		int frameId = -frameEntryProtectedMaxId.incrementAndGet();
		return new XRFrameEntry(frameId, frame, name, object);
	}

	public static IRFrame createFrameSubject(IRSubject subject, IRFrame parentFrame) throws RException {

		XRSubjectFrame frame = new XRSubjectFrame();
		frame.setParentFrame(parentFrame);
		frame.setSubject(subject);
		frame.setFrameName(String.format("subject-%d", frameUnNameCount.getAndIncrement()));
		frame.setThreadContext(parentFrame.getThreadContext());

		_allocateFrameId(frame);

		return frame;
	}

	public static IRFunction createFunction(IRFrame defineFrame, String funName, List<IRParaAttr> paraAttrs,
			IRExpr funBody, String description) {

		RType.FUNC.incCreateCount();
		return new XRFunction(defineFrame, funName, paraAttrs, funBody, description);
	}

	public static IRFunction createFunctionLambda(IRFunction func, IRFrame definedFrame) throws RException {
		RType.FUNC.incCreateCount();
		return new XRFunctionLambda(func, definedFrame, lambdaCount.getAndIncrement());
	}

	public static IRFunctionList createFunctionList(String funName) {
		RType.FUNC.incCreateCount();
		return new XRFunctionList(funName);
	}

	public static IRInstance createInstanceOfDefault(IRClass rClass, String instanceName, IRFrame definedFrame) {
		RType.INSTANCE.incCreateCount();
		return new XRDefInstance(rClass, instanceName, definedFrame);
	}

	public static IRInstance createInstanceOfMap(IRInterpreter interpreter) throws RException {
		RType.INSTANCE.incCreateCount();
		return new XRMap(RuntimeUtil.getNoClass(interpreter));
	}

	public static IRInstance createInstanceOfSet(IRInterpreter interpreter) throws RException {
		RType.INSTANCE.incCreateCount();
		return new XRSet(RuntimeUtil.getNoClass(interpreter));
	}

	public static IRInstance createInstanceOfSocket(String addr, int port) {
		RType.INSTANCE.incCreateCount();
		return new XRSocket(addr, port);
	}

	public static IRInteger createInteger(int i) {

		if (i >= IntegerCache.low && i <= IntegerCache.high) {
			return IntegerCache.cache[i + (-IntegerCache.low)];
		}

		RType.INT.incCreateCount();
		return new XRInteger(i);
	}

	public static IRInterpreter createInterpreter() throws RException, IOException {

		interpreterCount.incrementAndGet();

		/******************************************************/
		// Root Frame
		/******************************************************/
		XRFrame rootFrame = new XRFrameProtected();
		rootFrame.setFrameId(I_FRAME_ROOT_ID);
		rootFrame.setFrameName(A_ROOT);
		rootFrame.setEntry(A_ROOT, rootFrame);

		/******************************************************/
		// System Frame
		/******************************************************/
		XRFrameProtected systemFrame = new XRFrameProtected();
		systemFrame.setFrameId(I_FRAME_SYSTEM_ID);
		systemFrame.setFrameName(A_SYSTEM);
		systemFrame.setParentFrame(rootFrame);
		systemFrame.setEntry(A_ROOT, rootFrame);
		systemFrame.setEntry(A_SYSTEM, systemFrame);

		/******************************************************/
		// Main Frame
		/******************************************************/
		XRFrame mainFrame = new XRFrame();
		mainFrame.setFrameId(I_FRAME_MAIN_ID);
		mainFrame.setFrameName(A_MAIN);
		mainFrame.setParentFrame(systemFrame);
		mainFrame.setEntry(A_ROOT, rootFrame);
		mainFrame.setEntry(A_SYSTEM, systemFrame);
		mainFrame.setEntry(A_MAIN, mainFrame);

		/******************************************************/
		// Main Frame
		/******************************************************/
		XRInterpreter interpreter = new XRInterpreter();
		interpreter.setMainFrame(mainFrame);

		/******************************************************/
		// Load base object
		/******************************************************/

		// Objects
		RulpUtil.addFrameObject(rootFrame, O_Nil);
		RulpUtil.addFrameObject(rootFrame, O_True);
		RulpUtil.addFrameObject(rootFrame, O_False);
		RulpUtil.addFrameObject(rootFrame, O_Nan);
		RulpUtil.addFrameObject(rootFrame, O_Public);
		RulpUtil.addFrameObject(rootFrame, O_Private);
		RulpUtil.addFrameObject(rootFrame, O_Default);
		RulpUtil.addFrameObject(rootFrame, O_Final);
		RulpUtil.addFrameObject(rootFrame, O_Static);
		RulpUtil.addFrameObject(rootFrame, O_From);

		RulpUtil.addFrameObject(rootFrame, T_Null);
		RulpUtil.addFrameObject(rootFrame, T_Atom);
		RulpUtil.addFrameObject(rootFrame, T_Bool);
		RulpUtil.addFrameObject(rootFrame, T_Int);
		RulpUtil.addFrameObject(rootFrame, T_Int);
		RulpUtil.addFrameObject(rootFrame, T_Long);
		RulpUtil.addFrameObject(rootFrame, T_Float);
		RulpUtil.addFrameObject(rootFrame, T_Double);
		RulpUtil.addFrameObject(rootFrame, T_String);
		RulpUtil.addFrameObject(rootFrame, T_List);
		RulpUtil.addFrameObject(rootFrame, T_Expr);
		RulpUtil.addFrameObject(rootFrame, T_Array);
		RulpUtil.addFrameObject(rootFrame, T_Var);
		RulpUtil.addFrameObject(rootFrame, T_Factor);
		RulpUtil.addFrameObject(rootFrame, T_Func);
		RulpUtil.addFrameObject(rootFrame, T_Macro);
		RulpUtil.addFrameObject(rootFrame, T_Instance);
		RulpUtil.addFrameObject(rootFrame, T_Class);
		RulpUtil.addFrameObject(rootFrame, T_Native);
		RulpUtil.addFrameObject(rootFrame, T_Member);
		RulpUtil.addFrameObject(rootFrame, T_Frame);

		RuntimeUtil.init(rootFrame);
		TraceUtil.init(rootFrame);

		RulpUtil.addFrameObject(rootFrame, new XRFactorNameOf(F_NAME_OF));
		RulpUtil.addFrameObject(rootFrame, new XRFactorTypeOf(F_TYPE_OF));
		RulpUtil.addFrameObject(rootFrame, new XRFactorValueOf(F_VALUE_OF));
		RulpUtil.addFrameObject(rootFrame, new XRFactorValueTypeOf(F_VALUE_TYPE_OF));
		RulpUtil.addFrameObject(rootFrame, new XRFactorAlias(F_ALIAS));
		RulpUtil.addFrameObject(rootFrame, new XRFactorEqual(F_EQUAL_DEFAULT));
		RulpUtil.addFrameObject(rootFrame, new XRFactorToAtom(F_TO_ATOM));
		RulpUtil.addFrameObject(rootFrame, new XRFactorSizeOfList(F_SIZE_OF_LIST));
		RulpUtil.addFrameObject(rootFrame, new XRFactorSizeOfArray(F_SIZE_OF_ARRAY));

		// Class
		RulpUtil.addFrameObject(rootFrame, new XRNoClass(A_NOCLASS, rootFrame));
		RulpUtil.addFrameObject(rootFrame, new XRFactorClassOf(F_CLASS_OF));
		RulpUtil.addFrameObject(rootFrame, new XRFactorPropertyOf(F_PROPERTY_OF));
		RulpUtil.addFrameObject(rootFrame, new XRFactorAccess(F_ACCESS));
		RulpUtil.addFrameObject(rootFrame, new XRFactorNew(F_NEW));
		RulpUtil.addFrameObject(rootFrame, new XRFactorDelete(F_DELETE));
		RulpUtil.addFrameObject(rootFrame, new XRFactorDefClass(F_DEFCLASS));
		RulpUtil.addFrameObject(rootFrame, new XRFactorGetMbr(F_O_MBR));
		RulpUtil.addFrameObject(rootFrame, new XRFactorUse(F_USE));
		RulpUtil.addFrameObject(rootFrame, new XRFactorLs(F_LS));

		// IO
		RulpUtil.addFrameObject(rootFrame, new XRFactorPrint(F_PRINT));
		RulpUtil.addFrameObject(rootFrame, new XRFactorLoad(F_LOAD));
		RulpUtil.addFrameObject(rootFrame, new XRFactorOutToFile(F_OUT_TO_FILE));
		RulpUtil.addFrameObject(rootFrame, new XRFactorOpenTxtFile(F_OPEN_TXT_FILE));
		RulpUtil.addFrameObject(rootFrame, new XRFactorSaveTxtFile(F_SAVE_TXT_FILE));
		RulpUtil.addFrameObject(rootFrame, new XRFactorPrintSubject(F_PRINT_SUBJECT));

		// Variable, Value & Expression
		RulpUtil.addFrameObject(rootFrame, new XRFactorDefvar(F_DEFVAR, true, false));
		RulpUtil.addFrameObject(rootFrame, new XRFactorSetq(F_SETQ));
		RulpUtil.addFrameObject(rootFrame, new XRFactorDoWhenVarChanged(F_DO_WHEN_VAR_CHANGED));
		RulpUtil.addFrameObject(rootFrame, new XRFactorDoWhenObjDeleted(F_DO_WHEN_OBJ_DELETED));
		RulpUtil.addFrameObject(rootFrame, new XRFactorRef(F_REF));

		// String
		RulpUtil.addFrameObject(rootFrame, new XRFactorToString(F_TO_STRING));
		RulpUtil.addFrameObject(rootFrame, new XRFactorToInteger(F_TO_INT));
		RulpUtil.addFrameObject(rootFrame, new XRFactorStrCat(F_STRCAT));
		RulpUtil.addFrameObject(rootFrame, new XRFactorStrEqual(F_STR_EQUAL));
		RulpUtil.addFrameObject(rootFrame, new XRFactorStrEqualNoCase(F_STR_EQUAL_NOCASE));
		RulpUtil.addFrameObject(rootFrame, new XRFactorStrTrim(F_STR_TRIM));
		RulpUtil.addFrameObject(rootFrame, new XRFactorStrTrimHead(F_STR_TRIM_HEAD));
		RulpUtil.addFrameObject(rootFrame, new XRFactorStrTrimTail(F_STR_TRIM_TAIL));
		RulpUtil.addFrameObject(rootFrame, new XRFactorStrMatch(F_STR_MATCH));
		RulpUtil.addFrameObject(rootFrame, new XRFactorStrStartsWith(F_STR_START_WITH));
		RulpUtil.addFrameObject(rootFrame, new XRFactorStrEndWith(F_STR_END_WITH));
		RulpUtil.addFrameObject(rootFrame, new XRFactorStrSubStr(F_STR_SUBSTR));
		RulpUtil.addFrameObject(rootFrame, new XRFactorStrLength(F_STR_LENGTH));
		RulpUtil.addFrameObject(rootFrame, new XRFactorStrIndexOf(F_STR_INDEX_OF));
		RulpUtil.addFrameObject(rootFrame, new XRFactorStrLastIndexOf(F_STR_LAST_INDEX_OF));
		RulpUtil.addFrameObject(rootFrame, new XRFactorStrFormat(F_STR_FORMAT));
		RulpUtil.addFrameObject(rootFrame, new XRFactorStrCharAt(F_STR_CHAR_AT));

		// Arithmetic
		RulpUtil.addFrameObject(rootFrame, new XRFactorArithmetic(F_O_ADD, ArithmeticType.ADD));
		RulpUtil.addFrameObject(rootFrame, new XRFactorArithmetic(F_O_SUB, ArithmeticType.SUB));
		RulpUtil.addFrameObject(rootFrame, new XRFactorArithmetic(F_O_BY, ArithmeticType.BY));
		RulpUtil.addFrameObject(rootFrame, new XRFactorArithmetic(F_O_DIV, ArithmeticType.DIV));
		RulpUtil.addFrameObject(rootFrame, new XRFactorArithmetic(F_O_MOD, ArithmeticType.MOD));
		RulpUtil.addFrameObject(rootFrame, new XRFactorArithmetic(F_O_POWER, ArithmeticType.POWER));

		// Boolean
		RulpUtil.addFrameObject(rootFrame, new XRFactorBoolNot(F_B_NOT));
		RulpUtil.addFrameObject(rootFrame, new XRFactorBoolAnd(F_B_AND));
		RulpUtil.addFrameObject(rootFrame, new XRFactorBoolOr(F_B_OR));

		// Relational
		RulpUtil.addFrameObject(rootFrame, new XRFactorComparison(F_O_EQ, ComparisonType.Equal)); // =
		RulpUtil.addFrameObject(rootFrame, new XRFactorComparison(F_O_NE, ComparisonType.NotEqual)); // =
		RulpUtil.addFrameObject(rootFrame, new XRFactorComparison(F_O_GT, ComparisonType.Bigger)); // >
		RulpUtil.addFrameObject(rootFrame, new XRFactorComparison(F_O_LT, ComparisonType.Smaller)); // <
		RulpUtil.addFrameObject(rootFrame, new XRFactorComparison(F_O_GE, ComparisonType.BiggerOrEqual)); // >=
		RulpUtil.addFrameObject(rootFrame, new XRFactorComparison(F_O_LE, ComparisonType.SmallerOrEqual));// <=

		RulpUtil.addFrameObject(rootFrame, new XRFactorCompare(F_CMP_DEF));
		RulpUtil.addFrameObject(rootFrame, new XRFactorCanCast(F_CAN_CAST));

		// Macro & Function
		RulpUtil.addFrameObject(rootFrame, new XRFactorDefmacro(F_DEFMACRO));
		RulpUtil.addFrameObject(rootFrame, new XRFactorDefun(F_DEFUN));
		RulpUtil.addFrameObject(rootFrame, new XRFactorMakeCPS(F_MAKE_CPS));

		// Lambda
		RulpUtil.addFrameObject(rootFrame, new XRFactorLet(F_LET));
		RulpUtil.addFrameObject(rootFrame, new XRFactorLambda(F_LAMBDA));

		// Control
		RulpUtil.addFrameObject(rootFrame, new XRFactorIf(F_IF));
		RulpUtil.addFrameObject(rootFrame, new XRFactorWhen(F_WHEN));
		RulpUtil.addFrameObject(rootFrame, new XRFactorLoop(F_LOOP));
		RulpUtil.addFrameObject(rootFrame, new XRFactorReturn(F_RETURN));
		RulpUtil.addFrameObject(rootFrame, new XRFactorContinue(F_CONTINUE));
		RulpUtil.addFrameObject(rootFrame, new XRFactorBreak(F_BREAK));
		RulpUtil.addFrameObject(rootFrame, new XRFactorDo(F_DO));

		// Exception
		RulpUtil.addFrameObject(rootFrame, new XRFactorError(F_E_ERROR));
		RulpUtil.addFrameObject(rootFrame, new XRFactorErrorValue(F_E_ERROR_VALUE));
		RulpUtil.addFrameObject(rootFrame, new XRFactorTry(F_E_TRY));

		// Collection
		RulpUtil.addFrameObject(rootFrame, new XRFactorForeach(F_FOREACH));
		RulpUtil.addFrameObject(rootFrame, new XRFactorGetOfList(F_GET_OF_LIST));
		RulpUtil.addFrameObject(rootFrame, new XRFactorAdd(F_ADD));
		RulpUtil.addFrameObject(rootFrame, new XRFactorAddAll(F_ADD_ALL));
		RulpUtil.addFrameObject(rootFrame, new XRFactorRemove(F_REMOVE));
		RulpUtil.addFrameObject(rootFrame, new XRFactorJoin(F_JOIN));
		RulpUtil.addFrameObject(rootFrame, new XRFactorUnion(F_UNION));
		RulpUtil.addFrameObject(rootFrame, new XRFactorUniq(F_UNIQ));
		RulpUtil.addFrameObject(rootFrame, new XRFactorSort(F_SORT));
		RulpUtil.addFrameObject(rootFrame, new XRFactorReverse(F_REVERSE));
		RulpUtil.addFrameObject(rootFrame, new XRFactorGetOfArray(F_GET_OF_ARRAY));

		// Thread
		RulpUtil.addFrameObject(rootFrame, new XRFactorSleep(F_T_SLEEP));
		RulpUtil.addFrameObject(rootFrame, new XRFactorDoParallel(F_DO_Parallel));

		// Time
		RulpUtil.addFrameObject(rootFrame, new XRFactorDate(F_DATE));

		// System
		RulpUtil.addFrameObject(rootFrame, new XRFactorSystemGC(F_SYS_GC));
		RulpUtil.addFrameObject(rootFrame, new XRFactorSystemTime(F_SYS_TIME));

		// Rulp Utils
		RulpUtil.addFrameObject(rootFrame, new XRFactorRulpObjectCount(F_RULP_OBJ_COUNT));

		// Load base script
		LoadUtil.loadRulpFromJar(interpreter, systemFrame, "alpha/resource/base.rulp", "utf-8");

		// Native Class Initialization
		XRSet.init(interpreter, systemFrame);
		XRMap.init(interpreter, systemFrame);
		XRSocket.init(interpreter, systemFrame);

		/******************************************************/
		// Load JVM object
		/******************************************************/
		JVMUtil.loadJVMVars(interpreter, rootFrame);

		/******************************************************/
		// Load object
		/******************************************************/
		for (IRObjectLoader loader : rulpLoaders) {
			loader.load(interpreter, systemFrame);
		}

		return interpreter;
	}

	public static IRList createList() {
		return EMPTY_LIST;
	}

	public static IRList createList(IRIterator<? extends IRObject> iter) throws RException {

		if (iter == null || !iter.hasNext()) {
			return EMPTY_LIST;
		}

		RType.LIST.incCreateCount();
		return new XRListIteratorR(iter, RType.LIST, null);
	}

	public static IRList createList(IRObject... elements) {

		RType.LIST.incCreateCount();
		return new XRListArray(elements, RType.LIST, null);
	}

	public static IRList createList(Iterator<? extends IRObject> iter) {

		if (iter == null || !iter.hasNext()) {
			return EMPTY_LIST;
		}

		RType.LIST.incCreateCount();
		return new XRListIterator(iter, RType.LIST, null);
	}

	public static IRList createList(List<? extends IRObject> list) {

		if (list == null || list.isEmpty()) {
			return EMPTY_LIST;
		}

		RType.LIST.incCreateCount();
		return new XRListList(list, RType.LIST, null, false);
	}

	public static IRList createListOfString(Collection<String> collection) {

		RType.LIST.incCreateCount();
		return new XRListBuilderIterator<String>(collection.iterator(), (str) -> {
			return RulpFactory.createString(str);

		}, RType.LIST, null);
	}

	public static IRLong createLong(long value) {
		RType.LIST.incCreateCount();
		return new XRLong(value);
	}

	public static IRMacro createMacro(String macroName, List<String> paraNames, IRList macroBody) throws RException {
		RType.MACRO.incCreateCount();
		return new XRMacro(macroName, paraNames, macroBody);
	}

	public static IRMember createMember(IRObject subject, String name, IRObject value) {
		RType.MEMBER.incCreateCount();
		return new XRMember(subject, name, value);
	}

	public static IRList createNamedList(IRIterator<? extends IRObject> iter, String name) throws RException {

		if (name != null && name.length() == 0) {
			return createList(iter);
		}

		if (iter == null || !iter.hasNext()) {
			return createNamedList(name);
		}

		RType.LIST.incCreateCount();
		return new XRListIteratorR(iter, RType.LIST, name);
	}

	public static IRList createNamedList(Iterator<? extends IRObject> iter, String name) {

		if (name != null && name.length() == 0) {
			return createList(iter);
		}

		if (iter == null || !iter.hasNext()) {
			return createNamedList(name);
		}

		RType.LIST.incCreateCount();
		return new XRListIterator(iter, RType.LIST, name);
	}

	public static IRList createNamedList(List<? extends IRObject> list, String name) {

		if (name != null && name.length() == 0) {
			return createList(list);
		}

		if (list == null || list.isEmpty()) {
			return createNamedList(name);
		}

		RType.LIST.incCreateCount();
		return new XRListList(list, RType.LIST, name, false);
	}

	public static IRList createNamedList(String name) {

		if (name == null) {
			return EMPTY_LIST;

		} else {
			RType.LIST.incCreateCount();
			return new XRListEmpty(name);
		}
	}

	public static IRList createNamedList(String name, IRObject... elements) {
		RType.LIST.incCreateCount();
		return new XRListArray(elements, RType.LIST, name);
	}

	public static IRNative createNative(Object obj) {
		RType.NATIVE.incCreateCount();
		return new XRNative(obj);
	}

	public static IRAtom createNil() {
		return new XRAtom(A_NIL) {
			@Override
			public RType getType() {
				return RType.NIL;
			}
		};
	}

	public static IRParaAttr createParaAttr(String paraName) {
		return createParaAttr(paraName, O_Nil);
	}

	public static IRParaAttr createParaAttr(String paraName, IRAtom paraType) {

		if (paraType == null) {
			paraType = O_Nil;
		}

		return new XRParaAttr(paraName, paraType);
	}

	public static IRParser createParser() {
		return new XRParser(createTokener());
	}

	public static <T> IRIterator<T> createRIterator(Iterator<T> iter) throws RException {
		return new XRIteratorAdatper<T>(iter);
	}

//	public static IRClass createNoClass() {
//		IRClass noClass = new XRDefClass(A_NOCLASS);
//		try {
//			RulpUtil.incRef(noClass);
//		} catch (RException e) {
//			e.printStackTrace();
//		}
//		return noClass;
//	}

	public static IRString createString() {
		return EMPTY_STR;
	}

	public static IRString createString(String value) {

		if (value == null) {
			return null;
		}

		if (value.length() == 0) {
			return EMPTY_STR;
		}

		RType.STRING.incCreateCount();
		return new XRString(value);
	}

	public static IRThreadContext createThreadContext() {
		return new XRThreadContext();
	}

	public static IRTokener createTokener() {
		return new XRTokener();
	}

	public static IRVar createVar(String name) {
		RType.VAR.incCreateCount();
		return new XRVar(name);
	}

	public static IRVar createVar(String name, IRObject newVal) throws RException {
		IRVar var = createVar(name);
		var.setValue(newVal);
		return var;
	}

	public static synchronized void freeFrameId(int frameId) throws RException {

		/*********************************************/
		// free to pool1
		/*********************************************/
		if (frameId < FRAME_POOL_1_MAX) {

			if (globalFramePool1[frameId] == null) {
				throw new RException("Frame not found in pool1: " + frameId);
			}

			globalFramePool1[frameId] = null;

		}
		/*********************************************/
		// free to pool2
		/*********************************************/
		else {

			int pool2Index = frameId - FRAME_POOL_1_MAX;

			if (pool2Index >= globalFramePool2.size()) {
				throw new RException("Invalid frame id: " + frameId);
			}

			if (globalFramePool2.get(pool2Index) == null) {
				throw new RException("Frame id not found: " + frameId);
			}

			globalFramePool2.set(pool2Index, null);

		}

		globalFreeFrameIdList.push(frameId);
	}

	public static int getFrameEntryCreateCount() {
		return frameEntryProtectedMaxId.get() + frameEntryDefaultMaxId.get();
	}

	public static int getFrameFreeIdCount() {
		return globalFreeFrameIdList.size();
	}

	public static int getFrameMaxId() {
		return globalFrameMaxId;
	}

	public static int getInterpreterCount() {
		return interpreterCount.get();
	}

	public static int getLambdaCount() {
		return lambdaCount.get();
	}

	public static int getObjectCreateCount(RType type) {
		return type.getCreateCount();
	}

	public static int getObjectDeleteCount(RType type) {
		return type.getDeleteCount();
	}

	public static synchronized List<IRFrame> listGlobalFrames() {

		List<IRFrame> frames = new LinkedList<>();

		for (XRFrame frame : globalFramePool1) {
			if (frame != null) {
				frames.add(frame);
			}
		}

		for (XRFrame frame : globalFramePool2) {
			if (frame != null) {
				frames.add(frame);
			}
		}

		return frames;
	}

	public static void registerLoader(Class<? extends IRObjectLoader> loaderClass) {

		if (loaderClasses.contains(loaderClass)) {
			return;
		}

		try {
			IRObjectLoader loader = loaderClass.newInstance();
			rulpLoaders.add(loader);
			loaderClasses.add(loaderClass);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	public static void reset() {

		for (RType type : RType.ALL_RTYPE) {
			type.reset();
		}

		globalFrameMaxId = I_FRAME_ID_MIN;

		globalFramePool1 = new XRFrame[FRAME_POOL_1_MAX];
		globalFramePool2.clear();
		globalFreeFrameIdList.clear();

		frameUnNameCount.set(0);
		frameEntryDefaultMaxId.set(0);
		frameEntryProtectedMaxId.set(0);
		interpreterCount.set(0);
		lambdaCount.set(0);
	}

}
