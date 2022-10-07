package alpha.rulp.runtime;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;

public interface IRFrameLoader {

	public IRObject load(String name) throws RException;
}
