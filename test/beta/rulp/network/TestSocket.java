package beta.rulp.network;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestSocket extends RulpTestBase {

	@Test
	void test_socket_1_ls() {

		_setup();
		_test_script();
		_gInfo();
	}

	@Test
	void test_socket_2_open() {

		_setup();
		_test_script();
		_gInfo();
	}

	@Test
	void test_socket_3_getLocalHost() {

		_setup();
		_test("(socket::getLocalHost)", "&?b1");
	}
}
