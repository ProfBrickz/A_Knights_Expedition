package edu.ycp.cs320.TBAG.persist;

public class DatabaseProvider {
	private static Database databaseInstance;

	public static void setInstance(Database databaseInstance) {
		DatabaseProvider.databaseInstance = databaseInstance;
	}

	public static Database getInstance() {
		if (databaseInstance == null) {
			throw new IllegalStateException("IDatabase instance has not been set!");
		}
		return databaseInstance;
	}
}
