/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utils;

import static alpha.rulp.lang.Constant.A_NAMESPACE;
import static alpha.rulp.lang.Constant.A_NIL;
import static alpha.rulp.lang.Constant.O_Nil;
import static alpha.rulp.lang.Constant.S_QUESTION;
import static alpha.rulp.lang.Constant.S_QUESTION_C;
import static alpha.rulp.lang.Constant.T_Atom;
import static alpha.rulp.lang.Constant.T_Instance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
import alpha.rulp.lang.IRMap;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRNative;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRString;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RAccessType;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFactorBody;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRFunctionList;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRMacro;
import alpha.rulp.runtime.IRTemplate;
import alpha.rulp.runtime.IRTemplate.TemplatePara;
import alpha.rulp.runtime.IRTemplate.TemplateParaEntry;
import alpha.rulp.runtime.RName;
import alpha.rulp.ximpl.collection.XRMap;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;
import alpha.rulp.ximpl.network.XRSocket;

public class RulpUtil {

	static interface IRFormater {
		public void format(StringBuffer sb, IRObject obj) throws RException;
	}

	static class XRFactorWrapper extends AbsRFactorAdapter implements IRFactor {

		private IRFactorBody factorBody;

		private boolean threadSafe;

		public XRFactorWrapper(String factorName, IRFactorBody factorBody, boolean threadSafe) {
			super(factorName);
			this.factorBody = factorBody;
			this.threadSafe = threadSafe;
		}

		@Override
		public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {
			return factorBody.compute(args, interpreter, frame);
		}

		public boolean isThreadSafe() {
			return threadSafe;
		}

	}

	static class XRFormater implements IRFormater {

		@Override
		public void format(StringBuffer sb, IRObject obj) throws RException {

			if (obj == null) {
				sb.append(A_NIL);
				return;
			}

			switch (obj.getType()) {
			case NIL:
				sb.append(A_NIL);
				break;

			case ATOM:
				IRAtom atom = (IRAtom) obj;
				RName rName = atom.getRName();
				sb.append(rName == null ? atom.getName() : rName.getShorName());
				break;

			case INT:
				sb.append(((IRInteger) obj).asInteger());
				break;

			case LONG:
				sb.append("" + ((IRLong) obj).asLong() + "L");
				break;

			case FLOAT:
				sb.append(((IRFloat) obj).asFloat());
				break;

			case DOUBLE:
				sb.append("" + ((IRDouble) obj).asDouble() + "D");
				break;

			case BOOL:
				sb.append(((IRBoolean) obj).asBoolean());
				break;

			case FACTOR:
				sb.append(((IRFactor) obj).getName());
				break;

			case TEMPLATE:
				sb.append(((IRTemplate) obj).getName());
				break;

			case FUNC:
				sb.append(((IRFunction) obj).getName());
				break;

			case MACRO:
				sb.append(((IRMacro) obj).getName());
				break;

			case STRING:
				sb.append("\"");
				sb.append(StringUtil.addEscapeString(((IRString) obj).asString()));
				sb.append("\"");
				break;

			case VAR:
				sb.append("&");
				sb.append(((IRVar) obj).getName());
				break;

			case INSTANCE:
				sb.append(((IRInstance) obj).asString());
				break;

			case CLASS:
				sb.append(((IRClass) obj).asString());
				break;

			case NATIVE:
				sb.append(((IRNative) obj).asString());
				break;

			case FRAME:
				sb.append(((IRFrame) obj).getFrameName());
				break;

			default:
				throw new RException("unsupport type: " + obj.getType() + ", " + obj.toString());
			}
		}
	}

	static class XShortFormater extends XRFormater {

		private boolean bShort = false;

		private final int maxLength;

		public XShortFormater(int maxLength) {
			super();
			this.maxLength = maxLength;
		}

		@Override
		public void format(StringBuffer sb, IRObject obj) throws RException {

			if (bShort) {
				return;
			}

			if (sb.length() >= maxLength) {
				bShort = true;
				return;
			}

			super.format(sb, obj);
		}

	}

	static IRFormater objFormater = new XRFormater();

