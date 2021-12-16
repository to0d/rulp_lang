package alpha.rulp.lang;

public interface IRArrayList {

	public IRObject get(int index) throws RException;

	public boolean isEmpty() throws RException;

	public int size() throws RException;
}
