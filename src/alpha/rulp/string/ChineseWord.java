package alpha.rulp.string;

public class ChineseWord {

	static final String cnNum = "��һ�����������߰˾�";

	// ͨ�����������ַ����ڸ��ַ����в������Ӧ����ֵ�����ʾ����λ
	// 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14
	// �� һ �� �� �� �� �� �� �� �� ʮ �� ǧ �� ��
	static final String dic = "��һ�����������߰˾�ʮ��ǧ����";

	// ���������ģ��
	static final String model = "��ʮ��ǧ��ʮ��ǧ��ʮ��ǧ";

	public static boolean isChineseNum(char c) {
		return dic.indexOf(c) != -1;
	}

	/**
	 * �������������ַ�������ȡ������ʾ����ֵ
	 * 
	 * @param numberZhString ���������ַ���
	 * @return ���������ַ������������ֵ
	 */
	public static long parseLong(String numberZhString) {

		if (numberZhString == "") {
			return 0;
		}

		// ����������������ַ������� eg.��ǧ����Ű����� -> ����پ�����ǧ��
		numberZhString = new StringBuffer(numberZhString).reverse().toString();

		// ����һ�����飬�뷴�������ģ���Ӧ
		char[] resultChars = new char[12];

		// ������ַ�����ʹ���ַ�0��ʼ��
		for (int i = 0; i < resultChars.length; i++) {
			resultChars[i] = '0';
		}

		// loc -- ��������resultChars�������±�
		char lastChar = 0;
		int loc = 0;
		for (int i = 0; i < numberZhString.length(); i++) {

			// ����ȡ��numberZhString�е��ַ����������㣬�٣�ǧ
			lastChar = numberZhString.charAt(i);

			// �����ַ�c����Ӧ����ֵ���ʾ����λ
			// �磺����Ӧ3���ٶ�Ӧ11�����λ�����λ�����λ
			int t = dic.indexOf(lastChar);

			// ����ַ�c����һ����ֵ��������ֵת��Ϊ�����ַ���������resultChars.
			if (t >= 1 && t <= 9) {
				resultChars[loc] = (char) (t + '0');
			}

			// ����ַ�c����һ����λ���ƶ�resultChars�������±�j
			else if (t >= 10 && t <= 14) {
				loc = model.indexOf(lastChar, loc);
			}
			// ����ַ�c����0��ʲôҲ����
			else if (t == 0) {

			}
			// �����׳��������쳣
			else {
				throw new RuntimeException("���������ַ��������Ƿ��ַ���ֻ�ܰ��������ַ���" + "�㡢һ�����������ġ��塢�����ߡ��ˡ��š�ʮ���١�ǧ������");
			}
		}

		if (lastChar == 'ʮ' && resultChars[loc] == '0') {
			resultChars[loc] = '1';
		}

		// ��ȡresultChars���ַ�����ʽ
		String resultString = String.copyValueOf(resultChars);
		// �����ַ�������
		resultString = new StringBuffer(resultString).reverse().toString();

		// �����ַ�������Ϊlong����
		return Long.parseLong(resultString);
	}

	/**
	 * ���س�����l�����ı�ʾ��ʽ
	 */
	public static String toString(long l) {

		if (l == 0) {
			return "��";
		}

		String numberString = Long.toString(l);

		numberString = new StringBuffer(numberString).reverse().toString();

		char[] resultChars = new char[12];
		for (int i = 0; i < resultChars.length; i++) {
			resultChars[i] = '��';
		}

		for (int i = 0; i < numberString.length(); i++) {
			resultChars[i] = dic.charAt(numberString.charAt(i) - '0');
		}

		StringBuffer sb = new StringBuffer(20);

		for (int i = 0; i < numberString.length(); i++) {
			if (i == 4) {
				sb.append('��');
			}
			if (i == 8) {
				sb.append('��');
			}
			if (resultChars[i] != '��') {
				if (i == 4 || i == 8) {
					sb.append(resultChars[i]);
				} else {
					sb.append("" + model.charAt(i) + resultChars[i]);
				}
			} else {
				if (sb.charAt(sb.length() - 1) != '��' && sb.charAt(sb.length() - 1) != '��'
						&& sb.charAt(sb.length() - 1) != '��') {
					sb.append('��');
				}
			}
		}

		sb.deleteCharAt(0);
		sb.reverse();

		return sb.toString();
	}
}
