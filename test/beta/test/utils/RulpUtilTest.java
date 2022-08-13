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

	String _test_toString(String input) throws RException {

		List<IRObject> rst = _getParser().parse(input);
		if (rst.isEmpty()) {
			return "[]";
		}

		if (rst.size() == 1) {
			return "[" + RulpUtil.toString(rst.get(0)) + "]";
		}

		return RulpUtil.toString(RulpFactory.createList(rst));
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
	void test_isValidRulpStmt() {

		_setup();

		assertTrue(RulpUtil.isValidRulpStmt("(a b c); comments"));
		assertTrue(RulpUtil.isValidRulpStmt("(a b c) (b c)"));
	}

	@Test
	void test_toList_1() {

		assertEquals("[]", RulpUtil.toList().toString());
		assertEquals("[1, 2, a]", RulpUtil.toList(1, 2, "a").toString());
	}

	@Test
	void test_toString_1() {

		_setup();
		_test((input) -> {
			return _test_toString(input);
		});
	}

	@Test
	void test_toString_2() {

		_setup();
		_getParser().registerPrefix("nm", "https://github.com/to0d/nm#");

		_test((input) -> {
			return _test_toString(input);
		});

	}

	@Test
	void test_toUniqString_1() {
		try {
			assertEquals("$$null", RulpUtil.toUniqString(null));
		} catch (RException e) {
			fail(e.toString());
		}
	}

	@Test
	void test_toUniqString_2() {

		_setup();

		_test((input) -> {
			return _toUniqString_1(input);
		});

	}

	@Test
	void test_toUniqString_3() {

		_setup();

		_test((input) -> {
			return _toUniqString_2(input);
		});

	}
}
