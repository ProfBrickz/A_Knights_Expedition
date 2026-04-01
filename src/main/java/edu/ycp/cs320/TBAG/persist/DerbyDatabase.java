package edu.ycp.cs320.TBAG.persist;

import edu.ycp.cs320.TBAG.model.*;

import java.sql.ResultSet;
import java.util.HashMap;

public class DerbyDatabase implements Database {
	@Override
	public void loadInitialData() {
		throw new UnsupportedOperationException("TODO - implement");
	}


	// Player-related methods
	@Override
	public Player getPlayer() {
		throw new UnsupportedOperationException("TODO - implement");
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
}
