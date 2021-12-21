package alpha.rulp.utils;

import static alpha.rulp.lang.Constant.A_LOCAL;
import static alpha.rulp.lang.Constant.A_NOCLASS;
import static alpha.rulp.lang.Constant.A_OP_CPS;
import static alpha.rulp.lang.Constant.A_OP_STABLE;
import static alpha.rulp.lang.Constant.A_PARENT;
import static alpha.rulp.lang.Constant.A_TRACE;
import static alpha.rulp.lang.Constant.F_IF;
import static alpha.rulp.lang.Constant.F_MBR_THIS;
import static alpha.rulp.lang.Constant.F_O_ADD;
import static alpha.rulp.lang.Constant.F_O_AND;
import static alpha.rulp.lang.Constant.F_O_BY;
import static alpha.rulp.lang.Constant.F_O_DIV;
import static alpha.rulp.lang.Constant.F_O_EQ;
import static alpha.rulp.lang.Constant.F_O_GE;
import static alpha.rulp.lang.Constant.F_O_GT;
import static alpha.rulp.lang.Constant.F_O_LE;
import static alpha.rulp.lang.Constant.F_O_LT;
import static alpha.rulp.lang.Constant.F_O_MBR;
import static alpha.rulp.lang.Constant.F_O_MOD;
import static alpha.rulp.lang.Constant.F_O_NE;
import static alpha.rulp.lang.Constant.F_O_NOT;
import static alpha.rulp.lang.Constant.F_O_OR;
import static alpha.rulp.lang.Constant.F_O_POWER;
import static alpha.rulp.lang.Constant.F_O_SUB;
import static alpha.rulp.lang.Constant.F_O_XOR;
import static alpha.rulp.lang.Constant.F_RETURN;
import static alpha.rulp.lang.Constant.O_False;
import static alpha.rulp.lang.Constant.O_Nil;
import static alpha.rulp.lang.Constant.O_True;
import static alpha.rulp.lang.Constant.T_Expr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRConst;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRThreadContext;
import alpha.rulp.ximpl.optimize.CPSUtils;

public final class RuntimeUtil {

	private static AtomicInteger callStatsId = new AtomicInteger(0);

	private static AtomicInteger exprComputeFactorCount = new AtomicInteger(0);

	private static AtomicInteger exprComputeFuncCount = new AtomicInteger(0);

	private static AtomicInteger exprComputeMacroCount = new AtomicInteger(0);

	private static AtomicInteger exprComputeMemberCount = new AtomicInteger(0);

	private static AtomicInteger exprComputeTemplateCount = new AtomicInteger(0);

	private static AtomicInteger frameMaxLevel = new AtomicInteger(0);

	private static void _checkFrame(IRFrame frame) throws RException {

		/***************************************************/
		// Check frame ref
		/***************************************************/
		if (frame.getRef() < 0) {
			throw new RException(frame, String.format("ref=%d, frame=%s", frame.getRef(), frame.toString()));
		}

		/***************************************************/
		// Check Async Completed
		/***************************************************/
		{
			IRThreadContext atext = frame.getThreadContext();
			if (atext != null && atext.isCompleted()) {
				throw new RException(frame, "thread is already completed");
			}
		}

		/***************************************************/
		// Update max level
		/***************************************************/
		{
			int lvl = frame.getLevel();
			if (lvl > frameMaxLevel.get()) {
				frameMaxLevel.getAndSet(lvl);
			}
		}
	}

	private static void _checkObject(IRObject obj) throws RException {

//		if (obj == null) {
//			System.out.println();
//		}

		/***************************************************/
		// Check object ref
		/***************************************************/
		if (obj.getRef() < 0) {
			throw new RException(obj, String.format("ref=%d, obj=%s", obj.getRef(), obj.toString()));
		}
	}

	public static boolean _findCallee_expr(IRList stmtList, Set<String> calleeNames, IRFrame frame, boolean tailCall)
			throws RException {

		int size = stmtList.size();
		if (size == 0) {
			return true;
		}

		for (int i = 0; i < size; ++i) {
			if (!_findCallee_obj(stmtList.get(i), calleeNames, frame, i == 0, tailCall)) {
				return false;
			}
		}

		return true;
	}

