/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import alpha.rulp.lang.RException;

public class StringUtil {

	public enum CharType {
		CN_CHAR, CN_SYMBOL, EN_CHAR, EN_SYMBOL, JP_SYMBOL, NUMBER, OTHER, ROMAN_NUM, TW_ZHUYIN
	}

	static interface IMatchParser {
		public boolean match(String content, ArrayList<String> values, boolean ignoreCase);
	}

	static class StringMatchUtility {

		static interface IItemParser {
			public XItem parse(String content, int contentOff, int contentLen, boolean ignoreCase);

		}

		static class XItem {

			public String content = null;

			public int itemLen;

			public int itemOff;

			public String getConent() {

				if (content != null && itemOff >= 0 && itemLen > 0) {
					return content.substring(itemOff, itemOff + itemLen);
				}

				return null;
			}

		}

		private static class XMatchParser implements IMatchParser {

			// private String _toString = null;

			// private

			private IItemParser[] parsers;

			private void addItemParser(ArrayList<IItemParser> parseList, StringBuffer parseBuffer, int parseIndex,
					IItemParser parser) throws RException {

				IItemParser lastParser = parseList.isEmpty() ? null : parseList.get(parseList.size() - 1);

				if (parseBuffer.length() != 0) {
					lastParser = new XPlainStringItermParser(parseBuffer.toString());
					parseList.add(lastParser);
					parseBuffer.setLength(0);

				} else {

					if (parser == anyParser && (lastParser == anyParser || lastParser == varParser)) {
						throw new RException("next var should not be %? or |, i= " + parseIndex);
					}
				}

				// Ignore [][]
				if (parser == lastParser && lastParser == spaceParse) {
					return;
				}

				parseList.add(parser);
			}

			public void buildParser(String matchMode) throws RException {

				StringBuffer parseBuffer = new StringBuffer();
				ArrayList<IItemParser> parseList = new ArrayList<>();

				int parseIndex = 0;

				for (parseIndex = 0; parseIndex < matchMode.length(); ++parseIndex) {
					char c = matchMode.charAt(parseIndex);

					switch (c) {
					case ' ':

						addItemParser(parseList, parseBuffer, parseIndex, spaceParse);
						break;
					case '*': {

						addItemParser(parseList, parseBuffer, parseIndex, anyParser);
						break;
					}
					case '|': {

						addItemParser(parseList, parseBuffer, parseIndex, new XOrParser());
						break;
					}
					case '%':

						if (parseIndex < matchMode.length() - 1) {

							char c2 = matchMode.charAt(++parseIndex);

							switch (c2) {
							// %% means char'%'
							case '%':
								parseBuffer.append('%');
								break;
							// %d means a integer
							case 'd':

								addItemParser(parseList, parseBuffer, parseIndex, intParse);
								break;
							case '|':
								parseBuffer.append('|');
								break;
							case '?':

								addItemParser(parseList, parseBuffer, parseIndex, varParser);
								break;

							case '*':
								parseBuffer.append("*");
								break;

							case 'n':
								addItemParser(parseList, parseBuffer, parseIndex, new XSpecialCharItermParser('\n'));
								break;

							case 'r':
								addItemParser(parseList, parseBuffer, parseIndex, new XSpecialCharItermParser('\r'));
								break;

							case '$':
								addItemParser(parseList, parseBuffer, parseIndex, endParser);
								break;

							default:
								throw new RException("not support %" + c2);
							}

						} else {
							throw new RException("should not be the last %");
						}

						break;
					case 0:
					case '\n':
					case '\r': {
						throw new RException("not support char:" + c);
					}
					default:

						parseBuffer.append(c);
						// if (c >= 32 && c < 128) {
						// sb.append(c);
						// } else {
						// throw new RException("not support char:" +
						// c);
						// }
					}
				}

				if (parseBuffer.length() != 0) {
					parseList.add(new XPlainStringItermParser(parseBuffer.toString()));
					parseBuffer.setLength(0);
				}

				parsers = new IItemParser[parseList.size()];
				for (int i = 0; i < parseList.size(); ++i) {
					parsers[i] = parseList.get(i);
				}
			}

