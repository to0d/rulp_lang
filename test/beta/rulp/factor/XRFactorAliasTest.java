package beta.rulp.factor;

import org.junit.Test;

import alpha.rulp.utils.RulpTestBase;

public class XRFactorAliasTest extends RulpTestBase {

	@Test
	public void test1() {
		_setup();
		_test("(defvar x 10) x (alias x y) y", "&x 10 &x 10");

	}

	@Test
	public void test2() {
		_setup();
		_test("(alias + myplus) (myplus 1 2)", "+ 3");

	}

	@Test
	public void test3_macro_alias() {

		_setup();
		_test("(defmacro m1 (?v1 ?v2) (?v1 ?v2 3)) (m1 + 1)", "m1 4");
		_test("(alias m1 m2) (m2 + 1)", "m1 4");

	}
}
