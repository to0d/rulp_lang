package alpha.rulp.utils;

import static alpha.rulp.lang.Constant.MAX_COUNTER_SIZE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRTemplate;
import alpha.rulp.runtime.IRThreadContext;
import alpha.rulp.ximpl.optimize.LCOUtil;

public class TraceUtil {

	static class FrameTree {

		Set<IRFrame> allFrames;
		Map<IRFrame, Set<IRFrame>> frameTreeMap = new HashMap<>();
		IRInterpreter interpreter;
		StringBuilder sb = new StringBuilder();

		public FrameTree(List<IRFrame> globalFrames, IRInterpreter interpreter) {

			this.allFrames = new HashSet<>(globalFrames);
			this.allFrames.add(interpreter.getMainFrame()); // main frame
			this.allFrames.add(interpreter.getMainFrame().getParentFrame()); // system frame
			this.allFrames.add(interpreter.getMainFrame().getParentFrame().getParentFrame()); // root frame
			this.interpreter = interpreter;
		}

		private void _printFrame(IRFrame frame, int level) throws RException {

			for (int i = 0; i < level; i++) {
				sb.append("    ");
			}

			sb.append(String.format("->FRAME(%s): id=%d, lvl=%d", frame.getFrameName(), frame.getFrameId(),
					frame.getLevel()));
			IRSubject frameSubject = frame.getSubject();
			if (frameSubject != null) {
				sb.append(String.format(", subject=%s", frameSubject.getSubjectName()));
			}
			List<IRFrame> searchFrameList = frame.getSearchFrameList();
			if (searchFrameList != null) {
				sb.append(String.format(", search=%s", "" + searchFrameList));
			}

			sb.append("\n");

			Set<IRFrame> childFrames = frameTreeMap.get(frame);
			if (childFrames == null) {
				return;
			}

			ArrayList<IRFrame> childFrameList = new ArrayList<>(childFrames);
			Collections.sort(childFrameList, (f1, f2) -> {
				return f1.getFrameId() - f2.getFrameId();
			});

			for (IRFrame child : childFrameList) {
				_printFrame(child, level + 1);
			}

			frameTreeMap.remove(frame);
		}

		public String output() throws RException {

			for (IRFrame frame : allFrames) {

				IRFrame parentFrame = frame.getParentFrame();
				if (parentFrame == null) {

					Set<IRFrame> childFrames = frameTreeMap.get(frame);
					if (childFrames == null) {
						childFrames = new HashSet<>();
						frameTreeMap.put(frame, childFrames);
					}

				} else if (!allFrames.contains(parentFrame)) {

					throw new RException(String.format("Unlinked frame: frame=%s, parent=%s", frame, parentFrame));

				} else {

					Set<IRFrame> childFrames = frameTreeMap.get(parentFrame);
					if (childFrames == null) {
						childFrames = new HashSet<>();
						frameTreeMap.put(parentFrame, childFrames);
					}

					childFrames.add(frame);
				}
			}

			_printFrame(interpreter.getMainFrame().getParentFrame().getParentFrame(), 0);
			if (!frameTreeMap.isEmpty()) {
				while (!frameTreeMap.isEmpty()) {
					IRFrame aFrame = frameTreeMap.entrySet().iterator().next().getKey();
					_printFrame(aFrame, 0);
				}
			}

			return sb.toString();
		}

	}

	public interface IROptInfoTracer {
		public String printInfo(IRInterpreter interpreter) throws RException;
	}

	static Map<String, IROptInfoTracer> optTracerMap = new HashMap<>();

	static List<String> optTracerNameList = new ArrayList<>();

//	private static StaticVar varTrace = new StaticVar(A_TRACE, O_False);

	static final String SEP_LINE1 = "==========================================================================================================================\n";

	static final String SEP_LINE2 = "--------------------------------------------------------------------------------------------------------------------------\n";

	static final String SEP_LINE3 = "..........................................................................................................................\n";

