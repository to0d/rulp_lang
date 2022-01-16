package alpha.rulp.ximpl.attribute;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorStmtCountOf extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorStmtCountOf(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		return RulpFactory.createInteger(StmtCountUtil.getStmtCount(args.get(1), interpreter, frame));
	}

}
