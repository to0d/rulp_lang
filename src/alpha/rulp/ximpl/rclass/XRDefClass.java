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
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;

public final class XRDefClass extends AbsRClass implements IRClass {

	public XRDefClass(String className, IRFrame definedFrame, IRClass superClass) {
		super(className, definedFrame, superClass);
	}

	public XRDefClass(String className) {
		super(className, null, null);
	}

	@Override
	public IRInstance newInstance(String instanceName, IRList args, IRInterpreter interpreter, IRFrame frame)
			throws RException {
		return RulpFactory.createInstanceOfDefault(this, instanceName, this.getSubjectFrame());
	}

}
