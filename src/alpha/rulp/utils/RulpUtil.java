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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
import alpha.rulp.lang.IRMap;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRNative;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRObjectIterator;
import alpha.rulp.lang.IRString;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RAccessType;
import alpha.rulp.lang.RError;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RRelationalOperator;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFactorBody;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRFunctionList;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.runtime.IRMacro;
import alpha.rulp.runtime.IRNameSpace;
import alpha.rulp.runtime.IRObjectLoader;
import alpha.rulp.runtime.IRTemplate;
import alpha.rulp.runtime.IRTemplate.TemplatePara;
import alpha.rulp.runtime.IRTemplate.TemplateParaEntry;
import alpha.rulp.runtime.RName;
import alpha.rulp.ximpl.collection.XRMap;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;
import alpha.rulp.ximpl.rclass.XRFactorNew;

public class RulpUtil {

	public interface EqualAble<T> {
		public boolean isEqualTo(T another);
	}

	static interface IRFormater {
		public void format(StringBuffer sb, IRObject obj) throws RException;
	}

	public static class RResultList {

		public List<IRObject> results = new ArrayList<>();

		public void addResult(IRObject rst) throws RException {
			RulpUtil.incRef(rst);
			results.add(rst);
		}

		public void free() throws RException {

			for (IRObject rst : results) {
				RulpUtil.decRef(rst);
			}

			results.clear();
		}
	}

	static class XRFactorWrapper extends AbsAtomFactorAdapter implements IRFactor {

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
				sb.append(StringUtil.addEscape(((IRString) obj).asString()));
				sb.append("\"");
				break;

			case VAR:
				sb.append("&");
				sb.append(((IRVar) obj).getName());
				break;

			case CONSTANT:
				sb.append(((IRConst) obj).getName());
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

	private static String _spaceLines[] = new String[1024];

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
				sb.append(StringUtil.removeEscape(((IRString) obj).asString()));
				break;

