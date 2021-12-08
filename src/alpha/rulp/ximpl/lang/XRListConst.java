/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpUtil;

public class XRListConst extends AbsRList implements IRList, IRExpr {

	protected IRObject elements[];

	protected int size;

	protected final boolean earlyExpresion;

	public XRListConst(IRObject elements[], RType type, String name, boolean earlyExpresion) throws RException {

		super(type, name);
		this.elements = elements;
		this.size = elements == null ? 0 : elements.length;
		this.earlyExpresion = earlyExpresion;

		if (elements != null) {
			for (IRObject e : elements) {
				if (e != null) {
					RulpUtil.incRef(e);
				}
			}
		}
	}

	@Override
	protected void _delete() throws RException {
		if (elements != null) {
			for (IRObject e : elements) {
				if (e != null) {
					RulpUtil.decRef(e);
				}
			}
		}
		super._delete();
	}

	public void add(IRObject obj) throws RException {
		throw new RException("Can't add object to const list: " + obj);
	}

	@Override
	public IRObject get(int index) {
		return index < size ? elements[index] : null;
	}

	@Override
	public boolean isConst() {
		return true;
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
