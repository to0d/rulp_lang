/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

import alpha.rulp.runtime.IRListener3;

public interface IRVar extends IRObject {

	public String getName();

	public IRObject getValue();

	public void setValue(IRObject newVal) throws RException;

	public void addVarListener(IRListener3<IRVar, IRObject, IRObject> listener);
}
