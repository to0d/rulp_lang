package alpha.rulp.string;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alpha.rulp.lang.RException;

public class StringMatchUtil {

	public static interface IMatchParser {
		public boolean match(String content, List<String> values, boolean ignoreCase);
	}

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
					// throw new AException("not support char:" +
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
		public boolean match(String content, List<String> values, boolean ignoreCase) {

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

	private StringMatchUtil() {

	}

}
