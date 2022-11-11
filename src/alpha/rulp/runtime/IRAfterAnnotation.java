package alpha.rulp.runtime;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;

public interface IRAfterAnnotation {

	public IRObject build(IRObject obj, IRInterpreter interpreter, IRFrame frame) throws RException;
}
