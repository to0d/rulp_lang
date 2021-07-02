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

public class RContinue extends RIException {

	private static final long serialVersionUID = 1843377852429313677L;

	public RContinue(IRObject fromObject, IRFrame fromFrame) {
		super(fromObject, fromFrame);
	}

}
