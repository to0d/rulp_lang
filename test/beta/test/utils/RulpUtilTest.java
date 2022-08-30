package beta.test.utils;

import static alpha.rulp.lang.Constant.A_ARRAY;
import static alpha.rulp.lang.Constant.A_ATOM;
import static alpha.rulp.lang.Constant.A_BLOB;
import static alpha.rulp.lang.Constant.A_BOOL;
import static alpha.rulp.lang.Constant.A_CLASS;
import static alpha.rulp.lang.Constant.A_CONST;
import static alpha.rulp.lang.Constant.A_CONSTANT;
import static alpha.rulp.lang.Constant.A_DOUBLE;
import static alpha.rulp.lang.Constant.A_EXPRESSION;
import static alpha.rulp.lang.Constant.A_FACTOR;
import static alpha.rulp.lang.Constant.A_FINAL;
import static alpha.rulp.lang.Constant.A_FLOAT;
import static alpha.rulp.lang.Constant.A_FRAME;
import static alpha.rulp.lang.Constant.A_FUNCTION;
import static alpha.rulp.lang.Constant.A_INSTANCE;
import static alpha.rulp.lang.Constant.A_INTEGER;
import static alpha.rulp.lang.Constant.A_LAMBDA;
import static alpha.rulp.lang.Constant.A_LIST;
import static alpha.rulp.lang.Constant.A_LONG;
import static alpha.rulp.lang.Constant.A_MACRO;
import static alpha.rulp.lang.Constant.A_MEMBER;
import static alpha.rulp.lang.Constant.A_NATIVE;
import static alpha.rulp.lang.Constant.A_NIL;
import static alpha.rulp.lang.Constant.A_OPT_CCO;
import static alpha.rulp.lang.Constant.A_OPT_ERO;
import static alpha.rulp.lang.Constant.A_OPT_ID;
import static alpha.rulp.lang.Constant.A_OPT_TCO;
import static alpha.rulp.lang.Constant.A_PRIVATE;
import static alpha.rulp.lang.Constant.A_PUBLIC;
import static alpha.rulp.lang.Constant.A_QUESTION_LIST;
import static alpha.rulp.lang.Constant.A_RETURN_TYPE;
import static alpha.rulp.lang.Constant.A_STABLE;
import static alpha.rulp.lang.Constant.A_STATIC;
import static alpha.rulp.lang.Constant.A_STRING;
import static alpha.rulp.lang.Constant.A_TEMPLATE;
import static alpha.rulp.lang.Constant.A_THREAD_UNSAFE;
import static alpha.rulp.lang.Constant.A_VAR;
import static alpha.rulp.lang.Constant.F_B_AND;
import static alpha.rulp.lang.Constant.F_COMPUTE;
import static alpha.rulp.lang.Constant.F_EQUAL;
import static alpha.rulp.lang.Constant.F_NOT_EQUAL;
import static alpha.rulp.lang.Constant.F_O_EQ;
import static alpha.rulp.lang.Constant.F_O_GE;
import static alpha.rulp.lang.Constant.F_O_GT;
import static alpha.rulp.lang.Constant.F_O_LE;
import static alpha.rulp.lang.Constant.F_O_LT;
import static alpha.rulp.lang.Constant.F_O_NE;
import static alpha.rulp.lang.Constant.F_RETURN;
import static alpha.rulp.lang.Constant.O_ADD;
import static alpha.rulp.lang.Constant.O_B_AND;
import static alpha.rulp.lang.Constant.O_COMPUTE;
import static alpha.rulp.lang.Constant.O_CONST;
import static alpha.rulp.lang.Constant.O_EMPTY;
import static alpha.rulp.lang.Constant.O_EQ;
import static alpha.rulp.lang.Constant.O_False;
import static alpha.rulp.lang.Constant.O_Final;
import static alpha.rulp.lang.Constant.O_LAMBDA;
import static alpha.rulp.lang.Constant.O_Nil;
import static alpha.rulp.lang.Constant.O_OPT_CCO;
import static alpha.rulp.lang.Constant.O_OPT_ERO;
import static alpha.rulp.lang.Constant.O_OPT_ID;
import static alpha.rulp.lang.Constant.O_OPT_TCO;
import static alpha.rulp.lang.Constant.O_Private;
import static alpha.rulp.lang.Constant.O_Public;
import static alpha.rulp.lang.Constant.O_QUESTION_LIST;
import static alpha.rulp.lang.Constant.O_RETURN;
import static alpha.rulp.lang.Constant.O_RETURN_TYPE;
import static alpha.rulp.lang.Constant.O_STABLE;
import static alpha.rulp.lang.Constant.O_Static;
import static alpha.rulp.lang.Constant.O_THREAD_UNSAFE;
import static alpha.rulp.lang.Constant.O_True;
import static alpha.rulp.lang.Constant.T_Array;
import static alpha.rulp.lang.Constant.T_Atom;
import static alpha.rulp.lang.Constant.T_Blob;
import static alpha.rulp.lang.Constant.T_Bool;
import static alpha.rulp.lang.Constant.T_Class;
import static alpha.rulp.lang.Constant.T_Constant;
import static alpha.rulp.lang.Constant.T_Double;
import static alpha.rulp.lang.Constant.T_Expr;
import static alpha.rulp.lang.Constant.T_Factor;
import static alpha.rulp.lang.Constant.T_Float;
import static alpha.rulp.lang.Constant.T_Frame;
import static alpha.rulp.lang.Constant.T_Func;
import static alpha.rulp.lang.Constant.T_Instance;
import static alpha.rulp.lang.Constant.T_Int;
import static alpha.rulp.lang.Constant.T_List;
import static alpha.rulp.lang.Constant.T_Long;
import static alpha.rulp.lang.Constant.T_Macro;
import static alpha.rulp.lang.Constant.T_Member;
import static alpha.rulp.lang.Constant.T_Native;
import static alpha.rulp.lang.Constant.T_String;
import static alpha.rulp.lang.Constant.T_Template;
import static alpha.rulp.lang.Constant.T_Var;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RRelationalOperator;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RulpUtil.RResultList;

