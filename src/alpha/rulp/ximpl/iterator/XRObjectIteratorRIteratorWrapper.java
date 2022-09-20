package alpha.rulp.ximpl.iterator;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRObjectIterator;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRIterator;

public class XRObjectIteratorRIteratorWrapper extends AbsRefObjectIterator implements IRObjectIterator {

	private IRIterator<? extends IRObject> iterator;

	private IRObject topObject;

	public XRObjectIteratorRIteratorWrapper(IRIterator<? extends IRObject> iterator) {
		super();
		this.iterator = iterator;
	}

	@Override
	public boolean hasNext() throws RException {
		return topObject != null || iterator.hasNext();
	}

	@Override
	public IRObject next() throws RException {

		if (topObject != null) {
			return topObject;
		}

		return iterator.next();
	}

	@Override
	public IRObject peek() throws RException {

		if (topObject == null) {
			if (iterator.hasNext()) {
				topObject = iterator.next();
			}
		}

		return topObject;
	}
}
