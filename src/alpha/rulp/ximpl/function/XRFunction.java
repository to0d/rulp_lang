/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.function;

import static alpha.rulp.lang.Constant.A_FUN_PRE;
import static alpha.rulp.lang.Constant.O_Nil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.error.RReturn;
import alpha.rulp.ximpl.error.RUnmatchParaException;
import alpha.rulp.ximpl.runtime.AbsFunctionAdapter;

public class XRFunction extends AbsFunctionAdapter implements IRFunction {

	protected IRFrame defineFrame;

	protected IRExpr funBody;

	protected String name;

	protected final List<IRParaAttr> paraAttrList;

	protected final int paraCount;

	protected String signature;

	public XRFunction(IRFrame defineFrame, String name, List<IRParaAttr> paraAttrList, IRExpr funBody) {

		this.defineFrame = defineFrame;
		this.name = name;
		this.paraAttrList = new ArrayList<>(paraAttrList);
		this.paraCount = paraAttrList.size();
		this.funBody = funBody;
	}

	protected void _matchTypeList(IRList args, IRFrame frame) throws RException {

		if ((paraCount + 1) != args.size()) {
			throw new RUnmatchParaException(this, frame, "Invalid parameter count: " + paraCount);
		}

		Iterator<IRParaAttr> attrIter = paraAttrList.iterator();
		IRIterator<? extends IRObject> valIter = args.listIterator(1);

		int argIndex = 0;

		while (attrIter.hasNext()) {

			IRParaAttr attr = attrIter.next();

			IRAtom typeAtom = attr.getParaType();
			IRObject valObj = valIter.next();

			// Match any type
			if (typeAtom == O_Nil || valObj == null) {
				continue;
			}

			if (!RulpUtil.matchParaType(valObj, typeAtom)) {
				throw new RUnmatchParaException(this, frame,
						String.format("the %d argument<%s> not match type <%s>", argIndex, valObj, typeAtom));
			}

			++argIndex;
		}
	}

	@Override
	public String asString() {
		return name;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		_matchTypeList(args, frame);

		IRFrame funFrame = RulpFactory.createFrame(frame, A_FUN_PRE + name);

		try {

			RulpUtil.incRef(funFrame);

			IRIterator<? extends IRObject> argIter = args.listIterator(1); // Skip factor head element
			Iterator<IRParaAttr> attrIter = paraAttrList.iterator();
			while (argIter.hasNext()) {

				IRParaAttr attr = attrIter.next();
				String paraName = attr.getParaName();

				IRObject arg = argIter.next();
				if (arg == null) {
					arg = O_Nil;
				} else if (arg.getType() != RType.VAR) {
					arg = RulpFactory.createVar(paraName, arg);
				}

				funFrame.setEntry(paraName, arg);
			}

			interpreter.compute(funFrame, funBody);
			return O_Nil;

		} catch (RReturn r) {
			return r.returnValue(frame);

		} finally {
			RulpUtil.decRef(funFrame);
		}
	}

	@Override
	public int getArgCount() {
		return paraCount;
	}

	@Override
	public IRFrame getDefineFrame() {
		return defineFrame;
	}

	@Override
	public IRExpr getFunBody() {
		return funBody;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<IRParaAttr> getParaAttrs() {
		return paraAttrList;
	}

	@Override
	public String getSignature() throws RException {

		if (signature == null) {
			StringBuilder sb = new StringBuilder();
			sb.append('(');
			sb.append(name);

			for (IRParaAttr attr : getParaAttrs()) {
				sb.append(' ');
				sb.append(attr.getParaType().asString());
			}
			sb.append(')');
			signature = sb.toString();
		}

		return signature;
	}

	@Override
	public RType getType() {
		return RType.FUNC;
	}

	@Override
	public boolean isLambda() {
		return false;
	}

	@Override
	public boolean isList() {
		return false;
	}

	public String toString() {
		try {
			return this.getSignature();
		} catch (RException e) {
			e.printStackTrace();
			return e.toString();
		}
	}

}