package beta.test.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.RException;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.StringUtil;

public class StringUtilTest extends RulpTestBase {

	void _test_getCharType(String input, String output) {

		ArrayList<Integer> types = new ArrayList<>();

		for (char c : input.toCharArray()) {
			types.add(StringUtil.getCharType(c).index);
		}

		assertEquals(output, types.toString());
	}

	@Test
	void test_add_Escape() {

		_setup();

		assertEquals("", StringUtil.addEscape(""));
		assertEquals("123", StringUtil.addEscape("123"));
		assertEquals("a\\\\b", StringUtil.addEscape("a\\b"));
	}

	@Test
	void test_getCharType() {

		_setup();

		_test_getCharType("abz", "[10, 10, 10]");
		_test_getCharType("123", "[0, 0, 0]");
		_test_getCharType("\"()+-\\\'|:;,[]{}@#=/$?&*%<>!^·_.`",
				"[11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11]");
		_test_getCharType("ⅠⅩⅪⅫⅡⅢⅣⅤⅥⅦⅧⅨ", "[53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53]");
		_test_getCharType("ㄅㄒㄟ", "[33, 33, 33]");
		_test_getCharType("の", "[41]");
	}

	@Test
	void test_getSingleMatchString() {

		_setup();

		try {
			assertNull(StringUtil.getSingleMatchString(" %? %d *", "   "));
		} catch (RException e) {

			e.printStackTrace();
			fail(e.toString());
		}

	}

	@Test
	void test_parseChineseNumber() {

		_setup();

		assertEquals(1l, StringUtil.parseChineseNumber("一"));
		assertEquals(100l, StringUtil.parseChineseNumber("一百"));
		assertEquals(10l, StringUtil.parseChineseNumber("十"));
		assertEquals(12l, StringUtil.parseChineseNumber("十二"));
		assertEquals(12l, StringUtil.parseChineseNumber("十二零"));
	}

	@Test
	void test_remove_escape() {

		_setup();

		assertEquals("", StringUtil.removeEscape(""));
		assertEquals(null, StringUtil.removeEscape(null));
		assertEquals("a", StringUtil.removeEscape("a"));
		assertEquals(" ", StringUtil.removeEscape(" "));
		assertEquals(" a ", StringUtil.removeEscape(" a "));
		assertEquals(" a\nb ", StringUtil.removeEscape(" a\\" + "nb "));
		assertEquals(" a\nb ", StringUtil.removeEscape(" a\\nb "));
		assertEquals(" a\\b ", StringUtil.removeEscape(" a\\\\b "));
	}

	@Test
	void test_splitStringByChar() {

		_setup();

		assertEquals("[]", "" + StringUtil.splitStringByChar("", ' '));
		assertEquals("[a, b]", "" + StringUtil.splitStringByChar("a b", ' '));
		assertEquals("[a, b]", "" + StringUtil.splitStringByChar("a  b", ' '));
		assertEquals("[a, b]", "" + StringUtil.splitStringByChar(" a b ", ' '));
		assertEquals("[a, b]", "" + StringUtil.splitStringByChar(" a*b ", ' ', '*'));
	}

	@Test
	void test_splitStringByStr() {

		_setup();

		assertEquals("[a, b, c]", StringUtil.splitStringByStr("a::b::c", "::").toString());
	}

	@Test
	void test_trimHead() {

		_setup();

		assertEquals("abc", StringUtil.trimHead(" abc", ' '));
		assertEquals("abc ", StringUtil.trimHead(" abc ", ' '));
		assertEquals("abc", StringUtil.trimHead("  abc", ' '));
		assertEquals("abc", StringUtil.trimHead(" |abc", ' ', '|'));
		assertEquals("abc", StringUtil.trimHead(" | abc", ' ', '|'));
	}

	@Test
	void test_trimTail() {

		_setup();

		assertEquals("abc", StringUtil.trimTail("abc ", ' '));
		assertEquals(" abc", StringUtil.trimTail(" abc ", ' '));
		assertEquals("abc", StringUtil.trimTail("abc  ", ' '));
		assertEquals("abc", StringUtil.trimTail("abc |", ' ', '|'));
		assertEquals("abc", StringUtil.trimTail("abc | ", ' ', '|'));
	}
}
