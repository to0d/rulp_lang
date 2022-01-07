/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import alpha.rulp.lang.IRString;
import alpha.rulp.lang.RType;

public class XRString extends AbsAtomObject implements IRString {

	private String value;

	public XRString(String value) {
		this.value = value;
	}

	@Override
	public String asString() {
		return value;
	}

	@Override
	public RType getType() {
		return RType.STRING;
	}
}