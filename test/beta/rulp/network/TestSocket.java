package beta.rulp.network;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.RulpTestBase;

public class TestSocket extends RulpTestBase {

	@Test
	void test_1() {

		_setup();
		_test("(property-of socket)", "'(final)");
		_test("(ls socket)",
				"'(socket::?impl socket::_socket_close socket::_socket_init socket::_socket_open socket::_socket_write socket::close socket::init socket::open socket::write)");
	}

	@Test
	void test_2() {

		_setup();
		_test("(new socket s1 '(\"127.0.0.1\" 1234))", "s1");
		_test_error("(s1::open)",
				"fail to open socket<127.0.0.1:1234>: java.net.ConnectException: Connection refused: connect\n"
						+ "at _$fun$_open: (socket::_socket_open ?impl)\n" + "at main: (s1::open)");
		_test("(s1::close)");
	}

	@Test
	void test_3() {

		_setup();

		_test("(defvar ?addr \"127.0.0.1\")", "&?addr");
		_test("(new socket s1 '(?addr 1234))", "s1");
	}
}
