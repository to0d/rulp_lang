package beta.rulp.utils;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.StringUtil;

class StringUtilTest {

	@Test
	public void test_removeEscapeString() {
		assertEquals("", StringUtil.removeEscapeString(""));
		assertEquals(null, StringUtil.removeEscapeString(null));
		assertEquals("a", StringUtil.removeEscapeString("a"));
		assertEquals(" ", StringUtil.removeEscapeString(" "));
		assertEquals(" a ", StringUtil.removeEscapeString(" a "));
		assertEquals(" a\nb ", StringUtil.removeEscapeString(" a\\" + "nb "));
		assertEquals(" a\nb ", StringUtil.removeEscapeString(" a\\nb "));
		assertEquals(" a\\b ", StringUtil.removeEscapeString(" a\\\\b "));
	}

	@Test
	public void test_addEscapeString() {
		assertEquals("", StringUtil.addEscapeString(""));
		assertEquals("123", StringUtil.addEscapeString("123"));
		assertEquals("a\\\\b", StringUtil.addEscapeString("a\\b"));
	}

	@Test
	public void test_splitStringByStr() {
		assertEquals("[a, b, c]", StringUtil.splitStringByStr("a::b::c", "::").toString());
	}
}
