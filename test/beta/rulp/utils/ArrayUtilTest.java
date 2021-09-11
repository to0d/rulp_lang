package beta.rulp.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.RException;
import alpha.rulp.utils.ArrayUtil;

class ArrayUtilTest {

	void _getArrayIndex(int sizes[], int[] indexs, int expectArrayIndex) {

		try {
			int arrayIndex = ArrayUtil.getArrayIndex(sizes, indexs);
			assertEquals(expectArrayIndex, arrayIndex);
		} catch (RException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	void _getArrayIndex_error(int sizes[], int[] indexs, String expectError) {

		try {
			ArrayUtil.getArrayIndex(sizes, indexs);
			fail("Should fail");
		} catch (RException e) {
			assertEquals(expectError, e.getMessage());
		}
	}

	@Test
	void test_1() {

		int size2_1[] = { 2, 3 };
		int size3_1[] = { 2, 2, 3 };

		int index2_1[] = { 1, 2 };
		int index3_1[] = { 1, 1, 2 };
		int index3_2[] = { 1, 1, 3 };

		_getArrayIndex(size3_1, index3_1, 11);
		_getArrayIndex(size2_1, index2_1, 5);

		_getArrayIndex_error(size3_1, index2_1, "Invalid length<2>: expect=3, indexs=[1, 2]");
		_getArrayIndex_error(size3_1, index3_2, "Invalid index<3>: dim=2, indexs=[1, 1, 3]");

	}

}
