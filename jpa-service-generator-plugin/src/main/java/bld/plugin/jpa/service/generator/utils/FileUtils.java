package bld.plugin.jpa.service.generator.utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {

	public static void deleteFile(File file) throws IOException {
		for (File deleteFile : file.listFiles()) {
			if (deleteFile.isDirectory())
				deleteFile(deleteFile);
			deleteFile.delete();
		}
	}

	public static void deleteFile(String path) throws IOException {
		File file=new File(path);
		deleteFile(file);
	}
	
}