	public static boolean _findCallee_factor(IRFactor factor, Set<String> calleeNames, IRFrame frame, boolean tailCall)
			throws RException {

		switch (factor.getName()) {
		case F_O_ADD:
		case F_O_SUB:
		case F_O_BY:
		case F_O_DIV:
		case F_O_MOD:
		case F_O_POWER:
		case F_O_AND:
		case F_O_NOT:
		case F_O_OR:
		case F_O_XOR:
		case F_O_GT:
		case F_O_GE:
		case F_O_LT:
		case F_O_LE:
		case F_O_EQ:
		case F_O_NE:
			break;

		case F_IF:

			break;

		case F_RETURN:

//			if (size == 2) {
//				IRObject e1 = expr.get(1);
//				if (e1.getType() == RType.EXPR) {
//					return _findCallee(frame, (IRList) e1, calleeNames, true);
//				}
//			}

			break;
		}

		return true;
	}

	public static boolean _findCallee_list(IRList stmtList, Set<String> calleeNames, IRFrame frame, boolean tailCall)
			throws RException {

		int size = stmtList.size();
		for (int i = 0; i < size; ++i) {
			_findCallee_obj(stmtList.get(i), calleeNames, frame, i == 0, tailCall && (i + 1) == size);
		}

		return true;
	}

	public static boolean _findCallee_obj(IRObject obj, Set<String> calleeNames, IRFrame frame, boolean isHead,
			boolean tailCall) throws RException {

		if (obj == null) {
			return true;
		}

		switch (obj.getType()) {
		case INT:
		case LONG:
		case FLOAT:
		case BOOL:
		case STRING:
			return true;

		case INSTANCE:
		case NATIVE:
		case VAR:
		case NIL:
		case MACRO: // not support yet
		case CLASS:
			return false;

		case LIST:
			return _findCallee_list((IRList) obj, calleeNames, frame, tailCall);

		case ATOM:

			String atomName = ((IRAtom) obj).getName();
			IRFrameEntry entry = frame.getEntry(atomName);
			if (entry == null) {
				if (tailCall) {
					calleeNames.add(atomName);
				}

				return true;
			}

			return _findCallee_obj(entry.getObject(), calleeNames, frame, isHead, tailCall);

		case EXPR:
			return _findCallee_expr((IRList) obj, calleeNames, frame, tailCall);

		case FACTOR:

			if (isHead) {

			}

			return _findCallee_factor((IRFactor) obj, calleeNames, frame, tailCall);

		case FUNC:
			if (tailCall) {
				calleeNames.add(((IRFunction) obj).getName());
			}

			return true;

		default:

			return false;
		}
	}

	public static boolean canAccess(IRObject obj, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (obj == null) {
			return true;
		}

		switch (obj.getType()) {
		case MEMBER:

			IRMember mbr = (IRMember) obj;

			// Check access authority
			switch (mbr.getAccessType()) {
			case PRIVATE:

				IRObject mbrSub = mbr.getSubject();
				IRSubject frameSub = frame.getSubject();
				if (mbrSub == frameSub) {
					return true;
				}

				if (mbrSub instanceof IRSubject) {
					return RuntimeUtil.isInstanceOf(frameSub, (IRSubject) mbrSub);
				}

				return false;

			case DEFAULT:
			case PUBLIC:
				return true;

			default:
				throw new RException("invalid accessType: " + mbr.getAccessType());
			}

		default:
			return true;
		}

	}

