/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.error;

import static alpha.rulp.lang.Constant.A_RETURN_VAR;
import static alpha.rulp.lang.Constant.O_Nan;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;

public class RReturn extends RIException {

	private static final long serialVersionUID = 5632039390920503560L;

	private IRObject rtValue;

	public RReturn(IRObject fromObject, IRFrame fromFrame, IRObject rtValue) throws RException {
		super(fromObject, fromFrame);
		this.rtValue = rtValue;
		RulpUtil.incRef(rtValue);
	}

	public IRObject returnValue(IRFrame frame) throws RException {

		IRObject returnValue = this.rtValue;

		if (returnValue != null && returnValue != O_Nan) {

			IRObject returnVar = frame.findLocalObject(A_RETURN_VAR);
			if (returnVar == null) {
				returnVar = RulpFactory.createVar(A_RETURN_VAR);
				frame.setEntry(A_RETURN_VAR, returnVar);
			}

			RulpUtil.asVar(returnVar).setValue(returnValue);

			RulpUtil.decRef(this.rtValue);
			this.rtValue = null;
		}

		return returnValue;
	}
}
