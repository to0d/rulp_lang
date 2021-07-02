package alpha.rulp.runtime;

import alpha.rulp.lang.RException;

public interface IRTokener {

	public static class Token {

		public final int endPos;

		public int lineIndex = 0;

		public final TokenType type;

		public final String value;

		public Token(TokenType tokenType, String tokenValue, int tokenEndPos) {
			super();
			this.type = tokenType;
			this.value = tokenValue;
			this.endPos = tokenEndPos;
		}

		@Override
		public String toString() {
			return "[" + getType(type) + ":" + (value == null ? 0 : value.length()) + ":" + lineIndex + ":" + endPos
					+ ":" + value + "]";
		}
	}

	public enum TokenType {
		TT_0BAD, // Bad
		TT_1BLK, // symbol blank space
		TT_2SYM, // other symbol
		TT_3NAM, // simple string, ABC123, _123ABC,
		TT_4STR, // in quotation marks, "abc123 "
		TT_5INT, // integer, 0123
		TT_6FLT, // float 123.5
		TT_7CBI, // Combine Symbols "%%"
		TT_8FLE, // float 11.0e+4, scientific expression
		TT_9END // '\n'
	}

	public static char getType(TokenType t) {
		switch (t) {
		case TT_0BAD:
			return 'B';

		case TT_1BLK:
			return 'X';

		case TT_2SYM:
			return 'S';

		case TT_7CBI:
			return 'C';

		case TT_3NAM:
			return 'N';

		case TT_4STR:
			return 'T';

		case TT_5INT:
			return 'I';

		case TT_6FLT:
			return 'F';

		default:
			return 'U';
		}
	}

	public Token next() throws RException;

	public Token peek() throws RException;

	public void setContent(String content);

	public void setStrictMode(boolean strictMode);

}
