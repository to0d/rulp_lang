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
import java.util.List;
import java.util.Stack;

import alpha.rulp.lang.RException;
import alpha.rulp.utils.StringMatchUtil.IMatchParser;

public class StringUtil {

	public enum CharType {
		CN_CHAR, CN_SYMBOL, EN_CHAR, EN_SYMBOL, JP_SYMBOL, NUMBER, OTHER, ROMAN_NUM, TW_ZHUYIN
	}

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

	public static boolean matchFormat(String matchMode, String content, ArrayList<String> values) {

		try {

			return StringMatchUtil.getMatchParser(matchMode).match(content, values, false);
		} catch (RException e) {
			e.printStackTrace();
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