	static {
		registerOptInfoTracer("LCO", (_interpreter) -> {
			return String.format("rebuild=%d, arg=%d, pass=%d, hit=%d", LCOUtil.getRebuildCount(),
					LCOUtil.getArgCount(), LCOUtil.getPassCount(), LCOUtil.getHitCount());
		});
	}

	private static String _getEntryAliasName(IRFrameEntry entry) {

		String name = entry.getName();
		if (!entry.getAliasName().isEmpty()) {
			name += "(";
			for (int i = 0; i < entry.getAliasName().size(); ++i) {
				if (i != 0) {
					name += ",";
				}
				name += entry.getAliasName().get(i);
			}
			name += ")";
		}

		return name;
	}

	private static void _printCallableStatsInfo(StringBuffer sb, IRInterpreter interpreter) throws RException {

		ArrayList<Pair<Pair<IRObject, IRFrame>, DeCounter>> ccList = new ArrayList<>();

		// main frame
		{
			IRFrame frame = interpreter.getMainFrame();
			for (Entry<IRObject, DeCounter> e : RuntimeUtil.getObjecCallCount(frame).entrySet()) {
				ccList.add(new Pair<>(new Pair<>(e.getKey(), frame), e.getValue()));
			}
		}

		// system frame
		{
			IRFrame frame = interpreter.getMainFrame().getParentFrame();
			for (Entry<IRObject, DeCounter> e : RuntimeUtil.getObjecCallCount(frame).entrySet()) {
				ccList.add(new Pair<>(new Pair<>(e.getKey(), frame), e.getValue()));
			}
		}

		// root frame
		{
			IRFrame frame = interpreter.getMainFrame().getParentFrame().getParentFrame();
			for (Entry<IRObject, DeCounter> e : RuntimeUtil.getObjecCallCount(frame).entrySet()) {
				ccList.add(new Pair<>(new Pair<>(e.getKey(), frame), e.getValue()));
			}
		}

		Collections.sort(ccList, (e1, e2) -> {
			int d = e2.getValue().getTotalCount() - e1.getValue().getTotalCount();
			if (d == 0) {
				d = e2.getKey().getKey().asString().compareTo(e1.getKey().getKey().asString());
			}

			if (d == 0) {
				d = e2.getKey().getValue().getFrameName().compareTo(e1.getKey().getValue().getFrameName());
			}

			return d;
		});

		ArrayList<DeCounter> counters = new ArrayList<>();

		for (Pair<Pair<IRObject, IRFrame>, DeCounter> e : ccList) {
			counters.add(e.getValue());
		}

		ArrayList<String> counterLines = formatCounterTable(counters, MAX_COUNTER_SIZE);
		int counterLineIndex = 0;

		sb.append(String.format("Callable stats info: callId=%d, %s\n", interpreter.getCallId(),
				counterLines.get(counterLineIndex++)));
		sb.append(SEP_LINE1);
		sb.append(String.format("%8s %8s %6s %4s %-30s %s\n", "Frame", "RType", "Count", "Ref", "Object",
				counterLines.get(counterLineIndex++)));
		sb.append(SEP_LINE2);

		for (Pair<Pair<IRObject, IRFrame>, DeCounter> e : ccList) {

			DeCounter counter = e.getValue();
			IRFrame frame = e.getKey().getValue();
			IRObject obj = e.getKey().getKey();

			String objName = "" + obj;
			if (objName.length() > 30) {
				objName = objName.substring(0, 30);
			}

			sb.append(String.format("%8s %8s %6s %4s %-30s %s\n", frame.getFrameName(), _toTypeString(obj),
					counter.getTotalCount(), _toRefString(obj), objName, counterLines.get(counterLineIndex++)));
		}

		sb.append(SEP_LINE1);
		sb.append("\n");
	}

//	public static void init(IRFrame frame) throws RException {
//		varTrace.init(frame);
//	}
//
//	public static boolean isTrace() throws RException {
//		return varTrace.getBoolValue();
//	}

