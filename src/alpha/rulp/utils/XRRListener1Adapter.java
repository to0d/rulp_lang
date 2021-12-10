package alpha.rulp.utils;

import java.util.LinkedList;
import java.util.List;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRListener1;

public class XRRListener1Adapter<T extends IRObject> implements IRListener1<T> {

	private List<IRListener1<T>> listenerList = new LinkedList<>();

	public void addListener(IRListener1<T> listener) {
		listenerList.add(listener);
	}

	public void removeListener(IRListener1<T> listener) {
		listenerList.remove(listener);
	}

	@Override
	public void doAction(T obj) throws RException {
		for (IRListener1<T> listener : listenerList) {
			listener.doAction(obj);
		}
	}

}
