package alpha.rulp.ximpl.lang;

import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRObjectIterator;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.RulpUtil;

public class XRObjectIteratorListWrapper extends AbsRefObject implements IRObjectIterator {

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
	public RType getType() {
		return RType.ITERATOR;
	}

	@Override
	public boolean hasNext() throws RException {
		return pos < size;
	}

	@Override
	public IRObject next() throws RException {
		return list.get(pos++);
	}

	@Override
	public IRObject peek() throws RException {
		return list.get(pos);
	}

}
