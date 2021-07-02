/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import java.util.List;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRIterator;

public class XRListList extends AbsRList implements IRList, IRExpr {

	protected final boolean earlyExpresion;

	protected List<? extends IRObject> elements;

	protected int size;

	public XRListList(List<? extends IRObject> elements, RType type, String name, boolean earlyExpresion) {
		super(type, name);
		this.elements = elements;
		this.size = elements.size();
		this.earlyExpresion = earlyExpresion;
	}

	@Override
	public IRObject get(int index) {
		return index < size ? elements.get(index) : null;
	}

	@Override
	public boolean isEarly() {
		return earlyExpresion;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public IRIterator<IRObject> listIterator(int fromIndex) {

		return new IRIterator<IRObject>() {

			int index = fromIndex;

			@Override
			public boolean hasNext() {
				return index < size;
			}

			@Override
			public IRObject next() {
				return get(index++);
			}
		};
	}

	@Override
	public int size() {
		return size;
	}

}
