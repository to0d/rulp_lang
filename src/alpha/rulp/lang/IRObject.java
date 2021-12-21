/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes w@Override
	ith ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

import java.util.List;

import alpha.rulp.runtime.IRListener1;

public interface IRObject {

	public void addAttribute(String attr);

	public void addObjectDeletedListener(IRListener1<IRObject> listener);

	public String asString();

	public boolean containAttribute(String attr);

	public void decRef() throws RException;

	public List<String> getAttributeList();

	public int getMaxRef();

	public int getRef();

	public RType getType();

	public void incRef() throws RException;

	public boolean isConst();

	public boolean isDeleted();

	public boolean removeAttribute(String attr);
}
