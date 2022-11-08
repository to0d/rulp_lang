/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.ximpl.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import alpha.rulp.lang.IRAtom;
import alpha.rulp.lang.IRBoolean;
import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRFrameEntry;
import alpha.rulp.lang.IRInstance;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RError;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRDebugger;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInput;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.runtime.IRListener1;
import alpha.rulp.runtime.IROut;
import alpha.rulp.runtime.IRParser;
import alpha.rulp.runtime.IRTLS;
import alpha.rulp.utils.RulpFactory;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.RuntimeUtil;
import alpha.rulp.ximpl.debug.XRDebugger;
import alpha.rulp.ximpl.error.RIException;
import alpha.rulp.ximpl.error.RResumeException;

public class XRInterpreter implements IRInterpreter {

	static class TLS implements IRTLS {

		protected int level = 0;

		protected Map<Object, Object> tlsMap = null;

		@Override
		public Object get(Object key) {
			return tlsMap == null ? null : tlsMap.get(key);
		}

		@Override
		public int getCallLevel() {
			return level;
		}

		@Override
		public void put(Object key, Object value) {

			if (tlsMap == null) {
				tlsMap = new HashMap<>();
			}

			tlsMap.put(key, value);
		}

		@Override
		public Object remove(Object key) {
			return tlsMap == null ? null : tlsMap.remove(key);
		}
	}

	public static boolean TRACE = false;

	protected AtomicInteger callId = new AtomicInteger(0);

	protected IRDebugger debugger = null;

	protected BufferedReader defaultInput = null;

	protected IRInput input;

	protected IRFrame mainFrame;

	protected AtomicInteger maxCallLevel = new AtomicInteger(0);

	protected IROut out;

	protected IRParser parser;

	protected final ThreadLocal<TLS> tlsPerThread = new ThreadLocal<>();

	protected void _callLevel(int level) {

		// First level call
		if (level++ == 0) {
			callId.incrementAndGet();
		}

		if (level > maxCallLevel.get()) {
			maxCallLevel.set(level);
		}
	}

	protected BufferedReader _getDefaultInput() {

		if (defaultInput == null) {
			defaultInput = new BufferedReader(new InputStreamReader(System.in));
		}

		return defaultInput;
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

		TLS tls = getTLS();

		_callLevel(tls.level++);

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

		} catch (RResumeException e) {
			throw e;
		}

		catch (RIException e) {

			if (TRACE) {
				e.printStackTrace();
			}

			if (!e.isHandle()) {
				throw new RException("Unhandled internal exception: " + e.toString());
			}

			RException newExp = new RException("" + e.getExceptionMessage());

			for (String addMsg : e.getAdditionalMessages()) {
				newExp.addMessage(addMsg);
			}

			throw newExp;

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
	public void endDebug() {

		if (debugger != null) {
			debugger.shutdown();
			debugger = null;
		}

	}

	@Override
	public int getCallId() {
		return callId.get();
	}

	@Override
	public IRDebugger getActiveDebugger() {
		return debugger;
	}

	@Override
	public IRInput getInput() {
		return input;
	}

	@Override
	public IRFrame getMainFrame() {
		return mainFrame;
	}

	@Override
	public int getMaxCallLevel() {
		return maxCallLevel.get();
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
	public TLS getTLS() {

		TLS tls = tlsPerThread.get();
		if (tls == null) {
			tls = new TLS();
			tlsPerThread.set(tls);
		}

		return tls;
	}

	@Override
	public void out(String line) {

		if (out != null) {
			out.out(line);
		} else {
			System.out.print(line);
		}
	}

	@Override
	public String read() throws RException {

		try {

			if (input != null) {
				return input.read();
			} else {
				return _getDefaultInput().readLine();
			}

		} catch (IOException e) {

			if (RulpUtil.isTrace(mainFrame)) {
				e.printStackTrace();
			}

			throw new RException(e.toString());
		}
	}

	@Override
	public void setInput(IRInput input) {
		this.input = input;
	}

	public void setMainFrame(IRFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	@Override
	public void setOutput(IROut out) {
		this.out = out;
	}

	@Override
	public void startDebug() {

		if (debugger == null) {
			debugger = new XRDebugger();
		}

		debugger.setup();
	}

}
