/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import java.util.Iterator;

import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRIterator;

public class XRIteratorAdatper<T> implements IRIterator<T> {

	private Iterator<T> iter;

	public XRIteratorAdatper(Iterator<T> iter) {
		super();
		this.iter = iter;
	}

	@Override
	public boolean hasNext() throws RException {
		return iter.hasNext();
	}

	@Override
	public T next() throws RException {
		return iter.next();
	}

}
