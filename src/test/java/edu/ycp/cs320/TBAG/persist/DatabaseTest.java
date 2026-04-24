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
			DatabaseProvider.setInstance(new DerbyDatabase(databasePath));
			database = DatabaseProvider.getInstance();
			((DerbyDatabase) database).createTables();
		}

		@AfterEach
		public void tearDown() throws IOException {
			((DerbyDatabase) database).shutdown();
			Utils.deleteDirectory(new File(databasePath));

			Assertions.assertFalse(Files.exists(Path.of(databasePath)));
		}

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
	}


	@Nested
	class FakeTests {
		@BeforeEach
		public void setUp() {
			DatabaseProvider.setInstance(new FakeDatabase());
			database = DatabaseProvider.getInstance();
		}

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
	}


	private void getDialogTest() {
		ArrayList<String> dialog = database.getDialog();
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
		database.addDialog("a");
		database.addDialog("b");
		database.addDialog("c");

		ArrayList<String> dialog = database.getDialog();
		Assertions.assertEquals(3, dialog.size());

		database.clearDialog();
		dialog = database.getDialog();
		Assertions.assertTrue(dialog.isEmpty());
	}
}
