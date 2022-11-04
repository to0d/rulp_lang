package beta.test.utils;

import alpha.rulp.utils.FileUtil;
import alpha.rulp.utils.TimeUtil;

public class Test {

	public static void main(String[] args) {

		long time = FileUtil.getLastModifiedTime("C:\\data\\note\\Blog\\Rulp Language Reference.md");
		System.out.println("time=" + time);

		System.out.println(TimeUtil.formatStandardTime(time));
	}

}