			@Override
			public boolean match(String content, ArrayList<String> values, boolean ignoreCase) {

				int contentOff = 0;
				int contentLen = content.length();

				int parseIndex = 0;
				boolean finished = false;
				if (values != null) {
					values.clear();
				} else {
					values = new ArrayList<>();
				}

				final int parserCount = parsers.length;

				while (!finished && contentOff < contentLen && parseIndex < parserCount) {

					IItemParser curParser = parsers[parseIndex++];

					if (curParser == anyParser || curParser == varParser) {

						// any or var parse is last var
						if (parseIndex == parserCount
								|| ((parseIndex + 1) == parserCount) && parsers[parseIndex] == endParser) {

							XItem item = curParser.parse(content, contentOff, content.length(), ignoreCase);

							if (curParser == varParser) {

								if (item != null) {
									values.add(item.getConent());
								} else {
									values.add(null);
								}
							}

							finished = true;

						} else {

							IItemParser nextParse = parsers[parseIndex++];
							XItem nextItem = tryParse(content, contentOff, nextParse, ignoreCase);

							if (nextItem == null) {
								return false;
							}

							XItem thisItem = curParser.parse(content, contentOff, nextItem.itemOff, ignoreCase);

							if (curParser == varParser) {
								if (thisItem != null) {
									values.add(thisItem.getConent());
								} else {
									values.add(null);
								}
							}

							contentOff = nextItem.itemOff + nextItem.itemLen;
						}

					}
					// if (curParse == anyParser || curParse == varParser) {
					else {

						XItem item = curParser.parse(content, contentOff, content.length(), ignoreCase);
						if (item == null) {
							return false;
						}

						contentOff = item.itemOff + item.itemLen;
					}
				}

				if (parseIndex < parserCount) {

					// Not last one
					if (parseIndex != parserCount - 1) {
						return false;
					}

					IItemParser lastParer = parsers[parseIndex];

					// the left parse is *
					if (lastParer == anyParser || lastParer == endParser) {
						// nothing
					} else {
						return false;
					}

				}

				return true;
			}

			@Override
			public String toString() {

				String _toString = "";
				for (IItemParser p : parsers) {
					_toString += p.toString();
				}

				return _toString;

			}

			private XItem tryParse(String content, int off, IItemParser parser, boolean ignoreCase) {

				XItem item = null;
				while (item == null && off < content.length()) {
					item = parser.parse(content, off++, content.length(), ignoreCase);
				}

				return item;
			}

		}

		private static class XOrParser implements IItemParser {

			private IItemParser leftParser = null;

			private IItemParser rigthParser = null;

			@Override
			public XItem parse(String content, int contentOff, int contentLen, boolean ignoreCase) {
				return null;
			}

			@Override
			public String toString() {
				return null;
			}
		}

		private static class XPlainStringItermParser implements IItemParser {

			public String plainString = null;

			private XPlainStringItermParser(String plainString) {
				super();
				this.plainString = plainString;
			}

			@Override
			public XItem parse(String content, int contentOff, int contentLen, boolean ignoreCase) {

				if ((plainString.length() + contentOff) > contentLen) {
					return null;
				}

				for (int i = 0; i < plainString.length(); ++i) {

					char expectChar = plainString.charAt(i);
					char testChar = content.charAt(contentOff + i);

					if (ignoreCase) {
						expectChar = Character.toLowerCase(expectChar);
						testChar = Character.toLowerCase(testChar);
					}

					if (expectChar != testChar) {
						return null;
					}
				}

				XItem item = new XItem();

				item.content = content;
				item.itemOff = contentOff;
				item.itemLen = plainString.length();
				return item;
			}

			@Override
			public String toString() {

				if (plainString.length() == 1) {

					char singleChar = plainString.charAt(0);

					switch (singleChar) {
					case '|':
					case '%':
					case '*':
						return "%" + singleChar;
					}
				}

				return plainString;
			}

		}

		private static class XSpecialCharItermParser implements IItemParser {

			public char expectChar = 0;

			private XSpecialCharItermParser(char expectChar) {
				super();
				this.expectChar = expectChar;
			}

			@Override
			public XItem parse(String content, int contentOff, int contentLen, boolean ignoreCase) {

				if ((1 + contentOff) > contentLen) {
					return null;
				}

				char testChar = content.charAt(contentOff);
				if (expectChar != testChar) {
					return null;
				}

				XItem item = new XItem();

				item.content = content;
				item.itemOff = contentOff;
				item.itemLen = 1;
				return item;
			}

			@Override
			public String toString() {

				switch (expectChar) {
				case '\n':
					return "%n";
				case '\r':
					return "%r";
				}

				return "" + expectChar;
			}

		}

		private static IItemParser anyParser = new IItemParser() {

			@Override
			public XItem parse(String content, int contentOff, int contentLen, boolean ignoreCase) {
				return null;
			}

			@Override
			public String toString() {
				return "*";
			}
		};

		private static IItemParser endParser = new IItemParser() {

			@Override
			public XItem parse(String content, int contentOff, int contentLen, boolean ignoreCase) {
				for (int i = contentOff; i < contentLen; ++i) {
					switch (content.charAt(i)) {
					case '\r':
					case '\n':
						continue;
					default:
						return null;
					}
				}

				XItem item = new XItem();
				item.content = content;
				item.itemOff = contentOff;
				item.itemLen = contentLen - contentOff;
				return item;
			}

			@Override
			public String toString() {
				return "%$";
			}
		};

