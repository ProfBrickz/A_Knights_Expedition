package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.model.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class FakeDatabase implements Database {
	private Player player;
	private HashMap<Integer, Room> rooms = new HashMap<>();
	// A map between a room's id and (a map of its directions and connections)
	private HashMap<Integer, HashMap<String, RoomConnection>> roomConnections = new HashMap<>();


	// General purpose methods
	@Override
	public void loadInitialData() {
		try {
			rooms = InitialData.getRooms();
			player = InitialData.getPlayer();
		} catch (IllegalStateException exception) {
			throw new IllegalStateException("Initial data is incorrect", exception);
		} catch (IOException exception) {
			throw new IllegalStateException("Couldn't read initial data", exception);
		}
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
		throw new UnsupportedOperationException("TODO - implement");
	}

	@Override
	public void removeItemFromRoom(Room room, Item item) {
		throw new UnsupportedOperationException("TODO - implement");
	}


	// Item-related methods
	@Override
	public HashMap<Integer, Item> getItemsFromResultSet(ResultSet resultSet) {
		HashMap<Integer, Item> items = new HashMap<>();

		if (resultSet == null) {
			return items;
		}

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
		if (player == null) {
			return new HashMap<>();
		}

		return new HashMap<>(player.getInventory().getItems());
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
}
