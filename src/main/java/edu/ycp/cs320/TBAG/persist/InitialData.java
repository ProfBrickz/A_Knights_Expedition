package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class InitialData {
	private static final HashMap<Integer, Room> rooms = new HashMap<>();
	private static final HashMap<Integer, Item> items = new HashMap<>();
	private static final HashMap<Integer, NPC> npcs = new HashMap<>();
	private static final HashMap<Integer, Enemy> enemies = new HashMap<>();
	// Might not need maps for all of these, or might need maps for other things but this is what they would look like
	private static final HashMap<String, Integer> roomIds = new HashMap<>();
	private static final HashMap<String, Integer> itemIds = new HashMap<>();
	private static final HashMap<String, Integer> npcIds = new HashMap<>();
	private static final HashMap<String, Integer> enemyIds = new HashMap<>();


	public static HashMap<Integer, String> getDialog() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public static Player getPlayer() throws IOException, IllegalStateException {
		ArrayList<Player> players = new ArrayList<>();
		ReadCSV playersFile = new ReadCSV("player.csv");

		try {
			while (true) {
				List<String> tuple = playersFile.next();
				if (tuple == null) break;

				Iterator<String> iterator = tuple.iterator();

				String roomId = iterator.next();
				if (!roomIds.containsKey(roomId)) {
					throw new IllegalStateException(
						"The room the player is in with the id \""
							+ roomId
							+ "\" does not exist in the initial CSV data."
					);
				}
				Integer roomIdA = roomIds.get(roomId);
				if (roomIdA == null) {
					throw new IllegalStateException(
						"The room the player is in with the id \""
							+ roomId
							+ "\" does not exist in the initial CSV data."
					);
				}
				Room playerRoom = rooms.get(roomIdA);
				if (playerRoom == null) {
					throw new IllegalStateException(
						"The room the player is in with the id \""
							+ roomId
							+ "\" does not exist in the initial CSV data."
					);
				}

				Integer stateOrdinal = Integer.parseInt(iterator.next());
				PlayerState state = PlayerState.values()[stateOrdinal];
				Integer coins = Integer.parseInt(iterator.next());
				Integer health = Integer.parseInt(iterator.next());
				Integer maxHealth = Integer.parseInt(iterator.next());

				Player player = new Player(maxHealth, health, state, null);
				player.setRoom(playerRoom);
				player.setCoins(coins);
				players.add(player);
			}

			if (players.size() > 1) {
				throw new IllegalStateException("There can not be more than one player in the initial CSV data.");
			} else if (players.isEmpty()) {
				throw new IllegalStateException("No player exists in the initial CSV data.");
			}

			return players.get(0);
		} finally {
			playersFile.close();
		}
	}

	/**
	 * Returns a list of the players items
	 */
	public static ArrayList<Item> getPlayerItems() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	/**
	 * Rooms do not have items, npcs, or enemies
	 */
	public static HashMap<Integer, Room> getRooms() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	/**
	 * Returns a list of maps between room ids and a hashmap of (directions and room connection)
	 */
	public static HashMap<Integer, HashMap<String, RoomConnection>> getRoomConnections() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	/**
	 * Returns a map between room ids and a list of items
	 */
	public static HashMap<Integer, ArrayList<Item>> getRoomItems() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	/**
	 * Returns a map between room ids and a list of enemies
	 */
	public static HashMap<Integer, ArrayList<Enemy>> getRoomEnemies() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	/**
	 * Returns a list of all items without amounts
	 */
	public static ArrayList<Item> getItems() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public static ArrayList<NPC> getNPCs() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	/**
	 * Returns a map between npc ids and a list of items
	 */
	public static HashMap<Integer, ArrayList<Item>> getNPCItems() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public static ArrayList<WeaponAbility> getWeaponAbilities() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	/**
	 * Returns a list of enemies without items
	 */
	public static ArrayList<Enemy> getEnemies() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	/**
	 * Returns a map between enemy ids and a list of items
	 */
	public static HashMap<Integer, ArrayList<Item>> getEnemyItems() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}
}
