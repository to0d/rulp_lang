package alpha.rulp.runtime;

public interface IRTLS {

	public int getCallLevel();

	public void put(Object key, Object value);

	public Object remove(Object key);

	public Object get(Object key);
}
