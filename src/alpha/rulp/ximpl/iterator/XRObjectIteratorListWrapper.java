package alpha.rulp.ximpl.iterator;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRObjectIterator;
import alpha.rulp.lang.RException;
import alpha.rulp.utils.RulpUtil;

public class XRObjectIteratorListWrapper extends AbsRefObjectIterator implements IRObjectIterator {

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
	public boolean hasNext() throws RException {
		return pos < size;
	}

	@Override
	public IRObject next() throws RException {
		if (pos >= size) {
			throw new RException(String.format("Iterator out of space: pos=%d, size=%d", pos, size));
		}

		return list.get(pos++);
	}

	@Override
	public IRObject peek() throws RException {
		return list.get(pos);
	}

}
