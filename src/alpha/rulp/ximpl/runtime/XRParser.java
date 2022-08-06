/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime;

import static alpha.rulp.lang.Constant.A_FALSE;
import static alpha.rulp.lang.Constant.A_TRUE;
import static alpha.rulp.lang.Constant.F_O_MBR;
import static alpha.rulp.lang.Constant.O_False;
import static alpha.rulp.lang.Constant.O_Nil;
import static alpha.rulp.lang.Constant.O_True;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.runtime.IRTokener;
import alpha.rulp.runtime.IRTokener.Token;
import alpha.rulp.runtime.IRTokener.TokenType;
import alpha.rulp.runtime.RName;
import alpha.rulp.utils.AttrUtil;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.StringUtil;
import alpha.rulp.ximpl.error.RParseException;
import alpha.rulp.ximpl.error.RulpIncompleteException;

public class XRParser implements IRParser {

	static final Token END_TOKEN = new Token(TokenType.TT_9END, null, -1);

	public static final int MAX_PARSE_COUNT = 65535 * 256;

	public static final int MAX_STACK_DEPTH = 65535 * 256;

	public static final int MAX_TOKEN_COUNT = 65535 * 256;

	private static boolean _isBlankToken(Token token) {
		return token.type == TokenType.TT_1BLK;
	}

	private static boolean _isEndToken(Token token) {
		return token.type == TokenType.TT_9END;
	}

	static boolean _isNameToken(Token token, String name) throws RException {

		if (token != null && token.type == TokenType.TT_3NAM && token.value != null && token.value.length() == 1
				&& token.value.equals(name)) {

			return true;
		}

		return false;
	}

	static boolean _isSeparatorToken(Token token) throws RException {

		if (token == null) {
			return true;
		}

		switch (token.type) {
		case TT_1BLK:
		case TT_9END:
			return true;

		case TT_2SYM:
			switch (token.value) {
			case "(":
			case ")":
			case "{":
			case "}":
			case "[":
			case "]":
			case ",":
				return true;
			}

		default:

		}

		return false;
	}

	private static boolean _isSupportIdentifierHeadToken(Token token) {

		if (token == null || token.value == null || token.value.length() == 0) {
			return false;
		}

		switch (token.type) {

		case TT_3NAM: {

			switch (XRTokener.getCharType(token.value.charAt(0))) {
			case XRTokener.C00_CHAR:
			case XRTokener.C01_NUM:
			case XRTokener.C04_UNDERSCORE:
				return true;
			}

			break;
		}

		case TT_2SYM:
			switch (token.value) {
			case "$":
			case "_":
			case "?":
				return true;
			}

			break;

		default:
			return false;
		}

		return false;
	}

	static boolean _isSymbolToken(Token token, char symbol) throws RException {

		if (token != null && token.type == TokenType.TT_2SYM && token.value != null && token.value.length() == 1
				&& token.value.charAt(0) == symbol) {

			return true;
		}

		return false;
	}

	static <T> void _set(ArrayList<T> array, int index, T v) {

		if (index < array.size()) {
			array.set(index, v);

		} else {

			while (index > array.size()) {
				array.add(null);
			}

			array.add(v);
		}
	}

	private int lineIndex = 0;

	private int linePos = 0;

	private ArrayList<String> lines = new ArrayList<>();

	private int operationCount = 0;

	private Map<String, String> prefixNameSpaceMap = new HashMap<>();

	private int stackDepth = 0;

	private boolean supportComment = true;

	private boolean supportNumber = true;

	private int tokenCount = 0;

	private IRTokener tokener;

	private ArrayList<Integer> tokenIndexs = new ArrayList<>();

	private ArrayList<Token> tokenList = new ArrayList<>();

	public XRParser(IRTokener tokener) {
		super();
		this.tokener = tokener;
	}

	private void _checkRecursion() throws RException {

		if (operationCount++ >= MAX_PARSE_COUNT) {
			throw new RException();
		}
	}

	private Token _curToken() throws RException {

		if (_tokenPos() >= tokenCount)
			return null;

		Token token = tokenList.get(_tokenPos());
		linePos = token.endPos;
		return token;
	}

