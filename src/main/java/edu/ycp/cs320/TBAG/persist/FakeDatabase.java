package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class FakeDatabase implements Database {
	private HashMap<Integer, String> dialog = new HashMap<>();
	private Player player = null;
	private HashMap<Integer, Room> rooms = new HashMap<>();
	// A map between a room's id and (a map of its directions and connections)
	private HashMap<Integer, HashMap<String, RoomConnection>> roomConnections = new HashMap<>();
	private HashMap<Integer, Item> items = new HashMap<>();


	// General purpose methods
	@Override
	public void loadInitialData() {
		try {
			dialog = InitialData.getDialog();
			rooms = InitialData.getRooms();
			roomConnections = InitialData.getRoomConnections();
			player = InitialData.getPlayer();
			items = InitialData.getItems();
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
		roomConnections.clear();
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
		return roomConnections.get(room.getID());
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
	public HashMap<Integer, Item> getItemsForEnemy(Enemy enemy) {
		throw new UnsupportedOperationException("TODO - implement");
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
