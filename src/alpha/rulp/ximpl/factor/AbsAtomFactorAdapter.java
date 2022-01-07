/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.factor;

import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.ximpl.runtime.AbsAtomCallableAdapter;

public abstract class AbsAtomFactorAdapter extends AbsAtomCallableAdapter implements IRFactor {

	protected String factorName;

	public AbsAtomFactorAdapter(String factorName) {
		super();
		this.factorName = factorName;
	}

	@Override
	public String asString() {
		return factorName;
	}

	@Override
	public String getName() {
		return factorName;
	}

	@Override
	public RType getType() {
		return RType.FACTOR;
	}

	@Override
	public String toString() {
		return factorName;
	}
}