	private int _depth() throws RException {

		if (stackDepth >= MAX_STACK_DEPTH) {
			throw new RException(String.format("[%d, %d]: out of Stack", lineIndex, linePos));
		}

		return stackDepth;
	}

	private String _getDoubleNumber(String name) {

		int len = name.length();

		if (len < 2) {
			return null;
		}

		char c = name.charAt(len - 1);
		if (c != 'D' && c != 'd') {
			return null;
		}

		int pointNum = 0;

		for (int pos = len - 2; pos >= 0; --pos) {

			c = name.charAt(pos);
			if (c < '0' || c > '9') {

				if (c == '.') {
					if (++pointNum > 1) {
						return null;
					}
				} else {
					return null;
				}
			}
		}

		return name.substring(0, len - 1);
	}

	private String _getLongNumber(String name) {

		int len = name.length();

		if (len < 2) {
			return null;
		}

		char c = name.charAt(len - 1);
		if (c != 'L' && c != 'l') {
			return null;
		}

		for (int pos = len - 2; pos >= 0; --pos) {

			c = name.charAt(pos);
			if (c < '0' || c > '9') {
				return null;
			}
		}

		return name.substring(0, len - 1);
	}

	private RName _getRName(String name) {

		int pos = name.lastIndexOf(':');
		if (pos == -1) {
			return null;
		}

		String prefix = name.substring(0, pos);
		String nameSpace = prefixNameSpaceMap.get(prefix);
		if (nameSpace == null) {
			return null;
		}

		String subName = name.substring(pos + 1);
		return new RName(nameSpace, prefix, subName, nameSpace + subName);
	}

	private boolean _ignoreBlank() throws RException {

		_checkRecursion();

		boolean ignoreComment = false;

		NEXT: while (_more()) {

			Token token = _curToken();

			if (ignoreComment) {

				// ignore end tokens
				if (_isEndToken(token)) {
					this.lineIndex++;
					ignoreComment = false;
				}

				_pushStack(1);
				continue NEXT;

			} else {

				// ignore blank tokens
				if (_isBlankToken(token)) {
					_pushStack(1);
					continue NEXT;
				}

				// ignore end tokens
				if (_isEndToken(token)) {
					this.lineIndex++;
					_pushStack(1);
					continue NEXT;
				}

				if (isSupportComment()) {

					// find symbol ";"
					if (token.type == TokenType.TT_2SYM && token.value != null && token.value.equals(";")) {
						_pushStack(1);
						ignoreComment = true;
						continue NEXT;
					}
				}
			}

			break;
		}

		return true;
	}

	private void _init() {

		this.operationCount = 0;
		this.stackDepth = 0;
		this.tokenCount = 0;
		this.lineIndex = 0;
		this.linePos = 0;

		this.lines.clear();
		this.tokenList.clear();
		this.tokenIndexs.clear();
		this.tokenIndexs.add(0);
	}

	private boolean _more() throws RException {
		return _tokenPos() < tokenCount;
	}

	private IRObject _nextObject() throws RException {

		IRObject obj = nextObject();

		/******************************************/
		// Try match Array: []
		/******************************************/
		if (obj != null) {
			List<IRObject> list = matchAttrList();
			if (list != null) {
				for (IRObject attr : list) {
					AttrUtil.addAttribute(obj, attr.asString());
				}
			}
		}

		return obj;
	}

	private void _pullStack(int newDepth) throws RException {

		if (newDepth < 0 || newDepth > _depth())
			throw new RException();

		this.stackDepth = newDepth;
	}

	private void _pushStack(int addTokenCount) throws RException {

		if (stackDepth >= (MAX_STACK_DEPTH - 1) || addTokenCount < 0)
			throw new RException();

		int pos = _tokenPos() + addTokenCount;
		if (pos >= MAX_TOKEN_COUNT) {
			throw new RException(String.format("[%d, %d]: buffer overflow", lineIndex, linePos));
		}

		_set(tokenIndexs, ++stackDepth, pos);
	}

	private int _tokenPos() throws RException {
		return tokenIndexs.get(_depth());
	}

