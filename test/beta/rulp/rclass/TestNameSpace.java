package beta.rulp.rclass;

import org.junit.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestNameSpace extends RulpTestBase {

	@Test
	public void test_namespace_1() {

		_setup();
		_test("(new namespace ns1)", "ns1");
		_test("(defvar ns1::var1 1)", "&var1");
		_test("(value-of ns1::var1)", "1");

		_test("(defvar var1 2)", "&var1");
		_test("(value-of var1)", "2");

		_test("(value-of main::var1)", "2");
	}

	@Test
	public void test_namespace_2() {

		_setup();

		_test("(new namespace ns1)", "ns1");
		_test("(use ns1 (defvar var1 1))", "ns1");
		_test("(value-of ns1::var1)", "1");
	}

}
