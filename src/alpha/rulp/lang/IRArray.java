package alpha.rulp.lang;

public interface IRArray extends IRObject {

	public int capacity();

	public int dimension() throws RException;

	public IRObject get(int... indexs) throws RException;

	public boolean isEmpty() throws RException;
	
	public int size() throws RException;

	public int size(int dim) throws RException;
}
