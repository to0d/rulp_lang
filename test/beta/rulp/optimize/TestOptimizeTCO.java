package beta.rulp.optimize;

import static org.junit.Assert.fail;

import org.junit.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestOptimizeTCO extends RulpTestBase {

//	@Override
//	protected IRInterpreter _createInterpreter() throws RException, IOException {
//
//		IRInterpreter ip = RulpFactory.createInterpreter();
//
//		RulpUtil.addFactor(ip.getMainFrame(), "findCallee", (_args, _interpreter, _frame) -> {
//
//			if (_args.size() != 2) {
//				throw new RException("Invalid parameters: " + _args);
//			}
//
//			IRFunction fun = RulpUtil.asFunction(_interpreter.compute(_frame, _args.get(1)));
//			Set<String> calleeNames = CPSUtils.findCPSCallee(fun.getFunBody(), _frame);
//			ArrayList<String> nameList = new ArrayList<>(calleeNames);
//			Collections.sort(nameList);
//			return RulpFactory.createListOfString(nameList);
//		});
//
//		return ip;
//	}

	@Test
	public void test_tco_1() {

		_setup();
		_run_script();
	}

	@Test
	public void test_tco_2_overflow() {

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

//	@Test
	public void test_tco_9_fun3() {

		fail("not support yet");

		_setup();

		// fun(n) = n*(n+1)/2
		_test("(load \"result/optimize/TestOptimizeTCO/test_tco_fact.rulp\")");
		_test("(fun3 0)", "1");
		_test("(fun3 1)", "1");
		_test("(fun3 2)", "2");
		_test("(fun3 3)", "1");
		_test("(fun3 4)", "4");
		_test("(fun3 5)", "1");
		_test("(fun3 100)", "100");
		_test("(fun3 101)", "1");
		_test("(fun3 1000)", "1000");
	}

}
