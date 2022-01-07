/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.error;

import static alpha.rulp.lang.Constant.A_ERROR;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRError;
import alpha.rulp.lang.IRObject;
import alpha.rulp.ximpl.rclass.AbsRInstance;

public class XRError extends AbsRInstance implements IRError {

	protected IRAtom id;

	protected IRObject value;

	public XRError(IRClass noClass, IRAtom id, IRObject value) {
		super(noClass, id.getName(), null);
		this.id = id;
		this.value = value;
	}

	@Override
	public String asString() {
		return A_ERROR + ": " + id.getName() + ", " + value;
	}

	@Override
	public IRAtom getId() {
		return id;
	}

	@Override
	public IRObject getValue() {
		return value;
	}

	@Override
	public String toString() {
		return asString();
	}
}
