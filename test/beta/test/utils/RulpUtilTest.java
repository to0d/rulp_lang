package beta.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;

public class RulpUtilTest extends RulpTestBase {

	void _test_toString(String input, String expect) {

		IRParser parser = RulpFactory.createParser();
		try {

			parser.registerPrefix("nm", "https://github.com/to0d/nm#");
			IRObject obj = RulpFactory.createList(parser.parse(input));
			String out = RulpUtil.toString(obj);
			assertEquals(input, expect, out);

		} catch (RException e) {
			e.printStackTrace();
			fail(e.toString());
		}

	}

	void _test_toUniqString(String input, String expect) {

		try {

			IRObject obj = RulpFactory.createList(_getParser().parse(input));
			String out = RulpUtil.toUniqString(obj);
			assertEquals(input, expect, out);

		} catch (RException e) {
			e.printStackTrace();
			fail(e.toString());
		}

	}

	@Test
	void test_formatAsShortName() {

		_setup();
		_getParser().registerPrefix("nm", "https://github.com/to0d/nm#");
		_test_toString("(nm:a nm:b)", "'((nm:a nm:b))");
	}

	@Test
	void test_isValidRulpStmt() {

		_setup();

		assertTrue(RulpUtil.isValidRulpStmt("(a b c); comments"));
		assertTrue(RulpUtil.isValidRulpStmt("(a b c) (b c)"));
	}

	@Test
	void test_list_1() {

		assertEquals("[]", RulpUtil.toList().toString());
		assertEquals("[1, 2, a]", RulpUtil.toList(1, 2, "a").toString());
	}

	@Test
	void test_toUniqString() {

		_setup();

		_test_toUniqString("1d", "'($$d_1.0)");
	}
}
