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

public class XRListIterator extends AbsRListIterator implements IRList, IRExpr {

	protected Iterator<? extends IRObject> inputIterator;

	protected int scanSize = 0;

	public XRListIterator(Iterator<? extends IRObject> iter, RType type, String name) {
		super(type, name);
		this.inputIterator = iter;
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

			IRObject obj = inputIterator.next();

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

				IRObject obj = inputIterator.next();

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
