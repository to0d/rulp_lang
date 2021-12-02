package alpha.rulp.lang;

public interface IRMember extends IRObject {

	public RAccessType getAccessType();

	public String getName();

	public int getProperty();

	public IRObject getSubject();

	public IRObject getValue();

	public void setAccessType(RAccessType accessType) throws RException;

	public void setProperty(int property) throws RException;

}
