package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorDateTest extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(date \"2020-01-01\")", "\"2020-01-01\"");
		_test("(date (date))", null);
	}

}
