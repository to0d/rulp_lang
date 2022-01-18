package alpha.rulp.lang;

public interface IRArray extends IRObject {

	public void add(IRObject obj) throws RException;

	public IRArray cloneArray();

	public IRObject get(int index) throws RException;

	public IRObject get(int... indexs) throws RException;

	public int getDimension() throws RException;

	public int getElementCount();

	public boolean isConst();

	public boolean isEmpty() throws RException;

	public void set(int index, IRObject obj) throws RException;

	public int size() throws RException;

	public int size(int dim) throws RException;
}
