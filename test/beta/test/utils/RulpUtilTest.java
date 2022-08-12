package beta.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RulpUtil.RResultList;

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

	String _toUniqString_1(String input) throws RException {

		List<IRObject> rst = _getParser().parse(input);
		if (rst.isEmpty()) {
			return "[]";
		}

		if (rst.size() == 1) {
			return "[" + RulpUtil.toUniqString(rst.get(0)) + "]";
		}

		return RulpUtil.toUniqString(RulpFactory.createList(rst));
	}

	String _toUniqString_2(String input) throws RException, IOException {

		RResultList rstList = RulpUtil.compute(this._getInterpreter(), input);

		try {
			
			if (rstList.results.isEmpty()) {
				return "[]";
			}

			if (rstList.results.size() == 1) {
				return "[" + RulpUtil.toUniqString(rstList.results.get(0)) + "]";
			}

			return RulpUtil.toUniqString(RulpFactory.createList(rstList.results));

		} finally {

			rstList.free();
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
	void test_to_uniq_string_1() {

		_setup();

		_test((input) -> {
			return _toUniqString_1(input);
		});

	}

	@Test
	void test_to_uniq_string_2() {
		try {
			assertEquals("$$null", RulpUtil.toUniqString(null));
		} catch (RException e) {
			fail(e.toString());
		}
	}

	@Test
	void test_to_uniq_string_3() {

		_setup();

		_test((input) -> {
			return _toUniqString_2(input);
		});

	}
}
