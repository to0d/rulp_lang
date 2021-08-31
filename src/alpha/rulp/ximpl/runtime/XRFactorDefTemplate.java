package alpha.rulp.ximpl.runtime;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;

public class XRFactorDefTemplate extends AbsRFactorAdapter implements IRFactor {

	public XRFactorDefTemplate(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean isThreadSafe() {
		return true;
	}
}
