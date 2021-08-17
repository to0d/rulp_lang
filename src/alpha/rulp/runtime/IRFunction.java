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

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRParaAttr;
import alpha.rulp.lang.RException;

public interface IRFunction extends IRObject, IRCallable {

	public int getArgCount();

	public IRFrame getDefineFrame();

	public IRExpr getFunBody();

	public String getName();

	public List<IRParaAttr> getParaAttrs();

	public String getSignature() throws RException;

	public boolean isLambda();

	public boolean isList();
}
