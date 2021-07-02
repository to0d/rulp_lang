/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRIterator;
import alpha.rulp.utils.RulpFactory;

public class XRCollection {

	private Map<String, IRObject> nameObjMap = null;

	private Set<IRObject> objectCollection = null;

	private void _add(IRObject obj) {

		if (obj.getType() == RType.ATOM) {

			if (nameObjMap == null) {
				nameObjMap = new HashMap<>();
			}

			nameObjMap.put(((IRAtom) obj).getName(), obj);

		} else {

			if (objectCollection == null) {
				objectCollection = new HashSet<>();
			}

			objectCollection.add(obj);
		}
	}

	private void _remove(IRObject obj) {

		if (obj.getType() == RType.ATOM) {

			if (nameObjMap != null) {
				nameObjMap.remove(((IRAtom) obj).getName());
			}

		} else {

			if (objectCollection != null) {
				objectCollection.add(obj);
			}
		}
	}

	public void add(IRObject obj) throws RException {

		if (obj.getType() == RType.LIST) {

			IRIterator<? extends IRObject> iter = ((IRList) obj).iterator();
			while (iter.hasNext()) {
				_add(iter.next());
			}

		} else {
			_add(obj);
		}
	}

	public boolean isEmpty() {
		return (nameObjMap == null || nameObjMap.isEmpty()) && (objectCollection == null || objectCollection.isEmpty());
	}

	public void remove(IRObject obj) throws RException {

		if (obj.getType() == RType.LIST) {

			IRIterator<? extends IRObject> iter = ((IRList) obj).iterator();
			while (iter.hasNext()) {
				_remove(iter.next());
			}

		} else {
			_remove(obj);
		}

	}

	public void retainAll(XRCollection another) {

		if (this.objectCollection != null) {
			if (another.objectCollection != null) {
				this.objectCollection.retainAll(another.objectCollection);
			} else {
				this.objectCollection = null;
			}
		}

		if (this.nameObjMap != null) {
			if (another.nameObjMap != null) {
				this.nameObjMap.keySet().retainAll(another.nameObjMap.keySet());
			} else {
				this.nameObjMap = null;
			}
		}
	}

	public IRList toList() throws RException {

		ArrayList<IRObject> rstList = new ArrayList<>();

		if (objectCollection != null) {
			for (IRObject obj : objectCollection) {
				rstList.add(obj);
			}
		}

		if (nameObjMap != null) {
			for (IRObject obj : nameObjMap.values()) {
				rstList.add(obj);
			}
		}

		return RulpFactory.createList(rstList);
	}
}
