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
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.RulpUtil;

public class XRListBuilderIterator<T> extends AbsRListIterator implements IRList, IRExpr {

	public static interface IRObjectBuidler<T> {
		public IRObject get(T v);
	}

	protected IRObjectBuidler<T> builder;

	protected Iterator<T> inputIterator;

	protected int scanSize = 0;

	public XRListBuilderIterator(Iterator<T> iter, IRObjectBuidler<T> builder, RType type, String name) {
		super(type, name);
		this.inputIterator = iter;
		this.builder = builder;
	}

	@Override
	protected boolean _scanTo(int toIndex) throws RException {

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
			RulpUtil.incRef(obj);
			++scanSize;
		}

		return true;
	}

	@Override
	public void add(IRObject obj) throws RException {
		throw new RException("Can't add object to const list: " + obj);
	}

	@Override
	public boolean isConst() {
		return true;
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
