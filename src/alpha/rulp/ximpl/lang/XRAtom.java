/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.RName;

public class XRAtom extends AbsAtomObject implements IRAtom {

	private String atomName;

	private RName rName = null;

	public XRAtom(String atomName) {
		super();
		this.atomName = atomName;
	}

	public XRAtom(String atomName, RName rName) {
		super();
		this.atomName = atomName;
		this.rName = rName;
	}

	@Override
	public String asString() {
		return atomName;
	}

	@Override
	public String getName() {
		return atomName;
	}

	@Override
	public RName getRName() {
		return rName;
	}

	@Override
	public RType getType() {
		return RType.ATOM;
	}

}
