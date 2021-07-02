package alpha.rulp.ximpl.lang;

import static alpha.rulp.lang.Constant.MAX_TOSTRING_LEN;

import java.util.List;

import alpha.rulp.lang.IRArray;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.ArrayUtil;
import alpha.rulp.utils.RulpUtil;

public class XRArray extends AbsAtomObject implements IRArray {

	static final XRArray emptyArray = new XRArray();

	public static XRArray buildArray(List<? extends IRObject> elements) {

		int size1 = elements.size();
		while (size1 > 0) {
			if (elements.get(size1 - 1) == null) {
				size1--;
			} else {
				break;
			}
		}

		if (size1 == 0) {
			return emptyArray;
		}

		XRArray array = new XRArray();
		array.arrayDimension = 1;
		array.arraySize = new int[1];
		array.arraySize[0] = size1;
		array.arrayElements = new IRObject[size1];
		array.elementSize = size1;
		array.elementCapacity = size1;

		for (int i = 0; i < size1; ++i) {
			array.arrayElements[i] = elements.get(i);
		}

		return array;
	}

	public static XRArray buildArray2(List<IRObject> elements) {

//		XRArray array = new XRArray();
//		array.arrayDimension = size.length;
//		array.arraySize = new int[array.arrayDimension];
//		for (int i = 0; i < array.arrayDimension; ++i) {
//
//		}

		return null;
	}

//	public static int visitArray(List<IRObject> elements, int level) {
//
//	}

	private int arrayDimension;

	private IRObject arrayElements[];

	private int arraySize[];

	private int elementSize;

	private int elementCapacity;

	public XRArray() {
		super();

		arrayDimension = 0;
		elementSize = 0;
		elementCapacity = 0;
	}

	@Override
	public String asString() {

		try {
			return RulpUtil.toString(this);
		} catch (RException e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	@Override
	public int capacity() {
		return elementCapacity;
	}

	@Override
	public int dimension() throws RException {
		return arrayDimension;
	}

	@Override
	public IRObject get(int... indexs) throws RException {

		int arrayIndex = ArrayUtil.getArrayIndex(arraySize, indexs);

		return arrayElements[arrayIndex];
	}

	@Override
	public RType getType() {
		return RType.ARRAY;
	}

	@Override
	public boolean isEmpty() throws RException {
		return arrayDimension == 0 || size() == 0;
	}

	@Override
	public int size() throws RException {
		return elementSize;
	}

	@Override
	public int size(int dim) throws RException {

		if (dim < 0) {
			throw new RException("Invalid dim: " + dim);
		}

		if (dim < arrayDimension) {
			return arraySize[dim];
		}

		return 0;
	}

	@Override
	public String toString() {

		try {
			return RulpUtil.toString(this, MAX_TOSTRING_LEN);
		} catch (RException e) {
			e.printStackTrace();
			return e.toString();
		}
	}

}
