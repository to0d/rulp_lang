/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

import alpha.rulp.runtime.IRIterator;

public interface IRList extends IRObject, IRArrayList {

	public void add(IRObject obj) throws RException;

	public String getNamedName();

	public boolean isConst();

	public IRIterator<? extends IRObject> iterator();

	public IRIterator<? extends IRObject> listIterator(int fromIndex);

}