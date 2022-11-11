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

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.utils.DeCounter;

public interface IRCallable extends IRObject {

	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException;

	public IRAfterAnnotation getAfterAnnotation(String attr);

	public IRBeforeAnnotation getBeforeAnnotation(String attr);

	public DeCounter getCallCount(int statsId);

	public boolean hasAfterAnnotation();

	public boolean hasBeforeAnnotation();

	public void incCallCount(int statsId, int callId);

	public boolean isDebug();

	public List<String> listAfterAnnotationAttr();
	
	public List<String> listBeforeAnnotationAttr();

	public void registerAfterAnnotation(String attr, IRAfterAnnotation builder);

	public void registerBeforeAnnotation(String attr, IRBeforeAnnotation builder);

	public void setDebug(boolean debug);
}
