package alpha.rulp.ximpl.array;

import static alpha.rulp.lang.Constant.MAX_TOSTRING_LEN;
import static alpha.rulp.lang.Constant.O_Nil;

import java.util.ArrayList;
import java.util.List;

import alpha.rulp.lang.IRArray;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.lang.AbsRefObject;

public class XRArrayVary extends AbsRefObject implements IRArray {

	public static XRArrayVary build(int... sizes) throws RException {

		int dimension = sizes.length;

		if (dimension < 1 || dimension > 2) {
			throw new RException("support dimension: " + dimension);
		}

		XRArrayVary array = new XRArrayVary();
		array.arrayDimension = dimension;
		array.arraySize = new int[dimension];

		for (int i = 0; i < dimension; ++i) {
			array.arraySize[i] = sizes[i];
		}

		if (dimension == 2) {
			array.arraySize[0] = 0;
			int size1 = sizes[1];
			for (int i = 0; i < sizes[0]; ++i) {
				array.add(build(size1));
			}
		}

		return array;
	}

	protected int arrayDimension = 0;

	protected int arraySize[];

	protected int elementCount = 0;

	protected List<IRObject> elements = new ArrayList<>();

	private XRArrayVary() {

	}

	protected void _add(IRObject obj) throws RException {

		elements.add(obj);
		arraySize[0]++;

		if (obj != null && obj.getType() != RType.NIL) {
			elementCount++;
			RulpUtil.incRef(obj);
		}
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

	private void _set(int index, IRObject newObj) {

		for (int i = elements.size(); i <= index; ++i) {
			elements.add(null);
		}

		elements.set(index, newObj);
	}

	@Override
	public void add(IRObject obj) throws RException {

		if (obj == null || obj.getType() == RType.NIL) {
			_add(obj);
			return;
		}

		if (obj.getType() != RType.ARRAY) {
			_add(obj);
			return;
		}

		IRArray ei = RulpUtil.asArray(obj);
		if (ei.getDimension() != 1) {
			throw new RException("Invalid element: " + obj);
		}

//		if (ei.getElementCount() == 0) {
//			return;
//		}

		if (this.arrayDimension > 2) {
			throw new RException("Invalid array: " + this);
		}

		if (this.arrayDimension == 1) {
			int size = this.arraySize[0];
			this.arraySize = new int[2];
			this.arraySize[0] = size;
			this.arraySize[1] = 0;
			this.arrayDimension = 2;
		}

		_add(ei);

		int elementSize = ei.size();
		if (this.arraySize[1] < elementSize) {
			this.arraySize[1] = elementSize;
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
	public IRArray cloneArray() {

		XRArrayVary newArray = new XRArrayVary();

		newArray.arrayDimension = arrayDimension;
		newArray.elementCount = elementCount;
		newArray.elements = new ArrayList<>(elements);

		newArray.arraySize = new int[arraySize.length];
		for (int i = 0; i < arraySize.length; ++i) {
			newArray.arraySize[i] = arraySize[i];
		}

		return newArray;
	}

	@Override
	public IRObject get(int... indexs) throws RException {

		if (indexs.length == 1) {

			int index = indexs[0];
			if (index < 0 || index >= size()) {
				throw new RException("Invalid index: " + index);
			}

			return (elements != null && index < elements.size()) ? elements.get(index) : null;

		} else {

			return _get(this, indexs, 0);
		}

	}

	@Override
	public IRObject get(int index) throws RException {
		return index < elements.size() ? elements.get(index) : O_Nil;
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
		return false;
	}

	@Override
	public boolean isEmpty() throws RException {
		return arrayDimension == 0 || size() == 0;
	}

	@Override
	public void set(int index, IRObject newObj) throws RException {

		if (index < 0 || index >= size()) {
			throw new RException("invalid index: " + index);
		}

		IRObject oldObj = get(index);
		int on = oldObj == null ? 0 : 1;
		int nn = 0;

		if (newObj != null && newObj.getType() != RType.NIL) {
			nn = 1;
			RulpUtil.incRef(newObj);
		}

		_set(index, nn == 1 ? newObj : null);
		elementCount += nn - on;

		RulpUtil.decRef(oldObj);
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
