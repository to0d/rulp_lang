/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utils;

import static alpha.rulp.string.Constant.CN_CHAR_COLON;
import static alpha.rulp.string.Constant.CN_CHAR_COLON2;
import static alpha.rulp.string.Constant.CN_CHAR_COMMNA;
import static alpha.rulp.string.Constant.CN_CHAR_DUN_HAO;
import static alpha.rulp.string.Constant.CN_CHAR_EXCLAMATION;
import static alpha.rulp.string.Constant.CN_CHAR_LEFT_BRACKET;
import static alpha.rulp.string.Constant.CN_CHAR_LEFT_KUOHU;
import static alpha.rulp.string.Constant.CN_CHAR_LEFT_PARENTHESIS;
import static alpha.rulp.string.Constant.CN_CHAR_LEFT_PIE;
import static alpha.rulp.string.Constant.CN_CHAR_LEFT_QUOTE;
import static alpha.rulp.string.Constant.CN_CHAR_LEFT_SHU_MING;
import static alpha.rulp.string.Constant.CN_CHAR_LONG_HORIZONTAL_LINE;
import static alpha.rulp.string.Constant.CN_CHAR_LONG_HORIZONTAL_LINE2;
import static alpha.rulp.string.Constant.CN_CHAR_PERIOD;
import static alpha.rulp.string.Constant.CN_CHAR_QUESTION_MARK;
import static alpha.rulp.string.Constant.CN_CHAR_RIGHTT_KUOHU;
import static alpha.rulp.string.Constant.CN_CHAR_RIGHTT_PIE;
import static alpha.rulp.string.Constant.CN_CHAR_RIGHT_BRACKET;
import static alpha.rulp.string.Constant.CN_CHAR_RIGHT_PARENTHESIS;
import static alpha.rulp.string.Constant.CN_CHAR_RIGHT_QUOTE;
import static alpha.rulp.string.Constant.CN_CHAR_RIGHT_SHU_MIN;
import static alpha.rulp.string.Constant.CN_CHAR_SEMICOLON;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_1;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_10;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_11;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_12;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_13;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_14;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_15;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_16;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_17;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_18;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_19;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_2;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_21;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_22;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_23;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_24;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_25;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_26;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_27;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_28;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_29;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_30;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_31;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_32;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_4;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_5;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_6;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_7;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_8;
import static alpha.rulp.string.Constant.CN_CHAR_UNAME_9;
import static alpha.rulp.string.Constant.EN_SEPARATION_DOT;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import alpha.rulp.lang.RException;
import alpha.rulp.string.CharCaster;
import alpha.rulp.string.CharMatch;
import alpha.rulp.string.CharType;
import alpha.rulp.string.ChineseWord;
import alpha.rulp.string.StringMatchUtil;
import alpha.rulp.string.StringMatchUtil.IMatchParser;

public class StringUtil {

