package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestStack extends RulpTestBase {

	@Test
	void test_stack_1() {

		_setup();

		_test("(new stack s1)", "s1");
		_test("(name-of s1)", "\"s1\"");
		_test("(s1::size-of)", "0");

		// push & pop
		_test("(s1::push 1)", "nil");
		_test("(s1::push 2)", "nil");
		_test("(s1::size-of)", "2");
		_test("(s1::pop)", "2");
		_test("(s1::pop)", "1");
		_test("(s1::size-of)", "0");

		// is-empty
		_test("(s1::is-empty)", "true");
		_test("(is-empty s1)", "true");

		// to-list
		_test("(s1::push 1)", "nil");
		_test("(s1::push 2)", "nil");
		_test("(s1::push 3)", "nil");
		_test("(s1::to-list)", "'(1 2 3)");
		_test("(to-list s1)", "'(1 2 3)");

		// clear
		_test("(s1::clear)", "nil");
		_test("(clear s1)", "nil");
		_test("(is-empty s1)", "true");

		_gInfo("result/collection/TestStack/test_stack_1_ginfo.txt");
	}

	@Test
	void test_stack_2() {

		_setup();

		_test("(defmacro macro_push (?stack ?v) (?stack::push ?v))");
		_test("(new stack s1)", "s1");
		_test("(macro_push s1 1)", "nil");
		_test("(s1::size-of)", "1");
		_test("(s1::pop)", "1");
	}

	@Test
	void test_stack_3() {

		_setup();

		_test("(defun fun_push (?stack ?v) (?stack::push ?v))");
		_test("(new stack s1)", "s1");
		_test("(fun_push s1 1)", "nil");
		_test("(s1::size-of)", "1");
		_test("(s1::pop)", "1");
	}

	@Test
	void test_stack_4() {

		_setup();
		_test("(ls stack)",
				"'(stack::?impl stack::clear stack::init stack::is-empty stack::peek stack::pop stack::push stack::size-of stack::to-list)");

		_test("(new stack s1)", "s1");
		_test("(ls s1)", "'(s1::?impl s1::init s1::this)");

		_test("(s1::push 1)", "nil");
		_test("(ls s1)", "'(s1::?impl s1::init s1::push s1::this)");

		_test("(ls-print stack)", "nil", _load("result/collection/TestStack/stack_ls_print.txt") + "\n");
	}

	@Test
	void test_stack_5_final() {

		_setup();
		_run_script();
	}

}