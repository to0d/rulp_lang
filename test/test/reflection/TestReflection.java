package test.reflection;

import java.lang.reflect.InvocationTargetException;

public class TestReflection {

	public static void main(String[] args) {

		test("java.lang.String", "java.lang.String", "tag-xx");
	}

	static void test(String className, String pt, String pv) {

		try {
			Class c = Class.forName(className);
			Class p1 = Class.forName(pt);

			Object o = c.getConstructor(p1).newInstance(pv);

			System.out.println(o);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
