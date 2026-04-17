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
				Integer value = parseIntegerOrNull(iterator.next());
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
		HashMap<Integer, String> dialog = new HashMap<>();
		ReadCSV dialogFile = new ReadCSV("dialog.csv");

		try {
			while (true) {
				List<String> tuple = dialogFile.next();
				if (tuple == null) break;

				Iterator<String> iterator = tuple.iterator();

				String text = iterator.next();

				dialog.put(dialog.size(), text);
			}

			return dialog;
		} finally {
			dialogFile.close();
		}
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
				Room playerRoom = rooms.get(roomIdA);
				if (playerRoom == null) {
					throw new IllegalStateException(
						"The room the player is in with the id \""
							+ roomId
							+ "\" does not exist in the initial CSV data."
					);
				}

				String stateString = iterator.next();
				PlayerState state = PlayerState.getByName(stateString);
				if (state == null) {
					throw new IllegalStateException(
						"Invalid player state: \"" + stateString + "\""
					);
				}

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
	public static HashMap<Integer, Item> getPlayerItems() throws IOException {
		ensureItemsLoaded();

		HashMap<Integer, Item> result = new HashMap<>();
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
					result.put(item.getId(), item);
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
		ReadCSV roomsFile = new ReadCSV("rooms.csv");

		try {
			while (true) {
				List<String> tuple = roomsFile.next();
				if (tuple == null) break;

				Iterator<String> it = tuple.iterator();

				String roomKey = it.next();   // CSV string ID
				String name = it.next();
				String description = it.next();
				String assetName = it.next();

				// Map CSV string ID → integer ID
				Integer id = roomIds.size();
				roomIds.put(roomKey, id);

				Room room = new Room(id, name, description, assetName);
				rooms.put(id, room);
			}

			return rooms;
		} finally {
			roomsFile.close();
		}
	}

	/**
	 * Returns a list of maps between room ids and a hashmap of (directions and room connection)
	 */
	public static HashMap<Integer, HashMap<String, RoomConnection>> getRoomConnections() throws IOException, IllegalStateException {
		HashMap<Integer, HashMap<String, RoomConnection>> result = new HashMap<>();
		ReadCSV connFile = new ReadCSV("room_connections.csv");

		try {
			while (true) {
				List<String> tuple = connFile.next();
				if (tuple == null) break;

				Iterator<String> it = tuple.iterator();

				String fromKey = it.next();
				String direction = it.next();
				String toKey = it.next();
				String description = it.next();

				Integer fromId = roomIds.get(fromKey);
				Integer toId = roomIds.get(toKey);

				if (fromId == null || toId == null) {
					throw new IllegalStateException(
						"Invalid room reference in connections CSV: " +
							fromKey + " -> " + toKey
					);
				}

				Room targetRoom = rooms.get(toId);

				RoomConnection connection =
					new RoomConnection(targetRoom, description);

				result
					.computeIfAbsent(fromId, k -> new HashMap<>())
					.put(direction, connection);
			}

			return result;

		} finally {
			connFile.close();
		}
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
	public static HashMap<Integer, Item> getItems() throws IOException, IllegalStateException {
		HashMap<Integer, Item> items = new HashMap<>();
		ReadCSV itemsFile = new ReadCSV("items.csv");

		try {
			while (true) {
				List<String> tuple = itemsFile.next();
				if (tuple == null) break;

				Iterator<String> iterator = tuple.iterator();

				Integer id = parseIntegerOrNull(iterator.next());
				String name = iterator.next();
				String description = iterator.next();
				String assetName = iterator.next();
				Integer value = parseIntegerOrNull(iterator.next());
				String typeString = iterator.next().toLowerCase();
				ItemType type = ItemType.getByName(typeString);
				if (type == null) {
					throw new IllegalStateException("The item: \"" + id + "\" has an invalid type " + typeString);
				}
				Integer healAmount = parseIntegerOrNull(iterator.next());
				Integer defense = parseIntegerOrNull(iterator.next());
				Boolean activeArmor = parseBooleanOrNull(iterator.next().toLowerCase());

				if (type == ItemType.ARMOR) {
					if (defense == null) {
						throw new IllegalStateException("The armor: \"" + id + "\" does not have a defense set.");
					}
					if (activeArmor == null) {
						throw new IllegalStateException("The armor: \"" + id + "\" has to have active set to \"true\" or \"false\".");
					}
					items.put(id, new Armor(id, name, description, defense, activeArmor, value, assetName));
				} else if (type == ItemType.HEALING) {
					if (healAmount == null) {
						throw new IllegalStateException("The healing item: \"" + id + "\" does not have a heal amount set.");
					}
					items.put(id, new HealingItem(id, name, description, healAmount, value, assetName));
				} else if (type == ItemType.WEAPON) {
					items.put(id, new Weapon(id, name, description, value, assetName));
				} else if (type == ItemType.ITEM) {
					items.put(id, new Item(id, name, description, value, assetName));
				} else {
					throw new IllegalStateException("The item: \"" + id + "\" has an invalid type " + typeString);
				}
			}

			return items;
		} finally {
			itemsFile.close();
		}
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

	
	// Utility methods

	private static Integer parseIntegerOrNull(String text) {
		Integer integer = null;

		try {
			integer = Integer.parseInt(text);
		} catch (NumberFormatException ignored) {
		}

		return integer;
	}

	private static Boolean parseBooleanOrNull(String text) {
		Boolean bool = null;

		if (text.equals("true")) bool = true;
		else if (text.equals("false")) bool = false;

		return bool;
	}
}
