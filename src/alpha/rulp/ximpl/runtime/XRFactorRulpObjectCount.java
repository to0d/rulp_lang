package alpha.rulp.ximpl.runtime;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;

public class XRFactorRulpObjectCount extends AbsRFactorAdapter implements IRFactor {

	public XRFactorRulpObjectCount(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		RType type = RType.toType(RulpUtil.asAtom(interpreter.compute(frame, args.get(1))).asString());
		int count = RulpFactory.getObjectCreateCount(type) - RulpFactory.getObjectDeleteCount(type);
		return RulpFactory.createInteger(count);
	}

}
