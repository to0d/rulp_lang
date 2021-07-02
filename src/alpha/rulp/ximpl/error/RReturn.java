/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.error;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRObject;

public class RReturn extends RIException {

	private static final long serialVersionUID = 5632039390920503560L;

	private IRObject rtValue;

	public RReturn(IRObject fromObject, IRFrame fromFrame, IRObject rtValue) {
		super(fromObject, fromFrame);
		this.rtValue = rtValue;
	}

	public IRObject getReturnValue() {
		return rtValue;
	}

}
