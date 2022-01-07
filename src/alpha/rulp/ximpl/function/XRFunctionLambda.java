/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.function;

import java.util.List;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.runtime.AbsFunctionAdapter;

public class XRFunctionLambda extends AbsFunctionAdapter implements IRFunction {

	protected IRFrame definedFrame;

	protected IRFunction func;

	protected int lambdaId;

	public XRFunctionLambda(IRFunction func, IRFrame definedFrame, int lambdaId) throws RException {

		this.func = func;
		this.definedFrame = definedFrame;
		this.lambdaId = lambdaId;

		RulpUtil.incRef(definedFrame);
	}

	protected void _delete() throws RException {

		if (definedFrame != null) {
			RulpUtil.decRef(definedFrame);
			definedFrame = null;
		}

		super._delete();
	}

	@Override
	public String asString() {
		return func.asString();
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

//		IRFrame lambdaFrame = RulpFactory.createFrame(definedFrame, String.format("LAMBDA-%d", lambdaId));

		try {
//			RulpUtil.incRef(lambdaFrame);
			return func.compute(args, interpreter, definedFrame);
		} finally {
//			RulpUtil.decRef(lambdaFrame);
		}

	}

	@Override
	public int getArgCount() {
		return func.getArgCount();
	}

	@Override
	public IRFrame getDefineFrame() {
		return definedFrame;
	}

	@Override
	public IRExpr getFunBody() {
		return func.getFunBody();
	}

	@Override
	public String getName() {
		return func.getName();
	}

	@Override
	public List<IRParaAttr> getParaAttrs() {
		return func.getParaAttrs();
	}

	@Override
	public String getSignature() throws RException {
		return func.getSignature();
	}

	@Override
	public RType getType() {
		return RType.FUNC;
	}

	@Override
	public boolean isLambda() {
		return true;
	}

	@Override
	public boolean isList() {
		return false;
	}

}