	static IRFormater printFormater = new XRFormater() {

		@Override
		public void format(StringBuffer sb, IRObject obj) throws RException {

			if (obj == null) {
				sb.append(A_NIL);
				return;
			}

			switch (obj.getType()) {
			case STRING:
				sb.append(StringUtil.removeEscapeString(((IRString) obj).asString()));
				break;

			default:
				super.format(sb, obj);
			}
		}
	};

	private static final String R_ATOM_PRE = "$$a_";

	private static final String R_BOOL_PRE = "$$b_";

	private static final String R_CLASS_PRE = "$$c_";

	private static final String R_FACTOR_PRE = "$$ff_";

	private static final String R_FLOAT_PRE = "$$f_";

	private static final String R_INT_PRE = "$$i_";

	private static final String R_NAME_NIL = "$$nil";

	private static final String R_NAME_NULL = "$$null";

	private static final String R_TEMPALTE_PRE = "$$tp_";

	private static final String R_VAR_PRE = "$$v_";

	private static void _toString(StringBuffer sb, IRIterator<? extends IRObject> iterator, IRFormater formater)
			throws RException {

		int i = 0;
		while (iterator.hasNext()) {

			if (i++ != 0) {
				sb.append(' ');
			}

			_toString(sb, iterator.next(), formater);
		}
	}

	private static void _toString(StringBuffer sb, IRObject obj, IRFormater formater) throws RException {

		if (obj == null) {
			sb.append("nil");
			return;
		}

		switch (obj.getType()) {
		case LIST:

			IRList list = (IRList) obj;
			if (list.getNamedName() != null) {
				sb.append(list.getNamedName());
				sb.append(':');
			}

			sb.append("'(");
			_toString(sb, list.iterator(), formater);
			sb.append(")");
			break;

		case ARRAY:

			IRArray arr = (IRArray) obj;
			if (arr.dimension() != 1) {
				throw new RException("high dimension: " + arr.dimension());
			}

			sb.append("{");
			int size = arr.size(0);
			for (int i = 0; i < size; ++i) {
				if (i != 0) {
					sb.append(',');
				}
				_toString(sb, arr.get(i), formater);
			}
			sb.append("}");
			break;

		case EXPR: {

			IRExpr expr = (IRExpr) obj;
			sb.append(expr.isEarly() ? "$(" : "(");
			_toString(sb, expr.iterator(), formater);
			sb.append(")");
			break;
		}

		case MEMBER:
			IRMember mbr = (IRMember) obj;
			_toString(sb, mbr.getSubject(), formater);
			sb.append("::");
			sb.append(mbr.getName());
			break;

		default:
			formater.format(sb, obj);
		}
	}

	private static void _toStringList(IRObject obj, List<String> list) throws RException {

		switch (obj.getType()) {
		case STRING:
			list.add(((IRString) obj).asString());
			break;

		case ATOM:
			list.add(((IRAtom) obj).getName());
			break;

		case EXPR:
		case LIST:
			IRIterator<? extends IRObject> iter = ((IRList) obj).iterator();
			while (iter.hasNext()) {
				list.addAll(toStringList(iter.next()));
			}
			break;

		default:
			throw new RException("Can't conver to string list: " + obj.toString());
		}
	}

	private static String _uniqStringIterator(IRIterator<? extends IRObject> iterator) throws RException {

		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (iterator.hasNext()) {
			if (i++ != 0) {
				sb.append(' ');
			}

			sb.append(toUniqString(iterator.next()));
		}

		return sb.toString();
	}

	public static void addAll(Collection<IRObject> l1, IRIterator<? extends IRObject> it) throws RException {
		while (it.hasNext()) {
			l1.add(it.next());
		}
	}

	public static void addAll(Collection<IRObject> l1, IRList l2) throws RException {
		addAll(l1, l2.iterator());
	}

	public static void addAll(Collection<IRObject> l1, IRList l2, int begin, int end) throws RException {

		for (int i = begin; i < end; ++i) {
			l1.add(l2.get(i));
		}
	}

	public static void addFactor(IRFrame frame, String factorName, IRFactorBody factorBody) throws RException {
		addFactor(frame, factorName, factorBody, false);
	}

