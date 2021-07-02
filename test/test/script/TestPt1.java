package test.script;

import java.io.IOException;

import alpha.rulp.lang.RException;
import alpha.rulp.runtime.IRInterpreter;
import alpha.rulp.utils.RulpFactory;

public class TestPt1 {

	public static void main(String[] args) throws RException, IOException {

		IRInterpreter interpreter = RulpFactory.createInterpreter();
		System.out.println("rst: " + interpreter.compute("(load \"D:\\data\\rs\\workspace\\pt\\pt1.rulp\")"));
	}

}