	private static void _printCallerAnnotation(StringBuffer sb, IRInterpreter interpreter) throws RException {

		List<IRFrame> allFrames = new ArrayList<>();
		allFrames.add(interpreter.getMainFrame().getParentFrame().getParentFrame());
		allFrames.add(interpreter.getMainFrame().getParentFrame());
		allFrames.add(interpreter.getMainFrame());
		allFrames.addAll(RulpFactory.listGlobalFrames());

		boolean outputHead = false;

		for (IRFrame frame : allFrames) {

			List<IRFrameEntry> frameEntries = null;

			for (IRFrameEntry entry : frame.listEntries()) {
				IRObject obj = entry.getValue();
				if (obj instanceof IRCallable) {
					IRCallable callObj = (IRCallable) obj;
					if (callObj.hasBeforeAnnotation() || callObj.hasAfterAnnotation()) {
						if (frameEntries == null) {
							frameEntries = new ArrayList<>();
						}
						frameEntries.add(entry);
					}
				}
			}

			if (frameEntries == null) {
				continue;
			}

			Collections.sort(frameEntries, (f1, f2) -> {
				return f1.getEntryId() - f2.getEntryId();
			});

			for (IRFrameEntry entry : frameEntries) {

				IRCallable callObj = (IRCallable) entry.getObject();

				if (!outputHead) {
					sb.append("Caller annotation:\n");
					sb.append(SEP_LINE1);
					sb.append(String.format("%-20s : %-8s %-8s %-10s %s\n", "Name(alias)", "EntryId", "FrameId", "Type",
							"Annotation"));
					sb.append(SEP_LINE2);
					outputHead = true;
				}

				String name = _getEntryAliasName(entry);
				IRFrame entryFrame = entry.getFrame();

				String out = "";
				if (callObj.hasBeforeAnnotation()) {
					out += "before: " + callObj.listBeforeAnnotationAttr();
				}

				if (callObj.hasAfterAnnotation()) {
					if (!out.isEmpty()) {
						out += ", ";
					}
					out += "after: " + callObj.listAfterAnnotationAttr();
				}

				sb.append(String.format("%-20s : %-8d %-8s %-10s %s\n", name, entry.getEntryId(),
						entryFrame == null ? -1 : entryFrame.getFrameId(), _toTypeString(callObj), out));
			}
		}

		if (outputHead) {
			sb.append(SEP_LINE1);
			sb.append("\n");
		}
	}

	private static void _printExpressionComputeCount(StringBuffer sb, IRInterpreter interpreter) throws RException {

		sb.append("Expression compute count:\n");
		sb.append(SEP_LINE1);
		sb.append(String.format("%10s: %8s\n", "RType", "Count"));
		sb.append(SEP_LINE2);
		for (RType t : RType.ALL_RTYPE) {

			int count = RuntimeUtil.getExprComputeCount(t);
			if (count == 0) {
				continue;
			}

			sb.append(String.format("%10s: %8d\n", _toTypeString(t), count));
		}
		sb.append(SEP_LINE1);
		sb.append("\n");
	}

	private static void _printObjectCreateCount(StringBuffer sb, IRInterpreter interpreter) throws RException {

		sb.append("Object create count:\n");
		sb.append(SEP_LINE1);
		sb.append(String.format("%12s: %12s %12s %12s\n", "RType", "Create", "Delete", "Exist"));
		sb.append(SEP_LINE2);
		for (RType t : RType.ALL_RTYPE) {
			int createCount = RulpFactory.getObjectCreateCount(t);
			if (createCount == 0) {
				continue;
			}

			int deleteCount = RulpFactory.getObjectDeleteCount(t);
			sb.append(String.format("%12s: %12d %12d %12d\n", _toTypeString(t), createCount, deleteCount,
					createCount - deleteCount));
		}

		sb.append(String.format("%12s: %12d %12d\n", "interpreter", RulpFactory.getInterpreterCount(), 0));
		sb.append(String.format("%12s: %12d %12d\n", "frameEntry", RulpFactory.getFrameEntryCreateCount(), 0));
		sb.append(String.format("%12s: %12d %12d\n", "lambda", RulpFactory.getLambdaCount(), 0));

		sb.append(SEP_LINE1);
		sb.append("\n");
	}

