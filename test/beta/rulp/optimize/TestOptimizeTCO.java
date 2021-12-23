package beta.rulp.optimize;

import static org.junit.Assert.fail;

import org.junit.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestOptimizeTCO extends RulpTestBase {

	@Test
	public void test_tco_1() {

		_setup();
		_run_script();
	}

	@Test
	public void test_tco_2_overflow_a() {

		_setup();
		_run_script();
	}

	@Test
	public void test_tco_2_overflow_b() {

		_setup();
		_run_script();
	}

	@Test
	public void test_tco_3_return() {

		_setup();
		_run_script();
	}

	@Test
	public void test_tco_4_if() {

		_setup();
		_run_script();
	}

	@Test
	public void test_tco_5_multi() {

		_setup();
		_run_script();

//		fail("how to optimize");
//		_test("(fun2 1001)", "45866");
//		assertEquals(0, CPSUtils.getCPSCount());
	}

	@Test
	public void test_tco_6_recursion_cycle() {
		_setup();
		_run_script();
	}

	@Test
	public void test_tco_7_str() {

		_setup();
		_run_script();
	}
	
	@Test
	public void test_tco_8_no_op() {

		_setup();
		_run_script();
	}

//	@Test
	public void test_tco_8_fun2_by_cc() {

		_setup();

		// cc: compute in cache
		_test("(cc (fun2 0))", "0");
		_test("(cc (fun2 1))", "1");
		_test("(cc (fun2 2))", "3");
		_test("(cc (fun2 3))", "6");
		_test("(cc (fun2 11))", "65");
		_test("(cc (fun2 101))", "132564");
		_test("(cc (fun2 1001))", "45866");

		_gInfo();

	}
}
