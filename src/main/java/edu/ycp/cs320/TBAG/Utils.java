package edu.ycp.cs320.TBAG;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Utils {
	/// From https://stackoverflow.com/a/29175213
	public static void deleteDirectory(File file) throws IOException {
		File[] contents = file.listFiles();

		if (contents != null) {
			for (File f : contents) {
				if (!Files.isSymbolicLink(f.toPath())) {
					deleteDirectory(f);
				}
			}
		}

		file.delete();
	}
}
