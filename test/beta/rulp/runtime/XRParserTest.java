package beta.rulp.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.utils.FileUtil;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;

public class XRParserTest extends RulpTestBase {

	void _test_parse_error(String input, String expect_msg) {

		try {
			_getParser().parse(input);
			fail("expect fail");
		} catch (RException e) {
			assertEquals(expect_msg, e.toString());
		}
	}

	void _test_parse_line(String line) {
		_test_parse_line(line, line, true);
	}

	void _test_parse_line(String line, String expect) {
		_test_parse_line(line, expect, true);
	}

	void _test_parse_line(String input, String expect, boolean succ) {

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
			}
		}
	}

	void _test_parse_line_error(String input, String error) {

		try {

			_getParser().parse(input);
			fail("Should fail: " + input);

		} catch (RException e) {
			assertEquals(input, error, e.getMessage());
		}

	}

	@Test
	public void test_1_atom() {

		_setup();

		_test_parse_line("AD");

		try {
			for (String line : FileUtil.openTxtFile("result/runtime/XRParserTest/parse_atom_1.txt", "utf-8")) {
				if (line.isEmpty()) {
					continue;
				}
				_test_parse_line(line);
			}
		} catch (IOException e) {
			fail(e.toString());
		}
	}

	@Test
	public void test_2_operator() {

		_setup();
		_test_parse_line("+");
		_test_parse_line("-");
		_test_parse_line("*");
		_test_parse_line("/");
		_test_parse_line("mod");
		_test_parse_line("rem");
		_test_parse_line("incf");
		_test_parse_line("decf");

		_test_parse_line("=");
		_test_parse_line("/=");
		_test_parse_line(">");
		_test_parse_line("<");
		_test_parse_line(">=");
		_test_parse_line("<=");
		_test_parse_line("max");
		_test_parse_line("min");

		_test_parse_line("and");
		_test_parse_line("or");
		_test_parse_line("not");

		_test_parse_line("logand");
		_test_parse_line("logior");
		_test_parse_line("logxor");
		_test_parse_line("lognor");
		_test_parse_line("logeqv");

		_test_parse_line("def!");
		_test_parse_line("let*");

		_test_parse_line("?");
	}

	@Test
	public void test_3_value() {

		_setup();

		_test_parse_line("1");
		_test_parse_line("+1", "1");
		_test_parse_line("-1");
		_test_parse_line("2147483647");
		_test_parse_line("2147483648", "[0, -1]: invalid int format, 2147483648", false);
		_test_parse_line("-2147483648");
		_test_parse_line("-2147483649", "[0, 11]: invalid int format, -2147483649", false);

		_test_parse_line("1.1");
		_test_parse_line("+1.1", "1.1");
		_test_parse_line("-1.1");

		_test_parse_line("true");
		_test_parse_line("false");

		_test_parse_line("100L");
		_test_parse_line("+100L", "100L");
		_test_parse_line("-100L");
		_test_parse_line("100l", "100L");
		_test_parse_line("+100l", "100L");
		_test_parse_line("-100l", "-100L");

		_test_parse_line("9223372036854775807L");
		_test_parse_line("9223372036854775808L", "[0, -1]: invalid long format, 9223372036854775808L", false);
		_test_parse_line("-9223372036854775808L");
		_test_parse_line("-9223372036854775809L", "[0, 21]: invalid long format, -9223372036854775809L", false);

		_test_parse_line("100D", "100.0D");
		_test_parse_line("+100D", "100.0D");
		_test_parse_line("-100D", "-100.0D");
		_test_parse_line("1.1d", "1.1D");
		_test_parse_line("+1.1d", "1.1D");
		_test_parse_line("-1.1d", "-1.1D");
	}

	@Test
	public void test_4_vars() {

		_setup();
		_test_parse_line("&var");
		_test_parse_line("& var", "& var");
	}

	@Test
	public void test_5_expr() {

		_setup();

		_test_parse_line("a::b::c", "(:: (:: a b) c)");
		_test_parse_line("obj::mbr1");
		_test_parse_line("(:: obj mbr1)");
		_test_parse_line("(+ 1 2 3)");
		_test_parse_line("(-a)");
		_test_parse_line("()");
		_test_parse_line("(())");
		_test_parse_line("(_a)");
		_test_parse_line("(a)");
		_test_parse_line("((a))");
		_test_parse_line("((a) b)");
		_test_parse_line("((&a) &b)");
		_test_parse_line("(\"abc\")");
		_test_parse_line("(?xx)");
		_test_parse_line("(setRelatedTags $NOTE $RULE $UID)");
		_test_parse_line("setRelatedTags TeX LaTex)", "token=[S:1:0:25:)], line=setRelatedTags TeX LaTex)", false);
		_test_parse_line("(add_tag_relation (CL-Operator hasSubCollection CL-ArithmeticOp))");
		_test_parse_line("(a b.c)");
		_test_parse_line("(hasSubTags BOSS-option SS1-Option-6)");
		_test_parse_line("(C&C++ a)", "(C&C++ a)", true);
		_test_parse_line("(a b )", "(a b)", true);
		_test_parse_line("(a b c)", "(a b c)", true);
		_test_parse_line("(a b-c)", "(a b-c)", true);
		_test_parse_line("(a b c) (x y z)", "(a b c) (x y z)", true);
		_test_parse_line("(a b ?)", "(a b ?)", true);
		_test_parse_line("(join (query a b ?) (query ? b d))");
		_test_parse_line("(defmacro ?x (?Team) \"a dest\" (join (x AS400 hasChild ?) (y ?Team tagAttrOf ?)))");

	}

	@Test
	public void test_5_list() {

		_setup();

		_test_parse_line("'(a b c)");
		_test_parse_line("' (a b c)", "'(a b c)");

	}

	@Test
	public void test_6_expr() {

		_setup();
		_test_parse_line("(-> mod '(a p1 c))");
		_test_parse_line("(defs a p1 c)");
		_test_parse_line("(lists a ? ?)");
		_test_parse_line("$(a b c)");
		_test_parse_line("(a ,)");
	}

	@Test
	public void test_7_comments() {

		_setup();
		_test_parse_line("(a b); this is comments", "(a b)");
	}

	@Test
	public void test_9_multi_lines() {

		_setup();
		_test_parse_line("(a b)\n(x y)", "(a b) (x y)");
		_test_parse_line("(a b);a comment line\n(x y)", "(a b) (x y)");
		_test_parse_line("(a b ;a comment line\n" + "(x y);\n" + ")", "(a b (x y))");
		_test_parse_line("(a b \n\n   )", "(a b)");

	}

	@Test
	public void test_9_multi_lines_2() {
		_setup();
		_test_parse_line(_load("result/runtime/XRParserTest/parse_in_1.txt", "utf-8"),
				_load("result/runtime/XRParserTest/parse_out_1.txt", "utf-8"));
	}

	@Test
	public void test_9_multi_lines_3() {
		_setup();
		_test_parse_line(_load("result/runtime/XRParserTest/parse_in_2.txt", "utf-8"),
				_load("result/runtime/XRParserTest/parse_out_2.txt", "utf-8"));
	}

	@Test
	public void test_9_special_escape_string() {
		_setup();
		_test_parse_line("\"\\\"abc\"");
	}

	@Test
	public void test_9_special_string() {

		_setup();
		_test_parse_line("(doSendCommand \"EVAL %%TST.\\\\\\\"%^&*~!()<>?./,{}||-=var7\\\\\\\":s 5\")");
		_test_parse_line("(expectSimpleResult \"'hello'\")");
		_test_parse_line("(defvar ?file-separatorChar \";\")");
	}

	@Test
	public void test_9_special_string_not_support() {

		_setup();
		_test_parse_line("(defvar ?file-separatorChar \"/\")");
		_test_parse_line("(defvar ?file-separatorChar \"\\\\\")");
	}

	@Test
	public void test_a_error() {

		_setup();
		_test_parse_error("(a b (x y z)", "Bad Syntax at line 0: miss match '(' found in position 0, (a b (x y z)");
		_test_parse_error("(a b c", "Bad Syntax at line 0: miss match '(' found in position 0, (a b c");
		_test_parse_error("(a b) (x", "Bad Syntax at line 0: miss match '(' found in position 6, (x");
	}

	@Test
	public void test_b_namespace() {

		_setup();
		_getParser().registerPrefix("nm", "https://github.com/to0d/nm#");
		_test_parse_line("nm:hasChild", "nm:hasChild");
		_test_parse_line("(nm:a nm:b)", "(nm:a nm:b)");
	}

	@Test
	public void test_c_number() {

		_setup();
		this._getParser().setSupportNumber(false);
		_test_parse_line("1111111111111111111111111111111");
		_test_parse_line("100");
		_test_parse_line("1.0");
		_test_parse_line("+100");
		_test_parse_line("-100");
		_test_parse_line("+1.0");
		_test_parse_line("-1.0");
		_test_parse_line("11.0e+4");

		_setup();
		_test_parse_line("100");
		_test_parse_line("1.0");
		_test_parse_line("+100", "100");
		_test_parse_line("-100");
		_test_parse_line("+1.0", "1.0");
		_test_parse_line("-1.0");
		_test_parse_line("11.0e+4");
	}

	@Test
	public void test_d_any() {
		_setup();
		_test_parse_line("&name");
		_test_parse_line("-NONE-");
		_test_parse_line("*pro*");
		_test_parse_line("*T*-2");
		_test_parse_line("(3.2Íò)");
	}

	@Test
	public void test_e_named() {

		_setup();
		_test_parse_line("name:'()");
		_test_parse_line("name:'(a b c)");
		_test_parse_line("(x name:'(a b c))");
		_test_parse_line("(add-rule m if name1:'(?x p ?y) do (-> '(?y p ?x)))");
		_test_parse_line(":'(a b c)", ":' (a b c)");
	}

	@Test
	public void test_f_array() {

		_setup();
		_test_parse_line("{a,b}", "{a,b}");
		_test_parse_line("{a b c,x y,z}", "{'(a b c),'(x y),z}");
		_test_parse_line("{'(a b c),'(x y),z}", "{'(a b c),'(x y),z}");
		_test_parse_line("{{a,b,c},{x,y},z}", "{{a,b,c},{x,y},z}");
	}

	@Test
	public void test_g_escape_string() {
		_test_parse_line("\"abc\\ndef\"", "\"abc\\\\ndef\"");
		_test_parse_line("abc\ndef", "abc def");
	}
}