	public static void addFactor(IRFrame frame, String factorName, IRFactorBody factorBody, boolean threadSafe)
			throws RException {
		frame.setEntry(factorName, new XRFactorWrapper(factorName, factorBody, threadSafe));
	}

	public static void addFrameObject(IRFrame frame, IRObject obj) throws RException {

		switch (obj.getType()) {
		case NIL:
		case ATOM:
			frame.setEntry(((IRAtom) obj).getName(), obj);
			break;

		case BOOL:
			frame.setEntry(((IRBoolean) obj).asString(), obj);
			break;

		case FACTOR:
			frame.setEntry(((IRFactor) obj).getName(), obj);
			break;

		case TEMPLATE:
			frame.setEntry(((IRTemplate) obj).getName(), obj);
			break;

		case INSTANCE:
			frame.setEntry(((IRInstance) obj).getInstanceName(), obj);
			break;

		case VAR:
			frame.setEntry(((IRVar) obj).getName(), obj);
			break;

		case CLASS:
			frame.setEntry(((IRClass) obj).getClassName(), obj);
			addFrameObject(frame, ((IRClass) obj).getClassTypeAtom());
			break;

		default:
			throw new RException("Invalid object: " + obj);
		}

	}

	public static IRTemplate addTemplate(IRFrame frame, String templateName, IRCallable templateBody,
			int totalParaCount, String... fixedParaNames) throws RException {

		int fixedParaCount = fixedParaNames.length;

		// Add entry
		TemplateParaEntry paraEntry = new TemplateParaEntry();
		paraEntry.body = templateBody;
		paraEntry.isVary = totalParaCount > (fixedParaCount + 1);
		paraEntry.fixedParaCount = fixedParaCount;

		if (fixedParaCount > 0) {

			paraEntry.fixedParas = new TemplatePara[fixedParaCount];

			for (int i = 0; i < fixedParaCount; ++i) {

				TemplatePara tp = new TemplatePara();
				tp.isVar = false;

				IRAtom fixPara = RulpFactory.createAtom(fixedParaNames[i]);
				IRFrameEntry fixParaEntry = RuntimeUtil.lookupFrameEntry(fixPara, frame);
				if (fixParaEntry != null) {
					IRObject fixParaObj = fixParaEntry.getObject();
					tp.paraType = RulpUtil.getObjectType(fixParaObj);
					tp.paraValue = fixParaObj;
				} else {
					tp.paraType = T_Atom;
					tp.paraValue = RulpFactory.createAtom(fixedParaNames[i]);
				}

				paraEntry.fixedParas[i] = tp;
			}
		}

		return addTemplate(frame, templateName, paraEntry);
	}

	public static IRTemplate addTemplate(IRFrame frame, String templateName, IRFactorBody templateBody,
			int totalParaCount, String... fixedParaNames) throws RException {

		return addTemplate(frame, templateName, new XRFactorWrapper(templateName, templateBody, false), totalParaCount,
				fixedParaNames);
	}

	public static IRTemplate addTemplate(IRFrame frame, String templateName, TemplateParaEntry paraEntry)
			throws RException {

		IRTemplate template = null;

		// Create template
		IRFrameEntry entry = frame.getEntry(templateName);
		if (entry == null) {
			template = RulpFactory.createTemplate(templateName, frame);
			frame.setEntry(templateName, template);

		} else {

			template = RulpUtil.asTemplate(entry.getValue());

			// Copy template
			if (template.getDefineFrame() != frame) {

				List<TemplateParaEntry> oldTpEntrys = template.getTemplateParaEntryList();
				template = RulpFactory.createTemplate(templateName, frame);
				for (TemplateParaEntry tpEntry : oldTpEntrys) {
					template.addEntry(tpEntry);
				}

				frame.setEntry(templateName, template);
			}
		}

		template.addEntry(paraEntry);

		return template;
	}

	public static IRArray asArray(IRObject obj) throws RException {

		if (obj.getType() != RType.ARRAY) {
			throw new RException("Can't convert to array: " + obj);
		}

		return (IRArray) obj;
	}

	public static IRAtom asAtom(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.ATOM) {
			throw new RException("Can't convert to atom: " + obj);
		}

