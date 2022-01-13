/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.subject;

import java.util.HashMap;
import java.util.Map;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.RException;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.runtime.XRFrame;

public class XRSubjectFrame extends XRFrame implements IRFrame {

	private Map<String, IRFrameEntry> entryCacheMap = null;

	private IRSubject subject;

	@Override
	public IRObject findLocalObject(String name) throws RException {

		IRMember mbr = subject.getMember(name);
		if (mbr == null || mbr.getSubject() != this.subject) {
			return null;
		}

		return mbr.getValue();
	}

	@Override
	public synchronized IRFrameEntry getEntry(String name) throws RException {

		IRMember mbr = subject.getMember(name);
		if (mbr == null) {
			return super.getEntry(name);
		}

		IRObject val = mbr.getValue();
		IRFrameEntry entry = null;

		if (entryCacheMap == null) {
			entryCacheMap = new HashMap<>();
		} else {
			entry = entryCacheMap.get(name);
		}

		if (entry == null || entry.getObject() != val) {
			entry = createFrameEntry(name, val);
			entryCacheMap.put(name, entry);
		}

		return entry;
	}

	@Override
	public IRSubject getSubject() {
		return subject;
	}

	@Override
	public synchronized IRFrameEntry removeEntry(String name) throws RException {
		throw new RException("not support operation");
	}

	@Override
	public synchronized void setEntry(String name, IRObject obj) throws RException {

		IRMember mbr = subject.getMember(name);
		if (mbr != null) {
			throw new RException("member " + name + " exist");
		}

		subject.setMember(name, RulpFactory.createMember(subject, name, obj));
	}

	@Override
	public synchronized void setEntryAliasName(IRFrameEntry entry, String aliasName) throws RException {
		throw new RException("not support operation");
	}

	public void setParentFrame(IRFrame parentFrame) throws RException {

		if (parentFrame == null) {
			return;
		}

		if (parentFrame.isDeleted()) {
			setParentFrame(parentFrame.getParentFrame());
			return;
		}

		parentFrame.addFrameReleaseListener((_frame) -> {

			if (this.parentFrame != null) {
				RulpUtil.decRef(this.parentFrame);
				this.parentFrame = null;
			}

			if (!this.isDeleted()) {
				setParentFrame(_frame.getParentFrame());
			}
		});

		super.setParentFrame(parentFrame);
	}

	public void setSubject(IRSubject subject) {
		this.subject = subject;
	}
}
