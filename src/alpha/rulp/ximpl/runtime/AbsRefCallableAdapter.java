package alpha.rulp.ximpl.runtime;

import static alpha.rulp.lang.Constant.MAX_COUNTER_SIZE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alpha.rulp.runtime.IRAfterAnnotation;
import alpha.rulp.runtime.IRBeforeAnnotation;
import alpha.rulp.runtime.IRCallable;
import alpha.rulp.utils.DeCounter;
import alpha.rulp.ximpl.lang.AbsRefObject;

public abstract class AbsRefCallableAdapter extends AbsRefObject implements IRCallable {

	private List<String> _afterAnnotationAttrList = null;

	private List<String> _beforeAnnotationAttrList = null;

	private Map<String, IRAfterAnnotation> afterAnnotationMap = null;

	private Map<String, IRBeforeAnnotation> beforeAnnotationMap = null;

	private DeCounter callCounter = new DeCounter(MAX_COUNTER_SIZE);

	private boolean debug = false;

	private int statsId = -1;

	public IRAfterAnnotation getAfterAnnotation(String attr) {

		if (afterAnnotationMap == null) {
			return null;
		}

		return afterAnnotationMap.get(attr);
	}

	public IRBeforeAnnotation getBeforeAnnotation(String attr) {

		if (beforeAnnotationMap == null) {
			return null;
		}

		return beforeAnnotationMap.get(attr);
	}

	@Override
	public DeCounter getCallCount(int statsId) {

		if (this.statsId != statsId) {
			this.statsId = statsId;
			this.callCounter = new DeCounter(MAX_COUNTER_SIZE);
		}

		return callCounter;
	}

	public boolean hasAfterAnnotation() {
		return afterAnnotationMap != null && !afterAnnotationMap.isEmpty();
	}

	public boolean hasBeforeAnnotation() {
		return beforeAnnotationMap != null && !beforeAnnotationMap.isEmpty();
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

	public List<String> listAfterAnnotationAttr() {

		if (_afterAnnotationAttrList == null) {
			if (!hasAfterAnnotation()) {
				_afterAnnotationAttrList = Collections.emptyList();
			} else {
				_afterAnnotationAttrList = new ArrayList<>(afterAnnotationMap.keySet());
				Collections.sort(_afterAnnotationAttrList);
			}
		}

		return _afterAnnotationAttrList;
	}

	public List<String> listBeforeAnnotationAttr() {
		if (_beforeAnnotationAttrList == null) {
			if (!hasBeforeAnnotation()) {
				_beforeAnnotationAttrList = Collections.emptyList();
			} else {
				_beforeAnnotationAttrList = new ArrayList<>(beforeAnnotationMap.keySet());
				Collections.sort(_beforeAnnotationAttrList);
			}
		}

		return _beforeAnnotationAttrList;
	}

	public void registerAfterAnnotation(String attr, IRAfterAnnotation anno) {

		if (afterAnnotationMap == null) {
			afterAnnotationMap = new HashMap<>();
		}

		afterAnnotationMap.put(attr, anno);
		_afterAnnotationAttrList = null;
	}

	public void registerBeforeAnnotation(String attr, IRBeforeAnnotation anno) {

		if (beforeAnnotationMap == null) {
			beforeAnnotationMap = new HashMap<>();
		}

		beforeAnnotationMap.put(attr, anno);
		_beforeAnnotationAttrList = null;
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