public class RulpUtilTest extends RulpTestBase {

	String _test_toString(String input) throws RException {

		List<IRObject> rst = _getParser().parse(input);
		if (rst.isEmpty()) {
			return "[]";
		}

		if (rst.size() == 1) {
			return "[" + RulpUtil.toString(rst.get(0)) + "]";
		}

		return RulpUtil.toString(RulpFactory.createList(rst));
	}

	String _toLong(String input) throws RException {

		List<IRObject> rst = _getParser().parse(input);
		assertTrue(rst.size() == 1);
		return "" + RulpUtil.toLong(rst.get(0));
	}

	String _toUniqString_1(String input) throws RException {

		List<IRObject> rst = _getParser().parse(input);
		if (rst.isEmpty()) {
			return "[]";
		}

		if (rst.size() == 1) {
			return "[" + RulpUtil.toUniqString(rst.get(0)) + "]";
		}

		return RulpUtil.toUniqString(RulpFactory.createList(rst));
	}

	String _toUniqString_2(String input) throws RException, IOException {

		RResultList rstList = RulpUtil.compute(this._getInterpreter(), input);

		try {

			if (rstList.results.isEmpty()) {
				return "[]";
			}

			if (rstList.results.size() == 1) {
				return "[" + RulpUtil.toUniqString(rstList.results.get(0)) + "]";
			}

			return RulpUtil.toUniqString(RulpFactory.createList(rstList.results));

		} finally {

			rstList.free();
		}
	}

	@Test
	void test_buildListOfString_1() {

		_setup();

		try {
			List<String> list = null;
			assertEquals("'()", "" + RulpUtil.buildListOfString(list));
			assertEquals("'()", "" + RulpUtil.buildListOfString(new ArrayList<>()));
			assertEquals("'(\"a\")", "" + RulpUtil.buildListOfString(RulpUtil.toList("a")));
			assertEquals("'(\"a\" \"b\")", "" + RulpUtil.buildListOfString(RulpUtil.toList("a", "b")));
		} catch (RException e) {
			fail(e.toString());
		}
	}

