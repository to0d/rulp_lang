package alpha.rulp.ximpl.iterator;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRObjectIterator;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.lang.AbsRefObject;

public abstract class AbsRefObjectIterator extends AbsRefObject implements IRObjectIterator {

	protected boolean close = false;

	protected abstract void _close() throws RException;

	protected abstract boolean _hasNext() throws RException;

	protected abstract IRObject _next() throws RException;

	protected abstract IRObject _peek() throws RException ;
	
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
			_close();
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

		return _hasNext();
	}
	
	@Override
	public IRObject next() throws RException {

		if (close) {
			return null;
		}

		return _next();
	}

	@Override
	public IRObject peek() throws RException {

		if (close) {
			return null;
		}

		return _peek();
	}

}
