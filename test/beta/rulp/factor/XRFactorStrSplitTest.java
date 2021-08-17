package beta.rulp.factor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorStrSplitTest extends RulpTestBase {

	@Test
	void test() {
		_test("(str-split \"\" \" \")", "'()");
		_test("(str-split \"abc\" \" \")", "'(\"abc\")");
		_test("(str-split \"abc \" \" \")", "'(\"abc\")");
		_test("(str-split \"abc  def\" \" \")", "'(\"abc\" \"def\")");
		_test("(str-split \" abc|de f\" \" |\")", "'(\"abc\" \"de\" \"f\")");
	}
}