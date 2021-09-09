package alpha.rulp.ximpl.namespace;

import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRNameSpace;
import alpha.rulp.ximpl.rclass.AbsRInstance;

public class XRNameSpace extends AbsRInstance implements IRNameSpace {

	public XRNameSpace(String spaceName, IRClass rclass, IRFrame frame) throws RException {
		super(rclass, spaceName, frame);
	}
}
