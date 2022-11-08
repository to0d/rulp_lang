package alpha.rulp.ximpl.runtime;

import static alpha.rulp.lang.Constant.MAX_COUNTER_SIZE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alpha.rulp.runtime.IRAnnotationBuilder;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.utils.DeCounter;
import alpha.rulp.ximpl.lang.AbsAtomObject;

public abstract class AbsAtomCallableAdapter extends AbsAtomObject implements IRCallable {

	private List<String> _beforeAnnotationBuildeAttrList = null;

	private Map<String, IRAnnotationBuilder> beforeAnnotationBuildeMap = null;

	private DeCounter callCounter = new DeCounter(MAX_COUNTER_SIZE);

	private boolean debug = false;

	private int statsId = -1;

	public IRAnnotationBuilder getBeforeAnnotationBuilder(String attr) {
		
		if (beforeAnnotationBuildeMap == null) {
			return null;
		}

		return beforeAnnotationBuildeMap.get(attr);
	}

	@Override
	public DeCounter getCallCount(int statsId) {

		if (this.statsId != statsId) {
			this.statsId = statsId;
			this.callCounter = new DeCounter(MAX_COUNTER_SIZE);
		}

		return callCounter;
	}

	public boolean hasBeforeAnnotationBuilder() {
		return beforeAnnotationBuildeMap != null && !beforeAnnotationBuildeMap.isEmpty();
	}

	@Override
	public void incCallCount(int statsId, int callId) {

		if (this.statsId != statsId) {
			this.statsId = statsId;
			this.callCounter = new DeCounter(MAX_COUNTER_SIZE);
		}

		callCounter.add(callId);
	}

	@Override
	public boolean isDebug() {
		return debug;
	}

	public List<String> listBeforeAnnotationBuilderAttr() {
		if (_beforeAnnotationBuildeAttrList == null) {
			if (!hasBeforeAnnotationBuilder()) {
				_beforeAnnotationBuildeAttrList = Collections.emptyList();
			} else {
				_beforeAnnotationBuildeAttrList = new ArrayList<>(beforeAnnotationBuildeMap.keySet());
				Collections.sort(_beforeAnnotationBuildeAttrList);
			}
		}

		return _beforeAnnotationBuildeAttrList;
	}

	public void registerBeforeAnnotationBuilder(String attr, IRAnnotationBuilder builder) {

		if (beforeAnnotationBuildeMap == null) {
			beforeAnnotationBuildeMap = new HashMap<>();
		}

		beforeAnnotationBuildeMap.put(attr, builder);
		_beforeAnnotationBuildeAttrList = null;
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
