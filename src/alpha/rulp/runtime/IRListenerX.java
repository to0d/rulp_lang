package alpha.rulp.runtime;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;

public interface IRListenerX<T extends IRObject> {
	public void doAction(T[] objs) throws RException;
}