		private static IItemParser intParse = new IItemParser() {

			@Override
			public XItem parse(String content, int contentOff, int contentLen, boolean ignoreCase) {
				XItem item = new XItem();

				boolean findNonIntChar = false;
				item.content = content;
				item.itemOff = contentOff;
				int maxItemLen = contentLen - contentOff;

				item.itemLen = 0;
				while (!findNonIntChar && item.itemLen < maxItemLen) {

					char c = item.content.charAt(item.itemLen + contentOff);

					if (Character.isDigit(c)) {
						item.itemLen++;
					} else {
						findNonIntChar = true;
					}

				}
				if (item.itemLen > 0) {
					return item;
				}

				return null;
			}

			@Override
			public String toString() {

				return "%d";
			}
		};;

		private static Map<String, XMatchParser> modeMap = new HashMap<String, XMatchParser>();

		private static IItemParser spaceParse = new IItemParser() {

			@Override
			public XItem parse(String content, int contentOff, int contentLen, boolean ignoreCase) {
				XItem item = new XItem();

				item.content = content;
				item.itemOff = contentOff;
				item.itemLen = 0;

				boolean findNonSpace = false;

				while (!findNonSpace && (item.itemOff + item.itemLen) < contentLen) {

					if (content.charAt(item.itemOff + item.itemLen) == ' ') {
						++item.itemLen;
					} else {
						findNonSpace = true;
					}

				}

				if (item.itemLen > 0) {
					return item;
				}

				return null;
			}

			@Override
			public String toString() {
				return " ";
			}

		};

		private static IItemParser varParser = new IItemParser() {

			@Override
			public XItem parse(String content, int contentOff, int contentLen, boolean ignoreCase) {

				XItem item = new XItem();
				item.content = content;
				item.itemOff = contentOff;
				item.itemLen = contentLen - contentOff;

				return item;
			}

			@Override
			public String toString() {

				return "%?";
			}
		};

		public static IMatchParser getMatchParser(String matchMode) throws RException {

			XMatchParser parser = null;
			synchronized (modeMap) {
				parser = modeMap.get(matchMode);
			}

			if (parser == null) {
				parser = new XMatchParser();
				parser.buildParser(matchMode);
			}

			synchronized (modeMap) {
				modeMap.put(matchMode, parser);
			}

			return parser;
		}

		private StringMatchUtility() {

		}

	}

	public static String addEscapeString(String str) {

		StringBuffer sb = null;
		int size = str.length();

		for (int i = 0; i < size; ++i) {

			char c = str.charAt(i);
			if (needEscape(c)) {
				if (sb == null) {
					sb = new StringBuffer();
					if (i > 0) {
						sb.append(str.substring(0, i));
					}
				}
				sb.append('\\');
			}

			if (sb != null) {
				sb.append(c);
			}
		}

		return sb == null ? str : sb.toString();
	}

