package alpha.rulp.ximpl.iterator;

import java.util.Iterator;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRObjectIterator;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.lang.AbsRefObject;

public class XRObjectIteratorIteratorWrapper extends AbsRefObject implements IRObjectIterator {

	private Iterator<? extends IRObject> iterator;

	private IRObject topObject;

	private boolean close = false;

	public XRObjectIteratorIteratorWrapper(Iterator<? extends IRObject> iterator) {
		super();
		this.iterator = iterator;
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

		if (close) {
			return false;
		}

		return topObject != null || iterator.hasNext();
	}

	@Override
	public IRObject next() throws RException {

		if (close) {
			return null;
		}

		if (topObject != null) {
			return topObject;
		}

		return iterator.next();
	}

	@Override
	public IRObject peek() throws RException {

		if (close) {
			return null;
		}

		if (topObject == null) {
			if (iterator.hasNext()) {
				topObject = iterator.next();
			}
		}

		return topObject;
	}

	@Override
	public void close() throws RException {
		topObject = null;
		iterator = null;
		close = true;
	}

}
