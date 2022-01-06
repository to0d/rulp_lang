package alpha.rulp.ximpl.io;

import static alpha.rulp.lang.Constant.O_Nil;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.TraceUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorPrintFrameTree extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorPrintFrameTree(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {
		interpreter.out(TraceUtil.outputFrameTree(interpreter));
		return O_Nil;
	}

}
