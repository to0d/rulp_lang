/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import static alpha.rulp.lang.Constant.MAX_TOSTRING_LEN;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpUtil;

public class XRListNative extends AbsAtomObject implements IRList, IRExpr {

	protected final boolean earlyExpresion;

	protected IRObject elements[];

	protected String name;

	protected int size;

	protected RType type;

	public XRListNative(IRObject elements[], RType type, String name, boolean earlyExpresion) {

		this.type = type;
		this.name = name;
		this.elements = elements;
		this.size = elements == null ? 0 : elements.length;
		this.earlyExpresion = earlyExpresion;
	}

	public void add(IRObject obj) throws RException {
		throw new RException("Can't add object to const list: " + obj);
	}

	@Override
	public String asString() {

		try {
			return RulpUtil.toString(this);
		} catch (RException e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	@Override
	public IRObject get(int index) {
		return index < size ? elements[index] : null;
	}

	@Override
	public String getNamedName() {
		return name;
	}

	@Override
	public RType getType() {
		return RType.LIST;
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
	public IRIterator<? extends IRObject> iterator() {
		return listIterator(0);
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

	@Override
	public String toString() {

		try {
			return RulpUtil.toString(this, MAX_TOSTRING_LEN);
		} catch (RException e) {
			e.printStackTrace();
			return e.toString();
		}
	}

}
