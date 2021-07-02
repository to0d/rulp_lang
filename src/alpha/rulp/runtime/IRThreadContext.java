package alpha.rulp.runtime;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;

public interface IRThreadContext {

	public void addError(IRObject inObject, RException outException);

	public void addResult(IRObject inObject, IRObject outResult);

	public int getErrorCount();

	public RException getException(int index);

	public IRObject getResult(int index);

	public int getResultCount();

	public boolean isCompleted();
}
