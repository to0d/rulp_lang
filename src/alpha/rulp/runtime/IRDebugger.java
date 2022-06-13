package alpha.rulp.runtime;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;

public interface IRDebugger {

	public boolean addBreakObject(IRObject obj, IRInterpreter interpreter, IRFrame frame) throws RException;

	public boolean canBreak(IRObject obj);

	public boolean isDebug();

	public void popStack(IRFrame frame);

	public void pushStack(IRList expr, IRFrame frame) throws RException;

	public void run(IRInterpreter interpreter, IRFrame frame) throws RException;

	public void setup();

	public void shutdown();
}
