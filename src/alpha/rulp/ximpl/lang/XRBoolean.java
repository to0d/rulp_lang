/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import alpha.rulp.lang.IRBoolean;
import alpha.rulp.lang.RType;

public class XRBoolean extends AbsAtomObject implements IRBoolean {

	private boolean value;

	public XRBoolean(boolean value) {
		super();
		this.value = value;
	}

	@Override
	public boolean asBoolean() {
		return value;
	}

	@Override
	public String asString() {
		return "" + value;
	}

	@Override
	public RType getType() {
		return RType.BOOL;
	}

}
