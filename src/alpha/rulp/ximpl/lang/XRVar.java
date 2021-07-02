/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import static alpha.rulp.lang.Constant.O_Nil;

import java.util.LinkedList;
import java.util.List;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.IRVarListener;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.RulpUtil;

public class XRVar extends AbsRefObject implements IRVar {

	private String _asString = null;

	private IRObject value = O_Nil;

	private List<IRVarListener> varListenerList = null;

	private String varName;

	public XRVar(String varName) {
		super();
		this.varName = varName;
	}

	@Override
	protected void _delete() throws RException {

		if (this.value != null) {
			RulpUtil.decRef(this.value);
			this.value = null;
		}

		this.varListenerList = null;

		super._delete();
	}

	@Override
	public void addVarListener(IRVarListener listener) {

		if (varListenerList == null) {
			varListenerList = new LinkedList<>();
		}

		if (!varListenerList.contains(listener)) {
			varListenerList.add(listener);
		}
	}

	@Override
	public String asString() {

		if (_asString == null) {
			_asString = varName + ":\"" + value.asString() + "\"";
		}

		return _asString;
	}

	public void fireValueChanged(IRObject oldVal, IRObject newVal) throws RException {

		if (varListenerList == null) {
			return;
		}

		if (RulpUtil.equal(oldVal, newVal)) {
			return;
		}

		for (IRVarListener listener : varListenerList) {
			listener.valueChanged(this, oldVal, newVal);
		}
	}

	@Override
	public String getName() {
		return varName;
	}

	@Override
	public RType getType() {
		return RType.VAR;
	}

	@Override
	public IRObject getValue() {
		return value;
	}

	@Override
	public void setValue(IRObject newVal) throws RException {

		IRObject oldVal = this.value;
		this.value = newVal;
		this._asString = null;
		this.fireValueChanged(oldVal, newVal);

		RulpUtil.incRef(newVal);
		RulpUtil.decRef(oldVal);
	}

}