	@Test
	void test_buildListOfString_2() {

		_setup();

		try {
			String[] list = null;
			String[] list2 = new String[0];
			String[] list3 = new String[2];
			list3[0] = "a";
			list3[1] = "b";

			assertEquals("'()", "" + RulpUtil.buildListOfString(list));
			assertEquals("'()", "" + RulpUtil.buildListOfString(list2));
			assertEquals("'(\"a\" \"b\")", "" + RulpUtil.buildListOfString(list3));
		} catch (RException e) {
			fail(e.toString());
		}
	}

	@Test
	void test_compare_1() {

		_setup();

		try {
			assertEquals(0, RulpUtil.compare(null, null));
			assertEquals(0, RulpUtil.compare(null, O_Nil));
			assertEquals(0, RulpUtil.compare(O_Nil, O_Nil));
			assertEquals(0, RulpUtil.compare(O_True, O_True));
		} catch (RException e) {
			fail(e.toString());
		}
	}

	@Test
	void test_isEqual_1() {

		_setup();

		assertEquals(true, RulpUtil.isEqual(RulpUtil.toList("a", "b"), RulpUtil.toList("a", "b")));
	}

	@Test
	void test_isValidRulpStmt() {

		_setup();

		assertTrue(RulpUtil.isValidRulpStmt("(a b c); comments"));
		assertTrue(RulpUtil.isValidRulpStmt("(a b c) (b c)"));
	}

	@Test
	void test_subList_1() {

		_setup();

		try {
			IRList list = RulpFactory.createList(RulpFactory.createAtom("a"), RulpFactory.createAtom("b"),
					RulpFactory.createAtom("c"));
			assertEquals("[]", "" + RulpUtil.subList(list, 0, 0));
			assertEquals("[a, b, c]", "" + RulpUtil.subList(list, 0, 3));

		} catch (RException e) {

			fail(e.toString());
		}
	}

	@Test
	void test_toArray2_1() {

		_setup();

		int nums[] = new int[2];
		nums[0] = 1;
		nums[1] = 2;

		assertEquals("[1, 2]", "" + RulpUtil.toArray2(nums));
	}

	@Test
	void test_toArray2_2() {

		_setup();

		String str[] = new String[2];
		str[0] = "a";
		str[1] = "b";

		assertEquals("[a, b]", "" + RulpUtil.toArray2(str));
	}

