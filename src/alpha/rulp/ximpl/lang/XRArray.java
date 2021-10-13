package alpha.rulp.ximpl.lang;

import static alpha.rulp.lang.Constant.MAX_TOSTRING_LEN;
import static alpha.rulp.lang.Constant.O_Nil;

import java.util.ArrayList;
import java.util.List;

import alpha.rulp.lang.IRArray;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.RulpUtil;

public class XRArray extends AbsAtomObject implements IRArray {

//	static final XRArray emptyArray = new XRArray();

	public static XRArray buildArray(List<? extends IRObject> elements) throws RException {

		int size = elements.size();

		XRArray array = new XRArray();
		array.elementCount = 0;
		array.arrayDimension = 1;
		array.arraySize = new int[1];
		array.arraySize[0] = size;

		int elementArraySize = -1;

		NEXT: for (int i = 0; i < size; ++i) {

			IRObject ei = elements.get(i);
			if (ei == null || ei == O_Nil) {
				continue;
			}

			array.elements.add(ei);
			array.elementCount++;

			if (ei.getType() == RType.ARRAY) {

				IRArray arrayi = RulpUtil.asArray(ei);
				if (arrayi.getDimension() != 1) {
					throw new RException("not support array: " + ei);
				}

				if (array.getElementCount() == 0) {
					array.elements.set(i, null);
					array.elementCount--;
					continue NEXT;
				}

				int elementSize = arrayi.size();
				if (elementArraySize < elementSize) {
					elementArraySize = elementSize;
				}

				array.elementCount += arrayi.getElementCount() - 1;
			}

		}

		if (elementArraySize != -1) {

			array.arrayDimension = 2;
			array.arraySize = new int[2];
			array.arraySize[0] = size;
			array.arraySize[1] = elementArraySize;
		}

		return array;
	}

	protected int arrayDimension;

	protected int arraySize[];

	protected int elementCount;

	protected List<IRObject> elements = new ArrayList<>();

	public XRArray() {

		super();

		arrayDimension = 0;
		elementCount = 0;
	}

	protected IRObject _get(IRObject arrayObj, int[] indexs, final int from) throws RException {

		if (from == indexs.length) {
			return arrayObj;
		}

		int index = indexs[from];
		if (index < 0 || index >= arraySize[from]) {
			throw new RException("Invalid index: " + index);
		}

		if (arrayObj == null) {
			return null;
		}

		if (arrayObj.getType() != RType.ARRAY) {
			return index == 0 ? arrayObj : null;
		}

		IRArray array = RulpUtil.asArray(arrayObj);
		IRObject obj = null;
		if (index < array.size()) {
			obj = array.get(index);
		}

		return _get(obj, indexs, from + 1);
	}

	@Override
	public void add(IRObject obj) throws RException {

		if (obj.getType() == RType.ARRAY || arrayDimension != 1) {
			throw new RException("Invalid element: " + obj);
		}

		arraySize[0]++;

		if (obj == null || obj.getType() == RType.NIL) {
			elements.add(null);
		} else {
			elements.add(obj);
			elementCount++;
		}
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
	public IRObject get(int... indexs) throws RException {

		if (indexs.length == 1) {

			int index = indexs[0];
			if (index < 0 || index >= size()) {
				throw new RException("Invalid index: " + index);
			}

			return index < elements.size() ? elements.get(index) : null;

		} else {

			return _get(this, indexs, 0);
		}

	}

	@Override
	public int getDimension() throws RException {
		return arrayDimension;
	}

	@Override
	public int getElementCount() {
		return elementCount;
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
	public void set(int index, IRObject obj) throws RException {

		if (index < 0 || index >= size()) {
			throw new RException("invalid index: " + index);
		}

		int on = elements.get(index) == null ? 0 : 1;
		int nn = 0;

		if (obj != null && obj.getType() != RType.NIL) {
			nn = 1;
		}

		elements.set(index, nn == 1 ? obj : null);
		elementCount += nn - on;
	}

	@Override
	public int size() throws RException {
		return arraySize[0];
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
