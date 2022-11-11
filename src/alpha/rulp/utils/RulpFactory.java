/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utils;

import static alpha.rulp.lang.Constant.A_MAIN;
import static alpha.rulp.lang.Constant.A_NIL;
import static alpha.rulp.lang.Constant.A_ROOT;
import static alpha.rulp.lang.Constant.A_SYSTEM;
import static alpha.rulp.lang.Constant.I_FRAME_MAIN_ID;
import static alpha.rulp.lang.Constant.I_FRAME_ROOT_ID;
import static alpha.rulp.lang.Constant.I_FRAME_SYSTEM_ID;
import static alpha.rulp.lang.Constant.O_Nil;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRArray;
import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRBlob;
import alpha.rulp.lang.IRBoolean;
import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRConst;
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
import alpha.rulp.lang.IRObjectIterator;
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
import alpha.rulp.runtime.IRNameSpace;
import alpha.rulp.runtime.IRObjectLoader;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.runtime.IRTemplate;
import alpha.rulp.runtime.IRThreadContext;
import alpha.rulp.runtime.IRTokener;
import alpha.rulp.runtime.RName;
import alpha.rulp.ximpl.array.XRArrayConst;
import alpha.rulp.ximpl.array.XRArrayVary;
import alpha.rulp.ximpl.blob.XRBlob;
import alpha.rulp.ximpl.collection.XRMap;
import alpha.rulp.ximpl.collection.XRQueue;
import alpha.rulp.ximpl.collection.XRSet;
import alpha.rulp.ximpl.error.XRError;
import alpha.rulp.ximpl.function.XRFunction;
import alpha.rulp.ximpl.function.XRFunctionLambda;
import alpha.rulp.ximpl.function.XRFunctionList;
import alpha.rulp.ximpl.iterator.XRObjectIteratorIteratorWrapper;
import alpha.rulp.ximpl.iterator.XRObjectIteratorListWrapper;
import alpha.rulp.ximpl.iterator.XRObjectIteratorRIteratorWrapper;
import alpha.rulp.ximpl.lang.XRAtom;
import alpha.rulp.ximpl.lang.XRBoolean;
import alpha.rulp.ximpl.lang.XRConst;
import alpha.rulp.ximpl.lang.XRDouble;
import alpha.rulp.ximpl.lang.XRFloat;
import alpha.rulp.ximpl.lang.XRInteger;
import alpha.rulp.ximpl.lang.XRIteratorAdatper;
import alpha.rulp.ximpl.lang.XRListBuilderIterator;
import alpha.rulp.ximpl.lang.XRListConst;
import alpha.rulp.ximpl.lang.XRListIterator;
import alpha.rulp.ximpl.lang.XRListIteratorR;
import alpha.rulp.ximpl.lang.XRListNative;
import alpha.rulp.ximpl.lang.XRListVary;
import alpha.rulp.ximpl.lang.XRLong;
import alpha.rulp.ximpl.lang.XRNative;
import alpha.rulp.ximpl.lang.XRString;
import alpha.rulp.ximpl.lang.XRVar;
import alpha.rulp.ximpl.macro.XRMacro;
import alpha.rulp.ximpl.namespace.XRNameSpace;
import alpha.rulp.ximpl.rclass.XRDefClass;
import alpha.rulp.ximpl.rclass.XRDefInstance;
import alpha.rulp.ximpl.rclass.XRMember;
import alpha.rulp.ximpl.runtime.XRFrame;
import alpha.rulp.ximpl.runtime.XRFrameEntry;
import alpha.rulp.ximpl.runtime.XRFrameProtected;
import alpha.rulp.ximpl.runtime.XRInterpreter;
import alpha.rulp.ximpl.runtime.XRParaAttr;
import alpha.rulp.ximpl.runtime.XRParser;
import alpha.rulp.ximpl.runtime.XRThreadContext;
import alpha.rulp.ximpl.runtime.XRTokener;
import alpha.rulp.ximpl.subject.XRSubjectFrame;
import alpha.rulp.ximpl.template.XRTemplate;

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

	private static IRList EMPTY_CONST_LIST = null;

	private static IRExpr EMPTY_EXPR;

	private static final IRString EMPTY_STR = new XRString("");

	private static final IRBlob emptyBlob = new XRBlob(0);

	private static final XRBoolean False = new XRBoolean(false);

	private static final int FRAME_POOL_1_MAX = 256;

	static String FRAME_PRE_SUBJECT = "SF";

	private static AtomicInteger frameEntryDefaultMaxId = new AtomicInteger(0);

	private static AtomicInteger frameEntryProtectedMaxId = new AtomicInteger(0);

	private static FramePool framePool = new FramePool(FRAME_POOL_1_MAX);

	private static AtomicInteger frameUnNameCount = new AtomicInteger(0);

	private static AtomicInteger interpreterCount = new AtomicInteger(0);

	private static AtomicInteger lambdaCount = new AtomicInteger(0);

	private static final Set<Class<? extends IRObjectLoader>> loaderClasses = new HashSet<>();

	private static final List<IRObjectLoader> rulpLoaders = new LinkedList<>();

	private static final XRBoolean True = new XRBoolean(true);

	private static AtomicInteger uniqNameCount = new AtomicInteger(0);

	private static AtomicInteger unusedNameCount = new AtomicInteger(0);

	static {

		try {
			EMPTY_EXPR = new XRListConst(null, RType.EXPR, null, false);
			EMPTY_CONST_LIST = new XRListConst(null, RType.LIST, null, false);
		} catch (RException e) {
			e.printStackTrace();
		}
	}

	public static IRAtom createAtom(RName rname) {
		RType.ATOM.incCreateCount();
		return new XRAtom(rname.fullName, rname);
	}

	public static IRAtom createAtom(String name) {
		RType.ATOM.incCreateCount();
		return new XRAtom(name);
	}

	public static IRBlob createBlob(byte[] buf) {

		if (buf == null || buf.length <= 0) {
			return emptyBlob;
		}

		RType.BLOB.incCreateCount();
		return new XRBlob(buf);
	}

	public static IRBlob createBlob(int len) {

		if (len <= 0) {
			return emptyBlob;
		}

		RType.BLOB.incCreateCount();
		return new XRBlob(len);
	}

	public static IRBoolean createBoolean(boolean value) {
		return value ? True : False;
	}

	public static IRClass createClassDefClass(String className, IRFrame definedFrame, IRClass superClass) {
		RType.CLASS.incCreateCount();
		return new XRDefClass(className, definedFrame, superClass);
	}

	public static IRConst createConstant(String name, IRObject value) throws RException {
		RType.CONSTANT.incCreateCount();
		return new XRConst(name, value);
	}

	public static IRArray createConstArray(List<? extends IRObject> elements) throws RException {
		RType.ARRAY.incCreateCount();
		return XRArrayConst.build(elements);
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

	public static IRExpr createExpression(IRObject... elements) throws RException {
		RType.EXPR.incCreateCount();
		return new XRListConst(elements, RType.EXPR, null, false);
	}

	public static IRExpr createExpression(Iterator<? extends IRObject> iter) {

		if (iter == null || !iter.hasNext()) {
			return EMPTY_EXPR;
		}

		RType.EXPR.incCreateCount();
		return new XRListIterator(iter, RType.EXPR, null);
	}

	public static IRExpr createExpression(List<? extends IRObject> list) throws RException {

		if (list == null || list.isEmpty()) {
			return EMPTY_EXPR;
		}

		RType.EXPR.incCreateCount();

		return new XRListConst(RulpUtil.toFixArray(list), RType.EXPR, null, false);
	}

	public static IRExpr createExpressionEarly(List<? extends IRObject> list) throws RException {

		if (list == null || list.isEmpty()) {
			return EMPTY_EXPR;
		}

		RType.EXPR.incCreateCount();
		return new XRListConst(RulpUtil.toFixArray(list), RType.EXPR, null, true);
	}

	public static IRFloat createFloat(float value) {
		RType.FLOAT.incCreateCount();
		return new XRFloat(value);
	}

	public static IRFrame createFrame(IRFrame parentFrame, String name) throws RException {

		if (name == null) {
			name = String.format("F-%d", frameUnNameCount.getAndIncrement());
		}

		XRFrame frame = new XRFrame();
		frame.setParentFrame(parentFrame);
		frame.setFrameName(name);
		frame.setThreadContext(parentFrame.getThreadContext());
		framePool.allocateFrameId(frame);

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
		frame.setFrameName(String.format("%s-%s-%d", FRAME_PRE_SUBJECT, subject.getSubjectName(),
				frameUnNameCount.getAndIncrement()));
		frame.setThreadContext(parentFrame.getThreadContext());
		framePool.allocateFrameId(frame);
		return frame;
	}

	public static IRFunction createFunction(IRFrame defineFrame, String funName, List<IRParaAttr> paraAttrs,
			IRExpr funBody) {
		RType.FUNC.incCreateCount();
		return new XRFunction(defineFrame, funName, paraAttrs, funBody);
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

	public static IRInstance createInstanceOfQueue(IRInterpreter interpreter) throws RException {
		RType.INSTANCE.incCreateCount();
		return new XRQueue(RuntimeUtil.getNoClass(interpreter));
	}

	public static IRInstance createInstanceOfSet(IRInterpreter interpreter) throws RException {
		RType.INSTANCE.incCreateCount();
		return new XRSet(RuntimeUtil.getNoClass(interpreter));
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

		// Load base object
		new BaseLoader().load(interpreter, rootFrame);

		// Runtime Initialization
		RuntimeUtil.init(systemFrame);

		// Load base script
		LoadUtil.loadRulpFromJar(interpreter, systemFrame, "alpha/resource/base.res", "utf-8");

		/******************************************************/
		// Load JVM object
		/******************************************************/
		JVMUtil.loadJVMVars(interpreter, rootFrame);

		/******************************************************/
		// Load object
		/******************************************************/
		for (IRObjectLoader loader : rulpLoaders) {
			LoadUtil.loadClass(loader, interpreter, systemFrame);
		}

		return interpreter;
	}

	public static IRList createList(Collection<? extends IRObject> elements) throws RException {

		if (elements == null || elements.isEmpty()) {
			return EMPTY_CONST_LIST;
		}

		RType.LIST.incCreateCount();
		return new XRListConst(RulpUtil.toFixArray(elements), RType.LIST, null, false);
	}

	public static IRList createList(IRIterator<? extends IRObject> iter) throws RException {

		if (iter == null || !iter.hasNext()) {
			return EMPTY_CONST_LIST;
		}

		RType.LIST.incCreateCount();
		return new XRListIteratorR(iter, RType.LIST, null);
	}

	public static IRList createList(IRObject... elements) throws RException {
		RType.LIST.incCreateCount();
		return new XRListConst(elements, RType.LIST, null, false);
	}

	public static IRList createList(Iterator<? extends IRObject> iter) {

		if (iter == null || !iter.hasNext()) {
			return EMPTY_CONST_LIST;
		}

		RType.LIST.incCreateCount();
		return new XRListIterator(iter, RType.LIST, null);
	}

	public static IRObjectIterator createObjectIterator(IRList list) throws RException {
		RType.ITERATOR.incCreateCount();
		return new XRObjectIteratorListWrapper(list);
	}

	public static IRObjectIterator createObjectIterator(Iterator<? extends IRObject> iterator) throws RException {
		RType.ITERATOR.incCreateCount();
		return new XRObjectIteratorIteratorWrapper(iterator);
	}

	public static IRObjectIterator createObjectIterator(IRIterator<? extends IRObject> iterator) throws RException {
		RType.ITERATOR.incCreateCount();
		return new XRObjectIteratorRIteratorWrapper(iterator);
	}

	public static IRList createListOfString(Collection<String> elements) {

		if (elements == null || elements.isEmpty()) {
			return EMPTY_CONST_LIST;
		}

		RType.LIST.incCreateCount();
		return new XRListBuilderIterator<String>(elements.iterator(), (str) -> {
			return RulpFactory.createString(str);
		}, RType.LIST, null);
	}

	public static IRLong createLong(long value) {
		RType.LIST.incCreateCount();
		return new XRLong(value);
	}

	public static IRMacro createMacro(String macroName, List<String> paraNames, IRExpr macroBody) throws RException {
		RType.MACRO.incCreateCount();
		return new XRMacro(macroName, paraNames, macroBody);
	}

	public static IRMember createMember(IRObject subject, String name, IRObject value) throws RException {
		RType.MEMBER.incCreateCount();
		return new XRMember(subject, name, value);
	}

	public static IRList createNamedList(String name, Collection<? extends IRObject> elements) throws RException {

		RType.LIST.incCreateCount();
		return new XRListConst(RulpUtil.toFixArray(elements), RType.LIST, name, false);
	}

	public static IRList createNamedList(String name, IRIterator<? extends IRObject> iter) throws RException {

		if (name != null && name.length() == 0) {
			return createList(iter);
		}

		if (iter == null || !iter.hasNext()) {
			RType.LIST.incCreateCount();
			return new XRListConst(null, RType.LIST, name, false);
		}

		RType.LIST.incCreateCount();
		return new XRListIteratorR(iter, RType.LIST, name);
	}

	public static IRList createNamedList(String name, IRObject... elements) throws RException {
		RType.LIST.incCreateCount();
		return new XRListConst(elements, RType.LIST, name, false);
	}

	public static IRList createNamedList(String name, Iterator<? extends IRObject> iter) throws RException {

		if (name != null && name.length() == 0) {
			return createList(iter);
		}

		if (iter == null || !iter.hasNext()) {
			RType.LIST.incCreateCount();
			return new XRListConst(null, RType.LIST, name, false);
		}

		RType.LIST.incCreateCount();
		return new XRListIterator(iter, RType.LIST, name);
	}

	public static IRNameSpace createNameSpace(String name, IRClass rclass, IRFrame frame) throws RException {
		RType.INSTANCE.incCreateCount();
		return new XRNameSpace(name, rclass, frame);
	}

	public static IRNative createNative(Object obj) {
		RType.NATIVE.incCreateCount();
		return new XRNative(obj);
	}

	public static IRList createNativeList(Collection<? extends IRObject> elements) throws RException {
		RType.LIST.incCreateCount();
		return new XRListNative(RulpUtil.toFixArray(elements), RType.LIST, null, false);
	}

	public static IRList createNativeList(IRObject... elements) throws RException {
		RType.LIST.incCreateCount();
		return new XRListNative(elements, RType.LIST, null, false);
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

	public static IRTemplate createTemplate(String templateName, IRFrame defineFrame) {
		RType.TEMPLATE.incCreateCount();
		return new XRTemplate(templateName, defineFrame);
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

	public static IRThreadContext createThreadContext() {
		return new XRThreadContext();
	}

	public static IRTokener createTokener() {
		return new XRTokener();
	}

	public static String createUniqName(String name) {
		return name + uniqNameCount.getAndIncrement();
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

	public static IRArray createVaryArray() throws RException {
		RType.ARRAY.incCreateCount();
		return XRArrayVary.build(0);
	}

	public static IRArray createVaryArray(int[] sizes) throws RException {
		RType.ARRAY.incCreateCount();
		return XRArrayVary.build(sizes);
	}

	public static IRList createVaryList() {
		RType.LIST.incCreateCount();
		return new XRListVary(RType.LIST, null);
	}

	public static IRList createVaryList(List<? extends IRObject> elements) throws RException {

		RType.LIST.incCreateCount();
		XRListVary list = new XRListVary(RType.LIST, null);
		if (list != null) {
			for (IRObject element : elements) {
				list.add(element);
			}
		}
		return list;
	}

	public static IRList createVaryNamedList(List<? extends IRObject> elements, String name) throws RException {

		RType.LIST.incCreateCount();
		XRListVary list = new XRListVary(RType.LIST, name);
		if (list != null) {
			for (IRObject element : elements) {
				list.add(element);
			}
		}
		return list;
	}

	public static IRList createVaryNamedList(String name) {
		RType.LIST.incCreateCount();
		return new XRListVary(RType.LIST, name);
	}

	public static IRList emptyConstList() {
		return EMPTY_CONST_LIST;
	}

	public static void freeFrameId(int frameId) throws RException {
		framePool.freeFrameId(frameId);
	}

	public static int getFrameEntryCreateCount() {
		return frameEntryProtectedMaxId.get() + frameEntryDefaultMaxId.get();
	}

	public static int getFrameFreeIdCount() {
		return framePool.getFrameFreeIdCount();
	}

	public static int getFrameMaxId() {
		return framePool.getFrameMaxId();
	}

	public static int getInterpreterCount() {
		return interpreterCount.get();
	}

	public static int getLambdaCount() {
		return lambdaCount.get();
	}

	public static String getNextUnusedName() {
		return String.format("un%d", unusedNameCount.getAndIncrement());
	}

	public static int getObjectCreateCount(RType type) {
		return type.getCreateCount();
	}

	public static int getObjectDeleteCount(RType type) {
		return type.getDeleteCount();
	}

	public static List<IRFrame> listGlobalFrames() {
		return framePool.listGlobalFrames();
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

		framePool = new FramePool(FRAME_POOL_1_MAX);

		frameUnNameCount.set(0);
		frameEntryDefaultMaxId.set(0);
		frameEntryProtectedMaxId.set(0);
		unusedNameCount.set(0);
		interpreterCount.set(0);
		lambdaCount.set(0);
		uniqNameCount.set(0);
	}

}
