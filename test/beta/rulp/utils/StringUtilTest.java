package beta.rulp.utils;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.StringUtil;

class StringUtilTest {

	@Test
	public void test_remove_escape() {
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
	public void test_add_Escape() {
		assertEquals("", StringUtil.addEscape(""));
		assertEquals("123", StringUtil.addEscape("123"));
		assertEquals("a\\\\b", StringUtil.addEscape("a\\b"));
	}

	@Test
	public void test_splitStringByStr() {
		assertEquals("[a, b, c]", StringUtil.splitStringByStr("a::b::c", "::").toString());
	}
}
