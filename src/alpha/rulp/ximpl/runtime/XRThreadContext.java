package alpha.rulp.ximpl.runtime;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRThreadContext;

public class XRThreadContext implements IRThreadContext {

	public static class AsyncError {
		public IRObject inObject = null;
		public RException outException = null;
	}

	public static class AsyncResult {
		public IRObject inObject = null;
		public IRObject outResult = null;
	}

	private AtomicBoolean completed = new AtomicBoolean(false);

	private ArrayList<AsyncError> errList = new ArrayList<>();

	private ArrayList<AsyncResult> rstList = new ArrayList<>();

	public void addError(IRObject inObject, RException outException) {

		AsyncError rst = new AsyncError();
		rst.inObject = inObject;
		rst.outException = outException;

		synchronized (errList) {
			errList.add(rst);
		}
	}

	public void addResult(IRObject inObject, IRObject outResult) {

		AsyncResult rst = new AsyncResult();
		rst.inObject = inObject;
		rst.outResult = outResult;

		synchronized (rstList) {
			rstList.add(rst);
		}

		completed.set(true);
	}

	@Override
	public int getErrorCount() {
		synchronized (errList) {
			return errList.size();
		}
	}

	@Override
	public RException getException(int index) {
		synchronized (errList) {
			return errList.get(index).outException;
		}
	}

	@Override
	public IRObject getResult(int index) {
		synchronized (rstList) {
			return rstList.get(index).outResult;
		}
	}

	@Override
	public int getResultCount() {
		synchronized (rstList) {
			return rstList.size();
		}
	}

	public boolean isCompleted() {
		return completed.get();
	}
}