	private static void _printTotalRunTimeInfo(StringBuffer sb, IRInterpreter interpreter) {

		sb.append("Total runtime info:\n");
		sb.append(SEP_LINE1);
		sb.append(String.format("%30s: %8s\n", "Name", "info"));
		sb.append(SEP_LINE2);
		sb.append(String.format("%30s: %8d\n", "Interpreter call id", interpreter.getCallId()));
		sb.append(String.format("%30s: %8d %8d\n", "Interpreter call level/max", interpreter.getTLS().getCallLevel(),
				interpreter.getMaxCallLevel()));
		sb.append(String.format("%30s: %8d\n", "Frame max level", RuntimeUtil.getFrameMaxLevel()));
		sb.append(String.format("%30s: %8d\n", "Frame max id", RulpFactory.getFrameMaxId()));
		sb.append(String.format("%30s: %8d\n", "Frame free id count", RulpFactory.getFrameFreeIdCount()));
		sb.append(SEP_LINE1);
		sb.append("\n");

	}

	private static String _toRefString(IRObject obj) throws RException {

		if (obj == null) {
			return "nil";
		}

		return "" + obj.getRef();
	}

	private static String _toString(IRObject obj) throws RException {

		switch (obj.getType()) {
		case VAR:
			IRObject valueObj = RulpUtil.asVar(obj).getValue();

			if (valueObj == null) {
				return null;
			}

			if (valueObj.getType() == RType.NIL) {
				return _toTypeString(valueObj);
			}

			return valueObj + " : type=" + _toTypeString(valueObj);

		case FUNC:

			IRFunction func = RulpUtil.asFunction(obj);
			String out = func.getSignature() + " : frame=" + func.getDefineFrame();

			if (func.isList()) {
				out += ", type=list";
			}

			if (func.isLambda()) {
				out += ", type=lambda";
			}

			return out;

		case TEMPLATE:
			IRTemplate tp = RulpUtil.asTemplate(obj);
			return tp.getSignature() + " : frame=" + tp.getDefineFrame();

//		case INSTANCE:
//			IRInstance instance = RulpUtil.asInstance(obj);
//			IRClass rclass = instance.getRClass();
//			return instance.getInstanceName() + " : class=" + (rclass == null ? "null" : rclass.getClassName());

		default:
			return obj.toString();
		}
	}

	private static String _toTypeString(IRObject obj) throws RException {

		if (RulpUtil.isFunctionList(obj)) {
			return "funcList";
		}

		return _toTypeString(obj.getType());
	}

	private static String _toTypeString(RType type) throws RException {
		return RType.toObject(type).asString();
	}

