package alpha.rulp.string;

public class ChineseWord {

	static final String cnNum = "零一二三四五六七八九";

	// 通过中文数字字符，在该字符串中查找其对应的数值或其表示的数位
	// 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14
	// 零 一 二 三 四 五 六 七 八 九 十 百 千 万 亿
	static final String dic = "零一二三四五六七八九十百千万亿";

	// 反向的数字模板
	static final String model = "个十百千万十百千亿十百千";

	public static boolean isChineseNum(char c) {
		return dic.indexOf(c) != -1;
	}

	/**
	 * 解析中文数字字符串，获取它所表示的数值
	 * 
	 * @param numberZhString 中文数字字符串
	 * @return 中文数字字符串所代表的数值
	 */
	public static long parseLong(String numberZhString) {

		if (numberZhString == "") {
			return 0;
		}

		// 将传入的中文数字字符串倒置 eg.五千万零九百零三 -> 三零百九零万千五
		numberZhString = new StringBuffer(numberZhString).reverse().toString();

		// 声明一个数组，与反向的数字模板对应
		char[] resultChars = new char[12];

		// 将结果字符数组使用字符0初始化
		for (int i = 0; i < resultChars.length; i++) {
			resultChars[i] = '0';
		}

		// loc -- 用来访问resultChars的数组下标
		char lastChar = 0;
		int loc = 0;
		for (int i = 0; i < numberZhString.length(); i++) {

			// 依次取出numberZhString中的字符，如三，零，百，千
			lastChar = numberZhString.charAt(i);

			// 查找字符c所对应的数值或表示的数位
			// 如：三对应3，百对应11代表百位或百万位或百亿位
			int t = dic.indexOf(lastChar);

			// 如果字符c代表一个数值，将该数值转化为数字字符，并存入resultChars.
			if (t >= 1 && t <= 9) {
				resultChars[loc] = (char) (t + '0');
			}

			// 如果字符c代表一个数位，移动resultChars的数组下标j
			else if (t >= 10 && t <= 14) {
				loc = model.indexOf(lastChar, loc);
			}
			// 如果字符c代表0，什么也不做
			else if (t == 0) {

			}
			// 否则，抛出运行期异常
			else {
				throw new RuntimeException("中文数字字符串包含非法字符！只能包含以下字符：" + "零、一、二、三、四、五、六、七、八、九、十、百、千、万、亿");
			}
		}

		if (lastChar == '十' && resultChars[loc] == '0') {
			resultChars[loc] = '1';
		}

		// 获取resultChars的字符串形式
		String resultString = String.copyValueOf(resultChars);
		// 将该字符串倒置
		resultString = new StringBuffer(resultString).reverse().toString();

		// 将该字符串解析为long返回
		return Long.parseLong(resultString);
	}

	/**
	 * 返回长整数l的中文表示形式
	 */
	public static String toString(long l) {

		if (l == 0) {
			return "零";
		}

		String numberString = Long.toString(l);

		numberString = new StringBuffer(numberString).reverse().toString();

		char[] resultChars = new char[12];
		for (int i = 0; i < resultChars.length; i++) {
			resultChars[i] = '零';
		}

		for (int i = 0; i < numberString.length(); i++) {
			resultChars[i] = dic.charAt(numberString.charAt(i) - '0');
		}

		StringBuffer sb = new StringBuffer(20);

		for (int i = 0; i < numberString.length(); i++) {
			if (i == 4) {
				sb.append('万');
			}
			if (i == 8) {
				sb.append('亿');
			}
			if (resultChars[i] != '零') {
				if (i == 4 || i == 8) {
					sb.append(resultChars[i]);
				} else {
					sb.append("" + model.charAt(i) + resultChars[i]);
				}
			} else {
				if (sb.charAt(sb.length() - 1) != '零' && sb.charAt(sb.length() - 1) != '万'
						&& sb.charAt(sb.length() - 1) != '亿') {
					sb.append('零');
				}
			}
		}

		sb.deleteCharAt(0);
		sb.reverse();

		return sb.toString();
	}
}
