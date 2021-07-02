/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

public class RError extends RException {

	private static final long serialVersionUID = 1L;

	protected IRError err;

	protected IRFrame fromFrame;

	public RError(IRFrame fromFrame, IRObject fromObject, IRError err) {
		super(fromObject);
		this.fromFrame = fromFrame;
		this.err = err;
	}

	public IRError getError() {
		return err;
	}

	public IRFrame getFromFrame() {
		return fromFrame;
	}

	public String toString() {
		return "" + err;
	}
}
