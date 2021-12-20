package alpha.rulp.ximpl.runtime;

import static alpha.rulp.lang.Constant.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.utils.*;

public abstract class AbsFunctionAdapter extends AbsRefCallableAdapter implements IRFunction {

	protected Boolean isStable = null;

	protected List<IRAtom> attributeList = null;

	public Boolean getIsStable() {
		return isStable;
	}

	public void setIsStable(Boolean isStable) {
		this.isStable = isStable;
//		AttributeUtil.setAttribute(attributeList, A_STABLE, isStable);
	}

	public List<IRAtom> getAttributeList() {

		if (attributeList == null) {
			attributeList = new LinkedList<>();

			AttributeUtil.setAttribute(attributeList, A_STABLE, AttributeUtil.isTrue(isStable));
			AttributeUtil.setAttribute(attributeList, A_LIST, this.isList());
			AttributeUtil.setAttribute(attributeList, A_LAMBDA, this.isLambda());

		}

		return Collections.unmodifiableList(attributeList);
	}
}
