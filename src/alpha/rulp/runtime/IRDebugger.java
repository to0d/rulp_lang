package alpha.rulp.runtime;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;

public interface IRDebugger {

	public boolean addBreakObject(IRObject obj, IRInterpreter interpreter, IRFrame frame) throws RException;

	public void debugBegin(IRObject e0, IRList expr, IRInterpreter interpreter, IRFrame frame) throws RException;

	public void debugEnd(IRFrame frame);

	public boolean isDebug();

	public void setup();

	public void shutdown();
}