	public static ArrayList<String> formatCounterTable(ArrayList<DeCounter> counters, int columnCount)
			throws RException {

		ArrayList<String> lines = new ArrayList<>();

		int maxTimeUnit = -1;

		// Find max unit
		for (DeCounter counter : counters) {
			int unit = counter.getUnit();
			if (unit > maxTimeUnit) {
				maxTimeUnit = unit;
			}
		}

		final int rowCount = counters.size();
		final int[][] callCount = new int[rowCount][columnCount];
		final int[] totalCount = new int[columnCount];

		int maxValue = -1;

		// Expand all unit to max unit

		int row = 0;
		for (DeCounter counter : counters) {

			while (counter.getUnit() < maxTimeUnit) {
				counter.expand();
			}

			for (int i = 0; i < columnCount; ++i) {

				int value = counter.getCount(i);
				callCount[row][i] = value;
				totalCount[i] += value;
				if (value > maxValue) {
					maxValue = value;
				}
			}

			row++;
		}

		FIND_MAX: for (; columnCount > 0; --columnCount) {
			if (totalCount[columnCount - 1] != 0) {
				break FIND_MAX;
			}
		}

		// Calculate the max value unit: x(value unit), y(max count value)
		// x^10 < Y ==> x = e^(log(y)/10)
		int valueUnit = (int) Math.ceil(Math.exp(Math.log(maxValue) / 10));

		int maxTotalValue = -1;
		for (int i = 0; i < columnCount; ++i) {
			int value = totalCount[i];
			if (value > maxTotalValue) {
				maxTotalValue = value;
			}
		}

		int maxValueUnit = (int) Math.ceil(Math.exp(Math.log(maxTotalValue) / 10));
		double logOfMaxValueUnit = Math.log(maxValueUnit);

		StringBuffer countHead = new StringBuffer();
		for (int i = 0; i < columnCount; ++i) {

			int value = totalCount[i];
			if (value == 0) {

				countHead.append('.');

			} else if (value == 1) {

				countHead.append('1');

			} else {

				// Calculate value : x(value unit), y(count value), z(value)
				// X^Y = Z ==> Y = log(Y)/Log(X)
				int dv = (int) Math.ceil(Math.log(value) / logOfMaxValueUnit) + 1;
				if (dv >= 10) {
					countHead.append('X');
				} else {
					countHead.append("" + dv);
				}
			}
		}

		countHead.append(String.format(" max=%d, unit=%d^x", maxTotalValue, maxValueUnit));

		lines.add(String.format("row=%d, column=%d(%d), value-max=%d, value-unit=%d^x", rowCount, columnCount,
				maxTimeUnit, maxValue, valueUnit));
		lines.add(countHead.toString());

		double logOfvalueUnit = Math.log(valueUnit);

		for (DeCounter counter : counters) {

			StringBuffer valueLine = new StringBuffer();

			for (int i = 0; i < columnCount; ++i) {

				int value = counter.getCount(i);
				if (value == 0) {

					valueLine.append(' ');

				} else if (value == 1) {

					valueLine.append('1');

				} else {

					// Calculate value : x(value unit), y(count value), z(value)
					// X^Y = Z ==> Y = log(Y)/Log(X)
					int dv = (int) Math.ceil(Math.log(value) / logOfvalueUnit) + 1;
					if (dv >= 10) {
						valueLine.append('X');
					} else {
						valueLine.append("" + dv);
					}
				}
			}

			lines.add(valueLine.toString());
		}

		return lines;
	}

	public static String outputFrameTree(IRInterpreter interpreter) throws RException {

		return new FrameTree(RulpFactory.listGlobalFrames(), interpreter).output();
	}

	public static String printFrame(IRFrame frame) throws RException {
		return printFrame(frame, null);
	}

	public static String printFrame(IRFrame frame, Collection<IRObject> values) throws RException {

		StringBuffer sb = new StringBuffer();

		List<IRFrameEntry> frameEntries = new ArrayList<>(frame.listEntries());
		Collections.sort(frameEntries, (f1, f2) -> {
			return f1.getEntryId() - f2.getEntryId();
		});

		sb.append(String.format("id=%d, name=%s, lvl=%d, ref=%d/%d, entry=%d", frame.getFrameId(), frame.getFrameName(),
				frame.getLevel(), frame.getRef(), frame.getMaxRef(), frameEntries.size()));

		IRFrame frameParent = frame.getParentFrame();
		if (frameParent != null) {
			sb.append(String.format(", pid=%d(%s)", frameParent.getFrameId(), frameParent.getFrameName()));
		}

		IRSubject frameSubject = frame.getSubject();
		if (frameSubject != null) {
			sb.append(String.format(", subject=%s", frameSubject.getSubjectName()));
		}

		IRThreadContext frameThreadContext = frame.getThreadContext();
		if (frameThreadContext != null) {
			sb.append(String.format(", thread=%s",
					"" + frameThreadContext.isCompleted() + "/" + frameThreadContext.getResultCount()));
		}

		List<IRFrame> searchFrameList = frame.getSearchFrameList();
		if (searchFrameList != null) {
			sb.append(String.format(", search=%s", "" + searchFrameList));
		}

		sb.append("\n");

		if (!frameEntries.isEmpty()) {

			sb.append(SEP_LINE1);
			sb.append(String.format("%-20s : %-8s %-8s %-4s %-10s %-20s\n", "Name(alias)", "EntryId", "FrameId", "Ref",
					"Type", "Value"));
			sb.append(SEP_LINE2);

			for (IRFrameEntry entry : frameEntries) {

				IRObject entryObj = entry.getObject();

				String name = _getEntryAliasName(entry);
				IRFrame entryFrame = entry.getFrame();

				sb.append(String.format("%-20s : %-8d %-8s %-4s %-10s %-20s\n", name, entry.getEntryId(),
						entryFrame == null ? -1 : entryFrame.getFrameId(), _toRefString(entryObj),
						_toTypeString(entryObj), _toString(entryObj)));

				if (RulpUtil.isFunctionList(entryObj)) {
					for (IRFunction func : RulpUtil.asFunctionList(entryObj).getAllFuncList()) {
						sb.append(String.format("%40s %-4s %-10s %s\n", "", _toRefString(func), _toTypeString(func),
								_toString(func)));
					}
				}

				if (values != null) {
					values.add(entryObj);
				}
			}

			sb.append(SEP_LINE1);
		}

		return sb.toString();
	}

