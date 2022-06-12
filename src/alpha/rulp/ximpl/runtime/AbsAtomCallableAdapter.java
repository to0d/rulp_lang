package alpha.rulp.ximpl.runtime;

import static alpha.rulp.lang.Constant.MAX_COUNTER_SIZE;

import alpha.rulp.runtime.IRCallable;
import alpha.rulp.utils.DeCounter;
import alpha.rulp.ximpl.lang.AbsAtomObject;

public abstract class AbsAtomCallableAdapter extends AbsAtomObject implements IRCallable {

	private DeCounter callCounter = new DeCounter(MAX_COUNTER_SIZE);

	private boolean debug = false;

	private int statsId = -1;

	@Override
	public DeCounter getCallCount(int statsId) {

		if (this.statsId != statsId) {
			this.statsId = statsId;
			this.callCounter = new DeCounter(MAX_COUNTER_SIZE);
		}

		return callCounter;
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

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
