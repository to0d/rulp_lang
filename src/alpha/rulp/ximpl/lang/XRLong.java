/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import alpha.rulp.lang.IRLong;
import alpha.rulp.lang.RType;

public class XRLong extends AbsAtomObject implements IRLong {

	long value;

	public XRLong(long value) {
		super();
		this.value = value;
	}

	@Override
	public long asLong() {
		return value;
	}

	@Override
	public String asString() {
		return "" + value + "L";
	}

	@Override
	public RType getType() {
		return RType.LONG;
	}

}