package edu.ycp.cs320.TBAG.persist;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/// From lab 7
public class ReadCSV implements Closeable {
	private BufferedReader reader;

	/// Slightly modified from lab 7 to work with different file structure
	public ReadCSV(String resourceName) throws IOException {
		File file = new File("src/resources/" + resourceName);
		if (!file.exists()) {
			throw new IOException("Couldn't open " + resourceName);
		}
		this.reader = new BufferedReader(new FileReader(file));

		// Skip first 3 lines that make it easier to read
		this.next();
		this.next();
		this.next();
	}

	public List<String> next() throws IOException {
		String line = reader.readLine();
		if (line == null) {
			return null;
		}
		List<String> tuple = new ArrayList<String>();
		StringTokenizer tok = new StringTokenizer(line, "|");
		while (tok.hasMoreTokens()) {
			tuple.add(tok.nextToken().trim());
		}
		return tuple;
	}

	public void close() throws IOException {
		reader.close();
	}
}
