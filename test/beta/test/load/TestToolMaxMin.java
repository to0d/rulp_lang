package beta.test.load;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestToolMaxMin extends RulpTestBase {

	@Test
	void test_max_1() {
		_setup();
		_run_script();
	}
	
	@Test
	void test_max_2_list() {
		_setup();
		_run_script();
	}

	@Test
	void test_min_1() {
		_setup();
		_run_script();
	}
	
	@Test
	void test_min_2_list() {
		_setup();
		_run_script();
	}
}
