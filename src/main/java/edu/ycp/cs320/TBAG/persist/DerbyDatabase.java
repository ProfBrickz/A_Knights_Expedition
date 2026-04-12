package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.model.*;

import java.io.IOException;
import java.sql.*;
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
		public ResultType execute(Connection conn) throws SQLException;
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
		Connection conn = DriverManager.getConnection("jdbc:derby:database.db;create=true");

		// Set autocommit too false to allow execution of
		// multiple queries/statements as part of the same transaction.
		conn.setAutoCommit(false);

		return conn;
	}

	/// From lab 7
	// The main method creates the database tables and loads the initial data.
	public static void main(String[] args) throws IOException {
		System.out.println("Creating tables...");
		DerbyDatabase db = new DerbyDatabase();
		db.createTables();

		System.out.println("Loading initial data...");
		db.loadInitialData();

		System.out.println("Library DB successfully initialized!");
	}


	// General purpose methods
	@Override
	public void loadInitialData() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection connection) throws SQLException {
				HashMap<Integer, String> dialog;
				Player player;
				HashMap<Integer, Room> rooms;
				// A map between a room's id and (a map of its directions and connections)
				HashMap<Integer, HashMap<String, RoomConnection>> roomConnections;

				try {
					dialog = InitialData.getDialog();
					rooms = InitialData.getRooms();
					roomConnections = InitialData.getRoomConnections();
					player = InitialData.getPlayer();
				} catch (IllegalStateException exception) {
					throw new IllegalStateException("Initial data is incorrect", exception);
				} catch (IOException exception) {
					throw new IllegalStateException("Couldn't read initial data", exception);
				}

				PreparedStatement addDialogStatement = null;
				PreparedStatement addPlayerStatement = null;
				PreparedStatement addRoomsStatement = null;
				PreparedStatement addRoomConnectionsStatement = null;

				try {
					addDialogStatement = connection.prepareStatement("""
							INSERT INTO dialog (id, text)
							VALUES (?, ?)
						""");
					for (Map.Entry<Integer, String> entry : dialog.entrySet()) {
						addDialogStatement.setInt(1, entry.getKey());
						addDialogStatement.setString(2, entry.getValue());
						addRoomsStatement.addBatch();
					}
					addDialogStatement.executeBatch();

					addPlayerStatement = connection.prepareStatement("""
							INSERT INTO player (room_id, state, coins, max_health, health)
							VALUES (?, ?, ?, ?, ?)
						""");
					addPlayerStatement.setInt(1, player.getRoom().getID());
					addPlayerStatement.setInt(2, player.getState().ordinal());
					addPlayerStatement.setInt(3, player.getCoins());
					addPlayerStatement.setInt(4, player.getMaxHealth());
					addPlayerStatement.setInt(5, player.getHealth());
					addPlayerStatement.executeUpdate();

					addRoomsStatement = connection.prepareStatement("""
							INSERT INTO rooms (id, name, description, asset_name)
							VALUES (?, ?, ?, ?)
						""");
					for (Room room : rooms.values()) {
						addRoomsStatement.setInt(1, room.getID());
						addRoomsStatement.setString(2, room.getName());
						addRoomsStatement.setString(3, room.getDescription());
						addRoomsStatement.setString(4, room.getAssetName());
						addRoomsStatement.addBatch();
					}
					addRoomsStatement.executeBatch();

					addRoomConnectionsStatement = connection.prepareStatement("""
							INSERT INTO room_connections (
								source_id,
								destination_id,
								direction,
								description
							)
							VALUES (?, ?, ?, ?)
						""");
					for (Map.Entry<Integer, HashMap<String, RoomConnection>> entry : roomConnections.entrySet()) {
						Integer roomId = entry.getKey();

						for (Map.Entry<String, RoomConnection> entry1 : entry.getValue().entrySet()) {
							String direction = entry1.getKey();
							RoomConnection roomConnection = entry1.getValue();

							addRoomConnectionsStatement.setInt(1, roomId);
							addRoomConnectionsStatement.setInt(2, roomConnection.getRoom().getID());
							addRoomConnectionsStatement.setString(3, direction);
							addRoomConnectionsStatement.setString(4, roomConnection.getDescription());
							addRoomConnectionsStatement.addBatch();
						}
					}
					addRoomConnectionsStatement.executeBatch();

					return true;
				} finally {
					DBUtil.closeQuietly(addDialogStatement);
					DBUtil.closeQuietly(addPlayerStatement);
					DBUtil.closeQuietly(addRoomsStatement);
					DBUtil.closeQuietly(addRoomConnectionsStatement);
				}
			}
		});
	}

	public void createTables() {
		final Integer DIALOG_MAX_LENGTH = 128;
		final Integer NAME_MAX_LENGTH = 128;
		final Integer DESCRIPTION_MAX_LENGTH = 512;
		final Integer DIRECTION_MAX_LENGTH = 64;

		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection connection) throws SQLException {
				PreparedStatement createDialogTableStatement = null;
				PreparedStatement createPlayerTableStatement = null;
				PreparedStatement createRoomsTableStatement = null;
				PreparedStatement createRoomConnectionsTableStatement = null;

				try {
					createDialogTableStatement = connection.prepareStatement("""
							CREATE TABLE dialog (
								id INTEGER PRIMARY KEY,
								text VARCHAR(%d) NOT NULL
							)
						""".formatted(DIALOG_MAX_LENGTH));
					createDialogTableStatement.executeUpdate();

					createPlayerTableStatement = connection.prepareStatement("""
							CREATE TABLE player (
								room_id INTEGER NOT NULL,
								state INTEGER NOT NULL,
								coins INTEGER NOT NULL,
								max_health INTEGER NOT NULL,
								health INTEGER NOT NULL
							)
						""");
					createPlayerTableStatement.executeUpdate();

					createRoomsTableStatement = connection.prepareStatement("""
							CREATE TABLE rooms (
								id INTEGER PRIMARY KEY,
								name VARCHAR(%d) NOT NULL,
								description VARCHAR(%d) NOT NULL,
								asset_name VARCHAR(%d) NOT NULL
							)
						""".formatted(
						NAME_MAX_LENGTH,
						DESCRIPTION_MAX_LENGTH,
						NAME_MAX_LENGTH
					));
					createRoomsTableStatement.executeUpdate();

					createRoomConnectionsTableStatement = connection.prepareStatement("""
							CREATE TABLE room_connections (
								source_id INTEGER,
								direction VARCHAR(%d) NOT NULL,
								destination_id INTEGER,
								description VARCHAR(%d) NOT NULL,
								locked BOOLEAN,
								locked_message VARCHAR(%d),
								PRIMARY KEY (source_id, destination_id)
							)
						""".formatted(
						DIRECTION_MAX_LENGTH,
						DESCRIPTION_MAX_LENGTH,
						DESCRIPTION_MAX_LENGTH
					));
					createRoomConnectionsTableStatement.execute();

					return true;
				} finally {
					DBUtil.closeQuietly(createDialogTableStatement);
					DBUtil.closeQuietly(createPlayerTableStatement);
					DBUtil.closeQuietly(createRoomsTableStatement);
					DBUtil.closeQuietly(createRoomConnectionsTableStatement);
				}
			}
		});
	}


	@Override
	public HashMap<Integer, String> getDialog() {
		return executeTransaction(new Transaction<HashMap<Integer, String>>() {
			@Override
			public HashMap<Integer, String> execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet resultSet = null;

				try {
					statement = connection.prepareStatement("""
							SELECT id, text
							FROM dialog
						""");
					resultSet = statement.executeQuery();

					HashMap<Integer, String> result = new HashMap<>();

					while (resultSet.next()) {
						Integer id = resultSet.getInt(1);
						String text = resultSet.getString(2);

						result.put(id, text);
					}

					return result;
				} finally {
					DBUtil.closeQuietly(statement);
					DBUtil.closeQuietly(resultSet);
				}
			}
		});
	}

	// Player-related methods
	@Override
	public Player getPlayer() {
		return executeTransaction(new Transaction<Player>() {
			@Override
			public Player execute(Connection connection) throws SQLException {
				PreparedStatement statement = null;
				ResultSet resultSet = null;

				try {
					statement = connection.prepareStatement("""
							SELECT room_id, state, coins, max_health, health
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

					Player player = new Player(maxHealth, health, state, room);
					player.setCoins(coins);

					return player;
				} finally {
					DBUtil.closeQuietly(statement);
					DBUtil.closeQuietly(resultSet);
				}
			}
		});
	}

	@Override
	public void setPlayerRoom(Integer roomId) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	@Override
	public void setPlayerCoins(Integer coins) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	@Override
	public void setPlayerState(PlayerState playerState) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	@Override
	public void setPlayerNPC(NPC npc) {
		throw new UnsupportedOperationException("TODO - implement");
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


	// Room-related methods
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
								rooms.description
							FROM room_connections, rooms
							WHERE rooms.id = room_connections.destination_id AND room_connections.source_id = ?
						""");
					statement.setInt(1, room.getID());
					resultSet = statement.executeQuery();

					HashMap<String, RoomConnection> result = new HashMap<>();

					while (resultSet.next()) {
						String connectionDescription = resultSet.getString(1);
						String direction = resultSet.getString(2);
						Integer roomId = resultSet.getInt(3);
						String roomName = resultSet.getString(4);
						String roomDescription = resultSet.getString(5);

						Room room = new Room(roomId, roomName, roomDescription);
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
					selectStatement.setInt(1, room.getID());
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
						updateStatement.setInt(2, room.getID());
						updateStatement.setInt(3, item.getId());
						updateStatement.executeUpdate();
					} else {
						insertStatement = connection.prepareStatement("""
								INSERT INTO room_items (room_id, item_id, amount)
								VALUES (?, ?, ?)
							""");
						insertStatement.setInt(1, room.getID());
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
					selectStatement.setInt(1, room.getID());
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
						updateStatement.setInt(2, room.getID());
						updateStatement.setInt(3, item.getId());
						updateStatement.executeUpdate();
					} else {
						deleteStatement = connection.prepareStatement("""
								DELETE FROM room_items
								WHERE room_id = ? AND item_id = ?
							""");
						deleteStatement.setInt(1, room.getID());
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


	// Item-related methods
	@Override
	public HashMap<Integer, Item> getItemsFromResultSet(ResultSet resultSet) {
		HashMap<Integer, Item> items = new HashMap<>();

		try {
			while (resultSet.next()) {
				Integer id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				String description = resultSet.getString("description");
				Integer value = resultSet.getInt("value");
				String type = resultSet.getString("type");
				Integer amount = resultSet.getInt("amount");

				if (resultSet.wasNull()) {
					amount = 1;
				}

				Item item;
				if (type != null) {
					type = type.trim().toLowerCase();
				}

				if ("weapon".equals(type)) {
					item = new Weapon(id, name, description, value, amount);
				} else if ("armor".equals(type)) {
					Integer defense = resultSet.getInt("defense");
					Boolean active = resultSet.getBoolean("active_armor");
					item = new Armor(id, name, description, defense, active, value, amount);
				} else if ("healing".equals(type)) {
					Integer healAmount = resultSet.getInt("heal_amount");
					item = new HealingItem(id, name, description, healAmount, value, amount);
				} else {
					item = new Item(id, name, description, value, amount);
				}

				items.put(id, item);
			}
		} catch (SQLException e) {
			throw new PersistenceException("Could not load items", e);
		}

		return items;
	}

	@Override
	public HashMap<Integer, Item> getItemsForPlayer(Player player) {
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
								items.value,
								items.type,
								items.heal_amount,
								items.defense,
								items.active_armor,
								room_items.amount
							FROM room_items, items
							WHERE items.id = room_items.item_id AND room_items.room_id = ?
						""");
					statement.setInt(1, room.getID());
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

	@Override
	public HashMap<Integer, Item> getItemsForEnemy(Enemy enemy) {
		throw new UnsupportedOperationException("TODO - implement");
	}


	// NPC-related methods
	@Override
	public HashMap<Integer, NPC> getNPCsForRoom(Room room) {
		throw new UnsupportedOperationException("TODO - implement");
	}


	// Enemy-related methods
	@Override
	public HashMap<Integer, Enemy> getEnemiesForRoom(Room room) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	@Override
	public void addItemToEnemy(Enemy enemy, Item item) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	@Override
	public void removeItemFromEnemy(Enemy enemy, Item item) {
		throw new UnsupportedOperationException("TODO - implement");
	}


	// WeaponAbility-related methods
	@Override
	public HashMap<Integer, WeaponAbility> getAbilitiesForWeapon(Weapon weapon) {
		throw new UnsupportedOperationException("TODO - implement");
	}
}
