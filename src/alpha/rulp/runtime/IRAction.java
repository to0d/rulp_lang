package alpha.rulp.runtime;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;

public interface IRAction<T extends IRObject> {
	public T doAction(T obj) throws RException;
}
