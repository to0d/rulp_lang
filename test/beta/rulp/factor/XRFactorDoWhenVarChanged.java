package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorDoWhenVarChanged extends RulpTestBase {

	@Test
	void test() {

		_setup();
		_test("(defvar x 1)");
		_test("(do-when-var-changed x (lambda (?var ?ov ?nv) (print \"var'\" (name-of ?var) \"' changed from '\" ?ov \"' to '\" ?nv \"'\")))");
		_test("(setq x 9)", "&x", "var'x' changed from '1' to '9'");

	}

}
