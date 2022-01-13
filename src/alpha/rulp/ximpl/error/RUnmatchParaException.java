package alpha.rulp.ximpl.error;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRObject;

public class RUnmatchParaException extends RIException {

	private static final long serialVersionUID = 5054712911219412309L;

	public RUnmatchParaException(IRObject fromObject, IRFrame fromFrame, String message) {
		super(fromObject, fromFrame, message);
	}

}
