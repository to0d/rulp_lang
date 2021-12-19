package alpha.rulp.ximpl.rclass;

import static alpha.rulp.lang.Constant.A_TYPE;

import java.util.Collection;
import java.util.Collections;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRClass;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRMember;
import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.RException;
import alpha.rulp.lang.RType;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.ISubjectLoader;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.ximpl.lang.AbsAtomObject;

public final class XRNoClass extends AbsAtomObject implements IRClass {

	protected String className;

	protected IRAtom classTypeAtom;

	protected IRFrame definedFrame;

	public XRNoClass(String className, IRFrame definedFrame) {
		super();
		this.className = className;
		this.definedFrame = definedFrame;
		this.classTypeAtom = RulpFactory.createAtom(A_TYPE + className);
	}

	@Override
	public void addLoader(ISubjectLoader loader) throws RException {
		throw new RException("Can't operation");
	}

	@Override
	public String asString() {
		return className;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public IRAtom getClassTypeAtom() {
		return classTypeAtom;
	}

	@Override
	public int getLevel() {
		return 0;
	}

	@Override
	public IRMember getMember(String name) throws RException {
		return null;
	}

	@Override
	public IRSubject getParent() {
		return null;
	}

	@Override
	public IRFrame getSubjectFrame() throws RException {
		return definedFrame;
	}

	@Override
	public String getSubjectName() {
		return className;
	}

	@Override
	public IRClass getSuperClass() {
		return null;
	}

	@Override
	public RType getType() {
		return RType.CLASS;
	}

	@Override
	public boolean hasMember(String name) throws RException {
		return false;
	}

	@Override
	public boolean hasSubjectFrame() {
		return true;
	}

	@Override
	public boolean isConst() {
		return true;
	}

	@Override
	public boolean isFinal() {
		return true;
	}

	@Override
	public Collection<? extends IRMember> listMembers() {
		return Collections.emptyList();
	}

	@Override
	public IRInstance newInstance(String instanceName, IRList args, IRInterpreter interpreter, IRFrame frame)
			throws RException {
		return RulpFactory.createInstanceOfDefault(this, instanceName, this.getSubjectFrame());
	}

	@Override
	public void setFinal(boolean bFinal) throws RException {
		throw new RException("Can't operation");
	}

	@Override
	public void setMember(String name, IRMember mbr) throws RException {
		throw new RException("Can't operation");
	}

}