	public static IRObject compute(IRObject obj, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (obj == null) {
			return O_Nil;
		}

		_checkFrame(frame);
		_checkObject(obj);

		try {

			RType rt = obj.getType();

			switch (rt) {
			case INT:
			case LONG:
			case FLOAT:
			case DOUBLE:
			case BOOL:
			case STRING:
			case INSTANCE:
			case NATIVE:
			case FACTOR:
			case TEMPLATE:
			case FUNC:
			case FRAME:
			case ARRAY:
				return obj;

			case VAR: {

				IRVar var = (IRVar) obj;
				if (var.getValue() != null) {
					return var.getValue();
				}

				IRFrameEntry entry = frame.getEntry(var.getName());
				if (entry != null) {
					return RulpUtil.asVar(entry.getObject());
				}

				throw new RException("var entry not found: " + var);
			}

			case CONSTANT: {
				IRConst ct = (IRConst) obj;
				return ct.getValue();
			}

			case ATOM: {

				IRFrameEntry entry = lookupFrameEntry(frame, RulpUtil.asAtom(obj).getName());
				if (entry == null) {
					return obj;
				}

				IRObject rst = entry.getObject();

				switch (rst.getType()) {
				case VAR:
					rst = ((IRVar) rst).getValue();
					break;
				case CONSTANT:
					rst = ((IRConst) rst).getValue();
					break;
				default:
				}

				return rst;
			}

			case NIL:
				return O_Nil;

			case EXPR:

				IRList expr = (IRList) obj;
				if (expr.size() == 0) {
					return obj;
				}

				IRObject e0 = compute(expr.get(0), interpreter, frame);

				switch (e0.getType()) {
				case FACTOR:
					exprComputeFactorCount.getAndIncrement();
					return RuntimeUtil.computeCallable((IRCallable) e0, expr, interpreter, frame);

				case MACRO:
					exprComputeMacroCount.getAndIncrement();
					return RuntimeUtil.computeCallable((IRCallable) e0, expr, interpreter, frame);

				case FUNC:
					exprComputeFuncCount.getAndIncrement();
					IRObject rst = RuntimeUtil.computeFun((IRFunction) e0, expr, interpreter, frame);
					if (rst == null) {
						return O_Nil;
					} else if (rst.getType() == RType.EXPR && RulpUtil.containAttribute(rst, A_OP_CPS)) {
						rst = CPSUtils.computeCPSExpr((IRExpr) rst, interpreter, frame);
					}
					return rst;

				case TEMPLATE:
					return RuntimeUtil.computeCallable((IRCallable) e0, expr, interpreter, frame);

				case MEMBER:
					exprComputeMemberCount.getAndIncrement();
					IRObject e1m = ((IRMember) e0).getValue();
					if (e1m.getType() != RType.FUNC) {
						throw new RException("factor not found: " + obj);
					}

					return RuntimeUtil.computeFun((IRFunction) e1m, expr, interpreter, frame);

				default:

					throw new RException("factor not found: " + obj);
				}

			case LIST:

				if (!RuntimeUtil.isComputable(frame, obj)) {
					return obj;
				}

				IRList oldList = (IRList) obj;

				ArrayList<IRObject> rstList = new ArrayList<>();
				IRIterator<? extends IRObject> it = ((IRList) obj).iterator();
				while (it.hasNext()) {
					rstList.add(compute(it.next(), interpreter, frame));
				}

				if (oldList.getNamedName() == null)
					return RulpFactory.createList(rstList);
				else
					return RulpFactory.createNamedList(rstList, oldList.getNamedName());

			case MEMBER:

				IRMember mbr = RuntimeUtil.getActualMember((IRMember) obj, interpreter, frame);
				if (!RuntimeUtil.canAccess(mbr, interpreter, frame)) {
					throw new RException("Can't access member<" + mbr + "> in frame<" + frame + ">");
				}

				return mbr.getValue();

			default:
				throw new RException("Invalid Type: " + rt + ", obj:" + obj.toString());
			}

		} catch (RException e) {

			String atExprStr = obj.toString();
			if (atExprStr.length() > 80) {
				atExprStr = atExprStr.substring(0, 77) + "...";
			}

			e.addMessage("at " + frame.getFrameName() + ": " + atExprStr);

			throw e;
		}
	}

	public static IRList computeAtomList(IRInterpreter intepreter, IRFrame frame, Collection<String> list)
			throws RException {

		if (list == null || list.isEmpty()) {
			return RulpFactory.emptyConstList();
		}

		LinkedList<IRObject> aList = new LinkedList<>();
		for (String element : list) {
			aList.add(intepreter.compute(frame, RulpFactory.createAtom(element)));
		}

		return RulpFactory.createList(aList);
	}

