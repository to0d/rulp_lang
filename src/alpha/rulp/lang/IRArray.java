package alpha.rulp.lang;

public interface IRArray extends IRObject {

	public IRObject get(int... indexs) throws RException;

	public int getDimension() throws RException;

	public int getElementCount();

	public boolean isEmpty() throws RException;

	public int size() throws RException;

	public int size(int dim) throws RException;
}