	@Override
	public IRTokener getTokener() {
		return tokener;
	}

	public boolean isSupportComment() {
		return supportComment;
	}

	public boolean isSupportNumber() {
		return supportNumber;
	}

	private List<IRObject> matchArray() throws RException {

		_checkRecursion();

		if (!_more()) {
			return null;
		}

		// save depth of option
		int depth = _depth();

		if (!matchSymbol('{')) {
			_pullStack(depth);
			return null;
		}

		_ignoreBlank();

		/* save depth of option */
		int depth2 = _depth();

		if (matchSymbol('}')) {
			return Collections.emptyList();
		} else {
			_pullStack(depth2);
		}

		ArrayList<IRObject> arrayList = new ArrayList<>();
		ArrayList<IRObject> curElements = new ArrayList<>();

		while (_ignoreBlank()) {

			if (matchSymbol(',')) {

				if (curElements.isEmpty()) {
					arrayList.add(O_Nil);
				} else if (curElements.size() == 1) {
					arrayList.add(curElements.get(0));
				} else {
					arrayList.add(RulpFactory.createList(curElements));
				}

				curElements = new ArrayList<>();
				continue;
			}

			if (matchSymbol('}')) {

				if (curElements.size() == 1) {
					arrayList.add(curElements.get(0));
				} else if (curElements.size() > 1) {
					arrayList.add(RulpFactory.createList(curElements));
				}

				return arrayList;
			}

			IRObject nextObj = _nextObject();
			if (nextObj == null) {
				break; // invalid
			}

			curElements.add(nextObj);
		}

		_pullStack(depth);
		return null;
	}

	private List<IRObject> matchAttrList() throws RException {

		_checkRecursion();

		if (!_more()) {
			return null;
		}

		// save depth of option
		int depth = _depth();

		if (!matchSymbol('[')) {
			_pullStack(depth);
			return null;
		}

		_ignoreBlank();

		/* save depth of option */
		int depth2 = _depth();

		if (matchSymbol(']')) {
			return null;
		} else {
			_pullStack(depth2);
		}

		ArrayList<IRObject> list = new ArrayList<>();
		IRObject obj = null;
		while (_ignoreBlank() && (obj = nextObject()) != null) {
			list.add(obj);
		}

		if (_ignoreBlank() && matchSymbol(']')) {
			return list;
		}

		_pullStack(depth);
		return null;
	}

	private List<IRObject> matchList() throws RException {

		_checkRecursion();

		if (!_more()) {
			return null;
		}

		// save depth of option
		int depth = _depth();

		if (!matchSymbol('(')) {
			_pullStack(depth);
			return null;
		}

		_ignoreBlank();

		/* save depth of option */
		int depth2 = _depth();

		if (matchSymbol(')')) {
			return Collections.emptyList();
		} else {
			_pullStack(depth2);
		}

		ArrayList<IRObject> list = new ArrayList<>();
		IRObject obj = null;
		while (_ignoreBlank() && (obj = _nextObject()) != null) {
			list.add(obj);
		}

		if (_ignoreBlank() && matchSymbol(')')) {
			return list;
		}

		_pullStack(depth);
		return null;
	}

	private boolean matchSymbol(char symbol) throws RException {

		_checkRecursion();

		if (_isSymbolToken(_curToken(), symbol)) {

			/* output symbol */
			_pushStack(1);
			return true;
		}

		return false;
	}

	private String nextAtom() throws RException {

		_checkRecursion();

		/* save depth of option */
		int depth = _depth();
		Token token = null;

		if ((token = _curToken()) != null && _isSupportIdentifierHeadToken(token)) {

			_pushStack(1);

			String atomName = token.value;
			while (_more() && (token = _curToken()) != null && token.value != null && token.value.length() > 0
					&& !_isSeparatorToken(token)) {
				atomName += token.value;
				_pushStack(1);
			}

			return atomName;
		}

		_pullStack(depth);
		return null;
	}

