package beta.test.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import alpha.common.utils.FileUtil;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;

public class XRParserTest extends RulpTestBase {

	void _test_parse_line(String line) {
//		System.out.println(line);
		_test_parse_line(line, line, true);
	}

	void _test_parse() {
		_test_parse(getCachePath() + ".txt");
	}

	void _test_parse(String path) {

		ArrayList<String> outlines = new ArrayList<>();

		try {

			List<String> lines = FileUtil.openTxtFile(path, "utf-8");
			int index = 0;
			while (index < lines.size()) {

				String line = lines.get(index++);
				if (line.trim().isEmpty()) {
					continue;
				}

				if (!line.startsWith("in : ")) {

					try {

						outlines.add("in : " + line);
						List<? extends IRObject> rstList = _getParser().parse(line);
						assertNotNull(rstList);
						outlines.add("out: " + RulpUtil.toString(rstList));
						outlines.add("");

					} catch (RException e) {
						outlines.add("err: " + e.toString());
						outlines.add("");
					}

					continue;
				}

				StringBuffer sb = new StringBuffer();
				sb.append(line.substring(5));
				while (index < lines.size()) {

					String line2 = lines.get(index++);
					if (line2.trim().equals("eof")) {
						break;
					}

					sb.append("\n" + line2);
				}

				String input = sb.toString();
				outlines.add("in : " + input);
				outlines.add("eof");
				try {

					List<? extends IRObject> rstList = _getParser().parse(input);
					assertNotNull(rstList);
					outlines.add("out: " + RulpUtil.toString(rstList));
					outlines.add("");

				} catch (RException e) {
					outlines.add("err: " + e.toString());
					outlines.add("");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(String.format("error found: %s, file=%s", e.toString(), path));

		} finally {

			try {
				FileUtil.saveTxtFile(path + ".out", outlines, "utf-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void _test_parse_line(String line, String expect) {

		_test_parse_line(line, expect, true);
	}

	void _test_parse_line(String input, String expect, boolean succ) {

//		if (input.indexOf('\n') != -1) {
//			System.out.println("in : " + input);
//			System.out.println("eof");
//		} else {
//			System.out.println(input);
//		}

		try {

			List<? extends IRObject> rstList = _getParser().parse(input);
			if (!succ) {
				fail("Should fail: " + input);
			}

			assertNotNull(rstList);
			assertEquals(input, expect, RulpUtil.toString(rstList));

		} catch (RException e) {

			if (succ) {
				e.printStackTrace();
				fail(e.toString());
			} else {
				assertEquals(input, expect, e.getMessage());
			}
		}
	}

	@Test
	void test_parser_1_special_char() {

		_setup();
		_test_parse();
	}

	@Test
	void test_parser_2_operator() {

		_setup();
		_test_parse();
	}

	@Test
	void test_parser_3_number_1() {

		_setup();
		_test_parse();
	}

	@Test
	void test_parser_3_number_2_hex() {
		_setup();
		_test_parse();
	}

	@Test
	void test_parser_4_vars() {

		_setup();
		_test_parse();
	}

	@Test
	void test_parser_5_expr() {

		_setup();
		_test_parse();
	}

	@Test
	void test_parser_5_list() {

		_setup();
		_test_parse();

	}

	@Test
	void test_parser_6_comments() {

		_setup();
		_test_parse();
	}

	@Test
	void test_parser_7_multi_lines_1() {

		_setup();
		_test_parse();
	}

	@Test
	void test_parser_7_multi_lines_2() {

		// example comes from chtb_0593.nw
		_setup();
		_test_parse();

	}

	@Test
	void test_parser_7_multi_lines_3() {
		_setup();
		_test_parse();
	}

	@Test
	void test_parser_8_special_escape_string() {
		_setup();
		_test_parse_line("\"\\\"abc\"");
		_test_parse_line("\"abc\\ndef\"", "\"abc\n" + "def\"");
		_test_parse_line("\"abc\\rdef\"", "\"abc\r" + "def\"");
		_test_parse_line("abc\ndef", "abc def");
		_test_parse_line("abc\r\ndef", "abc def");
		_test_parse_line("abc\rdef", "abcdef");
	}

	@Test
	void test_parser_8_special_string() {

		_setup();
		_test_parse();

	}

	@Test
	void test_parser_8_special_string_not_support() {

		_setup();
		_test_parse();
	}

	@Test
	void test_parser_8_special_string2() {

		_setup();
		_test_parse();

	}

	@Test
	void test_parser_9_error() {

		_setup();
		_test_parse();
	}

	@Test
	void test_parser_a_namespace() {

		_setup();
		_getParser().registerPrefix("nm", "https://github.com/to0d/nm#");
		_test_parse();
	}

	@Test
	void test_parser_b_number_a() {

		_setup();
		this._getParser().setSupportNumber(false);
		_test_parse();
	}

	@Test
	void test_parser_b_number_b() {

		_setup();
		_test_parse();
	}

	@Test
	void test_parser_c_any() {
		_setup();
		_test_parse();
	}

	@Test
	void test_parser_d_named() {

		_setup();
		_test_parse();
	}

	@Test
	void test_parser_e_array() {

		_setup();
		_test_parse();
	}

	@Test
	void test_parser_f_attr() {
		_setup();
		_test_parse();
	}

}
