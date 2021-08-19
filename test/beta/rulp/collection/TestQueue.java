package beta.rulp.collection;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestQueue extends RulpTestBase {

	@Test
	void test_queue_1() {

		_setup();

		_test("(new queue q1)", "q1");
		_test("(name-of q1)", "\"q1\"");
		_test("(q1::size-of)", "0");

		// push & pop
		_test("(q1::push 1)", "nil");
		_test("(q1::push 2)", "nil");
		_test("(q1::size-of)", "2");
		_test("(q1::pop)", "1");
		_test("(q1::pop)", "2");
		_test("(q1::size-of)", "0");

		// push_back & pop_back
		_test("(q1::push_back 1)", "nil");
		_test("(q1::push_back 2)", "nil");
		_test("(q1::size-of)", "2");
		_test("(q1::pop_back)", "2");
		_test("(q1::pop_back)", "1");
		_test("(q1::size-of)", "0");

		// push_front & pop_front
		_test("(q1::push_front 1)", "nil");
		_test("(q1::push_front 2)", "nil");
		_test("(q1::size-of)", "2");
		_test("(q1::pop_front)", "2");
		_test("(q1::pop_front)", "1");
		_test("(q1::size-of)", "0");

		_gInfo("result/collection/TestQueue/test_queue_1_ginfo.txt");
	}

	@Test
	void test_queue_2() {

		_setup();

		_test("(defmacro macro_push (?queue ?v) (?queue::push ?v))");
		_test("(new queue q1)");
		_test("(macro_push q1 1)", "nil");
		_test("(q1::size-of)", "1");
		_test("(q1::pop)", "1");
	}

	@Test
	void test_queue_3() {

		_setup();

		_test("(defun fun_push (?queue ?v) (?queue::push ?v))");
		_test("(new queue q1)");
		_test("(fun_push q1 1)", "nil");
		_test("(q1::size-of)", "1");
		_test("(q1::pop)", "1");
	}

	@Test
	void test_queue_4() {

		_setup();
		_test("(ls queue)",
				"'(queue::?impl queue::_queue_init queue::_queue_peek_back queue::_queue_peek_front queue::_queue_pop_back queue::_queue_pop_front queue::_queue_push_back queue::_queue_push_front queue::_queue_size_of queue::init queue::peek queue::peek_back queue::peek_front queue::pop queue::pop_back queue::pop_front queue::push queue::push_back queue::push_front queue::size-of)");

		_test("(new queue q1)", "q1");
		_test("(ls q1)", "'(q1::?impl q1::init q1::this)");

		_test("(q1::push 1)", "nil");
		_test("(ls q1)", "'(q1::?impl q1::init q1::push q1::this)");

		_test("(ls-print queue)", "nil", _load("result/collection/TestQueue/queue_ls_print.txt") + "\n");
	}

	@Test
	void test_queue_5_final() {

		_setup();
		_test_script("result/collection/TestQueue/test_queue_5_final.rulp");
	}

	@Test
	void test_queue_6_property() {

		_setup();
		_test("(property-of queue)", "'(final)");
	}

}
