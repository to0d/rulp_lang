package alpha.rulp.ximpl.iterator;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRObjectIterator;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.lang.AbsRefObject;

public class XRObjectIteratorListWrapper extends AbsRefObject implements IRObjectIterator {

	private boolean close = false;

	private IRList list;

	private int pos = 0;

	private final int size;

	public XRObjectIteratorListWrapper(IRList list) throws RException {
		super();
		this.list = list;
		this.size = list.size();
		RulpUtil.incRef(list);
	}

	@Override
	protected void _delete() throws RException {
		if (list != null) {
			RulpUtil.decRef(list);
			list = null;
		}
		super._delete();
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
	public void close() throws RException {

		if (!close) {

			if (list != null) {
				RulpUtil.decRef(list);
				list = null;
			}

			close = true;

		}
	}

	@Override
	public RType getType() {
		return RType.ITERATOR;
	}

	@Override
	public boolean hasNext() throws RException {

		if (close) {
			return false;
		}

		return pos < size;
	}

	@Override
	public IRObject next() throws RException {

		if (close) {
			return null;
		}

		if (pos >= size) {
			throw new RException(String.format("Iterator out of space: pos=%d, size=%d", pos, size));
		}

		return list.get(pos++);
	}

	@Override
	public IRObject peek() throws RException {

		if (close) {
			return null;
		}

		return list.get(pos);
	}

}
