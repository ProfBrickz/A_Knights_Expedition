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

	private static void ensureItemsLoaded() throws IOException {
		if (!items.isEmpty()) {
			return;
		}

		ReadCSV itemsFile = new ReadCSV("items.csv");

		try {
			while (true) {
				List<String> tuple = itemsFile.next();
				if (tuple == null) break;

				Iterator<String> iterator = tuple.iterator();

				String itemKey = iterator.next();
				String name = iterator.next();
				String description = iterator.next();
				Integer value = Integer.parseInt(iterator.next());
				String type = iterator.next();
				String healAmountString = iterator.next();
				String defenseString = iterator.next();
				String activeArmorString = iterator.next();

				if (itemIds.containsKey(itemKey)) {
					throw new IllegalStateException(
						"Duplicate item id \"" + itemKey + "\" in items CSV."
					);
				}

				Integer id;
				try {
					id = Integer.parseInt(itemKey);
				} catch (NumberFormatException exception) {
					id = 0;
					while (items.containsKey(id) || itemIds.containsValue(id)) {
						id++;
					}
				}

				Item item;
				String normalizedType = type == null ? "" : type.trim().toLowerCase();

				if ("weapon".equals(normalizedType)) {
					item = new Weapon(id, name, description, value);
				} else if ("armor".equals(normalizedType)) {
					Integer defense = Integer.parseInt(defenseString);
					Boolean active = Boolean.parseBoolean(activeArmorString);
					item = new Armor(id, name, description, defense, active, value);
				} else if ("healing".equals(normalizedType)) {
					Integer healAmount = Integer.parseInt(healAmountString);
					item = new HealingItem(id, name, description, healAmount, value);
				} else {
					item = new Item(id, name, description, value);
				}

				items.put(id, item);
				itemIds.put(itemKey, id);
			}
		} finally {
			itemsFile.close();
		}
	}

	private static Item copyItemWithAmount(Item baseItem, Integer amount) {
		if (baseItem == null) {
			return null;
		}

		Integer id = baseItem.getId();
		String name = baseItem.getName();
		String description = baseItem.getDescription();
		Integer value = baseItem.getValue();

		Item copy;
		if (baseItem instanceof Weapon) {
			copy = new Weapon(id, name, description, value, amount);
		} else if (baseItem instanceof Armor) {
			Armor armor = (Armor) baseItem;
			copy = new Armor(id, name, description, armor.getDefense(), armor.getActive(), value, amount);
		} else if (baseItem instanceof HealingItem) {
			HealingItem healingItem = (HealingItem) baseItem;
			copy = new HealingItem(id, name, description, healingItem.getHealAmount(), value, amount);
		} else {
			copy = new Item(id, name, description, value, amount);
		}

		copy.setAssetName(baseItem.getAssetName());
		return copy;
	}

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
		ensureItemsLoaded();

		ArrayList<Item> result = new ArrayList<>();
		ReadCSV playerItemsFile = new ReadCSV("player_items.csv");

		try {
			while (true) {
				List<String> tuple = playerItemsFile.next();
				if (tuple == null) break;

				Iterator<String> iterator = tuple.iterator();
				String itemKey = iterator.next();
				Integer amount = Integer.parseInt(iterator.next());

				if (!itemIds.containsKey(itemKey)) {
					throw new IllegalStateException(
						"Player item \"" + itemKey + "\" does not exist in the items CSV."
					);
				}

				Integer itemId = itemIds.get(itemKey);
				Item baseItem = items.get(itemId);
				if (baseItem == null) {
					throw new IllegalStateException(
						"Player item \"" + itemKey + "\" does not exist in the items CSV."
					);
				}

				Item item = copyItemWithAmount(baseItem, amount);
				if (item != null) {
					result.add(item);
				}
			}

			return result;
		} finally {
			playerItemsFile.close();
		}
	}

	/**
	 * Rooms do not have items, npcs, or enemies
	 */
	public static HashMap<Integer, Room> getRooms() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	/**
	 * Returns a list of maps between room ids and a list of room connection
	 * Each connection is a room connection (without a room) and a room id
	 */
	public static HashMap<Integer, ArrayList<Pair<RoomConnection, Integer>>> getRoomConnections() throws IOException {
		throw new UnsupportedOperationException("TODO - implement");
	}

	/**
	 * Returns a map between room ids and a list of items
	 */
	public static HashMap<Integer, ArrayList<Item>> getRoomItems() throws IOException {
		ensureItemsLoaded();

		HashMap<Integer, ArrayList<Item>> result = new HashMap<>();
		ReadCSV roomItemsFile = new ReadCSV("room_items.csv");

		try {
			while (true) {
				List<String> tuple = roomItemsFile.next();
				if (tuple == null) break;

				Iterator<String> iterator = tuple.iterator();

				String roomKey = iterator.next();
				String itemKey = iterator.next();
				Integer amount = Integer.parseInt(iterator.next());

				if (!roomIds.containsKey(roomKey)) {
					throw new IllegalStateException(
						"The room with the id \"" + roomKey + "\" does not exist in the rooms CSV."
					);
				}

				if (!itemIds.containsKey(itemKey)) {
					throw new IllegalStateException(
						"The item with the id \"" + itemKey + "\" does not exist in the items CSV."
					);
				}

				Integer roomId = roomIds.get(roomKey);
				Integer itemId = itemIds.get(itemKey);
				Item baseItem = items.get(itemId);
				if (baseItem == null) {
					throw new IllegalStateException(
						"The item with the id \"" + itemKey + "\" does not exist in the items CSV."
					);
				}

				ArrayList<Item> roomItems = result.get(roomId);
				if (roomItems == null) {
					roomItems = new ArrayList<>();
					result.put(roomId, roomItems);
				}

				Item item = copyItemWithAmount(baseItem, amount);
				if (item != null) {
					roomItems.add(item);
				}
			}

			return result;
		} finally {
			roomItemsFile.close();
		}
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