		return (IRAtom) obj;
	}

	public static IRBoolean asBoolean(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.BOOL) {
			throw new RException("Can't convert to bool: " + obj);
		}

		return (IRBoolean) obj;
	}

	public static IRClass asClass(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.CLASS) {
			throw new RException("Can't convert to class: " + obj);
		}

		return (IRClass) obj;
	}

	public static IRDouble asDouble(IRObject obj) throws RException {

		if (obj.getType() != RType.DOUBLE) {
			throw new RException("Can't convert to double: " + obj);
		}

		return (IRDouble) obj;
	}

	public static IRError asError(IRObject obj) throws RException {

		if (!(obj instanceof IRError)) {
			throw new RException("Can't convert to error: " + obj);
		}

		return (IRError) obj;
	}

	public static IRExpr asExpression(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.EXPR) {
			throw new RException("Can't convert to expression: " + obj);
		}

		return (IRExpr) obj;
	}

	public static IRFactor asFactor(IRObject obj) throws RException {

		if (obj.getType() != RType.FACTOR) {
			throw new RException("Can't convert to factor: " + obj);
		}

		return (IRFactor) obj;
	}

	public static IRFloat asFloat(IRObject obj) throws RException {

		if (obj.getType() != RType.FLOAT) {
			throw new RException("Can't convert to float: " + obj);
		}

		return (IRFloat) obj;
	}

	public static IRFrame asFrame(IRObject obj) throws RException {

		if (obj.getType() != RType.FRAME) {
			throw new RException("Can't convert to member: " + obj);
		}

		return (IRFrame) obj;
	}

	public static IRFunction asFunction(IRObject obj) throws RException {

		if (obj.getType() != RType.FUNC) {
			throw new RException("Can't convert to function: " + obj);
		}

		return (IRFunction) obj;
	}

	public static IRFunctionList asFunctionList(IRObject obj) throws RException {

		if (!(obj instanceof IRFunctionList)) {
			throw new RException("Can't convert to funclist: " + obj);
		}

		return (IRFunctionList) obj;
	}

	public static IRInstance asInstance(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.INSTANCE) {
			throw new RException("Can't convert to instance: " + obj);
		}

		return (IRInstance) obj;
	}

	public static IRInteger asInteger(IRObject obj) throws RException {

		if (obj.getType() != RType.INT) {
			throw new RException("Can't convert to integer: " + obj);
		}

		return (IRInteger) obj;
	}

	public static IRList asList(IRObject obj) throws RException {

		if (obj == null) {
			return RulpFactory.EMPTY_LIST;
		}

		if (obj.getType() != RType.LIST) {
			throw new RException("Can't convert to list: " + obj);
		}

		return (IRList) obj;
	}

	public static IRLong asLong(IRObject obj) throws RException {

		if (obj.getType() != RType.LONG) {
			throw new RException("Can't convert to long: " + obj);
		}

		return (IRLong) obj;
	}

	public static IRMacro asMacro(IRObject obj) throws RException {

		if (obj.getType() != RType.MACRO) {
			throw new RException("Can't convert to macro: " + obj);
		}

		return (IRMacro) obj;
	}

	public static XRMap asMap(IRObject obj) throws RException {

		if (!(obj instanceof XRMap)) {
			throw new RException("Can't convert object to map: " + obj);
		}

		return (XRMap) obj;
	}

	public static IRMember asMember(IRObject obj) throws RException {

		if (obj.getType() != RType.MEMBER) {
			throw new RException("Can't convert to member: " + obj);
		}

		return (IRMember) obj;
	}

	public static IRSubject asNameSpace(IRObject obj) throws RException {

		if (obj.getType() == RType.FRAME) {
			return (IRSubject) obj;
		}

		if (obj instanceof IRInstance && ((IRInstance) obj).getParent().getSubjectName().equals(A_NAMESPACE)) {
			return (IRSubject) obj;
		}

		throw new RException("Can't convert to namespace: " + obj);
	}

	@SuppressWarnings("unchecked")
	public static <T> T asNative(IRObject obj, Class<T> c) throws RException {

		if (obj.getType() != RType.NATIVE) {
			throw new RException("Can't convert to native: " + obj);
		}

		IRNative nativeObj = (IRNative) obj;
		Class<?> nativeClass = nativeObj.getNativeClass();

		if (!c.isAssignableFrom(nativeClass)) {
			throw new RException("Can't convert to class: " + c);
		}

		return (T) nativeObj.getObject();
	}

	public static XRSocket asSocket(IRObject obj) throws RException {

		if (!(obj instanceof XRSocket)) {
			throw new RException("Can't convert object to Socket: " + obj);
		}

		return (XRSocket) obj;
	}

	public static IRString asString(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.STRING) {
			throw new RException("Can't convert to string: " + obj);
		}

		return (IRString) obj;
	}

	public static IRSubject asSubject(IRObject obj) throws RException {

		if (!(obj instanceof IRSubject)) {
			throw new RException("Can't convert to subject: " + obj);
		}

		return (IRSubject) obj;
	}

	public static IRTemplate asTemplate(IRObject obj) throws RException {

		if (obj.getType() != RType.TEMPLATE) {
			throw new RException("Can't convert to factor: " + obj);
		}

		return (IRTemplate) obj;
	}

	public static IRVar asVar(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.VAR) {
			throw new RException("Can't convert to var: " + obj);
		}

		return (IRVar) obj;
	}

	public static IRList buildList(List<String> strlist) throws RException {

		LinkedList<IRObject> objList = new LinkedList<>();
		for (String str : strlist) {
			objList.add(RulpFactory.createString(str));
		}

		return RulpFactory.createList(objList);
	}

	public static int compare(IRObject a, IRObject b) throws RException {

		if (a == b) {
			return 0;
		}

		if (a == null) {
			a = O_Nil;
		}

		if (b == null) {
			b = O_Nil;
		}

		return a.asString().compareTo(b.asString());
	}

	public static void decRef(IRObject obj) throws RException {

		if (obj == null) {
			return;
		}

//		if (RulpUtil.toString(obj).equals("&x")) {
//			System.out.println();
//		}

		obj.decRef();
	}

	public static boolean equal(IRObject a, IRObject b) throws RException {

		if (a == b) {
			return true;
		}

		if (a == null) {
			return b == O_Nil;
		}

		if (b == null) {
			return a == O_Nil;
		}

		if (a.getType() != b.getType()) {
			return false;
		}

		boolean rc = false;

		switch (a.getType()) {
		case ATOM:
		case STRING:
			rc = a.asString().equals(b.asString());
			break;

		case BOOL:
			rc = ((IRBoolean) a).asBoolean() == ((IRBoolean) b).asBoolean();
			break;

		case FLOAT:
			rc = ((IRFloat) a).asFloat() == ((IRFloat) b).asFloat();
			break;

		case INT:
			rc = ((IRInteger) a).asInteger() == ((IRInteger) b).asInteger();
			break;

		case LONG:
			rc = ((IRLong) a).asLong() == ((IRLong) b).asLong();
			break;

		case NIL:
			return true;

		case EXPR:
		case LIST:

			IRList la = (IRList) a;
			IRList lb = (IRList) b;

			if (la.size() != lb.size()) {
				return false;
			}

			IRIterator<? extends IRObject> ia = la.iterator();
			IRIterator<? extends IRObject> ib = lb.iterator();

			while (ia.hasNext()) {
				if (!equal(ia.next(), ib.next())) {
					return false;
				}
			}

			return true;

		default:
			return false;
		}

		return rc;
	}

	public static void expectFactorParameterType(List<IRObject> args, RType type) throws RException {

		for (int i = 1; i < args.size(); ++i) {
			IRObject arg = args.get(i);
			if (arg.getType() != type) {
				throw new RException(
						String.format("the type of arg %d is not %s: %s", 1, type.toString(), args.get(1)));
			}
		}
	}

	public static IRClass findClass(IRObject obj, IRFrame frame) throws RException {

		switch (obj.getType()) {

		case ATOM:
			IRFrameEntry entry = frame.getEntry(RulpUtil.asAtom(obj).getName());
			if (entry != null && entry.getObject() != obj) {
				return findClass(entry.getObject(), frame);
			}

			return null;

		case INSTANCE:
			return RulpUtil.asInstance(obj).getRClass();

		case CLASS:
			return (IRClass) obj;

		default:
			return null;
		}
	}

	public static IRAtom getObjectType(IRObject valObj) throws RException {

		IRAtom valAtom = RType.toObject(valObj.getType());
		if (valAtom == T_Instance) {
			valAtom = RulpUtil.asInstance(valObj).getRClass().getClassTypeAtom();
		}

		return valAtom;
	}

