package beta.rulp.factor;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

class XRFactorArithmeticTest extends RulpTestBase {

	@Test
	void test_arithmetic_1_add_1() {
		_setup();
		_run_script();
	}
	
	@Test
	void test_arithmetic_1_add_2_str() {
		_setup();
		_run_script();
	}

	@Test
	void test_arithmetic_1_add_3_class() {
		_setup();
		_run_script();
	}

	
	@Test
	void test_arithmetic_2_sub() {
		_setup();
		_run_script(); 
	}

	@Test
	void test_arithmetic_3_by() {
		_setup();
		_run_script();  
	}

	@Test
	void test_arithmetic_4_div() {
		_setup();
		_run_script();   
	}

	@Test
	void test_arithmetic_5_mod() {
		_setup();
		_run_script();  
	}

	@Test
	void test_arithmetic_6_power() {
		_setup();
		_run_script(); 
	}

	@Test
	void test_arithmetic_7_and() {
		_setup();
		_run_script();  
	}

	@Test
	void test_arithmetic_8_or() {
		_setup();
		_run_script();   
	}

	@Test
	void test_arithmetic_9_xor() {
		_setup();
		_run_script();   
	}

	@Test
	void test_arithmetic_a_not() {
		_setup();
		_run_script(); 
	}
}
