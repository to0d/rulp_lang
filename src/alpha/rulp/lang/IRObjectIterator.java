package alpha.rulp.lang;

public interface IRObjectIterator extends IRObject {

	public boolean hasNext() throws RException;

	public IRObject next() throws RException;

	public IRObject peek() throws RException;

}
