/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.rclass;

import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RAccessType;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.ximpl.lang.AbsRefObject;

public class XRMember extends AbsRefObject implements IRMember {

	private String _string = null;

	private RAccessType accessType = RAccessType.DEFAULT;

	private boolean bFinal = false;

	private boolean bInherit = false;

	private boolean bStatic = false;

	private String name;

	private IRObject subject;

	private IRObject value;

	public XRMember(IRObject subject, String name, IRObject value) {
		super();
		this.subject = subject;
		this.name = name;
		this.value = value;
	}

	@Override
	public String asString() {

		if (_string == null) {
			_string = "" + subject + "::" + name;
		}

		return _string;
	}

	@Override
	public RAccessType getAccessType() {
		return accessType;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IRObject getSubject() {
		return subject;
	}

	@Override
	public RType getType() {
		return RType.MEMBER;
	}

	@Override
	public IRObject getValue() {
		return value;
	}

	@Override
	public boolean isFinal() {
		return bFinal;
	}

	@Override
	public boolean isInherit() {
		return bInherit;
	}

	@Override
	public boolean isStatic() {
		return bStatic;
	}

	@Override
	public void setAccessType(RAccessType accessType) throws RException {
		this.accessType = accessType;
	}

	@Override
	public void setFinal(boolean bFinal) {
		this.bFinal = bFinal;
	}

	@Override
	public void setInherit(boolean bInherit) throws RException {
		this.bInherit = bInherit;
	}

	@Override
	public void setStatic(boolean bStatic) throws RException {
		this.bStatic = bStatic;
	}

	@Override
	public String toString() {
		return asString();
	}

}
