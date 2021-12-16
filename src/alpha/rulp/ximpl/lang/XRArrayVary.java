package alpha.rulp.ximpl.lang;

import static alpha.rulp.lang.Constant.MAX_TOSTRING_LEN;

import java.util.ArrayList;
import java.util.List;

import alpha.rulp.lang.IRArray;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.RulpUtil;

public class XRArrayVary extends AbsRefObject implements IRArray {

	public static XRArrayVary build() throws RException {

		XRArrayVary array = new XRArrayVary();
		array.elementCount = 0;
		array.arrayDimension = 1;
		array.arraySize = new int[1];
		array.arraySize[0] = 0;

		return array;
	}

	public static XRArrayVary build(int[] sizes) throws RException {

		int dimension = sizes.length;

		if (dimension < 1 || dimension > 2) {
			throw new RException("support dimension: " + dimension);
		}

		XRArrayVary array = new XRArrayVary();
		array.elementCount = 0;
		array.arrayDimension = dimension;
		array.arraySize = new int[dimension];

		for (int i = 0; i < dimension; ++i) {

			int size = sizes[i];
			if (size < 1) {
				throw new RException("support size: " + size);
			}

			array.arraySize[i] = size;
		}

		return array;
	}

	public static XRArrayVary build(List<? extends IRObject> elements) throws RException {

		XRArrayVary array = new XRArrayVary();
		array.elementCount = 0;
		array.arrayDimension = 1;
		array.arraySize = new int[1];
		array.arraySize[0] = 0;

		for (IRObject e : elements) {
			array.add(e);
		}

		return array;
	}

	protected int arrayDimension;

	protected int arraySize[];

	protected int elementCount;

	protected List<IRObject> elements = new ArrayList<>();

	public XRArrayVary() {

		super();

		arrayDimension = 0;
		elementCount = 0;
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

		if (ei.getElementCount() == 0) {
			return;
		}

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

		IRObject oldObj = elements.get(index);
		int on = oldObj == null ? 0 : 1;
		int nn = 0;

		if (newObj != null && newObj.getType() != RType.NIL) {
			nn = 1;
			RulpUtil.incRef(newObj);
		}

		elements.set(index, nn == 1 ? newObj : null);
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
