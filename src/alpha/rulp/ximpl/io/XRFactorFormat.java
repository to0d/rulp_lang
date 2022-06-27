package alpha.rulp.ximpl.io;

import static alpha.rulp.lang.Constant.O_Nil;

import java.util.ArrayList;

import alpha.rulp.lang.IRFrame;
import alpha.rulp.lang.IRList;
import alpha.rulp.lang.IRObject;
import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRFactor;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.FormatUtil;
import alpha.rulp.ximpl.factor.AbsAtomFactorAdapter;

public class XRFactorFormat extends AbsAtomFactorAdapter implements IRFactor {

	public XRFactorFormat(String factorName) {
		super(factorName);
	}

	@Override
	public IRObject compute(IRList args, IRInterpreter interpreter, IRFrame frame) throws RException {

		if (args.size() != 2) {
			throw new RException("Invalid parameters: " + args);
		}

		ArrayList<String> lines = new ArrayList<>();
		FormatUtil.format(args.get(1), lines, 0);

		for (String line : lines) {
			interpreter.out(line + "\n");
		}

		return O_Nil;
	}

}
