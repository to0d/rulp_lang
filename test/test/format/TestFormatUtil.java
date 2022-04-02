package test.format;

import java.io.IOException;

import alpha.common.utils.FileUtil;
import alpha.rulp.lang.RException;
import alpha.rulp.utils.FormatUtil;

public class TestFormatUtil {

	public static void main(String[] args) throws RException, IOException {

		for (String line : FormatUtil.format(FileUtil.openTxtFile("D:\\p2d.rulp", "utf-8"))) {
			System.out.println(line);
		}
	}

}
