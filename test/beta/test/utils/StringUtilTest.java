package beta.test.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.string.ChineseWord;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.StringUtil;

public class StringUtilTest extends RulpTestBase {

	String _addEscape(String input) {
		return StringUtil.addEscape(input);
	}

	String _getCharType(String input) {

		ArrayList<Integer> types = new ArrayList<>();

		for (char c : input.toCharArray()) {
			types.add(StringUtil.getCharType(c).index);
		}

		return types.toString();
	}

	String _parseChineseNumber(String input) {
		return "" + StringUtil.parseChineseNumber(input);
	}

	String _removeEscape(String input) {
		return StringUtil.removeEscape(input);
	}

	String _smartSort_1(String input) throws RException {

		List<IRObject> objs = _getParser().parse(input);
		assertEquals(1, objs.size());

		ArrayList<String> elements = new ArrayList<>();

		IRIterator<? extends IRObject> it = RulpUtil.asList(objs.get(0)).iterator();
		while (it.hasNext()) {
			elements.add(it.next().toString());
		}

		String pre = StringUtil.smartSort(elements, false);

		return "" + elements + ", pre=[" + pre + "]";
	}

	@Test
	void test_addEscape_1() {

		_setup();

		assertEquals("", StringUtil.addEscape(""));

	}

	@Test
	void test_addEscape_2() {

		_setup();

		_test((input) -> {
			return _addEscape(input);
		});
	}

	@Test
	void test_chinese_toString_0() {

		_setup();

		assertEquals("零", ChineseWord.toString(0));
		assertEquals("一", ChineseWord.toString(1L));
		assertEquals("一千二百三十四", ChineseWord.toString(1234));
	}

	@Test
	void test_getCharType_1() {

		_setup();

		_test((input) -> {
			return _getCharType(input);
		});
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
	void test_parseChineseNumber_0() {

		_setup();

		assertEquals(0, StringUtil.parseChineseNumber(""));

	}

	@Test
	void test_parseChineseNumber_1() {

		_setup();

		_test((input) -> {
			return _parseChineseNumber(input);
		});

	}

	@Test
	void test_removeEscape_1() {

		_setup();

		assertEquals(null, StringUtil.removeEscape(null));
		assertEquals("", StringUtil.removeEscape(""));
		assertEquals(" ", StringUtil.removeEscape(" "));
		assertEquals(" a ", StringUtil.removeEscape(" a "));
	}

	@Test
	void test_removeEscape_2() {

		_setup();

		_test((input) -> {
			return _removeEscape(input);
		});
	}

	@Test
	void test_simplifyPath() {

		assertEquals("", StringUtil.simplifyPath(""));
		assertEquals("/ab", StringUtil.simplifyLinuxPath("/ab"));
		assertEquals("/", StringUtil.simplifyLinuxPath("/"));
		assertEquals("/c", StringUtil.simplifyLinuxPath("/a/./b/../../c/"));
		assertEquals("/x", StringUtil.simplifyLinuxPath("/////x"));
		assertEquals("/x/y", StringUtil.simplifyLinuxPath("/x//////y"));
		assertEquals("/y", StringUtil.simplifyLinuxPath("/x/../y"));
		assertEquals("/", StringUtil.simplifyLinuxPath("/x/../../"));
		assertEquals("/", StringUtil.simplifyLinuxPath("/.."));
		assertEquals("/e/f/g",
				StringUtil.simplifyLinuxPath("/a/./b///../c/../././../d/..//../e/./f/./g/././//.//h///././/..///"));
		assertEquals("/y\\\\ \\\\ ", StringUtil.simplifyLinuxPath("/x/../y\\\\ \\\\ "));
	}

	@Test
	void test_smartSort_1() {

		_setup();

		_test((input) -> {
			return _smartSort_1(input);
		});

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
