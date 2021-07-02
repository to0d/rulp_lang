package alpha.rulp.lang;

public interface IRListener<T extends IRObject> {

	public void doAction(T obj) throws RException;

}
