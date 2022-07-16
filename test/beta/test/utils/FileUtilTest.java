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
	void test_move_file() {

		_setup();

		String testRootPath = null;
		String testToFolder = null;
		String testFilePath = null;
		String expectFilePath = null;

		if (SystemUtil.getOSType() == OSType.Win) {

			testRootPath = "C:\\tmp\\cpp_FileUtilityTest_testMoveFile_File";
			testToFolder = testRootPath + "\\to";
			testFilePath = testRootPath + "\\File1";
			expectFilePath = testRootPath + "\\to\\File1";

		} else {

			testRootPath = "/tmp/test/cpp_FileUtilityTest_testMoveFile";
			testToFolder = testRootPath + "/to";
			testFilePath = testRootPath + "/File1";
			expectFilePath = testRootPath + "/to/File1";
		}

		File testRoot = new File(testRootPath);
		if (!testRoot.exists()) {
			assertTrue(testRoot.mkdirs());
		}

		File toFolder = new File(testToFolder);
		if (!toFolder.exists()) {
			assertTrue(toFolder.mkdirs());
		}

		File testFile = new File(testFilePath);
		try (PrintStream out = new PrintStream(testFilePath)) {
			out.println("line1");
			out.println("line2");

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}

		File expectFile = new File(expectFilePath);
		if (expectFile.exists()) {
			assertTrue(expectFile.delete());
		}

		assertTrue(testFile.exists());
		assertTrue(!expectFile.exists());
		assertTrue(FileUtil.moveFile(testFile, toFolder));

		assertTrue(!testFile.exists());
		assertTrue(expectFile.exists());

		FileUtil.deleteFile(new File(testRootPath));
	}

	@Test
	void test_move_folder() {

		_setup();

		String testRootPath = null;
		String toFolderPath = null;
		String testFile1Path = null;
		String expectFile1Path = null;
		String testFolder2Path = null;
		String expectFolder2Path = null;
		String testFile3Path = null;
		String expectFile3Path = null;
		String testSrcFolderPath = null;

		if (SystemUtil.getOSType() == OSType.Win) {

			testRootPath = "C:\\tmp\\cpp_FileUtilityTest_testMoveFile_Folder";
			toFolderPath = testRootPath + "\\to";
			testSrcFolderPath = testRootPath + "\\from";
			testFile1Path = testRootPath + "\\from\\File1";
			expectFile1Path = testRootPath + "\\to\\from\\File1";
			testFolder2Path = testRootPath + "\\from\\Folder2";
			expectFolder2Path = testRootPath + "\\to\\from\\Folder2";
			testFile3Path = testRootPath + "\\from\\Folder2\\File3";
			expectFile3Path = testRootPath + "\\to\\from\\Folder2\\File3";

		} else {

			testRootPath = "/tmp/test/cpp_FileUtilityTest_testMoveFile_Folder";
			toFolderPath = testRootPath + "/to";
			testSrcFolderPath = testRootPath + "/from";
			testFile1Path = testRootPath + "/from/File1";
			expectFile1Path = testRootPath + "/to/from/File1";
			testFolder2Path = testRootPath + "/from/Folder2";
			expectFolder2Path = testRootPath + "/to/from/Folder2";
			testFile3Path = testRootPath + "/from/Folder2/File3";
			expectFile3Path = testRootPath + "/to/from/Folder2/File3";
		}

		File testRoot = new File(testRootPath);
		if (!testRoot.exists()) {
			assertTrue(testRoot.mkdirs());
		}

		File testSrcFolder = new File(testSrcFolderPath);
		if (!testSrcFolder.exists()) {
			assertTrue(testSrcFolder.mkdirs());
		}

		File toFolder = new File(toFolderPath);
		if (!toFolder.exists()) {
			assertTrue(toFolder.mkdirs());
		}

		File testFolder2 = new File(testFolder2Path);
		if (!testFolder2.exists()) {
			assertTrue(testFolder2.mkdirs());
		}

		File testFile1 = new File(testFile1Path);
		try (PrintStream out = new PrintStream(testFile1Path)) {
			out.println("line1");
			out.println("line2");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}

		File testFile3 = new File(testFile3Path);
		try (PrintStream out = new PrintStream(testFile3Path)) {
			out.println("line1");
			out.println("line2");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}

		File expectFile1 = new File(expectFile1Path);
		if (expectFile1.exists()) {
			assertTrue(expectFile1.delete());
		}

		File expectFolder2 = new File(expectFolder2Path);
		if (expectFolder2.exists()) {
			assertTrue(expectFolder2.delete());
		}

		File expectFile3 = new File(expectFile3Path);
		if (expectFile3.exists()) {
			assertTrue(expectFile3.delete());
		}

		assertTrue(testSrcFolder.exists());
		assertTrue(testFile1.exists());
		assertTrue(testFolder2.exists());
		assertTrue(testFile3.exists());

		assertTrue(!expectFile3.exists());
		assertTrue(!expectFolder2.exists());
		assertTrue(!expectFile3.exists());

		assertTrue(FileUtil.moveFile(testSrcFolder, toFolder));

		assertTrue(!testSrcFolder.exists());

		assertTrue(expectFile3.exists());
		assertTrue(expectFolder2.exists());
		assertTrue(expectFile3.exists());

		FileUtil.deleteFile(new File(testRootPath));
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