	@Test
	void test_toAtom_1() {

		_setup();

		assertEquals(O_Nil, RulpUtil.toAtom(null));
		assertEquals(O_EMPTY, RulpUtil.toAtom(""));
		assertEquals(T_Atom, RulpUtil.toAtom(A_ATOM));
		assertEquals(T_Bool, RulpUtil.toAtom(A_BOOL));
		assertEquals(O_CONST, RulpUtil.toAtom(A_CONST));
		assertEquals(T_Instance, RulpUtil.toAtom(A_INSTANCE));
		assertEquals(T_Expr, RulpUtil.toAtom(A_EXPRESSION));
		assertEquals(T_Factor, RulpUtil.toAtom(A_FACTOR));
		assertEquals(T_Float, RulpUtil.toAtom(A_FLOAT));
		assertEquals(T_Double, RulpUtil.toAtom(A_DOUBLE));
		assertEquals(T_Func, RulpUtil.toAtom(A_FUNCTION));
		assertEquals(T_Int, RulpUtil.toAtom(A_INTEGER));
		assertEquals(T_Long, RulpUtil.toAtom(A_LONG));
		assertEquals(T_List, RulpUtil.toAtom(A_LIST));
		assertEquals(T_Macro, RulpUtil.toAtom(A_MACRO));
		assertEquals(T_Native, RulpUtil.toAtom(A_NATIVE));
		assertEquals(O_Nil, RulpUtil.toAtom(A_NIL));
		assertEquals(T_String, RulpUtil.toAtom(A_STRING));
		assertEquals(T_Blob, RulpUtil.toAtom(A_BLOB));
		assertEquals(T_Var, RulpUtil.toAtom(A_VAR));
		assertEquals(T_Constant, RulpUtil.toAtom(A_CONSTANT));
		assertEquals(T_Class, RulpUtil.toAtom(A_CLASS));
		assertEquals(T_Member, RulpUtil.toAtom(A_MEMBER));
		assertEquals(T_Frame, RulpUtil.toAtom(A_FRAME));
		assertEquals(T_Array, RulpUtil.toAtom(A_ARRAY));
		assertEquals(T_Template, RulpUtil.toAtom(A_TEMPLATE));
		assertEquals(O_LAMBDA, RulpUtil.toAtom(A_LAMBDA));
		assertEquals(O_Final, RulpUtil.toAtom(A_FINAL));
		assertEquals(O_Static, RulpUtil.toAtom(A_STATIC));
		assertEquals(O_Private, RulpUtil.toAtom(A_PRIVATE));
		assertEquals(O_Public, RulpUtil.toAtom(A_PUBLIC));
		assertEquals(O_COMPUTE, RulpUtil.toAtom(F_COMPUTE));
		assertEquals(O_OPT_ID, RulpUtil.toAtom(A_OPT_ID));
		assertEquals(O_STABLE, RulpUtil.toAtom(A_STABLE));
		assertEquals(O_THREAD_UNSAFE, RulpUtil.toAtom(A_THREAD_UNSAFE));
		assertEquals(O_OPT_CCO, RulpUtil.toAtom(A_OPT_CCO));
		assertEquals(O_OPT_ERO, RulpUtil.toAtom(A_OPT_ERO));
		assertEquals(O_OPT_TCO, RulpUtil.toAtom(A_OPT_TCO));
		assertEquals(O_RETURN_TYPE, RulpUtil.toAtom(A_RETURN_TYPE));
		assertEquals(O_RETURN, RulpUtil.toAtom(F_RETURN));
		assertEquals(O_QUESTION_LIST, RulpUtil.toAtom(A_QUESTION_LIST));
		assertEquals(O_B_AND, RulpUtil.toAtom(F_B_AND));
		assertEquals("abc", "" + RulpUtil.toAtom("abc"));
	}

	@Test
	void test_toBoolean_1() {

		_setup();

		try {
			assertEquals(false, RulpUtil.toBoolean(O_Nil));
			assertEquals(true, RulpUtil.toBoolean(O_True));
			assertEquals(false, RulpUtil.toBoolean(O_False));
		} catch (RException e) {
			fail(e.toString());
		}

		try {
			RulpUtil.toBoolean(RulpFactory.createAtom("abc"));
			fail("should fail");
		} catch (RException e) {
			assertEquals("alpha.rulp.lang.RException: Not support type: value=abc, type=ATOM", e.toString());
		}
	}

