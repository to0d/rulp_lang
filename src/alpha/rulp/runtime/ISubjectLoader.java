package alpha.rulp.runtime;

import alpha.rulp.lang.IRSubject;
import alpha.rulp.lang.RException;

public interface ISubjectLoader {

	public void load(IRSubject subject) throws RException;

}
