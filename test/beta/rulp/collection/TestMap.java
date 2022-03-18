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
		_run_script();
	}

	@Test
	void test_map_2_ls() {

		_setup();
		_run_script();
	}

	@Test
	void test_map_3_property() {

		_setup();
		_run_script();
	}

	@Test
	void test_map_4_get_map() {

		_setup();

		_test("(load map)");
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
		_run_script();

	}

	@Test
	void test_map_6_size_of() {
		_setup();
		_run_script();
	}

	@Test
	void test_map_7_ref_1() {

		_setup();
		_run_script();
	}
}
