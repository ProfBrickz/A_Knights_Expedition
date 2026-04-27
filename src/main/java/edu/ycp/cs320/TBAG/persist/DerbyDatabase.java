package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.Utils;
import edu.ycp.cs320.TBAG.controller.Command;
import edu.ycp.cs320.TBAG.model.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DerbyDatabase implements Database {
	/// From lab 7
	static {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (Exception e) {
			throw new IllegalStateException("Could not load Derby driver");
		}
	}

	/// From lab 7
	private interface Transaction<ResultType> {
		ResultType execute(Connection conn) throws SQLException;
	}

	/// From lab 7
	private static final int MAX_ATTEMPTS = 10;

	/// From lab 7
	public <ResultType> ResultType executeTransaction(Transaction<ResultType> txn) {
		try {
			return doExecuteTransaction(txn);
		} catch (SQLException e) {
			throw new PersistenceException("Transaction failed", e);
		}
	}

	/// From lab 7
	public <ResultType> ResultType doExecuteTransaction(Transaction<ResultType> txn) throws SQLException {
		Connection conn = connect();

		try {
			int numAttempts = 0;
			boolean success = false;
			ResultType result = null;

			while (!success && numAttempts < MAX_ATTEMPTS) {
				try {
					result = txn.execute(conn);
					conn.commit();
					success = true;
				} catch (SQLException e) {
					if (e.getSQLState() != null && e.getSQLState().equals("41000")) {
						// Deadlock: retry (unless max retry count has been reached)
						numAttempts++;
					} else {
						// Some other kind of SQLException
						throw e;
					}
				}
			}

			if (!success) {
				throw new SQLException("Transaction failed (too many retries)");
			}

			// Success!
			return result;
		} finally {
			DBUtil.closeQuietly(conn);
		}
	}

	/// From lab 7
	private Connection connect() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:derby:" + databasePath + ";create=true");

		// Set autocommit too false to allow execution of
		// multiple queries/statements as part of the same transaction.
		conn.setAutoCommit(false);

		return conn;
	}

	/// From lab 7
	// The main method creates the database tables and loads the initial data.
	public static void main(String[] args) throws IOException {
		System.out.println("Deleting old database...");
		Utils.deleteDirectory(new File(defaultDatabasePath));

		System.out.println("Creating tables...");
		DerbyDatabase db = new DerbyDatabase();
		db.createTables();

		System.out.println("Loading initial data...");
		db.loadInitialData();

		System.out.println("Library DB successfully initialized!");
	}

	public void shutdown() {
		try {
			DriverManager.getConnection("jdbc:derby:" + databasePath + ";shutdown=true");
		} catch (SQLException e) {
			String state = e.getSQLState();

			// These BOTH indicate successful shutdown
			if ("XJ015".equals(state) || "08006".equals(state)) {
				return; // success
			}

			e.printStackTrace(); // helpful while debugging
			throw new RuntimeException("Derby shutdown failed: " + state, e);
		}
	}

	private static final String defaultDatabasePath = "database.db";
	private final String databasePath;

	public DerbyDatabase(String databasePath) {
		this.databasePath = databasePath;
	}

	public DerbyDatabase() {
		this(defaultDatabasePath);
	}


	// General purpose methods
	@Override
	public void loadInitialData() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection connection) throws SQLException {
				// Dialog
				ArrayList<String> dialog;

				// Items
				HashMap<Integer, Item> items;
				HashMap<Integer, WeaponAbility> weaponAbilities;
				ArrayList<Pair<Integer, Integer>> weaponAbilitiesJunction;

				// NPCs
				HashMap<Integer, NPC> npcs;
				HashMap<Integer, ArrayList<Item>> npcItems;


				// Enemies
				HashMap<Integer, Enemy> enemies;
				HashMap<Integer, ArrayList<Item>> enemyItems;


				// Rooms
				HashMap<Integer, Room> rooms;
				// A map between a room's id and (a map of its directions and connections)
				HashMap<Integer, HashMap<String, RoomConnection>> roomConnections;
				HashMap<Integer, ArrayList<Item>> roomItems;
				HashMap<Integer, ArrayList<NPC>> roomNPCs;
				HashMap<Integer, ArrayList<Enemy>> roomEnemies;


				// Player
				Player player;
				HashMap<Integer, Item> playerItems;


				try {
					// Dialog
					dialog = InitialData.getDialog();

					// Items
					items = InitialData.getItems();
//					weaponAbilities = InitialData.getWeaponAbilities();
					weaponAbilities = new HashMap<>();
//					weaponAbilitiesJunction = InitialData.getWeaponAbilitiesJunction();
					weaponAbilitiesJunction = new ArrayList<>();

					// NPCs
					npcs = InitialData.getNPCs();
//					npcItems = InitialData.getNPCItems();
					npcItems = new HashMap<>();


					// Enemies
//					enemies = InitialData.getEnemies();
					enemies = new HashMap<>();
//					enemyItems = InitialData.getEnemyItems();
					enemyItems = new HashMap<>();


					// Rooms
					rooms = InitialData.getRooms();
					roomConnections = InitialData.getRoomConnections();
//					roomItems = InitialData.getRoomItems();
					roomItems = new HashMap<>();
//					roomNPCs = InitialData.getRoomNPCs();
					roomNPCs = new HashMap<>();
//					roomEnemies = InitialData.getRoomEnemies();
					roomEnemies = new HashMap<>();


					// Player
					player = InitialData.getPlayer();
					playerItems = InitialData.getPlayerItems();
				} catch (IllegalStateException exception) {
					throw new IllegalStateException("Initial data is incorrect", exception);
				} catch (IOException exception) {
					throw new IllegalStateException("Couldn't read initial data", exception);
				}

				PreparedStatement statement = null;

				try {
					// Dialog
					statement = connection.prepareStatement("""
							INSERT INTO dialog (text)
							VALUES (?)
						""");
					for (String text : dialog) {
						statement.setString(1, text);
						statement.addBatch();
					}
					statement.executeBatch();


					// Items
					statement = connection.prepareStatement("""
							INSERT INTO items (
								name,
								description,
								asset_name,
								value,
								type,
								heal_amount,
								defense,
								active_armor
							) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
						""");
					for (Item item : items.values()) {
						statement.setString(1, item.getName());
						statement.setString(2, item.getDescription());
						statement.setString(3, item.getAssetName());
						statement.setInt(4, item.getValue());
						statement.setInt(5, ItemType.getByItem(item).ordinal());

						if (item instanceof HealingItem healingItem) {
							statement.setInt(6, healingItem.getHealAmount());
						} else if (item instanceof Armor armor) {
							statement.setInt(7, armor.getDefense());
							statement.setBoolean(8, armor.getActive());
						}

						statement.addBatch();
					}
					statement.executeBatch();


					statement = connection.prepareStatement("""
							INSERT INTO weapon_abilities (damage, attack_description)
							VALUES (?, ?)
						""");
					for (WeaponAbility ability : weaponAbilities.values()) {
						statement.setInt(1, ability.getDamage());
						statement.setString(2, ability.getAttackDescription());
						statement.addBatch();
					}
					statement.executeBatch();


					statement = connection.prepareStatement("""
							INSERT INTO weapon_abilities_junction (weapon_id, weapon_ability_id)
							VALUES (?, ?)
						""");
					for (Pair<Integer, Integer> abilityJunction : weaponAbilitiesJunction) {
						statement.setInt(1, abilityJunction.getLeft());
						statement.setInt(2, abilityJunction.getRight());
						statement.addBatch();
					}
					statement.executeBatch();


					// NPCs
					statement = connection.prepareStatement("""
							INSERT INTO npcs (name, max_health)
							VALUES (?, ?)
						""");
					for (NPC npc : npcs.values()) {
						statement.setString(1, npc.getName());
						statement.setInt(2, npc.getMaxHealth());
					}
					statement.executeBatch();


					statement = connection.prepareStatement("""
							INSERT INTO npc_items (npc_id, item_id, amount)
							VALUES (?, ?, ?)
						""");
					for (Map.Entry<Integer, ArrayList<Item>> entry : npcItems.entrySet()) {
						for (Item item : entry.getValue()) {
							statement.setInt(1, entry.getKey());
							statement.setInt(2, item.getId());
							statement.setInt(3, item.getAmount());
						}
					}
					statement.executeBatch();


					// Enemies
					statement = connection.prepareStatement("""
							INSERT INTO enemies (name, max_health)
							VALUES (?, ?)
						""");
					for (Enemy enemy : enemies.values()) {
						statement.setString(1, enemy.getName());
						statement.setInt(2, enemy.getMaxHealth());
					}
					statement.executeBatch();


					statement = connection.prepareStatement("""
							INSERT INTO enemy_items (enemy_id, item_id, amount)
							VALUES (?, ?, ?)
						""");
					for (Map.Entry<Integer, ArrayList<Item>> entry : enemyItems.entrySet()) {
						for (Item item : entry.getValue()) {
							statement.setInt(1, entry.getKey());
							statement.setInt(2, item.getId());
							statement.setInt(3, item.getAmount());
						}
					}
					statement.executeBatch();


					// Rooms
					statement = connection.prepareStatement("""
							INSERT INTO rooms (name, description, asset_name)
							VALUES (?, ?, ?)
						""");
					for (Room room : rooms.values()) {
						statement.setString(1, room.getName());
						statement.setString(2, room.getDescription());
						statement.setString(3, room.getAssetName());
						statement.addBatch();
					}
					statement.executeBatch();


					statement = connection.prepareStatement("""
							INSERT INTO room_connections (
								source_id,
								destination_id,
								direction,
								description
							) VALUES (?, ?, ?, ?)
						""");
					for (Map.Entry<Integer, HashMap<String, RoomConnection>> entry : roomConnections.entrySet()) {
						Integer roomId = entry.getKey();

						for (Map.Entry<String, RoomConnection> entry1 : entry.getValue().entrySet()) {
							String direction = entry1.getKey();
							RoomConnection roomConnection = entry1.getValue();

							statement.setInt(1, roomId);
							statement.setInt(2, roomConnection.getRoom().getId());
							statement.setString(3, direction);
							statement.setString(4, roomConnection.getDescription());
							statement.addBatch();
						}
					}
					statement.executeBatch();


					statement = connection.prepareStatement("""
							INSERT INTO room_items (room_id, item_id, amount)
							VALUES (?, ?, ?)
						""");
					for (Map.Entry<Integer, ArrayList<Item>> entry : npcItems.entrySet()) {
						for (Item item : entry.getValue()) {
							statement.setInt(1, entry.getKey());
							statement.setInt(2, item.getId());
							statement.setInt(3, item.getAmount());
							statement.addBatch();
						}
					}
					statement.executeBatch();


					statement = connection.prepareStatement("""
							INSERT INTO room_npcs (room_id, npc_id, health)
							VALUES (?, ?, ?)
						""");
					for (Map.Entry<Integer, ArrayList<NPC>> entry : roomNPCs.entrySet()) {
						for (NPC npc : entry.getValue()) {
							statement.setInt(1, entry.getKey());
							statement.setInt(2, npc.getId());
							statement.setInt(3, npc.getHealth());
							statement.addBatch();
						}
					}
					statement.executeBatch();


					statement = connection.prepareStatement("""
							INSERT INTO room_enemies (room_id, enemy_id, health)
							VALUES (?, ?, ?)
						""");
					for (Map.Entry<Integer, ArrayList<Enemy>> entry : roomEnemies.entrySet()) {
						for (Enemy enemy : entry.getValue()) {
							statement.setInt(1, entry.getKey());
							statement.setInt(2, enemy.getId());
							statement.setInt(3, enemy.getHealth());
							statement.addBatch();
						}
					}
					statement.executeBatch();


					// Player
					statement = connection.prepareStatement("""
							INSERT INTO player (room_id, state, coins, max_health, health)
							VALUES (?, ?, ?, ?, ?)
						""");
					statement.setInt(1, player.getRoom().getId());
					statement.setInt(2, player.getState().ordinal());
					statement.setInt(3, player.getCoins());
					statement.setInt(4, player.getMaxHealth());
					statement.setInt(5, player.getHealth());
					statement.executeUpdate();


					statement = connection.prepareStatement("""
							INSERT INTO player_items (
								item_id,
								amount
							) values (?, ?)
						""");
					for (Item item : playerItems.values()) {
						statement.setInt(1, item.getId());
						statement.setInt(2, item.getAmount());
						statement.addBatch();
					}
					statement.executeBatch();


					return true;
				} finally {
					DBUtil.closeQuietly(statement);
				}
			}
		});
	}

	@Override
	public Boolean reset() {
		try {
			// Delete database
			File databaseFolder = new File(defaultDatabasePath);
			while (databaseFolder.exists()) {
				shutdown();
				Utils.deleteDirectory(databaseFolder);
			}

			// Recreate database
			createTables();
			loadInitialData();

			return true;
		} catch (IOException exception) {
			return false;
		}
	}

	public void createTables() {
		final Integer DIALOG_MAX_LENGTH = 8192;
		final Integer NAME_MAX_LENGTH = 128;
		final Integer DESCRIPTION_MAX_LENGTH = 512;
		final Integer DIRECTION_MAX_LENGTH = 64;

		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;

				try {
					// Dialog
					statement = connection.prepareStatement("""
							CREATE TABLE dialog (
								id INTEGER PRIMARY KEY
									GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),
								text VARCHAR(%d) NOT NULL
							)
						""".formatted(DIALOG_MAX_LENGTH));
					statement.executeUpdate();

					statement = connection.prepareStatement("""
							CREATE TABLE command_history (
								id INTEGER PRIMARY KEY
									GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),
								command VARCHAR(%d) NOT NULL
							)
						""".formatted(DESCRIPTION_MAX_LENGTH));
					statement.executeUpdate();


					// Items
					statement = connection.prepareStatement("""
							CREATE TABLE items (
								id INTEGER PRIMARY KEY
									GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),
								name VARCHAR(%d) NOT NULL,
								description VARCHAR(%d) NOT NULL,
								asset_name VARCHAR(%d),
								value INTEGER NOT NULL,
								type INTEGER NOT NULL,
								heal_amount INTEGER,
								defense INTEGER,
								active_armor BOOLEAN
							)
						""".formatted(
						NAME_MAX_LENGTH,
						DESCRIPTION_MAX_LENGTH,
						NAME_MAX_LENGTH
					));
					statement.execute();

					statement = connection.prepareStatement("""
							CREATE TABLE weapon_abilities (
								id INTEGER PRIMARY KEY
									GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),
								damage INTEGER NOT NULL,
								attack_description VARCHAR(%d) NOT NULL
							)
						""".formatted(
						DESCRIPTION_MAX_LENGTH
					));
					statement.execute();

					statement = connection.prepareStatement("""
							CREATE TABLE weapon_abilities_junction (
								weapon_id INTEGER,
								weapon_ability_id INTEGER,
								PRIMARY KEY (weapon_id, weapon_ability_id),
								FOREIGN KEY (weapon_id) REFERENCES items(id),
								FOREIGN KEY (weapon_ability_id) REFERENCES weapon_abilities(id)
							)
						"""
					);
					statement.execute();


					// NPCs
					statement = connection.prepareStatement("""
							CREATE TABLE npcs (
								id INTEGER PRIMARY KEY
									GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),
								name VARCHAR(%d) NOT NULL,
								max_health INTEGER,
								health INTEGER,
								greeting VARCHAR(128),
								goodbye VARCHAR(128)
							)
						""".formatted(
						NAME_MAX_LENGTH
					));
					statement.executeUpdate();

					statement = connection.prepareStatement("""
							CREATE TABLE npc_items (
								npc_id INTEGER,
								item_id INTEGER,
								amount INTEGER NOT NULL,
								PRIMARY KEY (npc_id, item_id),
								FOREIGN KEY (npc_id) REFERENCES npcs(id),
								FOREIGN KEY (item_id) REFERENCES items(id)
							)
						""");
					statement.execute();


					// Enemies
					statement = connection.prepareStatement("""
							CREATE TABLE enemies (
								id INTEGER PRIMARY KEY
									GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),
								name VARCHAR(%d) NOT NULL,
								max_health INTEGER
							)
						""".formatted(
						NAME_MAX_LENGTH
					));
					statement.executeUpdate();

					statement = connection.prepareStatement("""
							CREATE TABLE enemy_items (
								enemy_id INTEGER,
								item_id INTEGER,
								amount INTEGER NOT NULL,
								PRIMARY KEY (enemy_id, item_id),
								FOREIGN KEY (enemy_id) REFERENCES enemies(id),
								FOREIGN KEY (item_id) REFERENCES items(id)
							)
						""");
					statement.execute();


					// Rooms
					statement = connection.prepareStatement("""
							CREATE TABLE rooms (
								id INTEGER PRIMARY KEY
									GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1),
								name VARCHAR(%d) NOT NULL,
								description VARCHAR(%d) NOT NULL,
								asset_name VARCHAR(%d) NOT NULL
							)
						""".formatted(
						NAME_MAX_LENGTH,
						DESCRIPTION_MAX_LENGTH,
						NAME_MAX_LENGTH
					));
					statement.executeUpdate();

					statement = connection.prepareStatement("""
							CREATE TABLE room_connections (
								source_id INTEGER,
								destination_id INTEGER,
								direction VARCHAR(%d) NOT NULL,
								description VARCHAR(%d) NOT NULL,
								locked BOOLEAN,
								locked_message VARCHAR(%d),
								PRIMARY KEY (source_id, destination_id),
								FOREIGN KEY (source_id) REFERENCES rooms(id),
								FOREIGN KEY (destination_id) REFERENCES rooms(id)
							)
						""".formatted(
						DIRECTION_MAX_LENGTH,
						DESCRIPTION_MAX_LENGTH,
						DESCRIPTION_MAX_LENGTH
					));
					statement.execute();

					statement = connection.prepareStatement("""
							CREATE TABLE room_items (
								room_id INTEGER,
								item_id INTEGER,
								amount INTEGER NOT NULL,
								PRIMARY KEY (room_id, item_id),
								FOREIGN KEY (room_id) REFERENCES rooms(id),
								FOREIGN KEY (item_id) REFERENCES items(id)
							)
						""");
					statement.execute();

					statement = connection.prepareStatement("""
							CREATE TABLE room_npcs (
								room_id INTEGER,
								npc_id INTEGER,
								PRIMARY KEY (room_id, npc_id),
								FOREIGN KEY (room_id) REFERENCES rooms(id),
								FOREIGN KEY (npc_id) REFERENCES npcs(id),
								health INTEGER
							)
						""");
					statement.execute();

					statement = connection.prepareStatement("""
							CREATE TABLE room_enemies (
								room_id INTEGER,
								enemy_id INTEGER,
								PRIMARY KEY (room_id, enemy_id),
								FOREIGN KEY (room_id) REFERENCES rooms(id),
								FOREIGN KEY (enemy_id) REFERENCES enemies(id),
								health INTEGER
							)
						""");
					statement.execute();


					// Player
					statement = connection.prepareStatement("""
							CREATE TABLE player (
								room_id INTEGER NOT NULL,
								state INTEGER NOT NULL,
								coins INTEGER NOT NULL,
								max_health INTEGER NOT NULL,
								health INTEGER NOT NULL,
								current_npc INTEGER,
								last_command INTEGER,
								confirming BOOLEAN,
								FOREIGN KEY (room_id) REFERENCES rooms(id)
							)
						""");
					statement.executeUpdate();

					statement = connection.prepareStatement("""
							CREATE TABLE player_items (
								item_id INTEGER PRIMARY KEY,
								amount INTEGER NOT NULL,
								FOREIGN KEY (item_id) REFERENCES items(id)
							)
						""");
					statement.execute();

					return true;
				} finally {
					DBUtil.closeQuietly(statement);
				}
			}
		});
	}


	// Dialog
	@Override
	public ArrayList<String> getDialog() {
		return executeTransaction(new Transaction<ArrayList<String>>() {
			@Override
			public ArrayList<String> execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet resultSet = null;

				try {
					statement = connection.prepareStatement("""
							SELECT text
							FROM dialog
							ORDER BY id
						""");
					resultSet = statement.executeQuery();

					ArrayList<String> result = new ArrayList<>();

					while (resultSet.next()) {
						String text = resultSet.getString(1);

						result.add(text);
					}

					return result;
				} finally {
					DBUtil.closeQuietly(statement);
					DBUtil.closeQuietly(resultSet);
				}
			}
		});
	}

	@Override
	public void addDialog(String text) {
		executeTransaction(new Transaction<Void>() {
			@Override
			public Void execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;

				try {
					statement = connection.prepareStatement(
						"INSERT INTO dialog (text) VALUES (?)"
					);
					statement.setString(1, text);

					statement.executeUpdate();

					return null;
				} finally {
					DBUtil.closeQuietly(statement);
				}
			}
		});
	}

	@Override
	public void clearDialog() {
		executeTransaction(new Transaction<Void>() {
			@Override
			public Void execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;

				try {
					statement = connection.prepareStatement(
						"DELETE FROM dialog"
					);
					statement.executeUpdate();

					return null;
				} finally {
					DBUtil.closeQuietly(statement);
				}
			}
		});
	}


	// Command history
	@Override
	public ArrayList<String> getCommandHistory() {
		return executeTransaction(new Transaction<ArrayList<String>>() {
			@Override
			public ArrayList<String> execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet resultSet = null;

				try {
					statement = connection.prepareStatement(
						"SELECT command FROM command_history"
					);
					resultSet = statement.executeQuery();

					ArrayList<String> result = new ArrayList<>();

					while (resultSet.next()) {
						String command = resultSet.getString(1);

						result.add(command);
					}

					return result;
				} finally {
					DBUtil.closeQuietly(statement);
					DBUtil.closeQuietly(resultSet);
				}
			}
		});
	}

	@Override
	public void addCommandToHistory(String command) {
		executeTransaction(new Transaction<Void>() {
			@Override
			public Void execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;

				try {
					// Remove command from history if already in it
					statement = connection.prepareStatement("DELETE FROM command_history WHERE command = ?");
					statement.setString(1, command);
					statement.execute();

					// Add command to history
					statement = connection.prepareStatement("INSERT INTO command_history (command) VALUES (?)");
					statement.setString(1, command);
					statement.executeUpdate();

					// Trim history if longer than max length
					statement = connection.prepareStatement("""
							DELETE FROM command_history WHERE id NOT IN (
								SELECT id from command_history
								ORDER BY id DESC
								FETCH FIRST %d ROWS ONLY
							)
						""".formatted(MAX_HISTORY_SIZE));
					statement.execute();

					return null;
				} finally {
					DBUtil.closeQuietly(statement);
				}
			}
		});
	}


	// Items
	public HashMap<Integer, Item> getItemsFromResultSet(ResultSet resultSet) {
		HashMap<Integer, Item> items = new HashMap<>();

		try {
			while (resultSet.next()) {
				Integer id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				String description = resultSet.getString("description");
				String assetName = resultSet.getString("asset_name");
				Integer value = resultSet.getInt("value");
				Integer typeOrdinal = resultSet.getInt("type");
				ItemType type = ItemType.values()[typeOrdinal];
				Integer amount = resultSet.getInt("amount");

				if (resultSet.wasNull()) {
					amount = 1;
				}

				Item item;

				if (type == ItemType.WEAPON) {
					item = new Weapon(id, name, description, value, amount, assetName);
				} else if (type == ItemType.ARMOR) {
					Integer defense = resultSet.getInt("defense");
					Boolean active = resultSet.getBoolean("active_armor");
					item = new Armor(id, name, description, defense, active, value, amount, assetName);
				} else if (type == ItemType.HEALING) {
					Integer healAmount = resultSet.getInt("heal_amount");
					item = new HealingItem(id, name, description, healAmount, value, amount, assetName);
				} else {
					item = new Item(id, name, description, value, amount, assetName);
				}

				items.put(id, item);
			}
		} catch (SQLException e) {
			throw new PersistenceException("Could not load items", e);
		}

		return items;
	}

	@Override
	public HashMap<Integer, WeaponAbility> getAbilitiesForWeapon(Weapon weapon) { // Hamed
		if (weapon == null) {
			return new HashMap<>();
		}

		return executeTransaction(new Transaction<HashMap<Integer, WeaponAbility>>() {
			@Override
			public HashMap<Integer, WeaponAbility> execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet resultSet = null;

				try {
					statement = connection.prepareStatement("""
							SELECT
								weapon_abilities.id,
								weapon_abilities.damage,
								weapon_abilities.attack_description
							FROM weapon_abilities_junction, weapon_abilities
							WHERE weapon_abilities.id = weapon_abilities_junction.weapon_ability_id
								AND weapon_abilities_junction.weapon_id = ?
						""");
					statement.setInt(1, weapon.getId());
					resultSet = statement.executeQuery();

					HashMap<Integer, WeaponAbility> result = new HashMap<>();
					while (resultSet.next()) {
						Integer id = resultSet.getInt(1);
						Integer damage = resultSet.getInt(2);
						String attackDescription = resultSet.getString(3);
						result.put(id, new WeaponAbility(id, damage, attackDescription));
					}
					return result;
				} finally {
					DBUtil.closeQuietly(statement);
					DBUtil.closeQuietly(resultSet);
				}
			}
		});
	}


	// NPCs
	@Override
	public HashMap<Integer, Item> getItemsForNPC(NPC npc) {
		if (npc == null) {
			return new HashMap<>();
		}

		return executeTransaction(new Transaction<HashMap<Integer, Item>>() {
			@Override
			public HashMap<Integer, Item> execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet resultSet = null;

				try {
					statement = connection.prepareStatement("""
							SELECT
								items.id,
								items.name,
								items.description,
								items.asset_name,
								items.value,
								items.type,
								items.heal_amount,
								items.defense,
								items.active_armor,
								npc_items.amount
							FROM npc_items, items
							WHERE items.id = npc_items.item_id AND npc_items.npc_id = ?
						""");
					statement.setInt(1, npc.getId());
					resultSet = statement.executeQuery();

					return getItemsFromResultSet(resultSet);
				} finally {
					DBUtil.closeQuietly(statement);
					DBUtil.closeQuietly(resultSet);
				}
			}
		});
	}


	// Enemies
	@Override
	public HashMap<Integer, Item> getItemsForEnemy(Enemy enemy) { // Hamed
		if (enemy == null) {
			return new HashMap<>();
		}

		return executeTransaction(new Transaction<HashMap<Integer, Item>>() {
			@Override
			public HashMap<Integer, Item> execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet resultSet = null;

				try {
					statement = connection.prepareStatement("""
							SELECT
								items.id,
								items.name,
								items.description,
								items.asset_name,
								items.value,
								items.type,
								items.heal_amount,
								items.defense,
								items.active_armor,
								enemy_items.amount
							FROM enemy_items, items
							WHERE items.id = enemy_items.item_id AND enemy_items.enemy_id = ?
						""");
					statement.setInt(1, enemy.getId());
					resultSet = statement.executeQuery();

					return getItemsFromResultSet(resultSet);
				} finally {
					DBUtil.closeQuietly(statement);
					DBUtil.closeQuietly(resultSet);
				}
			}
		});
	}

	@Override
	public void addItemToEnemy(Enemy enemy, Item item) { // Hamed
		if (enemy == null || item == null) {
			return;
		}

		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection connection) throws SQLException {
				PreparedStatement selectStatement = null;
				PreparedStatement insertStatement = null;
				PreparedStatement updateStatement = null;
				ResultSet resultSet = null;

				try {
					selectStatement = connection.prepareStatement("""
							SELECT amount
							FROM enemy_items
							WHERE enemy_id = ? AND item_id = ?
						""");
					selectStatement.setInt(1, enemy.getId());
					selectStatement.setInt(2, item.getId());
					resultSet = selectStatement.executeQuery();

					int delta = item.getAmount() == null ? 1 : item.getAmount();
					if (delta <= 0) {
						return true;
					}

					if (resultSet.next()) {
						int currentAmount = resultSet.getInt(1);
						int newAmount = currentAmount + delta;

						updateStatement = connection.prepareStatement("""
								UPDATE enemy_items
								SET amount = ?
								WHERE enemy_id = ? AND item_id = ?
							""");
						updateStatement.setInt(1, newAmount);
						updateStatement.setInt(2, enemy.getId());
						updateStatement.setInt(3, item.getId());
						updateStatement.executeUpdate();
					} else {
						insertStatement = connection.prepareStatement("""
								INSERT INTO enemy_items (enemy_id, item_id, amount)
								VALUES (?, ?, ?)
							""");
						insertStatement.setInt(1, enemy.getId());
						insertStatement.setInt(2, item.getId());
						insertStatement.setInt(3, delta);
						insertStatement.executeUpdate();
					}

					return true;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(selectStatement);
					DBUtil.closeQuietly(insertStatement);
					DBUtil.closeQuietly(updateStatement);
				}
			}
		});
	}

	@Override
	public void removeItemFromEnemy(Enemy enemy, Item item) { // Hamed
		if (enemy == null || item == null) {
			return;
		}

		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection connection) throws SQLException {
				PreparedStatement selectStatement = null;
				PreparedStatement updateStatement = null;
				PreparedStatement deleteStatement = null;
				ResultSet resultSet = null;

				try {
					selectStatement = connection.prepareStatement("""
							SELECT amount
							FROM enemy_items
							WHERE enemy_id = ? AND item_id = ?
						""");
					selectStatement.setInt(1, enemy.getId());
					selectStatement.setInt(2, item.getId());
					resultSet = selectStatement.executeQuery();

					if (!resultSet.next()) {
						return true;
					}

					int delta = item.getAmount() == null ? 1 : item.getAmount();
					if (delta <= 0) {
						return true;
					}

					int currentAmount = resultSet.getInt(1);
					int newAmount = currentAmount - delta;

					if (newAmount > 0) {
						updateStatement = connection.prepareStatement("""
								UPDATE enemy_items
								SET amount = ?
								WHERE enemy_id = ? AND item_id = ?
							""");
						updateStatement.setInt(1, newAmount);
						updateStatement.setInt(2, enemy.getId());
						updateStatement.setInt(3, item.getId());
						updateStatement.executeUpdate();
					} else {
						deleteStatement = connection.prepareStatement("""
								DELETE FROM enemy_items
								WHERE enemy_id = ? AND item_id = ?
							""");
						deleteStatement.setInt(1, enemy.getId());
						deleteStatement.setInt(2, item.getId());
						deleteStatement.executeUpdate();
					}

					return true;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(selectStatement);
					DBUtil.closeQuietly(updateStatement);
					DBUtil.closeQuietly(deleteStatement);
				}
			}
		});
	}


	// Rooms
	@Override
	public Room getRoomById(Integer id) {
		return executeTransaction(new Transaction<Room>() {
			@Override
			public Room execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet resultSet = null;

				try {
					statement = connection.prepareStatement("""
							SELECT id, name, description, asset_name
							FROM rooms
							WHERE id = ?
						""");
					statement.setInt(1, id);
					resultSet = statement.executeQuery();

					if (!resultSet.next()) {
						return null;
					}

					Integer databaseId = resultSet.getInt(1);
					String name = resultSet.getString(2);
					String description = resultSet.getString(3);
					String assetName = resultSet.getString(4);

					return new Room(databaseId, name, description, assetName);
				} finally {
					DBUtil.closeQuietly(statement);
					DBUtil.closeQuietly(resultSet);
				}
			}
		});
	}

	@Override
	public HashMap<String, RoomConnection> getConnectionsForRoom(Room room) {
		return executeTransaction(new Transaction<HashMap<String, RoomConnection>>() {
			@Override
			public HashMap<String, RoomConnection> execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet resultSet = null;

				try {
					statement = connection.prepareStatement("""
							SELECT
								room_connections.description,
								room_connections.direction,
								rooms.id,
								rooms.name,
								rooms.description,
								rooms.asset_name
							FROM room_connections, rooms
							WHERE rooms.id = room_connections.destination_id AND room_connections.source_id = ?
						""");
					statement.setInt(1, room.getId());
					resultSet = statement.executeQuery();

					HashMap<String, RoomConnection> result = new HashMap<>();

					while (resultSet.next()) {
						String connectionDescription = resultSet.getString(1);
						String direction = resultSet.getString(2);
						Integer roomId = resultSet.getInt(3);
						String roomName = resultSet.getString(4);
						String roomDescription = resultSet.getString(5);
						String assetName = resultSet.getString(6);

						Room room = new Room(roomId, roomName, roomDescription, assetName);
						RoomConnection roomConnection = new RoomConnection(room, connectionDescription);

						result.put(direction, roomConnection);
					}

					return result;
				} finally {
					DBUtil.closeQuietly(statement);
					DBUtil.closeQuietly(resultSet);
				}
			}
		});
	}

	@Override
	public HashMap<Integer, NPC> getNPCsForRoom(Room room) {
		if (room == null) {
			return new HashMap<>();
		}

		return executeTransaction(new Transaction<HashMap<Integer, NPC>>() {
			@Override
			public HashMap<Integer, NPC> execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet resultSet = null;

				try {
					statement = connection.prepareStatement("""
							SELECT
								npcs.id,
								npcs.name,
								npcs.max_health,
								npcs.health,
								npcs.greeting,
								npcs.goodbye
							FROM room_npcs, npcs
							WHERE npcs.id = room_npcs.npc_id AND room_npcs.room_id = ?
						""");
					statement.setInt(1, room.getId());
					resultSet = statement.executeQuery();

					HashMap<Integer, NPC> result = new HashMap<>();
					while (resultSet.next()) {
						Integer id = resultSet.getInt(1);
						String name = resultSet.getString(2);
						Integer maxHealth = resultSet.getInt(3);
						Integer health = resultSet.getInt(4);
						String greeting = resultSet.getString(5);
						String goodbye = resultSet.getString(6);
						result.put(id, new NPC(id, name, maxHealth, health, greeting, goodbye));
					}
					return result;
				} finally {
					DBUtil.closeQuietly(statement);
					DBUtil.closeQuietly(resultSet);
				}
			}
		});
	}

	@Override
	public HashMap<Integer, Enemy> getEnemiesForRoom(Room room) { // Hamed
		if (room == null) {
			return new HashMap<>();
		}

		return executeTransaction(new Transaction<HashMap<Integer, Enemy>>() {
			@Override
			public HashMap<Integer, Enemy> execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet resultSet = null;

				try {
					statement = connection.prepareStatement("""
							SELECT
								enemies.id,
								enemies.name,
								enemies.max_health,
								room_enemies.health
							FROM room_enemies, enemies
							WHERE enemies.id = room_enemies.enemy_id AND room_enemies.room_id = ?
						""");
					statement.setInt(1, room.getId());
					resultSet = statement.executeQuery();

					HashMap<Integer, Enemy> result = new HashMap<>();
					while (resultSet.next()) {
						Integer id = resultSet.getInt(1);
						String name = resultSet.getString(2);
						Integer maxHealth = resultSet.getInt(3);
						Integer health = resultSet.getInt(4);
						result.put(id, new Enemy(id, name, maxHealth, health));
					}
					return result;
				} finally {
					DBUtil.closeQuietly(statement);
					DBUtil.closeQuietly(resultSet);
				}
			}
		});
	}

	@Override
	public HashMap<Integer, Item> getItemsForRoom(Room room) {
		if (room == null) {
			return new HashMap<>();
		}

		return executeTransaction(new Transaction<HashMap<Integer, Item>>() {
			@Override
			public HashMap<Integer, Item> execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet resultSet = null;

				try {
					statement = connection.prepareStatement("""
							SELECT
								items.id,
								items.name,
								items.description,
								items.asset_name,
								items.value,
								items.type,
								items.heal_amount,
								items.defense,
								items.active_armor,
								room_items.amount
							FROM room_items, items
							WHERE items.id = room_items.item_id AND room_items.room_id = ?
						""");
					statement.setInt(1, room.getId());
					resultSet = statement.executeQuery();

					return getItemsFromResultSet(resultSet);
				} finally {
					DBUtil.closeQuietly(statement);
					DBUtil.closeQuietly(resultSet);
				}
			}
		});
	}

	@Override
	public void addItemToRoom(Room room, Item item) {
		if (room == null || item == null) {
			return;
		}

		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection connection) throws SQLException {
				PreparedStatement selectStatement = null;
				PreparedStatement insertStatement = null;
				PreparedStatement updateStatement = null;
				ResultSet resultSet = null;

				try {
					selectStatement = connection.prepareStatement("""
							SELECT amount
							FROM room_items
							WHERE room_id = ? AND item_id = ?
						""");
					selectStatement.setInt(1, room.getId());
					selectStatement.setInt(2, item.getId());
					resultSet = selectStatement.executeQuery();

					int delta = item.getAmount() == null ? 1 : item.getAmount();
					if (delta <= 0) {
						return true;
					}

					if (resultSet.next()) {
						int currentAmount = resultSet.getInt(1);
						int newAmount = currentAmount + delta;

						updateStatement = connection.prepareStatement("""
								UPDATE room_items
								SET amount = ?
								WHERE room_id = ? AND item_id = ?
							""");
						updateStatement.setInt(1, newAmount);
						updateStatement.setInt(2, room.getId());
						updateStatement.setInt(3, item.getId());
						updateStatement.executeUpdate();
					} else {
						insertStatement = connection.prepareStatement("""
								INSERT INTO room_items (room_id, item_id, amount)
								VALUES (?, ?, ?)
							""");
						insertStatement.setInt(1, room.getId());
						insertStatement.setInt(2, item.getId());
						insertStatement.setInt(3, delta);
						insertStatement.executeUpdate();
					}

					return true;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(selectStatement);
					DBUtil.closeQuietly(insertStatement);
					DBUtil.closeQuietly(updateStatement);
				}
			}
		});
	}

	@Override
	public void removeItemFromRoom(Room room, Item item) {
		if (room == null || item == null) {
			return;
		}

		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection connection) throws SQLException {
				PreparedStatement selectStatement = null;
				PreparedStatement updateStatement = null;
				PreparedStatement deleteStatement = null;
				ResultSet resultSet = null;

				try {
					selectStatement = connection.prepareStatement("""
							SELECT amount
							FROM room_items
							WHERE room_id = ? AND item_id = ?
						""");
					selectStatement.setInt(1, room.getId());
					selectStatement.setInt(2, item.getId());
					resultSet = selectStatement.executeQuery();

					if (!resultSet.next()) {
						return true;
					}

					int delta = item.getAmount() == null ? 1 : item.getAmount();
					if (delta <= 0) {
						return true;
					}

					int currentAmount = resultSet.getInt(1);
					int newAmount = currentAmount - delta;

					if (newAmount > 0) {
						updateStatement = connection.prepareStatement("""
								UPDATE room_items
								SET amount = ?
								WHERE room_id = ? AND item_id = ?
							""");
						updateStatement.setInt(1, newAmount);
						updateStatement.setInt(2, room.getId());
						updateStatement.setInt(3, item.getId());
						updateStatement.executeUpdate();
					} else {
						deleteStatement = connection.prepareStatement("""
								DELETE FROM room_items
								WHERE room_id = ? AND item_id = ?
							""");
						deleteStatement.setInt(1, room.getId());
						deleteStatement.setInt(2, item.getId());
						deleteStatement.executeUpdate();
					}

					return true;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(selectStatement);
					DBUtil.closeQuietly(updateStatement);
					DBUtil.closeQuietly(deleteStatement);
				}
			}
		});
	}


	// Player
	@Override
	public Player getPlayer() {
		return executeTransaction(new Transaction<Player>() {
			@Override
			public Player execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet resultSet = null;

				try {
					statement = connection.prepareStatement("""
							SELECT room_id, state, coins, max_health, health, last_command, confirming
							FROM player
						""");
					resultSet = statement.executeQuery();

					if (!resultSet.next()) {
						throw new IllegalStateException("No player exists");
					}

					Room room = getRoomById(resultSet.getInt(1));
					Integer stateOrdinal = resultSet.getInt(2);
					PlayerState state = PlayerState.values()[stateOrdinal];
					Integer coins = resultSet.getInt(3);
					Integer health = resultSet.getInt(4);
					Integer maxHealth = resultSet.getInt(5);
					Integer lastCommandOrdinal = resultSet.getInt(6);
					Command lastCommand = Command.values()[lastCommandOrdinal];
					Boolean confirming = resultSet.getBoolean(7);

					Player player = new Player(maxHealth, health, state, room, coins, lastCommand, confirming);

					return player;
				} finally {
					DBUtil.closeQuietly(statement);
					DBUtil.closeQuietly(resultSet);
				}
			}
		});
	}

	@Override
	public NPC getNpcForPlayer() {
		return executeTransaction(new Transaction<NPC>() {
			@Override
			public NPC execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet resultSet = null;

				try {
					statement = connection.prepareStatement(
						"""
								SELECT
									npcs.id,
									npcs.name,
									npcs.max_health,
									npcs.health,
									npcs.greeting,
									npcs.goodbye
								FROM npcs
								WHERE npcs.id = player.current_npc
							""");

					resultSet = statement.executeQuery();

					if (!resultSet.next()) {
						return null;
					}

					return new NPC(
						resultSet.getInt(1),
						resultSet.getString(2),
						resultSet.getInt(3),
						resultSet.getInt(4),
						resultSet.getString(5),
						resultSet.getString(6)
					);
				} finally {
					DBUtil.closeQuietly(statement);
					DBUtil.closeQuietly(resultSet);
				}
			}
		});
	}

	@Override
	public HashMap<Integer, Item> getItemsForPlayer() {
		return executeTransaction(new Transaction<HashMap<Integer, Item>>() {
			@Override
			public HashMap<Integer, Item> execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet resultSet = null;

				try {
					statement = connection.prepareStatement("""
							SELECT
								items.id,
								items.name,
								items.description,
								items.asset_name,
								items.value,
								items.type,
								items.heal_amount,
								items.defense,
								items.active_armor,
								player_items.amount
							FROM player_items, items
							WHERE items.id = player_items.item_id
						""");
					resultSet = statement.executeQuery();

					return getItemsFromResultSet(resultSet);
				} finally {
					DBUtil.closeQuietly(statement);
					DBUtil.closeQuietly(resultSet);
				}
			}
		});
	}

	@Override
	public void addItemToPlayer(Item item) {
		if (item == null) {
			return;
		}

		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection connection) throws SQLException {
				PreparedStatement selectStatement = null;
				PreparedStatement insertStatement = null;
				PreparedStatement updateStatement = null;
				ResultSet resultSet = null;

				try {
					selectStatement = connection.prepareStatement("""
							SELECT amount
							FROM player_items
							WHERE item_id = ?
						""");
					selectStatement.setInt(1, item.getId());
					resultSet = selectStatement.executeQuery();

					int delta = item.getAmount() == null ? 1 : item.getAmount();
					if (delta <= 0) {
						return true;
					}

					if (resultSet.next()) {
						int currentAmount = resultSet.getInt(1);
						int newAmount = currentAmount + delta;

						updateStatement = connection.prepareStatement("""
								UPDATE player_items
								SET amount = ?
								WHERE item_id = ?
							""");
						updateStatement.setInt(1, newAmount);
						updateStatement.setInt(2, item.getId());
						updateStatement.executeUpdate();
					} else {
						insertStatement = connection.prepareStatement("""
								INSERT INTO player_items (item_id, amount)
								VALUES (?, ?)
							""");
						insertStatement.setInt(1, item.getId());
						insertStatement.setInt(2, delta);
						insertStatement.executeUpdate();
					}

					return true;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(selectStatement);
					DBUtil.closeQuietly(insertStatement);
					DBUtil.closeQuietly(updateStatement);
				}
			}
		});
	}

	@Override
	public void removeItemFromPlayer(Item item) {
		if (item == null) {
			return;
		}

		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection connection) throws SQLException {
				PreparedStatement selectStatement = null;
				PreparedStatement updateStatement = null;
				PreparedStatement deleteStatement = null;
				ResultSet resultSet = null;

				try {
					selectStatement = connection.prepareStatement("""
							SELECT amount
							FROM player_items
							WHERE item_id = ?
						""");
					selectStatement.setInt(1, item.getId());
					resultSet = selectStatement.executeQuery();

					if (!resultSet.next()) {
						return true;
					}

					int delta = item.getAmount() == null ? 1 : item.getAmount();
					if (delta <= 0) {
						return true;
					}

					int currentAmount = resultSet.getInt(1);
					int newAmount = currentAmount - delta;

					if (newAmount > 0) {
						updateStatement = connection.prepareStatement("""
								UPDATE player_items
								SET amount = ?
								WHERE item_id = ?
							""");
						updateStatement.setInt(1, newAmount);
						updateStatement.setInt(2, item.getId());
						updateStatement.executeUpdate();
					} else {
						deleteStatement = connection.prepareStatement("""
								DELETE FROM player_items
								WHERE item_id = ?
							""");
						deleteStatement.setInt(1, item.getId());
						deleteStatement.executeUpdate();
					}

					return true;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(selectStatement);
					DBUtil.closeQuietly(updateStatement);
					DBUtil.closeQuietly(deleteStatement);
				}
			}
		});
	}

	@Override
	public void setPlayerRoom(Integer roomId) {
		executeTransaction(new Transaction<Void>() {
			@Override
			public Void execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;

				try {
					statement = connection.prepareStatement(
						"UPDATE player SET room_id = ?"
					);

					statement.setInt(1, roomId);

					int rowsUpdated = statement.executeUpdate();

					if (rowsUpdated == 0) {
						throw new IllegalStateException("No player exists");
					}

					return null;
				} finally {
					DBUtil.closeQuietly(statement);
				}
			}
		});
	}

	@Override
	public void setPlayerCoins(Integer coins) {
		executeTransaction(new Transaction<Void>() {
			@Override
			public Void execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;

				try {
					statement = connection.prepareStatement(
						"UPDATE player SET coins = ?"
					);

					statement.setInt(1, coins);

					int rowsUpdated = statement.executeUpdate();

					if (rowsUpdated == 0) {
						throw new IllegalStateException("No player exists");
					}

					return null;
				} finally {
					DBUtil.closeQuietly(statement);
				}
			}
		});
	}

	@Override
	public void setPlayerState(PlayerState playerState) {
		executeTransaction(new Transaction<Void>() {
			@Override
			public Void execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;

				try {
					statement = connection.prepareStatement(
						"UPDATE player SET state = ?"
					);

					statement.setInt(1, playerState.ordinal());

					int rowsUpdated = statement.executeUpdate();

					if (rowsUpdated == 0) {
						throw new IllegalStateException("No player exists");
					}

					return null;
				} finally {
					DBUtil.closeQuietly(statement);
				}
			}
		});
	}

	@Override
	public void setPlayerNPC(NPC npc) {
		executeTransaction(new Transaction<Void>() {
			@Override
			public Void execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;

				try {
					statement = connection.prepareStatement(
						"UPDATE player SET current_npc = ?"
					);

					statement.setInt(1, npc.getId());

					int rowsUpdated = statement.executeUpdate();

					if (rowsUpdated == 0) {
						throw new IllegalStateException("No player exists");
					}

					return null;
				} finally {
					DBUtil.closeQuietly(statement);
				}
			}
		});
	}

	@Override
	public void setLastCommand(Command command) {
		executeTransaction(new Transaction<Void>() {
			@Override
			public Void execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;

				try {
					statement = connection.prepareStatement(
						"UPDATE player SET last_command = ?"
					);
					statement.setInt(1, command.ordinal());

					int rowsUpdated = statement.executeUpdate();

					if (rowsUpdated == 0) {
						throw new IllegalStateException("No player exists");
					}

					return null;
				} finally {
					DBUtil.closeQuietly(statement);
				}
			}
		});
	}

	@Override
	public void setConfirming(Boolean confirming) {
		executeTransaction(new Transaction<Void>() {
			@Override
			public Void execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;

				try {
					statement = connection.prepareStatement(
						"UPDATE player SET confirming = ?"
					);
					statement.setBoolean(1, confirming);

					int rowsUpdated = statement.executeUpdate();

					if (rowsUpdated == 0) {
						throw new IllegalStateException("No player exists");
					}

					return null;
				} finally {
					DBUtil.closeQuietly(statement);
				}
			}
		});
	}
}
