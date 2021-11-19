package alpha.rulp.ximpl.error;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRObject;

public class RInterrupt extends RIException {

	public RInterrupt(IRObject fromObject, IRFrame fromFrame) {
		super(fromObject, fromFrame);
	}

	private static final long serialVersionUID = 4748654476903459636L;

}
