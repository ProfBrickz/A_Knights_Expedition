package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.controller.Command;
import edu.ycp.cs320.TBAG.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FakeDatabase implements Database {
	// Dialog
	private final ArrayList<String> dialog = new ArrayList<>();
	private final ArrayList<String> commandHistory = new ArrayList<>();

	// Items
	private final HashMap<Integer, Item> items = new HashMap<>();
	private final HashMap<Integer, WeaponAbility> weaponAbilities = new HashMap<>();

	// NPCs
	private final HashMap<Integer, NPC> npcs = new HashMap<>();

	// Enemies
	private final HashMap<Integer, Enemy> enemies = new HashMap<>();

	// Rooms
	private final HashMap<Integer, Room> rooms = new HashMap<>();

	// Player
	private Player player = null;

	public void setDialog(ArrayList<String> dialog) {
		this.dialog.clear();
		this.dialog.addAll(dialog);
	}

	public void setItems(
		HashMap<Integer, Item> items,
		HashMap<Integer, WeaponAbility> weaponAbilities,
		ArrayList<Pair<Integer, Integer>> weaponAbilitiesJunction
	) {
		this.items.clear();
		this.items.putAll(items);
		this.weaponAbilities.clear();
		this.weaponAbilities.putAll(weaponAbilities);
		for (Pair<Integer, Integer> abilityJunction : weaponAbilitiesJunction) {
			Item item = this.items.get(abilityJunction.getLeft());
			if (!(item instanceof Weapon weapon)) continue;

			weapon.addAbility(this.weaponAbilities.get(abilityJunction.getRight()));
		}
	}

	public void setNPCs(
		HashMap<Integer, NPC> npcs,
		HashMap<Integer, ArrayList<Item>> npcItems
	) {
		this.npcs.clear();
		this.npcs.putAll(npcs);
		for (Map.Entry<Integer, ArrayList<Item>> entry : npcItems.entrySet()) {
			this.npcs.get(entry.getKey()).getInventory().addItems(entry.getValue());
		}
	}

	public void setEnemies(
		HashMap<Integer, Enemy> enemies,
		HashMap<Integer, ArrayList<Item>> enemyItems
	) {
		this.enemies.clear();
		this.enemies.putAll(enemies);
		for (Map.Entry<Integer, ArrayList<Item>> entry : enemyItems.entrySet()) {
			this.enemies.get(entry.getKey()).getInventory().addItems(entry.getValue());
		}
	}

	public void setRooms(
		HashMap<Integer, Room> rooms,
		HashMap<Integer, HashMap<String, RoomConnection>> roomConnections,
		HashMap<Integer, ArrayList<Item>> roomItems,
		HashMap<Integer, ArrayList<NPC>> roomNPCs,
		HashMap<Integer, ArrayList<Enemy>> roomEnemies
	) {
		this.rooms.putAll(rooms);
		for (Map.Entry<Integer, HashMap<String, RoomConnection>> entry : roomConnections.entrySet()) {
			this.rooms.get(entry.getKey()).getRoomConnections().putAll(entry.getValue());
		}
		for (Map.Entry<Integer, ArrayList<Item>> entry : roomItems.entrySet()) {
			this.rooms.get(entry.getKey()).getInventory().addItems(entry.getValue());
		}
		for (Map.Entry<Integer, ArrayList<NPC>> entry : roomNPCs.entrySet()) {
			this.rooms.get(entry.getKey()).addNPCs(entry.getValue());
		}
		for (Map.Entry<Integer, ArrayList<Enemy>> entry : roomEnemies.entrySet()) {
			this.rooms.get(entry.getKey()).addEnemies(entry.getValue());
		}
	}

	public void setPlayer(
		Player player,
		HashMap<Integer, Item> playerItems
	) {
		this.player = player;
		player.getInventory().addItems(playerItems);
	}

	// General purpose methods
	@Override
	public void loadInitialData() {
		try {
			// Dialog
			setDialog(InitialData.getDialog());

			// Items
			setItems(
				InitialData.getItems(),
				InitialData.getWeaponAbilities(),
				InitialData.getWeaponAbilitiesJunction()
			);

			// NPCs
			setNPCs(
				InitialData.getNPCs(),
				InitialData.getNPCItems()
			);


			// Enemies
			setEnemies(
				InitialData.getEnemies(),
				InitialData.getEnemyItems()
			);

			// Rooms
			setRooms(
				InitialData.getRooms(),
				InitialData.getRoomConnections(),
				InitialData.getRoomItems(),
				InitialData.getRoomNPCs(),
				InitialData.getRoomEnemies()
			);

			// Player
			setPlayer(
				InitialData.getPlayer(),
				InitialData.getPlayerItems()
			);
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
		return new ArrayList<>(dialog);
	}

	@Override
	public void addDialog(String text) {
		dialog.add(text);
	}

	@Override
	public void clearDialog() {
		dialog.clear();
	}

	@Override
	public ArrayList<String> getCommandHistory() {
		return new ArrayList<>(commandHistory);
	}

	@Override
	public void addCommandToHistory(String command) {
		// Remove command from history if already in it
		commandHistory.remove(command);

		// Add command to history
		commandHistory.add(command);

		// Trim history if longer than max length
		while (commandHistory.size() > Database.MAX_HISTORY_SIZE) {
			commandHistory.remove(0);
		}
	}


	// Player-related methods
	@Override
	public Player getPlayer() {
		if (player == null) {
			throw new IllegalStateException("No player exists");
		}

		return player.copy();
	}

	@Override
	public NPC getNpcForPlayer() {
		return player.getCurrentNPC().copy();
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
		Room room = rooms.get(id);

		return room.copy();
	}

	@Override
	public HashMap<String, RoomConnection> getConnectionsForRoom(Room room) {
		HashMap<String, RoomConnection> roomConnections = new HashMap<>();
		room = rooms.get(room.getId());

		for (Map.Entry<String, RoomConnection> entry : room.getRoomConnections().entrySet()) {
			roomConnections.put(entry.getKey(), entry.getValue().copy());
		}

		return roomConnections;
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

		room = rooms.get(room.getId());

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

		room = rooms.get(room.getId());

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
	private HashMap<Integer, Item> copyItems(HashMap<Integer, Item> items) {
		HashMap<Integer, Item> newItems = new HashMap<>();

		for (Item item : items.values()) {
			newItems.put(item.getId(), item.copy());
		}

		return newItems;
	}

	@Override
	public HashMap<Integer, Item> getItemsForPlayer() {
		if (player == null) {
			return new HashMap<>();
		}

		return copyItems(player.getInventory().getItems());
	}

	@Override
	public HashMap<Integer, Item> getItemsForRoom(Room room) {
		if (room == null) {
			return new HashMap<>();
		}

		room = rooms.get(room.getId());

		return copyItems(room.getInventory().getItems());
	}

	@Override
	public HashMap<Integer, Item> getItemsForNPC(NPC npc) {
		if (npc == null) {
			return new HashMap<>();
		}

		npc = npcs.get(npc.getId());

		return copyItems(npc.getInventory().getItems());
	}

	@Override
	public HashMap<Integer, Item> getItemsForEnemy(Enemy enemy) { // Hamed
		if (enemy == null) {
			return new HashMap<>();
		}

		enemy = enemies.get(enemy.getId());

		return copyItems(enemy.getInventory().getItems());
	}


	// NPC-related methods
	@Override
	public HashMap<Integer, NPC> getNPCsForRoom(Room room) {
		if (room == null) {
			return new HashMap<>();
		}

		room = rooms.get(room.getId());
		HashMap<Integer, NPC> npcs = new HashMap<>();

		for (NPC npc : room.getNpcs().values()) {
			npcs.put(npc.getId(), npc.copy());
		}

		return npcs;
	}


	// Enemy-related methods
	@Override
	public HashMap<Integer, Enemy> getEnemiesForRoom(Room room) { // Hamed
		if (room == null) {
			return new HashMap<>();
		}

		room = rooms.get(room.getId());

		HashMap<Integer, Enemy> enemies = new HashMap<>();

		for (Enemy enemy : room.getEnemies().values()) {
			enemies.put(enemy.getId(), enemy.copy());
		}

		return enemies;
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

		enemy = enemies.get(enemy.getId());

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

		weapon = (Weapon) items.get(weapon.getId());
		HashMap<Integer, WeaponAbility> weaponAbilities = new HashMap<>();

		for (WeaponAbility ability : weapon.getAbilities().values()) {
			weaponAbilities.put(ability.getId(), ability.copy());
		}

		return weaponAbilities;
	}
}
