package alpha.rulp.ximpl.runtime;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.RAccessType;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.ximpl.lang.AbsAtomObject;

public class XRFrameEntry extends AbsAtomObject implements IRMember, IRFrameEntry {

	private String _string = null;

	private List<String> aliasNames = null;

	private int entryId;

	private IRFrame frame;

	private String name;

	private IRObject object;

	public XRFrameEntry(int entryId, IRFrame frame, String name, IRObject object) {
		super();

		this.entryId = entryId;
		this.frame = frame;
		this.name = name;
		this.object = object;
	}

	public void addAliasName(String aliasName) {

		if (aliasNames == null) {
			aliasNames = new LinkedList<>();
		}

		aliasNames.add(aliasName);
	}

	@Override
	public String asString() {

		if (_string == null) {
			_string = "" + frame + "::" + name;
		}

		return _string;
	}

	@Override
	public RAccessType getAccessType() {
		return RAccessType.PUBLIC;
	}

	@Override
	public List<String> getAliasName() {
		return aliasNames == null ? Collections.emptyList() : aliasNames;
	}

	@Override
	public int getEntryId() {
		return entryId;
	}

	@Override
	public IRFrame getFrame() {
		return frame;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IRObject getObject() {
		return object;
	}

	@Override
	public IRSubject getSubject() {
		return frame;
	}

	@Override
	public RType getType() {
		return RType.MEMBER;
	}

	@Override
	public IRObject getValue() {
		return object;
	}

	@Override
	public boolean isFinal() {
		return false;
	}

	@Override
	public boolean isInherit() {
		return false;
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	@Override
	public void setAccessType(RAccessType accessType) throws RException {
		throw new RException("unable to update accessType for frame entry");
	}

	@Override
	public void setFinal(boolean bFinal) throws RException {
		throw new RException("unable to set final for frame entry");
	}

	@Override
	public void setInherit(boolean bInherit) throws RException {
		throw new RException("unable to set inherit for frame entry");
	}

	public void setObject(IRObject object) {
		this.object = object;
	}

	@Override
	public void setStatic(boolean bStatic) throws RException {
		throw new RException("unable to set static for frame entry");
	}

	public String toString() {
		return String.format("[name=%s, obj=%s, alias=%s]", name, object.toString(), getAliasName().toString());
	}
}
