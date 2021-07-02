/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import alpha.rulp.lang.IRInteger;
import alpha.rulp.lang.RType;

public class XRInteger extends AbsAtomObject implements IRInteger {

	int value;

	public XRInteger(int value) {
		super();
		this.value = value;
	}

	@Override
	public int asInteger() {
		return value;
	}

	@Override
	public String asString() {
		return "" + value;
	}

	@Override
	public RType getType() {
		return RType.INT;
	}

}