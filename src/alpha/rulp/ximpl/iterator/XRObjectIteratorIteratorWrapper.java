package alpha.rulp.ximpl.iterator;

import java.util.Iterator;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRObjectIterator;
import alpha.rulp.lang.RException;

public class XRObjectIteratorIteratorWrapper extends AbsRefObjectIterator implements IRObjectIterator {

	private Iterator<? extends IRObject> iterator;

	private IRObject topObject;

	public XRObjectIteratorIteratorWrapper(Iterator<? extends IRObject> iterator) {
		super();
		this.iterator = iterator;
	}

	@Override
	protected void _close() throws RException {
		topObject = null;
		iterator = null;
	}

	@Override
	protected boolean _hasNext() throws RException {
		return topObject != null || iterator.hasNext();
	}

	@Override
	protected IRObject _next() throws RException {
		if (topObject != null) {
			return topObject;
		}

		return iterator.next();
	}

	@Override
	protected IRObject _peek() throws RException {
		if (topObject == null) {
			if (iterator.hasNext()) {
				topObject = iterator.next();
			}
		}

		return topObject;
	}

}
