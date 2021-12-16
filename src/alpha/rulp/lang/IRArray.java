package alpha.rulp.lang;

public interface IRArray extends IRObject, IRArrayList {

	public void add(IRObject obj) throws RException;

	public IRObject get(int... indexs) throws RException;

	public int getDimension() throws RException;

	public int getElementCount();

	public boolean isConst();

	public void set(int index, IRObject obj) throws RException;

	public int size(int dim) throws RException;
}
