/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.rclass;

import static alpha.rulp.lang.Constant.A_TYPE;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.ximpl.subject.AbsRSubject;

public abstract class AbsRClass extends AbsRSubject implements IRClass {

	protected String className;

	protected IRAtom classTypeAtom;

	protected IRClass superClass;

	public AbsRClass(String className, IRFrame definedFrame, IRClass superClass) {
		super(definedFrame);
		this.className = className;
		this.classTypeAtom = RulpFactory.createAtom(A_TYPE + className);
		this.superClass = superClass;
	}

	@Override
	public String asString() {
		return className;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public IRAtom getClassTypeAtom() {
		return classTypeAtom;
	}

	@Override
	public IRSubject getParent() {
		return superClass;
	}

	@Override
	public String getSubjectName() {
		return this.getClassName();
	}

	public IRClass getSuperClass() {
		return superClass;
	}

	@Override
	public RType getType() {
		return RType.CLASS;
	}

	@Override
	public boolean isConst() {
		return false;
	}

}
