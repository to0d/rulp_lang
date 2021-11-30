package alpha.rulp.lang;

public interface IRMember extends IRObject {

	public static int MBR_PROP_FINAL = 0x0001;

	public static int MBR_PROP_INHERIT = 0x0004;

	public static int MBR_PROP_STATIC = 0x0002;

	public RAccessType getAccessType();

	public String getName();

	public int getProperty();

	public IRObject getSubject();

	public IRObject getValue();

	public void setAccessType(RAccessType accessType) throws RException;

	public void setProperty(int property) throws RException;

}
