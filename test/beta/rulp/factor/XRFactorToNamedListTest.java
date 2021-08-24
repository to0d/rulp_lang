package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorToNamedListTest extends RulpTestBase {

	@Test
	void test() {
		_setup();
		
		_test("(to-named-list a '(b c))", "a:'(b c)");
		_test("(to-named-list \"a\" '(b c))", "a:'(b c)");
		_test("(to-named-list a x:'(b c))", "a:'(b c)");
		
		_test("(defvar ?x a)");
		_test("(defvar ?y b)");
		_test("(to-named-list ?x '(?y c))", "a:'(b c)");
		
		_test("(defun fun (?x ?y) (return (to-named-list ?x '(?y c))))");
		_test("(fun a b)","a:'(b c)");
	}
}
