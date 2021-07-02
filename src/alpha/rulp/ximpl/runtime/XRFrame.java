/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime;

import static alpha.rulp.lang.Constant.A_LOCAL;
import static alpha.rulp.lang.Constant.A_PARENT;
import static alpha.rulp.lang.Constant.I_FRAME_NULL_ID;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRListener;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.IRVar;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRThreadContext;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.lang.AbsRefObject;

public class XRFrame extends AbsRefObject implements IRFrame {

	static class EntryNode {
		IRFrameEntry entry = null;
		boolean isLocal = false;
	}

	protected List<IRFrameEntry> _entryCacheList = null;

	protected int _frameLevel = -1;

	protected Map<String, EntryNode> _nodeCatchMap = null;

	protected IRSubject _subject = null;

	protected boolean bFinal = false;

	protected Map<String, IRFrameEntry> entryMap = null;

	protected int frameId;

	protected String frameName;

	protected List<IRListener<IRFrame>> frameReleaeListenerList = null;

	protected IRFrame parentFrame = null;

	protected IRThreadContext threadContext;

	@Override
	protected void _delete() throws RException {

		if (entryMap != null) {

			for (IRFrameEntry entry : entryMap.values()) {

				IRObject obj = entry.getObject();
				if (obj == this) {
					continue;
				}

				RulpUtil.decRef(obj);
			}

			entryMap = null;
			_entryCacheList = null;
			_nodeCatchMap = null;
		}

		if (this.parentFrame != null) {
			RulpUtil.decRef(this.parentFrame);
			this.parentFrame = null;
		}

		if (this.frameId != I_FRAME_NULL_ID) {
			RulpFactory.freeFrameId(frameId);
			this.frameId = I_FRAME_NULL_ID;
		}

		super._delete();
	}

	protected IRFrameEntry _findLocalEntry(String name) throws RException {

		IRFrameEntry entry = (entryMap == null ? null : entryMap.get(name));
		if (entry == null) {
			switch (name) {
			case A_LOCAL:
				entry = _insertEntry(A_LOCAL, this);
				break;

			case A_PARENT:
				if (parentFrame != null) {
					entry = _insertEntry(A_PARENT, parentFrame);
					RulpUtil.incRef(parentFrame);
				}
				break;
			default:
			}
		}

		return entry;
	}

	protected EntryNode _findNode(String name) {

		EntryNode entryNode = null;

		if (_nodeCatchMap == null) {
			_nodeCatchMap = new HashMap<>();
		} else {
			entryNode = _nodeCatchMap.get(name);
		}

		return entryNode;
	}

	protected IRFrameEntry _insertEntry(String name, IRObject obj) throws RException {

		IRFrameEntry localEntry = createFrameEntry(name, obj);
		if (entryMap == null) {
			entryMap = new HashMap<>();
		}

		entryMap.put(name, localEntry);
		if (_entryCacheList != null) {
			_entryCacheList.add(localEntry);
		}
		if (_nodeCatchMap != null) {
			_nodeCatchMap.remove(name);
		}

		return localEntry;
	}

	@Override
	public void addFrameReleaseListener(IRListener<IRFrame> listener) {

		if (frameReleaeListenerList == null) {
			frameReleaeListenerList = new LinkedList<>();
		}

		if (!frameReleaeListenerList.contains(listener)) {
			frameReleaeListenerList.add(listener);
		}
	}

	@Override
	public IRVar addVar(String name) throws RException {
		IRVar var = RulpFactory.createVar(name);
		this.setEntry(name, var);
		return var;
	}

	@Override
	public String asString() {
		return frameName;
	}

	@Override
	public IRFrameEntry createFrameEntry(String name, IRObject object) {
		return RulpFactory.createFrameEntry(this, name, object);
	}

	@Override
	public synchronized IRFrameEntry getEntry(String name) throws RException {

		EntryNode entryNode = _findNode(name);
		if (entryNode == null) {

			IRFrameEntry localEntry = _findLocalEntry(name);
			if (localEntry == null) {

				if (parentFrame != null) {

					IRFrameEntry entry = parentFrame.getEntry(name);
					if (entry == null) {
						return null;
					}

					entryNode = new EntryNode();
					entryNode.entry = entry;
					entryNode.isLocal = false;
					_nodeCatchMap.put(name, entryNode);

				} else {

					return null;
				}

			} else {

				entryNode = new EntryNode();
				entryNode.isLocal = true;
				entryNode.entry = localEntry;
				_nodeCatchMap.put(name, entryNode);
			}

		}

		return entryNode.entry;
	}

	@Override
	public int getFrameId() {
		return frameId;
	}

	@Override
	public String getFrameName() {
		return frameName;
	}

