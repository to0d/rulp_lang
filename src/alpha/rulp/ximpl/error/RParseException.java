package alpha.rulp.ximpl.error;

import alpha.rulp.lang.RException;

public class RParseException extends RException {

	private static final long serialVersionUID = 5909927455956994808L;

	private int lineLindex;

	public RParseException(int lineLindex, String msg) {
		super(msg);
		this.lineLindex = lineLindex;
		this.msg = msg;
	}

	private String msg;

	public String toString() {
		return String.format("Bad Syntax at line %d: %s", lineLindex, msg);
	}
}