	public static IRObject computeCallable(IRCallable callObject, IRList args, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		_checkFrame(frame);
		_checkObject(callObject);

		/* Check early expression */
		{
			int size = args.size();
			int firstEarlyIndex = -1;

			for (int i = 1; i < size; ++i) {
				IRObject arg = args.get(i);
				if (arg != null && arg.getType() == RType.EXPR && ((IRExpr) arg).isEarly()) {
					firstEarlyIndex = i;
					break;
				}
			}

			if (firstEarlyIndex != -1) {

				List<IRObject> newArgList = new ArrayList<>();
				for (int i = 0; i < firstEarlyIndex; ++i) {
					newArgList.add(args.get(i));
				}

				for (int i = firstEarlyIndex; i < size; ++i) {
					IRObject arg = args.get(i);
					if (arg != null && arg.getType() == RType.EXPR && ((IRExpr) arg).isEarly()) {
						arg = RuntimeUtil.compute(arg, interpreter, frame);
					}

					newArgList.add(arg);
				}

				args = args.getType() == RType.EXPR ? RulpFactory.createExpression(newArgList)
						: RulpFactory.createList(newArgList);
			}
		}

		callObject.incCallCount(callStatsId.get(), interpreter.getCallId());

		if (callObject.isThreadSafe()) {
			return callObject.compute(args, interpreter, frame);
		} else {
			synchronized (callObject) {
				return callObject.compute(args, interpreter, frame);
			}
		}
	}

	public static IRObject computeFun(IRFunction fun, IRList expr, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		_checkFrame(frame);
		_checkObject(fun);

		// For Function List, the argCount is -1
		int argCount = fun.getArgCount();
		if (argCount != -1 && expr.size() != (argCount + 1)) {
			throw new RException(String.format("Unexpect argument number in fun<%s>: expect=%d, actual=%d",
					fun.getName(), argCount, expr.size() - 1));
		}

		RulpUtil.incRef(fun);
		IRList newExpr = rebuildFuncExpr(fun, expr, interpreter, frame);
		RulpUtil.incRef(newExpr);

		try {
			return RuntimeUtil.computeCallable(fun, newExpr, interpreter, frame);
		} finally {
			RulpUtil.decRef(newExpr);
			RulpUtil.decRef(fun);
		}

	}

	public static IRObject computeFun2(IRFunction fun, IRInterpreter interpreter, IRFrame frame, IRObject... objs)
			throws RException {

		ArrayList<IRObject> args = new ArrayList<>();
		args.add(fun);

		for (IRObject obj : objs) {
			args.add(obj);
		}

		return RuntimeUtil.computeFun(fun, RulpFactory.createList(args), interpreter, frame);
	}

	public static IRMember getActualMember(IRMember mbr, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (mbr.getValue() != null) {
			return mbr;
		}

		IRObject subObj = RuntimeUtil.compute(mbr.getSubject(), interpreter, frame);
		if (subObj == null) {
			throw new RException("subject<" + mbr.getSubject() + "> not found");
		}

		IRSubject sub = RulpUtil.asSubject(subObj);

		// Get root object
		IRMember actMbr = sub.getMember(mbr.getName());
		if (actMbr == null) {
			throw new RException("member<" + mbr + "> not found in " + sub);
		}

		return actMbr;
	}

	public static IRVar getActualVar(IRVar var, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (var.getValue() != null) {
			return var;
		}

		IRFrameEntry entry = frame.getEntry(var.getName());
		if (entry == null) {
			throw new RException("var not found: " + var);
		}

		return RulpUtil.asVar(entry.getObject());
	}

	public static boolean getBoolVarValue(IRInterpreter interpreter, String varName) throws RException {
		return RulpUtil.asBoolean(RulpUtil.asVar(interpreter.getObject(varName)).getValue()).asBoolean();
	}