	@Test
	void test_toDoExpr_1() {

		_setup();

		try {
			IRList list = RulpFactory.createList(RulpFactory.createExpression(RulpFactory.createAtom("a")),
					RulpFactory.createExpression(RulpFactory.createAtom("b")));
			assertEquals("(do (a) (b))", "" + RulpUtil.toDoExpr(list.iterator()));
		} catch (RException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_toDoExpr_2() {

		_setup();

		try {

			ArrayList<IRObject> list = new ArrayList<>();
			list.add(RulpFactory.createExpression(RulpFactory.createAtom("a")));
			list.add(RulpFactory.createExpression(RulpFactory.createAtom("b")));
			assertEquals("(do (a) (b))", "" + RulpUtil.toDoExpr(list.iterator()));
		} catch (RException e) {
//			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_toDoExpr_3() {

		_setup();

		try {

			ArrayList<IRObject> list = new ArrayList<>();
			list.add(RulpFactory.createExpression(RulpFactory.createAtom("a")));
			list.add(RulpFactory.createExpression(RulpFactory.createAtom("b")));
			assertEquals("(do (a) (b))", "" + RulpUtil.toDoExpr(list));
		} catch (RException e) {
//			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_toExpr_1() {

		_setup();

		try {
			ArrayList<IRObject> list = new ArrayList<>();
			list.add(RulpFactory.createAtom("a"));
			list.add(RulpFactory.createAtom("b"));
			assertEquals("(x a b)", "" + RulpUtil.toExpr(RulpFactory.createAtom("x"), list));
		} catch (RException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_toIfExpr_1() {

		_setup();

		try {

			assertEquals("(if (= nil nil) do (+ nil) (+ nil))",
					RulpUtil.toIfExpr(RulpFactory.createExpression(O_EQ, O_Nil, O_Nil),
							RulpFactory.createExpression(O_ADD, O_Nil), RulpFactory.createExpression(O_ADD, O_Nil))
							.toString());

		} catch (RException e) {
//			e.printStackTrace();
			fail(e.toString());
		}

	}

	@Test
	void test_toIfExpr_2() {

		_setup();

		try {

			assertEquals("(if (= nil nil) do (+ nil))",
					RulpUtil.toIfExpr(RulpFactory.createExpression(O_EQ, O_Nil, O_Nil),
							RulpUtil.toList(null, RulpUtil.toList(RulpFactory.createExpression(O_ADD, O_Nil))))
							.toString());

		} catch (RException e) {
//			e.printStackTrace();
			fail(e.toString());
		}

	}

	@Test
	void test_toList_1() {

		_setup();

		assertEquals("[]", RulpUtil.toList().toString());
		assertEquals("[1, 2, a]", RulpUtil.toList(1, 2, "a").toString());
	}

	@Test
	void test_toLong_1() {

		_setup();

		_test((input) -> {
			return _toLong(input);
		});

	}

	@Test
	void test_toRelationalOperator_1() {

		_setup();

		assertEquals(RRelationalOperator.EQ, RulpUtil.toRelationalOperator(F_EQUAL));
		assertEquals(RRelationalOperator.EQ, RulpUtil.toRelationalOperator(F_O_EQ));
		assertEquals(RRelationalOperator.NE, RulpUtil.toRelationalOperator(F_NOT_EQUAL));
		assertEquals(RRelationalOperator.NE, RulpUtil.toRelationalOperator(F_O_NE));
		assertEquals(RRelationalOperator.GE, RulpUtil.toRelationalOperator(F_O_GE));
		assertEquals(RRelationalOperator.GT, RulpUtil.toRelationalOperator(F_O_GT));
		assertEquals(RRelationalOperator.LE, RulpUtil.toRelationalOperator(F_O_LE));
		assertEquals(RRelationalOperator.LT, RulpUtil.toRelationalOperator(F_O_LT));
		assertEquals(null, RulpUtil.toRelationalOperator(""));
	}

	@Test
	void test_toString_1() {

		_setup();

		_test((input) -> {
			return _test_toString(input);
		});
	}

	@Test
	void test_toString_2() {

		_setup();

		_getParser().registerPrefix("nm", "https://github.com/to0d/nm#");

		_test((input) -> {
			return _test_toString(input);
		});

	}

	@Test
	void test_toString_3() {

		_setup();

		try {
			IRList list = RulpFactory.createList(RulpFactory.createAtom("a"), RulpFactory.createAtom("b"));
			assertEquals("a b", RulpUtil.toString(list.iterator()));
		} catch (RException e) {
//			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_toStringList_1() {

		_setup();

		try {
			ArrayList<IRObject> list = new ArrayList<>();
			list.add(RulpFactory.createAtom("a"));
			list.add(RulpFactory.createString("b"));
			assertEquals("[a, b]", "" + RulpUtil.toStringList(list));
		} catch (RException e) {
//			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_toStringList_2() {

		_setup();

		try {
			ArrayList<IRObject> list = new ArrayList<>();
			list.add(RulpFactory.createInteger(1));
			RulpUtil.toStringList(list);
			fail("should fail");
		} catch (RException e) {
//			e.printStackTrace();
			assertEquals("alpha.rulp.lang.RException: Can't conver to string list: 1", e.toString());
		}
	}

	@Test
	void test_toUniqString_1() {

		_setup();

		try {
			assertEquals("$$null", RulpUtil.toUniqString(null));
		} catch (RException e) {
			fail(e.toString());
		}
	}

	@Test
	void test_toUniqString_2() {

		_setup();

		_test((input) -> {
			return _toUniqString_1(input);
		});

	}

	@Test
	void test_toUniqString_3() {

		_setup();

		_test((input) -> {
			return _toUniqString_2(input);
		});

	}
}
