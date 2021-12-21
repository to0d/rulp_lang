/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.lang;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRListener1;

public abstract class AbsRefObject extends AbsObject implements IRObject {

	protected int max_ref = 0;

	protected List<IRListener1<IRObject>> objDeletedListenerList = null;

	protected int ref = 0;

	protected void _delete() throws RException {

		if (ref != 0) {
			throw new RException(String.format("Can't delete: %s, ref=%d", this, ref));
		}

		if (objDeletedListenerList != null) {

			ArrayList<IRListener1<IRObject>> _tmpListeners = new ArrayList<>(objDeletedListenerList);
			objDeletedListenerList = null;

			for (IRListener1<IRObject> listener : _tmpListeners) {
				listener.doAction(this);
			}

		}

		ref = -1;
	}

	@Override
	public void addObjectDeletedListener(IRListener1<IRObject> listener) {

		if (objDeletedListenerList == null) {
			objDeletedListenerList = new LinkedList<>();
		}

		if (!objDeletedListenerList.contains(listener)) {
			objDeletedListenerList.add(listener);
		}
	}

	@Override
	public void decRef() throws RException {

		if (ref <= 0) {
			throw new RException(String.format("Can't decRef: %s, ref=%d", this, ref));
		}

		if (--ref == 0) {
			getType().incDeleteCount();
			_delete();
		}
	}

	@Override
	public int getMaxRef() {
		return max_ref;
	}

	@Override
	public int getRef() {
		return ref;
	}

	@Override
	public void incRef() throws RException {

		if (ref < 0) {
			throw new RException(String.format("Can't incRef: %s, ref=%d", this, ref));
		}

		if (++ref > max_ref) {
			max_ref = ref;
		}
	}

	@Override
	public boolean isDeleted() {
		return ref < 0;
	}

}
