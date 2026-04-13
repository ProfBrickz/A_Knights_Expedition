package edu.ycp.cs320.TBAG.persist;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
		StringBuilder current = new StringBuilder();
		boolean inQuotes = false;

		for (int i = 0; i < line.length(); i++) {
			char character = line.charAt(i);

			if (character == '"') {
				inQuotes = !inQuotes; // toggle quote state
			} else if (character == ',' && !inQuotes) {
				// comma outside quotes = field separator
				tuple.add(current.toString().trim().replace("\\n", "\n"));
				current.setLength(0);
			} else {
				current.append(character);
			}
		}

		// add last field
		tuple.add(current.toString().trim().replace("\\n", "\n"));

		return tuple;
	}

	public void close() throws IOException {
		reader.close();
	}
}
