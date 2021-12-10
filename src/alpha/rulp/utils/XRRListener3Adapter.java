package alpha.rulp.utils;

import java.util.LinkedList;
import java.util.List;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRListener3;

public class XRRListener3Adapter<T1 extends IRObject, T2 extends IRObject, T3 extends IRObject>
		implements IRListener3<T1, T2, T3> {

	private List<IRListener3<T1, T2, T3>> listenerList = new LinkedList<>();

	public void addListener(IRListener3<T1, T2, T3> listener) {
		listenerList.add(listener);
	}

	@Override
	public void doAction(T1 o1, T2 o2, T3 o3) throws RException {
		for (IRListener3<T1, T2, T3> listener : listenerList) {
			listener.doAction(o1, o2, o3);
		}
	}

}