	@Override
	public int getLevel() {

		if (_frameLevel == -1) {

			if (getParent() == null) {
				_frameLevel = 0;
			} else {
				_frameLevel = getParent().getLevel() + 1;
			}

		}

		return _frameLevel;
	}

	@Override
	public IRMember getMember(String name) throws RException {
		return getEntry(name);
	}

	@Override
	public synchronized IRObject getObject(String name) throws RException {
		IRFrameEntry entry = getEntry(name);
		return entry == null ? null : entry.getObject();
	}

	@Override
	public IRSubject getParent() {
		return parentFrame;
	}

	@Override
	public IRFrame getParentFrame() {
		return parentFrame;
	}

	@Override
	public IRSubject getSubject() {

		if (_subject == null) {

			if (parentFrame == null || parentFrame.getSubject() == parentFrame) {
				_subject = this;
			} else {
				_subject = parentFrame.getSubject();
			}
		}

		return _subject;
	}

	@Override
	public IRFrame getSubjectFrame() {
		return parentFrame;
	}

	@Override
	public String getSubjectName() {
		return frameName;
	}

	@Override
	public IRThreadContext getThreadContext() {
		return threadContext;
	}

	@Override
	public RType getType() {
		return RType.FRAME;
	}

	@Override
	public boolean isFinal() {
		return bFinal;
	}

	@Override
	public synchronized List<IRFrameEntry> listEntries() {

		if (entryMap == null || entryMap.isEmpty()) {
			return Collections.emptyList();
		}

		_entryCacheList = new LinkedList<>();
		for (Entry<String, IRFrameEntry> e : entryMap.entrySet()) {

			String name = e.getKey();
			IRFrameEntry entry = e.getValue();

			// ignore alias entries
			if (!entry.getName().equals(name)) {
				continue;
			}

			_entryCacheList.add(entry);
		}

		return _entryCacheList;
	}

	@Override
	public Collection<? extends IRMember> listMembers() {

		if (entryMap == null || entryMap.isEmpty()) {
			return Collections.emptyList();
		}

		return listEntries();
	}

	@Override
	public void release() throws RException {

		if (frameReleaeListenerList != null) {
			for (IRListener<IRFrame> listener : frameReleaeListenerList) {
				listener.doAction(this);
			}
		}

		if (entryMap != null) {

			for (IRFrameEntry entry : entryMap.values()) {

				IRObject obj = entry.getObject();
				if (obj == this) {
					continue;
				}

				RulpUtil.decRef(obj);
			}

			entryMap = null;
			_entryCacheList = null;
			_nodeCatchMap = null;
		}
	}

	@Override
	public synchronized IRFrameEntry removeEntry(String name) throws RException {

		IRFrameEntry entry = entryMap.remove(name);

		if (_nodeCatchMap != null) {
			_nodeCatchMap.remove(name);
		}

		if (entry != null) {
			_entryCacheList = null;
		}

		RulpUtil.decRef(entry.getValue());

		return entry;
	}

	@Override
	public synchronized void setEntry(String name, IRObject obj) throws RException {

		IRFrameEntry localEntry = _findLocalEntry(name);

		// New entry
		if (localEntry == null) {
			localEntry = _insertEntry(name, obj);
			RulpUtil.incRef(obj);
		}
		// Update entry
		else {

			IRObject oldObj = localEntry.getObject();

			((XRFrameEntry) localEntry).setObject(obj);
			RulpUtil.incRef(obj);
			RulpUtil.decRef(oldObj);
		}
	}

	@Override
	public synchronized void setEntryAliasName(IRFrameEntry entry, String aliasName) throws RException {

		// Check alias name
		IRFrameEntry localEntry = _findLocalEntry(aliasName);
		if (localEntry != null) {
			throw new RException(String.format("the name %s is already defined: %s", aliasName, localEntry));
		}

		if (entryMap == null) {
			entryMap = new HashMap<>();
		}
		entryMap.put(aliasName, entry);

		if (_nodeCatchMap != null) {
			_nodeCatchMap.remove(aliasName);
		}

		((XRFrameEntry) entry).addAliasName(aliasName);
	}

	@Override
	public void setFinal(boolean bFinal) {
		this.bFinal = bFinal;
	}

	public void setFrameId(int frameId) {
		this.frameId = frameId;
	}

	public void setFrameName(String frameName) {
		this.frameName = frameName;
	}

	@Override
	public void setMember(String name, IRMember mbr) throws RException {

		if (mbr == null || mbr.getValue() == null) {
			this.removeEntry(name);
		} else {
			this.setEntry(name, mbr.getValue());
		}
	}

	public void setParentFrame(IRFrame parentFrame) throws RException {
		this.parentFrame = parentFrame;
		RulpUtil.incRef(parentFrame);
	}

	@Override
	public void setThreadContext(IRThreadContext context) {
		this.threadContext = context;
	}

	public String toString() {
		return frameName;
	}
}