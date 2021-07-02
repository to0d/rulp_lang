/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import alpha.rulp.lang.IRFloat;
import alpha.rulp.lang.RType;

public class XRFloat extends AbsAtomObject implements IRFloat {

	private float value;

	public XRFloat(float value) {
		super();
		this.value = value;
	}

	@Override
	public float asFloat() {
		return value;
	}

	@Override
	public String asString() {
		return "" + value;
	}

	@Override
	public RType getType() {
		return RType.FLOAT;
	}

}
