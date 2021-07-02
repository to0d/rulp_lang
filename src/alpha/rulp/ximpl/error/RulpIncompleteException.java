package alpha.rulp.ximpl.error;

public class RulpIncompleteException extends RParseException {

	public RulpIncompleteException(int lineLindex, String msg) {
		super(lineLindex, msg);
	}

	private static final long serialVersionUID = -6311400804269250528L;

}
