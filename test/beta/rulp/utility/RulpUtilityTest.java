package beta.rulp.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;

public class RulpUtilityTest extends RulpTestBase {

	public void _test_toString(String input, String expect) {

		IRParser parser = RulpFactory.createParser();
		try {

			parser.registerPrefix("nm", "https://github.com/to0d/nm#");
			IRObject obj = RulpFactory.createList(_getParser().parse(input));
			String out = RulpUtil.toString(obj);
			assertEquals(input, expect, out);

		} catch (RException e) {
			e.printStackTrace();
			fail(e.toString());
		}

	}

	@Test
	public void test_formatAsShortName() {

		_setup();
		_getParser().registerPrefix("nm", "https://github.com/to0d/nm#");
		_test_toString("(nm:a nm:b)", "'((nm:a nm:b))");
	}

	@Test
	public void test_isValidRulpStmt() {

		assertTrue(RulpUtil.isValidRulpStmt("(a b c); comments"));
		assertTrue(RulpUtil.isValidRulpStmt("(a b c) (b c)"));
	}

}