	public static String addEscape(String str) {

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

	public static char convertEscapeCharToReal(char c) {

		switch (c) {
		case 'n':
			return '\n';

		case 'r':
			return '\r';

		default:
			return c;
		}
	}

	public static String decodeByCharse(String charset, byte[] buffer, int offset, int len)
			throws UnsupportedEncodingException {
		return CharCaster.decodeByCharse(charset, buffer, offset, len);
	}

	public static CharType getCharType(char c) {

		switch (c) {
		case CN_CHAR_COMMNA: // CN ','
		case CN_CHAR_DUN_HAO:// CN '¡¢'
		case CN_CHAR_PERIOD: // '¡£':
		case CN_CHAR_QUESTION_MARK: // '£¿'
		case CN_CHAR_EXCLAMATION: // '£¡'
		case CN_CHAR_COLON:// CN ':'
		case CN_CHAR_COLON2: // CN '¡Ã'
		case CN_CHAR_LEFT_BRACKET: // CN '['
		case CN_CHAR_LEFT_PARENTHESIS:// CN '('
		case CN_CHAR_LEFT_QUOTE:// CN '¡°'
		case CN_CHAR_LEFT_SHU_MING:// CN '<'
		case CN_CHAR_RIGHT_BRACKET: // CN ']'
		case CN_CHAR_RIGHT_PARENTHESIS: // CN ')'
		case CN_CHAR_RIGHT_QUOTE:// CN '¡±'
		case CN_CHAR_RIGHT_SHU_MIN: // CN '>'
		case CN_CHAR_SEMICOLON:// CN ';'
		case CN_CHAR_LEFT_KUOHU: // CN '¡¸'
		case CN_CHAR_RIGHTT_KUOHU: // CN '¡¹'
		case CN_CHAR_LEFT_PIE: // CN '¡®'
		case CN_CHAR_RIGHTT_PIE: // CN '¡¯'
		case CN_CHAR_LONG_HORIZONTAL_LINE:// CN '©¥'
		case CN_CHAR_LONG_HORIZONTAL_LINE2:// CN '©¤'
		case CN_CHAR_UNAME_1:// CN '¡ð'
		case CN_CHAR_UNAME_2:// CN '¡Á'
		case CN_CHAR_UNAME_4:// CN '¡õ'
		case CN_CHAR_UNAME_5:// CN '~'
		case CN_CHAR_UNAME_6:// CN '¡ô'
		case CN_CHAR_UNAME_7:// CN '¡ñ'
		case CN_CHAR_UNAME_8:// CN '¡ö'
		case CN_CHAR_UNAME_9:// CN '¡æ'
		case CN_CHAR_UNAME_10:// CN '¡Ñ'
		case CN_CHAR_UNAME_11:// CN '¡ø'
		case CN_CHAR_UNAME_12:// CN '¨‹'
		case CN_CHAR_UNAME_13:// CN '¦¶'
		case CN_CHAR_UNAME_14:// CN '¡ï'
		case CN_CHAR_UNAME_15:// CN '§à'
		case CN_CHAR_UNAME_16:
		case CN_CHAR_UNAME_17:
		case CN_CHAR_UNAME_18:
		case CN_CHAR_UNAME_19:
		case CN_CHAR_UNAME_21:
		case CN_CHAR_UNAME_22:
		case CN_CHAR_UNAME_23:
		case CN_CHAR_UNAME_24:
		case CN_CHAR_UNAME_25:
		case CN_CHAR_UNAME_26:
		case CN_CHAR_UNAME_27:
		case CN_CHAR_UNAME_28:
		case CN_CHAR_UNAME_29:
		case CN_CHAR_UNAME_30:
		case CN_CHAR_UNAME_31:
		case CN_CHAR_UNAME_32:
			return CharType.CN_SYMBOL;

		case EN_SEPARATION_DOT: // '¡¤'
			return CharType.EN_SYMBOL;

		default:

			if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
				return CharType.EN_CHAR;
			}

			if (c >= '0' && c <= '9') {
				return CharType.NUMBER;
			}

			if ((c >= 0x21 && c <= 0x2f) || (c >= 0x3a && c <= 0x40) || (c >= 0x5b && c <= 0x60)
					|| (c >= 0x7b && c <= 0x7e)) {
				return CharType.EN_SYMBOL;
			}

			if (c >= 0x2160 && c <= 0x216b) {
				return CharType.ROMAN_NUM;
			}

			if (c >= 0x3105 && c <= 0x312d) {
				return CharType.TW_ZHUYIN;
			}

			if (c >= 0x3041 && c <= 0x3096) {
				return CharType.JP_SYMBOL;
			}

			Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
			if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
					|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
					|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
					|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
				return CharType.CN_CHAR;
			}
		}