			default:
				super.format(sb, obj);
			}
		}
	};

	private static final String R_ATOM_PRE = "$$a_";

	private static final String R_BOOL_PRE = "$$b_";

	private static final String R_CLASS_PRE = "$$c_";

	private static final String R_DOUBLE_PRE = "$$d_";

	private static final String R_FACTOR_PRE = "$$ff_";

	private static final String R_FLOAT_PRE = "$$f_";

	private static final String R_INT_PRE = "$$i_";

	private static final String R_NAME_NIL = "$$nil";

	private static final String R_NAME_NULL = "$$null";

	private static final String R_TEMPALTE_PRE = "$$tp_";

	private static final String R_VAR_PRE = "$$v_";

	private static IRObject _compare_obj(IRObject obj) throws RException {

		switch (obj.getType()) {

		case CONSTANT:
			return _compare_obj(RulpUtil.asConstant(obj).getValue());

		case MEMBER:
			return _compare_obj(RulpUtil.asMember(obj).getValue());

		case VAR:
			return _compare_obj(RulpUtil.asVar(obj).getValue());

		default:
			return obj;
		}
	}

	private static void _formatAttrList(StringBuffer sb, IRObject obj) throws RException {

		List<String> attrKeyList = AttrUtil.getAttributeKeyList(obj);
		if (attrKeyList.isEmpty()) {
			return;
		}

		sb.append("[");
		Iterator<String> it = attrKeyList.iterator();
		int i = 0;
		while (it.hasNext()) {

			if (i++ != 0) {
				sb.append(' ');
			}

			String key = it.next();

			IRObject value = AttrUtil.getAttributeValue(obj, key);
			if (value == O_Nil) {
				sb.append(key);
			} else {
				sb.append(String.format("%s=%s", key, "" + value));
			}
		}

		sb.append("]");
	}

	private static String _lookupFile(String parent, String path) {

		if (FileUtil.isExistDirectory(parent)) {

			String newPath = FileUtil.toValidPath(parent) + path;
			if (FileUtil.isExistFile(newPath)) {
				return newPath;
			}

			if (!path.endsWith(A_RULP_SUFFIX)) {
				newPath = FileUtil.toValidPath(parent) + path + A_RULP_SUFFIX;
				if (FileUtil.isExistFile(newPath)) {
					return newPath;
				}
			}
		}

		return null;
	}

	private static void _toString(StringBuffer sb, IRIterator<? extends IRObject> iterator, IRFormater formater,
			boolean attr) throws RException {

		int i = 0;
		while (iterator.hasNext()) {

			if (i++ != 0) {
				sb.append(' ');
			}

			_toString(sb, iterator.next(), formater, attr);
		}
	}

	private static void _toString(StringBuffer sb, IRObject obj, IRFormater formater, boolean attr) throws RException {

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
			_toString(sb, list.iterator(), formater, attr);
			sb.append(")");
			break;

		case ARRAY:

			IRArray arr = (IRArray) obj;
			if (arr.getDimension() > 2) {
				throw new RException("invalid dimension: " + arr.getDimension());
			}

			sb.append("{");
			int size = arr.size(0);
			for (int i = 0; i < size; ++i) {
				if (i != 0) {
					sb.append(',');
				}
				_toString(sb, arr.get(i), formater, attr);
			}
			sb.append("}");
			break;

		case BLOB:

			IRBlob blob = (IRBlob) obj;
			byte[] bytes = blob.getValue();

			if (bytes == null) {
				sb.append("[]");
			} else if (bytes.length > MAX_TOSTRING_LEN) {
				sb.append("[" + EncodeUtil.convertBytesToHexString(bytes, 0, MAX_TOSTRING_LEN) + "...]");
			} else {
				sb.append("[" + EncodeUtil.convertBytesToHexString(bytes, 0, bytes.length) + "]");
			}

			break;

		case EXPR: {

			IRExpr expr = (IRExpr) obj;
			sb.append(expr.isEarly() ? "$(" : "(");
			_toString(sb, expr.iterator(), formater, attr);
			sb.append(")");
			break;
		}

		case MEMBER:
			IRMember mbr = (IRMember) obj;
			_toString(sb, mbr.getSubject(), formater, attr);
			sb.append("::");
			sb.append(mbr.getName());
			break;

		case ITERATOR:
			IRObjectIterator it = (IRObjectIterator) obj;
			sb.append("''(");
			if (it.hasNext()) {
				_toString(sb, it.peek(), formater, attr);
			}
			sb.append(")");
			break;

		default:
			formater.format(sb, obj);
		}

		if (attr) {
			_formatAttrList(sb, obj);
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

	public static void addAll(Collection<IRObject> l1, IRList l2) throws RException {
		addAll(l1, l2.iterator());
	}

	public static <T extends IRObject> void addAll(Collection<T> l1, IRIterator<? extends T> it) throws RException {
		while (it.hasNext()) {
			l1.add(it.next());
		}
	}

	public static IRArray addAll(IRArray array, IRList list) throws RException {

		if (array.isConst()) {
			throw new RException("Can't add const vary: " + array);
		}

		if (!list.isEmpty()) {
			IRIterator<? extends IRObject> it = list.iterator();
			while (it.hasNext()) {
				array.add(it.next());
			}
		}

		return array;
	}

	public static IRList addAll(IRList list, IRArray array) throws RException {

		if (array.isEmpty()) {
			return list;
		}

		ArrayList<IRObject> rstList = new ArrayList<>();
		RulpUtil.addAll(rstList, list);

		int size = array.size();
		for (int i = 0; i < size; ++i) {
			IRObject obj = array.get(i);
			if (obj == null) {
				obj = O_Nil;
			}

			rstList.add(obj);
		}

		return RulpFactory.createList(rstList);
	}

	public static void addAll(IRList list, IRIterator<? extends IRObject> it) throws RException {
		while (it.hasNext()) {
			list.add(it.next());
		}
	}

	public static IRList addAll(IRList l1, IRList l2) throws RException {

		if (l2.isEmpty()) {
			return l1;
		}

		if (l1.isEmpty()) {
			return l2;
		}

		ArrayList<IRObject> rstList = new ArrayList<>();
		RulpUtil.addAll(rstList, l1);
		RulpUtil.addAll(rstList, l2);

		return RulpFactory.createList(rstList);
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

				IRFrameEntry fixParaEntry = RuntimeUtil.lookupFrameEntry(frame, fixedParaNames[i]);
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
		IRObject obj = null;

		// Create template
		IRFrameEntry entry = frame.getEntry(templateName);
		if (entry == null) {

			template = RulpFactory.createTemplate(templateName, frame);
			frame.setEntry(templateName, template);

		} else if ((obj = entry.getValue()).getType() != RType.TEMPLATE) {

			if (entry.getFrame() != frame) {

				template = RulpFactory.createTemplate(templateName, frame);
				frame.setEntry(templateName, template);

			} else {

				throw new RException(String.format("Can't redifine <%s:%s> as a template", obj, obj.getType()));
			}

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

	public static IRVar addVar(IRFrame frame, String name) throws RException {
		IRVar var = RulpFactory.createVar(name);
		frame.setEntry(name, var);
		return var;
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

	public static IRBlob asBlob(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.BLOB) {
			throw new RException("Can't convert to blob: " + obj);
		}

		return (IRBlob) obj;
	}

	public static IRBoolean asBoolean(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.BOOL) {
			throw new RException("Can't convert to bool: " + obj);
		}

		return (IRBoolean) obj;
	}

	public static IRCallable asCallable(IRObject obj) throws RException {

		if (!(obj instanceof IRCallable)) {
			throw new RException("Can't convert object to callable: " + obj);
		}

		return (IRCallable) obj;
	}

	public static IRClass asClass(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.CLASS) {
			throw new RException("Can't convert to class: " + obj);
		}

		return (IRClass) obj;
	}

	public static IRConst asConstant(IRObject obj) throws RException {

		if (obj != null && obj.getType() != RType.CONSTANT) {
			throw new RException("Can't convert to const: " + obj);
		}

		return (IRConst) obj;
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

	public static IRObjectIterator asIterator(IRObject obj) throws RException {

		if (obj == null) {
			throw new RException("null iterator: " + obj);
		}

		if (obj.getType() != RType.ITERATOR) {
			throw new RException("Can't convert to iterator: " + obj);
		}

		return (IRObjectIterator) obj;
	}

	public static IRList asList(IRObject obj) throws RException {

		if (obj == null) {
			throw new RException("null list: " + obj);
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

	public static IRNameSpace asNameSpace(IRObject obj) throws RException {

		if (!(obj instanceof IRNameSpace)) {
			throw new RException("Can't convert object to namespace: " + obj);
		}

		return (IRNameSpace) obj;
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

	public static IRList buildListOfString(List<String> strlist) throws RException {

		if (strlist == null || strlist.isEmpty()) {
			return RulpFactory.createList();
		}

		ArrayList<IRObject> objList = new ArrayList<>();
		for (String str : strlist) {
			objList.add(RulpFactory.createString(str));
		}

		return RulpFactory.createList(objList);
	}

	public static IRList buildListOfString(String[] elements) throws RException {

		if (elements == null || elements.length == 0) {
			return RulpFactory.emptyConstList();
		}

		ArrayList<IRObject> objList = new ArrayList<>();
		for (String element : elements) {
			objList.add(RulpFactory.createString(element));
		}

		return RulpFactory.createList(objList);
	}

	public static IRObject clone(IRObject obj) throws RException {

		switch (obj.getType()) {

		case ATOM:
		case BOOL:
		case FLOAT:
		case DOUBLE:
		case INT:
		case LONG:
		case STRING:
		case NIL:
		case FACTOR:
		case TEMPLATE:
		case MACRO:
		case FUNC:
			return obj;

		case CONSTANT:
			return ((IRConst) obj).getValue();

		case LIST:
		case EXPR:
			return RuntimeUtil.rebuild(obj, new HashMap<>());

		case CLASS:
		case MEMBER:
			return obj;

		case INSTANCE:
			return obj;

		case ARRAY:
			return ((IRArray) obj).cloneArray();

		case BLOB:
			return ((IRBlob) obj).cloneBlob();

		case VAR:
			IRVar var = (IRVar) obj;
			return clone(var.getValue());

		case NATIVE:
		case FRAME:
		default:
			throw new RException("not support: " + obj);
		}
	}

	public static int compare(IRObject a, IRObject b) throws RException {

		if (a == b) {
			return 0;
		}

		if (a == null || a == O_Nil) {

			if (b == null || b == O_Nil) {
				return 0;
			}

			return -1;
		}

		if (b == null || b == O_Nil) {
			return -compare(b, a);
		}

		a = _compare_obj(a);
		b = _compare_obj(b);

		if (a == b) {
			return 0;
		}

		RType at = a.getType();
		RType bt = b.getType();

		switch (at) {
		case BOOL:
		case ATOM:
		case STRING:
		case LIST:

			switch (bt) {
			case BOOL:
			case ATOM:
			case STRING:
			case LIST:
				return a.asString().compareTo(b.asString());

			default:
				throw new RException(String.format("can't compare: %s %s", a.toString(), b.toString()));
			}

		default:
		}

		RType rt = MathUtil.getTypeConvert(at, bt);
		if (rt == null) {
			throw new RException(String.format("can't compare: %s %s", a.toString(), b.toString()));
		}

		switch (rt) {
		case DOUBLE: {

			double av = RulpUtil.toDouble(a);
			double bv = RulpUtil.toDouble(b);

			if (av > bv) {
				return 1;

			} else if (av < bv) {
				return -1;

			} else {
				return 0;
			}
		}

		case FLOAT: {

			float av = RulpUtil.toFloat(a);
			float bv = RulpUtil.toFloat(b);

			if (av > bv) {
				return 1;

			} else if (av < bv) {
				return -1;

			} else {
				return 0;
			}
		}

		case INT: {

			int av = RulpUtil.toInt(a);
			int bv = RulpUtil.toInt(b);

			if (av > bv) {
				return 1;

			} else if (av < bv) {
				return -1;

			} else {
				return 0;
			}
		}

		case LONG: {

			long av = RulpUtil.toLong(a);
			long bv = RulpUtil.toLong(b);

			if (av > bv) {
				return 1;

			} else if (av < bv) {
				return -1;

			} else {
				return 0;
			}
		}

		default:
			throw new RException(String.format("can't compare: %s %s", a.toString(), b.toString()));
		}

	}

	public static RResultList compute(IRInterpreter interpreter, String input) throws RException {

		RResultList resultList = new RResultList();

		interpreter.compute(input, (rst) -> {
			resultList.addResult(rst);
		});

		return resultList;
	}

	public static boolean containFunc(IRObject obj, String name) throws RException {

		switch (obj.getType()) {
		case EXPR: {
			IRExpr expr = (IRExpr) obj;
			if (expr.isEmpty()) {
				return false;
			}

			IRObject e0 = expr.get(0);
			if (RulpUtil.isObject(e0, name, RType.ATOM, RType.FUNC)) {
				return true;
			}

			IRIterator<? extends IRObject> it = expr.listIterator(1);
			while (it.hasNext()) {
				if (containFunc(it.next(), name)) {
					return true;
				}
			}

			return false;
		}

		case LIST: {
			IRList list = (IRList) obj;
			IRIterator<? extends IRObject> it = list.iterator();
			while (it.hasNext()) {
				if (containFunc(it.next(), name)) {
					return true;
				}
			}

			return false;
		}

		default:
			return false;
		}
	}

	public static void decRef(IRObject obj) throws RException {

		if (obj == null) {
			return;
		}

		obj.decRef();

//		if (obj.getType() == RType.ARRAY) {
//			System.out.print("decRef: " + obj.getRef() + ", " + obj);
//			System.out.println();
//		}

//		if ("class1@un2".equals(obj.toString())) {
//			System.out.print("decRef: " + obj.getRef() + ", " + obj);
//			System.out.println();
//		}

	}

	public static boolean equal(IRObject a, IRObject b) throws RException {

		if (a == b) {
			return true;
		}

		if (isNil(a)) {
			return isNil(b);
		}

		if (isNil(b)) {
			return false;
		}

		if (a.getType() != b.getType()) {
			return false;
		}

		switch (a.getType()) {
		case ATOM:
		case STRING:
			return a.asString().equals(b.asString());

		case BOOL:
			return ((IRBoolean) a).asBoolean() == ((IRBoolean) b).asBoolean();

		case FLOAT:
			return ((IRFloat) a).asFloat() == ((IRFloat) b).asFloat();

		case INT:
			return ((IRInteger) a).asInteger() == ((IRInteger) b).asInteger();

		case LONG:
			return ((IRLong) a).asLong() == ((IRLong) b).asLong();

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

	public static String formatAttribute(IRObject obj) throws RException {
		StringBuffer sb = new StringBuffer();
		_formatAttrList(sb, obj);
		return sb.toString();
	}

	public static IRAtom getObjectType(IRObject valObj) throws RException {

		IRAtom valAtom = RType.toObject(valObj.getType());
		if (valAtom == T_Instance) {
			valAtom = RulpUtil.asInstance(valObj).getRClass().getClassTypeAtom();
		}

		return valAtom;
	}

	public static String getSpaceLine(int len) {

		if (len < 0) {
			len = 0;
		}

		String line = _spaceLines[len];
		if (line == null) {

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < len * 4; ++i) {
				sb.append(' ');
			}

			line = sb.toString();
			_spaceLines[len] = line;
		}

		return line;
	}

	public static IRObject getVarValue(IRFrame frame, String varName) throws RException {

		IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, varName);
		if (entry == null) {
			return null;
		}

		IRObject entryValue = entry.getValue();
		if (entryValue == null || entryValue.getType() != RType.VAR) {
			return null;
		}

		return RulpUtil.asVar(entryValue).getValue();
	}

//	public static void expectFactorParameterType(List<IRObject> args, RType type) throws RException {
//
//		for (int i = 1; i < args.size(); ++i) {
//			IRObject arg = args.get(i);
//			if (arg.getType() != type) {
//				throw new RException(
//						String.format("the type of arg %d is not %s: %s", 1, type.toString(), args.get(1)));
//			}
//		}
//	}

	public static IRObject handle_error(IRError err, IRInterpreter interpreter, IRFrame frame) throws RException {

		String errId = err.getId().getName();

		String handleName = C_HANDLE + errId;
		String valueName = errId;

		IRFrameEntry handlEntry = frame.getEntry(handleName);
		if (handlEntry == null) {

			handlEntry = frame.getEntry(C_HANDLE_ANY);
			if (handlEntry == null) {
				return null;
			}

			valueName = RulpUtil.asAtom(frame.getEntry(C_ERROR_DEFAULT).getObject()).getName();
		}

		IRExpr catchExpr = RulpUtil.asExpression(handlEntry.getObject());
		frame.setEntry(valueName, err);

		IRObject rst = null;
		IRIterator<? extends IRObject> iter = catchExpr.listIterator(2);
		while (iter.hasNext()) {
			rst = interpreter.compute(frame, iter.next());
		}

		return rst;
	}

//	public static RType compare_type(IRObject obj) throws RException {
//
//		RType type = _compare_obj(obj).getType();
//
//		switch (type) {
//		case ATOM:
//		case BOOL:
//		case DOUBLE:
//		case FLOAT:
//		case INT:
//		case LONG:
//		case STRING:
//			return type;
//
//		default:
//			return null;
//		}
//	}

	public static void incRef(IRObject obj) throws RException {

		if (obj == null) {
			return;
		}

		obj.incRef();

//		if (obj.getType() == RType.ARRAY) {
//			System.out.print("incRef: " + obj.getRef() + ", " + obj);
//			System.out.println();
//		}

//		if ("class1@un2".equals(obj.toString())) {
//			System.out.print("incRef: " + obj.getRef() + ", " + obj);
//			System.out.println();
//		}

	}

	public static boolean isAnonymousVar(String var) {
		return var.equals(A_QUESTION);
	}

	public static boolean isAnyVar(IRObject obj) {
		return obj.getType() == RType.ATOM && ((IRAtom) obj).getName().equals(A_QUESTION);
	}

	public static boolean isAtom(IRObject obj) {
		return obj.getType() == RType.ATOM;
	}

	public static boolean isAtom(IRObject obj, String name) {
		return obj.getType() == RType.ATOM && ((IRAtom) obj).getName().equals(name);
	}

	public static boolean isEmptyExpr(IRObject obj) throws RException {

		if (obj.getType() != RType.EXPR) {
			return false;
		}

		return ((IRExpr) obj).size() == 0;
	}

	public static boolean isEqual(List<String> a, List<String> b) {

		if (a == null && b == null) {
			return true;
		}

		if (size(a) != size(b)) {
			return false;
		}

		Iterator<String> at = a.iterator();
		Iterator<String> bt = b.iterator();

		while (at.hasNext()) {

			String av = at.next();
			String bv = bt.next();

			if (!isEqual(av, bv)) {
				return false;
			}
		}

		return true;
	}

	public static boolean isEqual(String a, String b) {

		if (a == null)
			return b == null;
		else if (b == null)
			return false;

		return a.trim().equals(b.trim());
	}

	public static <E extends EqualAble<E>> boolean isEqualTo(List<E> a, List<E> b) {

		if (a == null && b == null) {
			return true;
		}

		if (size(a) != size(b)) {
			return false;
		}

		Iterator<E> at = a.iterator();
		Iterator<E> bt = b.iterator();

		while (at.hasNext()) {

			E av = at.next();
			E bv = bt.next();

			if (!av.isEqualTo(bv)) {
				return false;
			}
		}

		return true;
	}

	public static boolean isExpr(IRObject obj) {
		return obj.getType() == RType.EXPR;
	}

	public static boolean isExpr(IRObject obj, String factorName) throws RException {

		if (obj.getType() != RType.EXPR) {
			return false;
		}

		IRExpr expr = (IRExpr) obj;

		return expr.size() > 0 && isFactor(expr.get(0), factorName);
	}

	public static boolean isFactor(IRObject obj, String name) throws RException {
		return RulpUtil.isObject(obj, name, RType.ATOM, RType.FACTOR);
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

	public static boolean isNil(IRObject a) {
		return a == null || a.getType() == RType.NIL;
	}

	public static boolean isObject(IRObject obj, String name, RType... types) throws RException {

		for (RType type : types) {

			if (obj.getType() == type) {
				switch (type) {
				case ATOM:
					return ((IRAtom) obj).getName().equals(name);

				case FACTOR:
					return ((IRFactor) obj).getName().equals(name);

				case FUNC:
					return ((IRFunction) obj).getName().equals(name);

				case VAR:
					return ((IRVar) obj).getName().equals(name);

				default:
					return false;

				}
			}
		}

		return false;
	}

	public static boolean isPropertyFinal(IRMember mbr) {
		return (P_FINAL & mbr.getProperty()) != 0;
	}

	public static boolean isPropertyInherit(IRMember mbr) {
		return (P_INHERIT & mbr.getProperty()) != 0;
	}

	public static boolean isPropertyStatic(IRMember mbr) {
		return (P_STATIC & mbr.getProperty()) != 0;
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

	public static boolean isTrace(IRFrame frame) throws RException {
		return RulpUtil.asBoolean(RulpUtil.getVarValue(frame, A_TRACE)).asBoolean();
	}

	public static boolean isTrace(IRInterpreter interpreter) throws RException {
		return isTrace(interpreter.getMainFrame());
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
//			e.printStackTrace();
			return false;
		}
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

	public static boolean isValueAtom(IRObject obj) {
		return obj.getType() == RType.ATOM && !isVarName(((IRAtom) obj).getName());
	}

	public static boolean isVarAtom(IRObject obj) {
		return obj.getType() == RType.ATOM && isVarName(((IRAtom) obj).getName());
	}

	public static boolean isVarName(String var) {
		return var.length() > 1 && var.charAt(0) == A_QUESTION_C;
	}

	public static IRObject lookup(IRObject obj, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (obj.getType() == RType.ATOM) {

			IRFrameEntry entry = RuntimeUtil.lookupFrameEntry(frame, ((IRAtom) obj).getName());
			if (entry != null) {
				obj = entry.getObject();
			}

		} else if (obj.getType() == RType.MEMBER) {
			obj = RuntimeUtil.getActualMember((IRMember) obj, interpreter, frame);

		}

		return obj;
	}

	public static String lookupFile(String path, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (FileUtil.isExistFile(path)) {
			return path;
		}

		if (FileUtil.isAbsPath(path)) {
			return null;

		} else if (!path.endsWith(A_RULP_SUFFIX)) {
			String newPath = path + A_RULP_SUFFIX;
			if (FileUtil.isExistFile(newPath)) {
				return newPath;
			}
		}

		/*************************************************/
		// Search in "load-paths"
		/*************************************************/
		IRList lpList = RulpUtil.asList(RulpUtil.asVar(interpreter.getObject(A_PATH)).getValue());

		IRIterator<? extends IRObject> it = lpList.iterator();
		while (it.hasNext()) {
			String aPath = RulpUtil.asString(it.next()).asString();
			String newPath = _lookupFile(aPath, path);
			if (newPath != null) {
				return newPath;
			}
		}

		/*************************************************/
		// Search in System env "PATH"
		/*************************************************/
		for (String aPath : SystemUtil.getEnvPaths()) {
			String newPath = _lookupFile(aPath, path);
			if (newPath != null) {
				return newPath;
			}
		}

		return null;
	}

	public static boolean matchParaType(IRObject paraValue, IRAtom paraTypeAtom) throws RException {

		RType valueType = paraValue.getType();
		if (valueType == RType.INSTANCE) {
			return SubjectUtil.findMatchClass(RulpUtil.asInstance(paraValue), paraTypeAtom) != null;
		}

		RType expectType = RType.toType(paraTypeAtom.asString());
		if (valueType == expectType) {
			return true;
		}

		if (expectType == null) {
			return false;
		}

		return MathUtil.getTypeConvert(valueType, expectType) == expectType;
	}

	public static boolean matchType(IRAtom typeAtom, IRObject obj) throws RException {

		// Match any object
		if (typeAtom == O_Nil) {
			return true;
		}

		return getObjectType(obj) == typeAtom;
	}

	public static String nameOf(IRObject obj, IRFrame frame) throws RException {

		if (obj == null) {
			return null;
		}

		switch (obj.getType()) {

		case ATOM:
			IRFrameEntry entry = frame.getEntry(RulpUtil.asAtom(obj).getName());
			if (entry != null && entry.getObject() != obj) {
				return nameOf(entry.getObject(), frame);
			}

			return RulpUtil.asAtom(obj).getName();

		case INSTANCE:
			return RulpUtil.asInstance(obj).getInstanceName();

		case CLASS:
			return RulpUtil.asClass(obj).getClassName();

		case FACTOR:
			return RulpUtil.asFactor(obj).getName();

		case TEMPLATE:
			return RulpUtil.asTemplate(obj).getName();

		case FUNC:
			return RulpUtil.asFunction(obj).getSignature();

		case MACRO:
			return RulpUtil.asMacro(obj).getName();

		case VAR:
			return RulpUtil.asVar(obj).getName();

		case MEMBER:
			return RulpUtil.asMember(obj).getName();

		case FRAME:
			return RulpUtil.asFrame(obj).getFrameName();

		case LIST:
			return RulpUtil.asList(obj).getNamedName();

		case NIL:
		default:
			return null;
		}
	}

	public static IRInstance newInstance(String className, String instanceName, IRInterpreter interpreter,
			IRFrame frame) throws RException {

		// (new class name)
		return XRFactorNew.newInstance(
				RulpFactory.createList(O_New, RulpFactory.createAtom(className), RulpFactory.createAtom(instanceName)),
				interpreter, frame);
	}

	public static void registerNameSpaceLoader(IRInterpreter interpreter, IRFrame frame, String nsName,
			IRObjectLoader loader) throws RException {

		if (nsName == null || (nsName = nsName.trim()).isEmpty() || nsName.startsWith("/")) {
			throw new RException("invalid namespace name: " + nsName);
		}

		IRInstance instance = RulpUtil.newInstance(A_NAMESPACE, nsName, interpreter, frame);

		instance.addLoader((sub) -> {
			try {
				LoadUtil.loadClass(loader, interpreter, sub.getSubjectFrame());
			} catch (IOException e) {
				e.printStackTrace();
				throw new RException(e.toString());
			}
		});
	}

//	public static boolean isPureAtomPairList(IRObject obj) throws RException {
//
//		RType type = obj.getType();
//		if (type != RType.LIST && type != RType.EXPR) {
//			return false;
//		}
//
//		IRIterator<? extends IRObject> iter = ((IRList) obj).iterator();
//		while (iter.hasNext()) {
//
//			IRObject element = iter.next();
//
//			if (element.getType() == RType.ATOM) {
//				continue;
//			} else {
//
//			}
//
//			if (!isPureAtomList(element) || ((IRList) element).size() != 2) {
//				return false;
//			}
//		}
//
//		return true;
//	}

//	public static boolean isQuote(IRObject obj) {
//
//		if (obj.getType() == RType.ATOM) {
//			String name = ((IRAtom) obj).getName();
//			return name.equals("'") || name.equalsIgnoreCase("quote");
//		} else {
//			return false;
//		}
//	}

	public static void saveObjList(List<IRObject> list, IRObject obj) throws RException {
		list.add(obj);
		incRef(obj);
	}

	public static void setLocalVar(IRFrame frame, String varName, IRObject value) throws RException {

		IRObject varObj = frame.findLocalObject(varName);
		IRVar var = null;
		if (varObj == null) {
			var = addVar(frame, varName);
		} else {
			var = RulpUtil.asVar(varObj);
		}

		var.setValue(value);
	}

	public static void setMember(IRSubject subject, String name, IRFactorBody body, RAccessType accessType)
			throws RException {
		setMember(subject, name, body, accessType, 0);
	}

	public static void setMember(IRSubject subject, String name, IRFactorBody body, RAccessType accessType,
			int property) throws RException {
		setMember(subject, name, new XRFactorWrapper(name, body, true), accessType, property);
	}

	public static void setMember(IRSubject subject, String name, IRObject obj) throws RException {
		setMember(subject, name, obj, null, 0);
	}

	public static void setMember(IRSubject subject, String name, IRObject obj, RAccessType accessType)
			throws RException {
		setMember(subject, name, obj, accessType, 0);
	}

	public static void setMember(IRSubject subject, String name, IRObject obj, RAccessType accessType, int property)
			throws RException {

		IRMember mbr = RulpFactory.createMember(subject, name, obj);
		if (accessType != null) {
			mbr.setAccessType(accessType);
			mbr.setProperty(property);
		}

		subject.setMember(name, mbr);
	}

	public static void setPropertyFinal(IRMember mbr, boolean bValue) throws RException {

		if (isPropertyFinal(mbr) == bValue) {
			return;
		}

		if (bValue) {
			mbr.setProperty(mbr.getProperty() | P_FINAL);
		} else {
			mbr.setProperty(mbr.getProperty() & ~P_FINAL);
		}
	}

	public static void setPropertyInherit(IRMember mbr, boolean bValue) throws RException {

		if (isPropertyInherit(mbr) == bValue) {
			return;
		}

		if (bValue) {
			mbr.setProperty(mbr.getProperty() | P_INHERIT);
		} else {
			mbr.setProperty(mbr.getProperty() & ~P_INHERIT);
		}
	}

	public static void setPropertyStatic(IRMember mbr, boolean bValue) throws RException {

		if (isPropertyStatic(mbr) == bValue) {
			return;
		}

		if (bValue) {
			mbr.setProperty(mbr.getProperty() | P_STATIC);
		} else {
			mbr.setProperty(mbr.getProperty() & ~P_STATIC);
		}
	}

	public static void setTrace(IRFrame frame, boolean trace) throws RException {
		RulpUtil.setLocalVar(frame, A_TRACE, trace ? O_True : O_False);
	}

	public static void setTrace(IRInterpreter interpreter, boolean trace) throws RException {
		setTrace(interpreter.getMainFrame(), trace);
	}

	public static <T> int size(List<T> a) {
		return a == null ? 0 : a.size();
	}

	public static List<IRObject> subList(IRList l1, int begin, int end) throws RException {

		if (begin >= end) {
			return Collections.emptyList();
		}

		ArrayList<IRObject> list = new ArrayList<>();
		for (int i = begin; i < end; ++i) {
			list.add(l1.get(i));
		}

		return list;
	}

	public static IRObject throw_error(IRInterpreter interpreter, IRFrame frame, IRAtom errId, IRObject errValue,
			IRObject fromObject) throws RException {

		IRError err = RulpFactory.createError(interpreter, errId, errValue);
		IRObject rst = handle_error(err, interpreter, frame);
		if (rst == null) {
			throw new RError(frame, fromObject, err);
		}

		return rst;
	}

	public static List<IRObject> toArray(IRList list) throws RException {

		ArrayList<IRObject> arr = new ArrayList<>();
		IRIterator<? extends IRObject> iter = list.iterator();
		while (iter.hasNext()) {
			arr.add(iter.next());
		}

		return arr;
	}

	public static List<Integer> toArray2(int[] objs) {

		ArrayList<Integer> list = new ArrayList<>();
		for (Integer o : objs) {
			list.add(o);
		}

		return list;
	}

	public static <T> List<T> toArray2(T[] objs) {

		ArrayList<T> list = new ArrayList<>();
		for (T o : objs) {
			list.add(o);
		}

		return list;
	}

	public static IRAtom toAtom(String name) {

		if (name == null) {
			return O_Nil;
		}

		switch (name) {
		case "":
			return O_EMPTY;

		case A_ATOM:
			return T_Atom;

		case A_BOOL:
			return T_Bool;

		case A_CONST:
			return O_CONST;

		case A_INSTANCE:
			return T_Instance;

		case A_EXPRESSION:
			return T_Expr;

		case A_FACTOR:
			return T_Factor;

		case A_FLOAT:
			return T_Float;

		case A_DOUBLE:
			return T_Double;

		case A_FUNCTION:
			return T_Func;

		case A_INTEGER:
			return T_Int;

		case A_LONG:
			return T_Long;

		case A_LIST:
			return T_List;

		case A_ITERATOR:
			return T_Iterator;

		case A_MACRO:
			return T_Macro;

		case A_NATIVE:
			return T_Native;

		case A_NIL:
			return O_Nil;

		case A_STRING:
			return T_String;

		case A_BLOB:
			return T_Blob;

		case A_VAR:
			return T_Var;

		case A_CONSTANT:
			return T_Constant;

		case A_CLASS:
			return T_Class;

		case A_MEMBER:
			return T_Member;

		case A_FRAME:
			return T_Frame;

		case A_ARRAY:
			return T_Array;

		case A_TEMPLATE:
			return T_Template;

		case A_LAMBDA:
			return O_LAMBDA;

		case A_FINAL:
			return O_Final;

		case A_STATIC:
			return O_Static;

		case A_PRIVATE:
			return O_Private;

		case A_PUBLIC:
			return O_Public;

		case F_COMPUTE:
			return O_COMPUTE;

		case A_OPT_ID:
			return O_OPT_ID;

		case A_STABLE:
			return O_STABLE;

		case A_THREAD_UNSAFE:
			return O_THREAD_UNSAFE;

		case A_OPT_CCO:
			return O_OPT_CCO;

		case A_OPT_ERO:
			return O_OPT_ERO;

		case A_OPT_TCO:
			return O_OPT_TCO;

		case A_RETURN_TYPE:
			return O_RETURN_TYPE;

		case F_RETURN:
			return O_RETURN;

		case A_QUESTION_LIST:
			return O_QUESTION_LIST;

		case F_B_AND:
			return O_B_AND;

		default:
			return RulpFactory.createAtom(name);
		}
	}

	public static IRBlob toBlob(IRObject a) throws RException {

		switch (a.getType()) {
		case BLOB:
			return (IRBlob) a;

		case INT:
			IRBlob intBlob = RulpFactory.createBlob(4);
			EncodeUtil.encode(RulpUtil.asInteger(a).asInteger(), intBlob.getValue(), 0);
			return intBlob;

		case LONG:
			IRBlob longBlob = RulpFactory.createBlob(8);
			EncodeUtil.encode(RulpUtil.asLong(a).asLong(), longBlob.getValue(), 0);
			return longBlob;

		case FLOAT:
			IRBlob floatBlob = RulpFactory.createBlob(4);
			EncodeUtil.encode(RulpUtil.asFloat(a).asFloat(), floatBlob.getValue(), 0);
			return floatBlob;

		case DOUBLE:
			IRBlob doubleBlob = RulpFactory.createBlob(8);
			EncodeUtil.encode(RulpUtil.asDouble(a).asDouble(), doubleBlob.getValue(), 0);
			return doubleBlob;

		case STRING:
			return RulpFactory.createBlob(RulpUtil.asString(a).asString().getBytes());

		default:
			throw new RException(String.format("Not support type: %s", a.toString()));
		}
	}

	public static boolean toBoolean(IRObject a) throws RException {
		switch (a.getType()) {
		case NIL:
			return false;

		case BOOL:
			return ((IRBoolean) a).asBoolean();

		case FLOAT:
		case DOUBLE:
		case EXPR:
			return true;

		case INT:
		case LONG:
			return RulpUtil.toLong(a) != 0;

		default:
			throw new RException(String.format("Not support type: value=%s, type=%s", a.toString(), a.getType()));
		}
	}

	public static IRObject toConst(IRObject obj, IRFrame frame) throws RException {

		if (AttrUtil.isConst(obj, frame)) {
			return obj;
		}

		switch (obj.getType()) {
		case VAR:
			return toConst(RulpUtil.asVar(obj).getValue(), frame);

		case LIST:

			IRList list = (IRList) obj;
			if (list.isEmpty()) {
				return RulpFactory.emptyConstList();
			}

			ArrayList<IRObject> newObjs = new ArrayList<>();
			IRIterator<? extends IRObject> it = list.iterator();
			while (it.hasNext()) {
				newObjs.add(toConst(it.next(), frame));
			}

			return RulpFactory.createConstArray(newObjs);

		default:
			throw new RException("Can't to-const: " + obj);
		}
	}

	public static IRExpr toDoExpr(IRIterator<? extends IRObject> it) throws RException {

		ArrayList<IRObject> newExpr = new ArrayList<>();
		newExpr.add(RulpFactory.createAtom(A_DO));

		while (it.hasNext()) {
			newExpr.add(RulpUtil.asExpression(it.next()));
		}

		return RulpFactory.createExpression(newExpr);
	}

	public static IRExpr toDoExpr(Iterator<? extends IRObject> it) throws RException {

		ArrayList<IRObject> newExpr = new ArrayList<>();
		newExpr.add(RulpFactory.createAtom(A_DO));

		while (it.hasNext()) {
			newExpr.add(RulpUtil.asExpression(it.next()));
		}

		return RulpFactory.createExpression(newExpr);
	}

	public static IRExpr toDoExpr(List<? extends IRObject> it) throws RException {

		if (it.size() == 1) {
			return RulpUtil.asExpression(it.get(0));
		}

		ArrayList<IRObject> newExpr = new ArrayList<>();
		newExpr.add(RulpFactory.createAtom(A_DO));

		for (IRObject expr : it) {
			newExpr.add(RulpUtil.asExpression(expr));
		}

		return RulpFactory.createExpression(newExpr);
	}

	public static double toDouble(IRObject a) throws RException {
		switch (a.getType()) {
		case FLOAT:
			return ((IRFloat) a).asFloat();

		case DOUBLE:
			return ((IRDouble) a).asDouble();

		case INT:
			return ((IRInteger) a).asInteger();

		case LONG:
			return (double) ((IRLong) a).asLong();

		case STRING:
			return Double.valueOf(RulpUtil.asString(a).asString());

		default:
			throw new RException("Can't convert to double: " + a);
		}
	}

	public static IRExpr toExpr(IRObject factor, List<? extends IRObject> objList) throws RException {

		ArrayList<IRObject> exprList = new ArrayList<>();
		exprList.add(factor);
		for (IRObject obj : objList) {
			exprList.add(obj);
		}

		return RulpFactory.createExpression(exprList);
	}

	public static ArrayList<IRExpr> toExprList(IRList list) throws RException {

		ArrayList<IRExpr> array = new ArrayList<>();

		IRIterator<? extends IRObject> iter = list.iterator();
		while (iter.hasNext()) {
			array.add(RulpUtil.asExpression(iter.next()));
		}

		return array;
	}

	public static IRObject[] toFixArray(Collection<? extends IRObject> list) {

		IRObject[] arr = new IRObject[list.size()];
		int idx = 0;
		for (IRObject obj : list) {
			arr[idx++] = obj;
		}

		return arr;
	}

	public static float toFloat(IRObject a) throws RException {
		switch (a.getType()) {
		case FLOAT:
			return ((IRFloat) a).asFloat();

		case DOUBLE:
			return (float) ((IRDouble) a).asDouble();

		case INT:
			return ((IRInteger) a).asInteger();

		case LONG:
			return (float) ((IRLong) a).asLong();

		case STRING:
			return Float.valueOf(RulpUtil.asString(a).asString());

		default:
			throw new RException("Can't convert to float: " + a);
		}
	}

	public static IRExpr toIfExpr(IRExpr condExpr, IRExpr... doExprs) throws RException {

		ArrayList<IRObject> exprList = new ArrayList<>();
		exprList.add(RulpFactory.createAtom(F_IF));
		exprList.add(condExpr);
		exprList.add(RulpFactory.createAtom(A_DO));

		for (IRExpr doExpr : doExprs) {
			exprList.add(doExpr);
		}

		return RulpFactory.createExpression(exprList);
	}

	public static IRExpr toIfExpr(IRExpr condExpr, IRList doExprs) throws RException {

		ArrayList<IRObject> exprList = new ArrayList<>();
		exprList.add(RulpFactory.createAtom(F_IF));
		exprList.add(condExpr);
		exprList.add(RulpFactory.createAtom(A_DO));
		exprList.addAll(toExprList(doExprs));

		return RulpFactory.createExpression(exprList);
	}

	public static IRMap toImplMap(IRInstance instance) throws RException {
		return XRMap.toImplMap(instance);
	}

	public static int toInt(IRObject a) throws RException {
		switch (a.getType()) {

		case FLOAT:
			return (int) ((IRFloat) a).asFloat();

		case DOUBLE:
			return (int) ((IRDouble) a).asDouble();

		case INT:
			return ((IRInteger) a).asInteger();

		case LONG:
			return (int) ((IRLong) a).asLong();

		case STRING:
			return Integer.valueOf(RulpUtil.asString(a).asString());

		default:
			throw new RException("Can't convert to integer: " + a);
		}
	}

	public static <T> List<T> toList(IRIterator<T> iter) throws RException {

		List<T> list = new ArrayList<>();
		while (iter.hasNext()) {
			list.add(iter.next());
		}

		return list;
	}

	public static IRList toList(String name, IRArray array) throws RException {

		if (array.isEmpty()) {
			return RulpFactory.emptyConstList();
		}

		ArrayList<IRObject> rstList = new ArrayList<>();

		int size = array.size();
		for (int i = 0; i < size; ++i) {
			IRObject obj = array.get(i);
			if (obj == null) {
				obj = O_Nil;
			}
			rstList.add(obj);
		}

		return RulpFactory.createNamedList(name, rstList);
	}

	public static IRList toList(String name, IRObject listObj, IRFrame frame) throws RException {

		switch (listObj.getType()) {
		case ARRAY:
			return toList(name, RulpUtil.asArray(listObj));

		case LIST:
			IRList list = RulpUtil.asList(listObj);
			// remove list name
			if (name == null || name.trim().isEmpty()) {

				if (list.getNamedName() == null) {
					return list;
				}

				return RulpFactory.createList(list.iterator());

			} else {

				if (list.getNamedName() != null && name.equals(list.getNamedName())) {
					return list;
				}

				if (frame != null && !AttrUtil.isConst(list, frame)) {
					IRList varList = RulpFactory.createVaryNamedList(name);
					RulpUtil.addAll(varList, list.iterator());
					return varList;
				}

				return RulpFactory.createNamedList(name, list.iterator());
			}

		default:
			throw new RException("Can't convert to list: " + listObj);

		}
	}

	public static IRList toList(String name, IRObject[] elements) throws RException {

		if (name == null)
			return RulpFactory.createList(elements);
		else
			return RulpFactory.createNamedList(name, elements);

	}

	public static IRList toList(String name, List<IRObject> elements) throws RException {

		if (name == null)
			return RulpFactory.createList(elements);
		else
			return RulpFactory.createNamedList(name, elements);

	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> toList(T... objs) {

		if (objs == null || objs.length == 0) {
			return Collections.emptyList();
		}

		List<T> list = new ArrayList<T>();
		for (T o : objs) {
			list.add(o);
		}

		return list;
	}

	public static long toLong(IRObject a) throws RException {

		switch (a.getType()) {
		case FLOAT:
			return (long) ((IRFloat) a).asFloat();

		case DOUBLE:
			return (long) ((IRDouble) a).asDouble();

		case INT:
			return ((IRInteger) a).asInteger();

		case LONG:
			return ((IRLong) a).asLong();

		case STRING:
			return Long.valueOf(RulpUtil.asString(a).asString());

		default:
			throw new RException("Can't convert to long: " + a);
		}
	}

	public static IRList toNamedList(IRObject nameObj, IRObject listObj, IRFrame frame) throws RException {

		String name = null;

		if (nameObj != null) {
			
			switch (nameObj.getType()) {
			case ATOM:
				name = RulpUtil.asAtom(nameObj).getName();
				break;

			case STRING:
				name = RulpUtil.asString(nameObj).asString();
				break;

			default:
				throw new RException(
						String.format("Invalid name object type<%s>: %s", "" + nameObj.getType(), nameObj));
			}
		}

		return toList(name, listObj, frame);
	}

	public static String toOneLine(List<String> lines) {

		StringBuffer sb = new StringBuffer();
		int index = 0;

		for (String line : lines) {
			if (index++ != 0) {
				sb.append('\n');
			}
			sb.append(line);
		}

		return sb.toString();
	}

	public static RRelationalOperator toRelationalOperator(String name) {

		if (name == null) {
			return null;
		}

		switch (name) {
		case F_EQUAL:
		case F_O_EQ:
			return RRelationalOperator.EQ;

		case F_NOT_EQUAL:
		case F_O_NE:
			return RRelationalOperator.NE;

		case F_O_GE:
			return RRelationalOperator.GE;

		case F_O_GT:
			return RRelationalOperator.GT;

		case F_O_LE:
			return RRelationalOperator.LE;

		case F_O_LT:
			return RRelationalOperator.LT;

		default:
			return null;
		}
	}

	public static String toString(IRIterator<? extends IRObject> iterator) throws RException {

		StringBuffer sb = new StringBuffer();
		_toString(sb, iterator, objFormater, false);
		return sb.toString();
	}

	public static String toString(IRObject obj) throws RException {

		StringBuffer sb = new StringBuffer();
		_toString(sb, obj, objFormater, false);

		return sb.toString();
	}

	public static String toString(IRObject obj, boolean attr) throws RException {

		StringBuffer sb = new StringBuffer();
		_toString(sb, obj, objFormater, attr);

		return sb.toString();
	}

	public static String toString(IRObject obj, int maxLength) throws RException {

		StringBuffer sb = new StringBuffer();
		XShortFormater formater = new XShortFormater(maxLength);
		_toString(sb, obj, formater, false);
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
			_toString(sb, e, objFormater, false);
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
		_toString(sb, obj, printFormater, false);
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

		case DOUBLE:
			return R_DOUBLE_PRE + ((IRDouble) obj).asDouble();

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
		case ARRAY:
		case BLOB:
		case CLASS:
		case CONSTANT:
		case FRAME:
		case FUNC:
		case LONG:
		case MACRO:
		case MEMBER:
		case NATIVE:
		default:
			throw new RException("unsupport type: " + obj.getType());
		}
	}

	public static String toValidAttribute(String attr) {

		if (attr == null) {
			return null;
		}

		attr = attr.trim();
		if (attr.isEmpty()) {
			return null;
		}

		return attr;
	}

	public static IRObject toVary(IRObject obj, IRFrame frame) throws RException {

		if (!AttrUtil.isConst(obj, frame)) {
			return obj;
		}

		switch (obj.getType()) {
		case ARRAY:

			IRArray varyArray = RulpFactory.createVaryArray();
			IRArray constArray = RulpUtil.asArray(obj);

			int size = constArray.size();
			for (int i = 0; i < size; ++i) {
				varyArray.add(constArray.get(i));
			}

			return varyArray;

		default:
			throw new RException("Can't to-vary: " + obj);
		}
	}

}