	public static String printGlobalInfo(IRInterpreter interpreter) throws RException {

		StringBuffer sb = new StringBuffer();

		sb.append("Global Info:\n\n");

		/***********************************************/
		// total runtime info
		/***********************************************/
		_printTotalRunTimeInfo(sb, interpreter);

		/***********************************************/
		// Optimize info
		/***********************************************/
		sb.append("Optimize info:\n");
		printOptimizeInfo(sb, interpreter);
		sb.append("\n");

		/***********************************************/
		// expression compute count
		/***********************************************/
		_printExpressionComputeCount(sb, interpreter);

		/***********************************************/
		// Callable stats info
		/***********************************************/
		_printCallableStatsInfo(sb, interpreter);

		/***********************************************/
		// Annotation Builders
		/***********************************************/
		_printCallerAnnotation(sb, interpreter);

		/***********************************************/
		// object create count
		/***********************************************/
		_printObjectCreateCount(sb, interpreter);

		/***********************************************/
		// Global frame list
		/***********************************************/
		Set<IRObject> frameObjs = new HashSet<>();

		List<IRFrame> globalFrames = RulpFactory.listGlobalFrames();
		sb.append(String.format("Global frame list: total=%d\n", globalFrames.size()));
		sb.append("\n");

		sb.append(printFrame(interpreter.getMainFrame(), frameObjs));
		sb.append("\n");

		for (IRFrame frame : globalFrames) {
			sb.append("\n");
			sb.append(printFrame(frame, frameObjs));
		}

		/***********************************************/
		// Root var list
		/***********************************************/
		{
			ArrayList<IRVar> rootVars = new ArrayList<>();
			for (IRFrameEntry frameEntry : interpreter.getMainFrame().getParentFrame().listEntries()) {
				IRObject value = frameEntry.getValue();
				if (value.getType() == RType.VAR) {
					rootVars.add((IRVar) value);
				}
			}

			Collections.sort(rootVars, (v1, v2) -> {
				return v1.getName().compareTo(v2.getName());
			});

			sb.append("\n");
			sb.append(String.format("Root var list: total=%d\n", rootVars.size()));
			sb.append(SEP_LINE1);
			sb.append(String.format("%-20s : %s\n", "Var", "Value"));
			sb.append(SEP_LINE2);

			for (IRVar var : rootVars) {
				sb.append(String.format("%-20s : %s\n", var.getName(), _toString(var.getValue())));
			}

			sb.append(SEP_LINE1);
		}
		/***********************************************/
		// Frame tree
		/***********************************************/
		sb.append("\n");
		sb.append(String.format("Global frame tree:\n"));
		sb.append("\n");
		sb.append(outputFrameTree(interpreter));

		/***********************************************/
		// Global Subject list
		/***********************************************/
		sb.append("\n");
		sb.append("Global Subject list:\n");
		sb.append("\n");

		ArrayList<IRObject> sortedObjs = new ArrayList<>(frameObjs);
		Collections.sort(sortedObjs, (o1, o2) -> {
			return o1.toString().compareTo(o2.toString());
		});

		Set<IRObject> visitedObjs = new HashSet<>();
		Queue<IRObject> visitingObjs = new LinkedList<>(sortedObjs);

		while (!visitingObjs.isEmpty()) {

			IRObject obj = visitingObjs.poll();
			if (obj.getType() == RType.FRAME) {
				continue;
			}

			if (!(obj instanceof IRSubject)) {
				continue;
			}

			if (visitedObjs.contains(obj)) {
				continue;
			}

			ArrayList<IRObject> subObjs = new ArrayList<>();
			sb.append(printSubject((IRSubject) obj, subObjs));
			sb.append("\n");
			visitingObjs.addAll(subObjs);
			visitedObjs.add(obj);
		}

		return sb.toString();
	}

