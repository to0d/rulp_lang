/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.runtime;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;

public interface IRInterpreter {

	public void addObject(IRObject obj) throws RException;

	public IRObject compute(IRFrame frame, IRObject obj) throws RException;

	public void compute(String input) throws RException;

	public void compute(String input, IRListener1<IRObject> resultListener) throws RException;

	public void endDebug();

	public IRDebugger getActiveDebugger();

	public int getCallId();

	public IRInput getInput();

	public IRFrame getMainFrame();

	public int getMaxCallLevel();

	public IRObject getObject(String name) throws RException;

	public IROut getOut();

	public IRParser getParser();

	public IRTLS getTLS();

	public void out(String line);

	public String read() throws RException;

	public void setInput(IRInput in);

	public void setOutput(IROut out);

	public void startDebug();
}
