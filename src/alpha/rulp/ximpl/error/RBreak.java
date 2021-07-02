package alpha.rulp.ximpl.error;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRObject;

public class RBreak extends RIException {

	private static final long serialVersionUID = 1930566403100269029L;

	public RBreak(IRObject fromObject, IRFrame fromFrame) {
		super(fromObject, fromFrame);
	}
}
