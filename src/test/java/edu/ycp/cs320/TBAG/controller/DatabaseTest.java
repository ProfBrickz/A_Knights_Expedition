package edu.ycp.cs320.TBAG.controller;

import edu.ycp.cs320.TBAG.Utils;
import edu.ycp.cs320.TBAG.persist.Database;
import edu.ycp.cs320.TBAG.persist.DatabaseProvider;
import edu.ycp.cs320.TBAG.persist.DerbyDatabase;
import edu.ycp.cs320.TBAG.persist.FakeDatabase;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

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
		public void dialog() {
			dialogTest();
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
		public void dialog() {
			dialogTest();
		}
	}

	private void dialogTest() {
		HashMap<Integer, String> dialog = database.getDialog();
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
}