	public static String getSingleMatchString(String mode, String content) throws RException {

		ArrayList<String> values = new ArrayList<>();
		IMatchParser sm = StringMatchUtility.getMatchParser(mode);
		if (!sm.match(content, values, false)) {
			return null;
		}

		if (values.size() != 1) {

			throw new RException("Parse[" + content + "] String Mode <" + mode + "> error, match vars should not be:"
					+ values.toString());
		}

		return values.get(0);
	}

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}

	public static boolean isEnglish(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	public static boolean isEnglishString(String word) {

		if (word == null || word.isEmpty()) {
			return false;
		}

		for (int i = 0; i < word.length(); ++i) {

			char c = word.charAt(i);
			if (!isEnglish(c)) {
				return false;
			}
		}

		return true;
	}

	public static boolean isEscapeChar(char c) {

		switch (c) {
		case '\\':
		case 'n':
		case 'r':
		case '"':
			return true;
		}

		return false;
	}

	public static boolean needEscape(char c) {

		switch (c) {
		case '\\':
		case '"':
			return true;
		}

		return false;
	}

	public static char toEcapeChar(char c) {

		switch (c) {
		case 'n':
			return '\n';

		case 'r':
			return '\r';

		default:
			return c;
		}
	}

	public static boolean isNumber(String word) {

		if (word == null || word.isEmpty()) {
			return false;
		}

		for (int i = 0; i < word.length(); ++i) {
			if (!Character.isDigit(word.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	public static boolean matchFormat(String matchMode, String content) {

		try {
			return StringMatchUtility.getMatchParser(matchMode).match(content, null, false);
		} catch (RException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean matchFormat(String matchMode, String content, ArrayList<String> values) {

		try {

			return StringMatchUtility.getMatchParser(matchMode).match(content, values, false);
		} catch (RException e) {
			e.printStackTrace();
			return false;
		}

	}

	public static String removeEscapeString(String str) {

		if (str == null) {
			return null;
		}

		StringBuffer sb = null;
		int size = str.length();

		NEXT: for (int i = 0; i < size; ++i) {

			char c = str.charAt(i);
			if (c == '\\' && (i + 1) < size) {
				char c2 = str.charAt(i + 1);
				switch (c2) {
				case 'n':
					c2 = '\n';
					break;

				case '\\':
					c2 = '\\';
					break;

				case '"':
					c2 = '"';
					break;

				default:

					if (sb != null) {
						sb.append(c);
						sb.append(c2);
					}

					++i;
					continue NEXT;
				}

				if (sb == null) {
					sb = new StringBuffer();
					if (i > 0) {
						sb.append(str.substring(0, i));
					}
				}

				sb.append(c2);
				++i;

			} else {

				if (sb != null) {
					sb.append(c);
				}
			}
		}

		return sb == null ? str : sb.toString();
	}

	public static String simplifyPath(String path) {

		if (path.trim().isEmpty()) {
			return "";
		}

		Stack<String> pathStack = new Stack<>();
		int i = 0;
		int size = path.length();
		char c;

		while (i < size) {

			while (i < size && File.separatorChar == (c = path.charAt(i))) {
				++i;
			}

			StringBuilder sb = new StringBuilder();
			while (i < size && File.separatorChar != (c = path.charAt(i))) {
				sb.append(c);
				++i;
			}

			String s = sb.toString();

			switch (s) {
			case "":
			case ".": // nothing to do
				break;
			case "..":
				if (!pathStack.isEmpty()) {
					pathStack.pop();
				}
				break;
			default:
				pathStack.push(s);
			}
		}

		String newPath = "";
		while (!pathStack.isEmpty()) {
			newPath = File.separatorChar + pathStack.pop() + newPath;
		}

		return newPath.isEmpty() ? "" + File.separatorChar : newPath;
	}

	public static List<String> splitStringByStr(String input, String sep) {

		if (input == null) {
			return Collections.<String>emptyList();
		}

		ArrayList<String> subStrs = new ArrayList<>();
		if (sep == null || sep.length() == 0 || input.isEmpty()) {
			subStrs.add(input);
			return subStrs;
		}

		String line = input;
		int sep_len = sep.length();

		while (!line.isEmpty()) {
			int mPos = line.indexOf(sep);
			if (mPos > 0) {
				subStrs.add(line.substring(0, mPos));
				line = line.substring(mPos + sep_len);
			} else {
				subStrs.add(line);
				line = "";
			}
		}

		return subStrs;
	}

	public static List<String> splitStringByChar(String input, char... s) {

		if (input == null) {
			return Collections.<String>emptyList();
		}

		ArrayList<String> subStrs = new ArrayList<>();
		if (s == null || s.length == 0) {
			subStrs.add(input);
			return subStrs;
		}

		int slen = s.length;
		StringBuffer sb = new StringBuffer();

		NEXT_C: for (char c : input.toCharArray()) {

			for (int i = 0; i < slen; ++i) {

				// find
				if (c == s[i]) {

					if (sb.length() > 0) {
						subStrs.add(sb.toString());
						sb.setLength(0);
					}

					continue NEXT_C;
				}
			}

			// Separator character not found
			sb.append(c);
		}

		if (sb.length() > 0) {
			subStrs.add(sb.toString());
		}

		return subStrs;
	}

	public static String toOneLine(List<String> lines) throws RException, IOException {

		StringBuffer sb = new StringBuffer();
		int index = 0;

		for (String line : lines) {
			if (index++ != 0) {
				sb.append('\n');
			}
			sb.append(line);
		}

		return sb.toString();
	}

	public static List<String> toValidArgList(String args[]) {

		ArrayList<String> validArgs = new ArrayList<>();
		for (int i = 0; i < args.length; ++i) {
			String arg = args[i];
			if (arg != null && !arg.trim().isEmpty()) {
				validArgs.add(arg.trim());
			}
		}

		return validArgs;
	}

	public static String trimHead(String line, char... s) {

		if (s == null || s.length == 0) {
			return line;
		}

		int slen = s.length;
		int pos = 0;

		NEXT: for (; pos < line.length(); ++pos) {

			char c = line.charAt(pos);
			for (int i = 0; i < slen; ++i) {
				// trim
				if (c == s[i]) {
					continue NEXT;
				}
			}

			// not found any character
			break;
		}

		return line.substring(pos);
	}

	public static String trimTail(String line, char... s) {

		if (s == null || s.length == 0) {
			return line;
		}

		int slen = s.length;
		int pos = line.length() - 1;

		NEXT: for (; pos >= 0; --pos) {

			char c = line.charAt(pos);
			for (int i = 0; i < slen; ++i) {
				// trim
				if (c == s[i]) {
					continue NEXT;
				}
			}

			// not found any character
			break;
		}

		return line.substring(0, pos + 1);
	}
}