	private IRObject nextObject() throws RException {

		_checkRecursion();

		if (!_more()) {
			return null;
		}

		int depth = _depth();

		Token token = _curToken();
		_pushStack(1);
		switch (token.type) {
		case TT_2SYM:
			switch (token.value) {
			case ",":
				return RulpFactory.createAtom(token.value);
			default:
			}
		default:
		}

		Token next = _curToken();

		/******************************************/
		// Combine Symbols
		/******************************************/
		if (!_isSeparatorToken(next)) {

			switch (token.type) {

			case TT_2SYM:

				switch (token.value) {

				case "&":
					/******************************************/
					// Try match var: &abc
					/******************************************/
					String atomName = nextAtom();
					if (atomName != null) {
						return RulpFactory.createVar(atomName, null);
					}
					break;

				case "+":
				case "-":

					/******************************************/
					// Try match (+/-)number
					/******************************************/
					if (this.isSupportNumber()) {

						_pushStack(1);

						if (!_isSeparatorToken(next)) {

							switch (next.type) {

							// -123 or +123
							case TT_5INT:

								try {
									return RulpFactory.createInteger(Integer.valueOf(token.value + next.value));
								} catch (NumberFormatException e) {
									throw new RException(String.format("[%d, %d]: invalid int format, %s", lineIndex,
											linePos, token.value + next.value));
								}

								// -1.5 or +1.5
							case TT_6FLT:

								Token next2 = _curToken();

								// +1.1d
								if (_isNameToken(next2, "d") || _isNameToken(next2, "D")) {

									_pushStack(1);

									try {
										return RulpFactory.createDouble(Double.valueOf(token.value + next.value));
									} catch (NumberFormatException e) {
										throw new RException(String.format("[%d, %d]: invalid double format, %s",
												lineIndex, linePos, token.value, next2.value));
									}
								}

								return RulpFactory.createFloat(Float.valueOf(token.value + next.value));

							default:

								String longNum = _getLongNumber(next.value);
								if (longNum != null) {
									try {
										return RulpFactory.createLong(Long.valueOf(token.value + longNum));
									} catch (NumberFormatException e) {
										throw new RException(String.format("[%d, %d]: invalid long format, %s",
												lineIndex, linePos, token.value + next.value));
									}
								}

								String doubleNum = _getDoubleNumber(next.value);
								if (doubleNum != null) {
									try {
										return RulpFactory.createDouble(Double.valueOf(token.value + doubleNum));
									} catch (NumberFormatException e) {
										throw new RException(String.format("[%d, %d]: invalid double format, %s",
												lineIndex, linePos, token.value + next.value));
									}
								}
							}
						}

					}

					break; // break switch cur_token value

				default: // other symbol

				} // end of switch cur_token value
				break;

			default:

			} // switch cur_token type

		}
		// non Combine Symbols
		else {

			switch (token.type) {
			/******************************************/
			// Try match integer
			/******************************************/
			case TT_5INT:

				if (this.isSupportNumber()) {

					try {
						return RulpFactory.createInteger(Integer.valueOf(token.value));
					} catch (NumberFormatException e) {
						throw new RException(
								String.format("[%d, %d]: invalid int format, %s", lineIndex, linePos, token.value));
					}
				}

				break;

			/******************************************/
			// Try match float
			/******************************************/
			case TT_6FLT:

				if (this.isSupportNumber()) {
					return RulpFactory.createFloat(Float.valueOf(token.value));
				}

				break;

			/******************************************/
			// Try match string: "abc"
			/******************************************/
			case TT_4STR:
				String value = token.value;
				value = value.substring(1, value.length() - 1);
				return RulpFactory.createString(value);

			default:
			}
		}

		/******************************************/
		// Try match hex: 0x1A or 0X001
		/******************************************/
		if (token.type == TokenType.TT_3NAM) {

			String value = token.value;
			int len = value.length();

			if (len >= 3 && (value.startsWith("0x") || value.startsWith("0X"))) {

				boolean isHEX = true;
				int num = 0;

				for (int i = 2; isHEX && i < len; ++i) {

					char c = value.charAt(i);

					int n = 0;
					if (c >= '0' && c <= '9') {
						n = c - '0';
					} else if (c >= 'a' && c <= 'f') {
						n = c - 'a' + 10;
					} else if (c >= 'A' && c <= 'F') {
						n = c - 'A' + 10;
					} else {
						isHEX = false;
					}

					num = num * 16 + n;
				}

				if (isHEX) {
					return RulpFactory.createInteger(num);
				} else {
					throw new RException(String.format("[%d, %d]: invalid hex, %s", lineIndex, linePos, token.value));
				}
			}
		}

		/******************************************/
		// Try match double: float d
		/******************************************/
		if (token.type == TokenType.TT_6FLT && (_isNameToken(next, "d") || _isNameToken(next, "D"))) {

			_pushStack(1);

			try {
				return RulpFactory.createDouble(Double.valueOf(token.value));
			} catch (NumberFormatException e) {
				throw new RException(
						String.format("[%d, %d]: invalid double format, %s", lineIndex, linePos, token.value));
			}
		}

		/******************************************/
		// Try match List: '()
		/******************************************/
		_pullStack(depth);
		if (matchSymbol('\'') && _ignoreBlank()) {
			List<IRObject> list = matchList();
			if (list != null) {
				if (list.isEmpty()) {
					return RulpFactory.emptyConstList();
				} else {
					return RulpFactory.createList(list);
				}
			}
		}

		/******************************************/
		// Try match List: $()
		/******************************************/
		_pullStack(depth);
		if (matchSymbol('$') && _ignoreBlank()) {
			List<IRObject> list = matchList();
			if (list != null) {
				if (list.isEmpty()) {
					return RulpFactory.createExpression();
				} else {
					return RulpFactory.createExpressionEarly(list);
				}
			}
		}

		/******************************************/
		// Try match Expression: ()
		/******************************************/
		_pullStack(depth);
		{
			List<IRObject> list = matchList();
			if (list != null) {
				if (list.isEmpty()) {
					return RulpFactory.createExpression();
				} else {
					return RulpFactory.createExpression(list);
				}
			}
		}

		/******************************************/
		// Try match Array: {}
		/******************************************/
		_pullStack(depth);
		{
			List<IRObject> list = matchArray();
			if (list != null) {
				return RulpFactory.createConstArray(list);
			}
		}

		/******************************************/
		// Try match atom: abc
		/******************************************/
		_pullStack(depth);
		{
			String atomName = nextAtom();

			if (atomName != null) {

				switch (atomName) {
				case A_TRUE:
					return O_True;

				case A_FALSE:
					return O_False;

				default:

					// name:'(list)
					if (atomName.endsWith(":'")) {

						int namedDepth = _depth();
						List<IRObject> list = matchList();
						if (list != null) {
							String name = atomName.substring(0, atomName.length() - 2).trim();
							if (name.length() == 0) {
								name = null;
							}

							return RulpUtil.toList(name, list);

						}

						_pullStack(namedDepth);
					}

					// class::member
					int mPos = atomName.indexOf(F_O_MBR);
					if (mPos > 0 && mPos < (atomName.length() - 2)) {

						List<String> names = StringUtil.splitStringByStr(atomName, F_O_MBR);
						if (names.size() == 2) {
							return RulpFactory.createMember(RulpFactory.createAtom(names.get(0)), names.get(1), null);
						}
						// a::b::c ==>(:: (:: a b) c)
						else {

							int mbrSize = names.size();
							IRAtom getMbr = RulpFactory.createAtom(F_O_MBR);
							IRExpr mbrExpr = RulpFactory.createExpression(getMbr, RulpFactory.createAtom(names.get(0)),
									RulpFactory.createAtom(names.get(1)));
							for (int i = 2; i < mbrSize; ++i) {
								mbrExpr = RulpFactory.createExpression(getMbr, mbrExpr,
										RulpFactory.createAtom(names.get(i)));
							}

							return mbrExpr;
						}
					}

					// pre:name
					RName rName = _getRName(atomName);
					if (rName != null) {
						return RulpFactory.createAtom(rName);
					}

					// 100L
					String longNum = _getLongNumber(atomName);
					if (longNum != null) {
						try {
							return RulpFactory.createLong(Long.valueOf(longNum));
						} catch (NumberFormatException e) {
							throw new RException(
									String.format("[%d, %d]: invalid long format, %s", lineIndex, linePos, atomName));
						}
					}

					// 0.11D
					String doubleNum = _getDoubleNumber(atomName);
					if (doubleNum != null) {
						try {
							return RulpFactory.createDouble(Double.valueOf(doubleNum));
						} catch (NumberFormatException e) {
							throw new RException(
									String.format("[%d, %d]: invalid double format, %s", lineIndex, linePos, atomName));
						}
					}

					return RulpFactory.createAtom(atomName);
				}

			}
		}

		/******************************************/
		// Try match operator
		/******************************************/
		_pullStack(depth);
		String sym = "";
		while (_more() && (token = _curToken()) != null && !_isSeparatorToken(token)) {
			_pushStack(1);
			sym += token.value;
		}

		if (!sym.isEmpty()) {
			return RulpFactory.createAtom(sym);
		}

		_pullStack(depth);
		return null;
	}

