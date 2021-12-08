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

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpUtil;

public class XRListVary extends AbsRList implements IRList, IRExpr {

	protected ArrayList<IRObject> elements = null;

	protected int size = 0;

	public XRListVary(RType type, String name) {
		super(type, name);
	}

	@Override
	protected void _delete() throws RException {
		if (elements != null) {
			for (IRObject e : elements) {
				if (e != null) {
					RulpUtil.decRef(e);
				}
			}
//			elements = null;
		}
		super._delete();
	}

	@Override
	public void add(IRObject obj) throws RException {

		if (elements == null) {
			elements = new ArrayList<>();
		}

		elements.add(obj);
		this.size++;
		RulpUtil.incRef(obj);
	}

	@Override
	public IRObject get(int index) {
		return index < size ? elements.get(index) : null;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isEarly() {
		return false;
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
