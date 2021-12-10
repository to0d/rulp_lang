package alpha.rulp.runtime;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;

public interface IRListener1<T extends IRObject> {
	public void doAction(T obj) throws RException;
}
