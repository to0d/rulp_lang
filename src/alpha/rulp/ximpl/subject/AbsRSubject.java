/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.subject;

import static alpha.rulp.lang.Constant.M_CLASS;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.ISubjectLoader;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.ximpl.lang.AbsRefObject;

public abstract class AbsRSubject extends AbsRefObject implements IRSubject {

	protected int _subjectLevel = -1;

	protected boolean bFinal = false;

	protected IRFrame definedFrame;

	protected List<ISubjectLoader> loaderList = null;

	protected Map<String, IRMember> memberMap = null;

	protected IRFrame subjectFrame;

	public AbsRSubject() {
		super();
	}

	public AbsRSubject(IRFrame definedFrame) {
		super();
		this.definedFrame = definedFrame;
	}

	protected void _decMbrRef(IRMember mbr) throws RException {

		if (mbr == null) {
			return;
		}

		IRObject obj = mbr.getValue();
		if (obj != null && obj != this) {
			RulpUtil.decRef(obj);
		}

		RulpUtil.decRef(mbr);
	}

	@Override
	protected void _delete() throws RException {

		if (memberMap != null) {
			for (IRMember member : memberMap.values()) {
				_decMbrRef(member);
			}
			memberMap = null;
		}

		if (subjectFrame != null) {
			RulpUtil.decRef(subjectFrame);
			subjectFrame = null;
		}

		super._delete();
	}

	protected void _incMbrRef(IRMember mbr) throws RException {

		if (mbr == null) {
			return;
		}

		RulpUtil.incRef(mbr);

		IRObject obj = mbr.getValue();
		if (obj != null && obj != this) {
			RulpUtil.incRef(obj);
		}
	}

	protected void _load() throws RException {

		if (loaderList == null) {
			return;
		}

		List<ISubjectLoader> localLoaders = new LinkedList<>(loaderList);
		loaderList = null;

		for (ISubjectLoader loader : localLoaders) {
			loader.load(this);
		}
	}

	@Override
	public void addLoader(ISubjectLoader loader) {

		if (loaderList == null) {
			loaderList = new LinkedList<>();
		}

		loaderList.add(loader);
	}

	@Override
	public int getLevel() {

		if (_subjectLevel == -1) {

			if (getParent() == null) {
				_subjectLevel = 0;
			} else {
				_subjectLevel = getParent().getLevel() + 1;
			}

		}

		return _subjectLevel;
	}

	@Override
	public boolean hasMember(String name) throws RException {

		if (name == null || memberMap == null) {
			return false;
		}

		return memberMap.containsKey(name);
	}

	@Override
	public IRMember getMember(String name) throws RException {

		if (name == null) {
			return null;
		}

		IRMember mbr = memberMap == null ? null : memberMap.get(name);
		if (mbr == null && loaderList != null) {
			_load();
			mbr = getMember(name);
		}

		return mbr;
	}

	@Override
	public IRFrame getSubjectFrame() throws RException {

		if (subjectFrame == null) {
			subjectFrame = RulpFactory.createFrameSubject(this, definedFrame);
			RulpUtil.incRef(subjectFrame);
		}

		return subjectFrame;
	}

	@Override
	public boolean hasSubjectFrame() {
		return subjectFrame != null;
	}

	@Override
	public boolean isFinal() {
		return bFinal;
	}

	@Override
	public Collection<IRMember> listMembers() {

		if (memberMap == null) {
			return Collections.emptyList();
		}

		return memberMap.values();
	}

	@Override
	public void setFinal(boolean bFinal) {
		this.bFinal = bFinal;
	}

	@Override
	public void setMember(String name, IRMember mbr) throws RException {

		if (name == null) {
			throw new RException("member name is null");
		}

		switch (name) {
		case M_CLASS:
			throw new RException("Can't set class");

		default:

		}

		if (mbr == null) {

			if (memberMap != null) {

				IRMember oldMbr = memberMap.remove(name);
				_decMbrRef(oldMbr);

				if (memberMap.isEmpty()) {
					memberMap = null;
				}
			}

		} else {

			if (memberMap == null) {
				memberMap = new HashMap<>();
			}

			memberMap.put(name, mbr);
			_incMbrRef(mbr);
		}
	}

	public void setSubjectFrame(IRFrame subjectFrame) {
		this.subjectFrame = subjectFrame;
	}
}