//	public static IRSubject getUsingNameSpace(IRFrame frame) throws RException {
//
//		IRObject nsObj = frame.getObject(A_USING_NS);
//		if (nsObj == null) {
//			return null;
//		}
//
//		return RulpUtil.asSubject(nsObj);
//	}

	public static void incRef(IRObject obj) throws RException {

		if (obj == null) {
			return;
		}

//		if (RulpUtil.toString(obj).equals("DO")) {
//			System.out.println();
//		}

		obj.incRef();
	}

	public static boolean isAnonymousVar(String var) {
		return var.equals(S_QUESTION);
	}

	public static boolean isAtom(IRObject obj) {
		return obj.getType() == RType.ATOM;
	}

	public static boolean isAtom(IRObject obj, String name) {
		return obj.getType() == RType.ATOM && ((IRAtom) obj).getName().equals(name);
	}

	public static boolean isExpression(IRObject obj) {
		return obj.getType() == RType.EXPR;
	}

	public static boolean isFunctionLambda(IRObject obj) throws RException {
		return obj.getType() == RType.FUNC && ((IRFunction) obj).isLambda();
	}

	public static boolean isFunctionList(IRObject obj) throws RException {
		return obj.getType() == RType.FUNC && ((IRFunction) obj).isList();
	}

	public static boolean isList(IRObject obj) {
		return obj.getType() == RType.LIST;
	}

	public static boolean isNamedList(IRObject obj) throws RException {

		if (obj.getType() != RType.LIST) {
			return false;
		}

		return ((IRList) obj).getNamedName() != null;
	}

	public static boolean isPureAtomList(IRObject obj) throws RException {

		RType type = obj.getType();
		if (type != RType.LIST && type != RType.EXPR) {
			return false;
		}

		IRIterator<? extends IRObject> iter = ((IRList) obj).iterator();
		while (iter.hasNext()) {
			if (iter.next().getType() != RType.ATOM) {
				return false;
			}
		}

		return true;
	}

	public static boolean isPureAtomPairList(IRObject obj) throws RException {

		RType type = obj.getType();
		if (type != RType.LIST && type != RType.EXPR) {
			return false;
		}

		IRIterator<? extends IRObject> iter = ((IRList) obj).iterator();
		while (iter.hasNext()) {

			IRObject element = iter.next();

			if (element.getType() == RType.ATOM) {
				continue;
			} else {

			}

			if (!isPureAtomList(element) || ((IRList) element).size() != 2) {
				return false;
			}
		}

		return true;
	}

	public static boolean isQuote(IRObject obj) {

		if (obj.getType() == RType.ATOM) {
			String name = ((IRAtom) obj).getName();
			return name.equals("'") || name.equalsIgnoreCase("quote");
		} else {
			return false;
		}
	}

	public static boolean isValidRulpStmt(String line) {

		try {

			List<IRObject> rt = RulpFactory.createParser().parse(line);
			if (rt.isEmpty()) {
				return false;
			}

			for (IRObject obj : rt) {
				if (obj.getType() != RType.EXPR) {
					return false;
				}
			}

			return true;

		} catch (RException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean isValueAtom(IRObject obj) {
		return obj.getType() == RType.ATOM && !isVarName(((IRAtom) obj).getName());
	}

	public static boolean isVarAtom(IRObject obj) {
		return obj.getType() == RType.ATOM && isVarName(((IRAtom) obj).getName());
	}

	public static boolean isVarName(String var) {
		return var.length() > 1 && var.charAt(0) == S_QUESTION_C;
	}

	public static boolean matchType(IRAtom typeAtom, IRObject obj) throws RException {

		// Match any object
		if (typeAtom == O_Nil) {
			return true;
		}

		return getObjectType(obj) == typeAtom;
	}

	public static void setMember(IRSubject subject, String name, IRObject obj) throws RException {
		setMember(subject, name, obj, null);
	}

	public static void setMember(IRSubject subject, String name, IRObject obj, RAccessType accessType)
			throws RException {

		IRMember mbr = RulpFactory.createMember(subject, name, obj);
		if (accessType != null) {
			mbr.setAccessType(accessType);
		}
		subject.setMember(name, mbr);
	}

	public static ArrayList<IRObject> toArray(IRList list) throws RException {

		ArrayList<IRObject> arr = new ArrayList<>();
		IRIterator<? extends IRObject> iter = list.iterator();
		while (iter.hasNext()) {
			arr.add(iter.next());
		}
		return arr;
	}

	public static boolean toBoolean(IRObject a) throws RException {

		switch (a.getType()) {
		case NIL:
			return false;
		case BOOL:
			return ((IRBoolean) a).asBoolean();

		default:
			throw new RException(String.format("Not support type: %s", a.toString()));
		}
	}

	public static IRMap toImplMap(IRInstance instance) throws RException {
		return XRMap.toImplMap(instance);
	}

	public static <T> List<T> toList(IRIterator<T> iter) throws RException {

		List<T> list = new ArrayList<>();
		while (iter.hasNext()) {
			list.add(iter.next());
		}

		return list;
	}

	public static String toString(IRObject obj) throws RException {

		StringBuffer sb = new StringBuffer();
		_toString(sb, obj, objFormater);

		return sb.toString();
	}

	public static String toString(IRObject obj, int maxLength) throws RException {

		StringBuffer sb = new StringBuffer();
		XShortFormater formater = new XShortFormater(maxLength);
		_toString(sb, obj, formater);
		if (formater.bShort) {
			sb.append("...");
		}

		return sb.toString();
	}

	public static String toString(List<? extends IRObject> list) throws RException {

		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (IRObject e : list) {
			if (i++ != 0) {
				sb.append(' ');
			}
			_toString(sb, e, objFormater);
		}

		return sb.toString();
	}

	public static List<String> toStringList(IRObject obj) throws RException {

		LinkedList<String> list = new LinkedList<>();
		_toStringList(obj, list);

		return list;

	}

	public static List<String> toStringList(List<? extends IRObject> objList) throws RException {

		LinkedList<String> list = new LinkedList<>();
		for (IRObject obj : objList) {
			_toStringList(obj, list);
		}
		return list;
	}

	public static String toStringPrint(IRObject obj) throws RException {
		StringBuffer sb = new StringBuffer();
		_toString(sb, obj, printFormater);
		return sb.toString();
	}

	public static String toUniqString(IRObject obj) throws RException {

		if (obj == null) {
			return R_NAME_NULL;
		}

		switch (obj.getType()) {
		case NIL:
			return R_NAME_NIL;

		case ATOM:
			return R_ATOM_PRE + ((IRAtom) obj).getName();

		case INT:
			return R_INT_PRE + ((IRInteger) obj).asInteger();

		case FLOAT:
			return R_FLOAT_PRE + ((IRFloat) obj).asFloat();

		case BOOL:
			return R_BOOL_PRE + ((IRBoolean) obj).asBoolean();

		case FACTOR:
			return R_FACTOR_PRE + ((IRFactor) obj).getName();

		case TEMPLATE:
			return R_TEMPALTE_PRE + ((IRTemplate) obj).getName();

		case LIST:
			return "'(" + _uniqStringIterator(((IRList) obj).iterator()) + ")";

		case EXPR:
			return "(" + _uniqStringIterator(((IRExpr) obj).iterator()) + ")";

		case STRING:
			return "\"" + ((IRString) obj).asString() + "\"";

		case VAR:
			return R_VAR_PRE + ((IRVar) obj).getName();

		case INSTANCE:
			IRInstance ins = (IRInstance) obj;
			return R_CLASS_PRE + ins.getRClass().getClassName() + "_" + ins.getInstanceName();

		default:
			throw new RException("unsupport type: " + obj.getType());
		}
	}
}
