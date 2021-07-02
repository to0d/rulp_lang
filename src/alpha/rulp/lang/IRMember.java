package alpha.rulp.lang;

public interface IRMember extends IRObject {

	public RAccessType getAccessType();

	public String getName();

	public IRObject getSubject();

	public IRObject getValue();

	public boolean isFinal();

	public boolean isStatic();

	public void setAccessType(RAccessType accessType) throws RException;

	public void setFinal(boolean bFinal) throws RException;
	
	public void setStatic(boolean bStatic) throws RException;
}
