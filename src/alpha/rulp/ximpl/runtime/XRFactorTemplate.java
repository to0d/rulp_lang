package alpha.rulp.ximpl.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRFactorBody;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.ximpl.factor.AbsRFactorAdapter;

public class XRFactorTemplate extends AbsRFactorAdapter implements IRFactor {

	private Map<String, IRFactorBody> subFactorMap = new HashMap<>();

	public XRFactorTemplate(String factorName) {
		super(factorName);
	}

	public void addBody(String bodyName, IRFactorBody body) throws RException {

		if (subFactorMap.containsKey(bodyName)) {
			throw new RException(String.format("duplicate body name: factor=%s, body=%s", this.getName(), bodyName));
		}

		subFactorMap.put(bodyName, body);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() < 2) {
			throw new RException("Invalid parameters: " + args);
		}

		String subName = interpreter.compute(frame, args.get(1)).asString();
		IRFactorBody factorBody = subFactorMap.get(subName);
		if (factorBody == null) {
			throw new RException("factor body not found: " + subName);
		}

		return factorBody.compute(args, interpreter, frame);
	}

	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(this.getName());
		sb.append(":");

		ArrayList<String> subNames = new ArrayList<>(subFactorMap.keySet());
		Collections.sort(subNames);

		for (int i = 0; i < subNames.size(); ++i) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(subNames.get(i));
		}

		return sb.toString();
	}
}
