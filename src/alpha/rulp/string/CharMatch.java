package alpha.rulp.string;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import alpha.rulp.lang.RException;
import alpha.rulp.utils.StringUtil;

public class CharMatch {

	static interface CharMatcher extends Comparator<String> {

		public boolean isNum();

		public boolean match(char c);
	}

	static class ChineseCharMatcher implements CharMatcher {

		Collator cmp = Collator.getInstance(java.util.Locale.CHINA);

		@Override
		public int compare(String o1, String o2) {
			return cmp.compare(o1, o2);
		}

		@Override
		public boolean isNum() {
			return false;
		}

		@Override
		public boolean match(char c) {
			return StringUtil.isChinese(c);
		}

	}

	static class ChineseNumberCharMatcher implements CharMatcher {

		@Override
		public int compare(String o1, String o2) {

			long l1 = StringUtil.parseChineseNumber(o1);
			long l2 = StringUtil.parseChineseNumber(o2);

			return l1 == l2 ? 0 : (l1 > l2 ? 1 : -1);
		}

		@Override
		public boolean isNum() {
			return true;
		}

		@Override
		public boolean match(char c) {
			return StringUtil.isChineseNum(c);
		}

	}

	static class EnglishCharMatcher implements CharMatcher {

		@Override
		public int compare(String o1, String o2) {
			return o1.compareTo(o2);
		}

		@Override
		public boolean isNum() {
			return false;
		}

		@Override
		public boolean match(char c) {
			return StringUtil.isEnglish(c);
		}

	}

	static class IntegerCharMatcher implements CharMatcher {

		@Override
		public int compare(String o1, String o2) {
			return Integer.valueOf(o1) - Integer.valueOf(o2);
		}

		@Override
		public boolean isNum() {
			return true;
		}

		@Override
		public boolean match(char c) {
			return Character.isDigit(c);
		}

	}

	final static ArrayList<CharMatcher> matchers = new ArrayList<CharMatcher>();
	static {
		matchers.add(new IntegerCharMatcher());
		matchers.add(new ChineseNumberCharMatcher());
		matchers.add(new ChineseCharMatcher());
		matchers.add(new EnglishCharMatcher());
	}

	public static String smartSort(List<String> fileNames, boolean simpleMode) throws RException {

		int matchHeadPos = 0;

		boolean findDiff = false;

		while (!findDiff) {

			for (String n : fileNames) {
				if (n.length() <= matchHeadPos) {
					throw new RException(String.format("<%s> is too short", n));
				}
			}

			char commonChar = fileNames.get(0).charAt(matchHeadPos);
			for (String n : fileNames) {
				if (n.charAt(matchHeadPos) != commonChar) {
					findDiff = true;
					// System.out.println("Found diff: "
					// + n.charAt(matchHeadPos));
					break;
				}
			}

			if (!findDiff)
				++matchHeadPos;
		}

		if (!findDiff) {
			throw new RException("Diff not found");
		}

		String commonHead = fileNames.get(0).substring(0, matchHeadPos);
		CharMatcher foundMacher = null;

		if (!simpleMode) {

			for (String n : fileNames) {

				if (foundMacher == null) {

					char c = n.charAt(matchHeadPos);
					if (c == ' ') {
						continue;
					}

					for (CharMatcher m : matchers) {

						if (m.match(c)) {
							foundMacher = m;
							break;
						}
					}

					if (foundMacher == null) {
						throw new RException(String.format("Unknown mode<%s>", "" + c));
					}

				}

			}

			if (foundMacher == null) {
				throw new RException("Macher not found");

			} else {

				for (String n : fileNames) {

					char c = n.charAt(matchHeadPos);
					if (c != ' ' && !foundMacher.match(c)) {
						throw new RException(
								String.format("unmatch mode<%s:%s> %s ", foundMacher.getClass().getName(), "" + c, n));
					}
				}
			}

		}

		Map<String, String> tmpMap = new HashMap<String, String>();
		for (String n : fileNames) {
			String key = null;

			if (simpleMode || !foundMacher.isNum()) {

				key = n.substring(matchHeadPos).trim();

			} else {

				int bPos = matchHeadPos;
				int ePos = matchHeadPos;

				char c = n.charAt(matchHeadPos);
				if (c == ' ') {

					if (matchHeadPos == 0 || !foundMacher.match(n.charAt(matchHeadPos - 1))) {
						throw new RException(
								String.format("Unmatch %s for matcher", n, foundMacher.getClass().getName()));
					}

					bPos--;
					ePos--;
				}

				while (bPos > 0 && foundMacher.match(n.charAt(bPos - 1))) {
					--bPos;
				}

				while (ePos < n.length() - 1 && foundMacher.match(n.charAt(ePos + 1))) {
					++ePos;
				}

				key = n.substring(bPos, ePos + 1).trim();
			}

			if (key == null || key.isEmpty() || tmpMap.containsKey(key)) {
				throw new RException(String.format("Bad tmp key<%s> for %s", key, n));
			}

			tmpMap.put(key, n);
		}

		Vector<String> tempKey = new Vector<String>();
		tempKey.addAll(tmpMap.keySet());

		// for(String k: tempKey)
		// {
		// System.out.println(String.format("[%s]:[%s]", k,tmpMap.get(k)));
		// }

		if (foundMacher != null) {
			Collections.sort(tempKey, foundMacher);
		} else {
			Collections.sort(tempKey);
		}

		Vector<String> orderedFileNames = new Vector<String>();
		for (String k : tempKey) {
			orderedFileNames.add(tmpMap.get(k));
		}

		if (orderedFileNames.size() != fileNames.size()) {
			throw new RException("Bad size");
		}

		fileNames.clear();
		fileNames.addAll(orderedFileNames);

		return commonHead;
	}
}
