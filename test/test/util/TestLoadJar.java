package test.util;

import alpha.rulp.utils.JVMUtil;

public class TestLoadJar {

	public static void main(String[] args) throws Exception {

		String path = "C:\\data\\rs\\bin\\irulp_rule_bin_0.0.1.jar";

		JVMUtil.loadJar(path);
		Class<?> aClass = Class.forName("alpha.rulp.utility.RuleFactory");
		Object instance = aClass.newInstance();
		System.out.println(instance);
	}

}
