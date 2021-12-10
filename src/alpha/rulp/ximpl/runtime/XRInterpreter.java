/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRBoolean;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RError;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRListener1;
import alpha.rulp.runtime.IROut;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.error.RIException;

public class XRInterpreter implements IRInterpreter {

	static class TLS {
		public int level = 0;
	}

	public static boolean TRACE = false;

	protected AtomicInteger callId = new AtomicInteger(0);

	protected IRFrame mainFrame;

	protected IROut out;

	protected IRParser parser;

	protected final ThreadLocal<TLS> tlsPerThread = new ThreadLocal<>();

	protected TLS _getTLS() {

		TLS tls = tlsPerThread.get();
		if (tls == null) {
			tls = new TLS();
			tlsPerThread.set(tls);
		}

		return tls;
	}

	@Override
	public void addObject(IRObject obj) throws RException {

		switch (obj.getType()) {
		case NIL:
		case ATOM:
			IRAtom atom = (IRAtom) obj;
			mainFrame.setEntry(atom.getName(), atom);
			break;

		case BOOL:
			IRBoolean bv = (IRBoolean) obj;
			mainFrame.setEntry(bv.asString(), bv);
			break;

		case FACTOR:
			IRFactor factor = (IRFactor) obj;
			mainFrame.setEntry(factor.getName(), factor);
			break;

		case INSTANCE:
			IRInstance cv = (IRInstance) obj;
			mainFrame.setEntry(cv.getInstanceName(), cv);
			break;

		default:
			throw new RException("Invalid object: " + obj);
		}

	}

	@Override
	public IRObject compute(IRFrame frame, IRObject obj) throws RException {

		TLS tls = _getTLS();

		// First level call
		if (tls.level++ == 0) {
			callId.incrementAndGet();
		}

		try {
			return RuntimeUtil.compute(obj, this, frame);
		} finally {
			tls.level--;
		}
	}

	@Override
	public void compute(String input) throws RException {
		compute(input, null);
	}

	@Override
	public void compute(String input, IRListener1<IRObject> resultListener) throws RException {

		IRParser _parser = this.getParser();
		List<IRObject> objs;

		synchronized (_parser) {
			objs = _parser.parse(input);
		}

		try {

			for (IRObject obj : objs) {
				IRObject rst = compute(mainFrame, obj);
				RulpUtil.incRef(rst);

				if (resultListener != null) {
					resultListener.doAction(rst);
				}
				RulpUtil.decRef(rst);
			}

		} catch (RIException e) {

			if (TRACE) {
				e.printStackTrace();
			}

			throw new RException("Unhandled internal exception: " + e.toString());

		} catch (RError e) {

			if (TRACE) {
				e.printStackTrace();
			}

			RException newExp = new RException("" + e.getError());

			for (String addMsg : e.getAdditionalMessages()) {
				newExp.addMessage(addMsg);
			}

			throw newExp;
		}
	}

	@Override
	public int getCallId() {
		return callId.get();
	}

	@Override
	public int getCallLevel() {
		return _getTLS().level;
	}

	@Override
	public IRFrame getMainFrame() {
		return mainFrame;
	}

	@Override
	public IRObject getObject(String name) throws RException {
		IRFrameEntry entry = mainFrame.getEntry(name);
		return entry == null ? null : entry.getObject();
	}

	@Override
	public IROut getOut() {
		return out;
	}

	@Override
	public IRParser getParser() {

		if (parser == null) {
			parser = RulpFactory.createParser();
		}

		return parser;
	}

	@Override
	public void out(String line) {

		if (out != null) {
			out.out(line);
		} else {
			System.out.print(line);
		}
	}

	public void setMainFrame(IRFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	@Override
	public void setOutput(IROut out) {
		this.out = out;
	}

}
