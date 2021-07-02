/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

import alpha.rulp.runtime.IRInterpreter;

public interface IRInstance extends IRObject, IRSubject {

	public void delete(IRInterpreter interpreter, IRFrame frame) throws RException;

	public String getInstanceName();

	public IRClass getRClass();

	public void init(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException;
}
