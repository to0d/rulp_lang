package beta.rulp.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.IRMap;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;

public class TestMap extends RulpTestBase {

	@Test
	void test_map_1() {

		_setup();
		_test("(new map map1)", "map1");
		_test("(map1::size-of)", "0");

		_test("(map1::put 1 2)", "nil");
		_test("(map1::size-of)", "1");
		_test("(map1::get 1)", "2");
		_test("(map1::get 2)", "nil");

		_test("(map1::put a 2)", "nil");
		_test("(map1::size-of)", "2");
		_test("(map1::get a)", "2");
		_test("(map1::get b)", "nil");

		_test("(map1::put \"str1\" 2)", "nil");
		_test("(map1::size-of)", "3");
		_test("(map1::get \"str1\")", "2");
		_test("(map1::get \"str2\")", "nil");

		_test("(map1::put '(a) 2)", "nil");
		_test("(map1::size-of)", "4");
		_test("(map1::get '(a))", "2");
		_test("(map1::get '(b))", "nil");
	}

	@Test
	void test_map_2() {

		_setup();
		_test("(ls map)",
				"'(map::?impl map::_map_clear map::_map_get map::_map_init map::_map_is_empty map::_map_put map::_map_size_of map::clear map::get map::init map::is-empty map::put map::size-of)");

		_test("(new map map1)", "map1");
		_test("(ls map1)", "'(map1::?impl map1::init map1::this)");

		_test("(map1::put 1 2)", "nil");
		_test("(ls map1)", "'(map1::?impl map1::init map1::put map1::this)");

		_test("(ls-print map)", "nil", _load("result/collection/TestMap/map_ls_print.txt") + "\n");
	}

	@Test
	void test_map_3_property() {

		_setup();
		_test("(property-of map)", "'(final)");
	}

	@Test
	void test_map_4_get_map() {

		_setup();
		_test("(new map map1)", "map1");
		_test("(map1::put 1 2)", "nil");

		try {
			IRObject obj = this._getInterpreter().getObject("map1");
			assertNotNull(obj);

			IRMap map1 = RulpUtil.toImplMap(RulpUtil.asInstance(obj));
			assertNotNull(map1);

			assertEquals(1, map1.size());
			assertEquals("2", "" + map1.get(RulpFactory.createInteger(1)));

		} catch (RException | IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	void test_map_5_clear_and_is_empty() {

		_setup();
		_test("(new map map1)", "map1");
		_test("(map1::is-empty)", "true");
		_test("(map1::put 1 2)", "nil");
		_test("(is-empty map1)", "false");
		_test("(map1::clear)", "nil");
		_test("(clear map1)", "nil");
		_test("(is-empty map1)", "true");

	}
}
