/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.rclass;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.SubjectUtil;
import alpha.rulp.ximpl.subject.AbsRSubject;

public abstract class AbsRInstance extends AbsRSubject implements IRInstance {

	protected String _toString = null;

	protected final String instanceName;

	protected IRClass rClass;

	public AbsRInstance() {
		super();
		this.instanceName = RulpFactory.getNextUnusedName();
	}

	public AbsRInstance(IRClass rClass, String instanceName, IRFrame definedFrame) {
		super(definedFrame);
		this.rClass = rClass;
		this.instanceName = instanceName == null ? RulpFactory.getNextUnusedName() : instanceName;
	}

	@Override
	public String asString() {
		return instanceName;
	}

	@Override
	public void delete(IRInterpreter interpreter, IRFrame frame) throws RException {

		if (!this.isDeleted()) {
			this.ref = 0; // force deleted
			this._delete();
		}
	}

	@Override
	public String getInstanceName() {
		return instanceName;
	}

	@Override
	public IRMember getMember(String name) throws RException {

		if (name == null) {
			return null;
		}

		// Instance local member
		IRMember objMbr = super.getMember(name);
		if (objMbr == null && rClass != null) {
			objMbr = SubjectUtil.getClassMember(this, rClass, name);
			if (objMbr != null) {
				this.setMember(name, objMbr);
			}
		}

		return objMbr;
	}

	@Override
	public IRSubject getParent() {
		return rClass;
	}

	@Override
	public IRClass getRClass() {
		return rClass;
	}

	@Override
	public String getSubjectName() {
		return instanceName;
	}

	@Override
	public RType getType() {
		return RType.INSTANCE;
	}

	@Override
	public void init(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

	}

	public void setRClass(IRClass rClass) {
		this.rClass = rClass;
	}

	@Override
	public String toString() {

		if (_toString == null) {
			String className = (rClass == null ? "nil" : rClass.getClassName());
			_toString = className + "@" + instanceName;
		}

		return _toString;
	}

}
