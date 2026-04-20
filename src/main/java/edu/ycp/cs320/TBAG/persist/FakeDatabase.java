package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.controller.Command;
import edu.ycp.cs320.TBAG.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FakeDatabase implements Database {
	// Dialog
	private HashMap<Integer, String> dialog = new HashMap<>();

	// Items
	private HashMap<Integer, Item> items = new HashMap<>();
	private HashMap<Integer, WeaponAbility> weaponAbilities = new HashMap<>();

	// NPCs
	private HashMap<Integer, NPC> npcs = new HashMap<>();

	// Enemies
	private HashMap<Integer, Enemy> enemies = new HashMap<>();

	// Rooms
	private HashMap<Integer, Room> rooms = new HashMap<>();

	// Player
	private Player player = null;


	// General purpose methods
	@Override
	public void loadInitialData() {
		try {
			// Dialog
			dialog.putAll(InitialData.getDialog());

			// Items
			items.putAll(InitialData.getItems());
			weaponAbilities.putAll(InitialData.getWeaponAbilities());
			for (Pair<Integer, Integer> abilityJunction : InitialData.getWeaponAbilitiesJunction()) {
				Item item = items.get(abilityJunction.getLeft());
				if (!(item instanceof Weapon weapon)) continue;

				weapon.addAbility(weaponAbilities.get(abilityJunction.getRight()));
			}

			// NPCs
			npcs.putAll(InitialData.getNPCs());
			for (Map.Entry<Integer, ArrayList<Item>> entry : InitialData.getNPCItems().entrySet()) {
				npcs.get(entry.getKey()).getInventory().addItems(entry.getValue());
			}


			// Enemies
			enemies.putAll(InitialData.getEnemies());
			for (Map.Entry<Integer, ArrayList<Item>> entry : InitialData.getEnemyItems().entrySet()) {
				enemies.get(entry.getKey()).getInventory().addItems(entry.getValue());
			}

			// Rooms
			rooms.putAll(InitialData.getRooms());
			for (Map.Entry<Integer, HashMap<String, RoomConnection>> entry : InitialData.getRoomConnections().entrySet()) {
				rooms.get(entry.getKey()).getRoomConnections().putAll(entry.getValue());
			}
			for (Map.Entry<Integer, ArrayList<Item>> entry : InitialData.getRoomItems().entrySet()) {
				rooms.get(entry.getKey()).getInventory().addItems(entry.getValue());
			}
			for (Map.Entry<Integer, ArrayList<NPC>> entry : InitialData.getRoomNPCs().entrySet()) {
				rooms.get(entry.getKey()).addNPCs(entry.getValue());
			}
			for (Map.Entry<Integer, ArrayList<Enemy>> entry : InitialData.getRoomEnemies().entrySet()) {
				rooms.get(entry.getKey()).addEnemies(entry.getValue());
			}

			// Player
			player = InitialData.getPlayer();
			player.getInventory().addItems(new ArrayList<>(InitialData.getPlayerItems().values()));
		} catch (IllegalStateException exception) {
			throw new IllegalStateException("Initial data is incorrect", exception);
		} catch (IOException exception) {
			throw new IllegalStateException("Couldn't read initial data", exception);
		}
	}

	@Override
	public Boolean reset() {
		player = null;
		rooms.clear();
		items.clear();

		loadInitialData();

		return true;
	}


	// Dialog methods
	@Override
	public ArrayList<String> getDialog() {
		ArrayList<Integer> keys = new ArrayList<>(dialog.keySet());
		Collections.sort(keys);

		ArrayList<String> values = new ArrayList<>();
		for (Integer key : keys) {
			values.add(dialog.get(key));
		}

		return values;
	}

	@Override
	public void addDialog(String text) {
		dialog.put(dialog.size(), text);
	}

	@Override
	public void clearDialog() {
		dialog.clear();
	}

	// Player-related methods
	@Override
	public Player getPlayer() {
		if (player == null) {
			throw new IllegalStateException("No player exists");
		}

		return player;
	}

	@Override
	public void setPlayerRoom(Integer roomId) {
		if (player == null) {
			throw new IllegalStateException("No player exists");
		}
		Room room = rooms.get(roomId);
		player.setRoom(room);
	}

	@Override
	public void setPlayerCoins(Integer coins) {
		if (player == null) {
			throw new IllegalStateException("No player exists");
		}
		player.setCoins(coins);
	}

	@Override
	public void setPlayerState(PlayerState playerState) {
		if (player == null) {
			throw new IllegalStateException("No player exists");
		}
		player.setState(playerState);
	}

	@Override
	public void setPlayerNPC(NPC npc) {
		if (player == null) {
			throw new IllegalStateException("No player exists");
		}
		player.setCurrentNPC(npc);
	}

	@Override
	public void setLastCommand(Command command) {
		player.setLastCommand(command);
	}

	@Override
	public void setConfirming(Boolean confirming) {
		player.setConfirming(confirming);
	}

	@Override
	public void addItemToPlayer(Item item) {
		if (item == null || player == null) {
			return;
		}

		Integer itemId = item.getId();
		if (itemId == null) {
			return;
		}

		Item existing = player.getInventory().getItems().get(itemId);
		int delta = item.getAmount() == null ? 1 : item.getAmount();

		if (delta <= 0) {
			return;
		}

		if (existing == null) {
			player.getInventory().addItem(item);
		} else {
			existing.setAmount(existing.getAmount() + delta);
		}
	}

	@Override
	public void removeItemFromPlayer(Item item) {
		if (item == null || player == null) {
			return;
		}

		Integer itemId = item.getId();
		if (itemId == null) {
			return;
		}

		Item existing = player.getInventory().getItems().get(itemId);
		if (existing == null) {
			return;
		}

		int delta = item.getAmount() == null ? 1 : item.getAmount();
		if (delta <= 0) {
			return;
		}

		int newAmount = existing.getAmount() - delta;
		if (newAmount > 0) {
			existing.setAmount(newAmount);
		} else {
			player.getInventory().removeItem(existing);
		}
	}


	// Room-related methods
	@Override
	public Room getRoomById(Integer id) {
		return rooms.get(id);
	}

	@Override
	public HashMap<String, RoomConnection> getConnectionsForRoom(Room room) {
		return rooms.get(room.getID()).getRoomConnections();
	}

	@Override
	public void addItemToRoom(Room room, Item item) {
		if (room == null || item == null) {
			return;
		}

		Integer itemId = item.getId();
		if (itemId == null) {
			return;
		}

		Item existing = room.getInventory().getItems().get(itemId);
		int delta = item.getAmount() == null ? 1 : item.getAmount();

		if (delta <= 0) {
			return;
		}

		if (existing == null) {
			room.getInventory().addItem(item);
		} else {
			existing.setAmount(existing.getAmount() + delta);
		}
	}

	@Override
	public void removeItemFromRoom(Room room, Item item) {
		if (room == null || item == null) {
			return;
		}

		Integer itemId = item.getId();
		if (itemId == null) {
			return;
		}

		Item existing = room.getInventory().getItems().get(itemId);
		if (existing == null) {
			return;
		}

		int delta = item.getAmount() == null ? 1 : item.getAmount();
		if (delta <= 0) {
			return;
		}

		int newAmount = existing.getAmount() - delta;
		if (newAmount > 0) {
			existing.setAmount(newAmount);
		} else {
			room.getInventory().removeItem(existing);
		}
	}


	// Item-related methods
	@Override
	public HashMap<Integer, Item> getItemsForPlayer() {
		if (player == null) {
			return new HashMap<>();
		}

		return new HashMap<>(player.getInventory().getItems());
	}

	@Override
	public HashMap<Integer, Item> getItemsForRoom(Room room) {
		if (room == null) {
			return new HashMap<>();
		}

		return new HashMap<>(room.getInventory().getItems());
	}

	@Override
	public HashMap<Integer, Item> getItemsForNPC(NPC npc) {
		if (npc == null) {
			return new HashMap<>();
		}

		return new HashMap<>(npc.getInventory().getItems());
	}

	@Override
	public HashMap<Integer, Item> getItemsForEnemy(Enemy enemy) { // Hamed
		if (enemy == null) {
			return new HashMap<>();
		}

		return new HashMap<>(enemy.getInventory().getItems());
	}


	// NPC-related methods
	@Override
	public HashMap<Integer, NPC> getNPCsForRoom(Room room) {
		if (room == null) {
			return new HashMap<>();
		}
		return new HashMap<>(room.getNpcs());
	}


	// Enemy-related methods
	@Override
	public HashMap<Integer, Enemy> getEnemiesForRoom(Room room) { // Hamed
		if (room == null) {
			return new HashMap<>();
		}

		return new HashMap<>(room.getEnemies());
	}

	@Override
	public void addItemToEnemy(Enemy enemy, Item item) { // Hamed
		if (enemy == null || item == null) {
			return;
		}

		Integer itemId = item.getId();
		if (itemId == null) {
			return;
		}

		Item existing = enemy.getInventory().getItems().get(itemId);
		int delta = item.getAmount() == null ? 1 : item.getAmount();

		if (delta <= 0) {
			return;
		}

		if (existing == null) {
			enemy.getInventory().addItem(item);
		} else {
			existing.setAmount(existing.getAmount() + delta);
		}
	}

	@Override
	public void removeItemFromEnemy(Enemy enemy, Item item) { // Hamed
		if (enemy == null || item == null) {
			return;
		}

		Integer itemId = item.getId();
		if (itemId == null) {
			return;
		}

		Item existing = enemy.getInventory().getItems().get(itemId);
		if (existing == null) {
			return;
		}

		int delta = item.getAmount() == null ? 1 : item.getAmount();
		if (delta <= 0) {
			return;
		}

		int newAmount = existing.getAmount() - delta;
		if (newAmount > 0) {
			existing.setAmount(newAmount);
		} else {
			enemy.getInventory().removeItem(existing);
		}
	}


	// WeaponAbility-related methods
	@Override
	public HashMap<Integer, WeaponAbility> getAbilitiesForWeapon(Weapon weapon) { // Hamed
		if (weapon == null) {
			return new HashMap<>();
		}

		return new HashMap<>(weapon.getAbilities());
	}
}