	@Override
	public List<IRObject> parse(String inputLine) throws RException {

		/****************************************************/
		// Main routine
		/****************************************************/
		if (inputLine == null) {
			throw new RException("Empty Input");
		}

		this._init();

		/****************************************************/
		// Scan all tokens
		/****************************************************/
		int parseLineindex = 0;
		for (String newLine : StringUtil.splitStringByChar(inputLine, '\n')) {

			if (newLine.trim().isEmpty()) {
				continue;
			}

			tokener.setContent(newLine);
			Token token = null;
			boolean ignoreHeadSpace = true;

			while ((token = tokener.next()) != null) {

				token.lineIndex = parseLineindex;

				if (token.type == TokenType.TT_0BAD) {
					throw new RException(String.format("Bad token: %s", token.toString()));
				}

				/********************************/
				// Skip space in the head
				/********************************/
				{
					if (ignoreHeadSpace && _isBlankToken(token)) {
						continue;
					}
					ignoreHeadSpace = false;
				}

				if (tokenCount >= MAX_TOKEN_COUNT) {
					throw new RException(String.format("Too many token at line: %d", parseLineindex));
				}

				_set(tokenList, tokenCount++, token);
			}

			/****************************************************/
			// Remove tail space
			/****************************************************/
			while (tokenCount > 0 && _isBlankToken(tokenList.get(tokenCount - 1))) {
				--tokenCount;
			}

			_set(tokenList, tokenCount++, END_TOKEN);
			lines.add(newLine);
			++parseLineindex;
		}

		if (tokenCount == 0) {
			return Collections.<IRObject>emptyList();
		}

		/****************************************************/
		// Match rules
		/****************************************************/
		ArrayList<IRObject> list = new ArrayList<>();

		while (_ignoreBlank() && _more()) {

			IRObject obj = _nextObject();

			if (obj == null) {

				Token lastToken = this._curToken();
				int lastLineIndex = lastToken == null ? -1 : lastToken.lineIndex;
				String lastLine = lastLineIndex == -1 ? null : lines.get(lastLineIndex);

				if (_isSymbolToken(lastToken, '(')) {
					throw new RulpIncompleteException(lastLineIndex,
							String.format("miss match '(' found in position %d, %s", lastToken.endPos - 1,
									lastLine.substring(lastToken.endPos - 1)));
				} else {
					throw new RParseException(lastLineIndex, String.format("token=%s, line=%s", lastToken, lastLine));
				}
			}

			list.add(obj);

			// Clean stack
			int curTokenPos = _tokenPos();
			this.tokenIndexs.clear();
			this.tokenIndexs.add(curTokenPos);
			this.stackDepth = 0;
		}

		return list;
	}

	@Override
	public void registerPrefix(String prefix, String nameSpace) {
		prefixNameSpaceMap.put(prefix, nameSpace);
	}

	@Override
	public void setSupportComment(boolean supportComment) {
		this.supportComment = supportComment;
	}

	@Override
	public void setSupportNumber(boolean supportNumber) {
		this.supportNumber = supportNumber;
	}

}
