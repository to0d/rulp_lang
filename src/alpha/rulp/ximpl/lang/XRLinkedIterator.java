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

public class XRLinkedIterator<T> implements IRIterator<T> {

	private Iterator<T> headIterator = null;

	private boolean isEnd = false;

	private final Iterator<Iterator<T>> listIterator;

	public XRLinkedIterator(Iterator<Iterator<T>> listIterator) {
		super();
		this.listIterator = listIterator;
	}

	@Override
	public boolean hasNext() throws RException {

		if (isEnd) {
			return false;
		}

		while (headIterator == null || !headIterator.hasNext()) {

			if (!listIterator.hasNext()) {
				headIterator = null;
				isEnd = true;
				return false;
			}

			headIterator = listIterator.next();
		}

		return true;
	}

	@Override
	public T next() throws RException {
		return hasNext() ? headIterator.next() : null;
	}

}