	public static void printOptimizeInfo(StringBuffer sb, IRInterpreter interpreter) throws RException {

		if (optTracerNameList == null) {
			return;
		}

		sb.append(SEP_LINE1);
		sb.append(String.format("%10s: %s\n", "Name", "Status"));
		sb.append(SEP_LINE2);

		if (optTracerNameList != null) {
			for (String name : optTracerNameList) {
				IROptInfoTracer tracer = optTracerMap.get(name);
				sb.append(String.format("%10s: %s\n", name, tracer.printInfo(interpreter)));
			}
		}

		sb.append(SEP_LINE1);

	}

	public static String printSubject(IRSubject subject) throws RException {
		return printSubject(subject, null);
	}

	public static String printSubject(IRSubject subject, Collection<IRObject> values) throws RException {

		StringBuffer sb = new StringBuffer();

		IRSubject subParent = subject.getParent();
		IRFrame subFrame = subject.hasSubjectFrame() ? subject.getSubjectFrame() : null;

		ArrayList<IRMember> mbrs = new ArrayList<>(subject.listMembers());
		Collections.sort(mbrs, (m1, m2) -> {
			return m1.getName().compareTo(m2.getName());
		});

		sb.append(String.format("name=%s, string=%s, lvl=%d, ref=%d/%d, parent=%s, final=%s, mbrs=%d",
				subject.getSubjectName(), subject.toString(), subject.getLevel(), subject.getRef(), subject.getMaxRef(),
				subParent == null ? "null" : subParent.getSubjectName(), subject.isFinal(), mbrs.size()));

		if (subFrame != null) {
			sb.append(String.format(", frame=%d(%s)", subFrame.getFrameId(), subFrame.getFrameName()));
		} else {
			sb.append(", frame=null");
		}

		sb.append("\n");
		sb.append(SEP_LINE1);
		sb.append(String.format("%-30s : %-7s %-5s %-6s %-7s %-4s %-4s %-10s %s\n", "Name", "Access", "Final", "Static",
				"Inherit", "Prop", "Ref", "Type", "Value"));
		sb.append(SEP_LINE2);

		for (IRMember mbr : mbrs) {

			IRObject value = mbr.getValue();

			sb.append(String.format("%-30s : %-7s %-5s %-6s %-7s %-4d %-4s %-10s %s\n", mbr.getName(),
					mbr.getAccessType(), RulpUtil.isPropertyFinal(mbr) ? "Y" : "",
					RulpUtil.isPropertyStatic(mbr) ? "Y" : "", RulpUtil.isPropertyInherit(mbr) ? "Y" : "",
					mbr.getProperty(), _toRefString(value), _toTypeString(value), _toString(value)));

			if (RulpUtil.isFunctionList(value)) {
				for (IRFunction func : RulpUtil.asFunctionList(mbr.getValue()).getAllFuncList()) {
					sb.append(String.format("%66s %-4s %-10s %s\n", "", _toRefString(func), _toTypeString(func),
							_toString(func)));
				}
			}

			if (values != null) {
				values.add(value);
			}
		}
		sb.append(SEP_LINE1);

		return sb.toString();
	}

	static void registerOptInfoTracer(String name, IROptInfoTracer tracer) {

		if (optTracerNameList.contains(name)) {
			return;
		}

		optTracerNameList.add(name);
		optTracerMap.put(name, tracer);
	}

}
