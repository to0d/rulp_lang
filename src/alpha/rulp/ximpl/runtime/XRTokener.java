/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime;

import static alpha.rulp.string.Constant.EN_SEPARATION_DOT;

import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRTokener;
import alpha.rulp.utils.StringUtil;

public class XRTokener implements IRTokener {

	public static final int C00_CHAR = 0; // character

	public static final int C01_NUM = 1; // number

	public static final int C02_BLANK = 2; // Symbol Blank " " or \t

	public static final int C03_STRING = 3; // String ""

	public static final int C04_UNDERSCORE = 4; // Symbol _

	public static final int C05_PERIOD = 5; // Symbol .

	public static final int C06_OPERATOR = 6; // Symbol

	public static final int C07_END = 7; // New line \r \n

	public static final int C09_LEFT = 8; // Symbol (

	public static final int C10_RIGHT = 9; // Symbol )

	public static final int CX_UNKNOWN = -1; // unknown char

	static final int S_0INI = 0; // Init mode

	static final int S_BAD = -99; // bad mode

	static final int S_OT1 = -1; // output Name (without curChar)

	static final int S_OT2 = -2; // output integer (without curChar)

	static final int S_OT3 = -3; // output float (without curChar)

	static final int S_OT4 = -4; // output Blank (without curChar)

	static final int S_OT5 = -5; // output Symbol (with curChar)

//	static final int SAANY = 7; // any statement except "BLANK" or "END"

	static final int S_OT6 = -6; // output String with curChar

	static final int S1SKI = 1; // Skip mode

	static final int S2BLK = 2; // Blank mode

	static final int S4NAM = 3; // Name mode

	static final int S5INT = 4; // Integer mode

	static final int S6FLO = 5; // Float mode

	static final int S7STR = 6; // String mode

	static final int[][] X_STATE_STRICT = {
			// CHAR - NUM - BLANK -- " ----- _ ---- . -- SYMBOL END --- ( ----- )
			{ S4NAM, S5INT, S2BLK, S7STR, S4NAM, S_OT5, S_OT5, S1SKI, S_OT5, S_OT5 }, // S0INI
			{ S4NAM, S5INT, S2BLK, S7STR, S4NAM, S_OT5, S_OT5, S1SKI, S_OT5, S_OT5 }, // S1SKI
			{ S_OT4, S_OT4, S2BLK, S_OT4, S_OT4, S_OT4, S_OT4, S_OT4, S_OT4, S_OT4 }, // S2BLK
			{ S4NAM, S4NAM, S_OT1, S_OT1, S4NAM, S4NAM, S_OT1, S_OT1, S_OT1, S_OT1 }, // S4NAM
			{ S4NAM, S5INT, S_OT2, S_OT2, S4NAM, S6FLO, S_OT2, S_OT2, S_OT2, S_OT2 }, // S5INT
			{ S_OT3, S6FLO, S_OT3, S_OT3, S_OT3, S_OT3, S_OT3, S_OT3, S_OT3, S_OT3 }, // S6FLO:float
			{ S7STR, S7STR, S7STR, S_OT6, S7STR, S7STR, S7STR, S7STR, S7STR, S7STR }, // S7STR
	};

	static final int[][] X_STATE2 = {
			// CHAR - NUM - BLANK -- " ----- _ ---- . -- SYMBOL END --- ( ----- )
			{ S4NAM, S5INT, S2BLK, S7STR, S4NAM, S_OT5, S_OT5, S1SKI, S_OT5, S_OT5 }, // S0INI
			{ S4NAM, S5INT, S2BLK, S7STR, S4NAM, S_OT5, S_OT5, S1SKI, S_OT5, S_OT5 }, // S1SKI
			{ S_OT4, S_OT4, S2BLK, S_OT4, S_OT4, S_OT4, S_OT4, S_OT4, S_OT4, S_OT4 }, // S2BLK
			{ S4NAM, S4NAM, S_OT1, S_OT1, S4NAM, S4NAM, S_OT1, S_OT1, S_OT1, S_OT1 }, // S4NAM
			{ S4NAM, S5INT, S_OT2, S_OT2, S4NAM, S6FLO, S_OT2, S_OT2, S_OT2, S_OT2 }, // S5INT
			{ S_OT3, S6FLO, S_OT3, S_OT3, S_OT3, S_OT3, S_OT3, S_OT3, S_OT3, S_OT3 }, // S6FLO:float
			{ S7STR, S7STR, S7STR, S_OT6, S7STR, S7STR, S7STR, S_OT1, S_OT1, S_OT1 }, // S7STR
	};

	static boolean _isSingleSymbol(Token token, char c) {
		return token != null && token.type == TokenType.TT_2SYM && token.value.length() == 1
				&& token.value.charAt(0) == c;
	}

