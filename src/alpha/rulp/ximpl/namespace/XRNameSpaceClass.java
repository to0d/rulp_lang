package alpha.rulp.ximpl.namespace;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.ximpl.rclass.AbsRClass;

public class XRNameSpaceClass extends AbsRClass implements IRClass {

	public XRNameSpaceClass(String className, IRFrame definedFrame) {
		super(className, definedFrame, null);
	}

	@Override
	public IRInstance newInstance(String nameSpaceName, IRList args, IRInterpreter interpreter, IRFrame frame)
			throws RException {

		if (args.size() != 3) {
			throw new RException("Invalid construct parameters: " + args);
		}

		return RulpFactory.createNameSpace(nameSpaceName, this, frame);
	}

}
