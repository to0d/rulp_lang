package beta.test.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;

public class XRParserTest extends RulpTestBase {

	String _parse_line(String input) throws RException {
		List<? extends IRObject> rstList = _getParser().parse(input);
		assertNotNull(rstList);
		return RulpUtil.toString(rstList);
	}

	void _test_parse_line(String input, String expect) {

		try {
			List<? extends IRObject> rstList = _getParser().parse(input);
			assertNotNull(rstList);
			assertEquals(input, expect, RulpUtil.toString(rstList));

		} catch (RException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_parser_1_special_char() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_2_operator() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_3_number_1() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_3_number_2_hex() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_4_vars() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_5_expr() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_5_list() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_6_comments() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_7_multi_lines_1() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_7_multi_lines_2() {

		// example comes from chtb_0593.nw
		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_7_multi_lines_3() {
		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_8_special_escape_string() {
		_setup();
		_test((input) -> {
			return _parse_line(input);
		});

	}

	@Test
	void test_parser_8_special_escape_string_2() {
		_setup();
		_test_parse_line("abc\ndef", "abc def");
		_test_parse_line("abc\r\ndef", "abc def");
		_test_parse_line("abc\rdef", "abcdef");
	}

	@Test
	void test_parser_8_special_string() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_8_special_string_not_support() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_8_special_string2() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});

	}

	@Test
	void test_parser_9_error() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_a_namespace() {

		_setup();
		_getParser().registerPrefix("nm", "https://github.com/to0d/nm#");
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_b_number_a() {

		_setup();
		this._getParser().setSupportNumber(false);
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_b_number_b() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_c_any() {
		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_d_named() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_e_array() {

		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

	@Test
	void test_parser_f_attr() {
		_setup();
		_test((input) -> {
			return _parse_line(input);
		});
	}

}