	public static int getCharType(char c) {

		switch (c) {
		case 0x0A: // '\n';
		case 0x0D: // '\r'
			return C07_END;
		case 0x20: // blank
		case 0x09: // tab
			return C02_BLANK;
		case '"':
			return C03_STRING;

		case '(':
			return C09_LEFT;

		case ')':
			return C10_RIGHT;
		case '+':
		case '-':
		case '\'':
		case '\\':
		case '|':
		case ':':
		case ';':
		case ',':
		case '[':
		case ']':
		case '{':
		case '}':
		case '@':
		case '#':
		case '=':
		case '/':
		case '$':
		case '?':
		case '&':
		case '*':
		case '%':
		case '<':
		case '>':
		case '!':
		case '^':
		case '`':
		case EN_SEPARATION_DOT:
			return C06_OPERATOR;
		case '_':
			return C04_UNDERSCORE;
		case '.':
			return C05_PERIOD;

		default:
			break;
		}

		switch (StringUtil.getCharType(c)) {
		case CN_CHAR:
		case CN_SYMBOL:
		case TW_ZHUYIN:
		case JP_SYMBOL:
		case ROMAN_NUM:
			return C00_CHAR;

		case NUMBER:
			return C01_NUM;

		case EN_CHAR:
			return C00_CHAR;

		default:
			break;
		}

		return CX_UNKNOWN;
	}

	protected String content = null;

	protected int curPos = 0;

	protected int length = 0;

	private int[][] state = X_STATE_STRICT;

	private boolean strictMode = true;

	protected Token _scan(int begPos) throws RException {

		int scanPos = begPos;
		int retPos = -1;
		int lastState = S_0INI;
		int curState = S_0INI;
		TokenType findTokenType = null;

		char lastChar = 0;
		char stringBeginSymbol = 0;
		int escapeCount = 0;

		if (begPos >= length) {
			return null;
		}

		for (; findTokenType == null && scanPos <= length; lastState = curState, scanPos++) {

			int charType;

			if (scanPos >= length) {

				charType = C07_END;

			} else {

				lastChar = content.charAt(scanPos);
				charType = getCharType(lastChar);
				if (charType == CX_UNKNOWN) {

					// skip some char in a string " abc??def "
					if (lastState == S7STR) {
						continue;
					}

					// support any name
					if (lastState == S4NAM) {
						continue;
					}

					// not support char
					throw new RException(content + ":unsupport char<" + lastChar + ">, pos=" + scanPos);
				}
			}

			curState = state[lastState][charType];

			switch (curState) {
			case S_BAD:

				// example 11.0e+4
//				if (lastState == S_5FLO && lastChar == 'e') {
//					
//				}
//
//				if (lastState == S6FLO) {
//					curState = SAANY;
//					break;
//				}

				throw new RException(content + ": Invald DFA state in char<" + lastChar + ">, pos=" + scanPos);

			case S1SKI:
				++begPos;
				break;

			// output token : Name without curChar
			case S_OT1:
				return new Token(TokenType.TT_3NAM, content.substring(begPos, scanPos), scanPos);

			// output token : integer( without curChar)
			case S_OT2:
				return new Token(TokenType.TT_5INT, content.substring(begPos, scanPos), scanPos);

			// output token : float( without curChar)
			case S_OT3:
				return new Token(TokenType.TT_6FLT, content.substring(begPos, scanPos), scanPos);

			// output token : Blank( without curChar)
			case S_OT4:
				return new Token(TokenType.TT_1BLK, content.substring(begPos, scanPos), scanPos);

			// output token : Symbol (with curChar)
			case S_OT5:

				retPos = scanPos + 1;
				if (retPos != (begPos + 1))
					throw new RException(content + ": Invald DFA state in char<" + lastChar + ">, pos=" + scanPos);

				findTokenType = TokenType.TT_2SYM;
				break;

			// output token : String with curChar
			case S_OT6:

				if (stringBeginSymbol == 0)
					throw new RException(content + ": Null String Char, pos=" + scanPos);

				// Start with " and new the lastChar is '
				if (stringBeginSymbol != lastChar) {

					// Continue scan to "StringBeginSymbol"
					curState = S7STR;

				} else {

					retPos = scanPos + 1;
					findTokenType = TokenType.TT_4STR;
				}

				break;

			case S7STR:

				if (stringBeginSymbol == 0) {
					stringBeginSymbol = lastChar;
				}

				// Escape char
				if (lastChar == '\\' && ((scanPos + 1) <= length)) {
					char nextChar = content.charAt(scanPos + 1);
					if (StringUtil.isEscapeChar(nextChar)) {
						++escapeCount;
						++scanPos;
					}
				}

				break;

			}// switch (curState)
		}

		if (findTokenType == null) {

			switch (curState) {

			// String end in \n or \r
			case S1SKI:
				return null;

			default:
				findTokenType = TokenType.TT_0BAD;
				retPos = length;
			}
		}

		if (begPos >= retPos) {
			throw new RException(content + ": invaild length, <" + begPos + ":" + retPos + ">, pos=" + scanPos);
		}

		String value = content.substring(begPos, retPos);
		if (escapeCount > 0) {
			value = StringUtil.removeEscape(value);
		}

		return new Token(findTokenType, value, retPos);
	}

	private void _updateState() {

		if (strictMode) {
			state = X_STATE_STRICT;
		} else {
			state = X_STATE2;
		}

	}

	public boolean isStrictStringMode() {
		return strictMode;
	}

	@Override
	public Token next() throws RException {

		Token token = peek();
		curPos = (token == null ? length : token.endPos);
		return token;
	}

	@Override
	public Token peek() throws RException {

		Token token = null;

		if ((token = _scan(curPos)) == null) {
			return null;
		}

		return token;
	}

	@Override
	public void setContent(String content) {
		this.content = (content == null ? "" : content);
		this.length = content.length();
		this.curPos = 0;
	}

	@Override
	public void setStrictMode(boolean strictMode) {
		this.strictMode = strictMode;
		this._updateState();
	}

}
