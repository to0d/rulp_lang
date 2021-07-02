package alpha.rulp.runtime;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;

public interface IRFactorBody {

	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException;
}
