package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorSetaTest extends RulpTestBase {

	@Test
	public void test_seta_1() {
		_setup();
		_test("(seta {a, b} 1 x)", "{a,x}");
	}

	@Test
	public void test_seta_2() {
		_setup();
		_test("(defvar ?x {a, b})", "&?x");
		_test("(seta ?x 1 x)", "{a,x}");
		_test("?x", "{a,x}");
	}
}
