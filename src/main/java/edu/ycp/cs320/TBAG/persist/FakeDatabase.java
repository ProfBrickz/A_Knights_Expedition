package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.model.*;

import java.sql.ResultSet;
import java.util.HashMap;

public class FakeDatabase implements Database {
	// Player-related methods
	public Player getPlayer() {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void setPlayerRoom(Integer roomId) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void setPlayerCoins(Integer coins) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void setPlayerState(PlayerState playerState) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void addItemToPlayer(Item item) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void removeItemFromPlayer(Item item) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void setPlayerNPC(NPC npc) {
		throw new UnsupportedOperationException("TODO - implement");
	}


	// Room-related methods
	public Room getRoomById(Integer id) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public HashMap<String, RoomConnection> getConnectionsForRoom(Room room) {
		throw new UnsupportedOperationException("TODO - implement");
	}


	// Item-related methods
	public HashMap<Integer, Item> getItemsFromResultSet(ResultSet resultSet) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public HashMap<Integer, Item> getItemsForPlayer() {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public HashMap<Integer, Item> getItemsForRoom(Room room) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public HashMap<Integer, Item> getItemsForNPC(NPC npc) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public HashMap<Integer, Item> getItemsForEnemy(Enemy enemy) {
		throw new UnsupportedOperationException("TODO - implement");
	}


	// NPC-related methods
	public HashMap<Integer, NPC> getNPCsForRoom(Room room) {
		throw new UnsupportedOperationException("TODO - implement");
	}


	// Enemy-related methods
	public HashMap<Integer, Enemy> getEnemiesForRoom(Room room) {
		throw new UnsupportedOperationException("TODO - implement");
	}


	// WeaponAbility-related methods
	public HashMap<Integer, WeaponAbility> getAbilitiesForWeapon(Weapon weapon) {
		throw new UnsupportedOperationException("TODO - implement");
	}


	// Item management in rooms and enemies
	public void addItemToRoom(Room room, Item item) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void removeItemFromRoom(Room room, Item item) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void addItemToEnemy(Enemy enemy, Item item) {
		throw new UnsupportedOperationException("TODO - implement");
	}

	public void removeItemFromEnemy(Enemy enemy, Item item) {
		throw new UnsupportedOperationException("TODO - implement");
	}
}
