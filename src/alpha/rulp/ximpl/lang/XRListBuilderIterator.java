/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import java.util.ArrayList;
import java.util.Iterator;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRIterator;

public class XRListBuilderIterator<T> extends AbsRList implements IRList, IRExpr {

	public static interface IRObjectBuidler<T> {
		public IRObject get(T v);
	}

	protected IRObjectBuidler<T> builder;

	protected ArrayList<IRObject> elements = null;

	protected Iterator<T> inputIterator;

	protected int scanSize = 0;

	protected int size = -1;

	public XRListBuilderIterator(Iterator<T> iter, IRObjectBuidler<T> builder, RType type, String name) {
		super(type, name);
		this.inputIterator = iter;
		this.builder = builder;
	}

	protected boolean _scanTo(int toIndex) {

		if (size != -1) {
			return toIndex < size;
		}

		while (toIndex >= scanSize) {

			if (!inputIterator.hasNext()) {
				size = scanSize;
				return toIndex < size;
			}

			IRObject obj = builder.get(inputIterator.next());

			if (elements == null) {
				elements = new ArrayList<>();
			}

			elements.add(obj);
			++scanSize;
		}

		return true;
	}

	@Override
	public IRObject get(int index) {

		_scanTo(index);

		if (size == -1) {
			return elements.get(index);
		}

		return (size == -1 || index < size) ? elements.get(index) : null;
	}

	@Override
	public boolean isEarly() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		_scanTo(0);
		return size == 0;
	}

	@Override
	public IRIterator<IRObject> listIterator(int fromIndex) {

		return new IRIterator<IRObject>() {

			int index = fromIndex;

			@Override
			public boolean hasNext() {
				return _scanTo(index);
			}

			@Override
			public IRObject next() {
				return get(index++);
			}
		};
	}

	@Override
	public int size() {

		if (size == -1) {

			while (inputIterator.hasNext()) {

				IRObject obj = builder.get(inputIterator.next());

				if (elements == null) {
					elements = new ArrayList<>();
				}

				elements.add(obj);
				++scanSize;
			}

			size = scanSize;
		}

		return size;
	}
}
