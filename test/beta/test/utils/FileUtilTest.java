package beta.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import alpha.rulp.utils.FileUtil;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.SystemUtil;
import alpha.rulp.utils.SystemUtil.OSType;

public class FileUtilTest extends RulpTestBase {

	@Test
	void test_copyByChannel() {

		_setup();

		String testRootPath = null;
		String testFilePath = null;

		if (SystemUtil.getOSType() == OSType.Win) {
			testRootPath = "C:\\tmp\\cpp_FileUtilityTest_testCopyByChannel";
			testFilePath = testRootPath + "\\File1";
		} else {
			testRootPath = "/tmp/test/cpp_FileUtilityTest_testCopyByChannel";
			testFilePath = testRootPath + "/File1";
		}

		File folder = new File(testRootPath);
		File file1 = new File(testFilePath);

		String cpyFilePath = testFilePath + ".blk";
		if (!folder.exists()) {
			assertTrue(folder.mkdirs());
		}

		try {

			try (PrintStream out = new PrintStream(testFilePath)) {
				out.println("line1");
				out.println("line2");
			}

			if (new File(cpyFilePath).exists()) {
				assertTrue(new File(cpyFilePath).delete());
			}

			FileUtil.copyByChannel(file1, new File(cpyFilePath));

			assertTrue(new File(cpyFilePath).exists());

		} catch (IOException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		FileUtil.deleteFile(new File(testRootPath));
	}

	@Test
	void test_deleteFile() {

		_setup();

		String testRootPath = null;
		String testFilePath = null;

		if (SystemUtil.getOSType() == OSType.Win) {
			testRootPath = "C:\\tmp\\cpp_FileUtilityTest_testDeleteFile";
			testFilePath = testRootPath + "\\File1";
		} else {
			testRootPath = "/tmp/test/cpp_FileUtilityTest_testDeleteFile";
			testFilePath = testRootPath + "/File1";
		}

		File folder = new File(testRootPath);
		File file1 = new File(testFilePath);

		if (!folder.exists()) {
			assertTrue(folder.mkdirs());
		}

		try (PrintStream out = new PrintStream(testFilePath)) {

			out.println("line1");
			out.println("line2");

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}

		assertTrue(folder.exists());
		assertTrue(file1.exists());

		FileUtil.deleteFile(new File(testRootPath));

		assertTrue(!folder.exists());
		assertTrue(!file1.exists());
	}

	@Test
	void test_isAbsPath() {

		_setup();

		if (SystemUtil.getOSType() == OSType.Win) {
			assertFalse(FileUtil.isAbsPath(""));
			assertFalse(FileUtil.isAbsPath(null));
			assertTrue(FileUtil.isAbsPath("C:\\"));

		} else {

			assertTrue(FileUtil.isAbsPath("/"));
			assertFalse(FileUtil.isAbsPath("abc"));
		}
	}

	@Test
	void test_toValidPath() {

		_setup();

		if (SystemUtil.getOSType() == OSType.Win) {
			assertEquals("C:\\data\\itool\\rulp_lang\\", FileUtil.toValidPath(""));
			assertEquals("C:\\data\\itool\\rulp_lang\\", FileUtil.toValidPath(null));

		} else {
			assertEquals("/home/todd/data/itool/rulp_lang/", FileUtil.toValidPath(""));
			assertEquals("/home/todd/data/itool/rulp_lang/", FileUtil.toValidPath(null));
		}
	}

}
