package alpha.rulp.ximpl.runtime;

import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.utils.RulpFactory;

public class XRFrameProtected extends XRFrame {

	@Override
	protected void _delete() throws RException {
		throw new RException("unable to destructor this frame");
	}

	@Override
	public IRFrameEntry createFrameEntry(String name, IRObject object) {
		return RulpFactory.createFrameEntryProtected(this, name, object);
	}

	@Override
	public void decRef() throws RException {

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
	public IRFrameEntry removeEntry(String name) throws RException {
		throw new RException("unable to remove object in this frame: " + name);
	}
}