	public static int getExprComputeCount(RType type) {

		switch (type) {
		case FACTOR:
			return exprComputeFactorCount.get();

		case FUNC:
			return exprComputeFuncCount.get();

		case TEMPLATE:
			return exprComputeTemplateCount.get();

		case MACRO:
			return exprComputeMacroCount.get();

		case MEMBER:
			return exprComputeMemberCount.get();

		default:
			return 0;
		}
	}

	public static int getFrameMaxLevel() {
		return frameMaxLevel.get();
	}

	public static IRClass getNoClass(IRInterpreter interpreter) throws RException {
		return RulpUtil.asClass(interpreter.getObject(A_NOCLASS));
	}

	public static Map<IRObject, DeCounter> getObjecCallCount(IRFrame frame) {

		Map<IRObject, DeCounter> cm = new HashMap<>();

		for (IRFrameEntry entry : frame.listEntries()) {

			IRObject obj = entry.getObject();
			if (obj == null) {
				continue;
			}

			if (obj instanceof IRCallable) {
				IRCallable cobj = (IRCallable) obj;
				DeCounter counter = cobj.getCallCount(callStatsId.get());
				if (counter.getTotalCount() > 0) {
					cm.put(cobj, counter);
				}

				continue;
			}

			if (obj instanceof IRSubject) {

				if (obj.getType() == RType.FRAME) {
					continue;
				}

				IRSubject sub = (IRSubject) obj;
				for (IRMember mbr : sub.listMembers()) {

					IRObject mbrObj = mbr.getValue();
					if (mbrObj == null) {
						continue;
					}

					if (mbrObj instanceof IRCallable) {
						IRCallable cobj = (IRCallable) mbrObj;
						DeCounter counter = cobj.getCallCount(callStatsId.get());
						if (counter.getTotalCount() > 0) {
							cm.put(mbr, counter);
						}
					}
				}
			}
		}

		return cm;
	}

	public static void init(IRFrame frame) throws RException {
//		RulpUtil.setLocalVar(frame, A_OP_CPS, O_False);
		RulpUtil.setLocalVar(frame, A_OP_STABLE, O_False);
		RulpUtil.setLocalVar(frame, A_TRACE, O_False);
	}

	public static boolean isComputable(IRFrame curFrame, IRObject obj) throws RException {

		if (obj == null) {
			return false;
		}

		switch (obj.getType()) {
		case INT:
		case LONG:
		case FLOAT:
		case BOOL:
		case STRING:
		case NIL:
		case FACTOR:
		case TEMPLATE:
		case FUNC:
		case ARRAY:
			return false;

		case ATOM:
			return curFrame.getEntry(((IRAtom) obj).getName()) != null;

		case VAR:
		case EXPR:
		case MEMBER:
			return true;

		case LIST:

			IRIterator<? extends IRObject> iter = ((IRList) obj).iterator();
			while (iter.hasNext()) {
				if (isComputable(curFrame, iter.next())) {
					return true;
				}
			}

			return false;

		default:
			return true;
		}
	}

	public static boolean isCpsExpr(IRExpr expr, Set<String> calleeNames, IRFrame frame) throws RException {
		return false;
	}

	public static boolean isForceLocalEntryName(String name) {

		switch (name) {
		case A_LOCAL:
		case A_PARENT:
			return true;
		}

		return false;
	}

	public static boolean isInstanceOf(IRSubject child, IRSubject parent) throws RException {

		IRSubject childParent = child.getParent();
		while (childParent != null) {
			if (childParent == parent) {
				return true;
			}
			childParent = childParent.getParent();
		}

		return false;
	}

	public static boolean isPureOperatorName(String name) {
		switch (name) {
		case F_O_BY:
		case F_O_ADD:
		case F_O_DIV:
		case F_O_SUB:
		case F_O_POWER:
		case F_O_AND:
		case F_O_OR:
		case F_O_NOT:
		case F_O_XOR:
//		case F_STR_LENGTH:
			return true;
		default:
			return false;
		}
	}

//	public static boolean isSupportOpCPS(IRFrame frame) throws RException {
//		return RulpUtil.asBoolean(RulpUtil.getVarValue(frame, A_OP_CPS)).asBoolean();
//	}

