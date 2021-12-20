package alpha.rulp.ximpl.runtime;

import static alpha.rulp.lang.Constant.A_CONST;
import static alpha.rulp.lang.Constant.A_LAMBDA;
import static alpha.rulp.lang.Constant.A_LIST;
import static alpha.rulp.lang.Constant.A_STABLE;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.runtime.IRFunction;
import alpha.rulp.utils.AttributeUtil;

public abstract class AbsFunctionAdapter extends AbsRefCallableAdapter implements IRFunction {

	protected Boolean isStable = null;

	protected List<IRAtom> attributeList = null;

	public Boolean getIsStable() {
		return isStable;
	}

	public void setIsStable(Boolean isStable) {
		this.isStable = isStable;
		AttributeUtil.setAttribute(_buildAttributeList(), A_STABLE, isStable);
	}

	protected List<IRAtom> _buildAttributeList() {

		if (attributeList == null) {
			attributeList = new LinkedList<>();
			AttributeUtil.setAttribute(attributeList, A_STABLE, AttributeUtil.isTrue(isStable));
			AttributeUtil.setAttribute(attributeList, A_LIST, this.isList());
			AttributeUtil.setAttribute(attributeList, A_LAMBDA, this.isLambda());
			AttributeUtil.setAttribute(attributeList, A_CONST, this.isConst());
		}

		return attributeList;
	}

	public List<IRAtom> getAttributeList() {
		return Collections.unmodifiableList(_buildAttributeList());
	}
}
