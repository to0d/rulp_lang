package alpha.rulp.ximpl.array;

import static alpha.rulp.lang.Constant.MAX_TOSTRING_LEN;

import java.util.List;

import alpha.rulp.lang.IRArray;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.lang.AbsRefObject;

public class XRArrayConst extends AbsRefObject implements IRArray {

	public static XRArrayConst build(List<? extends IRObject> elements) throws RException {

		int dimension = 1;
		for (IRObject element : elements) {
			if (element.getType() == RType.ARRAY) {
				IRArray elementArray = RulpUtil.asArray(element);
				if (elementArray.getDimension() >= dimension) {
					dimension = elementArray.getDimension() + 1;
				}
			}
		}

		if (dimension < 1 || dimension > 2) {
			throw new RException("support dimension: " + dimension);
		}

		XRArrayConst array = new XRArrayConst();
		array.elementCount = 0;
		array.arrayDimension = dimension;
		array.arraySize = new int[dimension];

		for (int i = 0; i < dimension; ++i) {
			array.arraySize[i] = 0;
		}

		int size0 = elements.size();
		array.arraySize[0] = size0;
		array.elements = new IRObject[size0];

		int i = 0;
		for (IRObject element : elements) {

			array.elements[i++] = element;

			if (element != null && element.getType() != RType.NIL) {

				array.elementCount++;
				RulpUtil.incRef(element);

				if (element.getType() == RType.ARRAY) {
					IRArray elementArray = RulpUtil.asArray(element);
					int size1 = elementArray.size();
					if (size1 > array.arraySize[1]) {
						array.arraySize[1] = size1;
					}
				}
			}

		}

		return array;
	}

	protected int arrayDimension = 0;

	protected int arraySize[];

	protected int elementCount;

	protected IRObject[] elements;

	private XRArrayConst() {

	}

	@Override
	protected void _delete() throws RException {

		if (elements != null) {
			for (IRObject e : elements) {
				RulpUtil.decRef(e);
			}
			elements = null;
		}

		super._delete();
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
		throw new RException("not support");
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
	public IRArray cloneArray() {

		XRArrayConst newArray = new XRArrayConst();

		newArray.arrayDimension = arrayDimension;
		newArray.elementCount = elementCount;
		
		newArray.elements = new IRObject[elements.length];
		for (int i = 0; i < elements.length; ++i) {
			newArray.elements[i] = elements[i];
		}

		newArray.arraySize = new int[arraySize.length];
		for (int i = 0; i < arraySize.length; ++i) {
			newArray.arraySize[i] = arraySize[i];
		}

		return newArray;
	}

	@Override
	public IRObject get(int index) throws RException {
		return elements == null ? null : elements[index];
	}

	@Override
	public IRObject get(int... indexs) throws RException {

		if (indexs.length == 1) {

			int index = indexs[0];
			if (index < 0 || index >= size()) {
				throw new RException("Invalid index: " + index);
			}

			return (elements != null && index < elements.length) ? elements[index] : null;

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
	public boolean isConst() {
		return true;
	}

	@Override
	public boolean isEmpty() throws RException {
		return arrayDimension == 0 || size() == 0;
	}

	@Override
	public void set(int index, IRObject newObj) throws RException {
		throw new RException("not support");
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