	public static boolean isSupportOpStable(IRFrame frame) throws RException {
		return RulpUtil.asBoolean(RulpUtil.getVarValue(frame, A_OP_STABLE)).asBoolean();
	}

	public static boolean isTrace(IRFrame frame) throws RException {
		return RulpUtil.asBoolean(RulpUtil.getVarValue(frame, A_TRACE)).asBoolean();
	}

	public static IRFrameEntry lookupFrameEntry(IRFrame frame, String name) throws RException {

		if (isForceLocalEntryName(name)) {
			return frame.getEntry(name);
		}

		List<IRFrame> searchFrameList = frame.getSearchFrameList();
		if (searchFrameList == null) {
			return frame.getEntry(name);
		}

		// Searching the using name space if it was specified

		for (IRFrame searchFrame : searchFrameList) {
			IRFrameEntry entry = searchFrame.getEntry(name);
			if (entry != null) {
				return entry;
			}
		}

		return null;
	}

	public static IRInstance newInstance(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		// (new class1 o3 '(1 2))
		// (new class1 o3))
		// (new class1 '(1 2))
		// (new class1)
		if (args.size() > 4) {
			throw new RException("Invalid parameters: " + args);
		}

		int argIndex = 1;
		IRObject argObj = null;
		IRFrame definedFrame = frame;

		/******************************************/
		// Class
		/******************************************/
		IRClass rClass = RulpUtil.asClass(interpreter.compute(frame, args.get(argIndex++)));

		/******************************************/
		// Instance Name
		/******************************************/
		String instanceName = null;
		if (argIndex < args.size()) {

			argObj = args.get(argIndex);

			switch (argObj.getType()) {
			case ATOM:
				instanceName = RulpUtil.asAtom(argObj).getName();
				++argIndex;
				break;

			// Create instance in the frame of the specified subject
			case MEMBER:

				IRMember mbr = RulpUtil.asMember(argObj);

				IRObject subObj = RuntimeUtil.compute(mbr.getSubject(), interpreter, frame);
				if (subObj == null) {
					throw new RException("subject<" + mbr.getSubject() + "> not found");
				} else {
					IRSubject sub = RulpUtil.asSubject(subObj);
					definedFrame = sub.getSubjectFrame();
					instanceName = mbr.getName();
					++argIndex;
				}

				break;

			// Create instance in member expression: '(:: sub mbr)
			case EXPR:

				IRExpr subExpr = RulpUtil.asExpression(argObj);
				IRObject e0 = subExpr.get(0);

				if (e0.asString().equals(F_O_MBR) && subExpr.size() == 3
						&& (e0.getType() == RType.ATOM || e0.getType() == RType.FACTOR)
						&& subExpr.get(2).getType() == RType.ATOM) {

					IRSubject sub = RulpUtil.asSubject(interpreter.compute(frame, subExpr.get(1)));
					definedFrame = sub.getSubjectFrame();
					instanceName = RulpUtil.asAtom(subExpr.get(2)).getName();
					++argIndex;
				}

				break;
			}

		}

		/******************************************/
		// argument list
		/******************************************/
		IRList initArgs = null;
		if (argIndex < args.size()) {
			initArgs = RulpUtil.asList(interpreter.compute(frame, args.get(argIndex++)));

			if (argIndex != args.size()) {
				throw new RException("Invalid parameters: " + args);
			}
		} else {
			initArgs = RulpFactory.emptyConstList();
		}

		/******************************************/
		// Create instance
		/******************************************/
		IRInstance instance = rClass.newInstance(instanceName, args, interpreter, definedFrame);
		RulpUtil.setMember(instance, F_MBR_THIS, instance);

		/******************************************/
		// Call Initialization member
		/******************************************/
		instance.init(initArgs, interpreter, frame);

		/******************************************/
		// Add into frame
		/******************************************/
		if (instanceName != null) {

			// Check instance exist
			IRFrameEntry oldEntry = definedFrame.getEntry(instanceName);
			if (oldEntry != null && oldEntry.getFrame() == definedFrame) {
				throw new RException(
						String.format("duplicate object<%s> found: %s", instanceName, oldEntry.getObject()));
			}

			definedFrame.setEntry(instanceName, instance);
		}

		return instance;
	}

