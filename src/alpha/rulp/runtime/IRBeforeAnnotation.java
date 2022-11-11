package alpha.rulp.runtime;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.RException;

public interface IRBeforeAnnotation {

	public IRList build(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException;
}
