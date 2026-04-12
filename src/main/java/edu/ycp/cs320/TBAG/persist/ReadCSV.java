package edu.ycp.cs320.TBAG.persist;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/// From lab 7
public class ReadCSV implements Closeable {
	private BufferedReader reader;

	/// Modified from lab 7 to work with different file structure
	public ReadCSV(String resourceName) throws IOException {
		File file = new File("src/resources/" + resourceName);
		if (!file.exists()) {
			throw new IOException("Couldn't open " + resourceName);
		}
		this.reader = new BufferedReader(new FileReader(file));

		// Skip first 2 lines that make it easier to read
		this.next();
		this.next();
	}

	public List<String> next() throws IOException {
		String line = reader.readLine();
		if (line == null) {
			return null;
		}

		List<String> tuple = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(line, ",", true);
		String previousToken = null;

		while (tokenizer.hasMoreTokens()) {
			String currentToken = tokenizer.nextToken();

			if (currentToken.equals(",")) {
				if (previousToken == null || previousToken.equals(",")) {
					// consecutive comma -> empty field
					tuple.add("");
				}
			} else {
				tuple.add(currentToken.trim());
			}

			previousToken = currentToken;
		}

		// handle trailing comma(s)
		if (previousToken != null && previousToken.equals(",")) {
			tuple.add("");
		}

		return tuple;
	}

	public void close() throws IOException {
		reader.close();
	}
}
