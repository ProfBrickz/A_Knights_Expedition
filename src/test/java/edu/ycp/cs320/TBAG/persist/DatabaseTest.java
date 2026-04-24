package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.Utils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class DatabaseTest {
	private final String databasePath = "test-database.db";
	private Database database;


	@Nested
	class DerbyTests {
		@BeforeEach
		public void setUp() {
			InitialData.setCsvFolder("src/test/fixtures/database");
			DatabaseProvider.setInstance(new DerbyDatabase(databasePath));
			database = DatabaseProvider.getInstance();
			((DerbyDatabase) database).createTables();
			database.loadInitialData();
		}

		@AfterEach
		public void tearDown() throws IOException {
			((DerbyDatabase) database).shutdown();
			Utils.deleteDirectory(new File(databasePath));

			Assertions.assertFalse(Files.exists(Path.of(databasePath)));
			InitialData.setCsvFolder("src/resources");
		}

		// Dialog
		@Test
		public void getDialog() {
			getDialogTest();
		}

		@Test
		public void addDialog() {
			addDialogTest();
		}

		@Test
		public void clearDialog() {
			clearDialogTest();
		}

		// Command history
		@Test
		public void getCommandHistory() {
			getCommandHistoryTest();
		}

		@Test
		public void addCommandToHistory() {
			addCommandToHistoryTest();
		}
	}


	@Nested
	class FakeTests {
		@BeforeEach
		public void setUp() {
			InitialData.setCsvFolder("src/test/fixtures/database");
			DatabaseProvider.setInstance(new FakeDatabase());
			database = DatabaseProvider.getInstance();
			database.loadInitialData();
		}

		@AfterEach
		public void tearDown() {
			InitialData.setCsvFolder("src/resources");
		}

		// Dialog
		@Test
		public void getDialog() {
			getDialogTest();
		}

		@Test
		public void addDialog() {
			addDialogTest();
		}

		@Test
		public void clearDialog() {
			clearDialogTest();
		}

		// Command history
		@Test
		public void getCommandHistory() {
			getCommandHistoryTest();
		}

		@Test
		public void addCommandToHistory() {
			addCommandToHistoryTest();
		}
	}


	// Dialog
	private void getDialogTest() {
		ArrayList<String> dialog = database.getDialog();
		Assertions.assertEquals(1, dialog.size());
		Assertions.assertEquals("Welcome to the test!", dialog.get(0));

		database.clearDialog();
		dialog = database.getDialog();
		Assertions.assertTrue(dialog.isEmpty());

		database.addDialog("a");
		dialog = database.getDialog();
		Assertions.assertEquals(1, dialog.size());
		Assertions.assertEquals("a", dialog.get(0));

		database.addDialog("b");
		dialog = database.getDialog();
		Assertions.assertEquals(2, dialog.size());
		Assertions.assertEquals("a", dialog.get(0));
		Assertions.assertEquals("b", dialog.get(1));
	}

	private void addDialogTest() {
		database.clearDialog();

		database.addDialog("a");
		ArrayList<String> dialog = database.getDialog();
		Assertions.assertEquals(1, dialog.size());
		Assertions.assertEquals("a", dialog.get(0));

		database.addDialog("b");
		dialog = database.getDialog();
		Assertions.assertEquals(2, dialog.size());
		Assertions.assertEquals("a", dialog.get(0));
		Assertions.assertEquals("b", dialog.get(1));
	}

	private void clearDialogTest() {
		database.clearDialog();

		database.addDialog("a");
		database.addDialog("b");
		database.addDialog("c");

		ArrayList<String> dialog = database.getDialog();
		Assertions.assertEquals(3, dialog.size());

		database.clearDialog();
		dialog = database.getDialog();
		Assertions.assertTrue(dialog.isEmpty());
	}

	// Command history
	private void getCommandHistoryTest() {
		ArrayList<String> history = database.getCommandHistory();
		Assertions.assertTrue(history.isEmpty());

		database.addCommandToHistory("cmd1");
		history = database.getCommandHistory();
		Assertions.assertEquals(1, history.size());
		Assertions.assertEquals("cmd1", history.get(0));

		database.addCommandToHistory("cmd2");
		history = database.getCommandHistory();
		Assertions.assertEquals(2, history.size());
		Assertions.assertEquals("cmd1", history.get(0));
		Assertions.assertEquals("cmd2", history.get(1));
	}

	private void addCommandToHistoryTest() {
		database.addCommandToHistory("a");
		ArrayList<String> history = database.getCommandHistory();
		Assertions.assertEquals(1, history.size());
		Assertions.assertEquals("a", history.get(0));

		database.addCommandToHistory("b");
		history = database.getCommandHistory();
		Assertions.assertEquals(2, history.size());
		Assertions.assertEquals("a", history.get(0));
		Assertions.assertEquals("b", history.get(1));
	}
}
