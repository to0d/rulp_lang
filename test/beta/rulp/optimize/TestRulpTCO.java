package beta.rulp.optimize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.optimize.CPSUtils;
import alpha.rulp.ximpl.optimize.StableUtil;

public class TestRulpTCO extends RulpTestBase {

	@Override
	protected IRInterpreter _createInterpreter() throws RException, IOException {

		IRInterpreter ip = RulpFactory.createInterpreter();

		RulpUtil.addFactor(ip.getMainFrame(), "findCallee", (_args, _interpreter, _frame) -> {

			if (_args.size() != 2) {
				throw new RException("Invalid parameters: " + _args);
			}

			IRFunction fun = RulpUtil.asFunction(_interpreter.compute(_frame, _args.get(1)));
			Set<String> calleeNames = CPSUtils.findCPSCallee(fun.getFunBody(), _frame);
			ArrayList<String> nameList = new ArrayList<>(calleeNames);
			Collections.sort(nameList);
			return RulpFactory.createListOfString(nameList);
		});

		return ip;
	}

	private void _test_is_stable_fun(String funName, boolean expectStable) {

		try {

			IRInterpreter interpreter = _getInterpreter();

			IRFunction func = RulpUtil.asFunction(interpreter.getObject(funName));

			boolean isStable = StableUtil.isStableFuncion(func);
			assertEquals(funName, expectStable, isStable);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void test_fun_1() {

		_setup();

		_test("(defun fun1 () ())");
		_test("(fun1)", "nil");

	}

	@Test
	public void test_is_stable_fun_1() {

		_setup();
		_test("(load \"result/optimize/TestRulpTCO/test_tco_fact.rulp\")");
		_test_is_stable_fun("fact", true);
		_test_is_stable_fun("fact2", true);
		_test_is_stable_fun("fact3", true);
		_test_is_stable_fun("fun1a", true);
		_test_is_stable_fun("fun1b", true);
		_test_is_stable_fun("fun2", true);
		_test_is_stable_fun("fun3", true);
		_test_is_stable_fun("fun4a", true);
		_test_is_stable_fun("fun4b", true);

	}

	@Test
	public void test_tco_fact() {

		_setup();

		_test("(setq ?op-cps true)");

		// fact(n) = n*fact(n-1)
		_test("(load \"result/optimize/TestRulpTCO/test_tco_fact.rulp\")");

		_test("(fact 1)", "1");
		_test("(fact 2)", "2");
		_test("(fact 3)", "6");
		assertEquals(36, CPSUtils.getCPSCount());

		_test("(fact 4)", "24");
		assertEquals(60, CPSUtils.getCPSCount());

		_gInfo();
	}

	@Test
	public void test_tco_findCallee() {

		_setup();

		_test("(load \"result/optimize/TestRulpTCO/test_tco_fact.rulp\")");
		_test("(findCallee fact)", "'(\"*\" \"-\" \"fact\")");
		_test("(findCallee fact2)", "'(\"-\" \"fact2\")");
		_test("(findCallee fact3)", "'(\"-\" \"fact3\")");
		_test("(findCallee fun1a)", "'(\"+\" \"-\" \"fun1a\")");
		_test("(findCallee fun1b)", "'(\"+\" \"-\" \"fun1b\")");
		_test("(findCallee fun2)", "'(\"+\" \"-\" \"/\" \"fun2\")");
		_test("(findCallee fun4a)", "'(\"+\" \"-\" \"fun4b\")");
		_test("(findCallee fun4b)", "'(\"+\" \"-\" \"fun4a\")");
	}

	@Test
	public void test_tco_fun1a() {

		_setup();

		_test("(setq ?op-cps true)");

		// fun(n) = n*(n+1)/2
		_test("(load \"result/optimize/TestRulpTCO/test_tco_fact.rulp\")");
		_test("(fun1a 0)", "0");
		_test("(fun1a 1)", "1");
		_test("(fun1a 2)", "3");
		_test("(fun1a 3)", "6");
		_test("(fun1a 100)", "5050");
		assertEquals(636, CPSUtils.getCPSCount());

		_test("(fun1a 1000)", "500500");
		assertEquals(6636, CPSUtils.getCPSCount());

		_gInfo();
	}

	@Test
	public void test_tco_fun1b() {

		_setup();

		_test("(setq ?op-cps true)");

		// fun(n) = n*(n+1)/2
		_test("(load \"result/optimize/TestRulpTCO/test_tco_fact.rulp\")");
		_test("(fun1b 0)", "0");
		_test("(fun1b 1)", "1");
		_test("(fun1b 2)", "3");
		_test("(fun1b 3)", "6");
		_test("(fun1b 100)", "5050");
		assertEquals(636, CPSUtils.getCPSCount());

		_test("(fun1b 1000)", "500500");
		assertEquals(6636, CPSUtils.getCPSCount());

		_gInfo();
	}

	@Test
	public void test_tco_fun2() {

		_setup();

		_test("(setq ?op-cps true)");

		_test("(load \"result/optimize/TestRulpTCO/test_tco_fact.rulp\")");
		_test("(fun2 0)", "0");
		_test("(fun2 1)", "1");
		_test("(fun2 2)", "3");
		_test("(fun2 3)", "6");
		_test("(fun2 11)", "65");
		assertEquals(132, CPSUtils.getCPSCount());

		_test("(fun2 101)", "132564");
		assertEquals(252432, CPSUtils.getCPSCount());

		fail("how to optimize");
		_test("(fun2 1001)", "45866");
		assertEquals(0, CPSUtils.getCPSCount());

	}

	@Test
	public void test_tco_fun2_by_cc() {

		_setup();

		// cc: compute in cache

		_test("(load \"result/optimize/TestRulpTCO/test_tco_fact.rulp\")");
		_test("(cc (fun2 0))", "0");
		_test("(cc (fun2 1))", "1");
		_test("(cc (fun2 2))", "3");
		_test("(cc (fun2 3))", "6");
		_test("(cc (fun2 11))", "65");
		_test("(cc (fun2 101))", "132564");
		_test("(cc (fun2 1001))", "45866");

		_gInfo("result/optimize/TestRulpTCO/test_tco_fun2_by_cc.txt");
		
	}

	@Test
	public void test_tco_fun3() {

		fail("not support yet");

		_setup();

		// fun(n) = n*(n+1)/2
		_test("(load \"result/optimize/TestRulpTCO/test_tco_fact.rulp\")");
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

	@Test
	public void test_tco_fun4() {

		fail("not support yet");

		_setup();

		// fun(n) = n*(n+1)/2
		_test("(load \"result/optimize/TestRulpTCO/test_tco_fact.rulp\")");
		_test("(fun4a 0)", "0");
		_test("(fun4a 1)", "1");
		_test("(fun4a 2)", "3");
		_test("(fun4a 3)", "6");
		_test("(fun4a 100)", "5050");
		_test("(fun4a 1000)", "24");
	}
}
