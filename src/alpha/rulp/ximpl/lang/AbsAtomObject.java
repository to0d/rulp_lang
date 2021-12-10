package alpha.rulp.ximpl.lang;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRListener1;

public abstract class AbsAtomObject implements IRObject {

	@Override
	public void addObjectDeletedListener(IRListener1<IRObject> listener) {

	}

	@Override
	public void decRef() throws RException {

	}

	@Override
	public int getMaxRef() {
		return 0;
	}

	@Override
	public int getRef() {
		return 0;
	}

	@Override
	public void incRef() throws RException {
	}

	@Override
	public boolean isDeleted() {
		return false;
	}

	@Override
	public String toString() {
		return asString();
	}
}
