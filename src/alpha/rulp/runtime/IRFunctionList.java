/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.runtime;

import java.util.List;

import alpha.rulp.lang.RException;

public interface IRFunctionList extends IRFunction {

	public void addFunc(IRFunction fun) throws RException;

	public List<IRFunction> getAllFuncList();

}
