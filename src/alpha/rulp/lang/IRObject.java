/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

public interface IRObject {

	public void addObjectDeletedListener(IRListener<IRObject> listener);

	public String asString();

	public void decRef() throws RException;

	public int getMaxRef();

	public int getRef();

	public RType getType();

	public void incRef() throws RException;

	public boolean isDeleted();

}
