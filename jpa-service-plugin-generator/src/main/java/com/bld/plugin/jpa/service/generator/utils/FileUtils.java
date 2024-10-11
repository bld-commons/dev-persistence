/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.plugin.jpa.service.generator.utils.FileUtils.java
 */
package com.bld.plugin.jpa.service.generator.utils;

import java.io.File;
import java.io.IOException;

/**
 * The Class FileUtils.
 */
public class FileUtils {

	/**
	 * Delete file.
	 *
	 * @param file the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void deleteFile(File file) throws IOException {
		for (File deleteFile : file.listFiles()) {
			if (deleteFile.isDirectory())
				deleteFile(deleteFile);
			deleteFile.delete();
		}
	}

	/**
	 * Delete file.
	 *
	 * @param path the path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void deleteFile(String path) throws IOException {
		File file=new File(path);
		deleteFile(file);
	}
	
}
