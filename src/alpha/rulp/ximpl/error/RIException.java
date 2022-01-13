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
import alpha.rulp.lang.RException;

public class RIException extends RException {

	private static final long serialVersionUID = -426756038895792036L;

	protected IRFrame fromFrame;

	protected boolean handle = false;

	public RIException(IRObject fromObject, IRFrame fromFrame) {
		super(fromObject);
		this.fromFrame = fromFrame;
	}

	public RIException(IRObject fromObject, IRFrame fromFrame, String message) {
		super(fromObject, message);
		this.fromFrame = fromFrame;
	}

	public boolean isHandle() {
		return handle;
	}

	public void setHandle(boolean handle) {
		this.handle = handle;
	}
}
