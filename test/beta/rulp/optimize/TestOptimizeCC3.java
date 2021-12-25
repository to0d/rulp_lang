package beta.rulp.optimize;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestOptimizeCC3 extends RulpTestBase {

	@Test
	public void test_cc3_1_stable_fun() {

		_setup();
		_run_script();
	}

	@Test
	public void test_cc3_2_recursion_fun() {

		_setup();
		_run_script();
	}

	@Test
	public void test_cc3_3_lt0062_a() {

		_setup();
		_run_script();
		fail("why test_cc3_3_lt0062_a is bad than test_cc3_3_lt0062_b????");
	}

	@Test
	public void test_cc3_3_lt0062_b() {

		_setup();
		_run_script();
	}
}