		return CharType.OTHER;
	}

	public static int getFirstMatchIndex(String mode, ArrayList<String> result) {

		IMatchParser sm = null;
		try {
			sm = StringMatchUtil.getMatchParser(mode);
		} catch (RException e) {
			e.printStackTrace();
			return -1;
		}

		for (int matchIndex = 0; matchIndex < result.size(); ++matchIndex) {
			if (sm.match(result.get(matchIndex), null, false)) {
				return matchIndex;
			}
		}

		return -1;
	}

	public static IMatchParser getMatchParser(String matchMode) throws RException {
		return StringMatchUtil.getMatchParser(matchMode);
	}

	public static String getSingleMatchString(String mode, String content) throws RException {

		ArrayList<String> values = new ArrayList<>();
		IMatchParser sm = StringMatchUtil.getMatchParser(mode);
		if (!sm.match(content, values, false)) {
			return null;
		}

		if (values.size() != 1) {
			throw new RException("Parse[" + content + "] String Mode <" + mode + "> error, match vars should not be:"
					+ values.toString());
		}

		return values.get(0);
	}

	public static boolean hasChineseChar(String aString) {

		if (aString != null)

			for (char c : aString.toCharArray()) {
				if (isChinese(c) || isChineseSymbol(c)) {
					return true;
				}
			}

		return false;
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

	public static boolean isChineseNum(char c) {
		return ChineseWord.isChineseNum(c);
	}

	public static boolean isChineseSymbol(char c) {

		switch (c) {
		case CN_CHAR_COMMNA: // CN ','
		case CN_CHAR_DUN_HAO:// CN '¡¢'
		case CN_CHAR_PERIOD: // '¡£':
		case CN_CHAR_QUESTION_MARK: // '£¿'
		case CN_CHAR_EXCLAMATION: // '£¡'
		case CN_CHAR_COLON:// CN ':'
		case CN_CHAR_LEFT_BRACKET: // CN '['
		case CN_CHAR_LEFT_PARENTHESIS:// CN '('
		case CN_CHAR_LEFT_QUOTE:// CN '¡°'
		case CN_CHAR_LEFT_SHU_MING:// CN '<'
		case CN_CHAR_RIGHT_BRACKET: // CN ']'
		case CN_CHAR_RIGHT_PARENTHESIS: // CN ')'
		case CN_CHAR_RIGHT_QUOTE:// CN '¡±'
		case CN_CHAR_RIGHT_SHU_MIN: // CN '>'
		case CN_CHAR_SEMICOLON:// CN ';'
		case CN_CHAR_LEFT_KUOHU: // CN '¡¸'
		case CN_CHAR_RIGHTT_KUOHU: // CN '¡¹'
		case CN_CHAR_LEFT_PIE: // CN '¡®'
		case CN_CHAR_RIGHTT_PIE: // CN '¡¯'
		case CN_CHAR_LONG_HORIZONTAL_LINE: // CN '©¥'
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

	public static boolean isNumber(char c) {
		return Character.isDigit(c);
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
			return StringMatchUtil.getMatchParser(matchMode).match(content, null, false);
		} catch (RException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean matchFormat(String matchMode, String content, List<String> values) {

		try {

			return StringMatchUtil.getMatchParser(matchMode).match(content, values, false);
		} catch (RException e) {
			// e.printStackTrace();
			return false;
		}

	}

	public static boolean needEscape(char c) {

		switch (c) {
		case '\\':
		case '"':
			return true;
		}

		return false;
	}

	public static long parseChineseNumber(String numberZhString) {
		return ChineseWord.parseLong(numberZhString);
	}

	public static String removeEscape(String value) {

		if (value == null) {
			return null;
		}

		int size = value.length();
		StringBuffer sb = null;

		NEXT_CHAR: for (int i = 0; i < size; ++i) {

			char c = value.charAt(i);

			if ((i + 1) < size && c == '\\') {
				char c2 = value.charAt(i + 1);
				if (StringUtil.isEscapeChar(c2)) {

					c2 = StringUtil.convertEscapeCharToReal(c2);

					if (sb == null) {
						sb = new StringBuffer();
						if (i > 0) {
							sb.append(value.substring(0, i));
						}
					}

					sb.append(c2);
					++i;

				} else {

					if (sb != null) {
						sb.append(c);
						sb.append(c2);
					}

					++i;
					continue NEXT_CHAR;
				}

			} else {
				if (sb != null) {
					sb.append(c);
				}
			}
		}

		return sb == null ? value : sb.toString();
	}

	public static String simplifyLinuxPath(String path) {
		return simplifyPath(path, '/');
	}

	public static String simplifyPath(String path) {
		return simplifyPath(path, File.separatorChar);
	}

	public static String simplifyPath(String path, final char separator) {

		if (path.trim().isEmpty()) {
			return "";
		}

		Stack<String> pathStack = new Stack<>();
		int i = 0;
		int size = path.length();
		char c;

		while (i < size) {

			while (i < size && separator == (c = path.charAt(i))) {
				++i;
			}

			StringBuilder sb = new StringBuilder();
			while (i < size && separator != (c = path.charAt(i))) {
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
			newPath = separator + pathStack.pop() + newPath;
		}

		return newPath.isEmpty() ? "" + separator : newPath;
	}

	public static String smartSort(List<String> elements, boolean simpleMode) throws RException {
		return CharMatch.smartSort(elements, simpleMode);
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