	public static IRObject rebuild(IRObject obj, Map<String, IRObject> replaceMap) throws RException {

		if (obj == null) {
			return obj;
		}

		switch (obj.getType()) {
		case ATOM: {
			IRAtom atom = (IRAtom) obj;
			IRObject var = replaceMap.get(atom.getName());
			return var == null ? obj : var;
		}

		case EXPR:
		case LIST:

			ArrayList<IRObject> newList = new ArrayList<>();
			IRIterator<? extends IRObject> it = ((IRList) obj).iterator();
			while (it.hasNext()) {
				newList.add(rebuild(it.next(), replaceMap));
			}

			if (obj.getType() == RType.LIST) {
				return RulpFactory.createList(newList);
			}

			IRExpr expr = (IRExpr) obj;

			return expr.isEarly() ? RulpFactory.createExpressionEarly(newList) : RulpFactory.createExpression(newList);

		case MEMBER:

			IRMember mbr = (IRMember) obj;
			if (mbr.getValue() == null) {

				boolean update = false;

				IRObject sub = mbr.getSubject();
				String mbrName = mbr.getName();

				if (sub.getType() == RType.ATOM) {
					IRObject var = replaceMap.get(((IRAtom) sub).getName());
					if (var != null) {
						sub = var;
						update = true;
					}
				}

				if (update) {
					return RulpFactory.createMember(sub, mbrName, null);
				}
			}

			return obj;

		default:
			return obj;
		}
	}

	public static IRList rebuildFuncExpr(IRFunction fun, IRList expr, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		List<IRParaAttr> paraAttrs = fun.getParaAttrs();

		/*********************************************/
		// Check parameter attribute whether there is any pass-value parameter
		/*********************************************/
		boolean rebuildArgValues = false;
		if (paraAttrs == null) {
			rebuildArgValues = true;
		} else {
			for (IRParaAttr attr : fun.getParaAttrs()) {
				if (attr.getParaType() != T_Expr) {
					rebuildArgValues = true;
					break;
				}
			}
		}

		if (!rebuildArgValues) {
			return expr;
		}

		/*********************************************/
		// rebuild argument value list
		/*********************************************/
		ArrayList<IRObject> argList = new ArrayList<>();
		argList.add(fun);

		IRIterator<? extends IRObject> argIter = expr.listIterator(1); // Skip factor head element

		Iterator<IRParaAttr> attrIt = null;
		if (paraAttrs != null) {
			attrIt = paraAttrs.iterator();
		}

		while (argIter.hasNext()) {

			IRAtom paraType = null;
			if (attrIt != null) {
				paraType = attrIt.next().getParaType();
			}

			IRObject argValue = argIter.next();

			// pass by default
			if (paraType == null) {
				argValue = compute(argValue, interpreter, frame);
			}
			// pass by expression
			else if (paraType == T_Expr) {
				rebuildArgValues = true;
			}
			// other, pass by value
			else {
				argValue = compute(argValue, interpreter, frame);
			}

			argList.add(argValue);
		}

		return RulpFactory.createExpression(argList);
	}

	public static void reset() {
		exprComputeFactorCount.getAndSet(0);
		exprComputeMacroCount.getAndSet(0);
		exprComputeFuncCount.getAndSet(0);
		exprComputeTemplateCount.getAndSet(0);
		exprComputeMemberCount.getAndSet(0);
		frameMaxLevel.getAndSet(0);
		callStatsId.incrementAndGet();
	}

//	public static void setOpCPS(IRFrame frame, boolean support) throws RException {
//		RulpUtil.setLocalVar(frame, A_OP_CPS, support ? O_True : O_False);
//	}

	public static void setTrace(IRFrame frame, boolean trace) throws RException {
		RulpUtil.setLocalVar(frame, A_TRACE, trace ? O_True : O_False);
	}
}
