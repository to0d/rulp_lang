package alpha.rulp.lang;

import java.util.Collection;

import alpha.rulp.runtime.ISubjectLoader;

public interface IRSubject extends IRObject {

	public void addLoader(ISubjectLoader loader) throws RException;

	public int getLevel();

	public IRMember getMember(String name) throws RException;

	public IRSubject getParent();

	public IRFrame getSubjectFrame() throws RException;

	public String getSubjectName();

	public boolean hasMember(String name) throws RException;

	public boolean hasSubjectFrame();

	public boolean isFinal();

	public Collection<? extends IRMember> listMembers();

	public void setFinal(boolean bFinal) throws RException;

	public void setMember(String name, IRMember mbr) throws RException;
}
