package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.model.*;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

public class DerbyDatabase implements Database {
	// From lab 7
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


	// General purpose methods
	@Override
	public void loadInitialData() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection connection) throws SQLException {
				Player player;

				try {
					player = InitialData.getPlayer();
				} catch (IllegalStateException exception) {
					throw new IllegalStateException("Initial data is incorrect", exception);
				} catch (IOException exception) {
					throw new IllegalStateException("Couldn't read initial data", exception);
				}

				PreparedStatement addPlayer = null;

				try {
					addPlayer = connection.prepareStatement("""
							INSERT INTO players (room_id, state, coins, max_health, health)
							VALUES (?, ?, ?, ?, ?)
						""");
					addPlayer.setInt(1, player.getRoom().getID());
					addPlayer.setInt(2, player.getState().ordinal());
					addPlayer.setInt(3, player.getCoins());
					addPlayer.setInt(4, player.getMaxHealth());
					addPlayer.setInt(5, player.getHealth());
					addPlayer.executeUpdate();

					return true;
				} finally {
					DBUtil.closeQuietly(addPlayer);
				}
			}
		});
	}

	public void createTables() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection connection) throws SQLException {
				PreparedStatement addPlayerTableStatement = null;

				try {
					addPlayerTableStatement = connection.prepareStatement("""
							CREATE TABLE player (
								room_id INTEGER NOT NULL
								state INTEGER NOT NULL
								coins INTEGER NOT NULL
								max_health INTEGER NOT NULL
								health INTEGER NOT NULL
							);
						""");
					addPlayerTableStatement.executeUpdate();

					return true;
				} finally {
					DBUtil.closeQuietly(addPlayerTableStatement);
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
							LIMIT 1
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

					statement.setString(1, String.valueOf(playerState));

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
	public void addItemToPlayer(Item item) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	@Override
	public void removeItemFromPlayer(Item item) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	@Override
	public void setPlayerNPC(NPC npc) {
		throw new UnsupportedOperationException("TODO - implement");
	}


	// Room-related methods
	@Override
	public Room getRoomById(Integer id) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	@Override
	public HashMap<String, RoomConnection> getConnectionsForRoom(Room room) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	@Override
	public void addItemToRoom(Room room, Item item) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	@Override
	public void removeItemFromRoom(Room room, Item item) {
		throw new UnsupportedOperationException("TODO - implement");
	}


	// Item-related methods
	@Override
	public HashMap<Integer, Item> getItemsFromResultSet(ResultSet resultSet) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	@Override
	public HashMap<Integer, Item> getItemsForPlayer() {
		throw new UnsupportedOperationException("TODO - implement");
	}

	@Override
	public HashMap<Integer, Item> getItemsForRoom(Room room) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	@Override
	public HashMap<Integer, Item> getItemsForNPC(NPC npc) {
		throw new UnsupportedOperationException("TODO - implement");
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
		Connection conn = DriverManager.getConnection("jdbc:derby:test.db;create=true");

		// Set autocommit to false to allow execution of
		// multiple queries/statements as part of the same transaction.
		conn.setAutoCommit(false);

		return conn;
	}
}
