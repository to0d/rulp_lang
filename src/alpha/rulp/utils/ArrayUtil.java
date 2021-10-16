package alpha.rulp.utils;

import alpha.rulp.lang.RException;

public class ArrayUtil {

	public static int getArrayIndex(int sizes[], int[] indexs) throws RException {

		int indexCount = indexs.length;
		if (indexCount != sizes.length) {
			throw new RException(String.format("Invalid length<%d>: expect=%d, indexs=%s", indexCount, sizes.length,
					RulpUtil.toArray2(indexs)));
		}

		int arrayIndex = 0;

		for (int i = 0; i < indexs.length; ++i) {

			int index = indexs[i];
			int size = sizes[i];

			if (index < 0 || index >= size) {
				throw new RException(
						String.format("Invalid index<%d>: dim=%d, indexs=%s", index, i, RulpUtil.toArray2(indexs)));
			}

			arrayIndex *= size;
			arrayIndex += index;
		}

		return arrayIndex;
	}
}
