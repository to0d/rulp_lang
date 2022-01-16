/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.function;

import static alpha.rulp.lang.Constant.O_Nil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRFunctionList;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.AttrUtil;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.error.RUnmatchParaException;
import alpha.rulp.ximpl.runtime.AbsFunctionAdapter;

public class XRFunctionList extends AbsFunctionAdapter implements IRFunctionList {

	static class FuncList {

		static boolean isDefaultFun(IRFunction fun) {

			for (IRParaAttr attr : fun.getParaAttrs()) {
				if (attr.getParaType() != O_Nil) {
					return false;
				}
			}

			return true;
		}

		public IRFunction defaultFun;

		public List<IRFunction> funcList = new LinkedList<>();

		public Map<String, IRFunction> signatureMap = new HashMap<>();

		public IRFunction addFunc(IRFunction fun) throws RException {

			IRFunction oldFun = null;

			if (isDefaultFun(fun)) {

				if (this.defaultFun != null) {
					tryOverride(this.defaultFun, fun);
					oldFun = this.defaultFun;
				}

				this.defaultFun = fun;

			} else {

				String signature = fun.getSignature();

				oldFun = this.signatureMap.get(signature);
				if (oldFun != null) {
					tryOverride(oldFun, fun);
				}

				this.signatureMap.put(signature, fun);
				this.funcList.add(fun);
			}

			return oldFun;
		}

		public IRFunction findMatchFun(IRList args) throws RException {

			int lastMatchCount = -1;
			ArrayList<IRFunction> lastMatchFuns = new ArrayList<>();

			for (IRFunction fun : funcList) {

				int matchCount = matchFunParaCount(fun, args);
				if (matchCount < 0) {
					continue;
				}

				if (lastMatchCount == -1) {
					lastMatchFuns.add(fun);
					lastMatchCount = matchCount;
					continue;
				}

				if (matchCount < lastMatchCount) {
					continue;
				}

				if (matchCount == lastMatchCount) {
					lastMatchFuns.add(fun);
					continue;
				}

				// matchCount > lastMatchCount
				lastMatchFuns.clear();
				lastMatchFuns.add(fun);
				lastMatchCount = matchCount;
			}

			// not match fun found
			if (lastMatchCount < 0) {
				return defaultFun;
			}

			if (lastMatchCount == 0 && defaultFun != null) {
				throw new RException(String.format("ambiguous funcion found: def=%s, funs=%s, expr=%s", defaultFun,
						lastMatchFuns, args));
			}

			if (lastMatchFuns.size() > 1) {
				throw new RException(String.format("ambiguous funcion found: funs=%s, expr=%s", lastMatchFuns, args));
			}

			return lastMatchFuns.get(0);
		}

	}

	public static int matchFunParaCount(IRFunction fun, IRList args) throws RException {

		if ((fun.getParaAttrs().size() + 1) != args.size()) {
			return -1;
		}

		Iterator<IRParaAttr> typeIter = fun.getParaAttrs().iterator();
		IRIterator<? extends IRObject> valIter = args.listIterator(1);
		int matchCount = 0;

		while (typeIter.hasNext()) {

			IRParaAttr attr = typeIter.next();
			IRObject valObj = valIter.next();

			IRAtom typeAtom = attr.getParaType();

			// Match any type
			if (typeAtom == O_Nil || valObj == null) {
				continue;
			}

			if (typeAtom != RulpUtil.getObjectType(valObj)) {
				return -1;
			} else {
				++matchCount;
			}
		}

		return matchCount;
	}

	public static void tryOverride(IRFunction oldFun, IRFunction newFun) throws RException {

		IRFrame oldFrame = oldFun.getDefineFrame();
		IRFrame newFrame = newFun.getDefineFrame();

		int oldFrameId = oldFrame.getFrameId();
		int newFrameId = newFrame.getFrameId();

		if (oldFrameId == newFrameId) {
			throw new RException(String.format("duplicate funcion: %s", oldFun.getSignature()));
		}

		if (oldFrameId > newFrameId) {
			throw new RException(String.format("invalid new frame: new=%s, old=%s", "" + newFrame, "" + oldFrame));
		}
	}

	protected List<IRFunction> allFuncList = new LinkedList<>();

	protected Map<Integer, FuncList> funListMap = new HashMap<>();

	protected String name;

	protected String signature;

	public XRFunctionList(String name) {
		super();
		this.name = name;
	}

	@Override
	protected void _delete() throws RException {

		Iterator<IRFunction> it = allFuncList.iterator();
		while (it.hasNext()) {
			IRFunction fun = it.next();
			it.remove();
			RulpUtil.decRef(fun);
		}

		super._delete();
	}

	@Override
	public void addFunc(IRFunction fun) throws RException {

		int argCount = fun.getArgCount();

		FuncList funcList = funListMap.get(argCount);
		if (funcList == null) {
			funcList = new FuncList();
			funListMap.put(argCount, funcList);
		}

		IRFunction oldFun = funcList.addFunc(fun);
		if (oldFun != null) {
			allFuncList.remove(oldFun);
			RulpUtil.decRef(oldFun);
		}

		this.allFuncList.add(fun);
		RulpUtil.incRef(fun);

		this.signature = null;
		
		AttrUtil.removeAllAttributes(this);
	}

	@Override
	public String asString() {
		return name;
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		int argCount = args.size() - 1;
		FuncList funcList = funListMap.get(argCount);
		if (funcList == null) {
			throw new RException(String.format("match funcion not found:  expr=%s", args));
		}

		IRFunction matchFun = funcList.findMatchFun(args);
		if (matchFun == null) {
			throw new RUnmatchParaException(this, frame, String.format("match funcion not found:  expr=%s", args));
		}

		return RuntimeUtil.computeCallable(matchFun, args, interpreter, frame);
	}

	@Override
	public List<IRFunction> getAllFuncList() {
		return allFuncList;
	}

	@Override
	public int getArgCount() {
		return -1;
	}

	@Override
	public IRFrame getDefineFrame() {
		return null;
	}

	@Override
	public IRExpr getFunBody() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<IRParaAttr> getParaAttrs() {
		return null;
	}

	@Override
	public String getSignature() throws RException {

		if (signature == null) {

			ArrayList<String> allSignatures = new ArrayList<>();
			for (IRFunction func : allFuncList) {
				allSignatures.add(func.getSignature());
			}
			Collections.sort(allSignatures);

			StringBuilder sb = new StringBuilder();
			sb.append("'(");
			int index = 0;
			for (String sig : allSignatures) {
				if (index++ != 0) {
					sb.append(' ');
				}
				sb.append(sig);
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
		return true;
	}

	public String toString() {
		return name;
	}
}
