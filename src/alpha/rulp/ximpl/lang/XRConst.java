/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import alpha.rulp.lang.IRConst;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.RulpUtil;

public class XRConst extends AbsRefObject implements IRConst {

	private String _asString = null;

	private IRObject value;

	private final String varName;

	public XRConst(String varName, IRObject value) throws RException {
		super();
		this.varName = varName;
		this.value = value;

		RulpUtil.incRef(value);
	}

	protected void _delete() throws RException {

		if (value != null) {
			RulpUtil.decRef(value);
			value = null;
		}

		super._delete();
	}

	@Override
	public String asString() {

		if (_asString == null) {
			_asString = varName + ":\"" + value.asString() + "\"";
		}

		return _asString;
	}

	@Override
	public String getName() {
		return varName;
	}

	@Override
	public RType getType() {
		return RType.CONSTANT;
	}

	@Override
	public IRObject getValue() {
		return value;
	}

}
