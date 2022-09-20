package alpha.rulp.ximpl.iterator;

import alpha.rulp.lang.IRObjectIterator;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.lang.AbsRefObject;

public abstract class AbsRefObjectIterator extends AbsRefObject implements IRObjectIterator {

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

}
