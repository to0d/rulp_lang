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

public interface IRList extends IRObject {

	public void add(IRObject obj) throws RException;

	public IRObject get(int index) throws RException;

	public String getNamedName();

	public boolean isEmpty() throws RException;

	public IRIterator<? extends IRObject> iterator();

	public IRIterator<? extends IRObject> listIterator(int fromIndex);

	public int size() throws RException;

	public boolean isConst();
}