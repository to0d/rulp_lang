/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.lang;

import java.util.List;

import alpha.rulp.runtime.IRFrameLoader;
import alpha.rulp.runtime.IRListener1;
import alpha.rulp.runtime.IRThreadContext;

public interface IRFrame extends IRObject, IRSubject {

	public void addFrameReleaseListener(IRListener1<IRFrame> listener);

	public void addSearchFrame(IRFrame frame);

	public IRFrameEntry createFrameEntry(String name, IRObject object);

	public IRObject findLocalObject(String name) throws RException;

	public IRFrameEntry getEntry(String name) throws RException;

	public int getFrameId();

	public String getFrameName();

	public IRObject getObject(String name) throws RException;

	public IRFrame getParentFrame();

	public List<IRFrame> getSearchFrameList();

	public IRSubject getSubject();

	public IRThreadContext getThreadContext();

	public List<IRFrameEntry> listEntries();

	public void release() throws RException;

	public IRFrameEntry removeEntry(String name) throws RException;

	public void setEntry(String name, IRObject obj) throws RException;

	public void setEntryAliasName(IRFrameEntry entry, String aliasName) throws RException;

	public void setFrameLoader(IRFrameLoader frameLoader);

	public void setThreadContext(IRThreadContext context);

}
