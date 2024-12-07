package beta.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import alpha.rulp.lang.RException;
import alpha.rulp.utils.FileUtil;
import alpha.rulp.utils.RulpTestBase;
import alpha.rulp.utils.RulpUtil;
import alpha.rulp.utils.SystemUtil;
import alpha.rulp.utils.SystemUtil.OSType;

public class FileUtilTest extends RulpTestBase {

	String _getFileName(String input) {
		return FileUtil.getFileName(input);
	}

	String _getFileParent(String input) {
		return FileUtil.getFileParent(input);
	}

	String _getFilePreName(String input) {
		return FileUtil.getFilePreName(input);
	}

	String _getFileSubffix(String input) {
		return FileUtil.getFileSubffix(input);
	}

	String _getLocalHtmlFolder(String input) {
		return FileUtil.getLocalHtmlFolder(input);
	}

	@Test
	void test_copyByChannel() {

		_setup();

		String testRootPath = "C:\\tmp\\cpp_FileUtilityTest_testCopyByChannel";
		String testFilePath = testRootPath + "\\File1";

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
//			e.printStackTrace();
			fail(e.toString());
		}

		FileUtil.deleteFile(new File(testRootPath));
	}

	@Test
	void test_deleteFile() {

		_setup();

		String testRootPath = "C:\\tmp\\cpp_FileUtilityTest_testDeleteFile";
		String testFilePath = testRootPath + "\\File1";

		File folder = new File(testRootPath);
		File file1 = new File(testFilePath);

		if (!folder.exists()) {
			assertTrue(folder.mkdirs());
		}

		try (PrintStream out = new PrintStream(testFilePath)) {
			out.println("line1");
			out.println("line2");
		} catch (Exception e) {
//			e.printStackTrace();
			fail(e.toString());
		}

		assertTrue(folder.exists());
		assertTrue(file1.exists());

		FileUtil.deleteFile(new File(testRootPath));

		assertTrue(!folder.exists());
		assertTrue(!file1.exists());
	}

	@Test
	void test_getFileName() {

		_setup();

		_test((input) -> {
			return _getFileName(input);
		});

	}

	@Test
	void test_getFileParent() {

		_setup();

		_test((input) -> {
			return _getFileParent(input);
		});

	}

	@Test
	void test_getFilePreName() {

		_setup();

		_test((input) -> {
			return _getFilePreName(input);
		});

	}

	@Test
	void test_getFileSubffix() {

		_setup();

		_test((input) -> {
			return _getFileSubffix(input);
		});

	}

	@Test
	void test_getLastModifiedTime() {

		_setup();

		String testRootPath = null;
		String testFilePath = null;

		if (SystemUtil.getOSType() == OSType.Win) {

			testRootPath = "C:\\tmp\\cpp_FileUtilityTest_testGetLastModifiedTime";
			testFilePath = testRootPath + "\\File1";
		} else {
			testRootPath = "/tmp/test/cpp_FileUtilityTest_testGetLastModifiedTime";
			testFilePath = testRootPath + "/File1";
		}

		File testRoot = new File(testRootPath);
		if (!testRoot.exists()) {
			assertTrue(testRoot.mkdirs());
		}

		File testFile = new File(testFilePath);
		if (testFile.exists()) {
			assertTrue(testFile.delete());
		}

		try {

			long t1 = FileUtil.getLastModifiedTime(testRootPath);

			if (SystemUtil.getOSType() == OSType.Win) {
				Thread.sleep(5);
			} else {
				// Linux vm does not support millisecond
				Thread.sleep(1000);
			}

			long t2 = FileUtil.getLastModifiedTime(testRootPath);
			assertEquals(t1, t2);

			assertTrue(testFile.createNewFile());
			long t3 = FileUtil.getLastModifiedTime(testRootPath);
			assertTrue(t3 > t1);

			long t4 = FileUtil.getLastModifiedTime(testFilePath);

			if (SystemUtil.getOSType() == OSType.Win) {
				Thread.sleep(5);
			} else {

				// Linux vm does not support millisecond
				Thread.sleep(1000);
			}

			long t5 = FileUtil.getLastModifiedTime(testFilePath);
			assertEquals(t4, t5);

			PrintStream outputFile = new PrintStream(testFilePath);
			outputFile.print("Test");
			outputFile.close();

			if (SystemUtil.getOSType() == OSType.Win) {
				Thread.sleep(5);
			} else {

				// Linux vm does not support millisecond
				Thread.sleep(1000);
			}

			long t6 = FileUtil.getLastModifiedTime(testFilePath);
			assertTrue(t6 > t5);

		} catch (Exception e) {
			fail(e.toString());
		}

		FileUtil.deleteFile(new File(testRootPath));
	}

	@Test
	void test_getLocalHtmlFolder() {

		_setup();

		_test((input) -> {
			return _getLocalHtmlFolder(input);
		});

	}

	@Test
	void test_getMd5HashCode() {

		_setup();

		try {
			assertEquals("270c84e760043dc0ba14695261662127", FileUtil.getMd5HashCode(".project"));
		} catch (IOException | RException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_getMd5HashCode2() {

		_setup();

		try {
			assertEquals("270c84e760043dc0ba14695261662127", FileUtil.getMd5HashCode32(".project"));
		} catch (IOException | RException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_getMd5HashCode3() {

		_setup();

		String line1 = "abc";
		String line2 = "abc\n";
		String line3 = "abc\r";

		assertNotEquals(line1, line2);
		assertNotEquals(line1, line3);
		assertNotEquals(line2, line3);

		try {
			assertEquals("900150983cd24fb0d6963f7d28e17f72", FileUtil.getMd5HashCode(RulpUtil.toList(line1)));
			assertEquals("900150983cd24fb0d6963f7d28e17f72", FileUtil.getMd5HashCode(RulpUtil.toList(line2)));
			assertEquals("900150983cd24fb0d6963f7d28e17f72", FileUtil.getMd5HashCode(RulpUtil.toList(line3)));
		} catch (RException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	void test_isAbsPath() {

		_setup();

		assertFalse(FileUtil.isAbsPath(""));
		assertFalse(FileUtil.isAbsPath(null));
		assertTrue(FileUtil.isAbsPath("C:\\"));
	}

	@Test
	void test_isExistFile() {

		assertFalse(FileUtil.isExistFile(null, null));
		assertFalse(FileUtil.isExistFile(".", "not_exist"));
		assertTrue(FileUtil.isExistFile(".", ".project"));
	}

	@Test
	void test_move_file() {

		_setup();

		String testRootPath = "C:\\tmp\\cpp_FileUtilityTest_testMoveFile_File";
		String testToFolder = testRootPath + "\\to";
		String testFilePath = testRootPath + "\\File1";
		String expectFilePath = testRootPath + "\\to\\File1";

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

		String testRootPath = "C:\\tmp\\cpp_FileUtilityTest_testMoveFile_Folder";
		String toFolderPath = testRootPath + "\\to";
		String testSrcFolderPath = testRootPath + "\\from";
		String testFile1Path = testRootPath + "\\from\\File1";
		String expectFile1Path = testRootPath + "\\to\\from\\File1";
		String testFolder2Path = testRootPath + "\\from\\Folder2";
		String expectFolder2Path = testRootPath + "\\to\\from\\Folder2";
		String testFile3Path = testRootPath + "\\from\\Folder2\\File3";
		String expectFile3Path = testRootPath + "\\to\\from\\Folder2\\File3";

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
//			e.printStackTrace();
			fail(e.toString());
		}

		File testFile3 = new File(testFile3Path);
		try (PrintStream out = new PrintStream(testFile3Path)) {
			out.println("line1");
			out.println("line2");
		} catch (Exception e) {
//			e.printStackTrace();
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
	void test_toValidLinuxPath() {

		_setup();

		assertEquals("C:\\data\\itool\\rulp_lang", FileUtil.toValidLinuxPath("", false));
	}

	@Test
	void test_toValidPath() {

		_setup();
		assertEquals("C:\\data\\itool\\rulp_lang\\", FileUtil.toValidPath(""));
		assertEquals("C:\\data\\itool\\rulp_lang\\", FileUtil.toValidPath(null));
	}
}
