package alpha.rulp.ximpl.lang;

import java.util.ArrayList;

import alpha.rulp.lang.IRExpr;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpUtil;

public abstract class AbsRListIterator extends AbsRList implements IRList, IRExpr {

	protected ArrayList<IRObject> elements = null;

	protected int size = -1;

	public AbsRListIterator(RType type, String name) {
		super(type, name);
	}

	@Override
	protected void _delete() throws RException {
		if (elements != null) {
			for (IRObject e : elements) {
				if (e != null) {
					RulpUtil.decRef(e);
				}
			}
//			elements = null;
		}
		super._delete();
	}

	protected abstract boolean _scanTo(int toIndex) throws RException;

	@Override
	public IRObject get(int index) throws RException {

		_scanTo(index);

		if (size == -1) {
			return elements.get(index);
		}

		return (size == -1 || index < size) ? elements.get(index) : null;
	}

	@Override
	public boolean isEarly() {
		return false;
	}

	@Override
	public boolean isEmpty() throws RException {
		_scanTo(0);
		return size == 0;
	}

	@Override
	public IRIterator<IRObject> listIterator(int fromIndex) {

		return new IRIterator<IRObject>() {

			int index = fromIndex;

			@Override
			public boolean hasNext() throws RException {
				return _scanTo(index);
			}

			@Override
			public IRObject next() throws RException {
				return get(index++);
			}
		};
	}
}
