package alpha.rulp.lang;

import java.util.Collection;

public interface IRSubject extends IRObject {

	public int getLevel();

	public IRMember getMember(String name) throws RException;

	public IRSubject getParent();

	public boolean hasSubjectFrame();

	public IRFrame getSubjectFrame() throws RException;

	public String getSubjectName();

	public boolean isFinal();

	public Collection<? extends IRMember> listMembers();

	public void setFinal(boolean bFinal) throws RException;

	public void setMember(String name, IRMember mbr) throws RException;
}
