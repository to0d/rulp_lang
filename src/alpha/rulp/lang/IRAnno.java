package alpha.rulp.lang;

import java.util.List;

public interface IRAnno extends IRObject {

	public IRObject getSubject();

	public List<? extends IRObject> getValueList();
}
